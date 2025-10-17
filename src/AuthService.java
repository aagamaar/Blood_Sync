import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

    public static int login(String username, String password, String userType) throws SQLException {
        String tableName = "";
        String idColumn = "";

        switch (userType.toLowerCase()) {
            case "admin":
                tableName = "admins";
                idColumn = "admin_id";
                break;
            case "donor":
                tableName = "donors";
                idColumn = "donor_id";
                break;
            case "patient":
                tableName = "patients";
                idColumn = "patient_id";
                break;
            default:
                throw new IllegalArgumentException("Invalid user type.");
        }

        String sql = "SELECT " + idColumn + " FROM " + tableName + " WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(idColumn);
                }
            }
        }
        return -1;
    }

    public static String getUserName(int userId, String userType) throws SQLException {
        String tableName = "";
        String idColumn = "";
        String nameColumn = "name";

        switch (userType.toLowerCase()) {
            case "admin":
                tableName = "admins";
                idColumn = "admin_id";
                break;
            case "donor":
                tableName = "donors";
                idColumn = "donor_id";
                break;
            case "patient":
                tableName = "patients";
                idColumn = "patient_id";
                break;
            default:
                throw new IllegalArgumentException("Invalid user type.");
        }

        String sql = "SELECT " + nameColumn + " FROM " + tableName + " WHERE " + idColumn + " = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(nameColumn);
                }
            }
        }
        return "Unknown";
    }

    public static boolean registerPatient(String username, String password, String name, String bloodGroup, String location) throws SQLException {
        String sql = "INSERT INTO patients (username, password, name, blood_group, location) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, name);
            pstmt.setString(4, bloodGroup);
            pstmt.setString(5, location);
            return pstmt.executeUpdate() > 0;
        }
    }

    public static boolean registerDonor(String username, String password, String name, int age, String bloodGroup, String location, String contactInfo, String healthStatus) throws SQLException {
        String sql = "INSERT INTO donors (username, password, name, age, blood_group, location, contact_info, health_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, name);
            pstmt.setInt(4, age);
            pstmt.setString(5, bloodGroup);
            pstmt.setString(6, location);
            pstmt.setString(7, contactInfo);
            pstmt.setString(8, healthStatus);
            return pstmt.executeUpdate() > 0;
        }
    }
}