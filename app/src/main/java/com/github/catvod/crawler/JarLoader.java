package com.github.catvod.crawler;

import android.content.Context;
import android.os.Build;

import com.github.tvbox.osc.base.App;
import com.github.tvbox.osc.util.LOG;
import com.github.tvbox.osc.util.MD5;
import com.lzy.okgo.OkGo;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;
import okhttp3.Response;

public class JarLoader {
    private ConcurrentHashMap<String, ClassLoader> classLoaders = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Method> proxyMethods = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Spider> spiders = new ConcurrentHashMap<>();
    private volatile String recentJarKey = "";

    /**
     * 不要在主线程调用我
     *
     * @param jarFile jar文件
     */
    public boolean load(File jarFile) {
        spiders.clear();
        recentJarKey = "main";
        proxyMethods.clear();
        classLoaders.clear();
        return loadClassLoader(jarFile, "main");
    }


    /**
     * DEX在不用版本的API需要做不同的适配
     */
    private boolean loadClassLoader(File jarFile, String jarKey) {
        boolean success = false;
        try {

            File cacheDir = new File(App.getInstance().getCacheDir().getAbsolutePath() + "/catvod_csp");
            if (!cacheDir.exists()) cacheDir.mkdirs();
            jarFile.setReadOnly();

            ClassLoader classLoader = createClassLoader(jarFile.getAbsolutePath(), cacheDir.getAbsolutePath());
            if (classLoader == null) {
                LOG.e("类加载器初始化失败");
                return false;
            }

            // make force wait here, some device async dex load
            int count = 0;
            do {
                try {
                    Class<?> classInit = classLoader.loadClass("com.github.catvod.spider.Init");
                    if (classInit != null) {
                        Method method = classInit.getMethod("init", Context.class);
                        method.invoke(null, App.getInstance());
                        success = true;
                        try {
                            Class<?> proxy = classLoader.loadClass("com.github.catvod.spider.Proxy");
                            Method mth = proxy.getMethod("proxy", Map.class);
                            proxyMethods.put(jarKey, mth);
                        } catch (Throwable th) {
                            LOG.e(th);
                        }
                        break;
                    }
                    Thread.sleep(200);
                } catch (Throwable th) {
                    LOG.e(th);
                }
                count++;
            } while (count < 5);

            if (success) {
                classLoaders.put(jarKey, classLoader);
            }
        } catch (Throwable th) {
            LOG.e(th);
        }
        return success;
    }


    private ClassLoader createClassLoader(String jarPath, String cachePath) {
        try {

            Context context = App.getInstance().getApplicationContext();

            // Android 14+ 特殊处理
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // 尝试放宽反射限制（仅对部分API有效）
                tryToRelaxReflectionRestrictions();

                // 在Android 14上，优先使用PathClassLoader加载应用私有目录中的dex
                if (jarPath.startsWith(App.getInstance().getApplicationInfo().dataDir)) {
                    LOG.i("使用PathClassLoader加载: " + jarPath);
                    return new PathClassLoader(jarPath, context.getClassLoader());
                }
            }

            // 对Android 8.0+的DexClassLoader优化
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LOG.i("使用DexClassLoader加载，API级别: " + Build.VERSION.SDK_INT);
                return new DexClassLoader(jarPath, cachePath, null, context.getClassLoader());
            }

