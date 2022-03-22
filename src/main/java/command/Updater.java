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
import java.util.List;

import library.Repositories;
import library.Tools;
import model.Tool;

/**
 * Handles the repositories to update and rebuild them
 *
 * @author Max 'Libra' Kersten
 */
public class Updater {
    List<Tool> tools=Tools.getTools();


    public void update() throws IOException, Exception {
        System.out.println("[+]Starting the update");
        RepositoryManager repositoryManager = new RepositoryManager();
        System.out.println("[+]Starting updating the repositories");
        repositoryManager.updateRepositories(Repositories.getAll());
        System.out.println("[+]Updating finished");
        System.out.println("[+]Starting to build all repositories");
        repositoryManager.buildRepositories(tools);
        System.out.println("[+]All projects have been built");
        System.out.println("[+]Removing tools from the library");
        repositoryManager.emptyLibraryFolders(tools);
        System.out.println("[+]Succesfully removed tools from the library");
        System.out.println("[+]Starting to extract the builds to the library");
        repositoryManager.extractBuilds(tools);
        System.out.println("[+]Extraction complete");
        System.out.println("[+]Verifying the toolset");
        repositoryManager.verifyInstallationUsingRequiredFolders();
        System.out.println("[+]Verification succesful!");
        System.out.println("[+]Update complete!");
    }
}
