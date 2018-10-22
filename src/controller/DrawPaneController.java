package controller;

import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
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
    public ChoiceBox characterChoice;
    @FXML
    public ChoiceBox caseChoice;
    @FXML
    private Slider weightSlider;
    @FXML
    private Button resetButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button exportButton;

    private GraphicsContext gc;


    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        weightSlider.setShowTickLabels(true);
        weightSlider.setShowTickMarks(true);

        characterChoice.setItems(FXCollections.observableArrayList(
                "a", "b", "c"
        ));

        characterChoice.setValue("a");

        caseChoice.setItems(FXCollections.observableArrayList(
                "uppercase", "lowercase"
        ));

        caseChoice.setValue("lowercase");

        Label line_weight = new Label("Line Weight");

        /* ----------Draw Canvas---------- */
        gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);

        canvas.setOnMousePressed(e->{
            gc.beginPath();
            gc.lineTo(e.getX(), e.getY());
        });

        canvas.setOnMouseDragged(e->{
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        });

        canvas.setOnMouseReleased(e->{
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
            gc.closePath();
        });

        // weight slider
        weightSlider.valueProperty().addListener(e->{
            double width = weightSlider.getValue();
            line_weight.setText(String.format("%.1f", width));
            gc.setLineWidth(width);
        });


        /*------- Save & Reset ------*/
        // Save
        saveButton.setOnAction((e)->{
//            FileChooser savefile = new FileChooser();
//            savefile.setTitle("Save File");
//
//            File file = savefile.showSaveDialog(primaryStage);
//            if (file != null) {
//                try {
//                    WritableImage writableImage = new WritableImage(1080, 790);
//                    canvas.snapshot(null, writableImage);
//                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
//                    ImageIO.write(renderedImage, "png", file);
//                } catch (IOException ex) {
//                    System.out.println("Error!");
//                }
//            }

        });

        // Reset
        resetButton.setOnAction((e) -> {
            gc.clearRect(0,0, 1080, 790);
        });
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

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param main
     */
    public void setMainApp(MainController main) {
        this.mainController = main;

        // Add observable list data to the table
        // personTable.setItems(mainApp.getPersonData());
    }
}
