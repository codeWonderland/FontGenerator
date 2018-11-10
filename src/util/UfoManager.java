package util;

import model.Character;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class UfoManager {
    static void createMetaInfo(String dest) throws IOException {
        String contents = "<plist version=\"1.0\">\n " +
                "<dict>\n" +
                "   <key>creator</key>\n"+
                "   <string>Font Generator</sting>\n" +
                "   <key>formatVersion</key>\n" +
                "   <integer>3</integer>\n" +
                "</dict>\n" +
                "</plist>";

        File metaData = new File(dest +"\\metainfo.plist");
        createFile(contents, metaData);

    }
    static void createLayerContents(String dest) throws IOException {
        String contents = "<plist version=\"1.0\">\n" +
                "<array>\n" +
                "   <array>\n" +
                "       <string>public.default</string>\n" +
                "       <string>glyphs</string>\n" +
                "   </array>\n" +
                "</array>\n" +
                "</plist>";

        File layerContents = new File(dest + "\\layercontents.plist");
        createFile(contents, layerContents);
    }
    static void createContents(String dest) throws IOException{
        ArrayList<String> alphabet = new ArrayList<String>();
        String letter;

        for (Character.SYMBOL s : Character.SYMBOL.values()) {
            letter = s.toString();
            letter.toUpperCase();
            alphabet.add(letter);
            letter.toLowerCase();
            alphabet.add(letter);
        }


        String contents = "<plist version=\"1.0\">\n" +
                "<dict>\n";

        for (String charater : alphabet)
            contents += "   <key>" + charater + "</key>\n" +
                    "   <string>" + charater + "_.glif</string>\n";


        contents += "</dict>\n" +
                "</plist>";

        File contentFile = new File(dest + "\\contents.plist");
        createFile(contents, contentFile);
    }
    static void createFile(String contents, File newFile) throws IOException {
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

    UfoManager(String destFolder) {
        try {
            new File(destFolder + "\\ufo").mkdir();
            new File(destFolder + "\\ufo\\glyphs").mkdir();
            new File(destFolder + "\\ufo\\images").mkdir();
            new File(destFolder + "\\ufo\\data").mkdir();
        } catch (Exception e) {
            // TODO: Check permissions and results of mkdir()s
            e.printStackTrace();
        }

        try {
            createMetaInfo(destFolder + "\\ufo");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            createLayerContents(destFolder + "\\ufo");
        } catch (IOException e){
            e.printStackTrace();
        }

        try {
            createContents(destFolder + "\\ufo" + "\\glyphs");
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

