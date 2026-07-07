# 3D Astrophysical Orbit Simulation Engine

A high-performance, interactive 3D N-body orbital mechanics engine built from scratch using **100% Vanilla Java**. This project implements celestial mechanics calculations and projects 3D spatial environments onto a 2D screen using custom trigonometric projection matrices—**completely free of heavy third-party dependencies like OpenGL, Java3D, or OpenCV.**

---

## 🌌 Key Architectural Features

*   **Real-Time Vector Physics Engine:** Celestial bodies do not follow pre-programmed paths. All kinematics are computed on-the-fly using vector tracking implementations of **Newton's Law of Universal Gravitation**.
*   **Custom 3D-to-2D Rendering Pipeline:** Features manual matrix rotation and perspective projection math from scratch. 
*   **Double-Buffered Multi-Threaded Loop:** Separates the structural frame calculations from the Java Swing UI Event Dispatch Thread to maintain a stable ~60 FPS calculation cycle.
*   **Interactive Camera Control Engine:** Allows real-time pitch, yaw, and focal zoom manipulation through native keyboard hooks.

---

## Theoretical & Mathematical Framework

### 1. Gravitational Interactions (N-Body Vector Formulation)
Every body in the workspace exerts a gravitational pull on every other body. The dynamic step-velocity updates are mapped using:

$$F = G \frac{m_1 m_2}{r^2}$$

To prevent software division-by-zero errors when bodies closely intersect, a software softening modifier ($e^2$) is factored into the distance magnitude check:

$$\vec{a}_i = \sum_{j \neq i} \frac{G \cdot m_j \cdot (\vec{r}_j - \vec{r}_i)}{(|\vec{r}_j - \vec{r}_i|^2 + \epsilon^2)^{3/2}}$$

### 2. Custom 3D Projection Matrices
3D coordinates $(X, Y, Z)$ are converted into flat viewport pixel space $(X_{screen}, Y_{screen})$ using sequential spatial transformation rotations:

*   **Yaw Rotation (Around $Y$-Axis by angle $\theta$):**
    $$x_1 = x \cdot \cos(\theta) - z \cdot \sin(\theta)$$
    $$z_1 = x \cdot \sin(\theta) + z \cdot \cos(\theta)$$
*   **Pitch Rotation (Around $X$-Axis by angle $\phi$):**
    $$y_2 = y \cdot \cos(\phi) - z_1 \cdot \sin(\phi)$$
    $$z_2 = y \cdot \sin(\phi) + z_1 \cdot \cos(\phi)$$

Finally, a **Perspective Scale Factor** uses depth division to simulate natural field depth scaling:

$$X_{screen} = CenterX + \left( \frac{x_2 \cdot FocalLength}{z_2 + DistanceOffset} \right)$$
$$Y_{screen} = CenterY - \left( \frac{y_2 \cdot FocalLength}{z_2 + DistanceOffset} \right)$$

---

## User Interface & Control Telemetry

Once launched, the active window features an overlay heads-up telemetry display. You can navigate the interactive viewport space via:

| Key Input | Action Sequence |
| :--- | :--- |
| **`LEFT` / `RIGHT` Arrow** | Orbit Camera View (Yaw Left/Right) |
| **`UP` / `DOWN` Arrow** | Adjust View Height (Pitch Up/Down) |
| **`W` Key** | Zoom In (Increase Focal Scale) |
| **`S` Key** | Zoom Out (Decrease Focal Scale) |

---

## Compilation & Quick Start

Since this engine relies entirely on the native Java Runtime Environment (`java.desktop` modules), you do not need to configure Maven, Gradle, or download extra `.jar` packages. 

1. Clone or download the source directory files.
2. Open your terminal shell and navigate to your directory containing `main.java`.
3. Compile and execute using the standard unified launch command:

```cmd
java main.java
