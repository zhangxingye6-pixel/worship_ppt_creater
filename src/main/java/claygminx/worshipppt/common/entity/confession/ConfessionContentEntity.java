package claygminx.worshipppt.common.entity.confession;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 信条内容实体(单章节)
 */
@AllArgsConstructor
@NoArgsConstructor
public class ConfessionContentEntity {
    // 章名称
    private String chapterName;
    // 章号
    private int chapter;
    // 节号
    private List<Integer> verses;
    // 信条内容
    private List<String> contents;

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    public List<Integer> getVerses() {
        return verses;
    }

    public void setVerses(List<Integer> verses) {
        this.verses = verses;
    }

    @Override
    public String toString() {
        return "ConfessionContentEntity{" +
                "chapter=" + chapter +
                ", chapterName='" + chapterName + '\'' +
                ", verses=" + verses +
                ", contents=" + contents +
                '}';
    }
}
