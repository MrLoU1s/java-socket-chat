import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 5555;

    // volatile ensures changes made by one thread are immediately visible to the other.
    private static volatile boolean running = true;

    public static void main(String[] args) {
        System.out.println("Connecting to server at " + HOST + ":" + PORT + "...");

        try (Socket socket = new Socket(HOST, PORT)) {
            System.out.println("Connected. Waiting for server...\n");

            BufferedReader serverIn  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter    serverOut = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader keyboard  = new BufferedReader(new InputStreamReader(System.in));

            // READ THREAD — blocks on serverIn.readLine(), prints every server
            // message the instant it arrives, independent of keyboard activity.
            Thread readThread = new Thread(() -> {
                try {
                    String line;
                    while (running && (line = serverIn.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    if (running) {
                        System.out.println("Connection to server lost.");
                    }
                } finally {
                    running = false; // wake up the write thread if it's blocking
                }
            });
            // Daemon thread exits automatically when the main (write) thread finishes.
            readThread.setDaemon(true);
            readThread.start();

            // WRITE THREAD (this is the main thread) — reads keyboard input and
            // sends each line to the server.
            String input;
            while (running && (input = keyboard.readLine()) != null) {
                serverOut.println(input);
                if (input.equalsIgnoreCase("/quit")) {
                    running = false;
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Could not connect to server at " + HOST + ":" + PORT);
            System.err.println("Make sure the server is running first.");
        }

        System.out.println("Disconnected.");
    }
}
