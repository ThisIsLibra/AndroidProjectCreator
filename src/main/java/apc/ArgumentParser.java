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

import enumeration.Action;
import enumeration.DecompilerType;
import java.io.File;
import java.nio.file.Paths;
import model.ArgumentPackage;

/**
 * This class handles the parsing of the received arguments via the CLI
 *
 * @author Max 'Libra' Kersten
 */
public class ArgumentParser {

    /**
     * Extracts the arguments from the provided String array and saves them into
     * their respective fields in this class
     *
     * CLI usage:
     * <code>java -jar AndroidProjectCreator.jar -decompile [name] /path/to/app.apk /path/to/output/to</code>
     *
     * In case JEB3 is used, the JEB3 folder should be given as a fifth
     * argument, as can be seen in the example below: *
     * <code>java -jar AndroidProjectCreator.jar -decompile [name] /path/to/app.apk /path/to/output/to /path/to/jeb/folder</code>
     *
     * Possible decompilers: JDCmd (uses JD-Core, also used in JD-GUI), JADX,
     * Fernflower, CFR, ProCyon and JEB3
     *
     * @param args the string array containing the user defined input
     * @return if all variables were set, the requested action is returned as an
     * enum value. If one or more variables were not set successfully, the
     * enum's value equals Action.ERROR.
     */
    public ArgumentPackage setArguments(String[] args) {
        //Check if the amount of provided arguments equals one, which is also equal to the installation command. Note that both strings are converted to a lowercase variant.
        if (args.length == 1 && "-install".toLowerCase().equals(args[0].toLowerCase())) {
            return parseInstall();
            //Check if the update command is provided (if it is not the installation command).
        } else if (args.length == 1 && "-update".toLowerCase().equals(args[0].toLowerCase())) {
            return parseUpdate();
            //Check if the amount of arguments equals 4 or 5, of which the first equals to the decompile command.
        } else if ((args.length == 4 || args.length == 5) && "-decompile".toLowerCase().equals(args[0].toLowerCase())) {
            return parseDecompile(args);
        }
        //Return error if something went wrong
        return new ArgumentPackage(Action.ERROR);
    }

    /**
     * Obtain an argument package to install the tools
     *
     * @return the argument package which can be sent over to the argument
     * manager
     */
    private ArgumentPackage parseInstall() {
        ArgumentPackage argumentPackage = new ArgumentPackage(Action.INSTALL);
        return argumentPackage;
    }

    /**
     * Obtain an argument package to update the embedded tools
     *
     * @return the argument package which can be sent over to the argument
     * manager
     */
    private ArgumentPackage parseUpdate() {
        ArgumentPackage argumentPackage = new ArgumentPackage(Action.UPDATE);
        return argumentPackage;
    }

    /**
     * Obtain an argument package to decompile a given APK with a given
     * decompiler. If JEB3 isn't chosen as the decompiler, requesting the JEB3
     * folder from the package will return null.
     *
     * @param args the arguments that the user provided
     * @return the argument package which contains all required information for
     * the argument manager
     */
    private ArgumentPackage parseDecompile(String[] args) {
        DecompilerType decompilerType;
        File apk;
        File outputLocation;
        File jeb3Folder;
        ArgumentPackage argumentPackage;
        //Set the decompiler type
        String decompilerString = args[1];
        if (decompilerString.equalsIgnoreCase(DecompilerType.FERNFLOWER.toString())) {
            decompilerType = DecompilerType.FERNFLOWER;
        } else if (decompilerString.equalsIgnoreCase(DecompilerType.JADX.toString())) {
            decompilerType = DecompilerType.JADX;
        } else if (decompilerString.equalsIgnoreCase(DecompilerType.JDCMD.toString())) {
            decompilerType = DecompilerType.JDCMD;
        } else if (decompilerString.equalsIgnoreCase(DecompilerType.CFR.toString())) {
            decompilerType = DecompilerType.CFR;
        } else if (decompilerString.equalsIgnoreCase(DecompilerType.PROCYON.toString())) {
            decompilerType = DecompilerType.PROCYON;
        } else if (decompilerString.equalsIgnoreCase(DecompilerType.JEB3.toString())) {
            decompilerType = DecompilerType.JEB3;
        } else {
            return new ArgumentPackage(Action.ERROR);
        }
        //Sets the path to the APK
        apk = Paths.get(args[2]).toFile();
        //Sanity checks on the APK
        if (!apk.exists() || apk.isDirectory()) {
            return new ArgumentPackage(Action.ERROR);
        }
        //Set the location where the output should be written to
        outputLocation = Paths.get(args[3]).toFile();
        //Make sure the output location exists completely, thus all parent folders are also created if need be
        if (!outputLocation.exists()) {
            outputLocation.mkdirs();
        }
        //Detect if JEB is used
        if (args.length == 4) {
            argumentPackage = new ArgumentPackage(decompilerType, apk, outputLocation);
            return argumentPackage;
        } else if (args.length == 5) {
            jeb3Folder = Paths.get(args[4]).toFile();
            //Check if the JEB folder exists and is a directory (instead of a file)
            if (jeb3Folder.exists() && jeb3Folder.isDirectory()) {
                argumentPackage = new ArgumentPackage(apk, outputLocation, jeb3Folder);
                return argumentPackage;
            }
        }
        //In case this code is reached, something went wrong and thus the error package is returned
        return new ArgumentPackage(Action.ERROR);
    }
}
