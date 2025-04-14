/*
 * Copyright (C) 2018 Max 'Libra' Kersten
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package apc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * Contains all functions which handle files, such as removing and copying files
 *
 * @author Max 'Libra' Kersten
 */
public class FileManager {

    /**
     * Works on both ZIP and APK archives
     *
     * @param source ZIP or APK file location
     * @param destination place to extract all files to
     * @throws ZipException if the file is not a ZIP archive
     * @throws IOException if the source file cannot be found
     */
    public void extractArchive(String source, String destination) throws ZipException, IOException {
        //Checke if the source folder exists
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

    /**
     * Deletes a folder, including all sub directories.
     *
     * Code taken from Erickson (from
     * "https://stackoverflow.com/questions/779519/delete-directories-recursively-in-java").
     * Code altered by Max 'Libra' Kersten.
     *
     * @param file the file to delete
     * @throws IOException the exception when a file could not be deleted
     */
    public void delete(File file) throws IOException {
        //Check if the file does not exist
        if (!file.exists()) {
            //The file that should be deleted, does not exist
            return;
        }
        if (file.isDirectory()) {
            for (File currentFile : file.listFiles()) {
                delete(currentFile);
            }
        }
        try {
            Files.delete(Paths.get(file.getAbsolutePath()));
        } catch (Exception e) {
            throw new IOException("Failed to delete file: " + file);
        }
    }

    /**
     * Removes all files and subfolders from the provided folder, excluding the
     * ".git" folder.
     *
     * Code taken from Erickson (from
     * "https://stackoverflow.com/questions/779519/delete-directories-recursively-in-java").
     * Code altered by Max 'Libra' Kersten.
     *
     * @param folder the folder to remove files from
     * @throws IOException if an error occurs upon deleting the file
     */
    public void emptyFolder(File folder) throws IOException {
        //Check if the file does not exist
        if (!folder.exists()) {
            //A newly created folder is also an empty folder
            folder.mkdirs();
            return;
        }

        if (folder.isDirectory()) {
            for (File currentFile : folder.listFiles()) {
                //Exclude the .git folder to avoid errors on Windows
                if (!currentFile.getName().endsWith(".git")) {
                    delete(currentFile);
                }
            }
        }
    }

    /**
     * Copies a folder (including all the sub folders) to the given destination.
     *
     * Originally taken from pwipo
     * (https://stackoverflow.com/questions/29076439/java-8-copy-directory-recursively/34254130)
     *
     * @param src the file to be copied
     * @param dest the place to copy the <code>src</code> file to
     * @throws IOException if the destination is not a folder or if the copying
     * of the files failed
     */
    public void copyFolder(File src, File dest) throws IOException {
        if (src == null || dest == null) {
            return;
        }
        if (!src.isDirectory()) {
            return;
        }
        if (dest.exists()) {
            if (!dest.isDirectory()) {
                throw new IOException("The destination is not a folder: " + dest.getAbsolutePath());
            }
        } else {
            dest.mkdirs();
        }

        if (src.listFiles() == null || src.listFiles().length == 0) {
            return;
        }

        for (File file : src.listFiles()) {
            File fileDest = new File(dest, file.getName());
            if (file.isDirectory()) {
                copyFolder(file, fileDest);
            } else {
                if (fileDest.exists()) {
                    continue;
                }
                try {
                    Files.copy(file.toPath(), fileDest.toPath());
                } catch (IOException ex) {
                    throw new IOException("Something went wrong when trying to copy the file \"" + file.getAbsolutePath() + "\"! It might not exist or you might not have the correct permissions to write a file to the destination (\"" + fileDest.getAbsolutePath() + "\").");
                }
            }
        }
    }
}
