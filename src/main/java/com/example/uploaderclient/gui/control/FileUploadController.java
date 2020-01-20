package com.example.uploaderclient.gui.control;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.example.uploaderclient.api.boundary.DataSource;
import com.example.uploaderclient.api.control.ProcessingService;
import com.example.uploaderclient.gui.input.boundary.DataProvider;
import com.example.uploaderclient.gui.progress.control.TrackingProgressObserver;

import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class FileUploadController {

    public VBox windowPane;
    public TextField dataSourcesTextField;
    public ProgressIndicator progressIndicator;

    private final DataProvider dataProvider;
    private final ProcessingService processingService;

    @Autowired
    FileUploadController(DataProvider dataProvider, ProcessingService processingService) {
        this.dataProvider = dataProvider;
        this.processingService = processingService;
    }

    private List<DataSource> currentDataSources = new ArrayList<>();

    @FXML
    void selectFilesToUpload() {
        Window container = windowPane.getScene().getWindow();
        currentDataSources = dataProvider.getDataSources(container);

        String sourcesNames = getSourcesNames();
        dataSourcesTextField.setText(sourcesNames);
    }

    @FXML
    void runUpload() {
        if (currentDataSources.isEmpty()) {
            getStaticAlert(AlertType.WARNING, "No data source has been provided!").showAndWait();
        } else {

//
//            Alert alert = new Alert(AlertType.INFORMATION);
//            alert.setResizable(false);
//            alert.getDialogPane().setPrefWidth(400.0);
//
//            Label label = new Label();
//            label.setWrapText(true);
//            label.setTextAlignment(TextAlignment.JUSTIFY);
//            label.setPrefWidth(200.0);
//            label.setMinHeight(Region.USE_PREF_SIZE);
//
////            alert.setOnCloseRequest(Event::consume);
//            alert.getDialogPane().setContent(label);
//            alert.show();


            processingService.parseAndUpload(currentDataSources)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(JavaFxScheduler.platform())
                    .subscribe(new TrackingProgressObserver(windowPane, progressIndicator));

//                    .doOnSubscribe(d -> {
//                        System.out.println("Wyłączamy " + Thread.currentThread().getName());
//                        windowPane.setDisable(true);
//                        progressIndicator.setVisible(true);
//                    })
//                    .doFinally(() -> {
//                        log.info("Włączamy z powrotem " + Thread.currentThread().getName());
//                        windowPane.setDisable(false);
//                        progressIndicator.setVisible(false);
//                    })
//                    .subscribe(s -> label.setText(s.toString()),
//                            throwable -> {
//                                alert.close();
//                                handleException(throwable);
//                            });
        }
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

    private String getSourcesNames() {
        return currentDataSources.stream()
                .map(DataSource::getShortName)
                .collect(Collectors.joining(", "));
    }

//
//    private void handleException(Throwable throwable) {
//        Throwable original = throwable;
//        while (original.getCause() != null) {
//            original = original.getCause();
//        }
//        getStaticAlert(AlertType.ERROR, throwable + " -> " + original).show();
//
//        throwable.printStackTrace();
//    }



}
