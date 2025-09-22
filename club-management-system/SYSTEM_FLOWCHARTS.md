# Club Management System - Complete Flowcharts

## 📊 System Overview Flowchart

```mermaid
flowchart TD
    A[Application Start] --> B[Initialize Database]
    B --> C[Show Login Screen]
    C --> D{Authentication}
    D -->|Success| E{Check User Role}
    D -->|Failure| F[Show Error Message]
    F --> C

    E -->|CLUB_MANAGER| G[Manager Dashboard]
    E -->|GRADE_11| H[Grade 11 Dashboard]
    E -->|GRADE_9| I[Grade 9 Dashboard]

    G --> G1[Proposal Management]
    G --> G2[Attendance Reports]
    G --> G3[Club Assignments View]
    G --> G4[Profile/Settings]

    H --> H1[Self Attendance]
    H --> H2[Upload Proposals]
    H --> H3[Check Proposal Status]
    H --> H4[View Grade 9 Students]
    H --> H5[Profile/Settings]

    I --> I1[Club Selection]
    I --> I2[Self Attendance]
    I --> I3[Profile/Settings]

    G4 --> J[Logout]
    H5 --> J
    I3 --> J
    J --> C
```

---

## 🔐 Authentication & Login Process

```mermaid
flowchart TD
    A[Login Screen] --> B[Enter Username/Password]
    B --> C[Click Login]
    C --> D[AuthenticationService.login()]
    D --> E[Query Database]
    E --> F{User Exists?}

    F -->|No| G[Show "Invalid Credentials"]
    G --> A

    F -->|Yes| H{Password Valid?}
    H -->|No| G
    H -->|Yes| I[Create User Session]
    I --> J[Update Last Login]
    J --> K{Check Role}

    K -->|CLUB_MANAGER| L[Load Manager Dashboard]
    K -->|GRADE_11| M[Load Grade 11 Dashboard]
    K -->|GRADE_9| N{First Login?}

    N -->|Yes| O[Show Setup Wizard]
    O --> P[Load Grade 9 Dashboard]
    N -->|No| P

    L --> Q[Show Main Interface]
    M --> Q
    P --> Q
```

---

## 👔 Manager Dashboard Flowcharts

### Manager Main Navigation
```mermaid
flowchart TD
    A[Manager Login] --> B[Load Proposal Management]
    B --> C[Manager Dashboard Ready]

    C --> D{Navigation Choice}
    D -->|Dashboard| B
    D -->|Proposals| E[Proposal Management]
    D -->|Attendance Reports| F[Attendance Reports]
    D -->|Club Assignments| G[Club Assignments View]
    D -->|Profile| H[Profile Management]
    D -->|Password| I[Change Password]
    D -->|Help| J[User Guide]
    D -->|Logout| K[Logout Process]

    E --> L[Manage Proposals]
    F --> M[View/Export Attendance]
    G --> N[View/Export Assignments]
    H --> O[Update Profile]
    I --> P[Password Change]
    J --> Q[Help Documentation]
    K --> R[Return to Login]
```

### Proposal Management Process
```mermaid
flowchart TD
    A[Proposal Management] --> B[Load All Proposals]
    B --> C[Display Proposals Table]
    C --> D{Manager Action}

    D -->|View Proposal| E[Open PDF File]
    D -->|Approve| F[Update Status to ACCEPTED]
    D -->|Reject| G[Update Status to REJECTED]
    D -->|Refresh| H[Reload Proposals]

    F --> I[Save to Database]
    G --> I
    I --> J[Update Display]
    J --> C

    H --> B
    E --> K[Display PDF Viewer]
    K --> C
```

### Attendance Reports Process
```mermaid
flowchart TD
    A[Attendance Reports] --> B[Load Attendance Data]
    B --> C[Load Club Names]
    C --> D[Load User Names]
    D --> E[Display Attendance Table]

    E --> F{Manager Action}
    F -->|Filter by Club| G[Apply Club Filter]
    F -->|Filter by Date| H[Apply Date Filter]
    F -->|Filter by Status| I[Apply Status Filter]
    F -->|Search| J[Apply Search Filter]
    F -->|Export Excel| K[Export to CSV/Excel]
    F -->|Refresh| L[Reload Data]

    G --> M[Update Table Display]
    H --> M
    I --> M
    J --> M
    M --> E

    K --> N[Save File Dialog]
    N --> O[Generate Export File]
    O --> P[Show Success Message]
    P --> E

    L --> B
```

