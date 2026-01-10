package claygminx.worshipppt.components.impl;

import claygminx.worshipppt.common.Dict;
import claygminx.worshipppt.common.config.SystemConfig;
import claygminx.worshipppt.util.TextUtil;
import org.apache.poi.xslf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 诗歌标题阶段
 */
public class PoetryTitleStep extends AbstractWorshipStep {

    private final static Logger logger = LoggerFactory.getLogger(PoetryTitleStep.class);

    private final String slideName;
    private final String poetryName;

    // 字体常量
    private final double DEFAULT_POETRY_COVER_FONT_SIZE  = 55.0;
    private final double DEFAULT_POETRY_TITLE_FONT_SIZE = 40.0;

    public PoetryTitleStep(XMLSlideShow ppt, String layout, String slideName, String poetryName) {
        super(ppt, layout);
        this.slideName = slideName;
        this.poetryName = poetryName;
    }

    @Override
    public void execute() {
        XMLSlideShow ppt = getPpt();
        XSLFSlideLayout layout = ppt.findLayout(getLayout());
        XSLFSlide slide = ppt.createSlide(layout);

        fillPlaceholder(slide, 0, slideName);
        fillPlaceholder(slide, 1, poetryName);

        logger.info("诗歌标题 - 幻灯片制作完成");
    }

    private void fillPlaceholder(XSLFSlide slide, int idx, String text) {
        XSLFTextShape placeholder = slide.getPlaceholder(idx);
        XSLFTextRun textRun = TextUtil.clearAndCreateTextRun(placeholder);
        textRun.setText(text.trim());
        TextUtil.setScriptureFontColor(textRun, TextUtil.FontColor.RGB_FONT_COLOR_BLACK);
        String fontStyle = SystemConfig.getUserConfigOrDefault(Dict.PPTProperty.POETRY_TITLE_FONT_FAMILT, DEFAULT_FONT_FAMILY);
        textRun.setFontFamily(fontStyle);
        if (idx == 1){  // 制作诗歌封面
            textRun.setFontSize(SystemConfig.getUserConfigOrDefault(Dict.PPTProperty.POETRY_TITLE_FONT_SIZE, DEFAULT_POETRY_COVER_FONT_SIZE));
        }else {     // 制作诗歌标题
            textRun.setFontSize(DEFAULT_POETRY_TITLE_FONT_SIZE);
        }


    }
}
