# Roadmap — java-socket-chat

This document tracks the planned enhancements to evolve this project from a university lab into a fully-featured, production-aware chat system. Phases are ordered by complexity and build on each other.

---

## Current State

- [x] **v1-basic** — Single-client TCP chat, turn-based protocol, blocking I/O
- [x] **v2-multithreaded** — Multi-client group chat, one thread per client, broadcast messaging, `/list` and `/quit` commands

---

## Phase 1 — Quick Wins (~2 hours)

Small, high-impact changes to the existing v2 implementation.

- [ ] **Timestamps on messages** — Every message is prefixed with the time it was sent
  - Format: `[10:32] [Alice]: Hello everyone!`
  - File: `v2-multithreaded/ClientHandler.java`

- [ ] **Private messaging (`/msg`)** — Send a message visible only to one specific user
  - Usage: `/msg Bob Hey, are you free?`
  - Sender sees: `[PM to Bob]: Hey, are you free?`
  - Recipient sees: `[PM from Alice]: Hey, are you free?`
  - Files: `v2-multithreaded/ClientHandler.java`, `v2-multithreaded/Server.java`

- [ ] **GitHub Actions CI** — Automatically compile both versions on every push
  - Green checkmark on the repo confirms the code always builds
  - File: `.github/workflows/build.yml`

---

## Phase 2 — Visual Polish (~1 hour)

Making the GitHub repository page itself look professional.

- [ ] **Demo GIF** — Screen recording of v2 running with 3 simultaneous clients embedded in the README
  - File: `docs/demo.gif`, referenced in `README.md`

- [ ] **README badges** — Build status, Java version, and license badges at the top of the README
  - Build badge powered by GitHub Actions (requires Phase 1c complete)

---

## Phase 3 — Chat Rooms (~4 hours)

A significant feature addition: multiple named rooms that clients can join and leave.

- [ ] **`Room.java`** — New class representing a chat room with its own member list and broadcast method
- [ ] **`/join #roomname`** — Move into a named room; created automatically if it doesn't exist
- [ ] **`/leave`** — Return to the default `#general` room
- [ ] **`/rooms`** — List all active rooms and how many users are in each
- [ ] **Default room** — All clients start in `#general` on connect
- [ ] Messages only broadcast to members of the current room

New commands: `/join #roomname`, `/leave`, `/rooms`

Files: `v2-multithreaded/Room.java` (new), `v2-multithreaded/Server.java`, `v2-multithreaded/ClientHandler.java`

---

## Phase 4 — SSL/TLS Encryption (~6 hours)

Encrypt all traffic between clients and server so messages cannot be read by a network observer.

- [ ] **Self-signed certificate** — Generated with `keytool` (bundled with the JDK), stored in `v2-multithreaded/certs/`
- [ ] **`SecureServer.java`** — Replaces plain `ServerSocket` with `SSLServerSocket` from `javax.net.ssl`
- [ ] **`SecureClient.java`** — Replaces plain `Socket` with `SSLSocket`, configured to trust the self-signed cert
- [ ] `ClientHandler.java` and `Server.java` are **unchanged** — only the socket type changes, proving the architecture is layered correctly
- [ ] Runs on port **5556** alongside the unencrypted v2 (port 5555)
- [ ] README updated with `keytool` setup command and instructions for running the secure version

---

## Phase 5 — JavaFX GUI Client (~2 days)

Replace the terminal-based client with a proper graphical chat window.

- [ ] **`ClientGUI.java`** — JavaFX application with a message area, input field, and send button
- [ ] Read thread uses `Platform.runLater()` to update the UI safely from a background thread
- [ ] The server (`Server.java`, `ClientHandler.java`) is **completely unchanged** — the GUI client speaks the same protocol
- [ ] README updated with JavaFX SDK setup instructions

---

## Concept Map — What Each Phase Teaches

| Phase | Core Concepts Introduced |
|---|---|
| v1-basic | Sockets, TCP, blocking I/O, turn-based protocol |
| v2-multithreaded | `Thread`/`Runnable`, `CopyOnWriteArrayList`, broadcast, two-threaded client, `volatile` |
| Phase 1 | `java.time` API, command parsing, thread-safe username lookup |
| Phase 2 | Developer documentation, CI/CD fundamentals |
| Phase 3 | `ConcurrentHashMap`, room-scoped broadcast, stateful command handling |
| Phase 4 | Public-key cryptography, `SSLContext`, `KeyStore`, TLS handshake |
| Phase 5 | JavaFX threading model, `Platform.runLater()`, event-driven UI |

---

## Estimated Total Time

| Phase | Estimate |
|---|---|
| Phase 1 — Quick Wins | ~2 hours |
| Phase 2 — Visual Polish | ~1 hour |
| Phase 3 — Chat Rooms | ~4 hours |
| Phase 4 — SSL/TLS | ~6 hours |
| Phase 5 — GUI Client | ~2 days |
| **Total** | **~3 days** |
