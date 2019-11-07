package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class DBWindow extends Application{

	@Override
	public void start(Stage windowStage) throws Exception {
	
		BorderPane root = new BorderPane();
		GridPane base = new GridPane();
		root.setCenter(base);
		windowStage.setTitle("DBWindow");
		windowStage.setScene(new Scene(root, 200,200));
		windowStage.show();
	}

	
	
}
