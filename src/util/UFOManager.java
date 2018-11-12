package util;

import model.Character;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class UFOManager {
    public static void createUfo(String destFolder) {
        if (!new File(destFolder + "/glyphs").mkdir()) {
            System.err.println("Issue creating /glyphs folder");
        }

        if (!new File(destFolder + "/images").mkdir()) {
            System.err.println("Issue creating /images folder");
        }

        if (new File(destFolder + "/data").mkdir()) {
            System.err.println("Issue creating /data folder");
        }

        createMetaInfo(destFolder);
        createLayerContents(destFolder);
        createContents(destFolder + "/glyphs");
    }

    public static void exportUfo(String destFolder) {

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
            File newDir = new File(ufoDir.getParent() + "/" + ufoDir + ".ufo");

            if (ufoDir.renameTo(newDir)) {
                // TODO: Determine why renaming isn't working
                return ufoDir.getAbsolutePath();
            }

        }

        // if nothing needs to be done we just return the dest folder
        return ufoDir.getAbsolutePath();
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
        ArrayList<String> alphabet = new ArrayList<>();
        String letter;

        for (Character.SYMBOL s : Character.SYMBOL.values()) {
            letter = s.toString().toUpperCase();

            alphabet.add(letter);
            alphabet.add(letter.toLowerCase());
        }


        StringBuilder contents = new StringBuilder(
                "<plist version=\"1.0\">\n" +
                "<dict>\n"
        );

        for (String charater : alphabet)
            contents.append("   <key>")
                    .append(charater)
                    .append("</key>\n")
                    .append("   <string>")
                    .append(charater)
                    .append("_.glif</string>\n");


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

