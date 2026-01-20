package claygminx.worshipppt.components.impl;

import claygminx.worshipppt.common.entity.PreachEntity;
import claygminx.worshipppt.exception.PPTLayoutException;
import claygminx.worshipppt.util.TextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

/**
 * 证道阶段
 */
public class PreachStep extends AbstractWorshipStep {

    private final static Logger logger = LoggerFactory.getLogger(PreachStep.class);

    private final PreachEntity preachEntity;

    public PreachStep(XMLSlideShow ppt, String layout, PreachEntity preachEntity) {
        super(ppt, layout);
        this.preachEntity = preachEntity;
    }

    @Override
    public void execute() throws PPTLayoutException {
        XMLSlideShow ppt = getPpt();
        XSLFSlideLayout layout = ppt.findLayout(getLayout());
        XSLFSlide slide = ppt.createSlide(layout);

        XSLFTextShape placeholder = TextUtil.getPlaceholderSafely(slide, 0, getLayout(), "");
        String text = placeholder.getText();
        placeholder.setText(text.replace(getCustomPlaceholder(), preachEntity.getTitle()));

        logger.info("证道幻灯片制作完成");
    }
}
