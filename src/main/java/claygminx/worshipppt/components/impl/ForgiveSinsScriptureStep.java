package claygminx.worshipppt.components.impl;

import claygminx.worshipppt.common.config.SystemConfig;
import claygminx.worshipppt.common.entity.ScriptureNumberEntity;
import claygminx.worshipppt.components.ScriptureService;
import claygminx.worshipppt.exception.ScriptureNumberException;
import claygminx.worshipppt.exception.WorshipStepException;
import claygminx.worshipppt.util.ScriptureUtil;
import claygminx.worshipppt.common.Dict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.xslf.usermodel.*;

import java.util.List;

/**
 * 赦罪经文阶段
 */
public class ForgiveSinsScriptureStep extends AbstractWorshipStep {

    private final static Logger logger = LoggerFactory.getLogger(ForgiveSinsScriptureStep.class);

    private final String scriptureNumber;
    private final ScriptureService scriptureService;

    public ForgiveSinsScriptureStep(XMLSlideShow ppt, String layout, ScriptureService scriptureService, String scriptureNumber) {
        super(ppt, layout);
        this.scriptureService = scriptureService;
        this.scriptureNumber = scriptureNumber;
    }

    @Override
    public void execute() throws WorshipStepException {
        List<ScriptureNumberEntity> scriptureNumberList;
        try {
            scriptureNumberList = ScriptureUtil.parseNumbers(scriptureNumber);
        } catch (ScriptureNumberException e) {
            throw new WorshipStepException("解析赦罪经文编号 [" + scriptureNumber + "] 时出错！", e);
        }
        String[] titleAndScripture = getTitleAndScripture(scriptureService, scriptureNumberList);

        XMLSlideShow ppt = getPpt();
        XSLFSlideLayout layout = ppt.findLayout(getLayout());
        XSLFSlide slide = ppt.createSlide(layout);

        // 赦罪经文标题
        XSLFTextShape placeholder = slide.getPlaceholder(0);
        placeholder.clearText();
        placeholder.setText(titleAndScripture[0]);

        // 赦罪经文
        placeholder = slide.getPlaceholder(1);
        List<XSLFTextParagraph> paragraphs = placeholder.getTextParagraphs();
        // 遍历占位符中的所有文本段落
        boolean breakTag = false;
        for (XSLFTextParagraph paragraph : paragraphs) {
            List<XSLFTextRun> textRuns = paragraph.getTextRuns();
            // 遍历当前文本段落中的所有文本段
            for (XSLFTextRun textRun : textRuns) {
                String rawText = textRun.getRawText();
                logger.info("赦罪：读取到文本段" + rawText);
                if (rawText != null && rawText.contains("主领：")){
                    ScriptureUtil.setScriptureFontColor(textRun, Dict.PPTProperty.RGB_FONT_COLOR_RED);
                }
                if (rawText != null && rawText.contains(getCustomPlaceholder())) {
                    textRun.setText(rawText.replace(getCustomPlaceholder(), titleAndScripture[1]));
                    paragraph.setLineSpacing(SystemConfig.getDouble(Dict.PPTProperty.FORGIVE_SINS_SCRIPTURE_LINE_SPACING));
                    useCustomLanguage(paragraph);
                    breakTag = true;
                }
                if (rawText != null && rawText.contains("会众：")){
                    ScriptureUtil.setScriptureFontColor(textRun, Dict.PPTProperty.RGB_FONT_COLOR_BLUE);
                    if (breakTag) break;
                }
            }
        }

        logger.info("认罪经文幻灯片制作完成");
    }
}
