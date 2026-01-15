package claygminx.worshipppt.common.entity.confession;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 信条查询参数实体
 */

@AllArgsConstructor
@NoArgsConstructor
public class ConfessionQueryRequestEntity {

    // 开始章
    private int startChapter;
    // 终止章
    private int endChapter;
    // 开始节
    private int startVerse;
    // 终止节
    private int endVerse;

    /**
     * 检查是否是范围查询
     * @return
     */
    private boolean isRange(){
        return true;
    }

    /**
     * 参数校验
     */
    private void validate(){

    }

    public int getEndChapter() {
        return endChapter;
    }

    public void setEndChapter(int endChapter) {
        this.endChapter = endChapter;
    }

    public int getEndVerse() {
        return endVerse;
    }

    public void setEndVerse(int endVerse) {
        this.endVerse = endVerse;
    }

    public int getStartChapter() {
        return startChapter;
    }

    public void setStartChapter(int startChapter) {
        this.startChapter = startChapter;
    }

    public int getStartVerse() {
        return startVerse;
    }

    public void setStartVerse(int startVerse) {
        this.startVerse = startVerse;
    }

    @Override
    public String toString() {
        return "ConfessionQueryRequestEntity{" +
                "endChapter=" + endChapter +
                ", startChapter=" + startChapter +
                ", startVerse=" + startVerse +
                ", endVerse=" + endVerse +
                '}';
    }
}
