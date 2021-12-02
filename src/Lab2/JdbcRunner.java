package Lab2;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class JdbcRunner implements DAO{
    private final Connection connection;
    private Statement statement;
    private String query;
    private ResultSet resultSet;
    private int id;

    private HashMap<String, String> loginsAndPasswords;

    public JdbcRunner(Connection connection) {
        this.connection = connection;
        createStatement();
    }

    private void createStatement() {
        try {
            statement = connection.createStatement();
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
    }

    @Override
    public String[][] getTable(String table){
        query = "SELECT * FROM " + table;
        ArrayList<String[]> items = new ArrayList<>();

        try {
            resultSet = statement.executeQuery(query);
            int columns = resultSet.getMetaData().getColumnCount();

            if (!table.equals("elective.lessons")) columns -= 2;
            while (resultSet.next()) {
                String[] item = new String[columns-1];
                for (int i = 1; i < columns; i++) {
                    item[i-1] = resultSet.getString(i+1);
                }
                items.add(item);
            }
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }

        String[][] result = new String[items.size()][items.get(0).length];
        for (int i = 0; i < items.size(); i++) {
            System.arraycopy(items.get(i), 0, result[i], 0, items.get(0).length);
        }

        return result;
    }

    @Override
    public boolean[] logInto(String login, String password){
        boolean isStudent = false;
        if (login.replace(" ", "").startsWith("t")) query = "SELECT * FROM elective.teachers WHERE login=" + "\" '" + login + "'\"";
        else if (login.replace(" ", "").startsWith("s")) {
            query = "SELECT * FROM elective.students WHERE login=" + "\" '" + login + "'\"";
            isStudent = true;
        }

        try {
            resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                String name = resultSet.getString(3).replace(" ", "").replace("'", "");
                String secondName = resultSet.getString(4).replace(" ", "").replace("'", "");
                String userPassword = resultSet.getString(6).replace(" ", "").replace("'", "");

                if (userPassword.equalsIgnoreCase(password)) {
                    id = resultSet.getInt(1);
                    System.out.println("Добрый день, " + name + " " + secondName + "!");
                    return new boolean[]{true, isStudent};
                }
            }
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }

        return new boolean[]{false, false};
    }

    @Override
    public void getLessons(boolean isStudent){
        if (isStudent) query = "SELECT * FROM elective.students_lessons WHERE students_id=" + id;
        else getTeacherLessons();
        int[] lessons = new int[12];
        try {
            resultSet = statement.executeQuery(query);
            int i = 0;
            while (resultSet.next()){
                lessons[i] = resultSet.getInt(2);
                i++;
            }

            String[] lessonsName = getLessonsByID(lessons);
            for (String l : lessonsName) {
                System.out.println(l);
            }
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
    }

    @Override
    public void getTeacherLessons() {
        query = "SELECT * FROM elective.lessons WHERE teacher=" + id;
        try {
            resultSet = statement.executeQuery(query);
            System.out.println("Название:\tКол-во студентов:\tТема");
            while (resultSet.next()){
                System.out.println(resultSet.getString(5) + "\t" + resultSet.getString(4 ) + "\t" + resultSet.getString(2 ));
            }
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
    }

    @Override
    public void getThemeLessons(String theme) {
        query = "SELECT * FROM elective.lessons WHERE lesson_theme=" + "\" '" + theme + "'\"";
        try {
            resultSet = statement.executeQuery(query);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columns = resultSetMetaData.getColumnCount();

            System.out.println("Тема:\tПреподователь:\tКол-во студентов:\tНазвание:\tСтатус:");

            System.out.println();
            while (resultSet.next()){
                for (int i = 1; i < columns; i++) {
                    System.out.print(resultSet.getString(i+1) + "\t");
                }
                System.out.println();
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
    }

    @Override
    public void getMarks(boolean isStudent){
        if (isStudent) query = "SELECT * FROM elective.students_lessons WHERE students_id=" + id;
        int[] marks = new int[12];
        int[] lessons = new int[12];
        try {
            resultSet = statement.executeQuery(query);
            int i = 0;
            while (resultSet.next()){
                lessons[i] = resultSet.getInt(2);
                marks[i] = resultSet.getInt(3);
                i++;
            }
            String[] lessonsName = getLessonsByID(lessons);
            for (int j = 0; j < lessonsName.length; j++) {
                System.out.println(lessonsName[j] + "\t================\t" + marks[j]);
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
    }

    @Override
    public void sort(String table, String order) {
        if (order.startsWith("-")) query = "SELECT * FROM " + table + " ORDER BY " + order.replace("-", "") + " DESC";
        else query = "SELECT * FROM " + table + " ORDER BY " + order;
        try {
            resultSet = statement.executeQuery(query);
            int columns = resultSet.getMetaData().getColumnCount();

            if (!table.equals("elective.lessons")) columns -= 2;
            while (resultSet.next()) {
                for (int i = 1; i < columns; i++) {
                    String info = resultSet.getString(i+1);
                    System.out.print(info + "\t");
                }
                System.out.println();
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
    }

    private String[] getLessonsByID(int[] id){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT * FROM elective.lessons WHERE idlessons=").append(id[0]);
        String[] lessons = new String[12];
        for (int i =1; i < id.length; i++) {
            stringBuilder.append(" or idlessons=").append(id[i]);
        }
        try {
            query = stringBuilder.toString();
            resultSet = statement.executeQuery(query);
            int i = 0;
            while (resultSet.next()){
                lessons[i] = resultSet.getString(5);
                i++;
            }
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
        return lessons;
    }
}