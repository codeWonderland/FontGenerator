package model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import java.io.File;


public class Character {
    public static enum CASE {
        LOWERCASE,
        UPPERCASE
    }

    public static enum SYMBOL {
        a, b, c, d, e,
        f, g, h, i, j,
        k, l, m, n, o,
        p, q, r, s, t,
        u, v, w, x, y, z
    }

    public class Coordinate {
        public final double x;
        public final double y;

        public Coordinate(double x, double y) {
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

    public void save(String fileName) {
        try {
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


            //Set guideline
            Element guideline = document.createElement("guideline");

            attr  = document.createAttribute("y");
            attr.setValue("-12");
            guideline.setAttributeNode(attr);

            attr = document.createAttribute("name");
            attr.setValue("overshoot");
            guideline.setAttributeNode(attr);

            root.appendChild(guideline);

            //Set Anchor
            Element anchor = document.createElement("anchor");

            attr = document.createAttribute("x");
            attr.setValue("74");
            anchor.setAttributeNode(attr);

            attr = document.createAttribute("y");
            attr.setValue("197");
            anchor.setAttributeNode(attr);

            attr = document.createAttribute("top");
            attr.setValue("top");
            anchor.setAttributeNode(attr);

            root.appendChild(anchor);

            // create outline
            Element outline = document.createElement("outline");

            // for contour in outline
            for(int contourIndex = 0; contourIndex < mOutline.size(); contourIndex++)
            {
                //Create contour element
                Element  contour = document.createElement("contour");

                //For each point in the contour
                for (int pointIndex = 0; pointIndex <  mOutline.get(contourIndex).size(); pointIndex++)
                {
                    //Create points
                    Element point = document.createElement("point");

                    //Set x coordinate
                    attr = document.createAttribute("x");
                    attr.setValue(Double.toString(
                            mOutline.get(contourIndex).get(pointIndex).x
                    ));
                    point.setAttributeNode(attr);

                    //Set y coordinate
                    attr = document.createAttribute("y");
                    attr.setValue(Double.toString(
                            mOutline.get(contourIndex).get(pointIndex).y
                    ));
                    point.setAttributeNode(attr);

                    //Set point type and smooth
                    attr = document.createAttribute("type");
                    attr.setValue("curve");
                    point.setAttributeNode(attr);

                    attr = document.createAttribute("smooth");
                    attr.setValue("yes");
                    point.setAttributeNode(attr);

                    // append point to contour
                    contour.appendChild(point);
                }

                // append contour to outline
                outline.appendChild(contour);
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Character load(String fileName, SYMBOL symbol, CASE charCase) {
        Character character = new Character(symbol, charCase);

        // get data from file
        try {
            //Make sure the file exists first
            File inputFile = new File(fileName);
            if(inputFile.exists() && !inputFile.isDirectory())
            {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(inputFile);
                doc.getDocumentElement().normalize();

                Element eElement;
                NodeList nList;

                //Go through the points
                nList = doc.getElementsByTagName("contour");

                //For each contour
                for (int i = 0; i < nList.getLength(); i++)
                {

                    //Get the contour
                    character.openContour(); //Make new contour
                    Node nNode = nList.item(i);

                    if (nNode.getNodeType() == Node.ELEMENT_NODE)
                    {

                        eElement = (Element) nNode;
                        //Get list of point elements from the contour
                        NodeList pointList = eElement.getElementsByTagName("point");
                        for (int j = 0; j < pointList.getLength(); j++)
                        {
                            Node node = pointList.item(j);
                            if (node.getNodeType() == node.ELEMENT_NODE) {
                                Element point = (Element) node;
                                double x = Double.parseDouble(point.getAttribute("x"));
                                double y = Double.parseDouble(point.getAttribute("y"));
                                character.addPoint(x, y);;
                            }
                        }
                        // End of contour
                        character.closeContour();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // use openContour(), addPoint(), and closeContour()
        // to update the character with the loaded points

        return character;
    }

    public void openContour(double x, double y) {
        mCurrentContour = new ArrayList<Coordinate>();
        this.addPoint(x, y);
    }

    public void openContour()
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

    public void closeContour()
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
