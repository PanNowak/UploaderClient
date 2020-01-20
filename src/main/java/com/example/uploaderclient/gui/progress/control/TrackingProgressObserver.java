package com.example.uploaderclient.gui.progress.control;

import com.example.uploaderclient.api.entity.Statistics;

import io.reactivex.observers.DefaultObserver;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.util.concurrent.atomic.AtomicInteger;

public class TrackingProgressObserver extends DefaultObserver<Statistics> {

    private VBox windowPane;
    private ProgressIndicator progressIndicator;

    private Alert dynamicAlert;
    private Label dynamicLabel;

    public TrackingProgressObserver(VBox windowPane, ProgressIndicator progressIndicator) {
        this.windowPane = windowPane;
        this.progressIndicator = progressIndicator;
        this.dynamicAlert = getDynamicAlert();
        this.dynamicLabel = getDynamicLabel();
        dynamicAlert.getDialogPane().setContent(dynamicLabel);
    }

    @Override
    public void onStart() {
        windowPane.setDisable(true);
        progressIndicator.setVisible(true);
        dynamicAlert.show();
    }

    private AtomicInteger integer = new AtomicInteger();

    @Override
    public void onNext(Statistics statistics) {
        dynamicLabel.setText(statistics.toString());
        if (integer.incrementAndGet() == 10) {
            cancel();
            System.out.println("Anulowano");

            windowPane.setDisable(false);
            progressIndicator.setVisible(false);

            dynamicLabel.setText("Upload canceled!\n" + dynamicLabel.getText());
        }
    }

    @Override
    public void onError(Throwable e) {
        dynamicAlert.close();

        windowPane.setDisable(false);
        progressIndicator.setVisible(false);

        handleException(e);
    }

    @Override
    public void onComplete() {
        windowPane.setDisable(false);
        progressIndicator.setVisible(false);

        dynamicLabel.setText("Upload ended!\n" + dynamicLabel.getText());
    }

    private void handleException(Throwable throwable) { //TODO osobna klasa rozróżniająca różne typy błędów
        Throwable original = throwable;
        while (original.getCause() != null) {
            original = original.getCause();
        }
        getStaticAlert(AlertType.ERROR, throwable + " -> " + original).show();

        throwable.printStackTrace();
    }

    private Alert getStaticAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setResizable(false);
        alert.getDialogPane().setPrefWidth(400.0);

        Label label = new Label(message);
        label.setWrapText(true);
        label.setTextAlignment(TextAlignment.JUSTIFY);
        label.setPrefWidth(200.0);
        label.setMinHeight(Region.USE_PREF_SIZE);

        alert.getDialogPane().setContent(label);
        return alert;
    }

    private Alert getDynamicAlert() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setResizable(true);
        alert.getDialogPane().setPrefWidth(400.0);

        return alert;
    }

    private Label getDynamicLabel() {
        Label label = new Label();
        label.setWrapText(true);
        label.setTextAlignment(TextAlignment.JUSTIFY);
        label.setPrefWidth(200.0);
        label.setMinHeight(Region.USE_PREF_SIZE);

        return label;
    }
}
