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
        ArgumentManager manager = new ArgumentManager();
        //Show the version information
        manager.showVersion();
        //Set the action, if there is an error, the Action.ERROR value is provided. This is all handled within the setArguments function
        Action action = manager.setArguments(args);
        //Executes the action based on the return value of the setArguments function
        manager.executeAction(action);
    }
}