### Club Assignments View Process
```mermaid
flowchart TD
    A[Club Assignments View] --> B[Load All Students]
    B --> C[Load Club Data]
    C --> D[Load Assignment Data]
    D --> E[Display Assignments Table]

    E --> F{Manager Action}
    F -->|Filter by Grade| G[Apply Grade Filter]
    F -->|Filter by Club| H[Apply Club Filter]
    F -->|Search Student| I[Apply Search Filter]
    F -->|Export Excel| J[Export to CSV/Excel]
    F -->|Refresh| K[Reload Data]

    G --> L[Update Table Display]
    H --> L
    I --> L
    L --> E

    J --> M[Save File Dialog]
    M --> N[Generate Export File]
    N --> O[Show Success Message]
    O --> E

    K --> B
```

---

## 🎓 Grade 11 Student Dashboard Flowcharts

### Grade 11 Main Navigation
```mermaid
flowchart TD
    A[Grade 11 Login] --> B[Load Enhanced Dashboard]
    B --> C[Show Self Attendance Panel]
    C --> D[Grade 11 Dashboard Ready]

    D --> E{Navigation Choice}
    E -->|Mark Attendance| F[Self Attendance Panel]
    E -->|Upload Proposal| G[Proposal Upload Panel]
    E -->|Check Proposal Status| H[Proposal Status Panel]
    E -->|View Grade 9| I[Grade 9 Students View]
    E -->|Profile| J[Profile Management]
    E -->|Password| K[Change Password]
    E -->|Help| L[User Guide]
    E -->|Logout| M[Logout Process]

    F --> N[Mark Daily Attendance]
    G --> O[Upload PDF Proposal]
    H --> P[View Proposal Status]
    I --> Q[View Grade 9 Data]
    J --> R[Update Profile]
    K --> S[Password Change]
    L --> T[Help Documentation]
    M --> U[Return to Login]
```

### Self Attendance Process
```mermaid
flowchart TD
    A[Self Attendance Panel] --> B[Load Student Info]
    B --> C[Load Club Assignment]
    C --> D[Check Today's Attendance]
    D --> E{Already Marked?}

    E -->|Yes| F[Show Attendance Status]
    F --> G[Disable Buttons]
    G --> H[Display Message]

    E -->|No| I[Show Attendance Options]
    I --> J{Student Action}
    J -->|Mark Present| K[Create Present Record]
    J -->|Mark Absent| L[Create Absent Record]

    K --> M[Save to Database]
    L --> M
    M --> N{Save Successful?}

    N -->|Yes| O[Show Success Message]
    O --> P[Update Display]
    P --> G

    N -->|No| Q[Show Error Message]
    Q --> I
```

### Proposal Upload Process
```mermaid
flowchart TD
    A[Upload Proposal Panel] --> B[Load Existing Proposals]
    B --> C[Display Proposal History]
    C --> D{Student Action}

    D -->|Browse File| E[Open File Chooser]
    D -->|Submit Proposal| F{File Selected?}

    E --> G[Select PDF File]
    G --> H[Set File Path]
    H --> C

    F -->|No| I[Show Warning Message]
    I --> C

    F -->|Yes| J[Validate File]
    J --> K{File Valid?}

    K -->|No| L[Show Error Message]
    L --> C

    K -->|Yes| M[Create Proposal Record]
    M --> N[Save to Database]
    N --> O{Save Successful?}

    O -->|Yes| P[Show Success Message]
    P --> Q[Refresh Proposal List]
    Q --> B

    O -->|No| R[Show Error Message]
    R --> C
```

### Proposal Status Check Process
```mermaid
flowchart TD
    A[Proposal Status Panel] --> B[Load Student Proposals]
    B --> C[Display Status Table]
    C --> D{Student Action}

    D -->|Refresh Status| E[Reload Proposals]
    D -->|View Details| F[Show Proposal Details]

    E --> B
    F --> G[Display Proposal Info]
    G --> H[Show Status History]
    H --> C

    C --> I{Proposal Status}
    I -->|PENDING| J[Show Pending Message]
    I -->|ACCEPTED| K[Show Accepted Message]
    I -->|REJECTED| L[Show Rejected Message]

    J --> C
    K --> C
    L --> C
```

