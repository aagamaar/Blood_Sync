import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class PatientDashboard extends JPanel {
    private int patientId;
    private JTable donorsTable;
    private JComboBox<String> bloodGroupCombo;
    private JTextField locationField;
    private JButton searchButton, sendRequestButton, logoutButton;
    private JLabel welcomeLabel;

    public PatientDashboard() {
        initializeUI();
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
        updateWelcomeMessage();
    }

    private void updateWelcomeMessage() {
        try {
            String userName = AuthService.getUserName(patientId, "patient");
            welcomeLabel.setText("Patient Dashboard - Welcome, " + userName);
        } catch (SQLException e) {
            welcomeLabel.setText("Patient Dashboard - Welcome");
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        welcomeLabel = new JLabel("Patient Dashboard - Welcome", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(new JLabel("Blood Group:"));
        bloodGroupCombo = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"});
        searchPanel.add(bloodGroupCombo);

        searchPanel.add(new JLabel("Location:"));
        locationField = new JTextField(15);
        searchPanel.add(locationField);

        searchButton = new JButton("Search Donors");
        searchPanel.add(searchButton);

        donorsTable = new JTable();
        donorsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(donorsTable);

        JPanel actionPanel = new JPanel(new FlowLayout());
        sendRequestButton = new JButton("Send Request to Selected Donor");
        logoutButton = new JButton("Logout");

        actionPanel.add(sendRequestButton);
        actionPanel.add(logoutButton);

        searchButton.addActionListener(new SearchAction());
        sendRequestButton.addActionListener(new SendRequestAction());
        logoutButton.addActionListener(e -> BloodDonationSystemGUI.showLoginScreen());

        add(welcomeLabel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private class SearchAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String bloodGroup = bloodGroupCombo.getSelectedItem().toString();
            String location = locationField.getText().trim();

            if (location.isEmpty()) {
                JOptionPane.showMessageDialog(PatientDashboard.this,
                        "Please enter a location.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                List<Object[]> donors = PatientService.searchDonorsWithIds(bloodGroup, location);
                updateDonorsTable(donors);

                if (donors.isEmpty()) {
                    JOptionPane.showMessageDialog(PatientDashboard.this,
                            "No donors found with the specified criteria.", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(PatientDashboard.this,
                        "Search error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class SendRequestAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = donorsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(PatientDashboard.this,
                        "Please select a donor from the table.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int modelRow = donorsTable.convertRowIndexToModel(selectedRow);
                DefaultTableModel model = (DefaultTableModel) donorsTable.getModel();
                int donorId = (Integer) model.getValueAt(modelRow, 0);
                String donorName = (String) model.getValueAt(modelRow, 1);

                int response = JOptionPane.showConfirmDialog(
                        PatientDashboard.this,
                        "Send blood request to " + donorName + "?",
                        "Confirm Request",
                        JOptionPane.YES_NO_OPTION
                );

                if (response == JOptionPane.YES_OPTION) {
                    boolean success = PatientService.sendRequest(patientId, donorId);
                    if (success) {
                        PatientService.createNotificationForDonor(donorId, patientId);
                        JOptionPane.showMessageDialog(PatientDashboard.this,
                                "Request sent successfully to " + donorName + "!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(PatientDashboard.this,
                                "Failed to send request.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(PatientDashboard.this,
                        "Error sending request: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void updateDonorsTable(List<Object[]> donors) {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Name", "Age", "Blood Group", "Location", "Contact Info"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Integer.class : String.class;
            }
        };

        for (Object[] donor : donors) {
            model.addRow(donor);
        }

        donorsTable.setModel(model);
        donorsTable.removeColumn(donorsTable.getColumnModel().getColumn(0));
    }
}