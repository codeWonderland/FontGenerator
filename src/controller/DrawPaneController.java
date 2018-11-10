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
                    String fileName = getFile(
                            caseChoice.getValue(),
                            Character.SYMBOL.values()[(int) newValue]
                    );

                    currentChar.save(fileName);
                    clearChar();

                    /* TODO: on character choice change
                        - load new character data
                     */
                });

        // case select
        caseChoice.setItems(FXCollections.observableArrayList(
                model.Character.CASE.values()
        ));

        caseChoice.setValue(Character.CASE.LOWERCASE);

        caseChoice.getSelectionModel()
                .selectedIndexProperty()
                .addListener((observableValue, currentValue, newValue) -> {
                    String fileName = getFile(
                            Character.CASE.values()[(int) newValue],
                            characterChoice.getValue()
                    );

                    currentChar.save(fileName);
                    clearChar();

                    /* TODO: on case choice change
                        - load new character data
                     */
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

            currentChar.save(fileName);
        });

        // Reset
        resetButton.setOnAction((e) -> {
            clearChar();
        });

        currentChar = Character.load(
                getFile(
                        caseChoice.getValue(),
                        characterChoice.getValue()
                ),
                characterChoice.getValue(),
                caseChoice.getValue()
        );

        paintChar();
    }

    private void paintChar() {
        int index = 0;

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
        String fileName;

        if (charCase == Character.CASE.LOWERCASE) {
            fileName = symbol.name().toLowerCase() + "_.glif";

        } else {
            fileName = symbol.name().toUpperCase() + "_.glif";
        }

        return mainController.getProjectDir() + fileName;
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
