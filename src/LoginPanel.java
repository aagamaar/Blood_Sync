import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginPanel extends JPanel {
    private JComboBox<String> userTypeCombo;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    private final Color PRIMARY_COLOR = new Color(204, 0, 0);
    private final Color SECONDARY_COLOR = new Color(255, 102, 102);
    private final Color BACKGROUND_COLOR = new Color(255, 245, 245);
    private final Color ACCENT_COLOR = new Color(0, 102, 204);

    public LoginPanel() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel headerPanel = createHeaderPanel();
        JPanel formPanel = createFormPanel();
        JPanel buttonPanel = createButtonPanel();

        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JLabel titleLabel = new JLabel("🩸 Blood Donation System", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(PRIMARY_COLOR);

        JLabel subtitleLabel = new JLabel("Save Lives, Donate Blood", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.DARK_GRAY);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PRIMARY_COLOR, 2, true),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel typeLabel = createStyledLabel("User Type:");
        formPanel.add(typeLabel, gbc);

        gbc.gridx = 1;
        userTypeCombo = new JComboBox<>(new String[]{"Admin", "Patient", "Donor"});
        styleComboBox(userTypeCombo);
        formPanel.add(userTypeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel userLabel = createStyledLabel("Username:");
        formPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        styleTextField(usernameField);
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel passLabel = createStyledLabel("Password:");
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        styleTextField(passwordField);
        formPanel.add(passwordField, gbc);

        return formPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        loginButton = createStyledButton("🔐 Login", PRIMARY_COLOR);
        registerButton = createStyledButton("📝 Register New Account", ACCENT_COLOR);

        loginButton.addActionListener(new LoginAction());
        registerButton.addActionListener(new RegisterAction());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        return buttonPanel;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SECONDARY_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SECONDARY_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String userType = userTypeCombo.getSelectedItem().toString().toLowerCase();

            if (username.isEmpty() || password.isEmpty()) {
                showMessage("Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int userId = AuthService.login(username, password, userType);
                if (userId != -1) {
                    showMessage("Login successful! Welcome back.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    BloodDonationSystemGUI.showDashboard(userType, userId);
                    clearFields();
                } else {
                    showMessage("Login failed. Please check your credentials.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                showMessage("Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class RegisterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String userType = userTypeCombo.getSelectedItem().toString().toLowerCase();
            if ("admin".equals(userType)) {
                showMessage("Admin registration is not available.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            BloodDonationSystemGUI.showRegistrationScreen(userType);
        }
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }
}