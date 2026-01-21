package claygminx.worshipppt.components;

import claygminx.worshipppt.common.entity.GithubReleaseEntity;

/**
 * 升级服务
 */
public interface UpgradeService {

    /**
     * 检查新的发行版
     *
     * @return 版本提示信息
     */
    GithubReleaseEntity checkNewRelease();

}
