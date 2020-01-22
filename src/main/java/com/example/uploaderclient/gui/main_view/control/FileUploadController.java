package com.example.uploaderclient.gui.main_view.control;

import com.example.uploaderclient.gui.input.boundary.DataProvider;
import com.example.uploaderclient.gui.result_dialog.control.DialogController;
import com.example.uploaderclient.uploader.api.boundary.DataSource;
import com.example.uploaderclient.uploader.api.control.ProcessingService;
import com.example.uploaderclient.uploader.api.entity.Statistics;
import io.reactivex.Observer;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class FileUploadController {

    public StackPane windowPane;
    public TextField dataSourcesTextField;
    public ProgressIndicator progressIndicator;

    private List<DataSource> currentDataSources;
    private final DataProvider dataProvider;
    private final ProcessingService processingService;

    @Autowired
    FileUploadController(DataProvider dataProvider, ProcessingService processingService) {
        this.currentDataSources = new ArrayList<>();
        this.dataProvider = dataProvider;
        this.processingService = processingService;
    }

    @FXML
    void selectFilesToUpload() {
        Stage primaryStage = (Stage) windowPane.getScene().getWindow();
        currentDataSources = dataProvider.getDataSources(primaryStage);

        String sourcesNames = getSourcesNames();
        dataSourcesTextField.setText(sourcesNames);
    }

    @FXML
    void runUpload() throws IOException {
        if (currentDataSources.isEmpty()) {
            showMissingDataSourceAlert();
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(FileUploadController.class
                    .getClassLoader().getResource("resultDialog.fxml"));
            showProgressModal(fxmlLoader);

            DialogController dialogController = fxmlLoader.getController();
            dialogController.setPrimaryStage((Stage) windowPane.getScene().getWindow());
            doRunUpload(dialogController);
        }
    }

    private String getSourcesNames() {
        return currentDataSources.stream()
                .map(DataSource::getShortName)
                .collect(Collectors.joining(", "));
    }

    private void showMissingDataSourceAlert() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setResizable(false);
        alert.getDialogPane().setPrefWidth(400.0);

        Label label = new Label("No data source has been provided!");
        label.setWrapText(true);
        label.setTextAlignment(TextAlignment.JUSTIFY);
        label.setPrefWidth(200.0);
        label.setMinHeight(Region.USE_PREF_SIZE);

        alert.getDialogPane().setContent(label);
        alert.showAndWait();
    }

    private void showProgressModal(FXMLLoader fxmlLoader) throws IOException {
        Parent parent = fxmlLoader.load();

        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }

    private void doRunUpload(Observer<Statistics> statisticsObserver) {
        processingService.parseAndUpload(currentDataSources)
                .subscribeOn(Schedulers.computation())
                .throttleLatest(100, TimeUnit.MILLISECONDS, true)
                .observeOn(JavaFxScheduler.platform())
                .doOnSubscribe(d -> {
                    windowPane.setDisable(true);
                    progressIndicator.setVisible(true);
                })
                .doFinally(() -> {
                    windowPane.setDisable(false);
                    progressIndicator.setVisible(false);
                })
                .subscribe(statisticsObserver);
    }
}