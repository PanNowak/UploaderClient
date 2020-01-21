package com.example.uploaderclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

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
		fxmlLoader = new FXMLLoader(UploaderClientApplication.class
				.getClassLoader().getResource("mainPane.fxml"));
		fxmlLoader.setControllerFactory(springContext::getBean);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		Pane rootNode = fxmlLoader.load();
		Scene scene = new Scene(rootNode);

		primaryStage.setTitle("File uploader");
		primaryStage.setScene(scene);
		primaryStage.setResizable(true);

		primaryStage.show();
		primaryStage.setMinWidth(primaryStage.getWidth());
		primaryStage.setMinHeight(primaryStage.getHeight());
		primaryStage.setMaxHeight(primaryStage.getHeight());
	}

	@Override
	public void stop() {
		springContext.stop();
	}
}