# Library — Java Library Management System

A desktop library management application built in **Java** with a **JavaFX** graphical interface (plus a parallel console/CLI mode), created as an Object-Oriented Programming project. It models a small library with role-based access, book cataloguing, search/sort/filter tools, and basic statistics.

## Overview

The app simulates a real library workflow:

- An **Admin** logs in and manages the catalogue (adds/removes books and users).
- A **Client** logs in and browses, searches, and sorts the catalogue.
- The library is pre-seeded with a set of randomly generated books on startup, so there's always sample data to explore.

> **Note:** Data lives only in memory for the duration of a session — this project's focus is OOP design, not data persistence. Everything resets on restart. (Adding persistence via serialization or a database is a natural next step.)

## Features

- **Authentication & roles** — Admin vs. Client accounts, with admin-only actions hidden/disabled for regular users
- **Book catalogue** — add, look up by library number, and browse all books
- **Search** — by title, author, or keyword
- **Sorting** — by title, author, year, or rating (ascending/descending)
- **Statistics** — average rating, top-N rated books, grouping/counting books by genre
- **User management** (admin only) — add and remove client accounts
- **Random data seeding** — auto-generates sample books for quick testing/demo
- **Two interfaces** — a JavaFX GUI (`view.Application`) and a plain-text CLI (`Main`) sharing the same underlying model

## Tech stack

- **Java** (core language, OOP design)
- **JavaFX** + **FXML** for the GUI
- **Java Collections & Streams API** for filtering, sorting, and grouping data

## OOP concepts demonstrated

- **Inheritance & polymorphism** — abstract `User` class extended by `Admin` and `Client`, each overriding `isAdmin()` / `getRoleName()`
- **Interfaces** — `RandomBookFactory` (extending `Supplier<List<Book>>`) for generating sample data
- **Encapsulation & validation** — all model fields (`Book`, `User`) are private with validating setters that throw on invalid input
- **`Comparable` / `Comparator`** — natural ordering and flexible multi-field sorting for `Book`
- **Records** — `LibraryStats` as an immutable data carrier
- **Separation of concerns** — `model` (domain logic), `view` (JavaFX controllers/screens), `service` (data generation)

## Project structure

```
src/
├── Main.java                  # Console/CLI entry point
├── model/
│   ├── User.java               # Abstract base user
│   ├── Admin.java               # Admin role
│   ├── Client.java               # Client role
│   ├── Book.java                # Book entity with validation
│   ├── Genre.java                # Genre enum
│   ├── Library.java               # Core domain logic (catalogue, users, auth)
│   └── LibraryStats.java           # Stats record
├── service/
│   └── RandomBookFactory.java       # Generates sample/demo books
└── view/
    ├── Application.java             # JavaFX entry point
    ├── LoginController.java          # Login screen logic
    ├── Login.fxml                     # Login screen layout
    ├── Controller.java                 # Main screen logic
    └── Scene.fxml                       # Main screen layout
```

## Getting started

### Prerequisites
- JDK 17+ (uses records and modern switch expressions)
- JavaFX SDK (for the GUI mode)

### Running the GUI
Run `view.Application` as the main class (make sure the JavaFX SDK modules are on the module path/VM options in your IDE).

### Running the CLI
Run `Main` and type `help` for the list of available commands (`open`, `login`, `books`, `users`, `stats`, etc.).

**Default admin login:** username `admin`, password `0000`

## Possible future improvements

- Persist data between sessions (serialization or a database via JDBC)
- Password hashing instead of plain-text storage
- Book borrowing/returning workflow with due dates
- Unit tests for the `model` layer
