import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DonorService {

    public static List<Object[]> getPendingRequestsWithDetails(int donorId) throws SQLException {
        List<Object[]> requests = new ArrayList<>();
        String sql = "SELECT r.request_id, p.patient_id, p.name AS patient_name, p.blood_group, " +
                "p.location, r.request_date " +
                "FROM requests r JOIN patients p ON r.patient_id = p.patient_id " +
                "WHERE r.donor_id = ? AND r.status = 'Pending'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, donorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                            rs.getInt("request_id"),
                            rs.getInt("patient_id"),
                            rs.getString("patient_name"),
                            rs.getString("blood_group"),
                            rs.getString("location"),
                            rs.getString("request_date")
                    };
                    requests.add(row);
                }
            }
        }
        return requests;
    }

    public static boolean updateRequestStatus(int requestId, String status) throws SQLException {
        String sql = "UPDATE requests SET status = ? WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, requestId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public static void createNotificationForPatient(int patientId, String message) throws SQLException {
        String sql = "INSERT INTO notifications (user_id, user_type, message) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            pstmt.setString(2, "patient");
            pstmt.setString(3, message);
            pstmt.executeUpdate();
        }
    }

    public static int getPatientIdFromRequest(int requestId) throws SQLException {
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

    public static List<String> getPendingRequests(int donorId) throws SQLException {
        List<String> requests = new ArrayList<>();
        String sql = "SELECT r.request_id, p.name AS patient_name, p.blood_group FROM requests r JOIN patients p ON r.patient_id = p.patient_id WHERE r.donor_id = ? AND r.status = 'Pending'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, donorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String requestInfo = String.format(
                            "Request ID: %d, Patient: %s, Blood Group: %s",
                            rs.getInt("request_id"),
                            rs.getString("patient_name"),
                            rs.getString("blood_group")
                    );
                    requests.add(requestInfo);
                }
            }
        }
        return requests;
    }
}