import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("3D Astrophysics Orbital Engine (Vanilla Java)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 750);
            
            SpaceSimulationPanel panel = new SpaceSimulationPanel();
            frame.add(panel);
            
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Start the physics and rendering thread loop
            new Thread(panel).start();
        });
    }
}

// Represents a celestial body interacting in 3D space
class CelestialBody {
    double x, y, z;      // 3D Positions
    double vx, vy, vz;   // 3D Velocity vectors
    double mass;
    int radius;
    Color color;

    public CelestialBody(double x, double y, double z, double vx, double vy, double vz, double mass, int radius, Color color) {
        this.x = x; this.y = y; this.z = z;
        this.vx = vx; this.vy = vy; this.vz = vz;
        this.mass = mass;
        this.radius = radius;
        this.color = color;
    }

    // Apply Newton's Law of Universal Gravitation towards another body
    public void applyGravityTowards(CelestialBody target, double G, double dt) {
        double dx = target.x - this.x;
        double dy = target.y - this.y;
        double dz = target.z - this.z;
        
        double distanceSq = dx*dx + dy*dy + dz*dz + 100; // +100 is software softening to prevent infinite gravity division errors
        double distance = Math.sqrt(distanceSq);

        // F = G * (m1 * m2) / r^2 -> Acceleration = F / m1 -> A = G * m2 / r^2
        double forceMagnitude = (G * target.mass) / distanceSq;

        // Break down acceleration into vector directions
        this.vx += (forceMagnitude * (dx / distance)) * dt;
        this.vy += (forceMagnitude * (dy / distance)) * dt;
        this.vz += (forceMagnitude * (dz / distance)) * dt;
    }

    public void updatePosition(double dt) {
        this.x += this.vx * dt;
        this.y += this.vy * dt;
        this.z += this.vz * dt;
    }
}

class SpaceSimulationPanel extends JPanel implements Runnable {
    private final ArrayList<CelestialBody> bodies = new ArrayList<>();
    private final double G = 15.0; // Scaled Gravitational Constant for Simulation Space
    private boolean running = true;

    // 3D Camera Variables
    private double cameraAngleX = 0.3; // Pitch
    private double cameraAngleY = 0.5; // Yaw
    private double cameraZoom = 350;   // Focal Length proxy

    public SpaceSimulationPanel() {
        setBackground(Color.BLACK);
        setFocusable(true);

        // Populate a Star System N-Body arrangement
        Random rand = new Random();
        
        // 1. Massive Central Star
        bodies.add(new CelestialBody(0, 0, 0, 0, 0, 0, 50000, 20, Color.YELLOW));

        // 2. Spawn a disk of orbiting planetary bodies / space particles
        for (int i = 0; i < 250; i++) {
            double radius = 100 + rand.nextDouble() * 250; // Distance from star
            double angle = rand.nextDouble() * Math.PI * 2;
            
            // Circular 3D orbital plane positions
            double px = Math.cos(angle) * radius;
            double pz = Math.sin(angle) * radius;
            double py = (rand.nextDouble() - 0.5) * 15; // slight orbital inclination variance

            // Calculate orbital velocity formula: v = sqrt(G*M / r)
            double orbitalVelocity = Math.sqrt((G * 50000) / radius);
            double vx = -Math.sin(angle) * orbitalVelocity;
            double vz = Math.cos(angle) * orbitalVelocity;
            double vy = 0;

            Color particleColor = Color.getHSBColor(rand.nextFloat(), 0.6f, 0.9f);
            bodies.add(new CelestialBody(px, py, pz, vx, vy, vz, 1.0, 3, particleColor));
        }

        // Setup Interactive Camera Controls (Arrow Keys)
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT)  cameraAngleY -= 0.05;
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) cameraAngleY += 0.05;
                if (e.getKeyCode() == KeyEvent.VK_UP)    cameraAngleX -= 0.05;
                if (e.getKeyCode() == KeyEvent.VK_DOWN)  cameraAngleX += 0.05;
                if (e.getKeyCode() == KeyEvent.VK_W)     cameraZoom += 10;
                if (e.getKeyCode() == KeyEvent.VK_S)     cameraZoom -= 10;
            }
        });
    }

    @Override
    public void run() {
        double dt = 0.1; // Time Step slice
        while (running) {
            // N-Body Gravitational cross-interaction loop calculations
            for (int i = 0; i < bodies.size(); i++) {
                for (int j = 0; j < bodies.size(); j++) {
                    if (i != j) {
                        bodies.get(i).applyGravityTowards(bodies.get(j), G, dt);
                    }
                }
            }

            // Update positional positions vectors
            for (CelestialBody body : bodies) {
                body.updatePosition(dt);
            }

            repaint();
            try { Thread.sleep(16); } catch (InterruptedException ignored) {} // ~60 Frames Per Second calculation loop
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Draw basic camera control heads-up telemetry display overlay strings
        g2.setColor(Color.GREEN);
        g2.drawString("🚀 3D CELESTIAL MECHANICS SIMULATOR", 20, 20);
        g2.drawString("Controls: Use [ARROW KEYS] to Rotate Space Grid Camera View", 20, 40);
        g2.drawString("Controls: Use [W] / [S] to Zoom In / Zoom Out", 20, 60);

        // Render celestial bodies by mapping 3D matrices dynamically down onto a 2D viewport viewport array planes
        for (CelestialBody body : bodies) {
            // 3D Rotation Transformations Matrices (Math implementations manually from scratch)
            
            // 1. Yaw Rotation around Y Axis
            double x1 = body.x * Math.cos(cameraAngleY) - body.z * Math.sin(cameraAngleY);
            double z1 = body.x * Math.sin(cameraAngleY) + body.z * Math.cos(cameraAngleY);
            double y1 = body.y;

            // 2. Pitch Rotation around X Axis
            double y2 = y1 * Math.cos(cameraAngleX) - z1 * Math.sin(cameraAngleX);
            double z2 = y1 * Math.sin(cameraAngleX) + z1 * Math.cos(cameraAngleX);
            double x2 = x1;

            // Perspective Projection Equation: Add a static distance offset so nothing clips out behind camera array
            double cameraDistance = 500;
            double perspectiveScale = cameraZoom / (z2 + cameraDistance);

            // Project 3D vector variables into raw flat 2D viewport point positions
            int screenX = (int) (centerX + (x2 * perspectiveScale));
            int screenY = (int) (centerY - (y2 * perspectiveScale));

            // Dynamic scaling proxy sizes based on 3D depth field calculations
            int renderSize = (int) Math.max(1, body.radius * perspectiveScale * 1.5);

            // Occlusion culling: Draw only if the projected coordinates rest ahead of our focal threshold line
            if (z2 + cameraDistance > 20) {
                g2.setColor(body.color);
                g2.fillOval(screenX - renderSize / 2, screenY - renderSize / 2, renderSize, renderSize);
            }
        }
    }
}
