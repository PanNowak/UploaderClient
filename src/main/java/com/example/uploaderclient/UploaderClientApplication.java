package com.example.uploaderclient;

import com.example.uploaderclient.api.boundary.DataSource;
import com.example.uploaderclient.api.boundary.UploadInfo;
import com.example.uploaderclient.api.control.FileBasedSource;
import com.example.uploaderclient.api.control.ProcessingService;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class UploaderClientApplication extends Application {

	private ConfigurableApplicationContext springContext;
	private FXMLLoader fxmlLoader;

	private ProcessingService processingService;
	private String sourcePath;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void init() {
		springContext = SpringApplication.run(UploaderClientApplication.class);
		processingService = springContext.getBean(ProcessingService.class);
		sourcePath = springContext.getEnvironment().getProperty("source.path");
		System.out.println(processingService.getAllSupportedFileTypes());


//		Observable.fromCallable(() -> getDataSources(sourcePath))
//					.observeOn(Schedulers.io())
//					.flatMap(processingService::parseAndUpload)
//					.observeOn(Schedulers.computation())
//					.blockingSubscribe(s -> {
//						System.out.println(s + " on " + Thread.currentThread().getName());
//						System.out.println(s.header());
//						System.out.println(s.body());
//					}, this::handleException);

//		fxmlLoader = new FXMLLoader();
//		fxmlLoader.setControllerFactory(springContext::getBean);
	}

	//TODO cleanup

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("File Chooser Sample");

		final FileChooser fileChooser = new FileChooser();

		final Button openButton = new Button("Open a file...");
		final Button runUploadButton = new Button("Upload files...");

		JavaFxObservable.actionEventsOf(openButton)
				.map(e -> fileChooser.showOpenDialog(stage))
				.filter(Objects::nonNull)
				.forEach(this::openFile);

//		runUploadButton = new RunUploadButtonController(() -> fileChooser.showOpenMultipleDialog(stage))

		JavaFxObservable.actionEventsOf(runUploadButton)
				.map(e -> fileChooser.showOpenMultipleDialog(stage))
				.filter(Objects::nonNull)
				.observeOn(Schedulers.computation())
				.map(list -> list.stream()
						.map(File::toPath)
						.map(FileBasedSource::new)
						.collect(Collectors.toList()))
				.flatMap(processingService::parseAndUpload)
				.map(UploadInfo::toString)
				.onErrorReturnItem("Error ocurred")
				.subscribe(System.out::println, this::handleException, () -> System.out.println("Closed observable"));

//
//		JavaFxObservable.actionEventsOf(runUploadButton)
//				.map(e -> fileChooser.showOpenMultipleDialog(stage))
//				.filter(Objects::nonNull)
//				.map(list -> list.stream()
//						.map(File::toPath)
//						.map(FileBasedSource::new)
//						.collect(Collectors.toList()))
//				.subscribe(sources -> processingService
//						.parseAndUpload(sources)
//						.subscribeOn(Schedulers.computation())
//						.map(UploadInfo::toString)
////						.retry(3)
//						.observeOn(JavaFxScheduler.platform())
//						.doOnSubscribe(s -> {
//							System.out.println("Wyłączam guzik w " + Thread.currentThread().getName());
//							runUploadButton.setDisable(true);
//						})
//						.doFinally(() -> {
//							System.out.println("Włączam guzik w " + Thread.currentThread().getName());
//							runUploadButton.setDisable(false);
//						})
//						.subscribe(System.out::println, this::handleException));



		final GridPane inputGridPane = new GridPane();

		GridPane.setConstraints(openButton, 0, 0);
		GridPane.setConstraints(runUploadButton, 1, 0);
		inputGridPane.setHgap(6);
		inputGridPane.setVgap(6);
		inputGridPane.getChildren().addAll(openButton, runUploadButton);

		final Pane rootGroup = new VBox(12);
		rootGroup.getChildren().addAll(inputGridPane);
		rootGroup.setPadding(new Insets(12, 12, 12, 12));

		stage.setScene(new Scene(rootGroup));
		stage.show();
	}

	private Desktop desktop = Desktop.getDesktop();

	private void openFile(File file) {
		try {
			desktop.open(file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	//	@Override
//	public void start(Stage primaryStage) throws Exception {
//
//		primaryStage.setTitle("Hello world");
//		Button button = new Button();
//		button.setText("Run upload");
//
//		GridPane gridPane = new GridPane();
//		gridPane.setAlignment(Pos.CENTER);
//		gridPane.setHgap(10);
//		gridPane.setVgap(10);
//		gridPane.setPadding(new Insets(25, 25, 25, 25));
//
//
////		StackPane root = new StackPane();
////		root.getChildren().addAll(button);
//		Scene scene = new Scene(gridPane, 300, 275);
//		primaryStage.setScene(scene);
//		Text actionTarget = new Text("some text");
//
//		gridPane.add(button, 0, 0);
//		gridPane.add(actionTarget, 0, 1);
//
//
//
////		JavaFxObservable.actionEventsOf(button)
////				.observeOn(Schedulers.computation())
////				.map(actionEvent -> getDataSources(sourcePath))
////				.map(processingService::parseAndUpload)
////				.flatMap(this::transform)
////				.observeOn(JavaFxScheduler.platform())
////				.subscribe(s -> {
////					actionTarget.setText(s.toString());
//////					System.out.println(s + " on " + Thread.currentThread().getName());
//////					System.out.println(s.header());
//////					System.out.println(s.body());
////				}, this::handleException);
//
//		JavaFxObservable.actionEventsOf(button)
//				.map(actionEvent -> getDataSources(sourcePath))
//				.subscribe(sources -> processingService
//						.parseAndUpload(sources)
//						.doOnSubscribe(s -> button.setDisable(true))
//						.doFinally(() -> button.setDisable(false))
//						.subscribeOn(Schedulers.computation())
//						.map(UploadInfo::toString)
////						.retry(3)
//						.observeOn(JavaFxScheduler.platform())
//						.subscribe(actionTarget::setText, this::handleException));
//
//
//		primaryStage.show();
//
////		fxmlLoader.setLocation(getClass().getResource("/fxml/sample.fxml"));
////		Parent rootNode = fxmlLoader.load();
////
////		primaryStage.setTitle("Hello World");
////		Scene scene = new Scene(rootNode, 800, 600);
////		primaryStage.setScene(scene);
////		primaryStage.show();
//	}

	@Override
	public void stop() {
		springContext.stop();
	}

//	@Bean
//	public CommandLineRunner runner(ConfigurableApplicationContext ctx) {
//		return args -> {
//			RxJavaPlugins.setErrorHandler(undeliverable -> System.out.println("Undeliverable: " + undeliverable.getCause()));//ciekawostka
//
//			ProcessingService service = ctx.getBean(ProcessingService.class);
//			String sourcePath = ctx.getEnvironment().getProperty("source.path");
//			System.out.println(service.getAllSupportedFileTypes());
//
//			Observable.fromCallable(() -> getDataSources(sourcePath))
//					.observeOn(Schedulers.io())
//					.flatMap(service::parseAndUpload)
//					.observeOn(Schedulers.computation())
//					.subscribe(s -> {
//						System.out.println(s + " on " + Thread.currentThread().getName());
//						System.out.println(s.header());
//						System.out.println(s.body());
//					}, this::handleException);
//
//
//			Thread.sleep(100000);
//		};
//	}

	private static List<DataSource> getDataSources(String sourcePath) {
		return Stream.of("Fully-correct-store.csv", "Fully-correct-sklep.xml")
				.map(filename -> Paths.get(sourcePath, filename))
				.map(FileBasedSource::new)
				.collect(Collectors.toList());
	}

	private void handleException(Throwable throwable) {
		Throwable original = throwable;
		while (original.getCause() != null) {
			original = original.getCause();
		}
		System.out.println(throwable + " -> " + original);
		System.out.println();
		throwable.printStackTrace();
	}
}