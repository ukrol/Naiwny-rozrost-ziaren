import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("TheGameOfLife.fxml"));
        primaryStage.setTitle("Naive Grain Growth");
        primaryStage.setScene(new Scene(root, 1000, 700));
        primaryStage.show();
        primaryStage.setResizable(true);        
    }


    public static void main(String[] args) {
        launch(args);
    }
}