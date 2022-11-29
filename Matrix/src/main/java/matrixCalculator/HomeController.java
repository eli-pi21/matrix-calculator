package matrixCalculator;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

/**
 * Manages the Home window.
 */
public class HomeController {

    @FXML
    private Button multiply;
    @FXML
    private Button subAdd;
    @FXML
    private Button determinant;
    @FXML
    private Button expression;
    @FXML
    private Button inverseMatrix;
    @FXML
    private Button rowEchelonForm;

    @FXML
    private BorderPane borderPane;

    @FXML
    private MenuItem commands;
    @FXML
    private MenuItem about;

    public static Label alert; // Label where alerts can be displayed
    public static AnchorPane anchorPane; // Pane where the label is displayed

    public static final String RESIZE_ALERT = "To work with bigger matrices, please resize the window";
    public static final String OVERFLOW_ALERT = "Overflow detected, please insert smaller numbers.";
    public static final String BUTTON_STYLE = "button";
    public static final String BUTTON_PRESSED_STYLE = "after-button";

    public void initialize() {
        anchorPane = new AnchorPane();
        alert = new Label();

        alert.getStyleClass().add("alertLabel");
        alert.setAlignment(Pos.CENTER);
        anchorPane.getChildren().add(alert);
        AnchorPane.setTopAnchor(alert, 0.0);
        AnchorPane.setBottomAnchor(alert, 0.0);
        AnchorPane.setLeftAnchor(alert, 0.0);
        AnchorPane.setRightAnchor(alert, 0.0);
        alert.setVisible(false);

        this.setButtonsOnAction();
        this.showMultiplicationPanel();
    }

    private void setButtonsOnAction() {
        this.multiply.setOnAction(e -> this.showMultiplicationPanel());
        this.subAdd.setOnAction(e -> this.showSubAddPanel());
        this.determinant.setOnAction(e -> this.showDetPanel());
        this.expression.setOnAction(e -> this.showExpressionPanel());
        this.inverseMatrix.setOnAction(e -> this.showInverseMatrixPanel());
        this.rowEchelonForm.setOnAction(e -> this.showRowEchelonFormPanel());
        this.commands.setOnAction(e -> this.showInfoPanel());
        this.about.setOnAction(e -> this.showAboutPanel());
    }

    private void showMultiplicationPanel() {
        this.resetStyleClass(this.determinant, this.inverseMatrix, this.subAdd, this.expression, this.rowEchelonForm);
        this.setPressedButtonStyleClass(this.multiply);
        this.openNewOperationPanel("fxmlFiles/Multiplication.fxml");
    }

    private void showDetPanel() {
        this.resetStyleClass(this.subAdd, this.inverseMatrix, this.multiply, this.expression, this.rowEchelonForm);
        this.setPressedButtonStyleClass(this.determinant);
        this.openNewOperationPanel("fxmlFiles/Determinant.fxml");
    }

    private void showSubAddPanel() {
        this.resetStyleClass(this.determinant, this.inverseMatrix, this.multiply, this.expression, this.rowEchelonForm);
        this.setPressedButtonStyleClass(this.subAdd);
        this.openNewOperationPanel("fxmlFiles/SubAdd.fxml");
    }

    private void showExpressionPanel() {
        this.resetStyleClass(this.determinant, this.multiply, this.subAdd, this.inverseMatrix, this.rowEchelonForm);
        this.setPressedButtonStyleClass(this.expression);
        this.openNewOperationPanel("fxmlFiles/Expression.fxml");
    }

    private void showInverseMatrixPanel() {
        this.resetStyleClass(this.determinant, this.multiply, this.subAdd, this.expression, this.rowEchelonForm);
        this.setPressedButtonStyleClass(this.inverseMatrix);
        this.openNewOperationPanel("fxmlFiles/InverseMatrix.fxml");
    }

    private void showRowEchelonFormPanel() {
        this.resetStyleClass(this.determinant, this.multiply, this.subAdd, this.expression, this.inverseMatrix);
        this.setPressedButtonStyleClass(this.rowEchelonForm);
        this.openNewOperationPanel("fxmlFiles/RowEchelonForm.fxml");
    }

    /**
     * Sets the default css style for the buttons.
     *
     * @param buttons All the operation buttons in Home.
     */
    private void resetStyleClass(Button... buttons) {
        for (Button b : buttons) {
            b.getStyleClass().clear();
            b.getStyleClass().add(BUTTON_STYLE);
        }
    }

    /**
     * Sets the css style for the pressed button.
     *
     * @param button The pressed button.
     */
    private void setPressedButtonStyleClass(Button button) {
        button.getStyleClass().clear();
        button.getStyleClass().add(BUTTON_PRESSED_STYLE);
    }

    private void showAboutPanel() {
        Main.aboutStage = new Stage();
        this.about.setDisable(true);

        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(this.getClass().getClassLoader().getResource("fxmlFiles/About.fxml")));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        Main.aboutStage.setScene(new Scene(Objects.requireNonNull(root), 600, 400));

        Main.aboutStage.setTitle("About");
        Main.aboutStage.initStyle(StageStyle.UNDECORATED);
        Main.aboutStage.setResizable(false);
        Main.aboutStage.showAndWait();
        this.about.setDisable(false);
    }

    private void showInfoPanel() {
        Main.infoStage = new Stage();
        this.commands.setDisable(true);

        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(this.getClass().getClassLoader().getResource("fxmlFiles/Info.fxml")));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        Main.infoStage.setScene(new Scene(Objects.requireNonNull(root), 720, 470));

        Main.infoStage.setTitle("Commands");
        Main.infoStage.getIcons().add(Main.icon);
        Main.infoStage.showAndWait();
        this.commands.setDisable(false);
    }

    private void openNewOperationPanel(String path) {
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(this.getClass().getClassLoader().getResource(path)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.borderPane.setCenter(root);
    }

    public static void showOverflowAlert() {
        HomeController.alert.setText(HomeController.OVERFLOW_ALERT);
        HomeController.alert.setVisible(true);
    }

    @Deprecated
    private void setNewStage(String path, Stage newStage) {
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(this.getClass().getClassLoader().getResource(path)));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        newStage.setScene(new Scene(root, 600, 400));
    }

    @Deprecated
    private static void showInvalidInputAlert() {
        HomeController.alert.setText("Invalid input, try again.");
        HomeController.alert.setVisible(true);
    }
}
