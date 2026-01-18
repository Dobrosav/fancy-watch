import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.awt.event.KeyEvent;

public class DoWatch extends JPanel {

    private static final int WIDTH = 750;
    private static final int HEIGHT = 750;

    private String[] zoneNames = {"BELGRADE", "LONDON", "NEW YORK", "TOKYO", "SYDNEY", "MOSCOW", "DUBAI", "LOS ANGELES"};
    private ZoneId[] zones = {
        ZoneId.systemDefault(),
        ZoneId.of("Europe/London"),
        ZoneId.of("America/New_York"),
        ZoneId.of("Asia/Tokyo"),
        ZoneId.of("Australia/Sydney"),
        ZoneId.of("Europe/Moscow"),
        ZoneId.of("Asia/Dubai"),
        ZoneId.of("America/Los_Angeles")
    };
    private int currentZoneIndex = 0;

    // Stopwatch variables
    private boolean stopwatchRunning = false;
    private long stopwatchStartTime = 0;
    private long stopwatchElapsedTime = 0;

    private boolean topBtnPressed = false;
    private boolean bottomBtnPressed = false;
    private Rectangle2D topBtnRect;
    // Bottom button rect is for drawing, but not mouse interaction anymore
    private Rectangle2D bottomBtnRect;

    // Timer for handling long press on top button
    private Timer longPressTimer;
    private boolean isLongPressProcessed = false;

