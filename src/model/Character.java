package model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
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

    public void save(String filePath) {
        try {
            //Create DOM document creator
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder  documentBuilder =  documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            //Set to indent for more legible output
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            //Create root element
            Element root =  document.createElement("glyph");
            document.appendChild(root);
            //Set the attributes of the root element
            Attr attr = document.createAttribute("Name");
            attr.setValue(mSymbol.toString());
            attr = document.createAttribute("format");
            attr.setValue("2");

            //Set advance
            Element advance = document.createElement("advance");
            root.appendChild(advance);
            attr = document.createAttribute("width");
            attr.setValue("268");
            advance.setAttributeNode(attr);

            //Set unicode
            Element unicode = document.createElement("unicode");
            root.appendChild(unicode);
            attr = document.createAttribute("hex");

            // Create hexcode element
            String hexcode;
            if(mCase == CASE.UPPERCASE)
            {
               hexcode = mSymbol.toString().toUpperCase();
            }
            else
            {
                hexcode = mSymbol.toString();
            }

            char[] ch = hexcode.toCharArray();
            int test = (int)ch[0];

            attr.setValue("00" + Integer.toHexString(test));
            unicode.setAttributeNode(attr);


            //Set guideline
            Element guideline = document.createElement("guideline");
            root.appendChild(guideline);
            attr  = document.createAttribute("y");
            attr.setValue("-12");
            guideline.setAttributeNode(attr);
            attr = document.createAttribute("name");
            attr.setValue("overshoot");
            guideline.setAttributeNode(attr);

            //Set Anchor
            Element anchor = document.createElement("anchor");
            root.appendChild(anchor);
            attr = document.createAttribute("x");
            attr.setValue("74");
            anchor.setAttributeNode(attr);
            attr = document.createAttribute("y");
            attr.setValue("197");
            anchor.setAttributeNode(attr);
            attr = document.createAttribute("top");
            attr.setValue("top");
            anchor.setAttributeNode(attr);

            //Create outline
            Element outline = document.createElement("outline");
            root.appendChild(outline);

            //For each contour
            Double tmp;
            for(int i = 0; i < mOutline.size(); i++)
            {
                //Create contour
                Element  contour = document.createElement("contour");
                outline.appendChild(contour);

                //For each point in the contour
                for (int j = 0; j <  mOutline.size(); j++)
                {
                    //Create points
                    Element point = document.createElement("point");
                    contour.appendChild(point);
                    //Set x coordinate
                    attr = document.createAttribute("x");
                    tmp = mOutline.get(i).get(j).x;
                    attr.setValue(tmp.toString());
                    point.setAttributeNode(attr);

                    //Set y coordinate
                    attr = document.createAttribute("y");
                    tmp = mOutline.get(i).get(j).y;
                    attr.setValue(tmp.toString());
                    point.setAttributeNode(attr);

                    //Set point type and smooth
                    attr = document.createAttribute("type");
                    attr.setValue("curve");
                    point.setAttributeNode(attr);
                    attr = document.createAttribute("smooth");
                    attr.setValue("yes");
                    point.setAttributeNode(attr);
                }
            }

            //Set the name
            String fileName = mCase.toString() + "_.glif";

            //Create the XML file
            DOMSource domSource = new DOMSource(document);

            StreamResult streamResult = new StreamResult(new File(filePath + fileName));

            transformer.transform(domSource,streamResult);




        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Character load(String fileName, SYMBOL symbol, CASE charCase) {
        Character character = new Character(symbol, charCase);

        // get data from file
        try {
            File inputFile = new File(fileName);

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
