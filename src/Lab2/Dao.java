package Lab2;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface Dao {
    @Deprecated
    String[][] getTable(String table);
    @Deprecated
    boolean[] logInto(String login, String password);
    @Deprecated
    void getLessons(boolean isStudent);
    @Deprecated
    void getTeacherLessons();
    @Deprecated
    void getThemeLessons(String theme);
    @Deprecated
    void sort(String table, String order);
    @Deprecated
    void getMarks(boolean isStudent);


    ArrayList<User> getUsers();
    ArrayList<Lesson> getLessons();
    ArrayList<Lesson> getLessons(String[] teacherName);
    User logUserInto(String login, String password);
    HashMap<Lesson, Integer> getMarks(User user);
    boolean setMark(String[] studentName, int lessonId, int mark);
    boolean setMark(int id, int lessonId, int mark);
    boolean signUp(User user, String lesson);
    User addTeacher(String[] teacherInfo);
    Lesson addLesson(String lessonTheme, int teacher, String lessonName);
    boolean changeCourseStatus(String lessonTheme, String lessonName, int status);
}
