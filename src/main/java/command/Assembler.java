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
package command;

import apc.ArchiveExtractor;
import apc.FileManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import library.Constants;
import net.lingala.zip4j.exception.ZipException;

/**
 * The assembler class contains all functions required to create the Android
 * Studio project using the output of the decompiler class and the Android
 * Studio ZIP archive from the
 * <code>Constants.ANDROIDPROJECT_LIBRARY_FOLDER</code>
 *
 * @author Max 'Libra' Kersten
 */
public class Assembler {

    /**
     * The location to place the filled Android Studio project
     */
    private final File outputLocation;

    /**
     * The file manager that is used to perform actions on files
     */
    private final FileManager fileManager;

    /**
     * The assembler class contains all functions required to create the Android
     * Studio project using the output of the decompiler class and the Android
     * Studio ZIP archive from the
     * <code>Constants.ANDROIDPROJECT_LIBRARY_FOLDER</code>
     *
     * @param outputLocation the location where the filled Android Studio
     * project should be placed
     */
    public Assembler(File outputLocation) {
        this.outputLocation = outputLocation;
        fileManager = new FileManager();
    }

    /**
     * The public function which contains all function calls to the private
     * methods which assemble the Android Studio project
     *
     * @throws IOException if something goes wrong with the file handling
     * @throws ZipException if an archive cannot be extracted
     * @throws FileNotFoundException if a file cannot be found (such as the
     * AndroidManifest file)
     */
    public void assemble() throws IOException, ZipException, FileNotFoundException {
        //Copy and extract the template zip
        prepareTemplateProject();
        //Copy the decoded and decompiled files to the template project
        copyManifest();
        copyResources();
        copyJavaCode();
        copyNativeLibraries();
        copySmaliFiles();
        copyAssets();
        //Copy the template project from the temporary location to the desired output location
        copyTemplateToOutputFolder();
        //TODO ZIP template project to the desired output location (if the zip flag is used)
        //Remove temporary files
        cleanup();
    }

    /**
     * Extracts the template archive file into the temporary folder (which is
     * located in ./Library/temp, in which the working directory is the one in
     * which the APK resides)
     *
     * @throws ZipException if an archive cannot be extracted
     */
    private void prepareTemplateProject() throws ZipException, IOException {
        try {
            //Read file
            System.out.println("[+]Reading Android Studio template project");
            //Create temp folder
            File tempFile = new File(Constants.TEMP_LIBRARY_FOLDER);
            tempFile.mkdir();
            System.out.println("[+]Writing template to a temporary folder (" + Constants.TEMP_TEMPLATE_FOLDER + ")");
            String androidStudioZipFileName = "/ap.zip";
            File targetFile = new File(Constants.ANDROIDPROJECT_LIBRARY_FOLDER + androidStudioZipFileName);
            File destinationFile = new File(Constants.TEMP_TEMPLATE_FOLDER);
            //Extract file
            System.out.println("[+]Extracting Android Studio template project");
            ArchiveExtractor.extractArchive(targetFile.getAbsolutePath(), destinationFile.getAbsolutePath());
            System.out.println("[+]Template extraction finished");
        } catch (ZipException ex) {
            throw new ZipException("An error occurred when trying to extract the template project. Reinstall AndroidProjectCreator using the \"-install\" flag and try again.");
        }
    }

    /**
     * Copies the resources from the APK into the template project in the
     * temporary folder
     *
     * @throws IOException if the resources cannot be copied
     */
    private void copyResources() throws IOException {
        try {
            //Get the teporary resource folder location
            File tempResourcesFolder = new File(Constants.TEMP_RESOURCES_FOLDER);
            //Copy the template resource folder
            File templateResourceFolder = new File(Constants.TEMP_TEMPLATE_RESOURCES_FOLDER);
            System.out.println("[+]Copying resources from the APK to the Android Studio project");
            //Copy the files
            fileManager.copyFolder(tempResourcesFolder, templateResourceFolder);
            System.out.println("[+]Copying resources complete");
        } catch (IOException ex) {
            throw new IOException("Something went wrong when trying to copy the resources");
        }
    }

