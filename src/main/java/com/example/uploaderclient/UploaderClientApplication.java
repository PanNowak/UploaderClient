package com.example.uploaderclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

@SpringBootApplication
public class UploaderClientApplication extends Application {

	private ConfigurableApplicationContext springContext;
	private FXMLLoader fxmlLoader;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void init() {
		springContext = SpringApplication.run(UploaderClientApplication.class);
		fxmlLoader = new FXMLLoader();
		fxmlLoader.setControllerFactory(springContext::getBean);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		fxmlLoader.setLocation(UploaderClientApplication.class
				.getClassLoader().getResource("mainPane.fxml"));

		Pane rootNode = fxmlLoader.load();
		Scene scene = new Scene(rootNode);

		primaryStage.setTitle("File uploader");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}


//		stage.setTitle("File Chooser Sample");
//
//		final FileChooser fileChooser = new FileChooser();
//
//		final Button openButton = new Button("Open a file...");
//		final Button runUploadButton = new Button("Upload files...");
//
//		JavaFxObservable.actionEventsOf(openButton)
//				.map(e -> fileChooser.showOpenDialog(stage))
//				.filter(Objects::nonNull)
//				.forEach(this::openFile);

//		final GridPane inputGridPane = new GridPane();
//
//		GridPane.setConstraints(openButton, 0, 0);
//		GridPane.setConstraints(runUploadButton, 1, 0);
//		inputGridPane.setHgap(6);
//		inputGridPane.setVgap(6);
//		inputGridPane.getChildren().addAll(openButton, runUploadButton);
//
//		final Pane rootGroup = new VBox(12);
//		rootGroup.getChildren().addAll(inputGridPane);
//		rootGroup.setPadding(new Insets(12, 12, 12, 12));
//
//		stage.setScene(new Scene(rootGroup));
//		stage.show();
//	}
//
//	private Desktop desktop = Desktop.getDesktop();
//
//	private void openFile(File file) {
//		try {
//			desktop.open(file);
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//	}

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


	@Override
	public void stop() {
		springContext.stop();
	}
}