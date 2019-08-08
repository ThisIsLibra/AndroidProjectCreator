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
package library;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import model.Command;
import model.ProjectInfo;

/**
 * All ProjectInfo objects are created in this class. To obtain information
 * about a specific project, one has to call that specific function. To obtain
 * all ProjectInfo objects in a list, use the <code>getAll</code> function.
 *
 * @author Max 'Libra' Kersten
 */
public class ProjectInfos {

    /**
     * Get all the ProjectInfo objects in a list
     *
     * @return all embedded ProjectInfo objects in a list
     */
    public static List<ProjectInfo> getAll() {
        List<ProjectInfo> projectInfos = new ArrayList<>();
        projectInfos.add(getApkTool());
        projectInfos.add(getCfr());
        projectInfos.add(getDex2Jar());
        projectInfos.add(getFernflower());
        projectInfos.add(getJadX());
        projectInfos.add(getJdCmd());
        return projectInfos;
    }

    /**
     * Get the project information regarding Cfr
     *
     * @return Cfr project information
     */
    public static ProjectInfo getCfr() {
        String compileCommand;
        //The terminal commands in Windows differ from the ones on MacOS and Linux distributions
        if (!Constants.isWindows()) {
            compileCommand = "mvn clean package";
        } else {
            compileCommand = "mvn clean package";
        }
        File directory = new File(Constants.CFR_REPOSITORY_FOLDER);
        //Set the information required to find the build, extract and copy it to the proper directory
        Command buildCommand = new Command(compileCommand, directory); //Gradle project
        File buildOutputFolder = new File(Constants.CFR_REPOSITORY_FOLDER + "/target");
        String partialOutputName = "-SNAPSHOT.jar";
        return new ProjectInfo(buildCommand, buildOutputFolder, partialOutputName);
    }

    /**
     * Get the project information regarding Dex2Jar
     *
     * @return Dex2Jar project information
     */
    public static ProjectInfo getDex2Jar() {
        String compileCommand;
        //The terminal commands in Windows differ from the ones on MacOS and Linux distributions
        if (!Constants.isWindows()) {
            compileCommand = "./gradlew clean distZip";
        } else {
            compileCommand = "gradlew.bat clean distZip";
        }
        File directory = new File(Constants.DEX2JAR_REPOSITORY_FOLDER);
        //Set the information required to find the build, extract and copy it to the proper directory
        Command buildCommand = new Command(compileCommand, directory); //Gradle project
        File buildOutputFolder = new File(Constants.DEX2JAR_REPOSITORY_FOLDER + "/dex-tools/build/distributions");
        String partialOutputName = "-SNAPSHOT.zip";
        return new ProjectInfo(buildCommand, buildOutputFolder, partialOutputName);
    }

    /**
     * Get the project information regarding JadX
     *
     * @return JadX project information
     */
    public static ProjectInfo getJadX() {
        String compileCommand;
        //The terminal commands in Windows differ from the ones on MacOS and Linux distributions
        if (!Constants.isWindows()) {
            compileCommand = "./gradlew dist";
        } else {
            compileCommand = "gradlew.bat dist";
        }
        File directory = new File(Constants.JADX_REPOSITORY_FOLDER);
        //Set the information required to find the build, extract and copy it to the proper directory
        Command buildCommand = new Command(compileCommand, directory);
        File buildOutputFolder = new File(Constants.JADX_REPOSITORY_FOLDER + "/build");
        String partialOutputName = "jadx-dev.zip";
        return new ProjectInfo(buildCommand, buildOutputFolder, partialOutputName);
    }

    /**
     * Get the project information regarding JD-CMD
     *
     * @return JD-CMD project information
     */
    public static ProjectInfo getJdCmd() {
        String compileCommand;
        if (!Constants.isWindows()) {
            compileCommand = "mvn package";
        } else {
            compileCommand = "mvn package";
        }
        File directory = new File(Constants.JDCMD_REPOSITORY_FOLDER);
        Command buildCommand = new Command(compileCommand, directory);
        File buildOutputFolder = new File(Constants.JDCMD_REPOSITORY_FOLDER);
        String partialOutputName = "-SNAPSHOT-dist.zip";
        return new ProjectInfo(buildCommand, buildOutputFolder, partialOutputName);
    }

    /**
     * Get the project information regarding Fernflower
     *
     * @return Fernflower project information
     */
    public static ProjectInfo getFernflower() {
        String compileCommand;
        if (!Constants.isWindows()) {
            compileCommand = "./gradlew jar";
        } else {
            compileCommand = "gradlew.bat jar";
        }
        File directory = new File(Constants.FERNFLOWER_REPOSITORY_FOLDER);
        Command buildCommand = new Command(compileCommand, directory);
        File buildOutputFolder = new File(Constants.FERNFLOWER_REPOSITORY_FOLDER + "/build/libs");
        String partialOutputName = "fernflower.jar";
        return new ProjectInfo(buildCommand, buildOutputFolder, partialOutputName);
    }

    /**
     * Get the project information regarding APKTool
     *
     * @return APKTool project information
     */
    public static ProjectInfo getApkTool() {
        String compileCommand;
        if (!Constants.isWindows()) {
            compileCommand = "./gradlew build shadowJar";
        } else {
            compileCommand = "gradlew.bat build shadowJar";
        }
        File directory = new File(Constants.APKTOOL_REPOSITORY_FOLDER);
        Command buildCommand = new Command(compileCommand, directory);
        File buildOutputFolder = new File(Constants.APKTOOL_REPOSITORY_FOLDER + "/brut.apktool/apktool-cli/build/libs");
        String partialOutputName = "apktool-cli-all.jar";
        return new ProjectInfo(buildCommand, buildOutputFolder, partialOutputName);
    }
}
