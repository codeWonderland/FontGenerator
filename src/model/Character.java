package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.io.File;


public class Character {
    public enum CASE {
        LOWERCASE,
        UPPERCASE
    }

    public enum SYMBOL {
        a, b, c, d, e,
        f, g, h, i, j,
        k, l, m, n, o,
        p, q, r, s, t,
        u, v, w, x, y, z
    }

    public class Coordinate {
        public final double x;
        public final double y;

        Coordinate(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    

    public Character() {

    }

    public Character(SYMBOL symbol, CASE charCase) {
        this.mSymbol = symbol;
        this.mCase = charCase;

        this.mOutline = new ArrayList<List<Coordinate>>();
    }

    public void clear() {
        this.mOutline = new ArrayList<List<Coordinate>>();
        this.mCurrentContour = null;
    }

    public void save(String fileName) throws ParserConfigurationException, TransformerException {

        //Create DOM document creator
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder  documentBuilder =  documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        //Create root element
        Element root =  document.createElement("glyph");

        //create root attribute
        Attr attr = document.createAttribute("name");
        attr.setValue(mSymbol.toString());
        root.setAttributeNode(attr);

        attr = document.createAttribute("format");
        attr.setValue("2");
        root.setAttributeNode(attr);

        //create advance element
        Element advance = document.createElement("advance");

        // set width attribute
        attr = document.createAttribute("width");
        attr.setValue("268");
        advance.setAttributeNode(attr);

        // append advance to root
        root.appendChild(advance);

        //create unicode element
        Element unicode = document.createElement("unicode");

        // Create hexcode element
        String charCode;

        if(mCase == CASE.UPPERCASE) {
            charCode = mSymbol.toString().toUpperCase();

        } else {
            charCode = mSymbol.toString().toLowerCase();
        }

        int charCodeInt = (int)charCode.charAt(0);


        attr = document.createAttribute("hex");

        attr.setValue("00" + Integer.toHexString(charCodeInt));
        unicode.setAttributeNode(attr);

        root.appendChild(unicode);

        // create outline
        Element outline = document.createElement("outline");

        // for contour in outline
        for (List<Coordinate> contour : mOutline) {
            //Create contour element
            Element contourElement = document.createElement("contour");

            int i = 0;

            //For each point in the contour
            for (Coordinate point : contour) {
                //Create points
                Element pointElement = document.createElement("point");

                //Set x coordinate
                attr = document.createAttribute("x");
                attr.setValue(Double.toString(
                        point.x
                ));
                pointElement.setAttributeNode(attr);

                //Set y coordinate
                attr = document.createAttribute("y");
                attr.setValue(Double.toString(
                        point.y
                ));
                pointElement.setAttributeNode(attr);

                //Set type
                attr = document.createAttribute("type");

                if (i == 0) {
                    attr.setValue("move");

                } else {
                    attr.setValue("line");
                }


                pointElement.setAttributeNode(attr);

                // append point to contour
                contourElement.appendChild(pointElement);

                i++;
            }

            // append contour to outline
            outline.appendChild(contourElement);
        }

        // appent outline to root
        root.appendChild(outline);

        // append root to document
        document.appendChild(root);

        // create transformer
        Transformer transformer = TransformerFactory.newInstance().newTransformer();

        //Set to indent for more legible output
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        // create output stream to file
        Result output = new StreamResult(new File(fileName));

        // create input source
        Source input = new DOMSource(document);

        // transform document to file
        transformer.transform(input, output);
    }

    public static Character load(String fileName, SYMBOL symbol, CASE charCase) throws ParserConfigurationException, IOException, SAXException {
        Character character = new Character(symbol, charCase);

        // grab file stream
        File inputFile = new File(fileName);

        // check if file exists and is valid
        if(inputFile.exists() && !inputFile.isDirectory())
        {
            // create document builder
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            Element contourElement;
            NodeList contours;

            //Go through the points
            contours = doc.getElementsByTagName("contour");

            //For each contour
            for (int i = 0; i < contours.getLength(); i++)
            {

                //Get the contour
                character.openContour(); //Make new contour
                Node contourNode = contours.item(i);

                if (contourNode.getNodeType() == Node.ELEMENT_NODE)
                {

                    contourElement = (Element) contourNode;

                    //Get list of point elements from the contour
                    NodeList points = contourElement.getElementsByTagName("point");

                    for (int pointIndex = 0; pointIndex < points.getLength(); pointIndex++)
                    {
                        Node pointNode = points.item(pointIndex);

                        if (pointNode.getNodeType() == pointNode.ELEMENT_NODE) {
                            Element pointElement = (Element) pointNode;

                            // get point coordinates
                            double x = Double.parseDouble(pointElement.getAttribute("x"));
                            double y = Double.parseDouble(pointElement.getAttribute("y"));

                            // add point to character
                            character.addPoint(x, y);
                        }
                    }

                    // End of contour
                    character.closeContour();
                }
            }
        }

        // use openContour(), addPoint(), and closeContour()
        // to update the character with the loaded points

        return character;
    }

    public void openContour(double x, double y) {
        mCurrentContour = new ArrayList<Coordinate>();
        this.addPoint(x, y);
    }

    private void openContour()
    {
        mCurrentContour = new ArrayList<Coordinate>();
    }

    public void addPoint(double x, double y) {
        mCurrentContour.add(new Coordinate(x, y));
    }

    public void closeContour(double x, double y) {
        addPoint(x, y);
        mOutline.add(mCurrentContour);
    }

    private void closeContour()
    {
        mOutline.add(mCurrentContour);
    }

    public List<List<Coordinate>> getOutline() {
        return mOutline;
    }

    private SYMBOL mSymbol;
    private CASE mCase;
    private List<Coordinate> mCurrentContour;
    private List<List<Coordinate>> mOutline;
}
