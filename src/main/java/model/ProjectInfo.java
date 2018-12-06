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

/**
 * The ProjectInfo class contains additional information for a repository
 *
 * @author Max 'Libra' Kersten
 */
public class ProjectInfo {

    /**
     * The command to build the project
     */
    private Command buildCommand;

    /**
     * The folder in which the output of the build is put, as described by the
     * programmer who created the original project (a.k.a. tool)
     */
    private final File buildOutputFolder;

    /**
     * In some cases, the version number is in the name of the build output, so
     * only the partial name can be used. If the full name is known, it can be
     * used here as well.
     */
    private final String partialOutputName;

    /**
     * Create an object with information about a repository
     *
     * @param buildCommand The command to build the project
     * @param buildOutputFolder The folder in which the output of the build is
     * put, as described by the programmer who created the original project
     * (a.k.a. tool)
     * @param partialOutputName In some cases, the version number is in the name
     * of the build output, so only the partial name can be used. If the full
     * name is known, it can be used here as well.
     */
    public ProjectInfo(Command buildCommand, File buildOutputFolder, String partialOutputName) {
        this.buildOutputFolder = buildOutputFolder;
        this.buildCommand = buildCommand;
        this.partialOutputName = partialOutputName;
    }

    /**
     * Get the command which is used to build the repository
     *
     * @return the build command
     */
    public Command getBuildCommand() {
        return buildCommand;
    }

    /**
     * Get the location where the building process places its output
     *
     * @return the output folder of the build
     */
    public File getBuildOutputFolder() {
        return buildOutputFolder;
    }

    /**
     * In some cases, the version number is in the name of the build output, so
     * only the partial name can be used. If the full name is known, it can be
     * used here as well.
     *
     * @return the (partial) output name
     */
    public String getPartialOutputName() {
        return partialOutputName;
    }
}
