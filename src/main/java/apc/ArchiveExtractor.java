package apc;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.IOException;

public class ArchiveExtractor {
    /**
     * Works on both ZIP and APK archives
     *
     * @param source ZIP or APK file location
     * @param destination place to extract all files to
     * @throws ZipException if the file is not a ZIP archive
     * @throws IOException if the source file cannot be found
     */
    public static void extractArchive(String source,String destination) throws ZipException, IOException{
        //Check if the source folder exists
        if (!new File(source).exists()) {
            throw new IOException("The source file does not exist");
        }
        try {
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            //A message is already provided
            throw new ZipException(e.getMessage());
        }
    }

}
