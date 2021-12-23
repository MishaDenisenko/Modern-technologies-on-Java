package Lab2;

public class Lesson extends BdSource{
    private int lessonId;
    private String lessonTheme;
    private int teacher;
    private int countOfStudents;
    private String lessonName;
    private int status;

    public Lesson(int lessonId, String lessonTheme, int teacher, int countOfStudents, String lessonName, int status) {
        this.lessonId = lessonId;
        this.lessonTheme = lessonTheme;
        this.teacher = teacher;
        this.countOfStudents = countOfStudents;
        this.lessonName = lessonName;
        this.status = status;
    }

    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public String getLessonTheme() {
        return lessonTheme;
    }

    public void setLessonTheme(String lessonTheme) {
        this.lessonTheme = lessonTheme;
    }

    public int getTeacher() {
        return teacher;
    }

    public void setTeacher(int teacher) {
        this.teacher = teacher;
    }

    public int getCountOfStudents() {
        return countOfStudents;
    }

    public void setCountOfStudents(int countOfStudents) {
        this.countOfStudents = countOfStudents;
    }

    public int getStatus() {
        return status;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Тема: '" + lessonTheme + '\'' +
                "\tУчитель: " + teacher +
                "\tСтудентов: " + countOfStudents +
                "\tНазвание: '" + lessonName + '\'' +
                "\tСтатус: " + status;
    }
}
