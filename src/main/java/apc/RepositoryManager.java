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

import enumeration.DecompilerType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import library.Constants;
import model.Repository;
import model.Tool;
import net.lingala.zip4j.exception.ZipException;

/**
 * Handles everything regarding repositories, such as cloning and updating
 *
 * @author Max 'Libra' Kersten
 */
public class RepositoryManager {

    /**
     * The file manager that is used within this class
     */
    private FileManager fileManager;

    /**
     * This class handles everything regarding repositories, such as cloning and
     * updating
     */
    public RepositoryManager() {
        fileManager = new FileManager();
    }

    /**
     * Removes the repository directory (as specified by the constant
     * <code>Constants.REPOSITORY_FOLDER</code>)
     *
     * @throws IOException when the repository cannot be removed
     */
    public void removeRepositoryFolder() throws IOException {
        File reposDirectory = new File(Constants.REPOSITORY_FOLDER);
        if (reposDirectory.exists() && reposDirectory.isDirectory()) {
            fileManager.delete(reposDirectory);
        }
    }

    /**
     * Clones all repositories that are provided
     *
     * @param repositoryList the repositories to be cloned
     *
     * @throws IOException if the cloning of a repository fails
     */
    public void cloneRepositories(List<Repository> repositoryList) throws IOException {
        for (Repository repository : repositoryList) {
            System.out.println("[+]Started cloning " + repository.getName());
            repository.cloneRepository();
            System.out.println("[+]Finished cloning " + repository.getName());
        }
    }

    /**
     * Updates all repositories that are given using.
     *
     * @param repositoryList the repositories to be updated
     * @throws IOException if an IO operation returns an error
     */
    public void updateRepositories(List<Repository> repositoryList) throws IOException {
        for (Repository repository : repositoryList) {
            System.out.println("[+]Started updating " + repository.getName());
            repository.updateRepository();
            System.out.println("[+]Succesfully updated " + repository.getName());
        }
    }

    /**
     * Builds each project that is provided
     *
     * @param tools the tools to be build
     * @throws IOException if the building process went wrong
     * @throws InterruptedException if the thread was somehow interrupted
     */
    public void buildRepositories(List<Tool> tools) throws IOException, InterruptedException {
        for (Tool tool : tools) {
            try {
                if (tool.getRepository().getName().equalsIgnoreCase("androidproject")
                        || tool.getRepository().getName().equalsIgnoreCase(DecompilerType.PROCYON.toString())
                        || tool.getRepository().getName().equalsIgnoreCase(DecompilerType.JEB3.toString())
                        || tool.getRepository().getName().equalsIgnoreCase(DecompilerType.JDCMD.toString())) {
                    continue;
                }
                System.out.println("[+]Starting to build " + tool.getRepository().getName());
                tool.getProjectInfo().getBuildCommand().execute();
                System.out.println("[+]Finished building " + tool.getRepository().getName());
            } catch (IOException ex) {
                throw new IOException("Something went wrong when building " + tool.getRepository().getName());
            }
        }
    }

    /**
     * Empty the folders of each repository before the content is placed in it.
     * This is required during the update process
     *
     * @param tools the tools which will be emptied
     * @throws IOException is an IO error occurs
     */
    public void emptyLibraryFolders(List<Tool> tools) throws IOException {
        for (Tool tool : tools) {
            fileManager.emptyFolder(new File(Constants.LIBRARY_FOLDER + "/" + tool.getRepository().getName()));
        }
    }

