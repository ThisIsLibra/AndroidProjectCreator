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

import command.Assembler;
import command.Installer;
import command.Decompiler;
import enumeration.Action;
import enumeration.DecompilerType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import library.Constants;
import library.OperatingSystemDetector;

/**
 * Handles everything regarding the flow of the AndroidProjectCreator, based on
 * the input provided by the user. The main function of this class is to
 * delegate functionality to one of the classes in the "command" package. All
 * exceptions (with a couple of exceptions which have their own documentation)
 * are handled in the <code>passArguments</code> function.
 *
 * @author Max 'Libra' Kersten
 */
public class ArgumentManager {

    private DecompilerType decompilerType;
    private File apk;
    private File outputLocation;

    /**
     * The action, as defined by the <code>setArguments</code> function, will be
     * executed.
     *
     * @param action the action to be executed
     */
    public void executeAction(Action action) {
        try {
            //Since there was no error, the remaining options for the enum are handled below. Exceptions that occur during the actions that are defined within the enum, are caught in a single try-catch structure to aggregate all exceptions at one place. This improves the exception handling usability and maintainability and provides more information to the user.
            switch (action) {
                case INSTALL:
                    /**
                     * Clone the repositories, build the projects and save them
                     * in the Constants.LIBRARY_FOLDER_NAME folder
                     */
                    Installer installer = new Installer();
                    installer.install();
                    //Usage is shown after the installation has successfully been completed
                    showUsage();
                    break;
                case UPDATE:
                    /**
                     * Updating the library is done via a new clone of the
                     * repositories, which are then built and stored in the
                     * <code>Constant.LIBRARY_FOLDER_NAME</code> folder.
                     *
                     * TODO in future versions, the repositories are saved,
                     * properly updated and build again.
                     */
                    Installer updater = new Installer();
                    updater.install();
                    break;
                case DECOMPILE:
                    //Decompile the APK into the parts required for the assembler
                    Decompiler decompiler = new Decompiler(decompilerType, apk);
                    decompiler.decompile();
                    //Assemble the taken manifest and source files together with the Android Studio project
                    Assembler assembler = new Assembler(outputLocation);
                    assembler.assemble();
                    break;
                default:
                    //Something went wrong since this case shouldnt be reachable, show the usage and exit
                    showUsage();
                    System.exit(1);
                    break;
            }
        } catch (Exception ex) {
            cleanOnError();
            showError(ex);
            System.exit(1);
        }
    }

    /**
     * If an error occurs, the output location and the temp folder within the
     * library need to be cleaned. This method does exactly that.
     */
    private void cleanOnError() {
        try {
            FileManager fileManager = new FileManager();
            //If something goes wrong during the installation or update, the outputLocation variable is equal to null
            if (outputLocation != null) {
                fileManager.delete(outputLocation);
            }
            fileManager.delete(new File(Constants.TEMP_LIBRARY_FOLDER));
        } catch (IOException ex) {
            System.out.println("[+]Something went wrong during the removal of " + outputLocation.getAbsolutePath());
        }
    }

