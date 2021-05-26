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
import library.Constants;
import model.ArgumentPackage;

/**
 * This class serves as a front for a possible GUI. The 'ArgumentManager' class
 * contains all functions.
 *
 * @author Max 'Libra' Kersten
 */
public class AndroidProjectCreator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /**
         * Build this project with: mvn clean compile assembly:single
         *
         * Ubuntu 18.04 settings (make sure JAVA_HOME is set to the JDK): sudo
         * apt-get install openjdk-8-jre openjdk-8-jdk maven
         *
         * Windows: Install Java 8 JRE, Java 8 JDK Maven and set the JAVA_HOME
         * system environment variable to the JDK
         *
         * MacOS: install Java 8 JRE, Java 8 JDK and use brew to install maven
         */
        boolean debugging = false;
        if (debugging) {
            installTest();
            //updateTest();
            //decompileTest(DecompilerType.FERNFLOWER);
            System.exit(0);
        }

        //Instantiate the ArgumentParser for later use
        ArgumentParser argumentParser = new ArgumentParser();
        //Instantiate the ArgumentManager for later use
        ArgumentManager argumentManager = new ArgumentManager();
        //Show the version information
        argumentManager.showVersion();
        //Set the action, if there is an error, the Action.ERROR value is provided. This is all handled within the setArguments function
        ArgumentPackage argumentPackage = argumentParser.setArguments(args);
        //If incorrect or unknown parameters are provided, APC provides feedback to the user and then terminates.
        if (argumentPackage.getAction() == Action.ERROR) {
            handleAction(argumentManager, args);
        }
        //Executes the action based on the return value of the setArguments function
        argumentManager.execute(argumentPackage);
    }

    /**
     * A method which is used during debugging to avoid mistakes in the code
     */
    private static void installTest() {
        ArgumentManager manager = new ArgumentManager();
        manager.showVersion();
        String[] args = new String[1];
        args[0] = "-install";
        ArgumentParser argumentParser = new ArgumentParser();
        ArgumentPackage argumentPackage = argumentParser.setArguments(args);
        manager.execute(argumentPackage);
    }

    /**
     * A method which is used during debugging to avoid mistakes in the code
     */
    private static void updateTest() {
        ArgumentManager manager = new ArgumentManager();
        manager.showVersion();
        String[] args = new String[1];
        args[0] = "-update";
        ArgumentParser argumentParser = new ArgumentParser();
        ArgumentPackage argumentPackage = argumentParser.setArguments(args);
        manager.execute(argumentPackage);
    }

    /**
     * A method which is used during debugging to avoid mistakes in the code
     *
     * @param decompiler the decompiler that should be used
     */
    private static void decompileTest(DecompilerType decompiler) {
        ArgumentManager manager = new ArgumentManager();
        manager.showVersion();
        String[] args;
        if (decompiler == DecompilerType.JEB3) {
            args = new String[5];
        } else {
            args = new String[4];
        }
        args[0] = "-decompile";
        args[1] = decompiler.toString().toLowerCase();
        //Mac is excluded from the tests
        if (Constants.isLinux()) {
            args[2] = "/home/libra/Documents/apc-test/apk/challenge1_release.apk";
        } else if (Constants.isWindows()) {
            args[2] = "C:\\Users\\Libra\\Downloads\\ap k.apk";
        }
        args[3] = "./test-output-guid-" + java.util.UUID.randomUUID();
        if (decompiler == DecompilerType.JEB3) {
            args[4] = "/home/libra/Downloads/jeb-pro";
        }
        ArgumentParser argumentParser = new ArgumentParser();
        ArgumentPackage argumentPackage = argumentParser.setArguments(args);
        manager.execute(argumentPackage);
    }

    //TODO refactor this method into the argument parser
    private static void handleAction(ArgumentManager manager, String[] args) {
        //If no arguments are given, the general usage information should be given
        if (args.length == 0 || args.length == 1) {
            manager.showUsage();
            System.exit(1);
        } else if (args.length > 4) {
            //Since there is no option to support more than four arguments, this should be fixed first
            System.out.println("\tAndroidProjectCreator does not support more than four arguments!");
        } else {
            //Obtain the provided arguments in a String-object for later use
            String arguments = "";
            //Concatenate the arguments behind one another
            for (int i = 0; i < args.length; i++) {
                arguments += args[i] + " ";
            }
            //Notify the user of the error
            System.out.println("[+]The provided input has not been recognised by AndroidProjectCreator, the provided arguments are given below.\n");
            //Display the received arguments back to the user
            System.out.println("\t" + arguments + "\n");
            //Loop through all of the arguments to display additional information
            for (int i = 0; i < args.length; i++) {
                switch (i) {
                    case 0:
                        //The first argument, in any case, is the requested method
                        System.out.println("\tThe requested method is:\t\t" + args[0]);
                        System.out.println("");
                        /**
                         * If this point is reached, either the "-decompile"
                         * method is called (since it requires more arguments
                         * than 1) or a non-existing method is called
                         *
                         */
                        if (args[0].equalsIgnoreCase("-decompile")) {
                            System.out.println("\tThe embedded decompilers that are included are:");
                            for (DecompilerType decompilerType : DecompilerType.values()) {
                                if (decompilerType.toString().equalsIgnoreCase("apktool") || decompilerType.toString().equalsIgnoreCase("dex2jar")) {
                                    continue;
                                }
                                System.out.println("\t\t" + decompilerType);
                            }
                        } else {
                            System.out.println("\tThe available methods are:");
                            for (Action method : Action.values()) {
                                if (method.toString().equalsIgnoreCase("error")) {
                                    System.out.println("\t\t-HELP");
                                    continue;
                                }
                                System.out.println("\t\t-" + method);
                            }
                        }
                        break;
                    case 1:
                        //If the decompile method is requested, the second argument is the requested decompiler
                        if (args[0].equalsIgnoreCase("-decompile")) {
                            System.out.println("\tThe requested decompiler is:\t\t" + args[1]);
                        } else {
                            //Notify the user that too many arguments are passed
                            System.out.println("\tOnly the \"-DECOMPILE\" option supports more than one argument!");
                        }
                        break;
                    case 2:
                        //If decompilation was chosen, the location of the APK file should also be provided
                        if (args[0].equalsIgnoreCase("-decompile")) {
                            //The location itself is shown
                            System.out.println("\tThe provided APK location is:\t\t" + args[2]);
                            File apk = new File(args[2]);
                            //At first, a check is done to see if the APK file actually exists, if this isn't the case, the user is notified
                            if (!apk.exists()) {
                                System.out.println("\t\tThe APK file does not exist!");
                            } else if (apk.exists() && apk.isFile()) { //If the file exists and is a file, the user is provided this information
                                System.out.println("\t\tThe APK file exists and is a file!");
                            } else if (apk.exists() && apk.isDirectory()) { //If the file is actually an existing folder, the user should know
                                System.out.println("\t\tThe APK file is not a file, but a folder!");
                            }
                        }
                        break;
                    default:
                        //The last argument of the decompilation method is the output location, this cannot be checked as it is made at the end of the decompilation process
                        break;
                }
            }
            //Close APC with the 'unsuccessful' message, hence the '1' as parameter, instead of 0
            System.exit(1);
        }
    }
}
