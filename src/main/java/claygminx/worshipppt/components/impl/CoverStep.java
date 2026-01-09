package claygminx.worshipppt.components.impl;

import claygminx.worshipppt.common.Dict;
import claygminx.worshipppt.common.entity.CoverEntity;
import claygminx.worshipppt.util.ScriptureUtil;
import claygminx.worshipppt.util.TextUtil;
import org.apache.poi.xslf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 封面阶段
 */
public class CoverStep extends AbstractWorshipStep {

    private final static Logger logger = LoggerFactory.getLogger(CoverStep.class);

    private final static double DEFAULT_FONT_SIZE = 36.0;

    private final CoverEntity coverEntity;

    public CoverStep(XMLSlideShow ppt, String layout, CoverEntity coverEntity) {
        super(ppt, layout);
        this.coverEntity = coverEntity;
    }

    @Override
    public void execute() {
        XMLSlideShow ppt = getPpt();
        XSLFSlideLayout coverLayout = ppt.findLayout(getLayout());
        XSLFSlide coverSlide = ppt.createSlide(coverLayout);

        // 设置敬拜日期文本和格式
        XSLFTextShape placeholder = coverSlide.getPlaceholder(0);
        XSLFTextRun worshipDataTextRun = TextUtil.clearAndCreateTextRun(placeholder);
        worshipDataTextRun.setText(coverEntity.getWorshipDate().trim());
        worshipDataTextRun.setFontSize(DEFAULT_FONT_SIZE);
        TextUtil.setScriptureFontColor(worshipDataTextRun,  TextUtil.FontColor.RGB_FONT_COLOR_BLUE);


        if (coverEntity.getChurchName() != null) {
            placeholder = coverSlide.getPlaceholder(1);
            XSLFTextRun churchNameTextRun = TextUtil.clearAndCreateTextRun(placeholder);
            churchNameTextRun.setText(coverEntity.getChurchName().trim());
            TextUtil.setScriptureFontColor(churchNameTextRun, TextUtil.FontColor.RGB_FONT_COLOR_BLACK);
        }
        logger.info("封面幻灯片制作完成");
    }
}
