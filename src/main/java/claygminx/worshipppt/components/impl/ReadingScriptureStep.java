package claygminx.worshipppt.components.impl;

import claygminx.worshipppt.common.config.SystemConfig;
import claygminx.worshipppt.common.entity.ScriptureEntity;
import claygminx.worshipppt.components.ScriptureService;
import claygminx.worshipppt.exception.ScriptureNumberException;
import claygminx.worshipppt.exception.WorshipStepException;
import claygminx.worshipppt.common.Dict;
import claygminx.worshipppt.util.ScriptureUtil;
import claygminx.worshipppt.util.UserConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.xslf.usermodel.*;

import java.awt.*;

/**
 * 读经阶段
 */
public class ReadingScriptureStep extends AbstractWorshipStep {

    private final static Logger logger = LoggerFactory.getLogger(ReadingScriptureStep.class);
    // 动态控制经文行数时使用
//    private final static int BEST_LINE_COUNT = 4;
//    private final static int BEST_HEIGHT = 207;
//    private final static int MAX_CHAR_COUNT = 32;\


    private final String READ_SCRIPTURE_FONT_SIZE_KEY = "ppt.readingScripture.fontSize";
    private final Double DEFAULT_FONT_SIZE = 35.0;

    private final ScriptureService scriptureService;
    private final String scriptureNumber;

    public ReadingScriptureStep(XMLSlideShow ppt, String layout, ScriptureService scriptureService, String scriptureNumber) {
        super(ppt, layout);
        this.scriptureService = scriptureService;
        this.scriptureNumber = scriptureNumber;
    }

    /**
     * 解析读经经文，并制作读经幻灯片
     *
     * @throws WorshipStepException
     */
    @Override
    public void execute() throws WorshipStepException {
        logger.info("开始读经" + scriptureNumber);
        ScriptureEntity scriptureEntity;
        try {
            scriptureEntity = scriptureService.getScriptureWithFormat(scriptureNumber, SystemConfig.getString(Dict.ScriptureProperty.FORMAT4));
        } catch (ScriptureNumberException e) {
            throw new WorshipStepException("解析经文编号 [" + scriptureNumber + "] 时出错！", e);
        }
        if (scriptureEntity == null) {
            return;
        }
        String scripture = scriptureEntity.getScripture();
        String[] scriptureArray = scripture.replaceAll("\r", "").split("\n");
        logger.info("共{}段经文", scriptureArray.length);

        XMLSlideShow ppt = getPpt();
        XSLFSlideLayout layout = ppt.findLayout(getLayout());


        // 按预设的读经模板，每一行最多32个中文字符，每一页行数最佳是4行，尽量控制最多6行
        // 根据当前的需要，为了使字号最大化，读经部分的幻灯片每页只保留两节经文
        XSLFSlide slide = null;
        XSLFTextShape placeholder;
//        int lineCount = 0;      // 行计数器 动态控制时使用
        int slideCount = 0;     // 页计数器 动态控制时使用

        Double finalFontSize = getUserFontSizeConfig();

        for (int i = 0; i < scriptureArray.length; i++) {
            String scriptureItem = scriptureArray[i];
            // 创建新一页
            if ((i + 1) % 2 == 1) {
                slideCount++;
                slide = ppt.createSlide(layout);
                // 获取版面中的占位符0
                placeholder = slide.getPlaceholder(0);
                String text = placeholder.getText();
                // 将自定义占位符替换成经文编号
                placeholder.setText(text.replace(getCustomPlaceholder(), scriptureNumber));
                // 获取并清空占位符1中的默认文字（同时会将段落和文本块清除，需要重新创建）
                placeholder = slide.getPlaceholder(1);
                placeholder.clearText();
                logger.info("开始第{}张读经幻灯片...", slideCount);
            }

            // 往正文添加一段经文，注意，可能因为经文字数较多，一行容不下
            placeholder = slide.getPlaceholder(1);
            XSLFTextParagraph paragraph = placeholder.addNewTextParagraph();        // 添加新的段落
            useCustomLanguage(paragraph);                                           // 设置段落格式
            XSLFTextRun textRun = paragraph.addNewTextRun();                        // 添加新的文本块
            String trimScriptureItem = scriptureItem.trim();
            textRun.setText(trimScriptureItem);
            // 设置字号
            textRun.setFontSize(finalFontSize);


            // 对不同的经节设置不同的字体颜色
            if (trimScriptureItem.startsWith("会众：")) {
                // 会众读经：蓝色
                ScriptureUtil.setScriptureFontColor(textRun, Dict.PPTProperty.RGB_FONT_COLOR_BLUE);
            } else if (trimScriptureItem.startsWith("主领：")) {
                // 主领读经：黑色
                ScriptureUtil.setScriptureFontColor(textRun, Dict.PPTProperty.RGB_FONT_COLOR_BLACK);
            } else {
                // 合读部分：红色
                ScriptureUtil.setScriptureFontColor(textRun, Dict.PPTProperty.RGB_FONT_COLOR_RED);
            }

            // 控制幻灯片里经文的数量和幻灯片的数量 动态控制时使用
//            int currentHeight = (int) Math.ceil(placeholder.getTextHeight());// 当前文本框的高度
//            int n = (int) Math.ceil((double) trimScriptureItem.length() / MAX_CHAR_COUNT);// 此节经文可能展示为多少行
//            lineCount += n;
//            if (currentHeight >= BEST_HEIGHT || lineCount >= BEST_LINE_COUNT) {
//                if (i < scriptureArray.length - 2) {
//                    logger.debug("当前幻灯片有{}个段落，应该有{}行", placeholder.getTextParagraphs().size(), lineCount);
//                    lineCount = 0;
//                }
//            }
        }
    }

    /**
     * 获取配置中的读经字号设置
     * 如果没有相关设置则使用默认字号35
     * @return
     */
    private Double getUserFontSizeConfig() {
        // 经文字号设置
        Double finalFontSize;
        String userConfig = UserConfigUtil.getUserConfig(READ_SCRIPTURE_FONT_SIZE_KEY);
        if (userConfig.isEmpty()){
            logger.info("未查询到读经经文字号，使用默认字号 - " + DEFAULT_FONT_SIZE);
            finalFontSize = Double.valueOf(DEFAULT_FONT_SIZE);
        }else {
            logger.info("读经经文使用当前使用自定义字号 - " + userConfig);
            finalFontSize = Double.valueOf(userConfig);
        }
        return finalFontSize;
    }

}
