package model;

import java.util.Objects;

public abstract class User {
    private String username;
    private String password;

    protected User(String username, String password) {
        setUsername(username);
        setPassword(password);
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public void setUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        this.username = username;
    }

    public void setPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        this.password = password;
    }

    public abstract boolean isAdmin();

    public abstract String getRoleName();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return username + " (" + getRoleName() + ")";
    }
}