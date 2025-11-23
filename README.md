# Simon Swift – Formative Task 2
  
This repository contains the Java implementation of **Simon Swift**, a SwiftBot-based memory game inspired by the classic Simon toy.

---

## 🧠 Project Overview

**Simon Swift** challenges the player to memorise and repeat a growing sequence of colours.  
Each colour corresponds to a SwiftBot LED and button (A, B, X, Y).  
The sequence increases by one colour every round.

The game ends when:
- The user inputs the wrong sequence  
- The user chooses to quit (every 5 rounds)

If the user scores **5 or more**, the SwiftBot performs a special **celebration dive**.

---

## 🎮 Core Features

- Four LED–button mappings (A, B, X, Y)
- Random colour sequence generation
- Sequence grows every round
- Player repeats the sequence via SwiftBot buttons
- Score tracking and round display
- Quit option on rounds 5, 10, 15, …
- “Game Over!” on incorrect input
- “See you again champ!” when user quits
- Celebration Dive with:
  - V-shaped movement (30 cm per arm)
  - LED flashing in all four colours before and after
  - Speed determined by score:
    - Score < 5 → speed = 40
    - Score 5–9 → speed = score × 10
    - Score ≥ 10 → speed = 100

---

## 📂 Project Structure

---


---

## 🧪 How to Run

1. Open **Eclipse** on a BU computer  
2. Ensure the **SwiftBot API JAR** is available (installed on university PCs)  
3. Import the `.java` files into a new project  
4. Place all classes in the **default package**  
5. Run `Main.java`  
6. Follow the console instructions

---

## 🔧 Requirements

- Java SDK (Standard Edition)  
- SwiftBot API 
- No external libraries unless included inside the ZIP submission  
- Must compile and run successfully on BU machines

---


## 👥 Group Work

This repository represents the implementation work done by the group.  
Individual contributions are documented in the final report as required.

---

## 📄 License

This project is for **academic use only** and is not licensed for commercial distribution.
