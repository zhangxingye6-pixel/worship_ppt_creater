package claygminx.worshipppt.components.impl;

import claygminx.worshipppt.common.config.SystemConfig;
import claygminx.worshipppt.common.entity.ScriptureEntity;
import claygminx.worshipppt.common.entity.ScriptureNumberEntity;
import claygminx.worshipppt.components.ScriptureService;
import claygminx.worshipppt.components.WorshipStep;
import claygminx.worshipppt.exception.PPTLayoutException;
import claygminx.worshipppt.exception.ScriptureServiceException;
import claygminx.worshipppt.common.Dict;
import claygminx.worshipppt.util.TextUtil;
import org.apache.poi.xslf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 通用的敬拜阶段抽象类
 */
public abstract class AbstractWorshipStep implements WorshipStep {

    private final static Logger logger = LoggerFactory.getLogger(AbstractWorshipStep.class);

    private final XMLSlideShow ppt;
    private final String layout;

    // 默认文本常量
    public final static double DEFAULT_TITLE_FONT_SIZE = 40.0;
    public final static double DEFAULT_SCRIPTURE_FONT_SIZE = 35.0;
    public final static String DEFAULT_FONT_FAMILY = "微软雅黑";
    public final static double DEFAULT_STEP_COVER_FONT_SIZE = 60.0;

    /**
     * 通用的构造器
     * @param ppt PPT对象
     * @param layout 幻灯片母版中版式的布局名称
     */
    public AbstractWorshipStep(XMLSlideShow ppt, String layout) {
        this.ppt = ppt;
        this.layout = layout;
    }

    public XMLSlideShow getPpt() {
        return ppt;
    }

    public String getLayout() {
        return layout;
    }

    /**
     * 获取自定义的占位符
     * <p>制作幻灯片时，你可以将此占位符替换为其它字符串。</p>
     * @return 文本框中的占位符
     */
    public String getCustomPlaceholder() {
        return SystemConfig.getString(Dict.PPTProperty.GENERAL_PLACEHOLDER);
    }

    public String getFontFamily() {
        return SystemConfig.getString(Dict.PPTProperty.GENERAL_FONT_FAMILY);
    }

    /**
     * 获取标题和经文
     * @param scriptureService 经文服务对象
     * @param scriptureNumberList 经文编号实体对象列表
     * @return 数组。第一个元素时经文编号，作为标题；第二个元素是经文，作为内容。
     */
    protected String[] getTitleAndScripture(ScriptureService scriptureService, List<ScriptureNumberEntity> scriptureNumberList) {
        StringBuilder titleBuilder = new StringBuilder();
        StringBuilder scriptureBuilder = new StringBuilder();
        if (logger.isDebugEnabled()) {
            logger.debug("{}个经文编号实体对象", scriptureNumberList.size());
        }
        for (ScriptureNumberEntity scriptureNumberEntity : scriptureNumberList) {
            if (logger.isDebugEnabled()) {
                logger.debug(scriptureNumberEntity.toString());
            }
            boolean validateResult = scriptureService.validateNumber(scriptureNumberEntity);
            if (!validateResult) {
                throw new ScriptureServiceException("经文编号格式错误！");
            }
            titleBuilder.append(scriptureNumberEntity.getValue());
            ScriptureEntity scriptureEntity = scriptureService.getScriptureWithFormat(scriptureNumberEntity, SystemConfig.getString(Dict.ScriptureProperty.FORMAT1));
            if (logger.isDebugEnabled()) {
                logger.debug(scriptureEntity.toString());
            }
            scriptureBuilder.append("\t").append(scriptureEntity.getScripture()).append('\n');
        }
        scriptureBuilder.setLength(scriptureBuilder.length() - 1);
        char lastChar = scriptureBuilder.charAt(scriptureBuilder.length() - 1);
        if (lastChar == '，' || lastChar == '；') {
            scriptureBuilder.setCharAt(scriptureBuilder.length() - 1, '。');
        }

        return new String[] {titleBuilder.toString(), scriptureBuilder.toString()};
    }

