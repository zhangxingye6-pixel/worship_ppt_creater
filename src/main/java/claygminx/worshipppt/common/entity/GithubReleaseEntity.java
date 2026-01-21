package claygminx.worshipppt.common.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class GithubReleaseEntity {

    private Long id;
    // 版本号
    private String tag_name;
    // 有信息的版本号
    private String name;
    // 版本描述
    private String body;
    // 发行时间
    private Date created_at;
    // 下载地址
    private String html_url;

}
