import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DonorDashboard extends JPanel {
    private int donorId;
    private JTable requestsTable;
    private JButton refreshButton, respondButton, logoutButton;
    private JComboBox<String> statusCombo;
    private JLabel welcomeLabel;

    public DonorDashboard() {
        initializeUI();
    }

    public void setDonorId(int donorId) {
        this.donorId = donorId;
        updateWelcomeMessage();
        refreshRequests();
    }

    private void updateWelcomeMessage() {
        try {
            String userName = AuthService.getUserName(donorId, "donor");
            welcomeLabel.setText("Donor Dashboard - Welcome, " + userName);
        } catch (SQLException e) {
            welcomeLabel.setText("Donor Dashboard - Welcome");
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        welcomeLabel = new JLabel("Donor Dashboard - Welcome", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Create a more prominent requests panel
        JPanel requestsPanel = new JPanel(new BorderLayout());
        requestsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(204, 0, 0), 2),
                "📋 Pending Blood Requests"
        ));

        requestsTable = new JTable();
        requestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requestsTable.setRowHeight(30);
        JScrollPane tableScrollPane = new JScrollPane(requestsTable);
        requestsPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Enhanced control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        controlPanel.add(new JLabel("Your Response:"));
        statusCombo = new JComboBox<>(new String[]{"Accepted", "Rejected"});
        statusCombo.setFont(new Font("Arial", Font.BOLD, 12));
        controlPanel.add(statusCombo);

        respondButton = new JButton("✅ Submit Response");
        respondButton.setBackground(new Color(0, 153, 0));
        respondButton.setForeground(Color.WHITE);
        respondButton.setFont(new Font("Arial", Font.BOLD, 12));
        controlPanel.add(respondButton);

        refreshButton = new JButton("🔄 Refresh Requests");
        controlPanel.add(refreshButton);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        logoutButton = new JButton("🚪 Logout");
        buttonPanel.add(logoutButton);

        refreshButton.addActionListener(e -> refreshRequests());
        respondButton.addActionListener(new RespondAction());
        logoutButton.addActionListener(e -> BloodDonationSystemGUI.showLoginScreen());

        add(welcomeLabel, BorderLayout.NORTH);
        add(requestsPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void refreshRequests() {
        try {
            List<Object[]> requests = DonorService.getPendingRequestsWithDetails(donorId);
            updateRequestsTable(requests);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading requests: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class RespondAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = requestsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(DonorDashboard.this,
                        "❌ Please select a blood request from the list above.",
                        "No Request Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int modelRow = requestsTable.convertRowIndexToModel(selectedRow);
                DefaultTableModel model = (DefaultTableModel) requestsTable.getModel();

                // CORRECTED COLUMN INDICES:
                int requestId = (Integer) model.getValueAt(modelRow, 0);        // Request ID (hidden)
                String patientName = (String) model.getValueAt(modelRow, 2);    // Patient Name
                String bloodGroup = (String) model.getValueAt(modelRow, 3);     // Blood Group
                String status = statusCombo.getSelectedItem().toString();

                String message = "";
                int messageType = JOptionPane.QUESTION_MESSAGE;

                if ("Accepted".equals(status)) {
                    message = "🎉 You're about to ACCEPT the blood request from:\n\n" +
                            "Patient: " + patientName + "\n" +
                            "Blood Group Needed: " + bloodGroup + "\n\n" +
                            "This will notify the patient and help save a life!\n" +
                            "Are you sure you want to proceed?";
                } else {
                    message = "⚠️ You're about to DECLINE the blood request from:\n\n" +
                            "Patient: " + patientName + "\n" +
                            "Blood Group Needed: " + bloodGroup + "\n\n" +
                            "The patient will be notified of your decision.\n" +
                            "Are you sure you want to proceed?";
                }

                int response = JOptionPane.showConfirmDialog(
                        DonorDashboard.this,
                        message,
                        "Confirm Your Decision",
                        JOptionPane.YES_NO_OPTION,
                        messageType
                );

                if (response == JOptionPane.YES_OPTION) {
                    boolean success = DonorService.updateRequestStatus(requestId, status);
                    if (success) {
                        // Send notification to patient
                        String notificationMsg = "Donor has " + status.toLowerCase() +
                                " your blood request for " + bloodGroup;
                        DonorService.createNotificationForPatient(getPatientIdFromRequest(requestId), notificationMsg);

                        String successMessage = "Accepted".equals(status) ?
                                "🎉 Thank you! You've accepted the blood request.\nThe patient has been notified of your generous offer!" :
                                "ℹ️ You've declined the blood request.\nThe patient has been notified.";

                        JOptionPane.showMessageDialog(DonorDashboard.this,
                                successMessage,
                                "Response Submitted",
                                JOptionPane.INFORMATION_MESSAGE);
                        refreshRequests();
                    } else {
                        JOptionPane.showMessageDialog(DonorDashboard.this,
                                "❌ Failed to update request status. Please try again.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(DonorDashboard.this,
                        "❌ Error processing response: " + ex.getMessage(),
                        "System Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }

        private int getPatientIdFromRequest(int requestId) throws SQLException {
            String sql = "SELECT patient_id FROM requests WHERE request_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, requestId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("patient_id");
                    }
                }
            }
            return -1;
        }
    } // ← Closing brace for RespondAction class

    private void updateRequestsTable(List<Object[]> requests) {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Request ID", "Patient ID", "Patient Name", "Blood Group", "Location", "Request Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Object[] request : requests) {
            model.addRow(request);
        }

        requestsTable.setModel(model);

        // Hide ID columns instead of removing them
        if (requestsTable.getColumnCount() > 0) {
            requestsTable.getColumnModel().getColumn(0).setMinWidth(0);
            requestsTable.getColumnModel().getColumn(0).setMaxWidth(0);
            requestsTable.getColumnModel().getColumn(0).setWidth(0);

            requestsTable.getColumnModel().getColumn(1).setMinWidth(0);
            requestsTable.getColumnModel().getColumn(1).setMaxWidth(0);
            requestsTable.getColumnModel().getColumn(1).setWidth(0);
        }
    }
} // ← Closing brace for DonorDashboard class