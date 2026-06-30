# java-socket-chat — Project Guide

Java Socket Programming lab (Strathmore University, Distributed Systems, Year 4.1) evolving from a basic TCP chat into a multi-feature, distributed-systems-aware chat system. Enhancement plan lives in `ROADMAP.md`.

## Structure
- `v1-basic/` — original single-client blocking chat (`MyServer`, `MyClient`). **Baseline — do not modify.**
- `v2-multithreaded/` — multi-client server (`Server`, `ClientHandler`, `Client`). Active development target.
- `docs/learning/` — one teaching note per completed roadmap phase.
- `ROADMAP.md` — 7-phase enhancement plan with progress checkboxes.

## Learning workflow (IMPORTANT)
This is a **learning project**. The owner is building understanding, not just shipping features.

After implementing each roadmap phase:
1. Write a teaching note at `docs/learning/phaseN-<name>.md`.
2. Walk the owner through it: **concept → implementation → gotchas / relevant extras**.
3. Update the matching checkbox in `ROADMAP.md` and the index in `docs/learning/README.md`.
4. Only move to the next phase once the owner confirms they understand.

Teaching style that works for this owner:
- **One concept at a time.** Do not dump everything at once — they lose focus with overload.
- Use simple, consistent analogies (the walkie-talkie / restaurant / kitchen style used so far).
- Prefer short sections and tables over walls of text.
- Always tie explanations back to the actual lines of code in the repo.

## Build & run (plain `javac` — no build tool yet)
- v1: `cd v1-basic && javac *.java && java MyServer` (then `java MyClient` in another terminal)
- v2: `cd v2-multithreaded && javac *.java && java Server` (then `java Client` per user)

## Conventions
- One commit (ideally one PR) per roadmap phase — git history should read as a progression.
- Keep `v1-basic/` untouched as the teaching baseline.
- Never commit private keys or keystores (see `.gitignore`).
- Stay on plain `javac` until Phase 7 (JavaFX) forces a build tool.
