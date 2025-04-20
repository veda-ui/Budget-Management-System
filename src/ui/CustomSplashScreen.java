package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class CustomSplashScreen extends JWindow {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private final Color primaryColor = new Color(41, 128, 185);
    private final Color secondaryColor = new Color(52, 152, 219);
    private JProgressBar progressBar;
    private int progress = 0;

    public CustomSplashScreen() {
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setContentPane(createContent());
        setAlwaysOnTop(true);
    }

    private JPanel createContent() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0, primaryColor, getWidth(), getHeight(), secondaryColor);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.fill(new Ellipse2D.Double(-50, -50, 200, 200));
                g2d.fill(new Ellipse2D.Double(getWidth() - 100, getHeight() - 100, 200, 200));

                g2d.setFont(new Font("Segoe UI", Font.BOLD, 32));
                g2d.setColor(Color.WHITE);
                String title = "Budget Management System";
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(title, (getWidth() - fm.stringWidth(title)) / 2, getHeight() / 3);
            }
        };
    }

    public void showSplashAndConnect(Runnable onComplete) {
        JLabel status = new JLabel("Initializing...", SwingConstants.CENTER);
        status.setForeground(Color.WHITE);
        status.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(46, 204, 113));
        progressBar.setBackground(new Color(255, 255, 255, 50));
        progressBar.setBorder(BorderFactory.createEmptyBorder());
        progressBar.setPreferredSize(new Dimension(400, 10));

        JPanel progressPanel = new JPanel(new BorderLayout(0, 10));
        progressPanel.setOpaque(false);
        progressPanel.add(status, BorderLayout.NORTH);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 50, 50));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(progressPanel, BorderLayout.SOUTH);

        setVisible(true);
        simulateLoading(status, onComplete);
    }

    private void simulateLoading(JLabel status, Runnable onComplete) {
        Timer timer = new Timer(30, e -> {
            progress += 1;
            progressBar.setValue(progress);

            if (progress < 33) {
                status.setText("Initializing components...");
            } else if (progress < 66) {
                status.setText("Connecting to database...");
            } else if (progress < 100) {
                status.setText("Loading application...");
            } else {
                ((Timer) e.getSource()).stop();
                dispose();
                onComplete.run(); 
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CustomSplashScreen splash = new CustomSplashScreen();
            splash.showSplashAndConnect(() -> {
                
                System.out.println("Splash screen completed.");
            });
        });
    }
}