package claygminx.worshipppt.common.entity.confession;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 信条查询结果封装对象
 */
@AllArgsConstructor
@NoArgsConstructor
public class ConfessionQueryResultEntity {

    private List<ConfessionContentEntity> confessionContents;

    public List<ConfessionContentEntity> getConfessionContents() {
        return confessionContents;
    }

    public void setConfessionContents(List<ConfessionContentEntity> confessionContents) {
        this.confessionContents = confessionContents;
    }

    @Override
    public String toString() {
        return "ConfessionQueryResultEntity{" +
                "confessionContents=" + confessionContents +
                '}';
    }
}
