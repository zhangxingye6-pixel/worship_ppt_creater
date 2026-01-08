package claygminx.worshipppt.common.config;

import claygminx.worshipppt.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * 系统配置
 * <p>使用用户目录。此软件的配置文件夹的名称是.worship-ppt，在里面放配置数据。</p>
 * TODO 用户配置每次都丢失 需要解决
 */
@Slf4j
public class SystemConfig {

    /**
     * 应用配置文件夹
     */
    public final static String APP_CONFIG_DIR_PATH = ".worship-ppt";

    /**
     * 配置文件的名称
     */
    public final static String APP_CONFIG_NAME = "system.config";

    /**
     * 核心配置
     */
    public final static String CORE_PROPERTIES = "core.properties";

    // 用户配置文件路径（运行时赋值）
    public static String USER_CONFIG_FILE_PATH = "";

    /**
     * 禁止用户自定义，只能是jar包内定义的配置参数
     */
    private final static String[] EXCLUDE_NAMESPACE = new String[] {
            "github", "gitee", "project"
    };

    /**
     * 系统属性实例对象
     */
    public final static Properties properties = new Properties();

    // 完成对properties的初始化, 初始化的结果是用户配置与核心配置会合并到properties中
    static {
        // 1.先去用户目录(每个系统可能不同)看是否已经有配置
        // System.getProperty("user.home")获得用户主路径
        File appDir = new File(System.getProperty("user.home"), APP_CONFIG_DIR_PATH);
        if (!appDir.exists()) {
            // 创建一个隐藏目录.worship-ppt
            logger.info("创建目录{}", APP_CONFIG_DIR_PATH);
            boolean flag = appDir.mkdirs();
            if (!flag) {
                logger.error("目录{}创建失败，系统退出！", appDir.getAbsolutePath());
                JOptionPane.showMessageDialog(
                        null,
                        "目录" + appDir.getAbsolutePath() + "创建失败，系统退出！",
                        "错误提示",
                        JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            }
        }

        // 查找appDir目录中的APP_CONFIG_NAME配置文件（user_home/.worship-ppt/system.config）
        File userConfigFile = new File(appDir, APP_CONFIG_NAME);
        ClassLoader classLoader = SystemConfig.class.getClassLoader();
        if (userConfigFile.exists()) {
            logger.info("配置文件已经存在，直接使用。");
        } else {
            logger.info("用户目录不存在配置文件，拷贝一份过去。");
            try (InputStream inputStream = classLoader.getResourceAsStream(APP_CONFIG_NAME)) { // 读取jar包中的默认配置文件(system.config)
                if (inputStream != null) {
                    FileUtils.copyToFile(inputStream, userConfigFile);
                } else {
                    logger.error("配置文件初始化失败，系统退出！");
                    JOptionPane.showMessageDialog(
                            null,
                            "配置文件初始化失败，系统退出！",
                            "错误提示",
                            JOptionPane.ERROR_MESSAGE
                    );
                    System.exit(1);
                }
            } catch (Exception e) {
                logger.error("配置文件初始化失败，系统退出！", e);
                JOptionPane.showMessageDialog(
                        null,
                        "配置文件初始化失败，系统退出！",
                        "错误提示",
                        JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            }
        }

        // 2.读取配置文件的路径
        String userPropertiesPath = null;
            // 解析默认配置文件
            // 取得默认配置中key为SystemConfigPath的值，表示用户配置的路径
            // Properties的解析规则会导致路径中的'\'丢失，这也是用户配置总是加载失败的根本原因
            // userPropertiesPath = cacheSystemConfig.getProperty("SystemConfigPath");

            // 更改为使用NIO的Files来解决读取出错的问题
        List<String> configurations = null;
        try {
            configurations = Files.readAllLines(Paths.get(userConfigFile.getAbsolutePath()), StandardCharsets.UTF_8);
            userPropertiesPath = (configurations.get(0).split("="))[1];
            logger.info("SystemConfigPath={}", userPropertiesPath);
        } catch (IOException ex) {
            logger.error("读取SystemConfigPath失败！", ex);
            JOptionPane.showMessageDialog(
                    null,
                    "读取SystemConfigPath失败！",
                    "错误提示",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }


        // 3.加载用户配置
        Properties userProperties = null;
        try {
            userProperties = loadUserProperties(userPropertiesPath);
        } catch (Exception e) {
            logger.error("用户配置加载失败！", e);
            JOptionPane.showMessageDialog(
                    null,
                    "用户配置加载失败！",
                    "错误提示",
                    JOptionPane.ERROR_MESSAGE
            );
            userPropertiesPath = JOptionPane.showInputDialog("你可以输入正确的配置文件的路径，再重新启动:)");
            try {
                update(userPropertiesPath);// 获取用户配置错误后更新
                userProperties = loadUserProperties(userPropertiesPath);
            } catch (IOException e2) {
                logger.error("用户配置加载失败！", e2);
                JOptionPane.showMessageDialog(
                        null,
                        "用户配置再次尝试加载失败！",
                        "错误提示",
                        JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            }
        }

        // 4.加载核心配置
        Properties coreProperties = new Properties();
        try (InputStreamReader reader = new InputStreamReader(
                // 从ClassPath中获取核心配置
                Objects.requireNonNull(classLoader.getResourceAsStream(CORE_PROPERTIES)),
                StandardCharsets.UTF_8)) {
            coreProperties.load(reader);
            logger.info("核心配置加载成功");

            Set<Object> keySet = coreProperties.keySet();
            for (Object key : keySet) {
                logger.info("{}={}", key, coreProperties.get(key));
            }
        } catch (Exception e) {
            logger.error("{}加载失败！", CORE_PROPERTIES, e);
            JOptionPane.showMessageDialog(
                    null,
                    CORE_PROPERTIES + "加载失败！",
                    "错误提示",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }

        // 5.合并配置
        try {
            // 合并核心配置
            properties.putAll(coreProperties);
            logger.info("合并了核心配置");

            mergeUserProperties(userProperties);

            USER_CONFIG_FILE_PATH = userPropertiesPath;
        } catch (Exception e) {
            logger.error("合并配置失败！", e);
            JOptionPane.showMessageDialog(
                    null,
                    "合并配置失败！",
                    "错误提示",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }
    }



    private SystemConfig() {
    }

    /**
     * 获取配置中的字符串值
     * @param key 键
     * @return 系统值
     */
    public static String getString(String key) {
        return properties.getProperty(key);
    }

    /**
     * 获取配置中的int值
     * @param key 键
     * @return 系统值
     */
    public static int getInt(String key) {
        String strValue = properties.getProperty(key);
        try {
            return Integer.parseInt(strValue);
        } catch (Exception e) {
            throw new SystemException("根据" + key + "获取int值失败！", e);
        }
    }

    /**
     * 获取配置中的double值
     * @param key 键
     * @return 系统值
     */
    public static double getDouble(String key) {
        String strValue = properties.getProperty(key);
        try {
            return Double.parseDouble(strValue);
        } catch (Exception e) {
            throw new SystemException("根据" + key + "获取double值失败！", e);
        }
    }

    /**
     * 更新用户配置的路径
     * @param propFilePath
     * @throws IOException
     */
    public static void update(String propFilePath) throws IOException {
        // "SystemConfigPath=propFilePath"
        String conf = "SystemConfigPath=" + propFilePath;
        // 用户主路径下的配置目录（user_home/.worship-ppt）
        File appDir = new File(System.getProperty("user.home"), APP_CONFIG_DIR_PATH);
        // 配置目录下的APP_CONFIG_NAME配置文件
        File systemConfigFile = new File(appDir, APP_CONFIG_NAME);

        // 将用户输入的配置文件路径写入默认配置
        FileUtils.writeStringToFile(systemConfigFile, conf, StandardCharsets.UTF_8);

        // 测试写入的内容
//        Path path = Paths.get(systemConfigFile.getAbsolutePath());
//        logger.info("默认配置的绝对路径：" + path);
//        List<String> strings = Files.readAllLines(path, StandardCharsets.UTF_8);
//        for (String string : strings) {
//            logger.info("写入后读取到配置：" + string);
//        }

        // 重新加载配置
        Properties userProperties = loadUserProperties(propFilePath);
        // 合并用户配置
        mergeUserProperties(userProperties);
        // 给用户的配置路径动态赋值
        USER_CONFIG_FILE_PATH = propFilePath;
        // 测试打印重新输入后的用户配置路径
        logger.info("重新输入的用户配置文件路径" + USER_CONFIG_FILE_PATH);
    }

    /**
     * 加载用户配置
     * @param propFilePath
     * @return
     * @throws IOException
     */
    private static Properties loadUserProperties(String propFilePath) throws IOException {
        Properties userProperties = new Properties();
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(propFilePath), StandardCharsets.UTF_8)) {
            userProperties.load(reader);
            logger.info("用户配置加载成功");

            // 获取用户配置中所有的key
            Set<Object> keySet = userProperties.keySet();
            for (Object key : keySet) {
                logger.info("{}={}", key, userProperties.get(key));
            }
        }
        return userProperties;
    }

    /**
     * 合并用户配置
     * @param userProperties
     */
    private static void mergeUserProperties(Properties userProperties) {
        Set<Object> keySet = userProperties.keySet();
        for (Object keyObj : keySet) {
            String key = (String) keyObj;
            String[] split = key.split("[.]");
            String ns = split[0];
            boolean found = false;
            // 查找用户是否配置了不可配置的内容, 找到的内容不进行合并
            for (int i = 0; i < EXCLUDE_NAMESPACE.length; i++) {
                if (EXCLUDE_NAMESPACE[i].equals(ns)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                logger.info("跳过{}", key);
            } else {
                properties.put(key, userProperties.get(key));
            }
        }
        logger.info("合并了用户配置");
    }
}
