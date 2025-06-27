package com.github.tvbox.osc.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 安卓平台域名访问速度检测工具类（优化版）
 * 支持网络状态检测、缓存机制、错误分类和异步回调
 */
public class DomainSpeedTester {

    // 错误类型枚举
    public enum TestError {
        NO_NETWORK(1),        // 无网络连接
        DNS_FAILED(2),        // DNS解析失败
        CONNECTION_TIMEOUT(3),// 连接超时
        SERVER_ERROR(4),      // 服务器错误
        UNKNOWN_ERROR(5);     // 未知错误

        private final int code;

        TestError(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    // 域名速度记录类
    public static class DomainSpeed {
        private String domain;
        private long responseTime; // 响应时间(毫秒)

        public DomainSpeed(String domain, long responseTime) {
            this.domain = domain;
            this.responseTime = responseTime;
        }

        public String getDomain() {
            return domain;
        }

        public long getResponseTime() {
            return responseTime;
        }

        @Override
        public String toString() {
            return domain + ": " + responseTime + "ms";
        }
    }

    // 测试统计信息类
    public static class TestStatistics {
        private int totalDomains;
        private int successfulDomains;
        private long averageSpeed;
        private long minSpeed;
        private long maxSpeed;

        public int getTotalDomains() {
            return totalDomains;
        }

        public int getSuccessfulDomains() {
            return successfulDomains;
        }

        public long getAverageSpeed() {
            return averageSpeed;
        }

        public long getMinSpeed() {
            return minSpeed;
        }

        public long getMaxSpeed() {
            return maxSpeed;
        }

        @Override
        public String toString() {
            return "统计信息: 共测试" + totalDomains + "个域名, " +
                    "成功" + successfulDomains + "个, " +
                    "平均耗时" + averageSpeed + "ms, " +
                    "最快" + minSpeed + "ms, 最慢" + maxSpeed + "ms";
        }
    }

    // 缓存结果类
    private static class CachedResult {
        long responseTime;
        long timestamp; // 缓存时间戳(毫秒)
    }

    // 连接超时时间(毫秒)
    private int connectTimeout = 5000;
    // 读取超时时间(毫秒)
    private int readTimeout = 5000;
    // 是否使用HTTPS连接
    private boolean useHttps = false;
    // 缓存有效时间(毫秒)
    private long cacheValidTime = 5000; // 5秒
    // 线程池执行器
    private Executor executor;
    // 域名缓存
    private final Map<String, CachedResult> domainCache = new HashMap<>();
    // 测试批次间隔时间(毫秒)
    private long batchInterval = 500;

    /**
     * 构造函数，使用默认参数
     */
    public DomainSpeedTester() {
        int coreCount = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(Math.max(2, coreCount - 1)); // 动态设置线程数
    }

    /**
     * 构造函数，自定义参数
     *
     * @param connectTimeout 连接超时时间(毫秒)
     * @param readTimeout    读取超时时间(毫秒)
     * @param cacheValidTime 缓存有效时间(毫秒)
     */
    public DomainSpeedTester(int connectTimeout, int readTimeout, long cacheValidTime) {
        this();
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.cacheValidTime = cacheValidTime;
    }

    /**
     * 设置是否使用HTTPS连接
     *
     * @param useHttps true为HTTPS，false为HTTP(默认)
     */
    public void setUseHttps(boolean useHttps) {
        this.useHttps = useHttps;
    }

    /**
     * 设置自定义线程池
     *
     * @param executor 线程池执行器
     */
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    /**
     * 设置缓存有效时间
     *
     * @param cacheValidTime 缓存有效时间(毫秒)
     */
    public void setCacheValidTime(long cacheValidTime) {
        this.cacheValidTime = cacheValidTime;
    }

    /**
     * 设置批次测试间隔
     *
     * @param batchInterval 批次间隔时间(毫秒)
     */
    public void setBatchInterval(long batchInterval) {
        this.batchInterval = batchInterval;
    }

    /**
     * 检查网络状态并配置超时参数
     *
     * @param context 上下文对象
     * @return 是否有网络连接
     */
    public boolean checkNetworkStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected()) {
            return false;
        }

