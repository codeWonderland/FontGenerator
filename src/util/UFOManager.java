package util;

import model.Character;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static java.nio.file.StandardCopyOption.*;

public class UFOManager {
    public static void createUfo(String destFolder) {
        if (!new File(destFolder + "/glyphs").mkdir()) {
            System.err.println("Issue creating /glyphs folder");
        }

        createMetaInfo(destFolder);
        createLayerContents(destFolder);
        createFontInfo(destFolder);
        createContents(destFolder + "/glyphs");
    }

    public static void exportUfo(String destFolder) {
        try {
            boolean isWindows = System.getProperty("os.name")
                    .toLowerCase().startsWith("windows");

            String command = String.format("fontmake -u %s", destFolder);

            if (isWindows) {
                command = String.format("cmd /C %s", command);
            } else {
                command = String.format("sh -c %s", command);
            }

            Runtime r = Runtime.getRuntime();
            Process p = r.exec(command);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes in a destination folder for a ufo project,
     * makes sure it is a valid folder path, and also
     * ensures that it ends in .ufo
     * */
    public static String formatUfoDir(String destFolder) {
        File ufoDir = new File(destFolder);

        if (!ufoDir.isDirectory()) {
            // if the path given is not a valid directory we print an error
            System.err.println("No project at designated path, " + destFolder);
            return null;

        } else if (!destFolder.endsWith(".ufo")) {
            // if the destination folder does not end in .ufo then we fix that
            Path source = Paths.get(ufoDir.getAbsolutePath());
            Path dest = Paths.get(ufoDir.getAbsolutePath() + ".ufo");

            try {
                // move files to new location with .ufo appended
                Files.move(source, dest, REPLACE_EXISTING);

            } catch (IOException e) {
                System.err.println("Issue appending .ufo to project folder");
            }

            return ufoDir.getAbsolutePath() + ".ufo";

        }

        // if nothing needs to be done we just return the dest folder
        return ufoDir.getAbsolutePath();
    }

    private static void createFontInfo(String destFolder) {
        String[] tmp = destFolder.split("/");

        String family = tmp[tmp.length - 1];

        tmp = family.split(".ufo");

        family = tmp[0];

        String contents = "<plist version=\"1.0\">\n" +
                "   <dict>\n" +
                "       <key>unitsPerEm</key>\n" +
                "       <integer>256</integer>\n" +
                "       <key>ascender</key>\n" +
                "       <integer>256</integer>\n" +
                "       <key>descender</key>\n" +
                "       <integer>-256</integer>\n" +
                "       <key>familyName</key>\n" +
                "       <string>" + family + "</string>\n" +
                "       <key>xHeight</key>\n" +
                "       <integer>128</integer>\n" +
                "       <key>capHeight</key>\n" +
                "       <integer>256</integer>\n" +
                "   </dict>\n" +
                "</plist>";

        File fontInfo = new File(destFolder + "/fontinfo.plist");

        createFile(contents, fontInfo);
    }

    private static void createMetaInfo(String dest) {
        String contents = "<plist version=\"1.0\">\n " +
                "<dict>\n" +
                "   <key>creator</key>\n"+
                "   <string>Font Generator</string>\n" +
                "   <key>formatVersion</key>\n" +
                "   <integer>3</integer>\n" +
                "</dict>\n" +
                "</plist>";

        File metaData = new File(dest +"/metainfo.plist");

        createFile(contents, metaData);
    }

    private static void createLayerContents(String dest) {
        String contents = "<plist version=\"1.0\">\n" +
                "<array>\n" +
                "   <array>\n" +
                "       <string>public.default</string>\n" +
                "       <string>glyphs</string>\n" +
                "   </array>\n" +
                "</array>\n" +
                "</plist>";

        File layerContents = new File(dest + "/layercontents.plist");

        createFile(contents, layerContents);
    }

    private static void createContents(String dest) {
        Character.SYMBOL[] alphabet = Character.SYMBOL.values();
        String letter = "";

        StringBuilder contents = new StringBuilder(
                "<plist version=\"1.0\">\n" +
                "<dict>\n"
        );

        for (Character.SYMBOL character : alphabet) {
            letter = character.toString().toLowerCase();

            contents.append("   <key>")
                    .append(letter)
                    .append("</key>\n")
                    .append("   <string>")
                    .append(letter)
                    .append("_lower.glif</string>\n");

            contents.append("   <key>")
                    .append(letter.toUpperCase())
                    .append("</key>\n")
                    .append("   <string>")
                    .append(letter)
                    .append("_upper.glif</string>\n");

        }


        contents.append("</dict>\n" + "</plist>");

        File contentFile = new File(dest + "/contents.plist");

        createFile(contents.toString(), contentFile);
    }

    private static void createFile(String contents, File newFile) {
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\"\n" +
                "\"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n";

        try {

            if(newFile.createNewFile()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
                writer.write(header + contents);
                writer.close();

            }
        } catch (IOException e) {
            System.out.println("issue creating file: " + newFile.getAbsolutePath());
        }
    }
}