    /**
     * 填充标题和经文
     * @param layoutName 幻灯片母版中版式的布局名称
     * @param scriptureService 经文服务对象
     * @param scriptureNumberList 经文编号对象列表
     */
    protected void fillTitleAndScripture(String layoutName, ScriptureService scriptureService, List<ScriptureNumberEntity> scriptureNumberList) throws PPTLayoutException {
        String[] titleAndScripture = getTitleAndScripture(scriptureService, scriptureNumberList);

        XMLSlideShow ppt = getPpt();
        XSLFSlideLayout layout = ppt.findLayout(layoutName);
        XSLFSlide slide = ppt.createSlide(layout);
        // 填充标题
        XSLFTextShape titlePlaceHolder = TextUtil.getPlaceholderSafely(slide, 0, getLayout(), "");
        XSLFTextRun titleTextRun = TextUtil.clearAndCreateTextRun(titlePlaceHolder);
        titleTextRun.setText(titleAndScripture[0]);
        TextUtil.setScriptureFontColor(titleTextRun, TextUtil.FontColor.RGB_FONT_COLOR_WHITE);
        double titleFontSize = SystemConfig.getUserConfigOrDefault(Dict.PPTProperty.GENERAL_TITLE_FONT_SIZE, DEFAULT_TITLE_FONT_SIZE);
        titleTextRun.setFontSize(titleFontSize);
        if (logger.isDebugEnabled()) {
            logger.debug("填充标题：" + titleAndScripture[0]);
        }

        // 填充经文
        XSLFTextShape contentPlaceHolder = TextUtil.getPlaceholderSafely(slide, 1, getLayout(), "");
        contentPlaceHolder.clearText();
        XSLFTextRun scriptureTextRun = TextUtil.clearAndCreateTextRun(contentPlaceHolder);
        TextUtil.setScriptureFontColor(scriptureTextRun, TextUtil.FontColor.RGB_FONT_COLOR_BLACK);
        scriptureTextRun.setText(titleAndScripture[1]);
        double scriptureFontSize = SystemConfig.getUserConfigOrDefault(Dict.PPTProperty.GENERAL_SCRIPTURE_FONT_SIZE, DEFAULT_SCRIPTURE_FONT_SIZE);
        scriptureTextRun.setFontSize(scriptureFontSize);
        List<XSLFTextParagraph> paragraphs = contentPlaceHolder.getTextParagraphs();
        for (XSLFTextParagraph paragraph : paragraphs) {
            useCustomLanguage(paragraph);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("填充经文：" + titleAndScripture[1]);
        }
    }

    /**
     * 使用自定义语言
     * @param paragraph 段落
     */
    protected void useCustomLanguage(XSLFTextParagraph paragraph) {
        CTTextParagraph xmlObject = paragraph.getXmlObject();
        CTTextParagraphProperties pPr = xmlObject.getPPr();
        if (pPr == null) {
            pPr = xmlObject.addNewPPr();
        }

        // 按中文习惯控制首尾字符
        pPr.setEaLnBrk(true);

        // 允许标点溢出边界
        pPr.setHangingPunct(true);

        CTTextCharacterProperties defRPr = pPr.getDefRPr();
        if (defRPr == null) {
            defRPr = pPr.addNewDefRPr();
        }
        // 设置语言
        String lang = SystemConfig.getString(Dict.PPTProperty.GENERAL_LANGUAGE);
        defRPr.setLang(lang);
        defRPr.setAltLang(lang);

        CTTextCharacterProperties endParaRPr = xmlObject.getEndParaRPr();
        if (endParaRPr == null) {
            endParaRPr = xmlObject.addNewEndParaRPr();
        }
        // 设置语言
        endParaRPr.setLang(lang);
        endParaRPr.setAltLang(lang);
    }
}
