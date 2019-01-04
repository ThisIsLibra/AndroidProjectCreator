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
            decompileTest(DecompilerType.CFR);
            System.exit(0);
        }

        ArgumentManager manager = new ArgumentManager();
        //Show the version information
        manager.showVersion();
        //Set the action, if there is an error, the Action.ERROR value is provided. This is all handled within the setArguments function
        Action action = manager.setArguments(args);
        //If incorrect or unknown parameters are provided, APC provides feedback to the user and then terminates.
        if (action == Action.ERROR) {
            //Obtain the provided arguments in a String-object for later use
            String arguments = "";
            for (int i = 0; i < args.length; i++) {
                arguments += args[i] + " ";
            }
            System.out.println("[+]The provided input has not been recognised by AndroidProjectCreator. Below is the information you've provided. \n   Afterwards, the correct usage information is given below for an easy comparison.");
            System.out.println("\t" + arguments);
            System.out.println("");
            manager.showUsage();
            //Close APC with the 'unsuccessful' message, hence the '1' as parameter, instead of 0
            System.exit(1);
        }
        //Executes the action based on the return value of the setArguments function
        manager.executeAction(action);
    }

    private static void decompileTest(DecompilerType decompiler) {
        ArgumentManager manager = new ArgumentManager();
        manager.showVersion();
        String[] arg = new String[4];
        arg[0] = "-decompile";
        arg[1] = decompiler.toString().toLowerCase();
        arg[2] = "/Users/_taste/Desktop/secchating.apk";
        arg[3] = "./procyon-test";
        Action action = manager.setArguments(arg);
        manager.executeAction(action);
    }
}
