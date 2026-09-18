package model;

public class Client extends User {
    public Client(String username, String password) {
        super(username, password);
    }

    @Override
    public boolean isAdmin() {
        return false;
    }

    @Override
    public String getRoleName() {
        return "CLIENT";
    }
}
