package Lab1;

import java.util.Scanner;
import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static void main(String[] args) {
        boolean isTruePath = false;
        boolean isStopped = false;
        File directory;

        System.out.println("Введите путь начальной директории:\n");

        do {
            Scanner inputText = new Scanner(System.in);
            String dirName = inputText.nextLine();
            directory = new File(dirName);

            if (dirName.toLowerCase().equals("stop")) isStopped = true;
            else if (!directory.exists())
                System.out.println("Указан неверный путь к директории, повторите попытку\nИли введите слово \"stop\":");
            else {
                isTruePath = true;
            }
        } while (!isStopped && !isTruePath);

        BlockingQueue <File> queue = new ArrayBlockingQueue<>(10);
        Thread founder = new Thread(new FileFounder(queue, directory));
        founder.start();
        Thread search = new Thread(new SearchTask(queue));
        search.start();

    }
}
