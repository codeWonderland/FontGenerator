package util;

import model.Character;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class UFOManager {
    public static void createUfo(String destFolder) {
        try {
            System.out.println(destFolder);
            new File(destFolder + "/glyphs").mkdir();
            new File(destFolder + "/images").mkdir();
            new File(destFolder + "/data").mkdir();
        } catch (Exception e) {
            // TODO: Check permissions and results of mkdir()s
            e.printStackTrace();
        }

        try {
            createMetaInfo(destFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            createLayerContents(destFolder);
        } catch (IOException e){
            e.printStackTrace();
        }

        try {
            createContents(destFolder + "/glyphs");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void exportUfo(String destFolder) {

    }

    public static String formatUfoDir(String destFolder) {
        File ufoDir = new File(destFolder);

        if (!ufoDir.isDirectory()) {
            System.out.println("No project at designated path, " + destFolder);
            return null;

        } else if (!destFolder.endsWith(".ufo")) {
            File newDir = new File(ufoDir.getParent() + "/" + ufoDir + ".ufo");
            ufoDir.renameTo(newDir);

            return newDir.getAbsolutePath();

        } else {
            return destFolder;
        }
    }

    private static void createMetaInfo(String dest) throws IOException {
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

    private static void createLayerContents(String dest) throws IOException {
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

    private static void createContents(String dest) throws IOException {
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

    private static void createFile(String contents, File newFile) throws IOException {
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\"\n" +
                "\"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n";

        if(newFile.createNewFile()) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
            writer.write(header + contents);
            writer.close();
        }
        else
            throw new IOException();
    }
}