    public DoWatch() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(20, 20, 20)); // Dark background
        setFocusable(true);
        requestFocusInWindow();
        
        // Initialize Long Press Timer (e.g., 800ms hold time)
        longPressTimer = new Timer(800, e -> {
            isLongPressProcessed = true;
            // Reset Stopwatch
            stopwatchRunning = false;
            stopwatchElapsedTime = 0;
            stopwatchStartTime = 0;
            repaint();
        });
        longPressTimer.setRepeats(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Top button - Setup for Toggle or Reset
                if (topBtnRect != null && topBtnRect.contains(e.getPoint())) {
                    topBtnPressed = true;
                    isLongPressProcessed = false;
                    longPressTimer.restart();
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Check if it was the top button being released
                if (topBtnPressed) {
                    longPressTimer.stop();
                    
                    // Only toggle if it wasn't a long press (Reset)
                    if (!isLongPressProcessed) {
                        if (stopwatchRunning) {
                            // Stop
                            stopwatchRunning = false;
                            stopwatchElapsedTime += System.currentTimeMillis() - stopwatchStartTime;
                        } else {
                            // Start
                            stopwatchRunning = true;
                            stopwatchStartTime = System.currentTimeMillis();
                        }
                    }
                    
                    topBtnPressed = false;
                    repaint();
                }
            }
        });

        // Key Binding for ENTER - Change Zone (Bottom Button Function)
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "pressBottomBtn");
        getActionMap().put("pressBottomBtn", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Animate button press
                bottomBtnPressed = true;
                repaint();
                
                // Change zone logic
                currentZoneIndex = (currentZoneIndex + 1) % zones.length;
                
                // Release button after short delay
                Timer releaseTimer = new Timer(150, evt -> {
                    bottomBtnPressed = false;
                    repaint();
                });
                releaseTimer.setRepeats(false);
                releaseTimer.start();
            }
        });
        
        Timer timer = new Timer(50, new ActionListener() { // Increased refresh rate for stopwatch smoothness
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;
        int radius = Math.min(w, h) / 2 - 50;

        drawButtons(g2d, cx, cy, radius);
        drawCrown(g2d, cx, cy, radius);
        drawCase(g2d, cx, cy, radius);
        drawDial(g2d, cx, cy, radius);
        
        // Sub-dials
        drawStopwatch(g2d, cx, cy, radius);           // Bottom (6 o'clock) - Seconds
        draw24hDial(g2d, cx, cy, radius);             // Left (9 o'clock) - 24h Time
        drawStopwatchMinuteDial(g2d, cx, cy, radius); // Right (3 o'clock) - Stopwatch Minutes
        
        drawHands(g2d, cx, cy, radius);
        drawCrystalReflection(g2d, cx, cy, radius);
    }

    private void draw24hDial(Graphics2D g2, int cx, int cy, int mainRadius) {
        // Left sub-dial at 9 o'clock
        int subRadius = mainRadius / 4; // Approx 80px
        int subCx = cx - mainRadius / 2 - 10;
        int subCy = cy;

        // Draw Face
        drawSubDialFace(g2, subCx, subCy, subRadius);

        // Markings (0 - 24)
        for (int i = 0; i < 24; i += 2) {
            double angle = Math.toRadians(15 * i - 90); // 15 degrees per hour
            int startR = subRadius - 5;
            int endR = subRadius;
            
            g2.setColor(Color.LIGHT_GRAY);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(
                (int) (subCx + Math.cos(angle) * startR), (int) (subCy + Math.sin(angle) * startR),
                (int) (subCx + Math.cos(angle) * endR), (int) (subCy + Math.sin(angle) * endR)
            );
            
            // Numbers for 0, 6, 12, 18
            if (i % 6 == 0) {
                 g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                 String num = String.valueOf(i);
                 if (i == 0) num = "24";
                 FontMetrics fm = g2.getFontMetrics();
                 int txtX = (int) (subCx + Math.cos(angle) * (subRadius - 15)) - fm.stringWidth(num) / 2;
                 int txtY = (int) (subCy + Math.sin(angle) * (subRadius - 15)) + fm.getAscent() / 2 - 1;
                 g2.drawString(num, txtX, txtY);
            }
        }
        
        // Label
        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        String lbl = "24H";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(lbl, subCx - fm.stringWidth(lbl)/2, subCy + 15);

        // Hand Logic
        ZonedDateTime now = ZonedDateTime.now(zones[currentZoneIndex]);
        double hour24 = now.getHour() + now.getMinute() / 60.0;
        double angle = Math.toRadians(15 * hour24 - 90);

        drawSubHand(g2, subCx, subCy, angle, subRadius - 5, Color.WHITE);
    }

    private void drawStopwatchMinuteDial(Graphics2D g2, int cx, int cy, int mainRadius) {
        // Right sub-dial at 3 o'clock
        int subRadius = mainRadius / 4;
        int subCx = cx + mainRadius / 2 + 10;
        int subCy = cy;

        // Draw Face
        drawSubDialFace(g2, subCx, subCy, subRadius);

        // Markings (0 - 60)
        for (int i = 0; i < 60; i += 5) {
            double angle = Math.toRadians(6 * i - 90);
            int startR = subRadius - 5;
            int endR = subRadius;
            
            g2.setColor(Color.LIGHT_GRAY);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(
                (int) (subCx + Math.cos(angle) * startR), (int) (subCy + Math.sin(angle) * startR),
                (int) (subCx + Math.cos(angle) * endR), (int) (subCy + Math.sin(angle) * endR)
            );
            
            // Numbers for 15, 30, 45, 60
            if (i % 15 == 0) {
                 g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                 String num = String.valueOf(i);
                 if (i == 0) num = "60";
                 FontMetrics fm = g2.getFontMetrics();
                 int txtX = (int) (subCx + Math.cos(angle) * (subRadius - 15)) - fm.stringWidth(num) / 2;
                 int txtY = (int) (subCy + Math.sin(angle) * (subRadius - 15)) + fm.getAscent() / 2 - 1;
                 g2.drawString(num, txtX, txtY);
            }
        }
        
        // Label
        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        String lbl = "MIN";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(lbl, subCx - fm.stringWidth(lbl)/2, subCy + 15);

        // Hand Logic
        long time = stopwatchElapsedTime;
        if (stopwatchRunning) {
            time += System.currentTimeMillis() - stopwatchStartTime;
        }
        long totalSeconds = time / 1000;
        double minutesVal = totalSeconds / 60.0; // Continuous movement
        double angle = Math.toRadians(6 * minutesVal - 90); // 6 degrees per minute (0-60 scale)

        drawSubHand(g2, subCx, subCy, angle, subRadius - 5, new Color(255, 100, 100));
    }

    private void drawSubDialFace(Graphics2D g2, int cx, int cy, int radius) {
        g2.setColor(new Color(20, 25, 40));
        g2.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
        g2.setColor(new Color(80, 80, 80));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
    }
    
    private void drawSubHand(Graphics2D g2, int cx, int cy, double angle, int length, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(cx, cy, 
            (int)(cx + Math.cos(angle) * length), 
            (int)(cy + Math.sin(angle) * length));
        g2.fillOval(cx - 2, cy - 2, 4, 4);
    }

    private void drawCrown(Graphics2D g2, int cx, int cy, int radius) {
        // Crown at 3 o'clock position
        int crownW = 15;
        int crownH = 25;
        int caseOuterRadius = radius + 25;
        int crownX = cx + caseOuterRadius - 3;
        int crownY = cy - crownH / 2;

        g2.setColor(new Color(160, 160, 160));
        g2.fillRoundRect(crownX, crownY, crownW, crownH, 5, 5);
        g2.setColor(new Color(80, 80, 80));
        g2.drawRoundRect(crownX, crownY, crownW, crownH, 5, 5);

        // Draw ribs on the crown
        g2.setColor(new Color(100, 100, 100));
        for (int i = 2; i < crownH; i += 4) {
            g2.drawLine(crownX, crownY + i, crownX + crownW, crownY + i);
        }
    }

    private void drawStopwatch(Graphics2D g2, int cx, int cy, int mainRadius) {
        // Sub-dial at 6 o'clock
        int subRadius = mainRadius / 4;
        int subCx = cx;
        int subCy = cy + mainRadius / 2 + 10; // Positioned lower half

        // Sub-dial face
        g2.setColor(new Color(20, 25, 40));
        g2.fillOval(subCx - subRadius, subCy - subRadius, subRadius * 2, subRadius * 2);
        g2.setColor(new Color(100, 100, 100));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(subCx - subRadius, subCy - subRadius, subRadius * 2, subRadius * 2);

        // Sub-dial markings (seconds)
        for (int i = 0; i < 60; i += 5) {
            double angle = Math.toRadians(6 * i - 90);
            int startR = subRadius - 5;
            int endR = subRadius;
            
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(
                (int) (subCx + Math.cos(angle) * startR), (int) (subCy + Math.sin(angle) * startR),
                (int) (subCx + Math.cos(angle) * endR), (int) (subCy + Math.sin(angle) * endR)
            );
        }

        // Calculate time
        long time = stopwatchElapsedTime;
        if (stopwatchRunning) {
            time += System.currentTimeMillis() - stopwatchStartTime;
        }
        
        // Calculate hand angle (60 seconds per revolution)
        long totalSeconds = time / 1000;
        long millis = time % 1000;
        double secondsVal = totalSeconds % 60 + millis / 1000.0;
        double angle = Math.toRadians(6 * secondsVal - 90);

        // Draw hand
        g2.setColor(new Color(255, 100, 100)); // Red hand
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(subCx, subCy, 
            (int)(subCx + Math.cos(angle) * (subRadius - 5)), 
            (int)(subCy + Math.sin(angle) * (subRadius - 5)));
            
        g2.fillOval(subCx - 2, subCy - 2, 4, 4);
    }

    private void drawButtons(Graphics2D g2, int cx, int cy, int radius) {
        // Calculate common dimensions
        int btnWidth = 20;
        int btnHeight = 40;
        // The case outer radius is roughly radius + 25 (see drawCase)
        // We want buttons to stick out.
        // X position where button starts (attached to case)
        int caseOuterRadius = radius + 25;
        int btnBaseX = cx + caseOuterRadius - 5; // Slight overlap with case

        // Define hit-test rectangles (static positions)
        int topBtnY = cy - 60;
        int bottomBtnY = cy + 30;

        // Update hit boxes for MouseListener
        // Note: Hit boxes should cover the visible area. 
        // If the button moves, strictly the hit box moves, but for UI feel keeping it static or covering the max area is often better.
        // Let's make the hit box cover the extended position.
        topBtnRect = new Rectangle2D.Double(btnBaseX, topBtnY, btnWidth, btnHeight);
        bottomBtnRect = new Rectangle2D.Double(btnBaseX, bottomBtnY, btnWidth, btnHeight);

        // Draw Top Button
        int drawX = btnBaseX;
        if (topBtnPressed) {
            drawX -= 5; // Move "down" (inwards)
        }
        
        g2.setColor(new Color(180, 180, 180)); // Metallic button
        g2.fill(new RoundRectangle2D.Double(drawX, topBtnY, btnWidth, btnHeight, 5, 5));
        g2.setColor(new Color(100, 100, 100));
        g2.draw(new RoundRectangle2D.Double(drawX, topBtnY, btnWidth, btnHeight, 5, 5));

        // Draw Bottom Button
        drawX = btnBaseX;
        if (bottomBtnPressed) {
            drawX -= 5; // Move "down" (inwards)
        }
        
        g2.setColor(new Color(180, 180, 180));
        g2.fill(new RoundRectangle2D.Double(drawX, bottomBtnY, btnWidth, btnHeight, 5, 5));
        g2.setColor(new Color(100, 100, 100));
        g2.draw(new RoundRectangle2D.Double(drawX, bottomBtnY, btnWidth, btnHeight, 5, 5));
    }

    private void drawCase(Graphics2D g2, int cx, int cy, int radius) {
        // Metallic Case - Brushed Steel look
        GradientPaint metal = new GradientPaint(
            cx - radius, cy - radius, new Color(200, 200, 200),
            cx + radius, cy + radius, new Color(100, 100, 100)
        );
        g2.setPaint(metal);
        g2.fillOval(cx - radius - 25, cy - radius - 25, (radius + 25) * 2, (radius + 25) * 2);
        
        // Inner Bezel Ring
        g2.setColor(new Color(30, 30, 30));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
    }

    private void drawDial(Graphics2D g2, int cx, int cy, int radius) {
        // Dial Face - Deep Navy/Black sunburst effect simulation (simplified to solid for Swing)
        g2.setColor(new Color(10, 15, 30)); 
        g2.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);

        // Citizen Logo
        g2.setColor(Color.LIGHT_GRAY);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        String logo = "CITIZEN";
        g2.drawString(logo, cx - fm.stringWidth(logo) / 2, cy - radius / 2);
        
        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        String subText = "Eco-Drive";
        fm = g2.getFontMetrics();
        g2.drawString(subText, cx - fm.stringWidth(subText) / 2, cy - radius / 2 + 19);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        String radioText = "RADIO CONTROLLED";
        fm = g2.getFontMetrics();
        g2.drawString(radioText, cx - fm.stringWidth(radioText) / 2, cy - radius / 2 + 38);

        // Zone Name Display - Moved up slightly
        g2.setColor(new Color(200, 200, 200));
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        String zoneText = zoneNames[currentZoneIndex];
        fm = g2.getFontMetrics();
        g2.drawString(zoneText, cx - fm.stringWidth(zoneText) / 2, cy - 50);

        // Designer Text at bottom
        g2.setColor(new Color(150, 150, 150));
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        String designText = "DESIGNED BY DOBROSAV VLASKOVIC";
        fm = g2.getFontMetrics();
        g2.drawString(designText, cx - fm.stringWidth(designText) / 2, cy + radius - 50);

        // Hour Markers
        for (int i = 0; i < 60; i++) {
            double angle = Math.toRadians(6 * i - 90);
            
            if (i % 5 == 0) {
                // Hour Indices
                int hour = i / 5;
                if (hour == 0) hour = 12;

                int startR = radius - 31;
                int endR = radius - 6;
                
                // Draw "12" explicitly as requested
                if (hour == 12) {
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Serif", Font.BOLD, 45));
                    String num = "12";
                    fm = g2.getFontMetrics();
                    // Draw slightly inside the tick marks
                    g2.drawString(num, cx - fm.stringWidth(num) / 2, cy - radius + 75);
                    
                    // Small marker above 12
                     g2.setStroke(new BasicStroke(5));
                     g2.setColor(new Color(220, 220, 220));
                     g2.drawLine(
                        (int) (cx + Math.cos(angle) * startR), (int) (cy + Math.sin(angle) * startR),
                        (int) (cx + Math.cos(angle) * endR), (int) (cy + Math.sin(angle) * endR)
                    );
                } else {
                    // Regular baton markers for other hours
                    g2.setStroke(new BasicStroke(5));
                    g2.setColor(new Color(220, 220, 220)); // Steel indices
                    g2.drawLine(
                        (int) (cx + Math.cos(angle) * startR), (int) (cy + Math.sin(angle) * startR),
                        (int) (cx + Math.cos(angle) * endR), (int) (cy + Math.sin(angle) * endR)
                    );
                }
            } else {
                // Minute ticks
                g2.setStroke(new BasicStroke(1.2f));
                g2.setColor(new Color(100, 100, 100));
                g2.drawLine(
                    (int) (cx + Math.cos(angle) * (radius - 12)), (int) (cy + Math.sin(angle) * (radius - 12)),
                    (int) (cx + Math.cos(angle) * (radius - 6)), (int) (cy + Math.sin(angle) * (radius - 6))
                );
            }
        }
        
        // Date Window at 4 o'clock position (approx angle 30 degrees)
        double dateAngle = Math.toRadians(30); 
        int dateDist = radius - 60; // Slightly inside
        int dateW = 42; // Increased width (was 30)
        int dateH = 32; // Increased height (was 23)
        int dateX = (int)(cx + Math.cos(dateAngle) * dateDist) - dateW / 2;
        int dateY = (int)(cy + Math.sin(dateAngle) * dateDist) - dateH / 2;
        
        // Rotate for 4 o'clock alignment? No, usually horizontal.
        g2.setColor(Color.WHITE);
        g2.fillRect(dateX, dateY, dateW, dateH);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18)); // Slightly larger/bolder font
        
        String dateStr = "18";
        FontMetrics dateFm = g2.getFontMetrics();
        int dateTextX = dateX + (dateW - dateFm.stringWidth(dateStr)) / 2;
        int dateTextY = dateY + (dateH - dateFm.getHeight()) / 2 + dateFm.getAscent();
        
        g2.drawString(dateStr, dateTextX, dateTextY); 
        g2.setColor(Color.GRAY);
        g2.setStroke(new BasicStroke(1));
        g2.drawRect(dateX, dateY, dateW, dateH);
    }

    private void drawHands(Graphics2D g2, int cx, int cy, int radius) {
        ZonedDateTime now = ZonedDateTime.now(zones[currentZoneIndex]);
        int second = now.getSecond();
        int minute = now.getMinute();
        int hour = now.getHour();

        double secondAngle = Math.toRadians(6 * second - 90);
        double minuteAngle = Math.toRadians(6 * minute + 0.1 * second - 90);
        double hourAngle = Math.toRadians(30 * (hour % 12) + 0.5 * minute - 90);

        // Hour Hand (Sword shape)
        g2.setColor(new Color(230, 230, 230));
        drawSwordHand(g2, cx, cy, hourAngle, radius * 0.55, 8);

        // Minute Hand (Sword shape)
        g2.setColor(new Color(230, 230, 230));
        drawSwordHand(g2, cx, cy, minuteAngle, radius * 0.8, 5);

        // Second Hand (Citizen often has a simple thin needle, sometimes yellow or white)
        g2.setColor(new Color(200, 180, 50)); // Gold/Yellowish accent
        g2.setStroke(new BasicStroke(1));
        int secLen = (int) (radius * 0.9);
        int tailLen = 31;
        g2.drawLine(
            (int)(cx - Math.cos(secondAngle) * tailLen), (int)(cy - Math.sin(secondAngle) * tailLen),
            (int)(cx + Math.cos(secondAngle) * secLen), (int)(cy + Math.sin(secondAngle) * secLen)
        );
        
        // Central Pivot
        g2.fillOval(cx - 4, cy - 4, 8, 8);
    }
    
    private void drawSwordHand(Graphics2D g2, int cx, int cy, double angle, double length, int width) {
        AffineTransform old = g2.getTransform();
        g2.translate(cx, cy);
        g2.rotate(angle);
        
        Path2D path = new Path2D.Double();
        path.moveTo(0, -width/2.0);
        path.lineTo(length, 0);
        path.lineTo(0, width/2.0);
        path.lineTo(-12, 0); // Short tail
        path.closePath();
        
        g2.fill(path);
        
        // Lume strip in the middle
        g2.setColor(new Color(50, 50, 50));
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(6, 0, (int)(length * 0.8), 0);
        
        g2.setTransform(old);
    }

    private void drawCrystalReflection(Graphics2D g2, int cx, int cy, int radius) {
        // Subtle reflection on the glass
        GradientPaint reflection = new GradientPaint(
            cx - radius, cy - radius, new Color(255, 255, 255, 30),
            cx, cy, new Color(255, 255, 255, 0)
        );
        g2.setPaint(reflection);
        g2.fillOval(cx - radius, cy - radius, radius * 2, radius * 2 / 2); // Top half gloss
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Try to set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("Citizen DoWatch Model");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            DoWatch watch = new DoWatch();
            frame.add(watch);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