        int networkType = networkInfo.getType();
        if (networkType == ConnectivityManager.TYPE_WIFI) {
            // WiFi环境：缩短超时时间
            connectTimeout = 2000;
            readTimeout = 3000;
        } else if (networkType == ConnectivityManager.TYPE_MOBILE) {
            // 移动数据：延长超时时间
            connectTimeout = 5000;
            readTimeout = 8000;
        }
        return true;
    }

    /**
     * 检查是否开启电池优化
     *
     * @param context 上下文对象
     * @return 是否忽略电池优化
     */
    public boolean isIgnoringBatteryOptimizations(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powerManager != null && powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
    }

    /**
     * 测试单个域名的访问速度(同步方法，不建议在UI线程调用)
     *
     * @param domain 要测试的域名
     * @return 响应时间(毫秒)，失败返回-1
     */
    public long testDomainSpeed(String domain) {
        // 检查缓存
        long cachedResult = getCachedResult(domain);
        if (cachedResult > 0) {
            Log.d("DomainTester", "使用缓存结果: " + domain + " - " + cachedResult + "ms");
            return cachedResult;
        }

        long startTime = System.currentTimeMillis();

        try {
            String protocol = useHttps ? "https" : "http";
            URL url = new URL(protocol + "://" + domain);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            connection.connect();

            int responseCode = connection.getResponseCode();
            connection.disconnect();

            long responseTime = (responseCode == 200) ?
                    (System.currentTimeMillis() - startTime) : -1;

            // 更新缓存
            updateCache(domain, responseTime);
            return responseTime;

        } catch (java.net.UnknownHostException e) {
            Log.e("DomainTester", "DNS解析失败: " + domain, e);
            return -2; // 特殊标记DNS错误
        } catch (java.net.SocketTimeoutException e) {
            Log.e("DomainTester", "连接超时: " + domain, e);
            return -3; // 特殊标记超时错误
        } catch (Exception e) {
            Log.e("DomainTester", "测试域名失败: " + domain, e);
            return -1;
        }
    }

    /**
     * 异步测试多个域名的访问速度
     *
     * @param context  上下文对象
     * @param domains  域名列表
     * @param callback 结果回调接口
     */
    public void testDomainsSpeedAsync(Context context, List<String> domains, SpeedTestCallback callback) {
        if (!checkNetworkStatus(context)) {
            if (callback != null) {
                callback.onTestFailed(TestError.NO_NETWORK, "无网络连接");
            }
            return;
        }

        if (domains == null || domains.isEmpty() || callback == null) {
            if (callback != null) {
                callback.onTestFailed(TestError.UNKNOWN_ERROR, "参数错误");
            }
            return;
        }

        // 去重处理
        List<String> uniqueDomains = new ArrayList<>(new java.util.HashSet<>(domains));
        new SpeedTestTask(uniqueDomains, callback).executeOnExecutor(executor);
    }

    /**
     * 异步测试单个域名的访问速度
     *
     * @param context  上下文对象
     * @param domain   要测试的域名
     * @param callback 结果回调接口
     */
    public void testDomainSpeedAsync(Context context, String domain, SpeedTestCallback callback) {
        if (domain == null || callback == null) {
            if (callback != null) {
                callback.onTestFailed(TestError.UNKNOWN_ERROR, "参数错误");
            }
            return;
        }

        List<String> domains = new ArrayList<>();
        domains.add(domain);
        testDomainsSpeedAsync(context, domains, callback);
    }

    /**
     * 获取缓存结果
     */
    private long getCachedResult(String domain) {
        CachedResult cache = domainCache.get(domain);
        if (cache != null && System.currentTimeMillis() - cache.timestamp < cacheValidTime) {
            return cache.responseTime;
        }
        return -1;
    }

    /**
     * 更新缓存
     */
    private void updateCache(String domain, long responseTime) {
        domainCache.put(domain, new CachedResult() {{
            this.responseTime = responseTime;
            this.timestamp = System.currentTimeMillis();
        }});
    }

    /**
     * 计算测试统计信息
     */
    private TestStatistics calculateStatistics(List<DomainSpeed> results) {
        TestStatistics stats = new TestStatistics();
        stats.totalDomains = results.size();

        if (results.isEmpty()) {
            return stats;
        }

        long sum = 0;
        long min = Long.MAX_VALUE;
        long max = 0;
        int successCount = 0;

        for (DomainSpeed speed : results) {
            if (speed.responseTime > 0) {
                sum += speed.responseTime;
                min = Math.min(min, speed.responseTime);
                max = Math.max(max, speed.responseTime);
                successCount++;
            }
        }

        stats.successfulDomains = successCount;
        stats.averageSpeed = successCount > 0 ? sum / successCount : 0;
        stats.minSpeed = min == Long.MAX_VALUE ? 0 : min;
        stats.maxSpeed = max;

        return stats;
    }

