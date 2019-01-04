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
     * Removes the library folder if it exists
     *
     * @throws IOException if the file and folder deletion goes wrong
     */
    private void removeLibraryFolder() throws IOException {
        File library = new File(Constants.LIBRARY_FOLDER);
        if (library.exists()) {
            fileManager.delete(library);
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
     * Builds each project that is provided
     *
     * @param tools the tools to be build
     * @throws IOException if the building process went wrong
     * @throws InterruptedException if the thread was somehow interrupted
     */
    public void buildRepositories(List<Tool> tools) throws IOException, InterruptedException {
        for (Tool tool : tools) {
            try {
                if (tool.getRepository().getName().equalsIgnoreCase("androidstudioproject")
                        || tool.getRepository().getName().equalsIgnoreCase(DecompilerType.CFR.toString())
                        || tool.getRepository().getName().equalsIgnoreCase(DecompilerType.PROCYON.toString())) {
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
     * Extracts the output of the builds to the library (as is defined in
     * <code>Constants.getLibraryFolderName()</code>
     *
     * @param tools the tools to be extracted
     * @throws IOException if the build of a tool cannot be extracted or copied
     */
    public void extractBuilds(List<Tool> tools) throws IOException {
        for (Tool tool : tools) {
            try {
                //Since the Android Studio Project repository, CFR mirror and Procyon mirror only consist of a single file, it does not need to be extracted.
                if (tool.getRepository().getName().equalsIgnoreCase("androidstudioproject")) {
                    fileManager.copyFolder(new File(Constants.ANDROIDPROJECT_REPOSITORY_FOLDER), new File(Constants.ANDROIDPROJECT_LIBRARY_FOLDER));
                    continue;
                } else if (tool.getRepository().getName().equalsIgnoreCase(DecompilerType.CFR.toString())) {
                    fileManager.copyFolder(new File(Constants.CFR_REPOSITORY_FOLDER), new File(Constants.CFR_LIBRARY_FOLDER));
                    continue;
                } else if (tool.getRepository().getName().equalsIgnoreCase(DecompilerType.PROCYON.toString())) {
                    fileManager.copyFolder(new File(Constants.PROCYON_REPOSITORY_FOLDER), new File(Constants.PROCYON_LIBRARY_FOLDER));
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

            } catch (IOException | ZipException ex) {
                throw new IOException("Something went wrong during the extraction of the build of " + tool.getRepository().getName());
            }
        }
    }

    /**
     * Removes the <code>Constants.LIBRARY_FOLDER</code> folder if it exists and
     * creates it. Otherwise the folder is just created.
     *
     * @throws IOException if a file cannot be deleted
     */
    public void setupLibrary() throws IOException {
        removeLibraryFolder();
        new File(Constants.LIBRARY_FOLDER).mkdirs();
    }
}
