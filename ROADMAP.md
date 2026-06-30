# Roadmap — java-socket-chat

This document tracks the planned evolution of this project from a university lab into a chat system with genuine **distributed-systems depth**. Phases are ordered by dependency. Each completed phase gets a teaching note in [`docs/learning/`](docs/learning/).

---

## Current State

- [x] **v1-basic** — Single-client TCP chat, turn-based protocol, blocking I/O
- [x] **v2-multithreaded** — Multi-client group chat, one thread per client, broadcast, `/list` and `/quit`

---

## Phase 1 — Quick Wins (~2.5 hours)

- [ ] **Timestamps** — every message prefixed `[10:32]`, applied to messages, join/leave, and PMs
- [ ] **Private messaging (`/msg`)** — `/msg Bob see you soon`; robust `split(" ", 3)` parsing so multi-word PMs survive; usage hint on misuse
- [ ] **Username uniqueness** — reject + re-prompt duplicate names (prerequisite for reliable `/msg`)
- [ ] **GitHub Actions CI + smoke test** — compile both versions AND run a `SmokeTest` that starts the server, connects two clients, and asserts a broadcast lands

Files: `v2-multithreaded/ClientHandler.java`, `Server.java`, `SmokeTest.java` (new), `.github/workflows/build.yml` (new)

---

## Phase 2 — Visual & Repo Polish (~1.5 hours)

- [ ] **`LICENSE` file** — real MIT license (not just a badge)
- [ ] **Demo GIF** — v2 with 3 clients, embedded in README (`docs/demo.gif`)
- [ ] **README badges** — build status, Java 17, MIT license

---

## Phase 3 — Thread Pool + Heartbeat (~3 hours) · Distributed Systems

- [ ] **Bounded thread pool** — replace unbounded `new Thread()` with an `ExecutorService`; note Java 21 virtual threads as the modern alternative
- [ ] **Heartbeat / dead-client detection** — `setSoTimeout` + client keepalive so a yanked connection is detected in seconds, not minutes

Files: `v2-multithreaded/Server.java`, `ClientHandler.java`, `Client.java`

---

## Phase 4 — Chat Rooms (~4 hours)

- [ ] **`Room.java`** — named room with its own member list and broadcast
- [ ] **`/join #room`, `/leave`, `/rooms`** — move between rooms; `#general` is the default
- [ ] **Room-scoped everything** — messages, join/leave announcements, and `/list` all confined to the current room

Files: `v2-multithreaded/Room.java` (new), `Server.java`, `ClientHandler.java`

---

## Phase 5 — SSL/TLS Encryption (~6 hours)

- [ ] **Self-signed certificate** via `keytool` — keystore is **gitignored** (never commit a private key)
- [ ] **`SecureServer.java` / `SecureClient.java`** — `SSLServerSocket` / `SSLSocket`; `ClientHandler` reused unchanged
- [ ] Runs on port **5556** alongside plain v2

Files: `v2-multithreaded/SecureServer.java` (new), `SecureClient.java` (new), `certs/` (gitignored)

---

## Phase 6 — Reconnection + Message History (~5 hours) · Distributed Systems

- [ ] **Server-side history** — each room keeps the last ~20 messages, replayed to joiners for context
- [ ] **Client auto-reconnect** — exponential backoff on connection loss, then session resume

Files: `v2-multithreaded/Room.java`, `ClientHandler.java`, `Client.java`

---

## Phase 7 — JavaFX GUI Client (~2 days)

- [ ] **`ClientGUI.java`** — message area + input field + send button
- [ ] Read thread updates UI via `Platform.runLater()`; server/protocol unchanged
- [ ] **Setup note:** JavaFX is not bundled in the JDK since Java 11 — requires the JavaFX SDK on the module path or a build tool (Gradle/Maven)

---

## Concept Map — What Each Phase Teaches

| Phase | Core Concepts |
|---|---|
| v1-basic | Sockets, TCP, blocking I/O, turn-based protocol |
| v2-multithreaded | `Thread`/`Runnable`, `CopyOnWriteArrayList`, broadcast, `volatile`, two-threaded client |
| 1 | `java.time`, command parsing, thread-safe lookup, smoke testing |
| 2 | Documentation, CI/CD, licensing |
| 3 | `ExecutorService`, bounded concurrency, failure detection, `setSoTimeout` |
| 4 | `ConcurrentHashMap`, room-scoped state, stateful commands |
| 5 | Public-key crypto, `SSLContext`, `KeyStore`, TLS handshake |
| 6 | Backoff/retry, session resumption, bounded history buffers |
| 7 | JavaFX threading, `Platform.runLater()`, event-driven UI |

---

## Estimated Total Time

| Phase | Estimate |
|---|---|
| 1 — Quick Wins | ~2.5 hours |
| 2 — Polish | ~1.5 hours |
| 3 — Thread Pool + Heartbeat | ~3 hours |
| 4 — Chat Rooms | ~4 hours |
| 5 — SSL/TLS | ~6 hours |
| 6 — Reconnection + History | ~5 hours |
| 7 — JavaFX GUI | ~2 days |
| **Total** | **~4 days** |

---

## How This Project Is Built

Each phase is implemented, then explained in a teaching note under [`docs/learning/`](docs/learning/) — concept, implementation, and gotchas. The goal is a project that is **understood, not just assembled**. One commit per phase keeps the git history readable as a progression.
