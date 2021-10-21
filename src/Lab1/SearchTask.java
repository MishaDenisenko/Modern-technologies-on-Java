package Lab1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class SearchTask implements Runnable {
    private final BlockingQueue<File> queue;
    private final String[] punctuationMarks = {",", ".", "!", "\"", ":", "?", "-", "(", ")", ";", "/", "\n"};

    @Override
    public void run() {
        while (true) {
            File file = null;
            try {
                file = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (file == FileFounder.getEXIT()) {
                try {
                    queue.put(file);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            } else changeFile(file);
        }
    }

    private void changeFile(File file) {
        if (file.canRead() && file.getName().endsWith(".txt")) {
            byte[] fileText = {};
            try {
                FileInputStream inputStream = new FileInputStream(file);
                int length = inputStream.available();
                fileText = new byte[length];
                inputStream.read(fileText);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String text = new String(fileText);
            String changedText = text;

            String[] words = text.split(" ");
            StringBuilder removedWords = new StringBuilder();
            for (String word : words) {
                for (String mark : punctuationMarks) {
                    if (word.contains(mark)) word = word.replace(mark, "");
                }
                if (word.length() >= 3 && word.length() <= 5) {
                    removedWords.append(word).append(" ");
                    changedText = changedText.replace(word + " ", "");
                }
            }

            String newText = "Было:\n" + text + "\n\nСтало:\n" + changedText + "\n\nСлова, которые были удалены:\n" + removedWords;

            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] newTextBytes = newText.getBytes();
                outputStream.write(newTextBytes);
                outputStream.close();
                System.out.println("Документ " + file.getName() + " исправлен.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Документ " + file.getName() + " не был исправлен.");
            }
            System.out.println("--------------");
        }
    }

    public SearchTask(BlockingQueue<File> queue) {
        this.queue = queue;
    }
}
