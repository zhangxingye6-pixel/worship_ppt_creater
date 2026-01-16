package claygminx.worshipppt.components.impl;

import claygminx.worshipppt.common.Dict;
import claygminx.worshipppt.common.config.SystemConfig;
import claygminx.worshipppt.common.entity.PreachEntity;
import claygminx.worshipppt.common.entity.ScriptureEntity;
import claygminx.worshipppt.components.ScriptureService;
import claygminx.worshipppt.exception.PPTLayoutException;
import claygminx.worshipppt.exception.ScriptureNumberException;
import claygminx.worshipppt.exception.WorshipStepException;
import claygminx.worshipppt.util.ScriptureUtil;
import claygminx.worshipppt.util.TextUtil;
import org.apache.poi.xslf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Text;

import java.awt.*;

/**
 * 证道经文阶段
 */
public class PreachScriptureStep extends AbstractWorshipStep {
    // 文本参数
    // TODO 修改经文页面布局
    private final static int BEST_LINE_COUNT = 8;       // 最佳行数
    private final static int BEST_HEIGHT = 400;         // 最佳高度
    private final static int MAX_CHAR_COUNT = 32;       // 每行最大字数

    // 经文服务对象
    private final ScriptureService scriptureService;

    private final static Logger logger = LoggerFactory.getLogger(PreachScriptureStep.class);

    private final PreachEntity preachEntity;

    public PreachScriptureStep(XMLSlideShow ppt, String layout, ScriptureService scriptureService, PreachEntity preachEntity) {
        super(ppt, layout);
        this.preachEntity = preachEntity;
        this.scriptureService = scriptureService;
    }

    @Override
    public void execute() throws WorshipStepException, PPTLayoutException {
        String scriptureNumber;
        scriptureNumber = preachEntity.getScriptureNumber();
        logger.info("开始制作证道经文" + scriptureNumber);

        ScriptureEntity scriptureEntity;
        try {
            // 获取经文实体，在证道部分的经文使用FORMART3 - 【约1:1】太初有道……
            logger.info("经文格式" + Dict.ScriptureProperty.FORMAT3);
            scriptureEntity = scriptureService.getScriptureWithFormat(scriptureNumber, SystemConfig.getString(Dict.ScriptureProperty.FORMAT3));
        } catch (ScriptureNumberException e) {
            throw new WorshipStepException("解析经文编号 [" + scriptureNumber + "] 时出错！", e);
        }
        if (scriptureEntity == null) {
            return;
        }

        String[] scriptureArray = scriptureEntity.getScripture().replaceAll("\r", "").split("\n");
        logger.info("共{}段经文", scriptureArray.length);

        XMLSlideShow ppt = getPpt();
        XSLFSlideLayout layout = ppt.findLayout(getLayout());

        // 按预设的模板，每一行最多32个中文字符，每一页行数最佳是6行，尽量控制最多8行
        XSLFSlide slide = null;
        XSLFTextShape placeholder = null;
        int lineCount = 0, slideCount = 0;
        for (int i = 0; i < scriptureArray.length; i++) {
            // 当前页面中已经添加了几节经文
//            int currentVerseCount;

            String scriptureItem = scriptureArray[i];
            if (lineCount == 0) {
                slideCount++;
                // 根据模板创建新的空白幻灯片
                slide = ppt.createSlide(layout);
                // 获取第一占位符（经文章节编号部分，不需要清空）
                placeholder = TextUtil.getPlaceholderSafely(slide, 0, getLayout(), "标题部分");
                XSLFTextRun titleTextRun = TextUtil.clearAndCreateTextRun(placeholder);
                titleTextRun.setText(scriptureNumber);
                titleTextRun.setFontFamily(AbstractWorshipStep.DEFAULT_FONT_FAMILY);
                titleTextRun.setFontSize(SystemConfig.getUserConfigOrDefault(Dict.PPTProperty.POETRY_TITLE_FONT_SIZE, AbstractWorshipStep.DEFAULT_TITLE_FONT_SIZE));
                TextUtil.setScriptureFontColor(titleTextRun, TextUtil.FontColor.RGB_FONT_COLOR_WHITE);

                // 获取第二占位符（经文区域），并且清空，填充新的经文
                placeholder = TextUtil.getPlaceholderSafely(slide, 1, getLayout(), "正文部分");
                placeholder.clearText();
                logger.info("开始第{}张证道经文幻灯片...", slideCount);
            }

            // 往正文添加一段经文，注意，可能因为经文字数较多，一行容不下
            placeholder = TextUtil.getPlaceholderSafely(slide, 1, getLayout(), "正文部分");
            XSLFTextParagraph paragraph = placeholder.addNewTextParagraph();
            useCustomLanguage(paragraph);
            XSLFTextRun textRun = paragraph.addNewTextRun();
            String trimScriptureItem = scriptureItem.trim();
            textRun.setText(trimScriptureItem);

            // 每一节的颜色不同，与读经颜色顺序保持一致，先蓝色后黑色
            if(i % 2 == 1){
                // 奇数行，显示蓝色字体
                TextUtil.setScriptureFontColor(textRun, claygminx.worshipppt.util.TextUtil.FontColor.RGB_FONT_COLOR_BLUE);
            }else{
                // 偶数行，显示黑色字体
                TextUtil.setScriptureFontColor(textRun, claygminx.worshipppt.util.TextUtil.FontColor.RGB_FONT_COLOR_BLACK);
            }

            // 控制幻灯片里经文的数量和幻灯片的数量
            // TODO 证道经文部分的文本可以更多一些
            int currentHeight = (int) Math.ceil(placeholder.getTextHeight());// 当前文本框的高度
            // 测试打印
            logger.info("当前文本框高度：" + currentHeight);
            int n = (int) Math.ceil((double) trimScriptureItem.length() / MAX_CHAR_COUNT);// 此节经文可能展示为多少行
            lineCount += n;
            // 当前文本框高度超过 或者经文行数超过设定的最佳值
            if (currentHeight >= BEST_HEIGHT || lineCount >= BEST_LINE_COUNT) {
                if (i < scriptureArray.length - 2) {
                    logger.debug("当前幻灯片有{}个段落，应该有{}行", placeholder.getTextParagraphs().size(), lineCount);
                    lineCount = 0;
                }
            }
        }

        logger.info("证道经文幻灯片制作完成");
    }
}
