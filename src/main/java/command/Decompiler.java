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
package command;

import apc.FileManager;
import enumeration.DecompilerType;
import java.io.File;
import java.io.IOException;
import library.Constants;
import library.ProjectInfos;
import model.ArgumentPackage;
import model.Command;
import net.lingala.zip4j.exception.ZipException;

/**
 * This class handles the decompilation of the APK with the embedded tools and
 * the flags for the command line interfaces that are provided.
 *
 * @author Max 'Libra' Kersten
 */
public class Decompiler {

    private final ArgumentPackage argumentPackage;

    public Decompiler(ArgumentPackage argumentPackage) {
        this.argumentPackage = argumentPackage;
    }

    /**
     * Decompiles the APK with the requested tool
     *
     * @throws IOException if the file handling goes wrong
     * @throws InterruptedException if the command execution is interrupted
     * @throws ZipException if an archive cannot be extracted (i.e. it is not a
     * ZIP archive)
     */
    public void decompile() throws IOException, InterruptedException, ZipException {
        //Declare local variables
        boolean isWindows = Constants.isWindows();
        String command;
        File workingDirectory;

        //Decode the APK with APKTool
        File apkOutput = new File(Constants.TEMP_LIBRARY_FOLDER);
        System.out.println("[+]Decompiling the APK to a temporary location (" + apkOutput.getAbsolutePath() + ")");
        apkOutput.mkdirs();
        /**
         * First the 'd' is provided to enable 'decoding'.
         *
         * The '-f' command is used to remove the destination folder if it
         * already exists.
         *
         * The '-s' command is used to keep the classes.dex file (instead of
         * decompiling it to smali code). This speeds up the process of decoding
         * the APK. Additionally, the classes.dex file is used by the
         * decompilers. Without the '-s' flag, the classes.dex file is not
         * present in the decoded output
         *
         * The '-o' command requires one more parameter behind it. This
         * specifies the output directory
         *
         * The '-m' command stands for "match-original" which matches files as
         * close as possible to original ones, but prevents the ability to
         * rebuild. This is irrelevant for APC users, since the decompiled Java
         * code isn't buildable in most cases
         *
         * The '-k' command allows broken resources to be decoded although the
         * project will then not be compilable. This is not a problem, since the
         * output of APC isn't fully buildable anyway.
         */
        System.out.println("[+]Calling APKTool to decompile the AndroidManifest.xml and the application resources");
        if (isWindows) {
            command = "java -jar apktool-cli-all.jar";
        } else {
            command = "java -jar ./apktool-cli-all.jar";
        }
        //Append the flags and the file paths to the commands. These are the same on any platform due to the Java runtime
        command += " d -f -s -m -k -o " + new File(Constants.TEMP_LIBRARY_FOLDER + "/apktool").getAbsolutePath() + " \"" + argumentPackage.getApk().getAbsolutePath() + "\"";

        workingDirectory = new File(Constants.APKTOOL_LIBRARY_FOLDER);
        executeCommand(DecompilerType.APKTOOL, command, workingDirectory);

        /**
         * Use APKTool again for the SMALI files, cant be done at once because
         * one either gets the 'classes.dex' file or the .smali files
         *
         * The flag '--no-assets' avoids the decoding of assets in the APK,
         * which are already extracted in the previous call of APKTool.
         *
         * The '--no-res' avoids the decoding of resources, which is also done
         * in the previous call of APKTool.
         *
         */
        System.out.println("[+]Calling APKTool to obtain the SMALI code");
        if (isWindows) {
            command = "java -jar apktool-cli-all.jar";
        } else {
            command = "java -jar ./apktool-cli-all.jar";
        }
        //Append the flags and the file paths to the commands. These are the same on any platform due to the Java runtime
        command += " d -f --no-assets --no-res -m -o " + new File(Constants.TEMP_LIBRARY_FOLDER + "/apktool-smali").getAbsolutePath() + " \"" + argumentPackage.getApk().getAbsolutePath() + "\"";

        workingDirectory = new File(Constants.APKTOOL_LIBRARY_FOLDER);
        executeCommand(DecompilerType.APKTOOL, command, workingDirectory);

        //TODO before the combine functionality is added, add a temporary "copy all classes[n].dex files to the template project's assets folder" method
        //TODO Combine classes[N].dex files into classes.dex to decompile every part of the binary, should be optional since it can exceed 64k functions. Use /Users/[name]/Library/Android/sdk/build-tools/28.0.2/lib/dx.jar com.android.dx.merge.DexMerger output.dex part1.dex part2.dex
        //Source for code: https://stackoverflow.com/questions/11257378/is-there-a-way-to-merge-two-or-more-dex-files-into-one-dex-file-using-scala
        //Convert the classes.dex to a JAR file for later use
        /**
         * Convert the classes.dex to a JAR (use the "sh" in front since the
         * script is not executable by default)
         *
         * The '-n' is used to ignore exceptions that are thrown by dex2jar
         *
         * The '-f' is used to forcefully overwrite existing files on the
         * destination location
         *
         * The '-o' is used to define the output location
         */
        if (isWindows) {
            command = "d2j-dex2jar.bat";
        } else {
            //Add extra shell here to avoid the need to chmod +x the shell script
            command = "sh ./d2j-dex2jar.sh";
        }
        //Append the flags and the file paths to the commands. These are the same on any platform due to the Java runtime
        command += " -n -f -o " + new File(Constants.TEMP_CONVERTED_JAR) + " " + new File(Constants.TEMP_LIBRARY_FOLDER + "/apktool/classes.dex").getAbsolutePath();
        workingDirectory = new File(Constants.DEX2JAR_LIBRARY_FOLDER);
        executeCommand(DecompilerType.DEX2JAR, command, workingDirectory);

        //Ensure that the output directory for the source code exists
        new File(Constants.TEMP_SOURCES_FOLDER).mkdir();

        //Handle each decompiler with different commands
        switch (argumentPackage.getDecompilerType()) {
            case FERNFLOWER:
                //TODO implement rename option
                //use -ren=1 for rename
                if (isWindows) {
                    command = "java -jar fernflower.jar";
                } else {
                    command = "java -jar ./fernflower.jar";
                }
                //Append the flags and the file paths to the commands. These are the same on any platform due to the Java runtime
                command += " " + new File(Constants.TEMP_CONVERTED_JAR) + " " + new File(Constants.TEMP_SOURCES_FOLDER).getAbsolutePath();
                workingDirectory = new File(Constants.FERNFLOWER_LIBRARY_FOLDER);
                break;
            case JADX:
                /**
                 * -d sets output dir
                 *
                 * -r avoids resources (got these with APKTool alraedy
                 *
                 * --escape-unicode to escape unicode characters
                 *
                 * --deobf to enable deobfuscation
                 *
                 * --deobf-min minimum length of the new names
                 *
                 * --deobf-max maximum length of the new names
                 */
                //TODO implement deobfuscation parameters
                if (isWindows) {
                    command = "jadx.bat";
                } else {
                    //Add extra shell here to avoid the need to chmod +x the shell script
                    command = "sh ./jadx";
                }
                //Append the flags and the file paths to the commands. These are the same on any platform due to the Java runtime
                command += " -r --escape-unicode -d " + new File(Constants.TEMP_LIBRARY_FOLDER).getAbsolutePath() + " " + new File(Constants.TEMP_CONVERTED_JAR);
                workingDirectory = new File(Constants.JADX_LIBRARY_FOLDER);
                break;
            case JDCMD:
                /**
                 *
                 * '-od' specifies the output directory
                 */
                if (isWindows) {
                    command = "java -jar jd-cli.jar";
                } else {
                    command = "java -jar ./jd-cli.jar";
                }
                //Append the flags and the file paths to the commands. These are the same on any platform due to the Java runtime
                command += " -od " + new File(Constants.TEMP_SOURCES_FOLDER).getAbsolutePath() + " " + new File(Constants.TEMP_CONVERTED_JAR).getAbsolutePath();
                workingDirectory = new File(Constants.JDCMD_LIBRARY_FOLDER);
                break;
            case CFR:
                //The file name of the CFR decompiler JAR, which contains the version number
                String cfrFileName = "";
                //Get the folder where the build is located
                File cfrLibraryFolder = new File(Constants.CFR_LIBRARY_FOLDER);
                //Iterate through all files in the folder to find the correct JAR
                for (File file : cfrLibraryFolder.listFiles()) {
                    //Even though only files are listed, this measure is added as an additional security layer
                    if (file.isDirectory()) {
                        continue;
                    }
                    //If the name of the iterated file in the build output directory contains the partial output name of CFR, the name is taken
                    //Both are in lower case to avoid errors when the casing is changed in the build output
                    if (file.getName().toLowerCase().contains(ProjectInfos.getCfr().getPartialOutputName().toLowerCase())) {
                        cfrFileName = file.getName();
                    }
                }
                /**
                 * --outputdir [the output directory]
                 *
                 * --aexagg true To remove nested exception handles which have
                 * the same semantics
                 *
                 */
                if (isWindows) {
                    command = "java -jar " + cfrFileName;
                } else {
                    command = "java -jar " + cfrFileName;
                }
                //Append the flags and the file paths to the commands. These are the same on any platform due to the Java runtime
                command += " " + new File(Constants.TEMP_CONVERTED_JAR).getAbsolutePath() + " --aexagg true --outputdir " + new File(Constants.TEMP_SOURCES_FOLDER).getAbsolutePath();
                workingDirectory = new File(Constants.CFR_LIBRARY_FOLDER);
                break;
            case PROCYON:
                /**
                 * -ci collapses multiple imports from the same package into a
                 * wildcard import
                 *
                 * -eml for eager loading
                 *
                 * -o [dir] for output
                 */
                if (isWindows) {
                    command = "java -jar procyon-decompiler-0.5.30.jar";
                } else {
                    command = "java -jar ./procyon-decompiler-0.5.30.jar";
                }
                //Append the flags and the file paths to the commands. These are the same on any platform due to the Java runtime
                command += " -ci -eml --jar-file " + new File(Constants.TEMP_CONVERTED_JAR).getAbsolutePath() + " -o " + new File(Constants.TEMP_SOURCES_FOLDER).getAbsolutePath();
                workingDirectory = new File(Constants.PROCYON_LIBRARY_FOLDER);
                break;
            case JEB3:
                /**
                 * No parameters are required besides the path to the file and
                 * the output directory (in that order)
                 *
                 * JEB decompile command:
                 *
                 * java -jar ./jeb.jar --srv2=DecompileAndroid.py -- FILE
                 * OUTPUT_DIR
                 */
                if (isWindows) {
                    command = "java -jar bin/app/jeb.jar";
                } else {
                    command = "java -jar ./bin/app/jeb.jar";
                }
                command += " --srv2 --script=" + new File(Constants.JEB3_CLI_ANDROID_SCRIPT_LIBRARY_FOLDER + "/DecompileAndroid.py").getAbsolutePath() + " -- " + argumentPackage.getApk().getAbsolutePath() + " " + new File(Constants.TEMP_SOURCES_FOLDER).getAbsolutePath();
                workingDirectory = argumentPackage.getJeb3Folder();
        }
        executeCommand(argumentPackage.getDecompilerType(), command, workingDirectory);
    }

