package claygminx.worshipppt.components.impl;

import claygminx.worshipppt.common.DeclarationThemeEnum;
import claygminx.worshipppt.common.Dict;
import claygminx.worshipppt.common.config.SystemConfig;
import claygminx.worshipppt.common.entity.DeclarationEntity;
import claygminx.worshipppt.common.entity.confession.ConfessionQueryRequestEntity;
import claygminx.worshipppt.components.ConfessionService;
import claygminx.worshipppt.exception.PPTLayoutException;
import claygminx.worshipppt.exception.ScriptureNumberException;
import claygminx.worshipppt.util.ConfessionUtil;
import claygminx.worshipppt.util.TextUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xslf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 宣信标题阶段 TODO可以使用上下文优化
 */
public class DeclarationTitleStep extends AbstractWorshipStep {

    private final static Logger logger = LoggerFactory.getLogger(DeclarationTitleStep.class);

    private final DeclarationEntity declarationEntity;
    private final ConfessionService confessionService;

    public DeclarationTitleStep(XMLSlideShow ppt, String layout, ConfessionService confessionService, DeclarationEntity declarationEntity) {
        super(ppt, layout);
        this.confessionService = confessionService;
        this.declarationEntity = declarationEntity;
    }

    @Override
    public void execute() throws ScriptureNumberException, PPTLayoutException {
        // step1: 数据准备
        String title = declarationEntity.getTitle();
        int firstNumber = ConfessionUtil.getIndexOfFirstNumber(title);
        String chapterNumber = title.substring(firstNumber);
        List<ConfessionQueryRequestEntity> requestEntities = ConfessionUtil.parseChapterNumber(chapterNumber);
        if (CollectionUtils.isEmpty(requestEntities)){
            throw new ScriptureNumberException("宣信封面：信条的章节编号输入有误，请检查后重新制作");
        }
        int chapter = requestEntities.get(0).getStartChapter();
        String chapterName = confessionService.getChapterNameWithChapter(chapter);
        if (StringUtils.isEmpty(chapterName)) {
            throw new ScriptureNumberException("宣信封面：章名称查询错误，请检查输入的章节");
        }

        // step2: 制作ppt
        XMLSlideShow ppt = getPpt();
        XSLFSlideLayout layout = ppt.findLayout(getLayout());
        XSLFSlide slide = ppt.createSlide(layout);

        XSLFTextShape placeholder = TextUtil.getPlaceholderSafely(slide, 0, getLayout(), "标题部分");
        List<XSLFTextParagraph> paragraphs = placeholder.getTextParagraphs();
        int index = 0;
        for (XSLFTextParagraph paragraph : paragraphs) {
            List<XSLFTextRun> textRuns = paragraph.getTextRuns();
            for (XSLFTextRun textRun : textRuns) {
                if (textRun.getRawText().contains(getCustomPlaceholder())) {
                    if (index == 0) {
                        textRun.setText(textRun.getRawText().replace(getCustomPlaceholder(), DeclarationThemeEnum.WESTMINSTER_CONFESSION.getDesc()));
                        textRun.setFontFamily(AbstractWorshipStep.DEFAULT_FONT_FAMILY);
                        textRun.setFontSize(SystemConfig.getUserConfigOrDefault(Dict.PPTProperty.GENERAL_TITLE_FONT_SIZE, AbstractWorshipStep.DEFAULT_TITLE_FONT_SIZE));
                        TextUtil.setScriptureFontColor(textRun, TextUtil.FontColor.RGB_FONT_COLOR_BLACK);
                        index++;
                        logger.debug("填充了宣信主题：" + DeclarationThemeEnum.WESTMINSTER_CONFESSION.getDesc());
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        String coverTitle = stringBuilder.append("第").append(chapter).append("章 ").append("《").append(chapterName).append("》").toString();
                        textRun.setText(textRun.getRawText().replace(getCustomPlaceholder(), coverTitle));
                        textRun.setFontSize(SystemConfig.getUserConfigOrDefault(Dict.PPTProperty.GENERAL_STEP_COVER_FONT_SIZE, AbstractWorshipStep.DEFAULT_STEP_COVER_FONT_SIZE));
                        textRun.setFontFamily(AbstractWorshipStep.DEFAULT_FONT_FAMILY);
                        TextUtil.setScriptureFontColor(textRun, TextUtil.FontColor.RGB_FONT_COLOR_BLACK);
                        logger.debug("填充了章名称：" + chapterName);
                    }
                }
            }
        }
        logger.info("宣信标题幻灯片制作完成");
    }
}
