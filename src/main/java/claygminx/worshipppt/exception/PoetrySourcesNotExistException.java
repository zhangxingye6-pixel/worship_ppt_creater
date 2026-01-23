package claygminx.worshipppt.exception;

/**
 * 歌谱路径中没有对应的歌谱
 */
public class PoetrySourcesNotExistException extends Exception {

    public PoetrySourcesNotExistException(String message) {
        super(message);
    }

    public PoetrySourcesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
