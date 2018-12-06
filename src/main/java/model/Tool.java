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

/**
 * A tool is a wrapper for a Repository object and a ProjectInfo object, as long
 * as both contain information about the same repository and project.
 *
 * @author Max 'Libra' Kersten
 */
public class Tool {

    /**
     * The repository of this tool
     */
    private Repository repository;

    /**
     * The project info of this tool
     */
    private ProjectInfo projectInfo;

    /**
     * A tool consists of a repository (which is cloned) and the project info
     * (how to build the tool from the repository)
     *
     * @param repository the repository
     * @param projectInfo additional information about the repository
     */
    public Tool(Repository repository, ProjectInfo projectInfo) {
        this.repository = repository;
        this.projectInfo = projectInfo;
    }

    /**
     * Gets the repository of this tool
     *
     * @return the repository
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * Gets the project info of this tool
     *
     * @return the project info
     */
    public ProjectInfo getProjectInfo() {
        return projectInfo;
    }
}
