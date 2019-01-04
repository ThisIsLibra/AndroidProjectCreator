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

import apc.RepositoryManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import library.Constants;
import library.OperatingSystemDetector;
import library.Repositories;
import library.Tools;

/**
 * Handles the installation of all tools
 *
 * @author Max 'Libra' Kersten
 */
public class Installer {

    /**
     * Clones all the git repositories in a sub folder of
     * <code>Constants.getLibraryFolderName()</code> (named "repos") in the
     * current directory. Then, all projects are built to ensure the latest
     * version is used. The outcome of the builds is either a ZIP or a JAR file.
     * A ZIP file will first be extracted. The tools are then copied into the
     * <code>Constants.getLibraryFolderName()</code> folder within their own
     * subdirectory.
     */
    public void install() throws IOException, Exception {
        System.out.println("[+]Starting the installation");
        RepositoryManager repositoryManager = new RepositoryManager();
        //Makes sure the library folder is empty. If it is not, it is deleted and recreated
        setupLibrary(repositoryManager);
        System.out.println("[+]Starting cloning the repositories");
        repositoryManager.cloneRepositories(Repositories.getAll());
        System.out.println("[+]Cloning finished");
        System.out.println("[+]Starting to build all repositories");
        repositoryManager.buildRepositories(Tools.getTools());
        System.out.println("[+]All projects have been build");
        System.out.println("[+]Starting to extract the builds to the library");
        repositoryManager.extractBuilds(Tools.getTools());
        System.out.println("[+]Extraction complete");
        removeRepositoryFolder(repositoryManager);
        System.out.println("[+]Verifying the installation directory");
        verifyInstallation();
        System.out.println("[+]Verification succesful!");
        System.out.println("[+]Installation complete!");
    }

    /**
     * Removes the library folder if it exists and then creates it. On Windows,
     * the deletion of the Git repository folders is not possible, which is why
     * an exception is thrown. The user should manually remove the folder before
     * installing them again
     *
     * @param repositoryManager the repository manager which handles the
     * repositories
     * @throws IOException if something goes wrong with the file handling
     */
    private void setupLibrary(RepositoryManager repositoryManager) throws IOException {
        if (OperatingSystemDetector.isWindows()) {
            File library = new File(Constants.LIBRARY_FOLDER);
            if (library.exists()) {
                throw new IOException("The library folder (" + library.getAbsolutePath() + " cannot programmatically be removed on a Windows system, this might change in future updates.For now, please remove it manually.");
            }
        } else {
            System.out.println("[+]Emptying the library folder");
            repositoryManager.setupLibrary();
            System.out.println("[+]Library folder emptied!");
        }
    }

    /**
     * Removes the repository folder to save disk space and avoid unused files
     * cluttering the library. File deletion is not working in the Git folders
     * on Windows, hence the warning that is displayed. It is not required to
     * delete the repositories after the building process, so the installation
     * process can continue.
     *
     * @param repositoryManager the repository manager which handles the
     * repositories
     * @throws IOException if something goes wrong with the file handling
     */
    private void removeRepositoryFolder(RepositoryManager repositoryManager) throws IOException {
        System.out.println("[+]Cleaning up temporary installation files");
        if (OperatingSystemDetector.isWindows()) {
            File repositoryFolder = new File(Constants.REPOSITORY_FOLDER);
            if (repositoryFolder.exists()) {
                System.out.println("[+]The repository folder (" + repositoryFolder.getAbsolutePath() + " cannot programmatically be removed on a Windows system, this might change in future updates.For now, please remove it manually.");
            }
        } else {
            repositoryManager.removeRepositoryFolder();
        }
        System.out.println("[+]Cleanup complete");
    }

    /**
     * Verify if all required folders are present (every enum value in
     * DecompilerType). Define which ones are missing and add those in the error
     * message. Reason for this method is the fact that the child process which
     * is launched in the command, launches another child to execute the
     * process. The error code is hard to retrieve, so this method was chosen
     * instead.
     *
     * @throws Exception if one or more tools fail to install
     */
    private void verifyInstallation() throws Exception {
        //Create a list in which all of the results go
        List<String> missingTools = new ArrayList<>();

        //Create an object for each folder
        File androidProjectFolder = new File(Constants.ANDROIDPROJECT_LIBRARY_FOLDER);
        File apkToolFolder = new File(Constants.APKTOOL_LIBRARY_FOLDER);
        File dex2jarFolder = new File(Constants.DEX2JAR_LIBRARY_FOLDER);
        File fernflowerFolder = new File(Constants.FERNFLOWER_LIBRARY_FOLDER);
        //Since the JadX folder is set to the "bin" folder within the "jadx" folder, the last four characters need to be removed in order for the name-printing to work
        File jadxFolder = new File(new String(Constants.JADX_LIBRARY_FOLDER).substring(0, Constants.JADX_LIBRARY_FOLDER.length() - 4));
        File jdcmdFolder = new File(Constants.JDCMD_LIBRARY_FOLDER);
        File crfFolder = new File(Constants.CFR_LIBRARY_FOLDER);
        File procyonFolder = new File(Constants.PROCYON_LIBRARY_FOLDER);

        //Add all folders in a list
        List<File> folders = new ArrayList<>();
        folders.add(androidProjectFolder);
        folders.add(apkToolFolder);
        folders.add(dex2jarFolder);
        folders.add(fernflowerFolder);
        folders.add(jadxFolder);
        folders.add(jdcmdFolder);
        folders.add(crfFolder);
        folders.add(procyonFolder);

        //Check each of the folders in the folders list
        for (File folder : folders) {
            //If it does not exist or is not a folder, then something went wrong
            if (folder.isDirectory() == false || folder.exists() == false) {
                //Add the missing tool to the list
                missingTools.add(folder.getName());
            }
        }

        //If the list contains 1 or more elements, an exception is thrown to notify the user
        if (missingTools.size() > 0) {
            StringBuilder message = new StringBuilder();
            message.append("\tThe following tools failed to install correctly:\n");
            for (String missingTool : missingTools) {
                message.append("\t\t" + missingTool.toUpperCase() + "\n");
            }
            message.append("\tSee the output log above for more details. Verify that you have installed the correct dependencies before you try again.");
            throw new Exception(message.toString());
        }
    }
}
