<h1 align="center">📊 Online Exam Monitoring System</h1>

<p align="center">
  🚀 Real-Time Exam System using Java AWT, Networking & Multithreading
</p>

<hr>

<h2>📌 Overview</h2>
<p>
This project simulates a <b>real-time online examination system</b> where multiple students can attempt an exam simultaneously, while the admin monitors responses live, evaluates answers instantly, and visualizes performance using graphs.
</p>

<hr>

<h2>✨ Features</h2>

<h3>👨‍🎓 Student Panel</h3>
<ul>
  <li>Answer arithmetic questions</li>
  <li>Next button for navigation</li>
  <li>Auto-send answers in real-time</li>
  <li>Exam completion detection</li>
  <li>🔴 Close button after finishing</li>
</ul>

<h3>🧑‍🏫 Admin Panel</h3>
<ul>
  <li>📡 Live monitoring of student answers</li>
  <li>✔ Correct / ❌ Wrong / ⏳ Not Answered display</li>
  <li>📊 Real-time score calculation</li>
  <li>📈 Graph visualization (bar chart)</li>
  <li>🎉 Detects all students finished</li>
  <li>🔴 Close button after completion</li>
</ul>

<hr>

<h2>🧠 Concepts Used</h2>

<table border="1" cellpadding="8">
<tr>
<th>Concept</th>
<th>Usage</th>
</tr>

<tr>
<td>Multithreading</td>
<td>Handles real-time server communication</td>
</tr>

<tr>
<td>UDP Networking</td>
<td>Sends & receives student answers</td>
</tr>

<tr>
<td>AWT (GUI)</td>
<td>Creates UI components</td>
</tr>

<tr>
<td>Event Handling</td>
<td>Handles button clicks</td>
</tr>

<tr>
<td>Collections</td>
<td>Stores answers and scores</td>
</tr>

<tr>
<td>Graphics (Canvas)</td>
<td>Draws result graph</td>
</tr>

<tr>
<td>Thread-safe UI</td>
<td>Prevents GUI freezing</td>
</tr>

</table>

<hr>

<h2>🔄 System Flow</h2>

<p align="center">
<b>
Student ➝ Send Answer ➝ Admin Receives ➝ Evaluate ➝ Update UI ➝ Graph ➝ Completion Check
</b>
</p>

<hr>

<h2>📊 Sample Questions</h2>
<ul>
  <li>5 + 3 = ?</li>
  <li>10 - 4 = ?</li>
  <li>6 × 2 = ?</li>
  <li>8 ÷ 2 = ?</li>
</ul>

<hr>

<h2>⚙️ How to Run</h2>

<pre>
javac OES.java
java OES
</pre>

<p>Enter number of students → Multiple student windows will open</p>

<hr>

<h2>⚠️ Important Notes</h2>
<ul>
  <li>Ensure port <b>5000</b> is free</li>
  <li>Run only one instance at a time</li>
  <li>Works on localhost (127.0.0.1)</li>
</ul>

<hr>

<h2>📁 Project Structure</h2>

<pre>
OES.java
 ├── AdminGUI
 ├── StudentGUI
 ├── ResultCanvas
</pre>

<hr>

<h2>🚀 Future Enhancements</h2>
<ul>
  <li>🏆 Ranking System</li>
  <li>⏱ Timer-based exam</li>
  <li>🌐 Multi-device support</li>
  <li>🎨 Modern UI (Swing/JavaFX)</li>
</ul>

<hr>

<h2 align="center">👨‍💻 Author</h2>

<p align="center">
<b>Dhruv Patva</b><br>
💡 Java Developer | OS Enthusiast
</p>

<hr>

<p align="center">
⭐ If you like this project, consider giving it a star!
</p>
