package claygminx.worshipppt.components.impl;

import claygminx.worshipppt.common.Dict;
import claygminx.worshipppt.common.entity.CoverEntity;
import claygminx.worshipppt.util.ScriptureUtil;
import org.apache.poi.xslf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 封面阶段
 */
public class CoverStep extends AbstractWorshipStep {

    private final static Logger logger = LoggerFactory.getLogger(CoverStep.class);

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
        XSLFTextRun worshipDataTextRun = ScriptureUtil.clearAndCreateTextRun(placeholder);
        worshipDataTextRun.setText(coverEntity.getWorshipDate().trim());
        ScriptureUtil.setScriptureFontColor(worshipDataTextRun, Dict.PPTProperty.RGB_FONT_COLOR_BLUE);

        if (coverEntity.getChurchName() != null) {
            placeholder = coverSlide.getPlaceholder(1);
            XSLFTextRun churchNameTextRun = ScriptureUtil.clearAndCreateTextRun(placeholder);
            churchNameTextRun.setText(coverEntity.getChurchName().trim());
            ScriptureUtil.setScriptureFontColor(churchNameTextRun, Dict.PPTProperty.RGB_FONT_COLOR_BLACK);
        }
        logger.info("封面幻灯片制作完成");
    }
}
