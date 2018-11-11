package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import model.Character;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DrawPaneController {

    // Reference to the main application.
    private MainController mainController;

    @FXML
    private Canvas canvas;
    @FXML
    public ChoiceBox<Character.SYMBOL> characterChoice;
    @FXML
    public ChoiceBox<Character.CASE> caseChoice;
    @FXML
    private Slider weightSlider;
    @FXML
    private Button resetButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button exportButton;

    private GraphicsContext gc;

    private Character currentChar;


    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        weightSlider.setShowTickLabels(true);
        weightSlider.setShowTickMarks(true);

        // character select
        characterChoice.setItems(FXCollections.observableArrayList(
                Character.SYMBOL.values()
        ));

        characterChoice.setValue(Character.SYMBOL.a);

        characterChoice.getSelectionModel()
                .selectedIndexProperty()
                .addListener((observableValue, currentValue, newValue) -> {
                    Character.CASE charCase = caseChoice.getValue();
                    Character.SYMBOL charSymbolOld = characterChoice.getValue();
                    Character.SYMBOL charSymbolNew = Character.SYMBOL.values()[(int) newValue];

                    String fileName = getFile(
                            charCase,
                            charSymbolOld
                    );

                    try {
                        currentChar.save(fileName);

                    } catch (ParserConfigurationException | TransformerException e) {
                        e.printStackTrace();
                    }
                    clearChar();

                    try {
                        currentChar = Character.load(
                                getFile(
                                        charCase,
                                        charSymbolNew
                                ),
                                charSymbolNew,
                                charCase
                        );

                    } catch (ParserConfigurationException | SAXException | IOException e) {
                        e.printStackTrace();
                    }

                    paintChar();
                });

        // case select
        caseChoice.setItems(FXCollections.observableArrayList(
                model.Character.CASE.values()
        ));

        caseChoice.setValue(Character.CASE.LOWERCASE);

        caseChoice.getSelectionModel()
                .selectedIndexProperty()
                .addListener((observableValue, currentValue, newValue) -> {
                    Character.CASE charCaseOld = caseChoice.getValue();
                    Character.CASE charCaseNew = Character.CASE.values()[(int) newValue];
                    Character.SYMBOL charSymbol = characterChoice.getValue();

                    String fileName = getFile(
                            charCaseOld,
                            charSymbol
                    );

                    try {
                        currentChar.save(fileName);

                    } catch (ParserConfigurationException | TransformerException e) {
                        e.printStackTrace();
                    }
                    clearChar();

                    try {
                        currentChar = Character.load(
                                getFile(
                                        charCaseNew,
                                        charSymbol
                                ),
                                charSymbol,
                                charCaseNew
                        );

                    } catch (ParserConfigurationException | IOException | SAXException e) {
                        e.printStackTrace();
                    }

                    paintChar();
                });

        Label line_weight = new Label("Line Weight");

        /* ----------Draw Canvas---------- */
        gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);

        canvas.setOnMousePressed(e->{
            double x = e.getX();
            double y = e.getY();

            gc.beginPath();
            gc.lineTo(x, y);

            currentChar.openContour(x, y);
        });

        canvas.setOnMouseDragged(e->{
            double x = e.getX();
            double y = e.getY();

            gc.lineTo(x, y);
            gc.stroke();

            currentChar.addPoint(x, y);
        });

        canvas.setOnMouseReleased(e->{
            double x = e.getX();
            double y = e.getY();

            gc.lineTo(x, y);
            gc.stroke();
            gc.closePath();

            currentChar.closeContour(x, y);
        });

        // weight slider
        weightSlider.valueProperty().addListener(e->{
            double width = weightSlider.getValue();
            line_weight.setText(String.format("%.1f", width));
            gc.setLineWidth(width);
        });

        // Save
        saveButton.setOnAction(e -> {
            String fileName = getFile(
                    caseChoice.getValue(),
                    characterChoice.getValue()
            );

            try {
                currentChar.save(fileName);

            } catch (ParserConfigurationException | TransformerException e1) {
                e1.printStackTrace();
            }
        });

        // Reset
        resetButton.setOnAction((e) -> {
            clearChar();
        });
    }

    void start() {
        try {
            currentChar = Character.load(
                    getFile(
                            caseChoice.getValue(),
                            characterChoice.getValue()
                    ),
                    characterChoice.getValue(),
                    caseChoice.getValue()
            );

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        paintChar();
    }

    private void paintChar() {
        int index = 0;

        resetButton.getOnMouseClicked();

        for (List<Character.Coordinate> contour : currentChar.getOutline()) {
            for (Character.Coordinate coordinate: contour) {
                if (index == 0) {
                    gc.beginPath();
                    gc.lineTo(coordinate.x, coordinate.y);

                    index++;

                } else if (index == contour.size() - 1) {
                    gc.lineTo(coordinate.x, coordinate.y);
                    gc.stroke();
                    gc.closePath();

                    index = 0;

                } else {
                    gc.lineTo(coordinate.x, coordinate.y);
                    gc.stroke();

                    index++;
                }
            }
        }
    }

    private void clearChar() {
        gc.clearRect(0,0,1080, 790);
    }

    /**
     * gets glyph file location based off of project directory and char info
     *
     * @param charCase the current case
     * @param symbol the current character
     */
    @NotNull
    private String getFile(Character.CASE charCase, Character.SYMBOL symbol) {
        String filePath = mainController.getProjectDir() + "/glyphs/";

        if (charCase == Character.CASE.LOWERCASE) {
            filePath += symbol.name().toLowerCase() + "_.glif";

        } else {
            filePath += symbol.name().toUpperCase() + "_.glif";
        }

        return filePath;
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param main reference to the main controller
     */
    void setMainApp(MainController main) {
        this.mainController = main;
    }
}
