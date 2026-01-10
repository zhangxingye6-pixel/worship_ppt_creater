package claygminx.worshipppt.util;

import claygminx.worshipppt.common.Dict;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

/**
 * 文本类工具
 * 为了逻辑清晰，各个部分的默认值存放在各自的常量中
 */
public class TextUtil {
    private final static Logger logger = LoggerFactory.getLogger(ScriptureUtil.class);

    /**
     * 字体颜色RGBs
     */
    public static class FontColor{
        public final static int[] RGB_FONT_COLOR_BLUE = {45, 71, 209};
        public final static int[] RGB_FONT_COLOR_RED = {239, 73, 15};
        public final static int[] RGB_FONT_COLOR_BLACK = {64, 64, 64};

    }

    // 禁止外部实例化
    private TextUtil() {
    }

    /**
     * 设置文本段的经文字体颜色
     *
     * @param textRun 文本段对象
     * @param rgbs    RGB颜色数组
     * @return
     */
    public static void setScriptureFontColor(XSLFTextRun textRun, int[] rgbs) {
        // 参数校验
        if (textRun == null) {
            logger.info("未找到指定文本框，经文字体颜色设置失败");
            return;
        }
        textRun.setFontColor(new Color(rgbs[0], rgbs[1], rgbs[2]));
        logger.info("经文字体颜色设置成功");
    }

    /**
     * 清除占位符，并通过占位符创建单独的文字段
     *
     * @param placeholder
     * @return 创建的textRun对象
     */
    public static XSLFTextRun clearAndCreateTextRun(XSLFTextShape placeholder) {
        placeholder.clearText();
        return placeholder.addNewTextParagraph().addNewTextRun();
    }

    /**
     * 将厘米长度值转换为磅
     * @param centimetre 厘米长度
     * @return 磅
     */
    public static double convertToPoints(double centimetre) {
        double points = centimetre / 0.035275;
        if (logger.isDebugEnabled()) {
            logger.debug("{}cm => {} points", centimetre, points);
        }
        return points;
    }

}
