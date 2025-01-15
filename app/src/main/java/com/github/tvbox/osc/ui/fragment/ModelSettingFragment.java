package com.github.tvbox.osc.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;

import com.github.tvbox.kotlin.ui.utils.SP;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.base.BaseActivity;
import com.github.tvbox.osc.base.BaseLazyFragment;
import com.github.tvbox.osc.bean.SourceBean;
import com.github.tvbox.osc.player.thirdparty.Kodi;
import com.github.tvbox.osc.player.thirdparty.MXPlayer;
import com.github.tvbox.osc.player.thirdparty.ReexPlayer;
import com.github.tvbox.osc.ui.activity.SettingActivity;
import com.github.tvbox.osc.ui.adapter.ApiHistoryDialogAdapter;
import com.github.tvbox.osc.ui.adapter.SelectDialogAdapter;
import com.github.tvbox.osc.ui.dialog.AboutDialog;
import com.github.tvbox.osc.ui.dialog.ApiDialog;
import com.github.tvbox.osc.ui.dialog.ApiHistoryDialog;
import com.github.tvbox.osc.ui.dialog.BackupDialog;
import com.github.tvbox.osc.ui.dialog.HomeIconDialog;
import com.github.tvbox.osc.ui.dialog.MediaSettingDialog;
import com.github.tvbox.osc.ui.dialog.ResetDialog;
import com.github.tvbox.osc.ui.dialog.SelectDialog;
import com.github.tvbox.osc.ui.dialog.XWalkInitDialog;
import com.github.tvbox.osc.util.FastClickCheckUtil;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.HistoryHelper;
import com.github.tvbox.osc.util.OkGoHelper;
import com.github.tvbox.osc.util.PlayerHelper;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.orhanobut.hawk.Hawk;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7GridLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import me.jessyan.autosize.utils.AutoSizeUtils;
import okhttp3.HttpUrl;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @author pj567
 * @date :2020/12/23
 * @description:
 */
public class ModelSettingFragment extends BaseLazyFragment {
    private TextView tvDebugOpen;
    private TextView tvApi;
    // Home Section
    private TextView tvHomeApi;
    private TextView tvHomeDefaultShow;
    private TextView tvHomeShow;
    private TextView tvHomeIcon;
    private TextView tvHomeRec;
    private TextView tvHomeNum;

    // Player Section
    private TextView tvShowPreviewText;
    private TextView tvScale;
    private TextView tvPlay;
    private TextView tvVideoPurifyText;

    // System Section
    private TextView tvLocale;
    private TextView tvTheme;
    private TextView tvRender;
    private TextView tvParseWebView;
    private TextView tvSearchView;
    private TextView tvDns;
    private TextView tvFastSearchText;

    public static ModelSettingFragment newInstance() {
        return new ModelSettingFragment().setArguments();
    }

    public ModelSettingFragment setArguments() {
        return this;
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_model;
    }

