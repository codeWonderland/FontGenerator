package controller;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainController extends Application {

    private BorderPane rootLayout;
    private Stage primaryStage;
    private String projectDir;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Font Generator");

        initRootLayout();

        showWelcomePane();
    }

    /**
     * Initializes the root layout.
     */
    private void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getResource("../view/Main.fxml"));
            rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the person overview inside the root layout.
     */
    private void showWelcomePane() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getResource("../view/WelcomePane.fxml"));
            AnchorPane welcome = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(welcome);

            // Give the controller access to the main app.
            WelcomeController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the person overview inside the root layout.
     */
    private void showDrawPane() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getResource("../view/DrawPane.fxml"));
            SplitPane personOverview = (SplitPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(personOverview);

            // Give the controller access to the main app.
            DrawPaneController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the main stage.
     * @return
     */
    Stage getPrimaryStage() {
        return primaryStage;
    }

    void setProjectDir(String path) {
        projectDir = path;
        showDrawPane();
    }

    String getProjectDir() {
        return projectDir;
    }

    public static void main(String[] args) {
        launch(args);
    }
}