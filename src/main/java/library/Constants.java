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

import apc.AndroidProjectCreator;
import java.io.File;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Within this class, constant values are declared. To keep an overview and
 * allow changes in the program without altering multiple classes and various
 * variables, the variables are stored in one static class.
 *
 * An example of an advantage would be a change in the location of the library
 * folder. In this class, the field <code>LIBRARY_FOLDER_NAME</code> is contains
 * the name of this folder. Changing it, will change the name within the whole
 * program, without any additional changes.
 *
 * @author Max 'Libra' Kersten
 */
public class Constants {

    /**
     * Obtain the base location of this instance of APC
     *
     * @return the folder in which the JAR resides
     */
    public static final String getProgramBase() {
        try {
            return new File(AndroidProjectCreator.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getAbsolutePath();
        } catch (URISyntaxException ex) {
            //Since this is a constant which is used in nearly every other constant, it may not fail. This is therefore also disconnected from the main error handling try-catch clause, which is located in the ArgumentManager.
            System.out.println("[+]An error occurred upon requesting the location of this JAR. Please make sure the permissions are correctly set and try again.");
            System.exit(1);
            //Unreachable code to avoid compiler issues
            return null;
        }
    }

    /**
     * The name of the library folder, which resides in the same folder as this
     * JAR
     */
    private static final String LIBRARY_FOLDER_NAME = "/library";

    /**
     * Gets the complete path of the library folder, which resides in the same
     * directory as this JAR resides in.
     */
    public static final String LIBRARY_FOLDER = getProgramBase() + LIBRARY_FOLDER_NAME;

    /**
     * The name of the build output
     */
    public static final String BUILD_OUTPUT = "buildOutput";

    /**
     * The temporary folder, which is used during the decompilation process.
     * This folder is unique during each execution of the program using the Java
     * UUID functionality. After the execution is done (during the cleaning),
     * the folder is removed but the folder named "temp" still exists, since
     * multiple instances of AndroidProjectCreator can run at the same time.
     */
    public static final String TEMP_LIBRARY_FOLDER = LIBRARY_FOLDER + "/temp" + "/" + UUID.randomUUID().toString();

    /**
     * The location of JAR, which was created by convertingthe classes.dex with
     * Dex2Jar and placed within the temporary library folder.
     */
    public static final String TEMP_CONVERTED_JAR = TEMP_LIBRARY_FOLDER + "/output.jar";

    /**
     * The location of the template project, which is used until the assembly
     * process is completed. After that, it is copied to the user-specified
     * output location and deleted
     */
    public static final String TEMP_TEMPLATE_FOLDER = TEMP_LIBRARY_FOLDER + "/ap";

    /**
     * The main folder within the source part of the template project. This
     * variable is used multiple times and only used within the Constants class.
     */
    private static final String TEMP_TEMPLATE_APP_SOURCE_MAIN_FOLDER = TEMP_TEMPLATE_FOLDER + "/app/src/main";

    /**
     * The location of the source code within the template project
     */
    public static final String TEMP_TEMPLATE_SOURCE = TEMP_TEMPLATE_APP_SOURCE_MAIN_FOLDER + "/java";

    /**
     * The location of the AndroidManifest file within the template project
     */
    public static final String TEMP_TEMPLATE_MANIFEST_FILE = TEMP_TEMPLATE_APP_SOURCE_MAIN_FOLDER + "/AndroidManifest.xml";

    /**
     * The location of the resources folder within the template project
     */
    public static final String TEMP_TEMPLATE_RESOURCES_FOLDER = TEMP_TEMPLATE_APP_SOURCE_MAIN_FOLDER + "/res";

    /**
     * The location of the assets within the resources folder of the template
     * project
     */
    public static final String TEMP_TEMPLATE_ASSET_FOLDER = TEMP_TEMPLATE_RESOURCES_FOLDER + "/assets";

    /**
     * The location of the SMALI folder within the resources folder of the
     * template project
     */
    public static final String TEMP_TEMPLATE_SMALI_FOLDER = TEMP_TEMPLATE_SOURCE + "/smali";

    /**
     * The location of the libraries within the template project
     */
    public static final String TEMP_TEMPLATE_LIBS_FOLDER = TEMP_TEMPLATE_SOURCE + "/libraries";

    /**
     * The temporary folder used to store the decompiled source code
     */
    public static final String TEMP_SOURCES_FOLDER = TEMP_LIBRARY_FOLDER + "/sources";

    /**
     * The location of the resources folder within the temporary folder
     */
    public static final String TEMP_RESOURCES_FOLDER = TEMP_LIBRARY_FOLDER + "/apktool/res";

    /**
     * The location of the manifest file in the temporary folder
     */
    public static final String TEMP_MANIFEST_FILE = TEMP_LIBRARY_FOLDER + "/apktool/AndroidManifest.xml";

    /**
     * The location of the libraries in the temporary folder
     */
    public static final String TEMP_LIB_FOLDER = TEMP_LIBRARY_FOLDER + "/apktool/lib";

    /**
     * The location of the Smali files in the temporary folder
     */
    public static final String TEMP_SMALI_FOLDER = TEMP_LIBRARY_FOLDER + "/apktool-smali/smali";

    /**
     * The location of the assets in the temporary folder
     */
    public static final String TEMP_ASSET_FOLDER = TEMP_LIBRARY_FOLDER + "/apktool/assets";
    /**
     * The location of APKTool within the library
     */
    public static final String APKTOOL_LIBRARY_FOLDER = LIBRARY_FOLDER + "/apktool";

    /**
     * The location of Dex2Jar within the library
     */
    public static final String DEX2JAR_LIBRARY_FOLDER = LIBRARY_FOLDER + "/dex2jar";

    /**
     * The location of Fernflower within the library
     */
    public static final String FERNFLOWER_LIBRARY_FOLDER = LIBRARY_FOLDER + "/fernflower";

    /**
     * The location of JadX within the library
     */
    public static final String JADX_LIBRARY_FOLDER = LIBRARY_FOLDER + "/jadx/bin";

    /**
     * The location of JD-CMD within the library
     */
    public static final String JDCMD_LIBRARY_FOLDER = LIBRARY_FOLDER + "/jdcmd";

    /**
     * The location of the Android Studio template project within the library
     */
    public static final String ANDROIDPROJECT_LIBRARY_FOLDER = LIBRARY_FOLDER + "/androidproject";

    /**
     * The location of the JEB3 CLI Android Decompiler script within the library
     */
    public static final String JEB3_CLI_ANDROID_SCRIPT_LIBRARY_FOLDER = LIBRARY_FOLDER + "/jeb3";
    /**
     * The location of CFR within the library
     */
    public static final String CFR_LIBRARY_FOLDER = LIBRARY_FOLDER + "/cfr";

    /**
     * The location of Procyon within the library
     */
    public static final String PROCYON_LIBRARY_FOLDER = LIBRARY_FOLDER + "/procyon";
    /**
     * The repository folder contains all cloned repositories during the
     * installation
     */
    public static final String REPOSITORY_FOLDER = LIBRARY_FOLDER + "/repos";

    /**
     * The location of the Dex2Jar repository on the disk
     */
    public static final String DEX2JAR_REPOSITORY_FOLDER = REPOSITORY_FOLDER + "/dex2jar";

    /**
     * The location of the JadX repository on the disk
     */
    public static final String JADX_REPOSITORY_FOLDER = REPOSITORY_FOLDER + "/jadx";

    /**
     * The location of the JD-CMD repository on the disk
     */
    public static final String JDCMD_REPOSITORY_FOLDER = REPOSITORY_FOLDER + "/jdcmd";

    /**
     * The location of the Fernflower repository on the disk
     */
    public static final String FERNFLOWER_REPOSITORY_FOLDER = REPOSITORY_FOLDER + "/fernflower";

    /**
     * The location of the APKTool repository on the disk
     */
    public static final String APKTOOL_REPOSITORY_FOLDER = REPOSITORY_FOLDER + "/apktool";

    /**
     * The location of the JEB3 CLI Android Decompiler script repository
     */
    public static final String JEB3_CLI_ANDROID_SCRIPT_REPOSITORY_FOLDER = REPOSITORY_FOLDER + "/jeb3";

    /**
     * The location of the Android Studio template repository on the disk
     */
    public static final String ANDROIDPROJECT_REPOSITORY_FOLDER = REPOSITORY_FOLDER + "/androidproject";

    /**
     * The location of the pre-built CFR decompiler repository on the disk
     */
    public static final String CFR_REPOSITORY_FOLDER = REPOSITORY_FOLDER + "/cfr";

    /**
     * The location of the pre-built ProCyon decompiler on the disk
     */
    public static final String PROCYON_REPOSITORY_FOLDER = REPOSITORY_FOLDER + "/procyon";
}
