import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class RegistrationPanel extends JPanel {
    private String currentUserType;
    private JPanel formPanel;
    private JTextField usernameField, nameField, locationField, contactField, healthField;
    private JPasswordField passwordField;
    private JComboBox<String> bloodGroupCombo;
    private JSpinner ageSpinner;
    private JButton registerButton, backButton;

    public RegistrationPanel() {
        initializeUI();
    }

    public void setUserType(String userType) {
        this.currentUserType = userType;
        updateFormForUserType();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel titleLabel = new JLabel("Registration", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel buttonPanel = new JPanel();
        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        registerButton.addActionListener(new RegisterAction());
        backButton.addActionListener(e -> BloodDonationSystemGUI.showLoginScreen());

        add(titleLabel, BorderLayout.NORTH);
        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateFormForUserType() {
        formPanel.removeAll();

        addFormField("Username:", usernameField = new JTextField());
        addFormField("Password:", passwordField = new JPasswordField());
        addFormField("Name:", nameField = new JTextField());
        addFormField("Blood Group:", bloodGroupCombo = new JComboBox<>(
                new String[]{"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"}));
        addFormField("Location:", locationField = new JTextField());

        if ("donor".equals(currentUserType)) {
            addFormField("Age:", ageSpinner = new JSpinner(new SpinnerNumberModel(18, 18, 65, 1)));
            addFormField("Contact Info:", contactField = new JTextField());
            addFormField("Health Status:", healthField = new JTextField());
        }

        formPanel.revalidate();
        formPanel.repaint();
    }

    private void addFormField(String label, JComponent field) {
        formPanel.add(new JLabel(label));
        formPanel.add(field);
    }

    private class RegisterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                boolean success = false;

                if ("patient".equals(currentUserType)) {
                    success = registerPatient();
                } else if ("donor".equals(currentUserType)) {
                    success = registerDonor();
                }

                if (success) {
                    JOptionPane.showMessageDialog(RegistrationPanel.this,
                            "Registration successful! You can now login.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    BloodDonationSystemGUI.showLoginScreen();
                } else {
                    JOptionPane.showMessageDialog(RegistrationPanel.this,
                            "Registration failed. Username might already exist.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(RegistrationPanel.this,
                        "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(RegistrationPanel.this,
                        "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private boolean registerPatient() throws SQLException {
            return AuthService.registerPatient(
                    usernameField.getText(),
                    new String(passwordField.getPassword()),
                    nameField.getText(),
                    bloodGroupCombo.getSelectedItem().toString(),
                    locationField.getText()
            );
        }

        private boolean registerDonor() throws SQLException {
            return AuthService.registerDonor(
                    usernameField.getText(),
                    new String(passwordField.getPassword()),
                    nameField.getText(),
                    (Integer) ageSpinner.getValue(),
                    bloodGroupCombo.getSelectedItem().toString(),
                    locationField.getText(),
                    contactField.getText(),
                    healthField.getText()
            );
        }
    }
}