package com.github.tvbox.osc.util;

import android.os.Build;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.function.*;

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


    public void CheckUpdate(BiConsumer<String, String> callback) {
        this.WebClient.DownloadHtml(Config.GITHUB_URL, (html) ->
        {
            LatestReleases latest = GetLatestReleases(html);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                callback.accept(latest.tag_name, latest.body);
            }
        });
    }


    public void CheckUpdate(Consumer<LatestReleases> callback) {
        this.WebClient.DownloadHtml(Config.GITHUB_URL, (html) ->
        {
            LatestReleases latest = GetLatestReleases(html);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                callback.accept(latest);
            }
        });
    }


    public void HasNewVersion(Consumer<Boolean> callback) {
        CheckUpdate((latest) ->
        {
            int result = VersionComparer.CompareVersion(latest.tag_name, this.CurrentVersion);
            callback.accept(result > 0);
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
            public String download_count;
            public String created_at;
            public String updated_at;
            public String browser_download_url;
        }
    }


    public class MyWebClient implements IWebClient {

        public void DownloadHtml(String url, Consumer<String> callback) {
            Thread thread = new Thread(() ->
            {
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