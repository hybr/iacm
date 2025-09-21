# Club Management System

A Java desktop application for managing club activities, proposals, attendance, and member allocation.

## Features

### Role-based Authentication
- **Club Manager**: Manages proposals, allocations, and attendance reports
- **11th Graders**: Submit proposals and view assigned club members
- **9th Graders**: Check proposal status, mark attendance, and view club information

### Core Functionality
1. **Proposal Management**
   - 11th graders can submit PDF proposals
   - Club Manager can accept/reject proposals
   - 9th graders can check status of proposals

2. **Attendance Tracking**
   - Digital attendance marking for 5 sessions
   - Minimum requirement: 3 out of 5 sessions
   - Automatic highlighting of students with poor attendance

3. **Club Allocation**
   - Automatic equal distribution of 9th graders across 6 clubs
   - Club Manager can view all allocations
   - 11th graders can see their assigned club members

## Technology Stack

- **Java Swing** for GUI
- **SQLite** for database storage
- **JDBC** for database connectivity

## Project Structure

```
club-management-system/
├── src/main/java/com/clubmanagement/
│   ├── models/          # Data models (User, Proposal, etc.)
│   ├── database/        # Database connection manager
│   ├── dao/            # Data Access Objects
│   ├── services/       # Business logic layer
│   ├── gui/            # Swing GUI components
│   └── ClubManagementApp.java  # Main application entry point
├── lib/                # External libraries (SQLite JDBC)
└── README.md
```

## Default Login Credentials

### Club Manager
- Username: `manager`
- Password: `manager123`

### 11th Graders
- Username: `grade11_1` to `grade11_5`
- Password: `pass123`

### 9th Graders
- Username: `grade9_1` to `grade9_12`
- Password: `pass123`

## Setup Instructions

### Prerequisites
1. Java JDK 11 or higher
2. SQLite JDBC driver (sqlite-jdbc-3.x.x.jar)

### Compilation and Running

1. **Download SQLite JDBC Driver**
   ```bash
   # Download from https://github.com/xerial/sqlite-jdbc/releases
   # Place sqlite-jdbc-3.x.x.jar in the lib/ directory
   ```

2. **Compile the Project**
   ```bash
   cd club-management-system
   javac -cp "lib/*" -d build src/main/java/com/clubmanagement/*.java src/main/java/com/clubmanagement/*/*.java
   ```

3. **Run the Application**
   ```bash
   java -cp "build:lib/*" com.clubmanagement.ClubManagementApp
   ```

### Alternative: Using an IDE
1. Open the project in your preferred Java IDE (IntelliJ IDEA, Eclipse, etc.)
2. Add the SQLite JDBC JAR to your project's classpath
3. Run the `ClubManagementApp.main()` method

## Database Schema

The application automatically creates the following SQLite tables:

- **users**: User authentication and role management
- **proposals**: Proposal submissions and status tracking
- **attendance**: Session attendance records
- **clubs**: Club information (6 default clubs)
- **club_allocation**: Student-to-club assignments

## Usage Guide

### For Club Managers
1. Login with manager credentials
2. Use "Proposal Management" to review and approve/reject proposals
3. Use "Club Allocation" to distribute students across clubs
4. Use "Attendance Report" to monitor student attendance

### For 11th Graders
1. Login with grade11 credentials
2. Use "Submit Proposal" to upload PDF proposals
3. Use "My Club Members" to view assigned 9th-grade students

### For 9th Graders
1. Login with grade9 credentials
2. Use "Check Proposal Status" to see proposal approval status
3. Use "Mark Attendance" to record session attendance
4. Use "My Club Info" to view club assignment details

## Key Features

### Attendance Requirements
- Students must attend at least 3 out of 5 sessions
- System automatically flags students with poor attendance
- Real-time attendance tracking and reporting

### Club Allocation
- Equal distribution algorithm ensures balanced club membership
- 6 default clubs: Science, Literature, Sports, Art, Music, Technology
- Automatic allocation with manual override capabilities

### Security
- Role-based access control
- Password-protected user accounts
- Session management with logout functionality

## Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Ensure SQLite JDBC driver is in the classpath
   - Check file permissions in the application directory

2. **GUI Display Issues**
   - Verify Java Swing is properly configured
   - Check system look-and-feel compatibility

3. **File Path Issues**
   - Use absolute paths for PDF file uploads
   - Ensure proper file permissions

## Future Enhancements

- Email notifications for proposal status updates
- Advanced reporting with charts and graphs
- Import/export functionality for student data
- Multi-language support
- Web-based interface option

## Contributing

This is a complete starter project for educational purposes. Feel free to extend and modify according to your requirements.