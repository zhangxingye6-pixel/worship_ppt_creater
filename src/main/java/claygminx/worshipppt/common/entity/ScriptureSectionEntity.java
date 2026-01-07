package claygminx.worshipppt.common.entity;

import claygminx.worshipppt.common.ScriptureStatusEnum;

import java.util.List;

/**
 * 经文章节实体
 */
public class ScriptureSectionEntity {

    // 默认的经文节模式
    private ScriptureStatusEnum status = ScriptureStatusEnum.NOMAL;

    /**
     * 第几章
     */
    private Integer chapter;

    /**
     * 哪些节，若不指定，就使用整章
     */
    private List<Integer> verses;

    public Integer getChapter() {
        return chapter;
    }

    public void setChapter(Integer chapter) {
        this.chapter = chapter;
    }

    public List<Integer> getVerses() {
        return verses;
    }

    public void setVerses(List<Integer> verses) {
        this.verses = verses;
    }

    // 将经节模式设为默认
    public void setStatusNoaml() {
        this.status = ScriptureStatusEnum.NOMAL;
    }

    // 将经节模式设为结尾经节
    public void setStatusFromStartOfChapter() {
        this.status = ScriptureStatusEnum.FROM_START_OF_CHAPTER;
    }

    // 将经节模式设为开始经节
    public void setStatusToEndOfChapter() {
        this.status = ScriptureStatusEnum.TO_END_OF_CHAPTER;
    }

    // 获取当前的经节模式
    public ScriptureStatusEnum getStatus() {
        return status;
    }


    @Override
    public String toString() {
        return "ScriptureSectionEntity{" +
                "chapter=" + chapter +
                ", verses=" + verses +
                '}';
    }
}
