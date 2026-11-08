package model;

public class CourierCredentials {
    private String login;
    private String password;

    // пустой конструктор
    public CourierCredentials() {
    }

    // Конструктор с параметрами
    public CourierCredentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    // Геттеры и Сеттеры
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
