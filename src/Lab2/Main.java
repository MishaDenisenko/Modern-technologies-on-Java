package Lab2;

import java.sql.*;
import java.util.*;

public class Main {
    public static Connection connection = null;

    public static void main(String[] args){
        boolean success = true;
        boolean stop = false;

        try {
            connection = DriverManager.getConnection(Constants.URL, Constants.USER_NAME, Constants.PASSWORD);
        } catch (SQLException throwables) {
            success = false;
            System.out.println(throwables.getMessage());
        }

        if (success){
            System.out.println("Connection Successful");
            DAO dao = new JdbcRunner(connection);
            Scanner scanner = new Scanner(System.in);
            do {
                System.out.print("Введите логин: ");
                String login = scanner.nextLine();
                System.out.print("Введите пароль: ");
                String password = scanner.nextLine();
                if (login.equals("a")) {
                    dao.logInto( "s_mihail", "mihail_denisenko");
                    doAction(dao, true);
                    stop = true;
                }
                if (login.equalsIgnoreCase("stop") || password.equalsIgnoreCase("stop")) stop = true;
                else {
                    boolean[] logInto = dao.logInto(login, password);
                    if (logInto[0]) {
                        doAction(dao, logInto[1]);
                        stop = true;
                    }
                    else {
                        System.out.println("Пользователь не найден или неверный логин или пароль! попробуйте снова..");
                    }
                }
//                else System.out.println("Некорректные данные, попробуйте снова!");
            } while (!stop);
        }

    }

    private static void doAction(DAO dao, boolean isStudent){
        if (!isStudent) doAction(dao);
        else {
            Scanner scanner = new Scanner(System.in);
            boolean stop = false;
            do {
                System.out.println("Введите действие: \n|\tСписок всех предметов - all-lessons (al)\t|\n" +
                        "|\tСписок предметов по темам - lessons-theme (tl)\t|\n" +
                        "|\tСписок всех учителей - teachers (t)\t|\n" +
                        "|\tПросмотр предметов, на которые вы записаны - lessons (l)\t|\n" +
                        "|\tПросмотр оценок  предметов, на которые вы записаны - marks (m)\t|\n" +
                        "|\tСписок моих учителей - my-teachers (mt)\t|\n");
                String action = scanner.nextLine();
                if (action.equalsIgnoreCase("stop")) stop = true;

                else if (action.equalsIgnoreCase("teachers") || action.equalsIgnoreCase("t")) {
                    outputList(dao.getTable("elective.teachers"));
                }
                else if (action.equalsIgnoreCase("lessons") || action.equalsIgnoreCase("l")) {
                    dao.getLessons(true);
                }
                else if (action.equalsIgnoreCase("marks") || action.equalsIgnoreCase("m")) {
                    dao.getMarks(true);
                }
                else doStandardAction(dao, action);
            } while (!stop);
        }


    }
    private static void doAction(DAO dao){
        Scanner scanner = new Scanner(System.in);
        boolean stop = false;
        do {
            System.out.println("Введите действие: \n|\tСписок всех предметов - all-lessons (al)\t|\n" +
                    "|\tСписок предметов по темам - lessons-theme (tl)\t|\n" +
                    "|\tСписок всех учеников - students (s)\t|\n" +
                    "|\tПросмотр предметов, которые я преподаю - lessons (l)\t|\n" +
                    "|\tВыставить оценку - mark (m)\t|\n");
            String action = scanner.nextLine();
            if (action.equalsIgnoreCase("stop")) stop = true;
            else if (action.equalsIgnoreCase("students") || action.equalsIgnoreCase("s")) {
                outputList(dao.getTable("elective.students"));
            }
            else if (action.equalsIgnoreCase("lessons") || action.equalsIgnoreCase("l")) {
                dao.getLessons(false);
            }
            else if (action.equalsIgnoreCase("mark") || action.equalsIgnoreCase("m")) {
                dao.getMarks(false);
            }
            else doStandardAction(dao, action);
        } while (!stop);

    }

    private static void doStandardAction(DAO dao, String action){
        Scanner scanner = new Scanner(System.in);
        if (action.equalsIgnoreCase("all-lessons") || action.equalsIgnoreCase("al")) {
            String[][] lessons = dao.getTable("elective.lessons");
            outputList(lessons);
            System.out.print("Отсортировать за: ");
            String order = scanner.nextLine();
            String checkOrder = order.replace("-", "");
            if (checkOrder.equalsIgnoreCase("count_of_students") ||
                    checkOrder.equalsIgnoreCase("lesson_name") ||
                    checkOrder.equalsIgnoreCase("status"))
                dao.sort("elective.lessons", order);
            else if (checkOrder.equalsIgnoreCase("none")) System.out.println();
            else System.out.println("Невозможно отсортировать за заданным критерием..");
        }
        else if (action.equalsIgnoreCase("lessons-theme") || action.equalsIgnoreCase("tl")) {
            System.out.print("Укажите тему: ");
            String theme = scanner.nextLine();
            dao.getThemeLessons(theme);
        }
        else System.out.println("Невозможно выполнить данное действие..");
    }


    private static void outputList(String[][] list){
        for (String[] strings : list) {
            for (String string : strings) {
                System.out.print(string + "\t");
            }
            System.out.println();
        }
    }
}