    /**
     * Executes the command based on the previously entered command within the
     * given working directory
     *
     * @param name the name of the tool that is executed
     * @param commandString the command that is executed
     * @param workingDirectory the working directory in which the command is
     * executed
     * @throws IOException if something goes wrong with file handling
     * @throws InterruptedException if the command is interrupted, although this
     * should never happen
     * @throws ZipException if an archive cannot be extracted
     */
    private void executeCommand(DecompilerType name, String commandString, File workingDirectory) throws IOException, InterruptedException, ZipException {
        Command command = new Command(commandString, workingDirectory);
        System.out.println("[+]Decompling JAR with " + name);
        try {
            command.execute();
            if (name.equals(DecompilerType.FERNFLOWER)) {
                new FileManager().extractArchive(new File(Constants.TEMP_SOURCES_FOLDER + "/output.jar").getAbsolutePath(), new File(Constants.TEMP_SOURCES_FOLDER).getAbsolutePath());
                new FileManager().delete(new File(Constants.TEMP_SOURCES_FOLDER + "/output.jar"));
            } else if (name.equals(DecompilerType.JEB3)) {
                /**
                 * If the JEB3 output folder exists, copy the contents to the
                 * TEMP_SOURCES_FOLDER and remove the JEB3 output folder. This
                 * way, the rest file handling can stay the same.
                 */
                File sourceLocation = new File(Constants.TEMP_SOURCES_FOLDER);
                File jeb3Output = new File(sourceLocation.getAbsolutePath() + "/Bytecode_decompiled");
                if (jeb3Output.exists() && jeb3Output.isDirectory()) {
                    for (File currentFileObject : sourceLocation.listFiles()) {
                        new FileManager().copyFolder(currentFileObject, sourceLocation);
                    }
                    new FileManager().delete(jeb3Output);
                }
            }
            System.out.println("[+]Decompilation finished");
        } catch (IOException ex) {
            throw new IOException("Something went wrong with the I/O during the decompilation. Check the permissions of the output directory and try again.");
        }
    }
}
