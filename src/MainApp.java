import ui.CustomSplashScreen;
import ui.LoginScreen;
import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CustomSplashScreen splashScreen = new CustomSplashScreen();
            LoginScreen loginScreen = new LoginScreen();
            loginScreen.setExtendedState(JFrame.NORMAL); 
            loginScreen.setSize(2000, 1000); 
            splashScreen.showSplashAndConnect(() -> {
                loginScreen.setLocationRelativeTo(null); 
                loginScreen.toFront(); 
                loginScreen.requestFocus(); 
                loginScreen.setVisible(true); 
            });
        });
    }
}
