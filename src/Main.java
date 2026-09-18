import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Library library = null;

        System.out.println("Welcome! Type 'help' for a list of commands.");

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            String command = parts[0].toLowerCase();

            try {
                switch (command) {
                    case "help":
                        printHelp();
                        break;

                    case "open":
                        library = new Library();
                        System.out.println("Library opened.");
                        break;

                    case "close":
                        library = null;
                        System.out.println("Library closed.");
                        break;

                    case "exit":
                        System.out.println("Exiting the program...");
                        return;

                    case "login":
                        requireOpenLibrary(library);
                        requireArgs(parts, 3, "Usage: login <username> <password>");
                        library.login(parts[1], parts[2]);
                        System.out.println("Welcome, " + parts[1] + "!");
                        break;

                    case "logout":
                        requireOpenLibrary(library);
                        library.logout();
                        System.out.println("Logged out.");
                        break;

                    case "stats":
                        requireOpenLibrary(library);
                        LibraryStats stats = library.getStats();
                        System.out.println("Books: " + stats.totalBooks()
                                + ", Users: " + stats.totalUsers()
                                + ", Admins: " + stats.totalAdmins());
                        break;

                    case "seed":
                        requireOpenLibrary(library);
                        library.seedRandomBooks();
                        System.out.println("Added random books.");
                        break;

                    case "books":
                        requireOpenLibrary(library);
                        handleBooksCommand(library, parts, scanner);
                        break;

                    case "users":
                        requireOpenLibrary(library);
                        handleUsersCommand(library, parts);
                        break;

                    default:
                        System.out.println("Unknown command: " + command);
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void handleBooksCommand(Library library, String[] parts, Scanner scanner) {
        requireArgs(parts, 2, "Usage: books <all|add|find|sort|info> ...");
        String sub = parts[1].toLowerCase();

        switch (sub) {
            case "all":
                library.printAllBooks();
                break;

            case "add":
                Book newBook = readBookFromInput(scanner);
                library.addBook(newBook);
                System.out.println("Book added: " + newBook);
                break;

            case "info":
                requireArgs(parts, 3, "Usage: books info <libraryNumber>");
                int number = Integer.parseInt(parts[2]);
                System.out.println(library.findBookByLibraryNumber(number));
                break;

            case "find":
                requireArgs(parts, 4, "Usage: books find <title|author> <value>");
                String field = parts[2].toLowerCase();
                String value = parts[3];
                List<Book> found = field.equals("title")
                        ? library.findBooksByTitle(value)
                        : library.findBooksByAuthor(value);
                found.forEach(System.out::println);
                break;

            case "sort":
                requireArgs(parts, 3, "Usage: books sort <title|author|year|rating> [asc|desc]");
                String criteria = parts[2].toLowerCase();
                boolean ascending = parts.length < 4 || !parts[3].equalsIgnoreCase("desc");
                List<Book> sorted = switch (criteria) {
                    case "title" -> library.sortByTitle(ascending);
                    case "author" -> library.sortByAuthor(ascending);
                    case "year" -> library.sortByYear(ascending);
                    case "rating" -> library.sortByRating(ascending);
                    default -> throw new IllegalArgumentException("Unknown sort criteria: " + criteria);
                };
                sorted.forEach(System.out::println);
                break;

            default:
                System.out.println("Unknown books command: " + sub);
        }
    }

    private static void handleUsersCommand(Library library, String[] parts) {
        requireArgs(parts, 2, "Usage: users <add|remove> ...");
        String sub = parts[1].toLowerCase();

        switch (sub) {
            case "add":
                requireArgs(parts, 4, "Usage: users add <username> <password>");
                library.addUser(parts[2], parts[3]);
                System.out.println("User added: " + parts[2]);
                break;

            case "remove":
                requireArgs(parts, 3, "Usage: users remove <username>");
                library.removeUser(parts[2]);
                System.out.println("User removed: " + parts[2]);
                break;

            default:
                System.out.println("Unknown users command: " + sub);
        }
    }

    private static Book readBookFromInput(Scanner scanner) {
        System.out.print("Author: ");
        String author = scanner.nextLine();

        System.out.print("Title: ");
        String title = scanner.nextLine();

        System.out.print("Genre (" + java.util.Arrays.toString(Genre.values()) + "): ");
        Genre genre = Genre.valueOf(scanner.nextLine().trim().toUpperCase());

        System.out.print("Description: ");
        String description = scanner.nextLine();

        System.out.print("Year: ");
        int year = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Keywords (comma-separated): ");
        String keywordsLine = scanner.nextLine();
        List<String> keywords = keywordsLine.isBlank()
                ? new ArrayList<>()
                : new ArrayList<>(List.of(keywordsLine.split("\\s*,\\s*")));

        System.out.print("Rating (0-5): ");
        double rating = Double.parseDouble(scanner.nextLine().trim());

        return new Book(author, title, genre, description, year, keywords, rating);
    }

    private static void requireOpenLibrary(Library library) {
        if (library == null) {
            throw new IllegalStateException("No library is currently open. Use 'open' first.");
        }
    }

    private static void requireArgs(String[] parts, int minLength, String usage) {
        if (parts.length < minLength) {
            throw new IllegalArgumentException(usage);
        }
    }

    private static void printHelp() {
        System.out.println("""
                Available commands:
                  open                          - opens a new library session
                  close                         - closes the current library
                  login <user> <pass>           - logs in
                  logout                        - logs out
                  stats                         - shows library statistics
                  books all                     - lists all books
                  books add                     - adds a new book (admin only)
                  books info <number>           - shows info about a book
                  books find title <value>      - finds books by title
                  books find author <value>     - finds books by author
                  books sort <field> [asc|desc] - sorts books by title/author/year/rating
                  users add <user> <pass>       - adds a new client (admin only)
                  users remove <user>           - removes a user (admin only)
                  exit                          - exits the program
                """);
    }
}