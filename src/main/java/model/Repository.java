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
package model;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import static java.util.Collections.singleton;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.TextProgressMonitor;

/**
 * The repository from which a tool is cloned
 *
 * @author Max 'Libra' Kersten
 */
public class Repository {

    /**
     * The name of the repository
     */
    private final String name;

    /**
     * The URL of the repository
     */
    private final String url;

    /**
     * The local directory in which the cloned repository should be saved
     */
    private final File directory;

    /**
     * The branch in the repository which should be used
     */
    private final String branch;

    /**
     * Create a repository object which can be used to clone the repository with
     * the provided information
     *
     * @param name The name of the repository
     * @param url The URL of the repository
     * @param directory The local directory in which the cloned repository
     * should be saved
     * @param branch The branch in the repository which should be used
     */
    public Repository(String name, String url, File directory, String branch) {
        this.name = name;
        this.url = url;
        this.directory = directory;
        this.branch = branch;
    }

    /**
     * Gets the name of the repository
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Function to clone the repository
     *
     * @throws IOException if something goes wrong, this exception is thrown
     */
    public void cloneRepository() throws IOException {
        try {
            Git.cloneRepository()
                    .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                    .setURI(url)
                    .setDirectory(directory)
                    .setBranchesToClone(singleton(branch))
                    .setBranch(branch)
                    .call();

        } catch (GitAPIException ex) {
            //Because the GitAPIException is abstract and cannot be instantiated, another (similar) exception is used
            throw new IOException("[+]There was an error cloning " + name + ". Verify your internet connection and the permissions of the folder!");
        }
    }

    /**
     * Updates the current repository
     *
     * @throws IOException if the Git pull goes wrong of if the directory cannot
     * be found
     */
    public void updateRepository() throws IOException {
        try {
            Git git = Git.open(directory);
            if (git.pull().setRemoteBranchName(branch).call().isSuccessful()) {
                System.out.println("[+]Git pull succesful for " + name);
            }
        } catch (GitAPIException ex) {
            //Because the GitAPIException is abstract and cannot be instantiated, another (similar) exception is used
            throw new IOException("Git pull failed, AndroidProjectCreator will now exit");
        } catch (IOException ex) {
            throw new IOException("Repository directory not found, reinstall AndroidProjectCreator and check your permissions in this folder. AndroidProjectCreator will now exit.");
        }
    }
}
