================================================================
       CLUB MANAGEMENT SYSTEM - README
       IBDP Computer Science Internal Assessment
================================================================

----------------------------------------------------------------
SYSTEM REQUIREMENTS
----------------------------------------------------------------
- Java JDK 11 or higher
- Windows 10/11 (or any OS with Java installed)
- Minimum 512MB RAM
- Screen resolution: 1024x768 or higher
- No internet connection required (fully offline)

----------------------------------------------------------------
HOW TO RUN THE PROGRAM
----------------------------------------------------------------
OPTION 1 - Windows (Recommended):
  1. Open the club-management-system folder
  2. Double-click compile.bat  (compiles the source code)
  3. Double-click run.bat      (launches the application)

OPTION 2 - Command Line:
  1. Open a terminal in the club-management-system folder
  2. Run: compile.bat
  3. Run: run.bat

NOTE: The database (club_management.db) is created automatically
on first run. Do not delete this file while the program is running.

----------------------------------------------------------------
LOGIN CREDENTIALS
----------------------------------------------------------------
CLUB MANAGER:
  Username : manager
  Password : manager123

GRADE 11 STUDENTS (5 accounts):
  Username : grade11_1  to  grade11_5
  Password : pass123
  Example  : username = grade11_1, password = pass123

GRADE 9 STUDENTS (12 accounts):
  Username : grade9_1  to  grade9_12
  Password : pass123
  Example  : username = grade9_1, password = pass123

----------------------------------------------------------------
WHAT EACH USER CAN DO
----------------------------------------------------------------
CLUB MANAGER:
  - Review and approve/reject Grade 11 proposals
  - Allocate Grade 9 students across 6 clubs
  - View attendance reports for all clubs

GRADE 11 STUDENTS:
  - Submit a club proposal (PDF)
  - View their assigned Grade 9 club members
  - Mark and track attendance

GRADE 9 STUDENTS:
  - Check proposal approval status
  - Mark attendance for club sessions
  - View their club assignment and information

----------------------------------------------------------------
FILE STRUCTURE
----------------------------------------------------------------
club-management-system/
  src/                        - All Java source code
  lib/                        - Required library files (JARs)
  build/                      - Compiled class files (auto-generated)
  club_management.db          - SQLite database file
  compile.bat                 - Compiles the program (Windows)
  run.bat                     - Runs the program (Windows)
  README.txt                  - This file

----------------------------------------------------------------
LIBRARIES USED
----------------------------------------------------------------
- sqlite-jdbc-3.45.0.0.jar   : SQLite database driver
- slf4j-api-1.7.36.jar       : Logging interface
- slf4j-simple-1.7.36.jar    : Logging implementation

These are located in the lib/ folder and are required to run
the program. Do not remove them.

----------------------------------------------------------------
TROUBLESHOOTING
----------------------------------------------------------------
Problem : "java is not recognized" error
Fix     : Install Java JDK 11+ and add it to your system PATH
          Download from: https://www.oracle.com/java/technologies/downloads/

Problem : Application does not start after compile.bat
Fix     : Ensure all files in the lib/ folder are present
          Re-run compile.bat and check for any error messages

Problem : Database error on startup
Fix     : Delete club_management.db and restart the program.
          A fresh database will be created automatically.

----------------------------------------------------------------