    /**
     * 测速异步任务类（支持批次处理和错误分类）
     */
    private class SpeedTestTask extends AsyncTask<Void, SpeedTestProgress, List<DomainSpeed>> {
        private List<String> domains;
        private SpeedTestCallback callback;
        private TestError lastError = TestError.UNKNOWN_ERROR;
        private String errorMessage;
        private List<DomainSpeed> resultList = new ArrayList<>();
        private int batchIndex = 0;

        public SpeedTestTask(List<String> domains, SpeedTestCallback callback) {
            this.domains = domains;
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (callback != null) {
                callback.onTestStarted();
            }
        }

        @Override
        protected List<DomainSpeed> doInBackground(Void... voids) {
            try {
                // 分批次处理域名（每5个一批）
                int batchSize = 5;
                int totalBatches = (domains.size() + batchSize - 1) / batchSize;

                for (int batch = 0; batch < totalBatches && !isCancelled(); batch++) {
                    int start = batch * batchSize;
                    int end = Math.min(start + batchSize, domains.size());

                    for (int i = start; i < end && !isCancelled(); i++) {
                        String domain = domains.get(i);
                        long responseTime = testDomainSpeed(domain);

                        // 转换错误码
                        TestError error = TestError.UNKNOWN_ERROR;
                        String msg = "测试失败";

                        if (responseTime == -2) {
                            error = TestError.DNS_FAILED;
                            msg = "DNS解析失败";
                        } else if (responseTime == -3) {
                            error = TestError.CONNECTION_TIMEOUT;
                            msg = "连接超时";
                        } else if (responseTime < 0) {
                            error = TestError.SERVER_ERROR;
                            msg = "服务器错误";
                        }

                        DomainSpeed speed = new DomainSpeed(domain, responseTime);
                        resultList.add(speed);

                        // 发布进度
                        SpeedTestProgress progress = new SpeedTestProgress();
                        progress.speed = speed;
                        progress.currentIndex = i;
                        progress.totalCount = domains.size();
                        progress.error = error;
                        progress.errorMessage = msg;
                        publishProgress(progress);
                    }

                    // 批次间隔
                    if (batch < totalBatches - 1 && !isCancelled()) {
                        Thread.sleep(batchInterval);
                    }

                    batchIndex = batch;
                }

                // 按响应时间排序
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(resultList, Comparator.comparingLong(DomainSpeed::getResponseTime));
                }
                return resultList;

            } catch (Exception e) {
                Log.e("DomainTester", "测速任务异常", e);
                lastError = TestError.UNKNOWN_ERROR;
                errorMessage = e.getMessage();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(SpeedTestProgress... values) {
            super.onProgressUpdate(values);
            if (callback != null && values.length > 0) {
                SpeedTestProgress progress = values[0];
                if (progress.error == TestError.UNKNOWN_ERROR) {
                    callback.onDomainTested(progress.speed);
                } else {
                    callback.onDomainTestFailed(progress.speed, progress.error, progress.errorMessage);
                }
            }
        }

        @Override
        protected void onPostExecute(List<DomainSpeed> results) {
            super.onPostExecute(results);
            if (callback != null) {
                if (results == null) {
                    callback.onTestFailed(lastError, errorMessage);
                } else {
                    TestStatistics stats = calculateStatistics(results);
                    callback.onTestCompleted(results, stats);
                }
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (callback != null) {
                callback.onTestCancelled();
            }
        }
    }

    // 进度更新数据类
    private static class SpeedTestProgress {
        DomainSpeed speed;
        int currentIndex;
        int totalCount;
        TestError error;
        String errorMessage;
    }

    /**
     * 测速结果回调接口（增强版）
     */
    public interface SpeedTestCallback {
        void onTestStarted();

        void onDomainTested(DomainSpeed speed);

        void onDomainTestFailed(DomainSpeed speed, TestError error, String message);

        void onTestCompleted(List<DomainSpeed> results, TestStatistics statistics);

        void onTestFailed(TestError error, String message);

        void onTestCancelled();
    }
}