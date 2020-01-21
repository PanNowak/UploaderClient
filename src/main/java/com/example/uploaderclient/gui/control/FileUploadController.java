package com.example.uploaderclient.gui.control;

import com.example.uploaderclient.gui.dialog.control.DialogController;
import com.example.uploaderclient.gui.input.boundary.DataProvider;
import com.example.uploaderclient.uploader.api.boundary.DataSource;
import com.example.uploaderclient.uploader.api.control.ProcessingService;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class FileUploadController {

    public StackPane windowPane;
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
    void runUpload() throws IOException, InterruptedException {
        if (currentDataSources.isEmpty()) {
            getStaticAlert(AlertType.WARNING, "No data source has been provided!").showAndWait();
        } else {

            FXMLLoader fxmlLoader = new FXMLLoader(FileUploadController.class
                    .getClassLoader().getResource("dialogPane.fxml"));
            Parent parent = fxmlLoader.load();
            DialogController dialogController = fxmlLoader.getController();

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);

            stage.show();


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

//
            processingService.parseAndUpload(currentDataSources)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(JavaFxScheduler.platform())
                    .doOnSubscribe(d -> {
                        windowPane.setDisable(true);
                        progressIndicator.setVisible(true);
                    })
                    .doFinally(() -> {
                        windowPane.setDisable(false);
                        progressIndicator.setVisible(false);
                    })
                    .subscribe(dialogController);

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
