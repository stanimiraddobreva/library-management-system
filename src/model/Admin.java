package model;

public class Admin extends User {
    public Admin(String username, String password) {
        super(username, password);
    }

    @Override
    public boolean isAdmin() {
        return true;
    }

    @Override
    public String getRoleName() {
        return "ADMIN";
    }
}