    /**
     * Copy the AndroidManifest.xml from the temporary folder to the template
     * project
     *
     * @throws FileNotFoundException if the manifest file cannot be found
     * @throws IOException if the manifest file cannot be copied
     */
    private void copyManifest() throws FileNotFoundException, IOException {
        try {
            //The manifest
            File tempManifest = new File(Constants.TEMP_MANIFEST_FILE);
            //Create file located in the Android Project
            File templateManifest = new File(Constants.TEMP_TEMPLATE_MANIFEST_FILE);
            //Copy the xml file
            System.out.println("[+]Copying the AndroidManifest.xml from the APK to the Android Studio project");
            templateManifest.createNewFile();
            InputStream input = null;
            OutputStream output = null;
            input = new FileInputStream(tempManifest);
            output = new FileOutputStream(templateManifest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
            input.close();
            output.close();
            System.out.println("[+]AndroidManifest.xml successfully copied");
        } catch (FileNotFoundException ex) {
            throw new FileNotFoundException("The manifest could not be found!");
        } catch (IOException ex) {
            throw new IOException("Something went wrong when copying the AndroidManifest.xml file to the template project.");
        }
    }

    /**
     * Copy the decompiled Java code from the output location of the decompiler
     * to the template project
     *
     * @throws IOException if something goes wrong during the copying of the
     * decompiled Java code
     */
    private void copyJavaCode() throws IOException {
        try {
            System.out.println("[+]Copying the decompiled Java source code from the APK to the Android Studio project");
            File sourceLocation = new File(Constants.TEMP_SOURCES_FOLDER);
            File sourceDest = new File(Constants.TEMP_TEMPLATE_SOURCE);
            fileManager.copyFolder(sourceLocation, sourceDest);
            System.out.println("[+]Source code successfully copied");
        } catch (IOException ex) {
            throw new IOException("An error occurred when the decompiled Java code was copied to the template project!");
        }
    }

    /**
     * Copies the native libraries from the APK to the template project. APKTool
     * only creates the lib folder, if there are libraries to extract from the
     * APK, hence the check. If it is not found, it is skipped
     *
     * @throws IOException when something goes wrong during the copying of the
     * libraries
     */
    private void copyNativeLibraries() throws IOException {
        try {
            System.out.println("[+]Looking for native libraries");
            File apkLibraryFolder = new File(Constants.TEMP_LIB_FOLDER);
            if (apkLibraryFolder.exists() && apkLibraryFolder.isDirectory()) {
                System.out.println("[+]Native libraries found!");
                System.out.println("[+]Copying native lbraries to the template project");
                File templateLibraryFolder = new File(Constants.TEMP_TEMPLATE_LIBS_FOLDER);
                templateLibraryFolder.mkdir();
                fileManager.copyFolder(apkLibraryFolder, templateLibraryFolder);
                System.out.println("[+]Native libraries succesfully copied!");
                return;
            }
            System.out.println("[+]No native libraries found, skipping this step.");
        } catch (IOException e) {
            throw new IOException("Something went wrong when copying the native libraries from the APK to the template project.");
        }

    }

    /**
     * Copy the SMALI files into the template project's resources folder
     *
     * @throws IOException when the copying fails
     */
    private void copySmaliFiles() throws IOException {
        try {
            System.out.println("[+]Looking for SMALI files");
            File apkSmaliFolder = new File(Constants.TEMP_SMALI_FOLDER);
            if (apkSmaliFolder.exists() && apkSmaliFolder.isDirectory()) {
                System.out.println("[+]SMALI files found!");
                System.out.println("[+]Copying the SMALI files to the template project");
                File templateSmaliFolder = new File(Constants.TEMP_TEMPLATE_SMALI_FOLDER);
                templateSmaliFolder.mkdir();
                fileManager.copyFolder(apkSmaliFolder, templateSmaliFolder);
                System.out.println("[+]SMALI files succesfully copied!");
                return;
            }
            System.out.println("[+]No SMALI files found, skipping this step.");
        } catch (IOException e) {
            throw new IOException("Something went wrong when copying the SMALI files from the APK to the template project.");
        }
    }

    /**
     * Copies assets from the APK to the resource folder of the template project
     *
     * @throws IOException
     */
    private void copyAssets() throws IOException {
        try {
            System.out.println("[+]Looking for assets");
            File apkAssetFolder = new File(Constants.TEMP_ASSET_FOLDER);
            if (apkAssetFolder.exists() && apkAssetFolder.isDirectory()) {
                System.out.println("[+]Assets found!");
                System.out.println("[+]Copying assets to the template project");
                File templateAssetFolder = new File(Constants.TEMP_TEMPLATE_ASSET_FOLDER);
                templateAssetFolder.mkdir();
                fileManager.copyFolder(apkAssetFolder, templateAssetFolder);
                System.out.println("[+]Assets succesfully copied!");
                return;
            }
            System.out.println("[+]No assets found, skipping this step.");
        } catch (IOException e) {
            throw new IOException("Something went wrong when copying assets from the APK to the template project.");
        }
    }

    /**
     * Copies the template project to the desired output location
     *
     * @throws IOException if there is an error during the copying of the
     * template project to the desired output location
     */
    private void copyTemplateToOutputFolder() throws IOException {
        try {
            System.out.println("[+]Copying temp folder to output folder");
            File tempProject = new File(Constants.TEMP_TEMPLATE_FOLDER);
            //Make sure the output location (and its parents) exists
            outputLocation.mkdirs();
            fileManager.copyFolder(tempProject, outputLocation);
            System.out.println("[+]Output folder succesfully populated!");
        } catch (IOException ex) {
            throw new IOException("Unable to copy the temporary project (\"" + new File(Constants.TEMP_TEMPLATE_FOLDER).getAbsolutePath() + "\") to the desired output location (\"" + outputLocation.getAbsolutePath() + "\").");
        }
    }

    /**
     * Removes the temporary folder within the library's temporary folder
     *
     * @throws IOException if something goes wrong during the deletion of the
     * file(s) and/or folder(s)
     */
    private void cleanup() throws IOException {
        try {
            System.out.println("[+]Cleaning the temporary files folder");
            fileManager.delete(new File(Constants.TEMP_LIBRARY_FOLDER));
            System.out.println("[+]Cleanup complete");
        } catch (IOException ex) {
            throw new IOException("An error occurred during the removal of the temporary files");
        }
    }
}
