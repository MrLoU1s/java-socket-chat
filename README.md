# Java Socket Programming Lab

A Distributed Systems lab (Strathmore University, Year 4.1) demonstrating client-server communication over TCP using Java Sockets. The project is structured as two progressive versions — each building on the concepts of the previous one.

---

## Project Structure

```
.
├── v1-basic/
│   ├── MyServer.java      # Single-client blocking server
│   └── MyClient.java      # Turn-based blocking client
│
└── v2-multithreaded/
    ├── Server.java        # Multi-client server with accept loop
    ├── ClientHandler.java # Dedicated thread per connected client
    └── Client.java        # Two-threaded client (read + write)
```

---

## v1-basic — Single-Client Blocking Chat

The original lab implementation. A server and client exchange messages in strict alternation: client sends, server responds, repeat. Either side types `bye` to end the session. The server exits after one client disconnects.

### Concepts Demonstrated

| Concept | Where it appears |
|---|---|
| `ServerSocket` | Opens port 5555; listens for connections |
| `Socket` | The actual communication channel |
| `BufferedReader` / `PrintWriter` | Stream wrapping chain for reading/writing text |
| Turn-based protocol | Client always speaks first; strict alternation |
| `"bye"` as termination signal | Application-level shutdown convention |
| Blocking I/O | `readLine()` freezes the thread until data arrives |

### Limitation

The server handles exactly **one client** and exits afterwards. A second client cannot connect until the server is restarted.

### How to Run

**Terminal 1 — Start the server first:**
```bash
cd v1-basic
javac MyServer.java
java MyServer
```
Output: `Server Initiated, Waiting for Client to Connect...`

**Terminal 2 — Start the client:**
```bash
cd v1-basic
javac MyClient.java
java MyClient
```
Output: `Connected to Server, Please type your message and hit Enter to send`

**Example session:**

| Client terminal | Server terminal |
|---|---|
| `Client: Hello!` | `Client: Hello!` |
| | `Server: Hi there` |
| `Server: Hi there` | |
| `Client: bye` | `Client: bye` |
| `Connection Terminated` | `Connection Terminated` |

---

## v2-multithreaded — Multi-Client Group Chat

An enhanced version that supports multiple simultaneous clients. Each connected client gets its own thread. Every message is broadcast to all other connected clients. The server runs indefinitely and never needs to be restarted between sessions.

### New Concepts Introduced

| Concept | Where it appears |
|---|---|
| `Thread` / `Runnable` | `new Thread(new ClientHandler(socket)).start()` in Server |
| Shared mutable state | `Server.clients` list accessed by many threads |
| `CopyOnWriteArrayList` | Thread-safe list; no `ConcurrentModificationException` during broadcast |
| `readLine()` returning `null` | Detects abrupt client disconnect without an exception |
| `try-with-resources` | Automatic stream cleanup in `ClientHandler` and `Client` |
| Two-threaded client | Read thread (server messages) + write thread (keyboard) run simultaneously |
| `volatile boolean` | Visibility guarantee for the `running` flag shared between client threads |
| Command pattern | `/list` and `/quit` dispatched inside the message loop |

### How to Run

**Terminal 1 — Start the server (leave running throughout):**
```bash
cd v2-multithreaded
javac *.java
java Server
```
Output: `Chat server started on port 5555`

**Terminal 2 — Connect first client:**
```bash
java Client
```
Enter a username when prompted (e.g., `Alice`).

**Terminal 3 — Connect second client:**
```bash
java Client
```
Enter a username (e.g., `Bob`). Alice's terminal immediately shows `Bob has joined the chat.` — without Alice pressing Enter. This is the two-threaded client working.

**Available commands (type in any client terminal):**
- `/list` — shows all currently connected usernames (only visible to you)
- `/quit` — disconnects gracefully; all others see `[username] has left the chat.`

### Example session (3 clients)

```
[Alice terminal]                [Bob terminal]             [Charlie terminal]
Connected. Enter username: Alice
Welcome, Alice! ...
                                Connected. Enter username: Bob
Alice has joined the chat. ←    Welcome, Bob! ...
                                                           Connected. Enter username: Charlie
Bob has joined the chat. ←      Alice has joined... ←     Welcome, Charlie! ...
Hello everyone!  →              [Alice]: Hello everyone!   [Alice]: Hello everyone!
                                Hey Alice! →               [Bob]: Hey Alice!
[Bob]: Hey Alice!                                          [Bob]: Hey Alice!
/list
-- Online users (3) --
  Alice
  Bob
  Charlie
----------------------------
                                /quit →
Bob has left the chat. ←                                  Bob has left the chat. ←
```

---

## Key Design Decisions

### Why `CopyOnWriteArrayList` instead of `ArrayList` + `synchronized`?

`broadcast()` iterates the client list while other threads may simultaneously add or remove clients (as they connect/disconnect). A plain `ArrayList` would throw `ConcurrentModificationException`. `Collections.synchronizedList(new ArrayList<>())` prevents that but still requires external locking during iteration. `CopyOnWriteArrayList` handles both problems automatically: every write creates a new internal copy of the array, so active iterators always see a consistent snapshot. For a chat server with low client turnover this is the cleanest solution.

### Why does the client need two threads?

In v1, communication is strictly turn-based: client sends, then waits for a response, then sends again. One thread is enough because the client always knows when to read vs. when to write.

In v2, the server can push a message at any time (another user may chat while you are typing). If the client has only one thread, it either blocks on `keyboard.readLine()` (missing incoming messages) or blocks on `serverIn.readLine()` (unable to send). Two threads — one owning each blocking operation — solve this permanently.

### Why `readThread.setDaemon(true)`?

When the user types `/quit`, the main (write) thread exits. Without the daemon flag, the JVM would stay alive waiting for the read thread, which is blocked indefinitely on `serverIn.readLine()`. Marking the read thread as a daemon means it is terminated automatically when the last non-daemon (main) thread finishes.

---

## Learning Progression

| Feature | v1-basic | v2-multithreaded |
|---|---|---|
| Number of clients | 1 | Unlimited |
| Server lifetime | Exits after 1 client | Runs indefinitely |
| Threading model | Single thread | Thread per client + 2-thread client |
| Client architecture | Blocking alternation | Read thread + write thread |
| Error handling | `throws Exception` | `try-with-resources` + `finally` |
| Disconnect detection | `"bye"` sentinel | `null` from `readLine()` |
| Messaging | 1-to-1 turn-based | Broadcast to all |
| Commands | None | `/list`, `/quit` |

---

## Problem Statement

> *To develop any distributed application through implementing client-server communication programs based on Java Sockets.*