            // 旧版Android的处理
            LOG.i("使用兼容的DexClassLoader加载，API级别: " + Build.VERSION.SDK_INT);
            return new DexClassLoader(jarPath, cachePath, null, context.getClassLoader());
        } catch (Exception e) {
            LOG.e("创建类加载器失败", e);
            return null;
        }
    }

    // 尝试放宽反射限制（针对Android 8.0+）
    private static void tryToRelaxReflectionRestrictions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                // 获取VMRuntime类
                Class<?> vmRuntimeClass = Class.forName("dalvik.system.VMRuntime");
                Method getRuntime = vmRuntimeClass.getDeclaredMethod("getRuntime");
                Method setHiddenApiExemptions = vmRuntimeClass.getDeclaredMethod("setHiddenApiExemptions", String[].class);

                // 调用setHiddenApiExemptions("L")以允许所有反射访问
                Object vmRuntime = getRuntime.invoke(null);
                setHiddenApiExemptions.invoke(vmRuntime, new Object[]{new String[]{"L"}});
                LOG.i("已尝试放宽反射限制");
            } catch (Exception e) {
                LOG.e("放宽反射限制失败: " + e.getMessage());
                // 继续执行，不要因为这个失败而终止整个加载过程
            }
        }
    }


    private ClassLoader loadJarInternal(String jar, String md5, String key) {
        if (classLoaders.contains(key)) return classLoaders.get(key);
        File jarFile = new File(App.getInstance().getFilesDir().getAbsolutePath() + "/" + key + ".jar");
        if (!md5.isEmpty()) {
            if (jarFile.exists() && MD5.getFileMd5(jarFile).equalsIgnoreCase(md5)) {
                loadClassLoader(jarFile, key);
                return classLoaders.get(key);
            }
        }
        try {
            Response response = OkGo.<File>get(jar).execute();
            InputStream is = response.body().byteStream();
            OutputStream os = new FileOutputStream(jarFile);
            try {
                byte[] buffer = new byte[2048];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            } finally {
                try {
                    is.close();
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            loadClassLoader(jarFile, key);
            return classLoaders.get(key);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public Spider getSpider(String key, String cls, String ext, String jar) {


        try {
            String clsKey = cls.replace("csp_", "");
            String jarUrl = "";
            String jarMd5 = "";
            String jarKey;
            if (jar.isEmpty()) {
                jarKey = "main";
            } else {
                String[] urls = jar.split(";md5;");
                jarUrl = urls[0];
                jarKey = MD5.string2MD5(jarUrl);
                jarMd5 = urls.length > 1 ? urls[1].trim() : "";
            }
            recentJarKey = jarKey;
            if (spiders.containsKey(key)) return spiders.get(key);
            ClassLoader classLoader = null;

            if (jarKey != null) {
                if (jarKey.equals("main")) {
                    classLoader = classLoaders.get("main");
                } else {
                    classLoader = loadJarInternal(jarUrl, jarMd5, jarKey);
                }
            }

            if (classLoader == null) return new SpiderNull();

            Class<?> clazz = classLoader.loadClass("com.github.catvod.spider." + clsKey);
            if (clazz != null && Spider.class.isAssignableFrom(clazz)) {
                Spider sp = (Spider) clazz.newInstance();
                sp.init(App.getInstance(), ext);
                if (!jar.isEmpty()) {
                    sp.homeContent(false); // 增加此行 应该可以解决部分写的有问题源的历史记录问题 但会增加这个源的首次加载时间 不需要可以已删掉
                }
                spiders.put(key, sp);
                return sp;
            }
            return new SpiderNull();
        } catch (ClassNotFoundException e) {
            LOG.e("类未找到: " + e.getMessage());
            // 检查类名是否正确，Dex文件是否包含该类
        } catch (NoClassDefFoundError e) {
            LOG.e("类定义缺失: " + e.getMessage());
            // 可能是依赖库缺失或Dex文件损坏
        } catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
            LOG.e("参数错误: " + e.getMessage());
        } catch (InvocationTargetException e) {
            // 目标方法内部抛出的异常
            Throwable targetEx = e.getTargetException();
            LOG.e("目标方法异常: " + targetEx.getMessage());
        } catch (ExceptionInInitializerError e) {
            LOG.e("类初始化异常: " + e.getMessage());
            // 检查静态初始化块或静态变量
        } catch (Exception e) {
            LOG.e("普通异常: " + e.getMessage());
        }
        return new SpiderNull();
    }

    public JSONObject jsonExt(String key, LinkedHashMap<String, String> jxs, String url) {
        try {
            ClassLoader classLoader = classLoaders.get("main");
            String clsKey = "Json" + key;
            String hotClass = "com.github.catvod.parser." + clsKey;
            Class jsonParserCls = classLoader.loadClass(hotClass);
            Method mth = jsonParserCls.getMethod("parse", LinkedHashMap.class, String.class);
            return (JSONObject) mth.invoke(null, jxs, url);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return null;
    }

    public JSONObject jsonExtMix(String flag, String key, String name, LinkedHashMap<String, HashMap<String, String>> jxs, String url) {
        try {
            ClassLoader classLoader = classLoaders.get("main");
            String clsKey = "Mix" + key;
            String hotClass = "com.github.catvod.parser." + clsKey;
            Class jsonParserCls = classLoader.loadClass(hotClass);
            Method mth = jsonParserCls.getMethod("parse", LinkedHashMap.class, String.class, String.class, String.class);
            return (JSONObject) mth.invoke(null, jxs, name, flag, url);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return null;
    }

    public Object[] proxyInvoke(Map params) {
        try {
            Method proxyFun = proxyMethods.get(recentJarKey);
            if (proxyFun != null) {
                return (Object[]) proxyFun.invoke(null, params);
            }
        } catch (Throwable th) {

        }
        return null;
    }
}