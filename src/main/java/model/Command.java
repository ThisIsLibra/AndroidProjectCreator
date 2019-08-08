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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import library.Constants;

/**
 * Execute a command line command on any platform (Windows, MacOS and Linux
 * distributions) and receive the results back. The results are shown through
 * the default output stream of this application.
 *
 * @author Max 'Libra' Kersten
 */
public class Command {

    /**
     * The directory in which the command is executed
     */
    private final File workingDirectory;

    /**
     * The command which is to be executed
     */
    private final String command;

    /**
     * Create an instance of the command class, which builds a Gradle project
     *
     * @param command the command which is required to compile the project
     * @param workingDirectory the directory where the project resides
     */
    public Command(String command, File workingDirectory) {
        this.command = command;
        this.workingDirectory = workingDirectory;
    }

    /**
     * Executes the command based on the previously provided information
     *
     * Original code taken from Pepe (edited by tvanfosson) from
     * https://stackoverflow.com/a/5437863 Code altered by Max 'Libra' Kersten
     *
     * @throws IOException if something within the file handling goes wrong
     */
    public void execute() throws IOException {
        try {
            String[] processName = new String[1];
            if (Constants.isWindows()) {
                processName[0] = "cmd";
            } else {
                processName[0] = "sh";
            }
            Process p = Runtime.getRuntime().exec(processName, null, workingDirectory);
            new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
            new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
            PrintWriter stdin = new PrintWriter(p.getOutputStream());
            //Enter commands for the shell here
            stdin.println(command);
            stdin.close();
            p.waitFor();
            //New line to avoid the next line to be printed on the same line as the one which ends the command
            System.out.println("");
        } catch (IOException | InterruptedException ex) {
            throw new IOException("The working directory (" + workingDirectory.getAbsolutePath() + ") can not be found or the command (" + command + ") can not be found!");
        }
    }
}

/**
 * Code used to execute commands and read the output stream back to another
 * stream, in this case, the standard output stream.
 *
 * The original code is taken from Pepe (and was edited by tvanfosson) from
 * https://stackoverflow.com/a/5437863
 *
 * @author Pepe and tvanfosson
 */
class SyncPipe implements Runnable {

    public SyncPipe(InputStream istrm, OutputStream ostrm) {
        istrm_ = istrm;
        ostrm_ = ostrm;
    }

    public void run() {
        final byte[] buffer = new byte[1024];
        try {
            for (int length = 0; (length = istrm_.read(buffer)) != -1;) {

                ostrm_.write(buffer, 0, length);
            }
        } catch (IOException ex) {
            Logger.getLogger(SyncPipe.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private final OutputStream ostrm_;
    private final InputStream istrm_;
}
