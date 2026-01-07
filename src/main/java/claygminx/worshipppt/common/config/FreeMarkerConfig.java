package claygminx.worshipppt.common.config;

import claygminx.worshipppt.common.Dict;
import claygminx.worshipppt.exception.SystemException;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import java.nio.charset.StandardCharsets;

/**
 * FreeMarker模板配置类
 */
public class FreeMarkerConfig {

    /**
     * FreeMarker核心配置
     */
    private final static Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);

    static {
        try {
            // 获取经文格式所在目录
            String templatePath = SystemConfig.getString(Dict.ScriptureProperty.PATH);
            // 设置默认解码方式
            configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
            // 模板异常处理
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            // 在ClassPath中加载模板文件
            configuration.setClassForTemplateLoading(FreeMarkerConfig.class, templatePath);
        } catch (Exception e) {
            throw new SystemException("无法初始化FreeMarker配置！", e);
        }
    }

    private FreeMarkerConfig() {
    }

    /**
     * 获取FreeMarker配置实例对象
     * @return FreeMarker配置实例对象
     */
    public static Configuration getConfiguration() {
        return configuration;
    }

}
