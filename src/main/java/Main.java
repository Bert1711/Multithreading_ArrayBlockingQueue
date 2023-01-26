
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    private static BlockingQueue<String> arrayBlockingQueue1 = new ArrayBlockingQueue<>(100);
    private static BlockingQueue<String> arrayBlockingQueue2 = new ArrayBlockingQueue<>(100);
    private static BlockingQueue<String> arrayBlockingQueue3 = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) {
        new Thread(()->{
            int lenght = 10_000;
            for (int i = 0; i < lenght; i++) {
                String text = generateText("abc", 100_000);
                try {
                    arrayBlockingQueue1.put(text);
                    arrayBlockingQueue2.put(text);
                    arrayBlockingQueue3.put(text);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread threadCountA = new Thread(() -> {
            char letter = 'a';
            int maxCountLetter = 0;
            for (int i = 0; i < 10000; i++) {
                try {
                    String text = arrayBlockingQueue1.take();
                    int countLetter = countLetter(text, letter);
                    if (maxCountLetter < countLetter)
                        maxCountLetter = countLetter;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Максимальное количество символов '" + letter + "' - " + maxCountLetter + " шт.");
        });

        Thread threadCountB = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            char letter = 'b';
            int maxCountLetter = 0;
//            while (!arrayBlockingQueue2.isEmpty()) { - Будет гонка. Поток по добавлению в очередь не успевает.
            for (int i = 0; i < 10000; i++) {
                try {
                    String text = arrayBlockingQueue2.take();
                    int countLetter = countLetter(text, letter);
                    if(maxCountLetter < countLetter)
                        maxCountLetter = countLetter;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Максимальное количество символов '" + letter + "' - " + maxCountLetter + " шт.");
        });

        Thread threadCountC = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            char letter = 'c';
            int maxCountLetter = 0;
            for (int i = 0; i < 10000; i++) {
                try {
                    String text = arrayBlockingQueue3.take();
                    int countLetter = countLetter(text, letter);
                    if(maxCountLetter < countLetter)
                        maxCountLetter = countLetter;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Максимальное количество символов '" + letter + "' - " + maxCountLetter + " шт.");
        });

        threadCountA.start();
        threadCountB.start();
        threadCountC.start();
        try {
            threadCountA.join();
            threadCountB.join();
            threadCountC.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static int countLetter(String text, char letter) {
        int countLetter = 0;
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(chars[i] == letter) {
                countLetter++;
            }
        }
        return countLetter;
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}

