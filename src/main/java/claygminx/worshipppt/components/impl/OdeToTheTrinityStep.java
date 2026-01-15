package claygminx.worshipppt.components.impl;

import claygminx.worshipppt.util.TextUtil;
import org.apache.poi.xslf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 三一颂阶段
 */
public class OdeToTheTrinityStep extends AbstractWorshipStep {

    private final static Logger logger = LoggerFactory.getLogger(OdeToTheTrinityStep.class);

    private final String title;

    public OdeToTheTrinityStep(XMLSlideShow ppt, String layout, String title) {
        super(ppt, layout);
        this.title = title;
    }

    @Override
    public void execute() {
        XMLSlideShow ppt = getPpt();
        XSLFSlideLayout layout = ppt.findLayout(getLayout());
        XSLFSlide slide = ppt.createSlide(layout);

        XSLFTextShape placeholder = slide.getPlaceholder(0);
        XSLFTextRun textRun = TextUtil.clearAndCreateTextRun(placeholder);
        textRun.setText(" " + title);
        textRun.setFontFamily(AbstractWorshipStep.DEFAULT_FONT_FAMILY);
        textRun.setFontSize(AbstractWorshipStep.DEFAULT_TITLE_FONT_SIZE);
        textRun.setBold(true);
        TextUtil.setScriptureFontColor(textRun, TextUtil.FontColor.RGB_FONT_COLOR_WHITE);
        logger.info("三一颂幻灯片制作完成");
    }
}
