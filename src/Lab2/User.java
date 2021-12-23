package Lab2;

public class User extends BdSource{
    private int userId;
    private String lastName;
    private String name;
    private String secondName;
    private String login;
    private String password;
    private UserCategory userCategory;

    public User(int userId, String lastName, String name, String secondName, String login, String password) {
        this.userId = userId;
        this.lastName = lastName;
        this.name = name;
        this.secondName = secondName;
        this.login = login;
        this.password = password;
        if (login.startsWith("s")) userCategory = UserCategory.STUDENT;
        else if (login.startsWith("t")) userCategory = UserCategory.TEACHER;
        else if (login.startsWith("a")) userCategory = UserCategory.ADMIN;
    }

    public User() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

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

    public UserCategory getUserCategory() {
        return userCategory;
    }

    public void setUserCategory(UserCategory userCategory) {
        this.userCategory = userCategory;
    }

    @Override
    public String toString() {
        return "id: " + userId +
                "\tФамилия: " + lastName +
                "\tИмя: " + name + '\'' +
                "\tОтчество: " + secondName +
                "\tuserCategory: " + userCategory;
    }
}

enum UserCategory{
    STUDENT,
    TEACHER,
    ADMIN
}