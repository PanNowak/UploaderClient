package com.example.uploaderclient.gui.result_dialog.control;

import com.example.uploaderclient.uploader.api.entity.Statistics;

import io.reactivex.observers.DefaultObserver;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

public class DialogController extends DefaultObserver<Statistics> { //TODO reformat

    private static final String STATISTICS_INFO_PATTERN = "Number of sent objects:\t%s" +
            "\nNumber of parsing failures:\t%s\nTotal number of problems:\t%s";

    private Stage primaryStage;
    private Stage window;
    public Text titleLabel;
    public Label valuesLabel;
//    public GridPane valuesPane;
    public Button closeButton;

    private ExceptionHandler exceptionHandler;
    private boolean isFirstStatistics;

    public void initialize() {
        exceptionHandler = new ExceptionHandler();
        isFirstStatistics = true;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void closeWindow() {
        window.close();
    }

    @Override
    public void onStart() {
        window = (Stage) closeButton.getScene().getWindow();
    }

    @Override
    public void onNext(Statistics statistics) {
        titleLabel.setText("Upload is running");
        String message = String.format(STATISTICS_INFO_PATTERN, statistics.getNumberOfSuccessfullyParsedObjects(),
                statistics.getNumberOfParsingFailures(), statistics.getTotalNumberOfProblems());
        valuesLabel.setText(message);

        window.sizeToScene();
        if (isFirstStatistics) {
            setNewWindowPosition(new Coordinates(primaryStage));
            isFirstStatistics = false;
        }
    }

    @Override
    public void onError(Throwable e) {
        Coordinates recentCoordinates = getRecentCoordinates();
        String message = exceptionHandler.logExceptionAndReturnErrorMessage(e);
        titleLabel.setText("Exception occurred!");
        valuesLabel.setText(message);

        closeButton.setVisible(true);
        window.sizeToScene();
        setNewWindowPosition(recentCoordinates);
    }

    @Override
    public void onComplete() {
        Coordinates recentCoordinates = new Coordinates(window);
        titleLabel.setText("Upload ended!");

        closeButton.setVisible(true);
        window.sizeToScene();
        setNewWindowPosition(recentCoordinates);
    }

    private Coordinates getRecentCoordinates() {
        if (isFirstStatistics) {
            isFirstStatistics = false;
            return new Coordinates(primaryStage);
        } else {
            return new Coordinates(window);
        }
    }

    private void setNewWindowPosition(Coordinates coordinates) {
        window.setX(coordinates.x + coordinates.width / 2 - window.getWidth() / 2);
        window.setY(coordinates.y + coordinates.height / 2 - window.getHeight() / 2);
    }

    private static final class Coordinates {

        private final double x;
        private final double y;
        private final double width;
        private final double height;

        Coordinates(Window window) {
            this.x = window.getX();
            this.y = window.getY();
            this.width = window.getWidth();
            this.height = window.getHeight();
        }
    }
}
