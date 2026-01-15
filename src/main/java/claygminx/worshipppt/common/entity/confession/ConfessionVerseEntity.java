package claygminx.worshipppt.common.entity.confession;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 一节内容实体
 */
@AllArgsConstructor
@NoArgsConstructor
public class ConfessionVerseEntity {

    private String chapterName;
    private int chapter;
    private int verse;
    private String Content;

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public int getVerse() {
        return verse;
    }

    public void setVerse(int verse) {
        this.verse = verse;
    }

    @Override
    public String toString() {
        return "ConfessionVerseEntity{" +
                "chapter=" + chapter +
                ", chapterName='" + chapterName + '\'' +
                ", verse=" + verse +
                ", Content='" + Content + '\'' +
                '}';
    }
}
