package Lab2;

import java.sql.*;

public interface DAO {
    String[][] getTable(String table);
    boolean[] logInto(String login, String password);
    void getLessons(boolean isStudent);
    void getTeacherLessons();
    void getThemeLessons(String theme);
    void getMarks(boolean isStudent);
    void sort(String table, String order);
}