    /**
     * Extracts the output of the builds to the library (as is defined in
     * <code>Constants.getLibraryFolderName()</code>
     *
     * @param tools the tools to be extracted
     */
    public void extractBuilds(List<Tool> tools) {
        for (Tool tool : tools) {
            try {
                /**
                 * Since the Android Studio Project repository, CFR mirror,
                 * Procyon mirror and JEB3 CLI Android Decompiler script only
                 * consist of a single file, it does not need to be extracted.
                 */
                if (tool.getRepository().getName().equalsIgnoreCase("androidproject")) {
                    fileManager.copyFolder(new File(Constants.ANDROIDPROJECT_REPOSITORY_FOLDER), new File(Constants.ANDROIDPROJECT_LIBRARY_FOLDER));
                    continue;
                } else if (tool.getRepository().getName().equalsIgnoreCase(DecompilerType.CFR.toString())) {
                    fileManager.copyFolder(tool.getProjectInfo().getBuildOutputFolder(), new File(Constants.CFR_LIBRARY_FOLDER));
                    continue;
                } else if (tool.getRepository().getName().equalsIgnoreCase(DecompilerType.PROCYON.toString())) {
                    fileManager.copyFolder(new File(Constants.PROCYON_REPOSITORY_FOLDER), new File(Constants.PROCYON_LIBRARY_FOLDER));
                    continue;
                } else if (tool.getRepository().getName().equalsIgnoreCase(DecompilerType.JEB3.toString())) {
                    fileManager.copyFolder(new File(Constants.JEB3_CLI_ANDROID_SCRIPT_REPOSITORY_FOLDER), new File(Constants.JEB3_CLI_ANDROID_SCRIPT_LIBRARY_FOLDER));
                    continue;
                } else if (tool.getRepository().getName().equalsIgnoreCase(DecompilerType.JDCMD.toString())) {
                    fileManager.copyFolder(new File(Constants.JDCMD_REPOSITORY_FOLDER), new File(Constants.JDCMD_LIBRARY_FOLDER));
                    continue;
                }
                System.out.println("[+]Extracting " + tool.getRepository().getName());
                File buildOutputFolder = tool.getProjectInfo().getBuildOutputFolder();
                String partialArchiveName = tool.getProjectInfo().getPartialOutputName();
                //Loop through the output directory of the build
                for (File currentFile : buildOutputFolder.listFiles()) {
                    //If this file contains the partial name, then it is treated as being the build output
                    if (currentFile.getName().toLowerCase().contains(partialArchiveName.toLowerCase()) && partialArchiveName.toLowerCase().endsWith(".zip")) {
                        File outputLocation = new File(buildOutputFolder.getAbsolutePath() + "/" + Constants.BUILD_OUTPUT);
                        System.out.println("[+]Extracting to " + outputLocation.getAbsolutePath());
                        outputLocation.mkdir();
                        fileManager.extractArchive(currentFile.getAbsolutePath(), outputLocation.getAbsolutePath());
                        //Copy extracted files to the lib folder, where only the builds reside
                        if (tool.getRepository().getName().equalsIgnoreCase(DecompilerType.DEX2JAR.toString())) {
                            fileManager.copyFolder(outputLocation.listFiles()[0], new File(Constants.DEX2JAR_LIBRARY_FOLDER));
                        } else {
                            fileManager.copyFolder(outputLocation, new File(Constants.LIBRARY_FOLDER + "/" + tool.getRepository().getName()));;
                        }
                    } else if (currentFile.getName().toLowerCase().contains(partialArchiveName.toLowerCase()) && partialArchiveName.toLowerCase().endsWith(".jar")) {
                        fileManager.copyFolder(buildOutputFolder, new File(Constants.LIBRARY_FOLDER + "/" + tool.getRepository().getName()));
                    }
                }
                System.out.println("[+]Finished extracting " + tool.getRepository().getName());

            } catch (ZipException ex) { }
              catch (IOException ex) {
                System.err.println("Something went wrong during the extraction of the build of " + tool.getRepository().getName() + ":\n" + ex.getMessage());
                ex.printStackTrace();
                }
        }
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
    public void verifyInstallation() throws Exception {
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
        File jeb3Script = new File(Constants.JEB3_CLI_ANDROID_SCRIPT_LIBRARY_FOLDER);

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
        folders.add(jeb3Script);

        //Check each of the folders in the folders list
        for (File folder : folders) {
            //If it does not exist or is not a folder, then something went wrong
            if (folder.exists() == false || folder.isDirectory() == false) {
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
