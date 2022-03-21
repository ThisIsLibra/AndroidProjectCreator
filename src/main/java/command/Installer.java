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
import java.io.IOException;
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
        System.out.println("[+]Starting cloning the repositories");
        repositoryManager.cloneRepositories(Repositories.getAll());
        System.out.println("[+]Cloning finished");
        System.out.println("[+]Starting to build all repositories");
        repositoryManager.buildRepositories(Tools.getTools());
        System.out.println("[+]All projects have been built");
        System.out.println("[+]Starting to extract the builds to the library");
        repositoryManager.extractBuilds(Tools.getTools());
        System.out.println("[+]Extraction complete");
        System.out.println("[+]Verifying the toolset");
        repositoryManager.verifyInstallationUsingRequiredFolders();
        System.out.println("[+]Verification succesful!");
        System.out.println("[+]Installation complete!");
    }
}
