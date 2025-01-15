package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.kotlin.ui.utils.SP;
import com.github.tvbox.osc.BuildConfig;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.base.App;
import com.github.tvbox.osc.bean.IJKCode;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.widget.OnItemClickListener;
import com.orhanobut.hawk.Hawk;
import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MediaSettingDialog extends BaseDialog {

    public MediaSettingDialog(@NonNull @NotNull Context context) {
        super(context);
        setContentView(R.layout.dialog_media_setting);
        setCanceledOnTouchOutside(true);
        TvRecyclerView listMediaTitle = findViewById(R.id.list_media_title);
        TvRecyclerView listMediaContent = findViewById(R.id.list_media_content);
        //右侧设置内容数据
        MediaSettingContentAdapter contentAdapter = new MediaSettingContentAdapter();
        listMediaContent.setAdapter(contentAdapter);
        //默认填充第一个
        List<MediaSettingEntity> listTitle = getListTitle();
        contentAdapter.replaceData(getListContent(listTitle.get(0).tag));
        //左侧数据展示
        MediaSettingTitleAdapter titleAdapter = new MediaSettingTitleAdapter(listTitle);
        listMediaTitle.setAdapter(titleAdapter);
        listMediaTitle.setOnItemListener(new SimpleOnItemListener() {
            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                //重新替换右侧数据
                contentAdapter.replaceData(getListContent(titleAdapter.getItem(position).tag));
                listMediaContent.setSelectedPosition(0);
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                this.onItemSelected(parent, itemView, position);
                itemView.requestFocus();
            }
        });


        listMediaContent.setOnItemListener((OnItemClickListener) (tvRecyclerView, view, i) -> {
            //处理点击事件
            MediaSettingEntity item = contentAdapter.getItem(i);
            MediaSettingEnum mediaSettingEnum = MediaSettingEnum.valueOf(item.tag);
            switch (mediaSettingEnum) {
                case IjkMediaCodecMode:
                    nextIJKCodec();
                    break;
                case IjkCache:
                    nextIJKCache();
                    break;
                case ExoRenderer:
                    nextExoRenderer();
                    break;
                case ExoRendererMode:
                    nextExoRendererMode();
                    break;
                case VodPlayerPreferred:
                    nextVodPlayerPreferred();
                    break;
            }
            contentAdapter.refreshNotifyItemChanged(i);
        });
    }


    public static void nextVodPlayerPreferred() {
        int index = SP.INSTANCE.getVodPlayerPreferred();
        App app = App.getInstance();
        String[] array = app.getResources().getStringArray(R.array.media_content_General_VodPlayerPreferred);
        index++;
        index %= array.length;
        SP.INSTANCE.setVodPlayerPreferred(index);
    }

    /**
     * 获取exo渲染器 自己存储的数据
     *
     * @return int
     */
    private static int getExoRenderer() {
        return SP.INSTANCE.getExoRenderer();
    }


    private static void nextExoRenderer() {
        App app = App.getInstance();
        String[] array = app.getResources().getStringArray(R.array.media_content_ExoPlayer_renderer);
        int renderer = getExoRenderer();
        renderer++;
        renderer %= array.length;
        SP.INSTANCE.setExoRenderer(renderer);
    }

    private static void nextIJKCache() {
        SP.INSTANCE.setIjkCachePlay(!getIJKCache());
    }

    private void nextIJKCodec() {
        List<IJKCode> ijkCodes = ApiConfig.get().getIjkCodes();
        String ijkCodec = SP.INSTANCE.getIjkCodec();
        int index = 0;
        for (int i = 0; i < ijkCodes.size(); i++) {
            IJKCode ijkCode = ijkCodes.get(i);
            if (ijkCode.getName().equals(ijkCodec)) {
                index = i;
                break;
            }
        }
        ijkCodes.get(index).selected(false);
        index++;
        index %= ijkCodes.size();
        ijkCodes.get(index).selected(true);
    }

    public List<MediaSettingEntity> getListTitle() {
        List<MediaSettingEntity> contentEntityList = new ArrayList<>();
        String[] stringTitle = getContext().getResources().getStringArray(R.array.media_title);
        String[] tags = getContext().getResources().getStringArray(R.array.media_title_tag);
        for (int i = 0; i < stringTitle.length; i++) {
            String content = stringTitle[i];
            String tag = tags[i];
            contentEntityList.add(new MediaSettingEntity(content, tag));
        }
        return contentEntityList;
    }


    /**
     * 获取 展示用的数据以及tag
     *
     * @param key
     */
    public List<MediaSettingEntity> getListContent(String key) {
        List<MediaSettingEntity> contentEntityList = new ArrayList<>();
        try {
            int id = getContext().getResources().getIdentifier("media_content_" + key, "array", BuildConfig.APPLICATION_ID);
            String[] strings = getContext().getResources().getStringArray(id);
            int idTag = getContext().getResources().getIdentifier("media_content_tag_" + key, "array", BuildConfig.APPLICATION_ID);
            String[] tags = getContext().getResources().getStringArray(idTag);
            for (int i = 0; i < strings.length; i++) {
                String content = strings[i];
                String tag = tags[i];
                contentEntityList.add(new MediaSettingEntity(content, tag));
            }
        } catch (Exception e) {
            LogUtils.w(Log.getStackTraceString(e));
        }
        return contentEntityList;
    }

    //左侧数据展示
    public static class MediaSettingTitleAdapter extends BaseQuickAdapter<MediaSettingEntity, BaseViewHolder> {
        public MediaSettingTitleAdapter(List<MediaSettingEntity> strings) {
            super(R.layout.item_dialog_select, strings);
        }

        @Override
        protected void convert(BaseViewHolder helper, MediaSettingEntity item) {
            TextView name = helper.getView(R.id.tvName);
            name.setText(item.content);
        }
    }

    //右侧的数据展示
    public static class MediaSettingContentAdapter extends BaseQuickAdapter<MediaSettingEntity, BaseViewHolder> {
        public MediaSettingContentAdapter() {
            super(R.layout.item_dialog_select2);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, MediaSettingEntity item) {
            TextView tvTitle = helper.getView(R.id.tv_title);
            tvTitle.setText(item.content);
            TextView tvContent = helper.getView(R.id.tv_content);
            MediaSettingEnum mediaSettingEnum = MediaSettingEnum.valueOf(item.tag);
            switch (mediaSettingEnum) {
                case IjkMediaCodecMode:
                    tvContent.setText(SP.INSTANCE.getIjkCodec());
                    break;
                case IjkCache:
                    tvContent.setText(getIJKCacheDesc());
                    break;
                case ExoRenderer:
                    tvContent.setText(getExoRendererDesc());
                    break;
                case ExoRendererMode:
                    tvContent.setText(getExoRendererModeDesc());
                    break;
                case VodPlayerPreferred:
                    tvContent.setText(getVodPlayerPreferredDesc());
                    break;
            }
        }
    }

    public static String getVodPlayerPreferredDesc() {
        App app = App.getInstance();
        String[] array = app.getResources().getStringArray(R.array.media_content_General_VodPlayerPreferred);
        return array[SP.INSTANCE.getVodPlayerPreferred()];
    }

    /**
     * 获取exo渲染器模式描述
     *
     * @return {@link String }
     */
    public static String getExoRendererModeDesc() {
        App app = App.getInstance();
        String[] array = app.getResources().getStringArray(R.array.media_content_ExoPlayer_renderer_mode);
        return array[getExoRendererMode()];
    }

    /**
     * 获取exo渲染器描述
     *
     * @return {@link String }
     */
    public static String getExoRendererDesc() {
        App app = App.getInstance();
        String[] array = app.getResources().getStringArray(R.array.media_content_ExoPlayer_renderer);
        return array[getExoRenderer()];
    }

    /**
     * 获取exo渲染器模式 自己存储的 值
     *
     * @return int
     */
    public static int getExoRendererMode() {
        return SP.INSTANCE.getExoRendererMode();
    }

    public static void nextExoRendererMode() {
        int rendererMode = getExoRendererMode();
        App app = App.getInstance();
        String[] array = app.getResources().getStringArray(R.array.media_content_ExoPlayer_renderer_mode);
        rendererMode++;
        rendererMode %= array.length;
        SP.INSTANCE.setExoRendererMode(rendererMode);
    }


    private static boolean getIJKCache() {
        return SP.INSTANCE.getIjkCachePlay();
    }

    private static String getIJKCacheDesc() {
        return getIJKCache() ? "开启" : "关闭";
    }

    //数据Bean
    public static class MediaSettingEntity {
        //展示名称
        private String content;
        //处理方式
        private String tag;
        //描述作用
        private String describe;

        public MediaSettingEntity(String content) {
            this.content = content;
        }

        public MediaSettingEntity(String content, String tag) {
            this.content = content;
            this.tag = tag;
        }

        public MediaSettingEntity(String content, String tag, String describe) {
            this.content = content;
            this.tag = tag;
            this.describe = describe;
        }
    }

    //数据枚举
    public enum MediaSettingEnum {
        IjkMediaCodecMode, IjkCache, ExoRenderer, ExoRendererMode, VodPlayerPreferred
    }
}
