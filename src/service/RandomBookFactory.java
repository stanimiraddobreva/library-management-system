package service;

import model.Book;
import model.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public interface RandomBookFactory extends Supplier<List<Book>> {
    Random GENERATOR = new Random();

    String[] SAMPLE_TITLES = {
            "The Silent Forest", "Echoes of Tomorrow", "Beyond the Horizon",
            "Whispers in the Dark", "The Last Algorithm", "Shadows of Time",
            "The Forgotten Path", "Rise of the Machines", "A Quiet Storm",
            "The Glass Kingdom"
    };

    String[] SAMPLE_AUTHORS = {
            "Elena Petrova", "James Carter", "Mihail Dimitrov", "Sofia Nikolova",
            "David Kim", "Anna Ivanova", "Marcus Reed", "Yana Georgieva"
    };

    String[] SAMPLE_KEYWORDS = {
            "adventure", "mystery", "love", "war", "technology",
            "family", "betrayal", "journey", "hope", "survival"
    };

    default List<Book> get() {
        List<Book> books = new ArrayList<>();
        int count = GENERATOR.nextInt(8) + 3; // 3 до 10 книги
        generateRandomBooks(books, count);
        return books;
    }

    private void generateRandomBooks(List<Book> books, int count) {
        for (int i = 0; i < count; i++) {
            String title = SAMPLE_TITLES[GENERATOR.nextInt(SAMPLE_TITLES.length)];
            String author = SAMPLE_AUTHORS[GENERATOR.nextInt(SAMPLE_AUTHORS.length)];
            Genre genre = Genre.values()[GENERATOR.nextInt(Genre.values().length)];
            String description = "Auto-generated description for \"" + title + "\".";
            int year = 1950 + GENERATOR.nextInt(75);
            double rating = Math.round(GENERATOR.nextDouble() * 5 * 10) / 10.0;

            books.add(new Book(author, title, genre, description, year,
                    pickRandomKeywords(), rating));
        }
    }

    private List<String> pickRandomKeywords() {
        int n = GENERATOR.nextInt(3) + 1;
        List<String> keywords = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            keywords.add(SAMPLE_KEYWORDS[GENERATOR.nextInt(SAMPLE_KEYWORDS.length)]);
        }
        return keywords;
    }
}
