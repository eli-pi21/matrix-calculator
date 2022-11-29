package matrixCalculator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * The application implements a matrix calculator which provides basic and advanced computing.
 * <p>
 * The user can compute matrix addition, subtraction, multiplication, reduce a matrix to its row echelon form and compute its rank.
 * An additional feature is to compute a complex expression with two matrices, where the input accept also scalars and the basic operations.
 *
 * @author Elisa Pioldi
 */

public class Main extends Application {

    public static Stage homeStage;
    public static Stage infoStage;
    public static Stage aboutStage;
    public static Image icon;
    public static final String ICON_STAGE_PATH = "/icons/MatrixIcon.png";

    @Override
    public void start(Stage stage) throws IOException {

        icon = new Image(ICON_STAGE_PATH);
        homeStage = new Stage();

        Parent root = FXMLLoader.load(Objects.requireNonNull(this.getClass().getClassLoader().getResource("fxmlFiles/Home.fxml")));
        homeStage.setScene(new Scene(root, 915, 638));
        homeStage.setTitle("Matrix Calculator");
        homeStage.setMinHeight(638);
        homeStage.setMinWidth(915);
        homeStage.getIcons().add(icon);

        homeStage.setOnCloseRequest(e -> {
            try {
                e.consume();
                this.closeRequest();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        homeStage.show();
    }

    public void closeRequest() throws IOException {

        Stage close = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(this.getClass().getClassLoader().getResource("fxmlFiles/Close.fxml")));
        close.setScene(new Scene(root, 350, 150));
        close.setResizable(false);
        close.getIcons().add(new Image(ICON_STAGE_PATH));

        close.initModality(Modality.WINDOW_MODAL);
        close.initOwner(Main.homeStage);
        close.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
