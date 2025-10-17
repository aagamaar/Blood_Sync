import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AdminDashboard extends JPanel {
    private JTable donorsTable, patientsTable, requestsTable;
    private JButton refreshButton, logoutButton;
    private JTabbedPane tabbedPane;
    private JLabel welcomeLabel;

    public AdminDashboard() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String userName = "Admin";
        try {
            userName = AuthService.getUserName(BloodDonationSystemGUI.getCurrentUserId(), "admin");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        welcomeLabel = new JLabel("Admin Dashboard - Welcome, " + userName, JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        tabbedPane = new JTabbedPane();

        donorsTable = new JTable();
        tabbedPane.addTab("Donors", new JScrollPane(donorsTable));

        patientsTable = new JTable();
        tabbedPane.addTab("Patients", new JScrollPane(patientsTable));

        requestsTable = new JTable();
        tabbedPane.addTab("Blood Requests", new JScrollPane(requestsTable));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        refreshButton = new JButton("Refresh Data");
        logoutButton = new JButton("Logout");

        refreshButton.addActionListener(e -> refreshData());
        logoutButton.addActionListener(e -> BloodDonationSystemGUI.showLoginScreen());

        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);

        add(welcomeLabel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshData();
    }

    public void refreshData() {
        try {
            refreshDonorsTable();
            refreshPatientsTable();
            refreshRequestsTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error refreshing data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshDonorsTable() throws SQLException {
        List<Object[]> donors = AdminService.getAllDonorsForTable();
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Name", "Blood Group", "Location", "Age", "Contact Info"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Object[] donor : donors) {
            model.addRow(donor);
        }

        donorsTable.setModel(model);
    }

    private void refreshPatientsTable() throws SQLException {
        List<Object[]> patients = AdminService.getAllPatientsForTable();
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Name", "Blood Group", "Location"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Object[] patient : patients) {
            model.addRow(patient);
        }

        patientsTable.setModel(model);
    }

    private void refreshRequestsTable() throws SQLException {
        List<Object[]> requests = AdminService.getAllRequestsForTable();
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Request ID", "Patient ID", "Patient Name", "Donor ID", "Donor Name", "Status", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Object[] request : requests) {
            model.addRow(request);
        }

        requestsTable.setModel(model);
    }
}