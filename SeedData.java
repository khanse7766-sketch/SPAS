package com.spas.db;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SeedData {
    public static void initializeSeedData(Connection conn) {
        if (conn == null) return;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next() && rs.getInt(1) > 0) return;
            System.out.println("Empty database detected. Generating seed data with jBCrypt hashes...");

            int adminId = insertUser(conn, "System Admin", "admin@spas.edu.pk", "03001234567", "Admin@123", "ADMIN");
            insertUser(conn, "Dr. Teacher", "teacher@spas.edu.pk", "03001234568", "Teacher@123", "TEACHER");
            int studentUserId = insertUser(conn, "Test Student", "student@spas.edu.pk", "03001234569", "Student@123", "STUDENT");
            int studentId = insertStudent(conn, studentUserId, "CS-2023-01", "Fall 2023", 3);

            int dsaId = insertSubject(conn, "CS201", "Data Structures and Algorithms", 4, 3);
            int dbId = insertSubject(conn, "CS301", "Database Systems", 3, 3);
            insertMarks(conn, studentId, dsaId, 3, 8.5, 14.0, 25.0, 38.0);
            insertMarks(conn, studentId, dbId, 3, 9.0, 13.0, 24.0, 36.0);
            insertAttendance(conn, studentId, dsaId, 3, "PRESENT");
            insertAttendance(conn, studentId, dbId, 3, "PRESENT");
            insertPreference(conn, "theme", "light");
            insertActionLog(conn, "INSERT", "seed_data", adminId, "Initial seed data created");
            System.out.println("Seed data generation complete.");
        } catch (SQLException e) {
            System.err.println("Failed to seed data: " + e.getMessage());
        }
    }

    private static int insertUser(Connection conn, String name, String email, String phone, String password, String role) throws SQLException {
        String q = "INSERT INTO users (full_name, email, phone, password_hash, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(q, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, BCrypt.hashpw(password, BCrypt.gensalt()));
            stmt.setString(5, role);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Could not create user " + email);
    }

    private static int insertStudent(Connection conn, int userId, String rollNo, String batch, int semester) throws SQLException {
        String q = "INSERT INTO students (user_id, roll_no, batch, current_semester) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(q, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId);
            stmt.setString(2, rollNo);
            stmt.setString(3, batch);
            stmt.setInt(4, semester);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Could not create seed student.");
    }

    private static int insertSubject(Connection conn, String code, String name, int credits, int semester) throws SQLException {
        String q = "INSERT INTO subjects (subject_code, name, credit_hours, semester) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(q, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, code);
            stmt.setString(2, name);
            stmt.setInt(3, credits);
            stmt.setInt(4, semester);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Could not create seed subject.");
    }

    private static void insertMarks(Connection conn, int studentId, int subjectId, int semester, double quiz, double assignment, double midterm, double finalExam) throws SQLException {
        String q = "INSERT INTO marks (student_id, subject_id, semester, quiz, assignment, midterm, final_exam) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(q)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, subjectId);
            stmt.setInt(3, semester);
            stmt.setDouble(4, quiz);
            stmt.setDouble(5, assignment);
            stmt.setDouble(6, midterm);
            stmt.setDouble(7, finalExam);
            stmt.executeUpdate();
        }
    }

    private static void insertAttendance(Connection conn, int studentId, int subjectId, int semester, String status) throws SQLException {
        String q = "INSERT INTO attendance (student_id, subject_id, semester, att_date, status) VALUES (?, ?, ?, CURRENT_DATE, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(q)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, subjectId);
            stmt.setInt(3, semester);
            stmt.setString(4, status);
            stmt.executeUpdate();
        }
    }

    private static void insertPreference(Connection conn, String key, String value) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO system_preferences (pref_key, pref_value) VALUES (?, ?)")) {
            stmt.setString(1, key);
            stmt.setString(2, value);
            stmt.executeUpdate();
        }
    }

    private static void insertActionLog(Connection conn, String action, String table, int userId, String value) throws SQLException {
        String q = "INSERT INTO action_log (action_type, table_name, record_id, old_value, new_value, performed_by) VALUES (?, ?, 0, '', ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(q)) {
            stmt.setString(1, action);
            stmt.setString(2, table);
            stmt.setString(3, value);
            stmt.setInt(4, userId);
            stmt.executeUpdate();
        }
    }
}
