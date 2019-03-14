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

import enumeration.Action;
import enumeration.DecompilerType;
import java.io.File;

/**
 * The ArgumentPackage class is used as a single object to be shared between the
 * ArgumentParser and the ArgumentManager. Depending on the decompiler, some
 * values may be null within this class, which is fine since they're not
 * required.
 *
 * @author Max 'Libra' Kersten
 */
public class ArgumentPackage {

    private DecompilerType decompilerType;
    private File apk;
    private File outputLocation;
    private File jeb3Folder;
    private Action action;

    /**
     * This constructor is used when an action requires no parameters, such as
     * INSTALL, UPDATE or ERROR
     *
     * @param action the action to be set
     */
    public ArgumentPackage(Action action) {
        this.action = action;
    }

    /**
     * This constructor is used to save the requested decompiler, the APK file
     * and the output location for the Android Studio Project. Note that the
     * action within this object is set to DECOMPILE by default.
     *
     * @param decompilerType the requested decompiler
     * @param apk the APK to be decompiled
     * @param outputLocation the output location for the Android Studio project
     */
    public ArgumentPackage(DecompilerType decompilerType, File apk, File outputLocation) {
        this.action = Action.DECOMPILE;
        this.decompilerType = decompilerType;
        this.apk = apk;
        this.outputLocation = outputLocation;
    }

    /**
     * This constructor is to be used when the specified decompiler is JEB3. The
     * action is set to DECOMPILE and the decompiler type to JEB3 by default.
     *
     * @param apk the APK to decompile
     * @param outputLocation the output location of the Android Studio project
     * @param jeb3Folder the folder in which JEB3 is installed, i.e.
     * /path/to/jeb3-pro
     */
    public ArgumentPackage(File apk, File outputLocation, File jeb3Folder) {
        this.action = Action.DECOMPILE;
        this.decompilerType = DecompilerType.JEB3;
        this.apk = apk;
        this.outputLocation = outputLocation;
        this.jeb3Folder = jeb3Folder;
    }

    /**
     * Get the action that was set by the ArgumentParser
     *
     * @return the action to execute
     */
    public Action getAction() {
        return action;
    }

    /**
     * The decompiler type that was set by the ArgumentParser
     *
     * @return the requested decompiler
     */
    public DecompilerType getDecompilerType() {
        return decompilerType;
    }

    /**
     * The APK file object that was set by the ArgumentParser
     *
     * @return the APK file object
     */
    public File getApk() {
        return apk;
    }

    /**
     * The output location of the Android Studio Project
     *
     * @return the output location file object
     */
    public File getOutputLocation() {
        return outputLocation;
    }

    /**
     * The file object of the JEB3 folder
     *
     * @return the JEB3 folder object
     */
    public File getJeb3Folder() {
        return jeb3Folder;
    }

}
