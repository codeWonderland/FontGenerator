package controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
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
    private Button resetButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button exportButton;


    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        Slider slider = new Slider(1, 5, 1);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);

        Label line_weight = new Label("Line Weight");

        Button[] basicArr = {resetButton, saveButton, exportButton};

        for(Button btn : basicArr) {
            btn.setMinWidth(90);
            btn.setCursor(Cursor.HAND);
            btn.setTextFill(Color.WHITE);
            btn.setStyle("-fx-background-color: #80334d;");
        }

        VBox btns = new VBox(10);
        btns.getChildren().addAll(line_weight, slider, resetButton, exportButton, saveButton);
        btns.setPadding(new Insets(5));
        btns.setStyle("-fx-background-color: #999");
        btns.setPrefWidth(100);

        /* ----------Draw Canvas---------- */
        Canvas canvas = new Canvas(1080, 790);
        GraphicsContext gc;
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

        // slider
        slider.valueProperty().addListener(e->{
            double width = slider.getValue();
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
//        FileChooser openFile = new FileChooser();
//        openFile.setTitle("Open File");
//        File file = openFile.showOpenDialog(primaryStage);
//        if (file != null) {
//            try {
//                InputStream io = new FileInputStream(file);
//                Image img = new Image(io);
//                gc.drawImage(img, 0, 0);
//            } catch (IOException ex) {
//                System.out.println("Error!");
//            }
//        }
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
