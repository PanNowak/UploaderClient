package com.example.uploaderclient.gui.dialog.control;

import com.example.uploaderclient.uploader.api.entity.Statistics;
import io.reactivex.observers.DefaultObserver;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class DialogController extends DefaultObserver<Statistics> {
    public Text titleLabel;
    public GridPane valuesPane;
    public Button someButton;

    @Override
    protected void onStart() {
        someButton.setOnAction(event -> {
            cancel();

            titleLabel.setText("Upload was canceled!");
            someButton.setText("Ok");
        });
    }

    @Override
    public void onNext(Statistics statistics) {
        titleLabel.setText("Upload is running");
    }

    @Override
    public void onError(Throwable e) {
        titleLabel.setText("Exception occurred!");
        handleException(e);

        someButton.setText("Ok");
    }

    @Override
    public void onComplete() {
        titleLabel.setText("Upload ended!");

        someButton.setText("Ok");

    }

    private void handleException(Throwable throwable) { //TODO osobna klasa rozróżniająca różne typy błędów
        Throwable original = throwable;
        while (original.getCause() != null) {
            original = original.getCause();
        }
        System.out.println(throwable + " -> " + original);

        throwable.printStackTrace();
    }
}
