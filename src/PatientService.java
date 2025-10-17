import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientService {

    public static List<Object[]> searchDonorsWithIds(String bloodGroup, String location) throws SQLException {
        List<Object[]> donors = new ArrayList<>();
        String sql = "SELECT donor_id, name, age, blood_group, location, contact_info FROM donors " +
                "WHERE blood_group = ? AND location = ? AND availability = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bloodGroup);
            pstmt.setString(2, location);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                            rs.getInt("donor_id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("blood_group"),
                            rs.getString("location"),
                            rs.getString("contact_info")
                    };
                    donors.add(row);
                }
            }
        }
        return donors;
    }

    public static boolean sendRequest(int patientId, int donorId) throws SQLException {
        String sql = "INSERT INTO requests (patient_id, donor_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            pstmt.setInt(2, donorId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public static void createNotificationForDonor(int donorId, int patientId) throws SQLException {
        String message = "New blood request received from patient ID " + patientId;
        String sql = "INSERT INTO notifications (user_id, user_type, message) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, donorId);
            pstmt.setString(2, "donor");
            pstmt.setString(3, message);
            pstmt.executeUpdate();
        }
    }

    public static String getDonorContactInfo(int donorId) throws SQLException {
        String sql = "SELECT contact_info FROM donors WHERE donor_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, donorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("contact_info");
                }
            }
        }
        return "Contact info not found.";
    }

    public static List<String> searchDonors(String bloodGroup, String location) throws SQLException {
        List<String> donors = new ArrayList<>();
        String sql = "SELECT name, age, blood_group, location FROM donors WHERE blood_group = ? AND location = ? AND availability = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bloodGroup);
            pstmt.setString(2, location);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String donorInfo = String.format(
                            "Name: %s, Age: %d, Blood Group: %s, Location: %s",
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("blood_group"),
                            rs.getString("location")
                    );
                    donors.add(donorInfo);
                }
            }
        }
        return donors;
    }
}