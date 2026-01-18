# ⌚ DoWatch - Luxury Virtual Timepiece

> *A sophisticated, highly detailed analog watch simulation built with pure Java Swing.*

**DoWatch** is a digital homage to high-end craftsmanship, inspired by the aesthetics of Citizen Eco-Drive timepieces. It combines precise multi-zone timekeeping with a fully functional chronograph, rendered entirely using Java's 2D Graphics API.

## ✨ Features

*   **🌍 Multi-Zone Timekeeping**: Instantly switch between global time zones including **Belgrade**, **London**, **New York**, **Tokyo**, **Dubai**, and more.
*   **⏱️ Chronograph Complication**:
    *   **Sub-dial at 6 o'clock**: Tracks stopwatch seconds.
    *   **Sub-dial at 3 o'clock**: Tracks stopwatch minutes.
*   **📅 24-Hour & Date Display**:
    *   **Sub-dial at 9 o'clock**: 24-hour hand synchronized with local time.
    *   **Date Window**: Positioned at 4 o'clock.
*   **💎 Premium Aesthetics**:
    *   Brushed steel case rendering.
    *   Deep navy sunburst-style dial.
    *   Dynamic crystal reflections, shadows, and "lume" details.

## 🎮 Controls

| Interaction | Action |
| :--- | :--- |
| **`ENTER` Key** | **Change Time Zone** (Cycles through available cities) |
| **Top Button (Click)** | **Start / Stop** Stopwatch |
| **Top Button (Hold)** | **Reset** Stopwatch (Hold for ~1 sec) |

## 🚀 Getting Started

### Prerequisites
*   Java Development Kit (JDK) 8 or higher.

### Installation & Run

1.  **Clone the repository**
    ```bash
    git clone https://github.com/yourusername/do-watch.git
    cd do-watch
    ```

2.  **Compile the source**
    ```bash
    mkdir -p bin
    javac -d bin src/DoWatch.java
    ```

3.  **Launch the application**
    ```bash
    java -cp bin DoWatch
    ```

## 🛠️ Tech Stack
*   **Language**: Java
*   **UI Framework**: Swing (JPanel, JFrame)
*   **Rendering**: Java 2D API (Graphics2D, GradientPaint, AffineTransform)

## 👨‍💻 Credits
**Designed & Developed by:** Dobrosav Vlaskovic

---
*"Time is the most valuable thing a man can spend."*
