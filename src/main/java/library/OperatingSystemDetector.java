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

/**
 * Detects if the operating system on which it is executed
 *
 * @author Max 'Libra' Kersten
 */
public class OperatingSystemDetector {

    /**
     * Determines if the current operating system is Windows.
     *
     * @return true if the operating system is Windows, false if it is not.
     */
    public static boolean isWindows() {
        //If the os.name property of the Dalvik VM contains "windows", the system is Windows based
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            return true;
        }
        return false;
    }

    /**
     * Determines if the current operating system is a Linux distribution.
     *
     * @return true if the operating system is a Linux distribution, false if it
     * is not.
     */
    public static boolean isLinux() {
        //If the os.name property of the Dalvik VM contains "linux", the system is a Linux distribution
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            return true;
        }
        return false;
    }

    /**
     * Determines if the current operating system is a MacOS based operating
     * system
     *
     * @return true if the host is running MacOS, false if not
     */
    public static boolean isMac() {
        //If the os.name property of the Dalvik VM contains "mac os", the system is a MacOS distribution
        if (System.getProperty("os.name").toLowerCase().contains("mac os")) {
            return true;
        }
        return false;
    }
}
