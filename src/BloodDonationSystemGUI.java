import javax.swing.*;
import java.awt.*;

public class BloodDonationSystemGUI {
    private static JFrame mainFrame;
    private static CardLayout cardLayout;
    private static JPanel mainPanel;

    private static int currentUserId = -1;
    private static String currentUserType = null;

    private static LoginPanel loginPanel;
    private static RegistrationPanel registrationPanel;
    private static AdminDashboard adminDashboard;
    private static PatientDashboard patientDashboard;
    private static DonorDashboard donorDashboard;

    public static void main(String[] args) {
        DBConnection.testConnection();

        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        mainFrame = new JFrame("Blood Donation Management System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1000, 700);
        mainFrame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel();
        registrationPanel = new RegistrationPanel();
        adminDashboard = new AdminDashboard();
        patientDashboard = new PatientDashboard();
        donorDashboard = new DonorDashboard();

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registrationPanel, "REGISTER");
        mainPanel.add(adminDashboard, "ADMIN");
        mainPanel.add(patientDashboard, "PATIENT");
        mainPanel.add(donorDashboard, "DONOR");

        mainFrame.add(mainPanel);
        showLoginScreen();
        mainFrame.setVisible(true);
    }

    public static void showLoginScreen() {
        cardLayout.show(mainPanel, "LOGIN");
        currentUserId = -1;
        currentUserType = null;
        if (loginPanel != null) {
            loginPanel.clearFields();
        }
    }

    public static void showRegistrationScreen(String userType) {
        if (registrationPanel != null) {
            registrationPanel.setUserType(userType);
            cardLayout.show(mainPanel, "REGISTER");
        }
    }

    public static void showDashboard(String userType, int userId) {
        currentUserId = userId;
        currentUserType = userType;

        switch (userType) {
            case "admin":
                adminDashboard.refreshData();
                cardLayout.show(mainPanel, "ADMIN");
                break;
            case "patient":
                patientDashboard.setPatientId(userId);
                cardLayout.show(mainPanel, "PATIENT");
                break;
            case "donor":
                donorDashboard.setDonorId(userId);
                cardLayout.show(mainPanel, "DONOR");
                break;
        }
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static String getCurrentUserType() {
        return currentUserType;
    }
}