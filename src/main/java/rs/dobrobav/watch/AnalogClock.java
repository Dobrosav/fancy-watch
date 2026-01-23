package rs.dobrobav.watch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Ellipse2D;
import java.time.LocalTime;

public class AnalogClock extends JPanel {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    
    public AnalogClock() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(30, 30, 30)); // Dark background
        
        // Timer to refresh the clock every 100ms for smooth updates (though seconds only tick once)
        Timer timer = new Timer(100, new ActionListener() {
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
        // High quality rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();
        
        // Center of the clock
        int cx = w / 2;
        int cy = h / 2;
        
        // Radius is slightly smaller than half the smallest dimension
        int radius = Math.min(w, h) / 2 - 20;

        drawClockFace(g2d, cx, cy, radius);
        drawHands(g2d, cx, cy, radius);
        drawCenterPivot(g2d, cx, cy);
    }

    private void drawClockFace(Graphics2D g2, int cx, int cy, int radius) {
        // Draw the outer rim
        g2.setColor(new Color(50, 50, 50));
        g2.fillOval(cx - radius, cy - radius, 2 * radius, 2 * radius);
        
        g2.setStroke(new BasicStroke(4));
        g2.setColor(new Color(200, 200, 200));
        g2.drawOval(cx - radius, cy - radius, 2 * radius, 2 * radius);

        // Draw hour and minute marks
        for (int i = 0; i < 60; i++) {
            double angle = Math.toRadians(6 * i - 90);
            int startRadius;
            int strokeWidth;
            
            if (i % 5 == 0) {
                // Hour mark
                startRadius = radius - 20;
                strokeWidth = 3;
                g2.setColor(Color.WHITE);
            } else {
                // Minute mark
                startRadius = radius - 10;
                strokeWidth = 1;
                g2.setColor(Color.GRAY);
            }

            int x1 = (int) (cx + Math.cos(angle) * startRadius);
            int y1 = (int) (cy + Math.sin(angle) * startRadius);
            int x2 = (int) (cx + Math.cos(angle) * (radius - 5));
            int y2 = (int) (cy + Math.sin(angle) * (radius - 5));

            g2.setStroke(new BasicStroke(strokeWidth));
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    private void drawHands(Graphics2D g2, int cx, int cy, int radius) {
        LocalTime now = LocalTime.now();
        int second = now.getSecond();
        int minute = now.getMinute();
        int hour = now.getHour();

        // Calculate angles (subtract 90 degrees to start from 12 o'clock)
        // Seconds: 6 degrees per second
        double secondAngle = Math.toRadians(6 * second - 90);
        
        // Minutes: 6 degrees per minute + adjust for seconds
        double minuteAngle = Math.toRadians(6 * minute + 0.1 * second - 90);
        
        // Hours: 30 degrees per hour + adjust for minutes
        double hourAngle = Math.toRadians(30 * (hour % 12) + 0.5 * minute - 90);

        // Draw Hour Hand
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int hourLen = (int) (radius * 0.5);
        g2.draw(new Line2D.Double(cx, cy, cx + Math.cos(hourAngle) * hourLen, cy + Math.sin(hourAngle) * hourLen));

        // Draw Minute Hand
        g2.setColor(new Color(220, 220, 220)); // Slightly off-white
        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int minuteLen = (int) (radius * 0.75);
        g2.draw(new Line2D.Double(cx, cy, cx + Math.cos(minuteAngle) * minuteLen, cy + Math.sin(minuteAngle) * minuteLen));

        // Draw Second Hand
        g2.setColor(new Color(255, 50, 50)); // Red
        g2.setStroke(new BasicStroke(2));
        int secondLen = (int) (radius * 0.85);
        int tailLen = 20; // Short tail in the opposite direction
        g2.draw(new Line2D.Double(
            cx - Math.cos(secondAngle) * tailLen, 
            cy - Math.sin(secondAngle) * tailLen, 
            cx + Math.cos(secondAngle) * secondLen, 
            cy + Math.sin(secondAngle) * secondLen
        ));
    }

    private void drawCenterPivot(Graphics2D g2, int cx, int cy) {
        g2.setColor(new Color(255, 50, 50));
        int r = 5;
        g2.fillOval(cx - r, cy - r, 2 * r, 2 * r);
        
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        g2.drawOval(cx - r, cy - r, 2 * r, 2 * r);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Elegant Analog Clock");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            AnalogClock clock = new AnalogClock();
            frame.add(clock);
            frame.pack();
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }
}
