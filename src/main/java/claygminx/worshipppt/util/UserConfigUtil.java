package claygminx.worshipppt.util;

import claygminx.worshipppt.common.config.SystemConfig;
import claygminx.worshipppt.exception.SystemException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Slf4j
public class UserConfigUtil {

    /**
     * 用户配置路径
     */
    private final static String USER_CONFIG_PATH = SystemConfig.USER_CONFIG_FILE_PATH;

    public static String getUserConfig(String key) {
        // step1. 获取输入流
        try (InputStream configInputStream = getConfigInputStream(USER_CONFIG_PATH)) {
            if (configInputStream == null) {
                logger.info("未找到用户配置: 请检查路径" + USER_CONFIG_PATH);
                return "";
            }
            // step2. 读取配置
            Properties properties = new Properties();
            properties.load(new InputStreamReader(configInputStream, StandardCharsets.UTF_8));
            return properties.getProperty(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 获取用户配置输入流
     * @param configPath
     * @return
     */
    private static InputStream getConfigInputStream(String configPath) {
        // 判断存储路径合法性
        if (USER_CONFIG_PATH.isEmpty()) {
            throw new SystemException("用户配置文件未初始化");
        }
        InputStream configInputStream = UserConfigUtil.class.getClassLoader().getResourceAsStream(configPath);
        return configInputStream;
    }
}

