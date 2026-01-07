package claygminx.worshipppt.common;

/**
 * ScriptureSectionEntity使用到的状态枚举
 * <p>
 *     <ol>
 *         <li>枚举类表示节的状态</li>
 *         <li>NOMAL表示是一个正常的节</li>
 *         <li>FROM_START_OF_CHAPTER表示包括本节在内，从章开始所有的节</li>
 *         <li>END_OF_SECTION表示包括本节在内，到章结束所有的节</li>
 *     </ol>
 * </p>
 */
public enum ScriptureStatusEnum {
    NOMAL,
    FROM_START_OF_CHAPTER,
    TO_END_OF_CHAPTER
}
