package utils;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class UIConstants {
    
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    public static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    public static final Color WARNING_COLOR = new Color(241, 196, 15);
    public static final Color ERROR_COLOR = new Color(231, 76, 60);
    public static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    public static final Color PANEL_BACKGROUND = Color.WHITE;

   
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

   
    public static final Border PANEL_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15));

  
    public static final Dimension BUTTON_SIZE = new Dimension(120, 35);
    public static final Dimension FIELD_SIZE = new Dimension(200, 30);
    public static final int PADDING = 15;

    
    public static void styleButton(JButton button) {
        button.setFont(REGULAR_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(BUTTON_SIZE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(SECONDARY_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
    }

    public static void styleTextField(JTextField textField) {
        textField.setFont(REGULAR_FONT);
        textField.setPreferredSize(FIELD_SIZE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 204, 204)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    public static void stylePanel(JPanel panel) {
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(PANEL_BORDER);
    }

    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
        frame.getRootPane().setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
    }
}
