package model;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Book implements Comparable<Book> {
    private static int bookCounter = 0;
    private static final Random random = new Random();

    private String author;
    private String title;
    private Genre genre;
    private String description;
    private int year;
    private List<String> keywords;
    private double rating;
    private final int libraryNumber;

    public Book(String author, String title, Genre genre, String description,
                int year, List<String> keywords, double rating) {
        setAuthor(author);
        setTitle(title);
        setGenre(genre);
        setDescription(description);
        setYear(year);
        setKeywords(keywords);
        setRating(rating);
        this.libraryNumber = generateLibraryNumber();
    }

    private static int generateLibraryNumber() {
        bookCounter++;
        int randomPart = random.nextInt(900) + 100;
        return randomPart * 10000 + bookCounter;
    }

    public static int getTotalBooksCreated() {
        return bookCounter;
    }

    public String getAuthor() { return author; }
    public String getTitle() { return title; }
    public Genre getGenre() { return genre; }
    public String getDescription() { return description; }
    public int getYear() { return year; }
    public double getRating() { return rating; }
    public int getLibraryNumber() { return libraryNumber; }

    public List<String> getKeywords() {
        return new ArrayList<>(keywords);
    }

    public void setAuthor(String author) {
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Author cannot be empty.");
        }
        this.author = author;
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        this.title = title;
    }

    public void setGenre(Genre genre) {
        if (genre == null) {
            throw new IllegalArgumentException("Genre is mandatory.");
        }
        this.genre = genre;
    }

    public void setDescription(String description) {
        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null.");
        }
        this.description = description;
    }

    public void setYear(int year) {
        int currentYear = Year.now().getValue();
        if (year < 0 || year > currentYear) {
            throw new IllegalArgumentException(
                    "Year must be between 0 and " + currentYear + ", given: " + year);
        }
        this.year = year;
    }

    public void setKeywords(List<String> keywords) {
        if (keywords == null) {
            throw new IllegalArgumentException("Keywords list cannot be null.");
        }
        this.keywords = keywords;
    }

    public void setRating(double rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException(
                    "Rating must be between 0 and 5, given: " + rating);
        }
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return libraryNumber == book.libraryNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(libraryNumber);
    }

    @Override
    public int compareTo(Book other) {
        return this.title.compareTo(other.title);
    }

    @Override
    public String toString() {
        return title + " | " + author + " | " + genre + " | #" + libraryNumber;
    }
}
