package Lab2;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class JdbcRunner implements Dao {
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
    public ArrayList<User> getUsers(){
        query = "SELECT * FROM elective.users";
        ArrayList<User> users = new ArrayList<>();
        try {
            resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                int id = resultSet.getInt(1);
                String lastName = resultSet.getString(2);
                String name = resultSet.getString(3);
                String firstName = resultSet.getString(4);
                String login = resultSet.getString(5);
                String password = resultSet.getString(6);

                users.add(new User(id, lastName, name, firstName, login, password));
            }
        } catch (SQLException throwables) {
            System.err.println(throwables.getMessage());
        }

        return users;
    }

    @Override
    public ArrayList<Lesson> getLessons(){
        query = "SELECT * FROM elective.lessons";
        return getLessonsFromDB(query);
    }

    @Override
    public ArrayList<Lesson> getLessons(String[] teacherName) {
        if (teacherName.length != 3) {
            System.out.println("Некорректные данные");
            return new ArrayList<>();
        }
        else {
            int id = 0;
            query = String.format("SELECT * FROM elective.users WHERE last_name=\"%s\" AND name=\"%s\" and second_name=\"%s\"", teacherName[0], teacherName[1], teacherName[2]);
            try {
                resultSet = statement.executeQuery(query);
                if (resultSet.next()) id = resultSet.getInt(1);
                resultSet.close();
            } catch (SQLException throwables) {
                System.err.println(throwables.getMessage());
            }

            query = "SELECT * FROM elective.lessons WHERE teacher=" + id;
            return getLessonsFromDB(query);
        }

    }

    private ArrayList<Lesson> getLessonsFromDB(String query) {
        ArrayList<Lesson> lessons = new ArrayList<>();
        try {
            resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                int id = resultSet.getInt(1);
                String theme = resultSet.getString(2);
                int teacherId = resultSet.getInt(3);
                int countStudents = resultSet.getInt(4);
                String lessonName = resultSet.getString(5);
                int status = resultSet.getInt(6);

                lessons.add(new Lesson(id, theme, teacherId, countStudents, lessonName, status));
            }
            return lessons;
        } catch (SQLException throwables) {
            System.err.println(throwables.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public User logUserInto(String login, String password) {
        query = "SELECT * FROM elective.users WHERE login=" + "\"" + login + "\"";

        try {
            resultSet = statement.executeQuery(query);
            resultSet.next();
            String userPassword = resultSet.getString(6);
            if (userPassword.equals(password)) {
                id = resultSet.getInt(1);
                String lastName = resultSet.getString(2);
                String name = resultSet.getString(3);
                String secondName = resultSet.getString(4);
                System.out.println("Добрый день, " + name + " " + secondName + "!");
                return new User(id, lastName, name, secondName, login, password);
            }
        } catch (SQLException throwables) {
            System.err.println(throwables.getMessage());
        }
        return null;
    }

    @Override
    public HashMap<Lesson, Integer> getMarks(User user) {
        HashMap<Lesson, Integer> lessonsMarks = new HashMap<>();
        query = "SELECT * FROM elective.student_lesson WHERE students_id=" + user.getUserId();
        ArrayList<Integer> lessons = new ArrayList<>();
        ArrayList<Integer> marks = new ArrayList<>();

        try {
            resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                lessons.add(resultSet.getInt(2));
                marks.add(resultSet.getInt(3));
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }

        StringBuilder request = new StringBuilder("SELECT * FROM elective.lessons WHERE idlessons=" + lessons.get(0));
        for (int i = 1; i < lessons.size(); i++) {
            request.append(" or ").append("idlessons=").append(lessons.get(i));
        }
        ArrayList<Lesson> lessonsName = getLessonsFromDB(request.toString());

        for (int i = 0; i < lessons.size(); i++) {
            lessonsMarks.put(lessonsName.get(i), marks.get(i));
        }

        return lessonsMarks;
    }

    @Override
    public boolean setMark(String[] studentName, int lessonId, int mark) {
        if (studentName.length != 3) return false;
        else {
            query = String.format("SELECT * FROM elective.users WHERE last_name=\"%s\" AND name=\"%s\" and second_name=\"%s\"", studentName[0].trim(), studentName[1].trim(), studentName[2].trim());
            int id = 0;
            try {
                ResultSet resultSet = statement.executeQuery(query);
                if (resultSet.next()) id = resultSet.getInt(1);
                resultSet.close();
            } catch (SQLException throwables) {
                System.err.println(throwables.getMessage());
            }

            return setMarkIntoDB(id, lessonId, mark);
        }
    }

    @Override
    public boolean setMark(int id, int lessonId, int mark) {
        return setMarkIntoDB(id, lessonId, mark);
    }

    @Override
    public boolean signUp(User user, String lesson) {
        query = "SELECT * FROM student_lesson WHERE students_id=" + user.getUserId();
        ArrayList<Integer> lessonsId = new ArrayList<>();
        try {
            resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                lessonsId.add(resultSet.getInt(2));
            }
            resultSet.close();
        } catch (SQLException throwables) {
            System.err.println(throwables.getMessage());
        }
        StringBuilder request = new StringBuilder("SELECT * FROM lessons WHERE idlessons=" + lessonsId.get(0));
        for (int i = 1; i < lessonsId.size(); i++) {
            request.append(" or ").append("idlessons=").append(lessonsId.get(i));
        }

        ArrayList<Lesson> studentLessons = getLessonsFromDB(request.toString());
        for (Lesson studentLesson : studentLessons) {
            if (studentLesson.getLessonName().equalsIgnoreCase(lesson)) {
                System.out.println("Вы уже записаны на этот курс.");
                return false;
            }
        }
        ArrayList<Lesson> allLessons = getLessonsFromDB("SELECT * FROM elective.lessons");
        for (Lesson l : allLessons) {
            if (l.getLessonName().equalsIgnoreCase(lesson) && l.getStatus() == -1){
                try {
                    query = "INSERT INTO elective.student_lesson (students_id, lesson_id, mark) VALUES (" + user.getUserId() + ", " + l.getLessonId() + ", " + 0 + ")";
                    statement.execute(query);
                    query = "UPDATE lessons SET count_of_students=" + (l.getCountOfStudents()+1) + " WHERE idlessons=" + l.getLessonId();
                    statement.execute(query);

                    return true;
                } catch (SQLException throwables) {
                    System.err.println(throwables.getMessage());
                }
            }
            else if (l.getLessonName().equalsIgnoreCase(lesson) && l.getStatus() != -1)
                System.out.println("Невозможно записаться на курс");
        }

        return false;
    }

    @Override
    public User addTeacher(String[] teacherInfo) {

        try {
            query = "SELECT * FROM users WHERE login=\"" + teacherInfo[3] + "\"";
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                System.out.println("Такой пользователь уже существует");
                return null;
            }
            query = "INSERT INTO users (last_name, name, second_name, login, password) VALUES (\"" + teacherInfo[0] + "\", \"" + teacherInfo[1] + "\", \"" + teacherInfo[2] + "\", \"" + teacherInfo[3] + "\", \"" + teacherInfo[4] + "\")";

            statement.execute(query);
            resultSet.close();
            query = "SELECT * FROM users WHERE login=\"" + teacherInfo[3] + "\"" + " AND password=\"" + teacherInfo[4] + "\"";
            resultSet = statement.executeQuery(query);
            if (resultSet.next()){
                int id = resultSet.getInt(1);
                String lastName = resultSet.getString(2);
                String name = resultSet.getString(3);
                String firstName = resultSet.getString(4);
                String login = resultSet.getString(5);
                String password = resultSet.getString(6);

                return new User(id, lastName, name, firstName, login, password);
            }
        } catch (SQLException throwables) {
            System.err.println(throwables.getMessage());
        }
        return null;
    }

    @Override
    public Lesson addLesson(String lessonTheme, int teacher, String lessonName) {
        try {
            query = "SELECT * FROM lessons WHERE lesson_theme=\"" + lessonTheme + "\"" +  " AND lesson_name=\"" + lessonName + "\"" ;
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                System.out.println("Такой предмет уже присутствует");
                return null;
            }
            query = "INSERT INTO lessons (lesson_theme, teacher, count_of_students, lesson_name, status) VALUES (\"" + lessonTheme + "\", " + teacher + ", " + 0 + ", \"" + lessonName + "\", " + -1 + ")";

            statement.execute(query);
            resultSet.close();
            query = "SELECT * FROM lessons WHERE lesson_theme=\"" + lessonTheme + "\"" +  " AND lesson_name=\"" + lessonName + "\"" ;
            resultSet = statement.executeQuery(query);
            if (resultSet.next()){
                int id = resultSet.getInt(1);
                String theme = resultSet.getString(2);
                int teacherId = resultSet.getInt(3);
                int countStudents = resultSet.getInt(4);
                String name = resultSet.getString(5);
                int status = resultSet.getInt(6);

                return new Lesson(id, theme, teacherId, countStudents, name, status);
            }
        } catch (SQLException throwables) {
            System.err.println(throwables.getMessage());
        }

        return null;
    }

    @Override
    public boolean changeCourseStatus(String lessonTheme, String lessonName, int status) {
        try {
            query = "SELECT * FROM lessons WHERE lesson_theme=\"" + lessonTheme + "\"" +  " AND lesson_name=\"" + lessonName + "\"" ;
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                query = "UPDATE lessons SET status=" + status +" WHERE lesson_theme=\"" + lessonTheme + "\"" +  " AND lesson_name=\"" + lessonName + "\"" ;
                statement.execute(query);
                return true;
            }
        } catch (SQLException throwables) {
            System.err.println(throwables.getMessage());
        }

        return false;
    }

    private boolean setMarkIntoDB(int id, int lessonId, int mark) {
        if (mark < 0 || mark > 100) return false;
        query = "UPDATE elective.student_lesson SET mark=" + mark + " WHERE students_id=" + id + " AND lesson_id=" + lessonId;
        try {
            statement.execute(query);
            return true;
        } catch (SQLException throwables) {
            System.err.println(throwables.getMessage());
        }
        return false;
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
                String userPassword = resultSet.getString(6).replace(" ", "").replace("'", "");

                if (userPassword.equalsIgnoreCase(password)) {
                    String name = resultSet.getString(3).replace(" ", "").replace("'", "");
                    String secondName = resultSet.getString(4).replace(" ", "").replace("'", "");
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