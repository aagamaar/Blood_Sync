import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminService {

    public static List<Object[]> getAllDonorsForTable() throws SQLException {
        List<Object[]> donors = new ArrayList<>();
        String sql = "SELECT donor_id, name, blood_group, location, age, contact_info FROM donors";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("donor_id"),
                        rs.getString("name"),
                        rs.getString("blood_group"),
                        rs.getString("location"),
                        rs.getInt("age"),
                        rs.getString("contact_info")
                };
                donors.add(row);
            }
        }
        return donors;
    }

    public static List<Object[]> getAllPatientsForTable() throws SQLException {
        List<Object[]> patients = new ArrayList<>();
        String sql = "SELECT patient_id, name, blood_group, location FROM patients";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("patient_id"),
                        rs.getString("name"),
                        rs.getString("blood_group"),
                        rs.getString("location")
                };
                patients.add(row);
            }
        }
        return patients;
    }

    public static List<Object[]> getAllRequestsForTable() throws SQLException {
        List<Object[]> requests = new ArrayList<>();
        String sql = "SELECT r.request_id, r.patient_id, p.name as patient_name, " +
                "r.donor_id, d.name as donor_name, r.status, r.request_date " +
                "FROM requests r " +
                "LEFT JOIN patients p ON r.patient_id = p.patient_id " +
                "LEFT JOIN donors d ON r.donor_id = d.donor_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("request_id"),
                        rs.getInt("patient_id"),
                        rs.getString("patient_name"),
                        rs.getInt("donor_id"),
                        rs.getString("donor_name"),
                        rs.getString("status"),
                        rs.getString("request_date")
                };
                requests.add(row);
            }
        }
        return requests;
    }

    public static void viewAllDonors() throws SQLException {
        String sql = "SELECT * FROM donors";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            System.out.println("--- All Donors ---");
            while (rs.next()) {
                System.out.printf("ID: %d, Name: %s, Blood Group: %s, Location: %s%n",
                        rs.getInt("donor_id"),
                        rs.getString("name"),
                        rs.getString("blood_group"),
                        rs.getString("location")
                );
            }
        }
    }

    public static void viewAllPatients() throws SQLException {
        String sql = "SELECT * FROM patients";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            System.out.println("--- All Patients ---");
            while (rs.next()) {
                System.out.printf("ID: %d, Name: %s, Blood Group: %s, Location: %s%n",
                        rs.getInt("patient_id"),
                        rs.getString("name"),
                        rs.getString("blood_group"),
                        rs.getString("location")
                );
            }
        }
    }

    public static void viewAllRequests() throws SQLException {
        String sql = "SELECT * FROM requests";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            System.out.println("--- All Blood Requests ---");
            while (rs.next()) {
                System.out.printf("Request ID: %d, Patient ID: %d, Donor ID: %d, Status: %s, Date: %s%n",
                        rs.getInt("request_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("donor_id"),
                        rs.getString("status"),
                        rs.getString("request_date")
                );
            }
        }
    }
}