    /**
     * Extracts the arguments from the provided String array and saves them into
     * their respective fields in this class
     *
     * CLI usage:
     * <code>java -jar AndroidProjectCreator.jar -decompile [name] /path/to/app.apk /path/to/output/to</code>
     *
     * Possible decompilers: JDCmd (uses JD-Core, also used in JD-GUI), JADX,
     * and Fernflower
     *
     * @param args the string array containing the user defined input
     * @return if all variables were set, the requested action is returned as an
     * enum value. If one or more variables were not set successfully, the
     * enum's value equals Action.ERROR.
     */
    public Action setArguments(String[] args) {
        //Check if the amount of provided arguments equals one, which is also equal to the installation command. Note that both strings are converted to a lowercase variant.
        if (args.length == 1 && "-install".toLowerCase().equals(args[0].toLowerCase())) {
            return Action.INSTALL;
            //Check if the update command is provided (if it is not the installation command).
        } else if (args.length == 1 && "-update".toLowerCase().equals(args[0].toLowerCase())) {
            return Action.UPDATE;
            //Check if the amount of arguments equals 4, of which the first equals to the decompile command.
        } else if (args.length == 4 && "-decompile".toLowerCase().equals(args[0].toLowerCase())) {
            //Sanity check all the other arguments
            if (null != args[1] && args[1].isEmpty() == false && null != args[2] && args[2].isEmpty() == false) {
                //Set the decompiler type
                String decompilerString = args[1].toLowerCase();
                if (decompilerString.equals(DecompilerType.FERNFLOWER.toString().toLowerCase())) {
                    decompilerType = DecompilerType.FERNFLOWER;
                } else if (decompilerString.equals(DecompilerType.JADX.toString().toLowerCase())) {
                    decompilerType = DecompilerType.JADX;
                } else if (decompilerString.equals(DecompilerType.JDCMD.toString().toLowerCase())) {
                    decompilerType = DecompilerType.JDCMD;
                } else if (decompilerString.equals(DecompilerType.CFR.toString().toLowerCase())) {
                    decompilerType = DecompilerType.CFR;
                } else if (decompilerString.equals(DecompilerType.PROCYON.toString().toLowerCase())) {
                    decompilerType = decompilerType.PROCYON;
                }
                //Sets the path to the APK
                apk = Paths.get(args[2]).toFile();
                //Sanity checks on the APK
                if (apk.exists() && apk.isFile()) {
                    //Set the location where the output should be written to
                    outputLocation = Paths.get(args[3]).toFile();
                    //Make sure the output location exists completely, thus all parent folders are also created if need be
                    if (!outputLocation.exists()) {
                        outputLocation.mkdirs();
                    }
                    return Action.DECOMPILE;
                }
            }
        }
        //Return error if something went wrong
        return Action.ERROR;
    }

    /**
     * Shows the usage information to the user
     */
    public void showUsage() {
        StringBuilder usage = new StringBuilder();
        usage.append("[+]AndroidProjectCreator has multiple modes, all of which are explained below, together with the required parameters.\n");
        usage.append("\t-install\n");
        usage.append("\t\tThis option requires an internet connection as it clones multiple repositories from Git.\n");
        usage.append("\t\tAfter the repositories have been downloaded, the projects will be compiled. \n");
        usage.append("\t\tThen, the compiled repositories are saved in a folder named \"Library\" in the folder where the JAR resides.\n");
        usage.append("\t\tThe repositories are then deleted from the disk.\n");
        usage.append("\t\tCurrently, the following tools are embedded:\n");
        for (DecompilerType type : DecompilerType.values()) {
            usage.append("\t\t\t" + type + "\n");
        }
        usage.append("\t-update\n");
        usage.append("\t\tUpdating the library folder is equal to reinstalling the library using the -install function.\n");
        usage.append("\t-decompile\n");
        usage.append("\t\tUsing this function, more parameters are required.\n");
        usage.append("\t\tThe name of the decompiler needs to be specified, using one of the following embedded decompilers:\n");
        usage.append("\t\t\tCFR, FERNFLOWER, JDCMD, JADX, PROCYON\n");
        usage.append("\t\tAdditionally, the location of the APK and the output location for the Android Project are required.\n");
        usage.append("\t\tSample usage to decompile an APK:\n");
        if (OperatingSystemDetector.isWindows()) {
            usage.append("\t\t\t java -jar AndroidProjectCreator.jar -decompile FERNFLOWER path\\to\\the.apk output\\path");
        } else {
            usage.append("\t\t\tjava -jar ./AndroidProjectCreator.jar -decompile FERNFLOWER /path/to/the.apk /output/path/\n");
        }
        System.out.println(usage.toString());
    }

    /**
     * Displays the given error message with an introduction
     */
    private void showError(Exception ex) {
        System.out.println("\n\n[+]An error has occurred, therefore AndroidProjectCreator has shut down. The error message is given below.");
        System.out.println("");
        System.out.println(ex.getMessage());
        System.out.println("");
        System.out.println("[+]For additional details, see the stack traces of the used tools above.");
    }

    /**
     * Display the version information
     */
    public void showVersion() {
        String versionNumber = "1.1-stable";

        StringBuilder version = new StringBuilder();
        version.append("[+]AndroidProjectCreator " + versionNumber + " [developed by Max 'Libra' Kersten <info@maxkersten.nl>]\n");
        System.out.println(version.toString());
    }
}