    @Override
    protected void init() {
        tvFastSearchText = findViewById(R.id.showFastSearchText);
        tvFastSearchText.setText(SP.INSTANCE.getFastSearchMode() ? "已开启" : "已关闭");
        tvDebugOpen = findViewById(R.id.tvDebugOpen);
        tvDebugOpen.setText(SP.INSTANCE.getDebugMode() ? "开启" : "关闭");
        tvApi = findViewById(R.id.tvApi);
        tvApi.setText(SP.INSTANCE.getApiUrl());
        // Home Section
        tvHomeApi = findViewById(R.id.tvHomeApi);
        tvHomeApi.setText(ApiConfig.get().getHomeSourceBean().getName());
        tvHomeShow = findViewById(R.id.tvHomeShow);
        tvHomeShow.setText(SP.INSTANCE.getShowSource()? "开启" : "关闭");
        tvHomeRec = findViewById(R.id.tvHomeRec);
        tvHomeRec.setText(getHomeRecName(SP.INSTANCE.getHomeRec()));
        tvHomeNum = findViewById(R.id.tvHomeNum);
        tvHomeNum.setText(HistoryHelper.getHomeRecName(SP.INSTANCE.getHomeNum()));
        // Player Section
        tvShowPreviewText = findViewById(R.id.showPreviewText);
        tvShowPreviewText.setText(SP.INSTANCE.getShowPreview() ? "开启" : "关闭");
        tvScale = findViewById(R.id.tvScaleType);
        tvScale.setText(PlayerHelper.getScaleName(Hawk.get(HawkConfig.PLAY_SCALE, 0)));
        tvPlay = findViewById(R.id.tvPlay);
        tvPlay.setText(PlayerHelper.getPlayerName(Hawk.get(HawkConfig.PLAY_TYPE, 0)));
        tvVideoPurifyText = findViewById(R.id.tvVideoPurifyText);
        tvVideoPurifyText.setText(SP.INSTANCE.getVideoPurify() ? "开启" : "关闭");
        // System Section
        tvLocale = findViewById(R.id.tvLocale);
        tvLocale.setText(getLocaleView(SP.INSTANCE.getLanguage()));
        tvTheme = findViewById(R.id.tvTheme);
        tvTheme.setText(getThemeView(Hawk.get(HawkConfig.THEME_SELECT, 0)));
        tvRender = findViewById(R.id.tvRenderType);
        tvRender.setText(PlayerHelper.getRenderName(Hawk.get(HawkConfig.PLAY_RENDER, 0)));
        tvParseWebView = findViewById(R.id.tvParseWebView);
        tvParseWebView.setText(Hawk.get(HawkConfig.PARSE_WEBVIEW, true) ? "系统自带" : "XWalkView");
        tvSearchView = findViewById(R.id.tvSearchView);
        tvSearchView.setText(getSearchView(Hawk.get(HawkConfig.SEARCH_VIEW, 0)));
        tvDns = findViewById(R.id.tvDns);
        tvDns.setText(OkGoHelper.dnsHttpsList.get(Hawk.get(HawkConfig.DOH_URL, 0)));
        tvHomeDefaultShow = findViewById(R.id.tvHomeDefaultShow);
        tvHomeDefaultShow.setText(SP.INSTANCE.getHomeDefaultShow() ? "开启" : "关闭");

        //takagen99 : Set HomeApi as default
        findViewById(R.id.llHomeApi).requestFocus();

        findViewById(R.id.llDebug).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                SP.INSTANCE.setDebugMode(!SP.INSTANCE.getDebugMode());
                tvDebugOpen.setText(SP.INSTANCE.getDebugMode() ? "开启" : "关闭");
            }
        });
        // Input Source URL ------------------------------------------------------------------------
        findViewById(R.id.llApi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                ApiDialog dialog = new ApiDialog(mActivity);
                EventBus.getDefault().register(dialog);
                dialog.setOnListener(new ApiDialog.OnListener() {
                    @Override
                    public void onchange(String api) {
                        SP.INSTANCE.setApiUrl(api);
                        tvApi.setText(api);
                    }
                });
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ((BaseActivity) mActivity).hideSystemUI(true);
                        EventBus.getDefault().unregister(dialog);
                    }
                });
                dialog.show();
            }
        });
        findViewById(R.id.llApiHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> history = new ArrayList<>(SP.INSTANCE.getApiHistory());
                if (history.isEmpty())
                    return;
                String current = SP.INSTANCE.getApiUrl();
                int idx = 0;
                if (history.contains(current))
                    idx = history.indexOf(current);
                ApiHistoryDialog dialog = new ApiHistoryDialog(getContext());
                dialog.setTip(getString(R.string.dia_history_list));
                dialog.setAdapter(new ApiHistoryDialogAdapter.SelectDialogInterface() {
                    @Override
                    public void click(String api) {
                        SP.INSTANCE.setApiUrl(api);
                        tvApi.setText(api);
                        dialog.dismiss();
                    }

                    @Override
                    public void del(String value, ArrayList<String> data) {
                        SP.INSTANCE.setApiHistory(new HashSet<>(data));
                    }
                }, history, idx);
                dialog.show();
            }
        });
        // 1. HOME Configuration ---------------------------------------------------------------- //
        // Select Home Source ------------------------------------
        findViewById(R.id.llHomeApi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                List<SourceBean> sites = new ArrayList<>();
                for (SourceBean sb : ApiConfig.get().getSourceBeanList()) {
                    if (sb.getHide() == 0) sites.add(sb);
                }
                if (sites.size() > 0) {
                    SelectDialog<SourceBean> dialog = new SelectDialog<>(mActivity);

                    // Multi Column Selection
                    int spanCount = (int) Math.floor(sites.size() / 10);
                    if (spanCount <= 1) spanCount = 1;
                    if (spanCount >= 3) spanCount = 3;

                    TvRecyclerView tvRecyclerView = dialog.findViewById(R.id.list);
                    tvRecyclerView.setLayoutManager(new V7GridLayoutManager(dialog.getContext(), spanCount));
                    ConstraintLayout cl_root = dialog.findViewById(R.id.cl_root);
                    ViewGroup.LayoutParams clp = cl_root.getLayoutParams();
                    if (spanCount != 1) {
                        clp.width = AutoSizeUtils.mm2px(dialog.getContext(), 400 + 260 * (spanCount - 1));
                    }

                    dialog.setTip(getString(R.string.dia_source));
                    dialog.setAdapter(tvRecyclerView, new SelectDialogAdapter.SelectDialogInterface<SourceBean>() {
                        @Override
                        public void click(SourceBean value, int pos) {
                            ApiConfig.get().setSourceBean(value);
                            tvHomeApi.setText(ApiConfig.get().getHomeSourceBean().getName());
                        }

                        @Override
                        public String getDisplay(SourceBean val) {
                            return val.getName();
                        }
                    }, new DiffUtil.ItemCallback<SourceBean>() {
                        @Override
                        public boolean areItemsTheSame(@NonNull @NotNull SourceBean oldItem, @NonNull @NotNull SourceBean newItem) {
                            return oldItem == newItem;
                        }

                        @Override
                        public boolean areContentsTheSame(@NonNull @NotNull SourceBean oldItem, @NonNull @NotNull SourceBean newItem) {
                            return oldItem.getKey().equals(newItem.getKey());
                        }
                    }, sites, sites.indexOf(ApiConfig.get().getHomeSourceBean()));
                    dialog.show();
                }
            }
        });
        // Switch to show / hide source header --------------------------
        findViewById(R.id.llHomeShow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.HOME_SHOW_SOURCE, !Hawk.get(HawkConfig.HOME_SHOW_SOURCE, false));
                tvHomeShow.setText(Hawk.get(HawkConfig.HOME_SHOW_SOURCE, true) ? "开启" : "关闭");
            }
        });
        findViewById(R.id.llHomeIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                HomeIconDialog dialog = new HomeIconDialog(mActivity);
                dialog.show();
            }
        });
        // Select Home Display Type : Douban / Recommended / History -----
        findViewById(R.id.llHomeRec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.HOME_REC, 0);
                ArrayList<Integer> types = new ArrayList<>();
                types.add(0);
                types.add(1);
                types.add(2);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip(getString(R.string.dia_hm_type));
                dialog.setAdapter(null, new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.HOME_REC, value);
                        tvHomeRec.setText(getHomeRecName(value));
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return getHomeRecName(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, types, defaultPos);
                dialog.show();
            }
        });
        // History to Keep ------------------------------------------
        findViewById(R.id.llHomeNum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.HOME_NUM, 0);
                ArrayList<Integer> types = new ArrayList<>();
                types.add(0);
                types.add(1);
                types.add(2);
                types.add(3);
                types.add(4);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip(getString(R.string.dia_history));
                dialog.setAdapter(null, new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.HOME_NUM, value);
                        tvHomeNum.setText(HistoryHelper.getHomeRecName(value));
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return HistoryHelper.getHomeRecName(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, types, defaultPos);
                dialog.show();
            }
        });
        // 2. PLAYER Configuration -------------------------------------------------------------- //
        // Switch for Preview Window -------------------------------
        findViewById(R.id.showPreview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                boolean isShowPreview = SP.INSTANCE.getShowPreview();
                tvShowPreviewText.setText(isShowPreview ? "开启" : "关闭");
                SP.INSTANCE.setShowPreview(!isShowPreview);
            }
        });
        // Select Screen Ratio -------------------------------------
        findViewById(R.id.llScale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.PLAY_SCALE, 0);
                ArrayList<Integer> players = new ArrayList<>();
                players.add(0);
                players.add(1);
                players.add(2);
                players.add(3);
                players.add(4);
                players.add(5);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip(getString(R.string.dia_ratio));
                dialog.setAdapter(null, new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.PLAY_SCALE, value);
                        tvScale.setText(PlayerHelper.getScaleName(value));
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return PlayerHelper.getScaleName(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, players, defaultPos);
                dialog.show();
            }
        });
        //后台播放
        View backgroundPlay = findViewById(R.id.llBackgroundPlay);
        TextView tvBgPlayType = findViewById(R.id.tvBackgroundPlayType);
        Integer defaultBgPlayTypePos = Hawk.get(HawkConfig.BACKGROUND_PLAY_TYPE, 0);
        ArrayList<String> bgPlayTypes = new ArrayList<>();
        bgPlayTypes.add("关闭");
        bgPlayTypes.add("开启");
        bgPlayTypes.add("画中画");
        tvBgPlayType.setText(bgPlayTypes.get(defaultBgPlayTypePos));
        backgroundPlay.setOnClickListener(view -> {
            FastClickCheckUtil.check(view);
            int bgPlayTypePos = Hawk.get(HawkConfig.BACKGROUND_PLAY_TYPE, 0);
            SelectDialog<String> dialog = new SelectDialog<>(mActivity);
            dialog.setTip("请选择");
            dialog.setAdapter(null, new SelectDialogAdapter.SelectDialogInterface<String>() {
                @Override
                public void click(String value, int pos) {
                    tvBgPlayType.setText(value);
                    Hawk.put(HawkConfig.BACKGROUND_PLAY_TYPE, pos);
                }

                @Override
                public String getDisplay(String val) {
                    return val;
                }
            }, new DiffUtil.ItemCallback<String>() {
                @Override
                public boolean areItemsTheSame(@NonNull @NotNull String oldItem, @NonNull @NotNull String newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areContentsTheSame(@NonNull @NotNull String oldItem, @NonNull @NotNull String newItem) {
                    return oldItem.equals(newItem);
                }
            }, bgPlayTypes, bgPlayTypePos);
            dialog.show();
        });
        // Select PLAYER Type --------------------------------------------
        findViewById(R.id.llPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.PLAY_TYPE, 0);
                ArrayList<Integer> players = new ArrayList<>();
                players.add(0);
                players.add(1);
                players.add(2);
                players.add(3);
                if (MXPlayer.getPackageInfo() != null) {
                    players.add(10);
                }
                if (ReexPlayer.getPackageInfo() != null) {
                    players.add(11);
                }
                if (Kodi.getPackageInfo() != null) {
                    players.add(12);
                }
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip(getString(R.string.dia_player));
                dialog.setAdapter(null, new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.PLAY_TYPE, value);
                        tvPlay.setText(PlayerHelper.getPlayerName(value));
                        PlayerHelper.init();
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return PlayerHelper.getPlayerName(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, players, defaultPos);
                dialog.show();
            }
        });


        // Select DECODER Type --------------------------------------------
        //更改选择是否用硬解码还是软解码 改成播放器设置
        findViewById(R.id.llMediaSetting).setOnClickListener(view -> {
            FastClickCheckUtil.check(view);
            MediaSettingDialog mediaSettingDialog = new MediaSettingDialog(view.getContext());
            mediaSettingDialog.show();
        });

        // toggle purify video -------------------------------------
        findViewById(R.id.llVideoPurify).setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            SP.INSTANCE.setVideoPurify(!SP.INSTANCE.getVideoPurify());
            tvVideoPurifyText.setText(SP.INSTANCE.getVideoPurify() ? "开启" : "关闭");
        });

        // 3. SYSTEM Configuration -------------------------------------------------------------- //
        // Select Webview ---------------------------------------------
        findViewById(R.id.llParseWebVew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                boolean useSystem = !Hawk.get(HawkConfig.PARSE_WEBVIEW, true);
                Hawk.put(HawkConfig.PARSE_WEBVIEW, useSystem);
                tvParseWebView.setText(Hawk.get(HawkConfig.PARSE_WEBVIEW, true) ? "系统自带" : "XWalkView");
                if (!useSystem) {
                    Toast.makeText(mContext, "注意: XWalkView只适用于部分低Android版本，Android5.0以上推荐使用系统自带", Toast.LENGTH_LONG).show();
                    XWalkInitDialog dialog = new XWalkInitDialog(mContext);
                    dialog.setOnListener(new XWalkInitDialog.OnListener() {
                        @Override
                        public void onchange() {
                        }
                    });
                    dialog.show();
                }
            }
        });
        // Select System Render ( Surface/Texture View ) ---------------------
        findViewById(R.id.llRender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.PLAY_RENDER, 0);
                ArrayList<Integer> renders = new ArrayList<>();
                renders.add(0);
                renders.add(1);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip(getString(R.string.dia_render));
                dialog.setAdapter(null, new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.PLAY_RENDER, value);
                        tvRender.setText(PlayerHelper.getRenderName(value));
                        PlayerHelper.init();
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return PlayerHelper.getRenderName(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, renders, defaultPos);
                dialog.show();
            }
        });
        // Select DNS ---------------------------------------------
        findViewById(R.id.llDns).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int dohUrl = Hawk.get(HawkConfig.DOH_URL, 0);

                SelectDialog<String> dialog = new SelectDialog<>(mActivity);
                dialog.setTip(getString(R.string.dia_dns));
                dialog.setAdapter(null, new SelectDialogAdapter.SelectDialogInterface<String>() {
                    @Override
                    public void click(String value, int pos) {
                        tvDns.setText(OkGoHelper.dnsHttpsList.get(pos));
                        Hawk.put(HawkConfig.DOH_URL, pos);
                        String url = OkGoHelper.getDohUrl(pos);
                        OkGoHelper.dnsOverHttps.setUrl(url.isEmpty() ? null : HttpUrl.get(url));
                        IjkMediaPlayer.toggleDotPort(pos > 0);
                    }

                    @Override
                    public String getDisplay(String val) {
                        return val;
                    }
                }, new DiffUtil.ItemCallback<String>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull String oldItem, @NonNull @NotNull String newItem) {
                        return oldItem.equals(newItem);
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull String oldItem, @NonNull @NotNull String newItem) {
                        return oldItem.equals(newItem);
                    }
                }, OkGoHelper.dnsHttpsList, dohUrl);
                dialog.show();
            }
        });
        // Select Backup / Restore -------------------------------------
        findViewById(R.id.llBackup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                BackupDialog dialog = new BackupDialog(mActivity);
                dialog.show();
            }
        });
        // resetApp
        findViewById(R.id.llReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                ResetDialog dialog = new ResetDialog(mActivity);
                dialog.show();
            }
        });
        // Load Wallpaper from URL -------------------------------------
        findViewById(R.id.llWp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                if (!ApiConfig.get().wallpaper.isEmpty())
                    Toast.makeText(mContext, getString(R.string.mn_wall_load), Toast.LENGTH_SHORT).show();
                OkGo.<File>get(ApiConfig.get().wallpaper).execute(new FileCallback(requireActivity().getFilesDir().getAbsolutePath(), "wp") {
                    @Override
                    public void onSuccess(Response<File> response) {
                        ((BaseActivity) requireActivity()).changeWallpaper(true);
                    }

                    @Override
                    public void onError(Response<File> response) {
                        super.onError(response);
                    }

                    @Override
                    public void downloadProgress(Progress progress) {
                        super.downloadProgress(progress);
                    }
                });
            }
        });
        // Restore Default Wallpaper from system -------------------------
        findViewById(R.id.llWpRecovery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                File wp = new File(requireActivity().getFilesDir().getAbsolutePath() + "/wp");
                if (wp.exists())
                    wp.delete();
                ((BaseActivity) requireActivity()).changeWallpaper(true);
            }
        });
        // Select Search Display Results ( Text or Picture ) -------------
        findViewById(R.id.llSearchView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.SEARCH_VIEW, 0);
                ArrayList<Integer> types = new ArrayList<>();
                types.add(0);
                types.add(1);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip(getString(R.string.dia_search));
                dialog.setAdapter(null, new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.SEARCH_VIEW, value);
                        tvSearchView.setText(getSearchView(value));
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return getSearchView(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, types, defaultPos);
                dialog.show();
            }
        });
        findViewById(R.id.showFastSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.FAST_SEARCH_MODE, !Hawk.get(HawkConfig.FAST_SEARCH_MODE, false));
                tvFastSearchText.setText(Hawk.get(HawkConfig.FAST_SEARCH_MODE, false) ? "已开启" : "已关闭");
            }
        });
        // Select App Language ( English / Chinese ) -----------------
        findViewById(R.id.llLocale).setOnClickListener(new View.OnClickListener() {
            private final int chkLang = Hawk.get(HawkConfig.HOME_LOCALE, 0);

            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.HOME_LOCALE, 0);
                ArrayList<Integer> types = new ArrayList<>();
                types.add(0);
                types.add(1);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip(getString(R.string.dia_locale));
                dialog.setAdapter(null, new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.HOME_LOCALE, value);
                        tvLocale.setText(getLocaleView(value));
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return getLocaleView(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, types, defaultPos);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (chkLang != Hawk.get(HawkConfig.HOME_LOCALE, 0)) {
                            reloadActivity();
                        }
                    }
                });
                dialog.show();
            }
        });
        // Select App Theme Color -------------------------------------
        findViewById(R.id.llTheme).setOnClickListener(new View.OnClickListener() {
            private final int chkTheme = Hawk.get(HawkConfig.THEME_SELECT, 0);

            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.THEME_SELECT, 0);
                ArrayList<Integer> types = new ArrayList<>();
                types.add(0);
                types.add(1);
                types.add(2);
                types.add(3);
                types.add(4);
                types.add(5);
                types.add(6);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip(getString(R.string.dia_theme));
                dialog.setAdapter(null, new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.THEME_SELECT, value);
                        tvTheme.setText(getThemeView(value));
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return getThemeView(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, types, defaultPos);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (chkTheme != Hawk.get(HawkConfig.THEME_SELECT, 0)) {
                            reloadActivity();
                        }
                    }
                });
                dialog.show();
            }
        });
        // About App -----------------------------------------------
        findViewById(R.id.llAbout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                AboutDialog dialog = new AboutDialog(mActivity);
                dialog.show();
            }
        });

        findViewById(R.id.llHomeLive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                SP.INSTANCE.setHomeDefaultShow(!SP.INSTANCE.getHomeDefaultShow());
                tvHomeDefaultShow.setText(SP.INSTANCE.getHomeDefaultShow() ? "开启" : "关闭");
            }
        });

        SettingActivity.callback = new SettingActivity.DevModeCallback() {
            @Override
            public void onChange() {
                findViewById(R.id.llDebug).setVisibility(View.VISIBLE);
            }
        };

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SettingActivity.callback = null;
    }

    String getHomeRecName(int type) {
        if (type == 1) {
            return "站点推荐";
        } else if (type == 2) {
            return "观看历史";
        } else {
            return "豆瓣热播";
        }
    }

    String getSearchView(int type) {
        if (type == 0) {
            return "文字列表";
        } else {
            return "缩略图";
        }
    }

    String getLocaleView(int type) {
        if (type == 0) {
            return "中文";
        } else {
            return "英文";
        }
    }

    String getThemeView(int type) {
        if (type == 0) {
            return "奈飞";
        } else if (type == 1) {
            return "哆啦";
        } else if (type == 2) {
            return "百事";
        } else if (type == 3) {
            return "鸣人";
        } else if (type == 4) {
            return "小黄";
        } else if (type == 5) {
            return "八神";
        } else {
            return "樱花";
        }
    }

    void reloadActivity() {
        Intent intent = getActivity().getApplicationContext().getPackageManager().getLaunchIntentForPackage(getActivity().getApplication().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putBoolean("useCache", true);
        intent.putExtras(bundle);
        getActivity().getApplicationContext().startActivity(intent);
        //  android.os.Process.killProcess(android.os.Process.myPid());
        //  System.exit(0);
    }

}
