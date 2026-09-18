package view;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Book;
import model.Genre;
import model.Library;
import model.LibraryStats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Controller {

    @FXML
    private HBox adminBar;

    @FXML
    private Button btnAddUser;

    @FXML
    private Button btnRemoveUser;

    @FXML
    private Button btnAddBook;

    @FXML
    private Button btnAverageRating;

    @FXML
    private Button btnGroupByGenre;

    @FXML
    private Button btnQuit;

    @FXML
    private Button btnSeed;

    @FXML
    private Button btnShowAll;

    @FXML
    private ComboBox<String> cmbSortField;

    @FXML
    private CheckBox chkAscending;

    @FXML
    private Button btnSort;

    @FXML
    private ComboBox<String> cmbSearchField;

    @FXML
    private TextField txtSearch;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnTopRated;

    @FXML
    private Button btnGetStats;

    @FXML
    private TextField txtTopN;

    @FXML
    private TextArea txtOutput;

    private Library library;

    // Извиква се от Application.java веднага след зареждане на екрана,
    // с вече готовия (логнат) Library обект.
    public void setLibrary(Library library) {
        this.library = library;

        cmbSortField.setItems(FXCollections.observableArrayList("Title", "Author", "Year", "Rating"));
        cmbSortField.getSelectionModel().selectFirst();

        cmbSearchField.setItems(FXCollections.observableArrayList("Title", "Author", "Keyword", "Number"));
        cmbSearchField.getSelectionModel().selectFirst();

        // Само admin вижда бутоните за администрация (add/remove user, add book) -
        // client-ите не трябва изобщо да ги виждат, не само да им са disabled.
        boolean isAdmin = library.getLoggedInUser().isAdmin();
        adminBar.setVisible(isAdmin);
        adminBar.setManaged(isAdmin);

        txtOutput.setText("Welcome, " + library.getLoggedInUser().getUsername()
                + "! " + library.getAllBooks().size() + " books loaded.");
    }

    @FXML
    void handleAddUser(ActionEvent event) {
        TextInputDialog usernameDialog = new TextInputDialog();
        usernameDialog.setTitle("Add User");
        usernameDialog.setHeaderText(null);
        usernameDialog.setContentText("Username:");
        Optional<String> usernameResult = usernameDialog.showAndWait();

        if (usernameResult.isEmpty() || usernameResult.get().isBlank()) {
            return; // потребителят е натиснал Cancel или не е въвел нищо
        }

        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("Add User");
        passwordDialog.setHeaderText(null);
        passwordDialog.setContentText("Password:");
        Optional<String> passwordResult = passwordDialog.showAndWait();

        if (passwordResult.isEmpty() || passwordResult.get().isBlank()) {
            return;
        }

        try {
            library.addUser(usernameResult.get().trim(), passwordResult.get());
            txtOutput.setText("User added: " + usernameResult.get().trim());
        } catch (IllegalArgumentException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    void handleRemoveUser(ActionEvent event) {
        TextInputDialog usernameDialog = new TextInputDialog();
        usernameDialog.setTitle("Remove User");
        usernameDialog.setHeaderText(null);
        usernameDialog.setContentText("Username to remove:");
        Optional<String> usernameResult = usernameDialog.showAndWait();

        if (usernameResult.isEmpty() || usernameResult.get().isBlank()) {
            return;
        }

        try {
            library.removeUser(usernameResult.get().trim());
            txtOutput.setText("User removed: " + usernameResult.get().trim());
        } catch (IllegalArgumentException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    void handleAddBook(ActionEvent event) {
        try {
            String author = promptText("Add Book", "Author:");
            if (author == null) return;

            String title = promptText("Add Book", "Title:");
            if (title == null) return;

            ChoiceDialog<Genre> genreDialog = new ChoiceDialog<>(Genre.FICTION, Genre.values());
            genreDialog.setTitle("Add Book");
            genreDialog.setHeaderText(null);
            genreDialog.setContentText("Genre:");
            Optional<Genre> genreResult = genreDialog.showAndWait();
            if (genreResult.isEmpty()) return;

            String description = promptText("Add Book", "Description:");
            if (description == null) return;

            String yearText = promptText("Add Book", "Year:");
            if (yearText == null) return;
            int year = Integer.parseInt(yearText.trim());

            String keywordsText = promptText("Add Book", "Keywords (comma separated):");
            if (keywordsText == null) return;
            List<String> keywords = new ArrayList<>();
            if (!keywordsText.isBlank()) {
                for (String kw : keywordsText.split(",")) {
                    if (!kw.isBlank()) keywords.add(kw.trim());
                }
            }

            String ratingText = promptText("Add Book", "Rating (0-5):");
            if (ratingText == null) return;
            double rating = Double.parseDouble(ratingText.trim());

            Book book = new Book(author.trim(), title.trim(), genreResult.get(),
                    description, year, keywords, rating);
            library.addBook(book);
            txtOutput.setText("Book added: " + book);
        } catch (NumberFormatException e) {
            showError("Year and rating must be valid numbers.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    void handleShowAll(ActionEvent event) {
        printBooks(library.getAllBooks(), "All books:");
    }

    @FXML
    void handleSort(ActionEvent event) {
        String field = cmbSortField.getValue();
        boolean ascending = chkAscending.isSelected();

        if (field == null) {
            return;
        }

        List<Book> sorted = switch (field) {
            case "Title" -> library.sortByTitle(ascending);
            case "Author" -> library.sortByAuthor(ascending);
            case "Year" -> library.sortByYear(ascending);
            case "Rating" -> library.sortByRating(ascending);
            default -> library.getAllBooks();
        };

        printBooks(sorted, "Sorted by " + field + " (" + (ascending ? "asc" : "desc") + "):");
    }

    @FXML
    void handleSearch(ActionEvent event) {
        String field = cmbSearchField.getValue();
        String query = txtSearch.getText();

        if (field == null || query == null || query.isBlank()) {
            showError("Choose a field and enter a value to search for.");
            return;
        }

        String value = query.trim();

        if (field.equals("Number")) {
            try {
                int number = Integer.parseInt(value);
                Book book = library.findBookByLibraryNumber(number);
                txtOutput.setText("Found:\n" + book);
            } catch (NumberFormatException e) {
                showError("Library number must be a whole number.");
            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
            }
            return;
        }

        List<Book> results = switch (field) {
            case "Title" -> library.findBooksByTitle(value);
            case "Author" -> library.findBooksByAuthor(value);
            case "Keyword" -> library.findBooksByKeyword(value);
            default -> List.of();
        };

        printBooks(results, "Search results (" + field + " = \"" + value + "\"):");
    }

    @FXML
    void handleGroupByGenre(ActionEvent event) {
        Map<Genre, Long> counts = library.countByGenre();
        StringBuilder sb = new StringBuilder("Books grouped by genre:\n\n");
        counts.forEach((genre, count) ->
                sb.append(genre).append(": ").append(count).append("\n"));
        txtOutput.setText(sb.toString());
    }

    @FXML
    void handleAverageRating(ActionEvent event) {
        double avg = library.averageRating();
        txtOutput.setText(String.format("Average rating of all books: %.2f", avg));
    }

    @FXML
    void handleTopRated(ActionEvent event) {
        int n = 5;
        String nText = txtTopN.getText();
        if (nText != null && !nText.isBlank()) {
            try {
                n = Integer.parseInt(nText.trim());
            } catch (NumberFormatException e) {
                showError("N must be a whole number.");
                return;
            }
        }
        printBooks(library.topRatedBooks(n), "Top " + n + " rated books:");
    }

    @FXML
    void handleGetStats(ActionEvent event) {
        LibraryStats stats = library.getStats();
        txtOutput.setText("Library stats:\n\n"
                + "Total books: " + stats.totalBooks() + "\n"
                + "Total users: " + stats.totalUsers() + "\n"
                + "Total admins: " + stats.totalAdmins());
    }

    @FXML
    void handleSeed(ActionEvent event) {
        library.seedRandomBooks();
        txtOutput.setText("Added random books. Total now: " + library.getAllBooks().size());
    }

    @FXML
    void handleQuit(ActionEvent event) {
        Stage stage = (Stage) btnQuit.getScene().getWindow();
        stage.close();
    }

    private void printBooks(List<Book> books, String header) {
        StringBuilder sb = new StringBuilder(header).append("\n\n");
        for (Book book : books) {
            sb.append(book).append("\n");
        }
        if (books.isEmpty()) {
            sb.append("(no books found)");
        }
        txtOutput.setText(sb.toString());
    }

    private String promptText(String title, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(content);
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
