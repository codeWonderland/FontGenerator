package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import util.UFOManager;
import java.io.File;

public class WelcomeController {

    @FXML
    private Button newProject;
    @FXML
    private Button loadProject;
    private MainController mainController;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        newProject.setOnAction(e -> {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setInitialDirectory(new File(System.getProperty("user.home")));
            File file = dc.showDialog(null);

            if (file != null) {
                String path = file.getAbsolutePath();

                UFOManager.createUfo(path);
                mainController.setProjectDir(path);
            }
        });

        loadProject.setOnAction(e -> {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setInitialDirectory(new File(System.getProperty("user.home")));
            File file = dc.showDialog(null);

            if (file != null) {
                String path = file.getAbsolutePath();

                mainController.setProjectDir(path);
            }
        });
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
