package com.github.tvbox.osc.util;

import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.bean.SourceBean;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SearchHelper {

    public static final String SOURCES_FOR_SEARCH = "checked_sources_for_search";

    public static HashMap<String, String> getSourcesForSearch() {
        String api = ApiConfig.get().getCurrentApiUrl();
        if (api.isEmpty()) {
            return null;
        }

        HashMap<String, String> checkSources = new HashMap<>();

        try {
            HashMap<String, HashMap<String, String>> mCheckSourcesForApi = Hawk.get(SOURCES_FOR_SEARCH, new HashMap<>());
            checkSources = mCheckSourcesForApi.get(api);
        } catch (Exception ignored) {

        }
        if (checkSources == null || checkSources.isEmpty()) {
            if (checkSources == null) {
                checkSources = new HashMap<>();
            }
            for (SourceBean bean : ApiConfig.get()
                    .getSourceBeanList()) {
                if (!bean.isSearchable()) {
                    continue;
                }
                checkSources.put(bean.getKey(), "1");
            }
        }
        return checkSources;
    }

    public static void putCheckedSources(HashMap<String, String> mCheckSources) {
        String api = ApiConfig.get().getCurrentApiUrl();
        if (api.isEmpty()) {
            return;
        }
        HashMap<String, HashMap<String, String>> mCheckSourcesForApi = Hawk.get(SOURCES_FOR_SEARCH, new HashMap<>());
        if (mCheckSourcesForApi == null || mCheckSourcesForApi.isEmpty()) {
            mCheckSourcesForApi = new HashMap<>();
        }
        mCheckSourcesForApi.put(api, mCheckSources);
        Hawk.put(SOURCES_FOR_SEARCH, mCheckSourcesForApi);
    }

    public static void putCheckedSource(String siteKey, boolean checked) {
        String api = ApiConfig.get().getCurrentApiUrl();
        if (api.isEmpty()) {
            return;
        }
        HashMap<String, HashMap<String, String>> mCheckSourcesForApi = Hawk.get(SOURCES_FOR_SEARCH, new HashMap<>());
        if (mCheckSourcesForApi == null || mCheckSourcesForApi.isEmpty()) {
            mCheckSourcesForApi = new HashMap<>();
        }
        if (mCheckSourcesForApi.get(api) == null) {
            mCheckSourcesForApi.put(api, new HashMap<>());
        }
        if (checked) {
            mCheckSourcesForApi.get(api).put(siteKey, "1");
        } else {
            if (mCheckSourcesForApi.get(api).containsKey(siteKey)) {
                mCheckSourcesForApi.get(api).remove(siteKey);
            }
        }
        Hawk.put(SOURCES_FOR_SEARCH, mCheckSourcesForApi);
    }

    public static List<String> splitWords(String text) {
        List<String> result = new ArrayList<>();
        result.add(text);
        String[] parts = text.split("\\W+");
        if (parts.length > 1) {
            result.addAll(Arrays.asList(parts));
        }
        return result;
    }
}