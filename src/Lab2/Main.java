package Lab2;

import java.sql.*;
import java.util.*;

public class Main {
    public static Connection connection = null;
    private static User user;
    private static User newTeacher;
    private static Lesson newLesson;

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
            Dao dao = new JdbcRunner(connection);
            Scanner scanner = new Scanner(System.in);
            do {
                System.out.print("Введите логин: ");
                String login = scanner.nextLine();
                System.out.print("Введите пароль: ");
                String password = scanner.nextLine();
                if (login.equals("a")) {
//                    user = dao.logUserInto("a_nikolay", "nikolay_stepanovich");
//                    user = dao.logUserInto("t_alexey", "alexey_namidin");
                    user = dao.logUserInto("s_mihail", "mihail_denisenko");
//                    dao.logInto( "s_mihail", "mihail_denisenko");
                    doAction(dao, user.getUserCategory());
                    stop = true;
                }
                if (login.equalsIgnoreCase("stop") || password.equalsIgnoreCase("stop")) stop = true;
                else {
                    user = dao.logUserInto(login, password);
                    if (user != null) {
                        doAction(dao, user.getUserCategory());
                        stop = true;
                    }
                    else {
                        System.out.println("Пользователь не найден или неверный логин или пароль! попробуйте снова..");
                    }
                }
            } while (!stop);
        }

    }

    private static void doAction(Dao dao, UserCategory userCategory){
        if (userCategory == UserCategory.TEACHER) doAction(dao);
        else if (userCategory == UserCategory.ADMIN) doAdminAction(dao);
        else {
            Scanner scanner = new Scanner(System.in);
            boolean stop = false;
            do {
                System.out.println("Введите действие: \n" +
                        "|\tСписок всех предметов - all-lessons (al)\t|\n" +
                        "|\tСписок предметов по темам - lessons-theme (tl)\t|\n" +
                        "|\tСписок предметов преподавателя - lessons-teacher (lt)\t|\n" +
                        "|\tСписок всех учителей - teachers (t)\t|\n" +
                        "|\tПросмотр предметов, на которые вы записаны - lessons (l)\t|\n" +
                        "|\tПредметы, которые еще не закончились - current-lessons (cl)\t|\n" +
                        "|\tЗавершенные предметы - completed-lessons (cpl)\t|\n" +
                        "|\tЗаписаться на курс - sign-in (sn)\t|\n" +
                        "|\tПросмотр оценок предметов, на которые вы записаны - marks (m)\t|\n");
                String action = scanner.nextLine();
                if (action.equalsIgnoreCase("stop")) stop = true;

                else if (action.equalsIgnoreCase("teachers") || action.equalsIgnoreCase("t")) {
                    ArrayList<User> users = dao.getUsers();
                    ArrayList<User> teachers = new ArrayList<>();
                    for (User user1 : users) {
                        if (user1.getUserCategory() == UserCategory.TEACHER) teachers.add(user1);
                    }
                    printSource(teachers.toArray(BdSource[]::new));
                }
                else if (action.equalsIgnoreCase("lessons") || action.equalsIgnoreCase("l")) {

                    showLessons(dao, 0, true);
                }
                else if (action.equalsIgnoreCase("current-lessons") || action.equalsIgnoreCase("cl")) {
                    showLessons(dao, 0, false);
                }
                else if (action.equalsIgnoreCase("completed-lessons") || action.equalsIgnoreCase("cpl")) {
                    showLessons(dao, 1, false);
                }
                else if (action.equalsIgnoreCase("sign-in") || action.equalsIgnoreCase("sn")) {
                    System.out.println("Введите название курса: ");
                    String lesson = scanner.nextLine();
                    boolean success = dao.signUp(user, lesson);
                    if (success) System.out.println("Вы успешно записались");
                    else System.out.println("Ошибка записи!");
                }
                else if (action.equalsIgnoreCase("marks") || action.equalsIgnoreCase("m")) {
                    showLessons(dao, 0, true);
                }
                else doStandardAction(dao, action);
            } while (!stop);
        }
    }

    private static void doAdminAction(Dao dao) {
        Scanner scanner = new Scanner(System.in);
        boolean stop = false;
        do {
            System.out.println("Введите действие: \n" +
                    "|\tСписок всех предметов - all-lessons (al)\t|\n" +
                    "|\tСписок предметов по темам - lessons-theme (tl)\t|\n" +
                    "|\tСписок предметов преподавателя - lessons-teacher (lt)\t|\n" +
                    "|\tСписок всех учителей - teachers (t)\t|\n" +
                    "|\tДобавить преподавателя - add-teacher (at)\t|\n" +
                    "|\tДобавить предмет - add-lesson (add -l)\t|\n" +
                    "|\tИзменить статус предмета - change-status (cs)\t|\n");
            String action = scanner.nextLine();
            if (action.equalsIgnoreCase("stop")) stop = true;

            else if (action.equalsIgnoreCase("teachers") || action.equalsIgnoreCase("t")) {
                printSource(dao.getUsers().toArray(BdSource[]::new));
            }
            else if (action.equalsIgnoreCase("add-teacher") || action.equalsIgnoreCase("at")) {
                System.out.print("Укажите ФИО преподавателя, его логин и пароль через ':'");
                String[] info = scanner.nextLine().strip().split(":");
                if (info.length != 5) System.out.println("Некорректный ввод");
                else {
                    if (!info[3].startsWith("t_")) info[3] = "t_" + info[3];
                    newTeacher = dao.addTeacher(info);
                    if (newTeacher != null) System.out.println("Пользователь добавлен");
                }
            }
            else if (action.equalsIgnoreCase("add-lesson") || action.equalsIgnoreCase("add -l")) {
                System.out.print("Укажите тему, id учителя и название предмета через ':'");
                String[] lesson = scanner.nextLine().split(":");
                if (lesson.length != 3) System.out.println("Некорректный ввод");
                else {
                    try {
                        int teacherId = Integer.parseInt(lesson[1]);
                        newLesson = dao.addLesson(lesson[0], teacherId, lesson[2]);
                        if (newLesson != null) System.out.println("Предмет добавлен");
                    } catch (NumberFormatException e) {
                        System.out.println("Некорректный id");
                    }
                }
            }
            else if (action.equalsIgnoreCase("change-status") || action.equalsIgnoreCase("cs")) {
                System.out.print("Укажите тему, название b статус предмета через ':'");
                String[] lesson = scanner.nextLine().split(":");
                if (lesson.length != 3) System.out.println("Некорректный ввод");
                else {
                    try {
                        int status = Integer.parseInt(lesson[2]);
                        if (status < -1 || status > 1) System.out.println("Неверный статус");
                        else {
                            boolean success = dao.changeCourseStatus(lesson[0], lesson[1], status);
                            if (success) System.out.println("Статус изменен");
                        }

                    } catch (NumberFormatException e) {
                        System.out.println("Некорректный статус");
                    }
                }
            }

            else doStandardAction(dao, action);
        } while (!stop);
    }

    private static void doAction(Dao dao){
        boolean stop = false;
        do {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Введите действие: \n" +
                    "|\tСписок всех предметов - all-lessons (al)\t|\n" +
                    "|\tСписок предметов по темам - lessons-theme (tl)\t|\n" +
                    "|\tСписок всех учеников - students (s)\t|\n" +
                    "|\tПросмотр предметов, которые я преподаю - lessons (l)\t|\n" +
                    "|\tВыставить оценку - mark (m)\t|\n");
            String action = scanner.nextLine();
            if (action.equalsIgnoreCase("stop")) stop = true;
            else if (action.equalsIgnoreCase("students") || action.equalsIgnoreCase("s")) {
                ArrayList<User> users = dao.getUsers();
                ArrayList<User> students = new ArrayList<>();
                for (User user : users) {
                    if (user.getUserCategory() == UserCategory.STUDENT) students.add(user);
                }
                printSource(students.toArray(BdSource[]::new));
            }
            else if (action.equalsIgnoreCase("lessons") || action.equalsIgnoreCase("l")) {
                ArrayList<Lesson> lessons = dao.getLessons();
                ArrayList<Lesson> myLessons = new ArrayList<>();
                for (Lesson lesson : lessons) {
                    if (lesson.getTeacher() == user.getUserId()) myLessons.add(lesson);
                }
                printSource(myLessons.toArray(BdSource[]::new));
            }
            else if (action.equalsIgnoreCase("mark") || action.equalsIgnoreCase("m")) {
                boolean correctData = false;
                System.out.println("Введите ФИО или id ученика: ");
                String input = scanner.nextLine();
                int id = -1;
                String[] name = input.split(" ");
                if (name.length == 1){
                    try {
                        id = Integer.parseInt(name[0]);
                    } catch (NumberFormatException e) {
                        System.err.println("Неверный id");
                    }
                }
                System.out.print("Введите предмет: ");
                String lessonName = scanner.nextLine().trim();
                ArrayList<Lesson> myLessons = dao.getLessons(new String[]{user.getLastName(), user.getName(), user.getSecondName()});
                for (Lesson lesson : myLessons) {
                    if (lesson.getLessonName().equalsIgnoreCase(lessonName)){
                        System.out.print("Введите оценку: ");
                        int mark = scanner.nextInt();
                        if (id > 0) correctData = dao.setMark(id, lesson.getLessonId(), mark);
                        else correctData = dao.setMark(name, lesson.getLessonId(), mark);
                        break;
                    }
                }

                if (!correctData) System.out.println("Некорректные данные");
                else System.out.println("Успешно");
            }

            else doStandardAction(dao, action);
        } while (!stop);

    }

    private static void showLessons(Dao dao, int i, boolean all) {
        HashMap<Lesson, Integer> currentLessons = dao.getMarks(user);
        if (all){
            for (Map.Entry<Lesson, Integer> entry : currentLessons.entrySet()) {
                System.out.println(entry.getKey() + " + Оцека: " + entry.getValue());
            }
        }
        else {
            for (Map.Entry<Lesson, Integer> entry : currentLessons.entrySet()) {
                if (entry.getKey().getStatus() == i) System.out.println(entry.getKey() + " + Оцека: " + entry.getValue());
            }
        }
    }

    private static void doStandardAction(Dao dao, String action){
        Scanner scanner = new Scanner(System.in);
        if (action.equalsIgnoreCase("all-lessons") || action.equalsIgnoreCase("al")) {
            ArrayList<Lesson> lessons = dao.getLessons();
            System.out.print("Отсортировать за: ");
            String order = scanner.nextLine();
            String checkOrder = order.replace("-", "");
            if (checkOrder.equalsIgnoreCase("none")) {
                System.out.println();
                printSource(lessons.toArray(BdSource[]::new));
            }
            else if (checkOrder.equalsIgnoreCase("count_of_students")){
                Comparator<Lesson> lessonsCountOFStudents;
                if (order.startsWith("-")) lessonsCountOFStudents = Comparator.comparingInt(Lesson::getCountOfStudents);
                else lessonsCountOFStudents = (o1, o2) -> (o2.getCountOfStudents()) - o1.getCountOfStudents();

                lessons.sort(lessonsCountOFStudents);
                printSource(lessons.toArray(BdSource[]::new));
            }
            else if (checkOrder.equalsIgnoreCase("lesson_name")){
                Comparator<Lesson> lessonsName;
                if (order.startsWith("-")) lessonsName = (o1, o2) -> o2.getLessonName().compareTo(o1.getLessonName());
                else lessonsName = Comparator.comparing(Lesson::getLessonName);

                lessons.sort(lessonsName);
                printSource(lessons.toArray(BdSource[]::new));
            }
            else if (checkOrder.equalsIgnoreCase("status")){
                Comparator<Lesson> lessonsStatus;
                lessonsStatus = Comparator.comparingInt(Lesson::getStatus);
                lessons.sort(lessonsStatus);
                printSource(lessons.toArray(BdSource[]::new));
            }
            else System.out.println("Невозможно отсортировать за заданным критерием..");

        }
        else if (action.equalsIgnoreCase("lessons-theme") || action.equalsIgnoreCase("tl")) {
            System.out.print("Укажите тему: ");
            String theme = scanner.nextLine();
            ArrayList<Lesson> lessons = dao.getLessons();
            ArrayList<Lesson> lessonsOfTheme = new ArrayList<>();
            for (Lesson lesson : lessons) {
                if (lesson.getLessonTheme().equalsIgnoreCase(theme)) lessonsOfTheme.add(lesson);
            }
            printSource(lessonsOfTheme.toArray(BdSource[]::new));
        }
        else if (action.equalsIgnoreCase("lessons-teacher") || action.equalsIgnoreCase("lt")) {
            System.out.print("Укажите ФИО преподавателя: ");
            String[] teacher = scanner.nextLine().trim().split(" ");
            ArrayList<Lesson> teacherLessons = dao.getLessons(teacher);
            printSource(teacherLessons.toArray(BdSource[]::new));
        }
        else System.out.println("Невозможно выполнить данное действие..");
    }

    private static void printSource(BdSource[] bdSource){
        if (bdSource.length == 0) System.out.println("Ничего не удалось найти..");
        else {
            for (BdSource source : bdSource) {
                System.out.println(source.toString());
            }
        }
    }
}

// Калинин:Игорь:Денисович:igor:igor_kalinin
// Выборочные дисциплины:32:Основы ИИ