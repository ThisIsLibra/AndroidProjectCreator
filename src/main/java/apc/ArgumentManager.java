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
import command.CompactInstaller;
import command.Installer;
import command.Decompiler;
import command.Updater;
import enumeration.DecompilerType;
import java.io.File;
import java.io.IOException;
import library.Constants;
import model.ArgumentPackage;

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

    /**
     * The action, as defined by the <code>setArguments</code> function, will be
     * executed.
     *
     * @param arguments the provided arguments
     */
    public void execute(ArgumentPackage arguments) {
        try {
            /**
             * Since there was no error, the remaining options for the enum are
             * handled below. Exceptions that occur during the actions that are
             * defined within the enum, are caught in a single try-catch
             * structure to aggregate all exceptions at one place. This improves
             * the exception handling usability and maintainability and provides
             * more information to the user.
             */
            switch (arguments.getAction()) {
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
                case COMPACT_INSTALL:
                    /**
                     * Clones all tools into the library folder from a Github
                     * repository that is occasionally updated. All tools on
                     * that repository are precompiled, meaning one only has to
                     * download the files, resulting in a compacter installation
                     * in both disk space and time.
                     */
                    CompactInstaller compactInstaller = new CompactInstaller();
                    compactInstaller.install();
                    break;
                case UPDATE:
                    /**
                     * Updating the library is done by pulling the repository
                     * from the selected branch, rebuilding the tool and
                     * replacing it in the correct library folder.
                     */
                    Updater updater = new Updater();
                    updater.update();
                    break;
                case DECOMPILE:
                    //Decompile the APK into the parts required for the assembler
                    Decompiler decompiler = new Decompiler(arguments);
                    decompiler.decompile();
                    //Assemble the taken manifest and source files together with the Android Studio project
                    Assembler assembler = new Assembler(arguments.getOutputLocation());
                    assembler.assemble();
                    break;
                default:
                    //Something went wrong since this case shouldnt be reachable, show the usage and exit
                    showUsage();
                    System.exit(1);
                    break;
            }
        } catch (Exception ex) {
            cleanOnError(arguments.getOutputLocation());
            showError(ex);
            System.exit(1);
        }
    }

    /**
     * If an error occurs, the output location and the temp folder within the
     * library need to be cleaned. This method does exactly that.
     */
    private void cleanOnError(File outputLocation) {
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
        for (DecompilerType decompilerType : DecompilerType.values()) {
            usage.append("\t\t\t" + decompilerType + "\n");
        }
        usage.append("\t-compactInstall\n");
        usage.append("\t\tDownloads precompiled instances of the tools that are listed at \"-install\" from a repository that is mainted by me.\n");
        usage.append("\t\tThis is faster than installing it yourself, but the the \"-update\" will not work. Tools will also be a bit older.\n");
        usage.append("\t-update\n");
        usage.append("\t\tUpdating the library folder is equal to reinstalling the library using the -install function.\n");
        usage.append("\t-decompile\n");
        usage.append("\t\tUsing this function, more parameters are required.\n");
        usage.append("\t\tThe name of the decompiler needs to be specified, using one of the following embedded decompilers:\n");
        usage.append("\t\t\tCFR, FERNFLOWER, JADX, JDCMD, JEB3 and PROCYON\n");
        usage.append("\t\tAdditionally, the location of the APK and the output location for the Android Project are required.\n");
        usage.append("\t\tSample usage to decompile an APK:\n");
        if (Constants.isWindows()) {
            usage.append("\t\t\t java -jar AndroidProjectCreator.jar -decompile FERNFLOWER path\\to\\the.apk output\\path\n");
        } else {
            usage.append("\t\t\tjava -jar ./AndroidProjectCreator.jar -decompile FERNFLOWER /path/to/the.apk /output/path/\n");
        }
        usage.append("\t\tNote that one should provide the path to the JEB3 folder as a fifth argument if JEB3 is chosen to decompile the code.\n ");
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
        String versionNumber = "1.5.3-beta";
        StringBuilder version = new StringBuilder();
        version.append("[+]AndroidProjectCreator " + versionNumber + " [developed by Max 'Libra' Kersten <info@maxkersten.nl> or @Libranalysis on Twitter, Modified by Husayn]\n");
        System.out.println(version.toString());
    }
}
