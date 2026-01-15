package claygminx.worshipppt.common;

/**
 * 宣信主题，用与切换在DeclarationContentStep中excute方法的模式，预留了扩展
 */
public enum DeclarationThemeEnum {
    WESTMINSTER_CONFESSION("威斯敏斯特公认信条");

    private final String desc;

    public String getDesc() {
        return desc;
    }

    private DeclarationThemeEnum(String desc) {
        this.desc = desc;
    }
}
