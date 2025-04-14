package service.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

public class ConsoleInputReadProcessor implements Runnable {

    private final BlockingQueue<String> queue;

    public ConsoleInputReadProcessor(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                queue.put(line);
            }
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