### Grade 9 Students View Process
```mermaid
flowchart TD
    A[Grade 9 Students View] --> B[Load Grade 9 Students]
    B --> C[Load Club Data]
    C --> D[Load Assignment Data]
    D --> E[Display Students Table]

    E --> F{Student Action}
    F -->|Search Students| G[Apply Search Filter]
    F -->|Filter by Club| H[Apply Club Filter]
    F -->|View Unassigned| I[Filter Unassigned]
    F -->|Refresh Data| J[Reload All Data]

    G --> K[Update Table Display]
    H --> K
    I --> K
    K --> E

    J --> B
```

---

## 🎒 Grade 9 Student Dashboard Flowcharts

### Grade 9 Main Navigation
```mermaid
flowchart TD
    A[Grade 9 Login] --> B{First Login?}

    B -->|Yes| C[Show Setup Wizard]
    C --> D[Complete Profile Setup]
    D --> E[Load Simple Dashboard]

    B -->|No| E
    E --> F[Grade 9 Dashboard Ready]

    F --> G{Navigation Choice}
    G -->|Home| H[Main Dashboard]
    G -->|My Club| I[Club Assignment View]
    G -->|Mark Attendance| J[Self Attendance]
    G -->|Profile| K[Profile Management]
    G -->|Password| L[Change Password]
    G -->|Help| M[User Guide]
    G -->|Logout| N[Logout Process]

    H --> O[Show Dashboard Info]
    I --> P[View Club Assignment]
    J --> Q[Mark Daily Attendance]
    K --> R[Update Profile]
    L --> S[Password Change]
    M --> T[Help Documentation]
    N --> U[Return to Login]
```

### Club Selection Process
```mermaid
flowchart TD
    A[Club Selection] --> B[Load Available Clubs]
    B --> C[Display Club Options]
    C --> D{Student Action}

    D -->|Select Club| E[Choose Club]
    D -->|View Club Info| F[Show Club Details]

    E --> G{Confirm Selection?}
    G -->|No| C
    G -->|Yes| H[Create Club Assignment]
    H --> I[Save to Database]
    I --> J{Save Successful?}

    J -->|Yes| K[Show Success Message]
    K --> L[Update Assignment Display]
    L --> M[Navigate to Dashboard]

    J -->|No| N[Show Error Message]
    N --> C

    F --> O[Display Club Information]
    O --> C
```

### Grade 9 Self Attendance Process
```mermaid
flowchart TD
    A[Grade 9 Self Attendance] --> B[Check Club Assignment]
    B --> C{Has Club Assignment?}

    C -->|No| D[Show Assignment Required]
    D --> E[Navigate to Club Selection]

    C -->|Yes| F[Load Club Info]
    F --> G[Check Today's Attendance]
    G --> H{Already Marked?}

    H -->|Yes| I[Show Attendance Status]
    I --> J[Disable Attendance Options]

    H -->|No| K[Show Attendance Options]
    K --> L{Student Action}
    L -->|Mark Present| M[Create Present Record]
    L -->|Mark Absent| N[Create Absent Record]

    M --> O[Save to Database]
    N --> O
    O --> P{Save Successful?}

    P -->|Yes| Q[Show Success Message]
    Q --> R[Update Display]
    R --> J

    P -->|No| S[Show Error Message]
    S --> K
```

---

## 🗄️ Database Interaction Flowcharts

### User Authentication Database Flow
```mermaid
flowchart TD
    A[Login Request] --> B[UserDAO.authenticate()]
    B --> C[Query users Table]
    C --> D[SELECT * FROM users WHERE username = ?]
    D --> E{User Found?}

    E -->|No| F[Return null]
    F --> G[Authentication Failed]

    E -->|Yes| H[Check Password]
    H --> I{Password Valid?}

    I -->|No| F
    I -->|Yes| J[Update last_login]
    J --> K[Create User Session]
    K --> L[Insert into user_sessions]
    L --> M[Return User Object]
    M --> N[Authentication Success]
```

### Proposal Management Database Flow
```mermaid
flowchart TD
    A[Proposal Action] --> B{Action Type}

    B -->|Submit| C[ProposalDAO.insertProposal()]
    B -->|View All| D[ProposalDAO.getAllProposals()]
    B -->|View by Student| E[ProposalDAO.getProposalsByStudentId()]
    B -->|Update Status| F[ProposalDAO.updateProposalStatus()]

    C --> G[INSERT INTO proposals]
    D --> H[SELECT * FROM proposals]
    E --> I[SELECT * FROM proposals WHERE student_id = ?]
    F --> J[UPDATE proposals SET status = ?]

    G --> K[Return Success/Failure]
    H --> L[Return Proposal List]
    I --> L
    J --> K
```

