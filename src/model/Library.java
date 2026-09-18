package model;

import java.util.*;
import java.util.stream.Collectors;
import service.RandomBookFactory;

public class Library implements RandomBookFactory {
    private List<Book> books;
    private List<User> users;
    private User loggedInUser;

    public Library() {
        this.books = new ArrayList<>();
        this.users = new ArrayList<>();
        this.loggedInUser = null;
        this.users.add(new Admin("admin", "0000"));
    }

    public void login(String username, String password) {
        if (loggedInUser != null) {
            throw new IllegalStateException("You are already logged in.");
        }
        User found = findUserByUsername(username);
        if (found == null || !found.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        loggedInUser = found;
    }

    public void logout() {
        if (loggedInUser == null) {
            throw new IllegalStateException("No user is currently logged in.");
        }
        loggedInUser = null;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void addBook(Book book) {
        requireLogin();
        requireAdmin();
        books.add(book);
    }

    public void seedRandomBooks() {
        books.addAll(get());
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    public Book findBookByLibraryNumber(int libraryNumber) {
        for (Book book : books) {
            if (book.getLibraryNumber() == libraryNumber) {
                return book;
            }
        }
        throw new IllegalArgumentException("No book found with number: " + libraryNumber);
    }

    public List<Book> findBooksByTitle(String title) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                result.add(book);
            }
        }
        return result;
    }

    public List<Book> findBooksByAuthor(String author) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getAuthor().equalsIgnoreCase(author)) {
                result.add(book);
            }
        }
        return result;
    }

    public List<Book> findBooksByKeyword(String keyword) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getKeywords().contains(keyword)) {
                result.add(book);
            }
        }
        return result;
    }

    public List<Book> sortByTitle(boolean ascending) {
        List<Book> sorted = getAllBooks();
        sorted.sort(Comparator.comparing(Book::getTitle));
        if (!ascending) Collections.reverse(sorted);
        return sorted;
    }

    public List<Book> sortByAuthor(boolean ascending) {
        List<Book> sorted = getAllBooks();
        sorted.sort(Comparator.comparing(Book::getAuthor));
        if (!ascending) Collections.reverse(sorted);
        return sorted;
    }

    public List<Book> sortByYear(boolean ascending) {
        List<Book> sorted = getAllBooks();
        sorted.sort(Comparator.comparingInt(Book::getYear));
        if (!ascending) Collections.reverse(sorted);
        return sorted;
    }

    public List<Book> sortByRating(boolean ascending) {
        List<Book> sorted = getAllBooks();
        sorted.sort(Comparator.comparingDouble(Book::getRating));
        if (!ascending) Collections.reverse(sorted);
        return sorted;
    }

    public double averageRating() {
        return books.stream()
                .mapToDouble(Book::getRating)
                .average()
                .orElse(0.0);
    }

    public Map<Genre, List<Book>> groupByGenre() {
        return books.stream()
                .collect(Collectors.groupingBy(Book::getGenre));
    }

    public Map<Genre, Long> countByGenre() {
        return books.stream()
                .collect(Collectors.groupingBy(Book::getGenre, Collectors.counting()));
    }

    public List<Book> topRatedBooks(int n) {
        return books.stream()
                .sorted(Comparator.comparingDouble(Book::getRating).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    public String formatBooksByGenre() {
        StringBuilder sb = new StringBuilder();
        groupByGenre().forEach((genre, list) -> {
            sb.append(genre).append(" (").append(list.size()).append(")\n");
            list.forEach(b -> sb.append("  ").append(b).append("\n"));
        });
        return sb.toString();
    }

    public void addUser(String username, String password) {
        requireLogin();
        requireAdmin();
        if (findUserByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        users.add(new Client(username, password));
    }

    public void removeUser(String username) {
        requireLogin();
        requireAdmin();
        User toRemove = findUserByUsername(username);
        if (toRemove == null) {
            throw new IllegalArgumentException("No user found with username: " + username);
        }
        users.remove(toRemove);
    }

    public void printAllBooks() {
        for (Book book : books) {
            System.out.println(book);
        }
    }

    public LibraryStats getStats() {
        int admins = 0;
        for (User user : users) {
            if (user.isAdmin()) admins++;
        }
        return new LibraryStats(books.size(), users.size(), admins);
    }

    private User findUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    private void requireLogin() {
        if (loggedInUser == null) {
            throw new IllegalStateException("You must be logged in to perform this action.");
        }
    }

    private void requireAdmin() {
        if (!loggedInUser.isAdmin()) {
            throw new IllegalStateException("Only admin users can perform this action.");
        }
    }
}
