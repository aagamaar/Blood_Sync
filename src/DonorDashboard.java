import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

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

        requestsTable = new JTable();
        requestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(requestsTable);

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("Response:"));
        statusCombo = new JComboBox<>(new String[]{"Accepted", "Rejected"});
        controlPanel.add(statusCombo);

        respondButton = new JButton("Respond to Selected Request");
        controlPanel.add(respondButton);

        refreshButton = new JButton("Refresh Requests");
        controlPanel.add(refreshButton);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        logoutButton = new JButton("Logout");

        buttonPanel.add(logoutButton);

        refreshButton.addActionListener(e -> refreshRequests());
        respondButton.addActionListener(new RespondAction());
        logoutButton.addActionListener(e -> BloodDonationSystemGUI.showLoginScreen());

        add(welcomeLabel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
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
                        "Please select a request from the table.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int requestId = (Integer) requestsTable.getValueAt(selectedRow, 0);
                String patientName = (String) requestsTable.getValueAt(selectedRow, 2);
                String status = statusCombo.getSelectedItem().toString();

                int response = JOptionPane.showConfirmDialog(
                        DonorDashboard.this,
                        "Are you sure you want to " + status.toLowerCase() + " the request from " + patientName + "?",
                        "Confirm Response",
                        JOptionPane.YES_NO_OPTION
                );

                if (response == JOptionPane.YES_OPTION) {
                    boolean success = DonorService.updateRequestStatus(requestId, status);
                    if (success) {
                        JOptionPane.showMessageDialog(DonorDashboard.this,
                                "Request " + status.toLowerCase() + " successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        refreshRequests();
                    } else {
                        JOptionPane.showMessageDialog(DonorDashboard.this,
                                "Failed to update request status.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(DonorDashboard.this,
                        "Error processing response: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

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

        if (requestsTable.getColumnCount() > 0) {
            requestsTable.removeColumn(requestsTable.getColumnModel().getColumn(0));
            requestsTable.removeColumn(requestsTable.getColumnModel().getColumn(0));
        }
    }
}