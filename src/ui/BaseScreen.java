package ui;

import javax.swing.*;
import utils.UIConstants;

public class BaseScreen extends JFrame {
    protected BaseScreen() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        UIConstants.styleFrame(this);
        setVisible(false);
    }
}
