import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            this.out = out;
            // Only add to the shared list after 'out' is assigned — broadcast()
            // calls sendMessage() which uses 'out', so it must never be null.
            Server.clients.add(this);

            // Username negotiation
            out.println("Enter your username:");
            username = in.readLine();
            if (username == null || username.isBlank()) {
                username = "Anonymous";
            }

            System.out.println(username + " connected. [" + Server.clients.size() + " online]");
            Server.broadcast(username + " has joined the chat.", this);
            out.println("Welcome, " + username + "! Commands: /list  /quit");
            out.println("--------------------------------------------------");

            // Main message loop — readLine() returns null when the client closes
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                } else if (message.equalsIgnoreCase("/list")) {
                    sendUserList();
                } else if (!message.isBlank()) {
                    Server.broadcast("[" + username + "]: " + message, this);
                }
            }

        } catch (IOException e) {
            // Client disconnected abruptly — handled in finally below
        } finally {
            Server.removeClient(this);
            if (username != null) {
                Server.broadcast(username + " has left the chat.", null);
                System.out.println(username + " disconnected. [" + Server.clients.size() + " online]");
            }
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private void sendUserList() {
        StringBuilder sb = new StringBuilder("-- Online users (" + Server.clients.size() + ") --\n");
        for (ClientHandler client : Server.clients) {
            sb.append("  ").append(client.username != null ? client.username : "connecting...").append("\n");
        }
        sb.append("----------------------------");
        sendMessage(sb.toString());
    }
}
