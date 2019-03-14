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

import java.util.ArrayList;
import java.util.List;
import model.Tool;

/**
 * All tools that are included in this project can be found in this class. A
 * tool consists of two parts. The Repository and the ProjectInfo, both of which
 * contain information that is required during the cloning and building.
 *
 * @author Max 'Libra' Kersten
 */
public class Tools {

    /**
     * Get all tools that are included in this project
     *
     * @return a list of all tools
     */
    public static List<Tool> getTools() {
        List<Tool> tools = new ArrayList<>();
        tools.add(getAndroidStudioProject());
        tools.add(getApkTool());
        tools.add(getCfr());
        tools.add(getDex2Jar());
        tools.add(getJadX());
        tools.add(getJdCmd());
        tools.add(getJeb3AndroidDecompilerScript());
        tools.add(getFernflower());
        tools.add(getProcyon());
        return tools;
    }

    /**
     * The Dex2Jar tool
     *
     * @return the Dex2Jar tool object
     */
    private static Tool getDex2Jar() {
        return new Tool(Repositories.getDex2Jar(), ProjectInfos.getDex2Jar());
    }

    /**
     * The JadX tool
     *
     * @return the JadX tool object
     */
    private static Tool getJadX() {
        return new Tool(Repositories.getJadX(), ProjectInfos.getJadX());
    }

    /**
     * The JD-CMD tool
     *
     * @return the JD-CMD tool object
     */
    private static Tool getJdCmd() {
        return new Tool(Repositories.getJdCmd(), ProjectInfos.getJdCmd());
    }

    /**
     * The Fernflower tool
     *
     * @return the Fernflower tool object
     */
    private static Tool getFernflower() {
        return new Tool(Repositories.getFernflower(), ProjectInfos.getFernflower());
    }

    /**
     * The APKTool tool
     *
     * @return the APKTool tool object
     */
    private static Tool getApkTool() {
        return new Tool(Repositories.getApkTool(), ProjectInfos.getApkTool());
    }

    /**
     * The Android Studio template project
     *
     * @return the Android Studio template tool object
     */
    private static Tool getAndroidStudioProject() {
        return new Tool(Repositories.getAndroidProject(), null);
    }

    /**
     * The pre-built CFR decompiler tool
     *
     * @return the pre-built CFR decompiler tool object
     */
    private static Tool getCfr() {
        return new Tool(Repositories.getCfr(), null);
    }

    /**
     * The pre-built Procyon decompiler tool
     *
     * @return the pre-built Procyon decompiler tool object
     */
    private static Tool getProcyon() {
        return new Tool(Repositories.getProcyon(), null);
    }

    /**
     * The JEB3 Android decompiler script
     *
     * @return the tool object containing the repository
     */
    private static Tool getJeb3AndroidDecompilerScript() {
        return new Tool(Repositories.getJeb3AndroidDecompilerScript(), null);
    }
}