### Attendance Management Database Flow
```mermaid
flowchart TD
    A[Attendance Action] --> B{Action Type}

    B -->|Mark Attendance| C[AttendanceDAO.markAttendance()]
    B -->|Get All Attendance| D[AttendanceDAO.getAllAttendance()]
    B -->|Get by Student| E[AttendanceDAO.getAttendanceByStudent()]
    B -->|Get by Date| F[AttendanceDAO.getAttendanceByClubAndDate()]

    C --> G[INSERT OR REPLACE INTO attendance]
    D --> H[SELECT * FROM attendance WITH JOINS]
    E --> I[SELECT * FROM attendance WHERE student_id = ?]
    F --> J[SELECT * FROM attendance WHERE club_id = ? AND session_date = ?]

    G --> K[Return Success/Failure]
    H --> L[Return Attendance Records]
    I --> L
    J --> L
```

### Club Assignment Database Flow
```mermaid
flowchart TD
    A[Club Assignment Action] --> B{Action Type}

    B -->|Allocate Student| C[ClubAllocationDAO.allocateStudentToClub()]
    B -->|Get All Allocations| D[ClubAllocationDAO.getAllAllocations()]
    B -->|Get by Student| E[ClubAllocationDAO.getAllocationByStudentId()]
    B -->|Get by Club| F[ClubAllocationDAO.getAllocationsByClubId()]

    C --> G[INSERT OR REPLACE INTO club_allocation]
    D --> H[SELECT * FROM club_allocation]
    E --> I[SELECT * FROM club_allocation WHERE student_id = ?]
    F --> J[SELECT * FROM club_allocation WHERE club_id = ?]

    G --> K[Return Success/Failure]
    H --> L[Return Allocation Records]
    I --> M[Return Single Allocation]
    J --> L
```

---

## 🔄 System State Transitions

### Application State Flow
```mermaid
stateDiagram-v2
    [*] --> ApplicationStart
    ApplicationStart --> DatabaseInit
    DatabaseInit --> LoginScreen
    LoginScreen --> Authentication
    Authentication --> LoginScreen : Failed
    Authentication --> ManagerDashboard : Manager Role
    Authentication --> Grade11Dashboard : Grade 11 Role
    Authentication --> Grade9Dashboard : Grade 9 Role

    ManagerDashboard --> ProposalMgmt
    ManagerDashboard --> AttendanceReports
    ManagerDashboard --> ClubAssignments
    ManagerDashboard --> ProfileMgmt

    Grade11Dashboard --> SelfAttendance
    Grade11Dashboard --> ProposalUpload
    Grade11Dashboard --> ProposalStatus
    Grade11Dashboard --> Grade9View
    Grade11Dashboard --> ProfileMgmt

    Grade9Dashboard --> ClubSelection
    Grade9Dashboard --> SelfAttendance
    Grade9Dashboard --> ProfileMgmt

    ProposalMgmt --> ManagerDashboard
    AttendanceReports --> ManagerDashboard
    ClubAssignments --> ManagerDashboard
    SelfAttendance --> Grade11Dashboard
    SelfAttendance --> Grade9Dashboard
    ProposalUpload --> Grade11Dashboard
    ProposalStatus --> Grade11Dashboard
    Grade9View --> Grade11Dashboard
    ClubSelection --> Grade9Dashboard
    ProfileMgmt --> LoginScreen : Logout
```

---

## 📋 Summary of System Processes

### Key System Features:
1. **Role-Based Access Control** - Different interfaces for Manager, Grade 11, and Grade 9 users
2. **Attendance Management** - Self-marking system for students with manager reporting
3. **Proposal System** - PDF upload and approval workflow
4. **Club Assignment** - Student self-selection with manager oversight
5. **Data Export** - Excel/CSV export capabilities for managers
6. **Real-time Updates** - Dynamic data loading and refresh capabilities

### Database Tables Used:
- **users** - User authentication and profile data
- **proposals** - Proposal submissions and status
- **attendance** - Attendance records
- **clubs** - Club information
- **club_allocation** - Student-club assignments
- **user_sessions** - Login session tracking
- **password_reset_tokens** - Password reset functionality

### Security Features:
- Password hashing with salt
- Role-based access control
- Session management
- Input validation
- File type validation for uploads