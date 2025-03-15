package com.github.tvbox.osc.util.update;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.function.*;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.base.App;
import com.github.tvbox.osc.ui.activity.HomeActivity;
import com.github.tvbox.osc.util.Config;
import com.github.tvbox.osc.util.DefaultConfig;
import com.github.tvbox.osc.util.FileUtils;
import com.github.tvbox.osc.util.LOG;
import com.github.tvbox.osc.util.ToastHelper;
import com.google.gson.Gson;


/****************************************************************
 *      MaiJZ                                                   *
 *      20161209                                                *
 *      https://github.com/maijz128/github-update-checker       *
 *                                                              *
 ****************************************************************/


public class UpdateChecker {

    public interface IWebClient {
        void DownloadHtml(String url, Consumer<String> callback);
    }

    public IWebClient WebClient;

    public String CurrentVersion;

    public UpdateChecker(String currentVersion) {
        this.CurrentVersion = currentVersion;
        this.WebClient = new MyWebClient();
    }


    public void checkThenUpgrade(Context context) {
        checkUpdate(latestReleases -> {
            if (latestReleases != null && hasNewVersion(latestReleases)) {
                App.post(() -> {
                    LatestReleases.Assets assets = latestReleases.assets.get(0);
                    showUpdateDialog(context, assets);
                }, 800);
            }
        });
    }


    private void checkUpdate(Consumer<LatestReleases> callback) {
        this.WebClient.DownloadHtml(Config.GITHUB_URL, (html) -> {
            LatestReleases latest = GetLatestReleases(html);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                callback.accept(latest);
            }
        });
    }

    private boolean hasNewVersion(LatestReleases latest) {
        return latest.tag_name != null && CurrentVersion != null && VersionComparer.CompareVersion(latest.tag_name, this.CurrentVersion) > 0;
    }


    private void showUpdateDialog(Context context, LatestReleases.Assets assets) {
        File apkFile = new File(FileUtils.getCacheDir(), CurrentVersion + ".apk");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("新版本提示");
        builder.setMessage("更新内容：" + assets.name);

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("确认", (dialog, which) -> {
            dialog.dismiss();
            downloadApk(context, assets.browser_download_url, apkFile.getPath());
        });

        builder.create().show();
    }

    private void downloadApk(Context context, String url, String apkPath) {

        Downloader.INSTANCE.downloadApk(Config.GH_PROXY + url, apkPath, new Downloader.Callback() {
            @Override
            public void onStart() {
                Toast.makeText(context, "后台下载中...，下载完自动安装", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(int progress) {
                LOG.i("下载中：" + progress);
                if (progress == 100) {
                    LOG.i("下载完成，开始安装APK");
                    ApkInstaller.INSTANCE.installApk(context, apkPath);
                }
            }


            @Override
            public void onFinish() {

            }

            @Override
            public void onError(@NonNull String msg) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static LatestReleases GetLatestReleases(String html) {
        Gson g = new Gson();
        return g.fromJson(html, LatestReleases.class);
    }


    public static class VersionComparer {

        public static int CompareVersion(String target, String current) {

            String target_f = Filter(target);
            String current_f = Filter(current);
            String[] tsplit = target_f.split("\\.");
            String[] csplit = current_f.split("\\.");
            int len = Math.max(tsplit.length, csplit.length);

            for (int i = 0; i < len; i++) {
                int tvalue = 0;
                int cvalue = 0;

                if (i < tsplit.length) {
                    tvalue = Integer.parseInt(tsplit[i]);
                }

                if (i < csplit.length) {
                    cvalue = Integer.parseInt(csplit[i]);
                }

                if (tvalue != cvalue) {
                    return tvalue - cvalue;
                }
            }
            return 0;
        }


        public static String Filter(String version) {
            StringBuilder sb = new StringBuilder();

            for (char c : version.toCharArray()) {
                Boolean condition = c >= '0' && c <= '9';
                if (condition || c == '.') {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

    }


    public class LatestReleases {

        public String html_url;
        public String tag_name;
        public String name;
        public String body;
        public List<Assets> assets;
        public String tarball_url;
        public String zipball_url;


        public class Assets {
            public String name;
            public String size;
            public String label;
            public String download_count;
            public String created_at;
            public String updated_at;
            public String browser_download_url;
        }
    }


    public class MyWebClient implements IWebClient {

        public void DownloadHtml(String url, Consumer<String> callback) {
            Thread thread = new Thread(() -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    callback.accept(HttpConnection(url));
                }
            });
            thread.start();
        }


        public String HttpConnection(String surl) {
            try {
                URL url = new URL(surl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);

                if (conn.getResponseCode() == 200) {
                    InputStream stream = conn.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = stream.read(buffer)) != (-1)) {
                        baos.write(buffer, 0, len);
                    }
                    return baos.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "{\"name\" : \"获取Html错误!\"}";
        }

    }

}