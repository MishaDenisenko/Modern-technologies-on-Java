package Lab1;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class FileFounder implements Runnable{
    private final BlockingQueue<File> queue;
    private final File startDirectory;
    private static final File EXIT = new File("");

    @Override
    public void run() {
        try {
            runDirectory(startDirectory);
            queue.put(EXIT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void runDirectory(File currentDirectory) throws InterruptedException {
        File[] files = currentDirectory.listFiles();
        ArrayList<File> filesTxtList = new ArrayList<>();
        ArrayList<File> directoriesList = new ArrayList<>();

        for (File file : files) {
            if (file.getName().endsWith(".txt")) filesTxtList.add(file);
            else if (file.isDirectory()) directoriesList.add(file);
        }

        System.out.println("В папке " + currentDirectory.getName() + " найдено " + filesTxtList.size() + " текстовых документа(ов):");
        if (filesTxtList.size() > 0 ){
            System.out.print("| ");
            for (int i = 0; i < filesTxtList.size(); i++) {
                if (i < filesTxtList.size()-1) System.out.print(filesTxtList.get(i).getName() + "  ::  ");
                else System.out.print(filesTxtList.get(i).getName() + " ");
            }
            System.out.print("|");
        }
        System.out.println("\n");

        System.out.println("И " + directoriesList.size() + " дерикторий:");
        if (directoriesList.size() > 0 ){
            System.out.print("| ");
            for (int i = 0; i < directoriesList.size(); i++) {
                if (i < directoriesList.size() - 1) System.out.print(directoriesList.get(i).getName() + "  ::  ");
                else System.out.print(directoriesList.get(i).getName() + " ");
            }
            System.out.print("|");
        }
        System.out.println("\n");

        for (File file : files) {
            if (file.isDirectory()) runDirectory(file);
            else queue.put(file);
        }
    }

    public FileFounder(BlockingQueue<File> queue, File startDirectory) {
        this.queue = queue;
        this.startDirectory = startDirectory;
    }

    public static File getEXIT() {
        return EXIT;
    }
}
