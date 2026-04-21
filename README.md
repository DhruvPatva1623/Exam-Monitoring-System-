# 📊 Online Exam Monitoring System

## 🚀 Overview

The **Online Exam Monitoring System** is a Java-based application that simulates a real-time online examination environment.
It allows multiple students to submit answers simultaneously while the admin monitors responses live, evaluates results instantly, and visualizes performance using graphs.

---

## 🎯 Key Features

### 👨‍🎓 Student Panel

* Answer arithmetic-based questions
* Navigate through questions using **Next button**
* Auto-submit answers in real-time
* **Close button enabled after exam completion**
* Sends completion signal to admin

### 🧑‍🏫 Admin Panel

* Real-time monitoring of student responses
* Displays:

  * ✔ Correct answers
  * ❌ Incorrect answers
  * ⏳ Not attempted
* Calculates **live scores**
* Shows **bar graph visualization**
* Detects when all students finish
* Displays:

  * 🎉 *Test Conducted Successfully*
* Provides **Close button after completion**

---

## 🧠 Concepts Used

| Concept                           | Usage                                              |
| --------------------------------- | -------------------------------------------------- |
| Multithreading                    | Handles real-time server communication             |
| UDP Networking (`DatagramSocket`) | Sends & receives student answers                   |
| AWT (GUI)                         | Builds interface (Frame, Button, TextArea, Canvas) |
| Event Handling                    | Handles button clicks                              |
| Collections (`Map`, `List`)       | Stores answers & results                           |
| Graphics (`Canvas`)               | Draws result graph                                 |
| Thread-safe UI (`EventQueue`)     | Prevents UI freezing                               |

---

## 🔄 System Flow

Student → Sends Answer → Admin Receives → Stores Data →
Evaluates → Updates GUI → Displays Graph → Tracks Completion

---

## 📊 Sample Questions

* 5 + 3 = ?
* 10 - 4 = ?
* 6 × 2 = ?
* 8 ÷ 2 = ?

---

## ⚙️ How to Run

1. Open terminal in project folder
2. Compile:

   ```bash
   javac OES.java
   ```
3. Run:

   ```bash
   java OES
   ```
4. Enter number of students
5. Student windows will open automatically

---

## ⚠️ Important Notes

* Ensure the port (e.g., `5000`) is free before running
* Run only **one instance** of the program at a time
* Works on local system (`127.0.0.1`)

---

## 📁 Project Structure

* `AdminGUI` → Admin dashboard
* `StudentGUI` → Student interface
* `ResultCanvas` → Graph visualization
* `OES.java` → Main file

---

## 🎓 Learning Outcome

This project demonstrates:

* Real-time system design
* Client-server communication
* GUI development in Java
* Data processing & visualization

---

## 💡 Future Enhancements

* 🏆 Ranking system
* ⏱ Timer-based exams
* 🌐 Multi-device support
* 🎨 Modern UI (Swing/JavaFX)

---

## 👨‍💻 Author

**Dhruv Patva**

---

## ⭐ Acknowledgement

This project is built for academic learning and demonstration of Operating System and Java concepts.
