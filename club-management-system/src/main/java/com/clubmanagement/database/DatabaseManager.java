package com.clubmanagement.database;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_NAME = "club_management.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);
                initializeDatabase();
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC driver not found", e);
            }
        }
        return connection;
    }

    private static void initializeDatabase() throws SQLException {
        createTables();
        migrateDatabase();
        insertDefaultData();
    }

    private static void createTables() throws SQLException {
        String[] createTableQueries = {
            // Users table
            """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                password_salt TEXT,
                email TEXT UNIQUE,
                full_name TEXT,
                security_question TEXT,
                security_answer TEXT,
                role TEXT NOT NULL CHECK (role IN ('CLUB_MANAGER', 'GRADE_11', 'GRADE_9')),
                assigned_club_id INTEGER,
                first_login_completed BOOLEAN DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                last_login TIMESTAMP,
                is_active BOOLEAN DEFAULT 1,
                FOREIGN KEY (assigned_club_id) REFERENCES clubs(id)
            )
            """,

            // Proposals table
            """
            CREATE TABLE IF NOT EXISTS proposals (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER NOT NULL,
                file_path TEXT NOT NULL,
                status TEXT NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED')),
                FOREIGN KEY (student_id) REFERENCES users(id)
            )
            """,

            // Enhanced attendance table
            """
            CREATE TABLE IF NOT EXISTS attendance (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER NOT NULL,
                club_id INTEGER NOT NULL,
                marked_by_id INTEGER NOT NULL,
                session_date DATE NOT NULL,
                session_time TIME DEFAULT '14:30:00',
                status TEXT NOT NULL DEFAULT 'PRESENT' CHECK (status IN ('PRESENT', 'ABSENT', 'LATE', 'EXCUSED')),
                notes TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (student_id) REFERENCES users(id),
                FOREIGN KEY (club_id) REFERENCES clubs(id),
                FOREIGN KEY (marked_by_id) REFERENCES users(id),
                UNIQUE(student_id, club_id, session_date)
            )
            """,

            // Attendance sessions table
            """
            CREATE TABLE IF NOT EXISTS attendance_sessions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                club_id INTEGER NOT NULL,
                session_date DATE NOT NULL,
                session_time TIME DEFAULT '14:30:00',
                session_title TEXT,
                session_description TEXT,
                created_by_id INTEGER NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_active BOOLEAN DEFAULT 1,
                FOREIGN KEY (club_id) REFERENCES clubs(id),
                FOREIGN KEY (created_by_id) REFERENCES users(id),
                UNIQUE(club_id, session_date)
            )
            """,

            // Clubs table
            """
            CREATE TABLE IF NOT EXISTS clubs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT UNIQUE NOT NULL
            )
            """,

            // Club allocation table (for Grade 9 students - single club)
            """
            CREATE TABLE IF NOT EXISTS club_allocation (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER NOT NULL,
                club_id INTEGER NOT NULL,
                FOREIGN KEY (student_id) REFERENCES users(id),
                FOREIGN KEY (club_id) REFERENCES clubs(id),
                UNIQUE(student_id)
            )
            """,

            // Grade 11 student clubs table (many-to-many mapping)
            """
            CREATE TABLE IF NOT EXISTS grade11_student_clubs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER NOT NULL,
                club_id INTEGER NOT NULL,
                assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (student_id) REFERENCES users(id),
                FOREIGN KEY (club_id) REFERENCES clubs(id),
                UNIQUE(student_id, club_id)
            )
            """,

            // Password reset tokens table
            """
            CREATE TABLE IF NOT EXISTS password_reset_tokens (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                token TEXT UNIQUE NOT NULL,
                user_id INTEGER NOT NULL,
                email TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                expires_at TIMESTAMP NOT NULL,
                used BOOLEAN DEFAULT 0,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
            """,

            // User sessions table (for tracking logins)
            """
            CREATE TABLE IF NOT EXISTS user_sessions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                logout_time TIMESTAMP,
                ip_address TEXT,
                user_agent TEXT,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
            """
        };

        try (Statement stmt = connection.createStatement()) {
            for (String query : createTableQueries) {
                stmt.execute(query);
            }
        }
    }

    private static void migrateDatabase() throws SQLException {
        // Check if new columns exist, if not add them
        try (Statement stmt = connection.createStatement()) {
            // Check if email column exists
            try {
                stmt.executeQuery("SELECT email FROM users LIMIT 1");
            } catch (SQLException e) {
                // Column doesn't exist, add new columns
                stmt.execute("ALTER TABLE users ADD COLUMN email TEXT UNIQUE");
                stmt.execute("ALTER TABLE users ADD COLUMN full_name TEXT");
                stmt.execute("ALTER TABLE users ADD COLUMN security_question TEXT");
                stmt.execute("ALTER TABLE users ADD COLUMN security_answer TEXT");
                stmt.execute("ALTER TABLE users ADD COLUMN password_salt TEXT");
                stmt.execute("ALTER TABLE users ADD COLUMN assigned_club_id INTEGER");
                stmt.execute("ALTER TABLE users ADD COLUMN first_login_completed BOOLEAN DEFAULT 0");
                stmt.execute("ALTER TABLE users ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
                stmt.execute("ALTER TABLE users ADD COLUMN last_login TIMESTAMP");
                stmt.execute("ALTER TABLE users ADD COLUMN is_active BOOLEAN DEFAULT 1");

                // Update existing users with default values
                updateExistingUsersWithDefaults();
            }

            // Check if password_salt column exists separately (for partial migrations)
            try {
                stmt.executeQuery("SELECT password_salt FROM users LIMIT 1");
            } catch (SQLException e) {
                stmt.execute("ALTER TABLE users ADD COLUMN password_salt TEXT");
                stmt.execute("ALTER TABLE users ADD COLUMN assigned_club_id INTEGER");
                stmt.execute("ALTER TABLE users ADD COLUMN first_login_completed BOOLEAN DEFAULT 0");
                stmt.execute("ALTER TABLE users ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
                stmt.execute("ALTER TABLE users ADD COLUMN last_login TIMESTAMP");
                stmt.execute("ALTER TABLE users ADD COLUMN is_active BOOLEAN DEFAULT 1");
            }
        }
    }

    private static void updateExistingUsersWithDefaults() throws SQLException {
        String updateQuery = """
            UPDATE users
            SET email = username || '@example.com',
                full_name = CASE
                    WHEN role = 'CLUB_MANAGER' THEN 'Club Manager'
                    WHEN role = 'GRADE_11' THEN 'Grade 11 Student'
                    WHEN role = 'GRADE_9' THEN 'Grade 9 Student'
                    ELSE username
                END,
                security_question = 'What is your favorite color?',
                security_answer = 'blue'
            WHERE email IS NULL
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(updateQuery);
        }
    }

    private static void insertDefaultData() throws SQLException {
        insertDefaultClubs();
        insertDefaultUsers();
    }

    private static void insertDefaultClubs() throws SQLException {
        String checkClubsQuery = "SELECT COUNT(*) FROM clubs";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(checkClubsQuery)) {

            if (rs.next() && rs.getInt(1) == 0) {
                String[] clubs = {
                    "Science",
                    "Humanities",
                    "Social Science",
                    "Math",
                    "Art",
                    "Mind Matters"
                };

                String insertClubQuery = "INSERT INTO clubs (name) VALUES (?)";
                try (PreparedStatement pstmt = connection.prepareStatement(insertClubQuery)) {
                    for (String clubName : clubs) {
                        pstmt.setString(1, clubName);
                        pstmt.executeUpdate();
                    }
                }
            }
        }
    }

    private static void insertDefaultUsers() throws SQLException {
        String checkUsersQuery = "SELECT COUNT(*) FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(checkUsersQuery)) {

            if (rs.next() && rs.getInt(1) == 0) {
                String insertUserQuery = "INSERT INTO users (username, password, password_salt, email, full_name, security_question, security_answer, role, first_login_completed, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(insertUserQuery)) {
                    // For demo purposes, use simple passwords. In production, all should be hashed
                    // Club Manager
                    pstmt.setString(1, "manager");
                    pstmt.setString(2, "manager123"); // Will be migrated to hashed in production
                    pstmt.setString(3, null); // Will be set when password is hashed
                    pstmt.setString(4, "manager@school.edu");
                    pstmt.setString(5, "Club Manager");
                    pstmt.setString(6, "What is your favorite color?");
                    pstmt.setString(7, "blue");
                    pstmt.setString(8, "CLUB_MANAGER");
                    pstmt.setBoolean(9, true); // Manager has completed first login
                    pstmt.setBoolean(10, true);
                    pstmt.executeUpdate();

                    // Grade 11 students
                    for (int i = 1; i <= 5; i++) {
                        pstmt.setString(1, "grade11_" + i);
                        pstmt.setString(2, "pass123");
                        pstmt.setString(3, null);
                        pstmt.setString(4, "grade11_" + i + "@school.edu");
                        pstmt.setString(5, "Grade 11 Student " + i);
                        pstmt.setString(6, "What is your favorite color?");
                        pstmt.setString(7, "blue");
                        pstmt.setString(8, "GRADE_11");
                        pstmt.setBoolean(9, true); // 11th graders have completed first login
                        pstmt.setBoolean(10, true);
                        pstmt.executeUpdate();
                    }

                    // Grade 9 students (first login not completed)
                    for (int i = 1; i <= 12; i++) {
                        pstmt.setString(1, "grade9_" + i);
                        pstmt.setString(2, "pass123");
                        pstmt.setString(3, null);
                        pstmt.setString(4, "grade9_" + i + "@school.edu");
                        pstmt.setString(5, "Grade 9 Student " + i);
                        pstmt.setString(6, "What is your favorite color?");
                        pstmt.setString(7, "blue");
                        pstmt.setString(8, "GRADE_9");
                        pstmt.setBoolean(9, false); // 9th graders need to complete first login
                        pstmt.setBoolean(10, true);
                        pstmt.executeUpdate();
                    }
                }
            }
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}