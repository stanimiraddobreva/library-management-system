package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Library;

import java.io.IOException;

public class Application extends javafx.application.Application {

    private Library library;
    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        // Library-то се създава ТУК, веднъж, и се предава на всеки
        // контролер - иначе всеки екран щеше да си прави своя собствена
        // библиотека и данните щяха да "изчезват" при смяна на сцената.
        this.library = new Library();
        this.library.seedRandomBooks();

        stage.setTitle("Library");
        showLoginScreen();
        stage.show();
    }

    private void showLoginScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();

        LoginController loginController = loader.getController();
        loginController.setLibrary(library);
        loginController.setOnLoginSuccess(() -> {
            try {
                showMainScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        stage.setScene(new Scene(root));
        stage.sizeToScene();
        stage.setResizable(false);
    }

    private void showMainScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Scene.fxml"));
        Parent root = loader.load();

        Controller mainController = loader.getController();
        mainController.setLibrary(library);

        stage.setScene(new Scene(root));
        stage.sizeToScene();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
