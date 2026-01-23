package claygminx.worshipppt.util;

import claygminx.worshipppt.common.Dict;
import claygminx.worshipppt.common.config.SystemConfig;
import claygminx.worshipppt.exception.PoetrySourcesNotExistException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

/**
 * 乐谱路径的校验，UI层和业务层高度吻合，所以使用工具类抽出相同的逻辑
 */
@Slf4j
public class PoetryFileLocator {

    private static final String SUB_DIRECTORY_NAME = SystemConfig.getString(Dict.PPTProperty.POETRY_SUB_DICTIONARY);
    private static final String POETRY_EXTENSION = SystemConfig.getString(Dict.PPTProperty.POETRY_EXTENSION);

    public static File[] getPoetryFiles(File directory) throws PoetrySourcesNotExistException {

        File[] poetrySources = getFromMainDirectory(directory);

        if (poetrySources == null) {
            throw new PoetrySourcesNotExistException("诗歌谱路径不存在");
        }

        if (poetrySources.length == 0){
            poetrySources = getFromSubDirectory(directory);
        }

        if (poetrySources == null ||poetrySources.length == 0){
            throw new PoetrySourcesNotExistException(directory.getAbsolutePath() + "及子目录" + SUB_DIRECTORY_NAME + "中不存在" + POETRY_EXTENSION + "乐谱");
        }

        return poetrySources;
    }

    /**
     * 在指定目录下一级子目录\SUB_directory_NAME中查找后缀为POETRY_EXTENSION的文件；
     * @param directory
     * @return
     */
    private static File[] getFromSubDirectory(File directory) throws PoetrySourcesNotExistException {
        if (directory == null){
            throw new PoetrySourcesNotExistException("诗歌谱路径不存在");
        }
        File[] files = directory.listFiles();
        if (files == null || files.length == 0){
            throw new PoetrySourcesNotExistException(directory.getAbsolutePath() + " - 目录是空的");
        }
        File[] subFiles = directory.listFiles(innerFile -> {
            boolean isDirectory = innerFile.isDirectory();
            boolean isTargetName = innerFile.getName().equalsIgnoreCase(SUB_DIRECTORY_NAME);
            return isDirectory && isTargetName;
        });
        // 子目录校验
        if (subFiles == null || subFiles.length == 0){
            throw new PoetrySourcesNotExistException(directory.getAbsolutePath() + "\\" + SUB_DIRECTORY_NAME + " - 目录不存在或访问受限");
        }
        File targetSubDir = subFiles[0];
        File[] targetFiles = targetSubDir.listFiles(innerFile -> {
            boolean isFile = innerFile.isFile();
            boolean endsWith = innerFile.getName().endsWith(SystemConfig.getString(Dict.PPTProperty.POETRY_EXTENSION));
            return isFile && endsWith;
        });

        if (targetFiles == null || targetFiles.length == 0){
            throw new PoetrySourcesNotExistException(directory.getAbsolutePath() + "\\" + SUB_DIRECTORY_NAME + " - 目录中未找到" + POETRY_EXTENSION + "文件");
        }

        for (File targetFile : targetFiles) {
            logger.info("发现" + POETRY_EXTENSION + "文件：" + targetFile.getAbsolutePath());
        }
        return targetFiles;


    }

    /**
     * 在指定的目录中查找后缀为POETRY_EXTENSION的文件
     * @param directory
     * @return
     * @throws PoetrySourcesNotExistException
     */
    private static File[] getFromMainDirectory(File directory) throws PoetrySourcesNotExistException {
        if (directory == null){
            throw new PoetrySourcesNotExistException("诗歌谱路径不存在");
        }
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            throw new PoetrySourcesNotExistException(directory.getAbsolutePath() + " - 目录是空的");
        }
        return Arrays.stream(files).filter(file -> {
            boolean isFile = file.isFile();
            boolean endsWith = file.getName().endsWith(SystemConfig.getString(Dict.PPTProperty.POETRY_EXTENSION));
            return isFile && endsWith;
        }).toArray(File[]::new);

    }
}
