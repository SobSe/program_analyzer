import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {
    public static String LETTERS = "abc";
    public static int COUNT_TEXTS = 10_000;
    public static int TEXT_LENGTH = 100_000;
    public static ArrayBlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) {
        Thread generator = new Thread(Main::addToQueue);
        Thread analyzerA = new Thread(() -> analyze('a'));
        Thread analyzerB = new Thread(() -> analyze('b'));
        Thread analyzerC = new Thread(() -> analyze('c'));

        generator.start();
        analyzerA.start();
        analyzerB.start();
        analyzerC.start();

        try {
            generator.join();
        } catch (InterruptedException e) {
            return;
        }

        analyzerA.interrupt();
        analyzerB.interrupt();
        analyzerC.interrupt();

    }

    public static void addToQueue() {
        for (int i = 0; i < COUNT_TEXTS; i++) {
            String text = generateText(LETTERS, TEXT_LENGTH);
            try {
                queueA.put(text);
                queueB.put(text);
                queueC.put(text);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public static void analyze(char symbol) {
        ArrayBlockingQueue<String> queue = null;
        switch (symbol) {
            case 'a' : queue = queueA; break;
            case 'b' : queue = queueB; break;
            case 'c' : queue = queueC; break;
        }
        int maxCountSymbols = 0;
        String result = null;
        while (!Thread.interrupted()) {
            try {
                String text = queue.take();
                int count = (int) text.chars()
                        .filter((s) -> (s == symbol))
                        .count();
                if (count > maxCountSymbols) {
                    maxCountSymbols = count;
                    result = text;
                }
            } catch (InterruptedException e) {
                break;
            }
        }
        String text = null;
        while ((text = queue.poll()) != null) {
            int count = (int) text.chars()
                    .filter((s) -> (s == symbol))
                    .count();
            if (count > maxCountSymbols) {
                maxCountSymbols = count;
                result = text;
            }
        }
        System.out.printf("Строка с максимальным колчиеством символа %c: %s\n", symbol, result);
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
