package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Library;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblError;

    private Library library;
    private Runnable onLoginSuccess;

    // Application.java ни подава готовия Library обект - не искаме
    // да създаваме нов, за да не изгубим данните.
    public void setLibrary(Library library) {
        this.library = library;
    }

    // Application.java ни казва какво да правим при успешен логин
    // (в случая - да смени екрана).
    public void setOnLoginSuccess(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    @FXML
    void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        try {
            library.login(username, password);
            lblError.setText("");
            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            lblError.setText(e.getMessage());
        }
    }
}
