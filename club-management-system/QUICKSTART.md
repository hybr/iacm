# Quick Start Guide

## The Application is Ready to Run!

The Club Management System has been successfully compiled and is ready to use.

### Files Status:
✅ SQLite JDBC Driver: Downloaded and placed in `lib/`
✅ Source Code: Compiled successfully to `build/`
✅ Database: Will be created automatically on first run

## How to Run (Windows PowerShell):

1. **Navigate to the project directory:**
   ```powershell
   cd club-management-system
   ```

2. **Run the application:**
   ```powershell
   java -cp "build;lib\sqlite-jdbc-3.45.0.0.jar" com.clubmanagement.ClubManagementApp
   ```

   Or use one of the batch files:
   ```powershell
   .\start.bat
   ```
   or
   ```powershell
   .\run.bat
   ```

## How to Run (Linux/Mac Terminal):

1. **Navigate to the project directory:**
   ```bash
   cd club-management-system
   ```

2. **Run the application:**
   ```bash
   java -cp "build:lib/sqlite-jdbc-3.45.0.0.jar" com.clubmanagement.ClubManagementApp
   ```

   Or use the shell script:
   ```bash
   ./launch.sh
   ```

## Default Login Credentials:

### Club Manager
- Username: `manager`
- Password: `manager123`

### 11th Graders
- Username: `grade11_1`, `grade11_2`, `grade11_3`, `grade11_4`, `grade11_5`
- Password: `pass123`

### 9th Graders
- Username: `grade9_1`, `grade9_2`, ..., `grade9_12`
- Password: `pass123`

## What to Expect:

1. **Login Screen**: Choose your credentials based on your role
2. **Dashboard**: Role-specific menu options will appear
3. **Database**: SQLite database (`club_management.db`) will be created automatically
4. **Features**: All functionality (proposals, attendance, club allocation) is ready to use

## Troubleshooting:

If you encounter any issues:

1. **"Could not find main class"**: Make sure you're in the `club-management-system` directory
2. **Java errors**: Ensure Java 8+ is installed
3. **Database errors**: The application will create the database automatically

## Next Steps:

- Try logging in with different user roles
- Test the proposal submission feature (11th graders)
- Mark attendance (9th graders)
- Manage proposals and allocations (Club Manager)

The application is fully functional and ready for use!