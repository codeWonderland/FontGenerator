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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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

        currentChar = new Character(characterChoice.getValue(), caseChoice.getValue());
    }

    public void openChar() {
        FileChooser openFile = new FileChooser();
        openFile.setTitle("Open File");
        File file = openFile.showOpenDialog(mainController.getPrimaryStage());
        if (file != null) {
            try {
                InputStream io = new FileInputStream(file);
                Image img = new Image(io);
                gc.drawImage(img, 0, 0);
            } catch (IOException ex) {
                System.out.println("Error!");
            }
        }
    };

    private void clearChar() {
        gc.clearRect(0,0,1080, 790);
    }

    private String getFile(Character.CASE charChase, Character.SYMBOL symbol) {
        return "";
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param main reference to the main controller
     */
    void setMainApp(MainController main) {
        this.mainController = main;

        // Add observable list data to the table
        // personTable.setItems(mainApp.getPersonData());
    }
}
