# Club Management System - Complete Technical Documentation

## 🛠️ **PROGRAMMING TOOLS & TECHNIQUES USED**

### **Core Technologies:**
- **Language**: Java (Object-Oriented Programming)
- **GUI Framework**: Java Swing with custom ModernTheme
- **Database**: SQLite with JDBC
- **Security**: SHA-256 password hashing with salt
- **Architecture**: MVC (Model-View-Controller) pattern

### **Libraries & Dependencies:**
- `sqlite-jdbc-3.45.0.0.jar` - SQLite database connectivity
- `slf4j-api-1.7.36.jar` - Simple Logging Facade for Java (API)
- `slf4j-simple-1.7.36.jar` - Simple logging implementation

### **Design Patterns Used:**
1. **MVC Pattern** - Separation of concerns (Models, Views, Controllers)
2. **DAO Pattern** - Data Access Objects for database operations
3. **Singleton Pattern** - DatabaseManager for connection management
4. **Observer Pattern** - ActionListeners for UI events
5. **Strategy Pattern** - Role-based feature access
6. **Factory Pattern** - ModernTheme component creation
7. **Callback Pattern** - Logout functionality callbacks

### **Security Techniques:**
- **Password Hashing**: SHA-256 with random salt generation
- **Secure Random**: SecureRandom for salt generation
- **Base64 Encoding**: For salt storage
- **Input Validation**: Form validation and sanitization
- **Role-Based Access Control**: Feature access based on user roles

### **UI/UX Techniques:**
- **Custom Theme System**: ModernTheme for consistent styling
- **CardLayout**: For panel switching in dashboards
- **Event-Driven Programming**: ActionListeners and callbacks
- **Responsive Design**: Flexible layouts with BorderLayout, GridBagLayout
- **User Feedback**: Status messages and confirmation dialogs

### **Database Techniques:**
- **Connection Pooling**: Single connection management
- **Prepared Statements**: SQL injection prevention
- **Database Migrations**: Schema versioning and updates
- **CRUD Operations**: Create, Read, Update, Delete via DAOs
- **Foreign Key Relationships**: Normalized database design

### **Programming Techniques:**
- **Exception Handling**: Try-catch blocks for error management
- **Encapsulation**: Private fields with getter/setter methods
- **Inheritance**: Panel inheritance for consistent UI structure
- **Polymorphism**: Interface-based programming
- **Composition**: Complex objects built from simpler components

# Club Management System - Feature Flowcharts

## 📋 **MANAGEMENT DASHBOARD FEATURES**

### 1. 🏠 Dashboard Feature
```
[Click Dashboard]
    ↓
[Load Initial Content]
    ↓
[Show Proposal Management Panel]
    ↓
[Display proposal table with status filtering]
    ↓
[Manager can approve/reject proposals]
    ↓
[Update status label: "Dashboard loaded"]
```

### 2. 📋 Proposals Feature
```
[Click Proposals]
    ↓
[Show Proposal Management Panel]
    ↓
[Load all pending/submitted proposals]
    ↓
[Display in table format with actions]
    ↓
┌─[Manager Actions]─┐
│ • Approve         │
│ • Reject         │
│ • View Details   │
└─────────────────┘
    ↓
[Update database with decision]
    ↓
[Refresh proposal list]
    ↓
[Status: "Proposal Management loaded"]
```

### 3. 📊 Attendance Reports Feature
```
[Click Attendance Reports]
    ↓
[Show Manager Attendance Report Panel]
    ↓
[Load all attendance data]
    ↓
┌─[Filter Options]─┐
│ • By Date Range  │
│ • By Student     │
│ • By Club        │
│ • Export Options │
└─────────────────┘
    ↓
[Display attendance statistics]
    ↓
[Generate reports & charts]
    ↓
[Export to PDF/CSV if requested]
    ↓
[Back to Dashboard callback available]
    ↓
[Status: "Attendance Reports loaded"]
```

### 4. 👥 Club Assignments Feature
```
[Click Club Assignments]
    ↓
[Show Manager Club Assignments View Panel]
    ↓
[Load all student-club assignments]
    ↓
┌─[View Options]─┐
│ • All Students │
│ • By Grade     │
│ • By Club      │
│ • Unassigned   │
└───────────────┘
    ↓
[Display assignment table]
    ↓
[Read-only view for managers]
    ↓
[Back to Dashboard callback]
    ↓
[Status: "Club Assignments loaded"]
```

## 🎓 **GRADE 11 DASHBOARD FEATURES**

### 1. 🏠 Home Feature
```
[Click Home]
    ↓
[Load Grade 11 Enhanced Dashboard]
    ↓
[Show Self-Attendance Panel (default)]
    ↓
[Display welcome message]
    ↓
[Show navigation buttons]
    ↓
[Status: "Dashboard loaded"]
```

### 2. 🎯 Allocation Feature
```
[Click Allocation]
    ↓
[Show Club Allocation Panel]
    ↓
[Load current allocations]
    ↓
[Display allocated clubs]
    ↓
[Show allocation status]
    ↓
[Status: "Club Allocation loaded"]
```

### 3. ✔️ Mark Attendance Feature
```
[Click Mark Attendance]
    ↓
[Show Grade 11 Self-Attendance Panel]
    ↓
[Check if already marked today]
    ↓
┌─[Attendance Options]─┐
│ • Present           │
│ • Absent            │
│ • Late              │
└───────────────────┘
    ↓
[Validate selection]
    ↓
[Save to database]
    ↓
[Show confirmation message]
    ↓
[Update attendance history]
    ↓
[Status: "Self-Attendance Marking loaded"]
```

### 4. 📤 Upload Proposal Feature
```
[Click Upload Proposal]
    ↓
[Show Proposal Submission Panel]
    ↓
┌─[Required Fields]─┐
│ • Proposal Title │
│ • Description    │
│ • File Upload    │
│ • Category       │
└─────────────────┘
    ↓
[Validate all fields]
    ↓
[Check file format & size]
    ↓
[Upload file to server]
    ↓
[Save proposal to database]
    ↓
[Generate submission ID]
    ↓
[Show success confirmation]
    ↓
[Status: "Upload Proposal loaded"]
```

### 5. 📊 Proposal Status Feature
```
[Click Proposal Status]
    ↓
[Show Proposal Status Panel]
    ↓
[Load user's submitted proposals]
    ↓
┌─[Status Types]─┐
│ • Pending     │
│ • Approved    │
│ • Rejected    │
│ • Under Review│
└───────────────┘
    ↓
[Display in table format]
    ↓
[Show submission date & comments]
    ↓
[Allow viewing proposal details]
    ↓
[Status: "Proposal Status loaded"]
```

### 6. 👥 View Grade 9 Feature
```
[Click View Grade 9]
    ↓
[Show Grade 9 Students View Panel]
    ↓
[Load all Grade 9 students]
    ↓
[Display student information]
    ↓
┌─[Information Shown]─┐
│ • Student Name     │
│ • Assigned Club    │
│ • Attendance Rate  │
│ • Contact Info     │
└───────────────────┘
    ↓
[Filter & search options]
    ↓
[Export student list option]
    ↓
[Status: "Grade 9 Students View loaded"]
```

### 7. 📈 History Feature
```
[Click History]
    ↓
[Show Attendance Report Panel]
    ↓
[Load user's attendance history]
    ↓
┌─[History View]─┐
│ • Calendar View│
│ • List View    │
│ • Statistics   │
│ • Charts       │
└───────────────┘
    ↓
[Display attendance patterns]
    ↓
[Show attendance percentage]
    ↓
[Generate personal report]
    ↓
[Status: "Attendance History loaded"]
```

## 🎒 **GRADE 9 DASHBOARD FEATURES**

### 1. 🏠 Home Feature
```
[Click Home]
    ↓
[Show Grade 9 Simple Dashboard Panel]
    ↓
[Display welcome message]
    ↓
[Show simplified interface]
    ↓
[Focus on attendance functionality]
    ↓
[Status: "Dashboard loaded"]
```

### 2. ✔️ Attendance Feature
```
[Click Attendance]
    ↓
[Show Grade 9 Self-Attendance Panel]
    ↓
[Check today's attendance status]
    ↓
┌─[If Not Marked Today]─┐
│ • Show Present/Absent │
│ • Simple interface   │
│ • Large buttons      │
└─────────────────────┘
    ↓
┌─[If Already Marked]─┐
│ • Show current status│
│ • Display message    │
│ • Option to change   │
└─────────────────────┘
    ↓
[Validate attendance mark]
    ↓
[Save to database]
    ↓
[Show success message]
    ↓
[Update simple dashboard]
    ↓
[Status: "Attendance Marking loaded"]
```

## 🔧 **COMMON FEATURES (ALL ROLES)**

### 1. 👤 Profile Feature
```
[Click Profile]
    ↓
[Open My Profile Frame]
    ↓
[Load user information]
    ↓
┌─[Profile Information]─┐
│ • Full Name          │
│ • Username           │
│ • Email              │
│ • Role               │
│ • Club Assignment    │
│ • Join Date          │
└─────────────────────┘
    ↓
[Display in modal window]
    ↓
[Allow viewing only (read-only)]
    ↓
[Close profile window]
    ↓
[Status: "Profile information displayed"]
```

### 2. 🔑 Password Feature
```
[Click Password]
    ↓
[Open Change Password Frame]
    ↓
┌─[Password Fields]─┐
│ • Current Password│
│ • New Password    │
│ • Confirm New     │
└─────────────────┘
    ↓
[Validate current password]
    ↓
[Check new password strength]
    ↓
┌─[Password Requirements]─┐
│ • Min 8 characters     │
│ • Uppercase letter     │
│ • Lowercase letter     │
│ • Number               │
│ • Special character    │
└──────────────────────┘
    ↓
[Hash new password]
    ↓
[Update in database]
    ↓
[Show success message]
    ↓
[Close password window]
    ↓
[Status: "Change Password opened"]
```

### 3. ℹ️ Help Feature
```
[Click Help]
    ↓
[Show User Guide Dialog]
    ↓
┌─[Help Content]─┐
│ • Navigation  │
│ • Features    │
│ • Contact Info│
│ • FAQ         │
└───────────────┘
    ↓
[Display in message dialog]
    ↓
[User reads guide]
    ↓
[Close help dialog]
    ↓
[Status: "User guide opened"]
```

### 4. 🚪 Logout Feature
```
[Click Logout]
    ↓
[Show Confirmation Dialog]
    ↓
["Are you sure you want to logout?"]
    ↓
┌─[User Choice]─┐
│ • Yes → Logout│
│ • No → Cancel │
└───────────────┘
    ↓
[If Yes: Clear session]
    ↓
[Close main dashboard]
    ↓
[Open Login Frame]
    ↓
[If No: Return to dashboard]
```

## 🚫 **REMOVED FEATURES**

### Grade 9 - My Club Feature (REMOVED)
```
❌ Previously Available:
[Click My Club]
    ↓
[Show Grade 9 Club Assignments Panel]
    ↓
[Display assigned club information]
    ↓
[Show club activities and schedule]

✅ Current State:
• Button removed from Grade 9 navigation
• Case handler disabled in MainDashboard
• showGrade9ClubAssignments() method commented out
• Grade 9 students have simplified interface focused on attendance
```

## 📊 **FEATURE SUMMARY BY ROLE**

### Club Manager (6 features):
1. 🏠 Dashboard - Default proposal management view
2. 📋 Proposals - Review and approve/reject proposals
3. 📊 Attendance Reports - View comprehensive attendance data
4. 👥 Club Assignments - View all student-club assignments
5. 👤 Profile - View personal information
6. 🔑 Password - Change account password
7. ℹ️ Help - Access user guide
8. 🚪 Logout - Sign out with confirmation

### Grade 11 (8 features):
1. 🏠 Home - Enhanced dashboard with multiple options
2. 🎯 Allocation - View club allocation status
3. ✔️ Mark Attendance - Self-attendance marking
4. 📤 Upload Proposal - Submit new proposals
5. 📊 Proposal Status - Check submission status
6. 👥 View Grade 9 - See Grade 9 student information
7. 📈 History - View personal attendance history
8. 👤 Profile - View personal information
9. 🔑 Password - Change account password
10. ℹ️ Help - Access user guide
11. 🚪 Logout - Sign out with confirmation

### Grade 9 (5 features):
1. 🏠 Home - Simple dashboard focused on attendance
2. ✔️ Attendance - Mark daily attendance (Present/Absent)
3. 👤 Profile - View personal information
4. 🔑 Password - Change account password
5. ℹ️ Help - Access user guide
6. 🚪 Logout - Sign out with confirmation

**Note**: Grade 9 has the most simplified interface with focus on core attendance functionality. The "My Club" feature has been completely removed per user request to reduce complexity.

---

## 🔐 **AUTHENTICATION & SECURITY FLOWS**

### Login Process Flow
```
[User enters credentials]
    ↓
[AuthenticationService.login()]
    ↓
[UserDAO.getUserByUsername()]
    ↓
[Database Query: SELECT * FROM users WHERE username = ?]
    ↓
[PasswordHasher.verifyPassword()]
    ↓
┌─[Password Verification]─┐
│ • Hash input password  │
│ • Compare with stored  │
│ • SHA-256 + salt       │
└───────────────────────┘
    ↓
┌─[If Valid]─┐    ┌─[If Invalid]─┐
│ Set current│    │ Show error   │
│ user       │    │ message      │
│ Open main  │    │ Clear fields │
│ dashboard  │    │ Stay on login│
└───────────┘    └─────────────┘
```

### Sign-Up Process Flow
```
[User fills sign-up form]
    ↓
[Validate all input fields]
    ↓
┌─[Validation Checks]─┐
│ • Username unique   │
│ • Password strength │
│ • Email format      │
│ • Confirm password  │
│ • Security answer   │
└───────────────────┘
    ↓
[PasswordHasher.hashPassword()]
    ↓
┌─[Password Processing]─┐
│ • Generate salt      │
│ • Hash with SHA-256  │
│ • Encode salt Base64 │
└─────────────────────┘
    ↓
[Create User object]
    ↓
[UserDAO.insertUser()]
    ↓
[Database INSERT with prepared statement]
    ↓
[Success confirmation]
    ↓
[Redirect to Login]
```

### Forgot Password Flow
```
[User enters username]
    ↓
[UserDAO.getUserByUsername()]
    ↓
[Check if security question exists]
    ↓
[Display security question]
    ↓
[User enters security answer]
    ↓
[UserDAO.validateSecurityAnswer()]
    ↓
┌─[Case-insensitive comparison]─┐
│ • Trim whitespace           │
│ • Convert to lowercase      │
│ • Compare with stored       │
│ • NO password hashing       │
└───────────────────────────┘
    ↓
┌─[If Correct]─┐    ┌─[If Wrong]─┐
│ Show password │    │ Show error │
│ reset form    │    │ Clear field│
└──────────────┘    └───────────┘
    ↓
[User sets new password]
    ↓
[Validate password strength]
    ↓
[PasswordHasher.hashPassword()]
    ↓
[UserDAO.updatePasswordWithSalt()]
    ↓
[Success message]
    ↓
[Redirect to Login]
```

---

## 💾 **DATABASE INTERACTION FLOWS**

### Database Connection Flow
```
[Application starts]
    ↓
[DatabaseManager.getConnection()]
    ↓
[Check if connection exists and open]
    ↓
┌─[If No Connection]─┐
│ • Load JDBC driver │
│ • Create connection│
│ • Initialize DB    │
└───────────────────┘
    ↓
[initializeDatabase()]
    ↓
┌─[Database Setup]─┐
│ • createTables() │
│ • migrateDatabase()│
│ • insertDefaultData()│
└─────────────────┘
    ↓
[Return connection]
```

### DAO Pattern Flow (Example: UserDAO)
```
[Business Logic Layer]
    ↓
[UserDAO method call]
    ↓
[Get database connection]
    ↓
[Prepare SQL statement]
    ↓
┌─[SQL Operations]─┐
│ • INSERT        │
│ • SELECT        │
│ • UPDATE        │
│ • DELETE        │
└─────────────────┘
    ↓
[Execute prepared statement]
    ↓
[Process ResultSet (if SELECT)]
    ↓
[Map results to Model objects]
    ↓
[Handle exceptions]
    ↓
[Return results to business layer]
```

### CRUD Operations Flow
```
CREATE (INSERT):
[User Object] → [UserDAO.insertUser()] → [PreparedStatement] → [Database]

READ (SELECT):
[Search Criteria] → [UserDAO.getUserBy...()] → [Query Execution] → [User Object]

UPDATE:
[Modified Data] → [UserDAO.update...()] → [PreparedStatement] → [Database]

DELETE:
[ID/Criteria] → [UserDAO.delete...()] → [PreparedStatement] → [Database]
```

---

## 🎨 **UI COMPONENT ARCHITECTURE FLOWS**

### ModernTheme Factory Pattern
```
[Component Request]
    ↓
[ModernTheme.createComponent()]
    ↓
┌─[Available Components]─┐
│ • createStyledTextField()│
│ • createPrimaryButton() │
│ • createCardPanel()     │
│ • createBodyLabel()     │
│ • createTitleLabel()    │
└────────────────────────┘
    ↓
[Apply consistent styling]
    ↓
┌─[Style Properties]─┐
│ • Colors          │
│ • Fonts           │
│ • Borders         │
│ • Padding         │
└──────────────────┘
    ↓
[Return styled component]
```

### CardLayout Navigation
```
[User clicks navigation button]
    ↓
[NavigationToolbar.notifyAction()]
    ↓
[MainDashboard.handleToolbarAction()]
    ↓
[Switch statement on command]
    ↓
┌─[Panel Management]─┐
│ • contentPanel.removeAll()│
│ • Create new panel       │
│ • Add to CardLayout      │
│ • cardLayout.show()      │
│ • revalidate/repaint     │
└─────────────────────────┘
    ↓
[Update status label]
    ↓
[Display new panel]
```

### Event Handling Flow
```
[User Action (click, type, etc.)]
    ↓
[Swing Event Dispatch Thread]
    ↓
[ActionListener.actionPerformed()]
    ↓
[Business logic execution]
    ↓
┌─[Possible Actions]─┐
│ • Database operation│
│ • UI state change  │
│ • Panel navigation │
│ • Validation       │
└───────────────────┘
    ↓
[Update UI components]
    ↓
[Provide user feedback]
```

---

## 📊 **ROLE-BASED ACCESS CONTROL FLOWS**

### Role Determination Flow
```
[User logs in]
    ↓
[AuthenticationService.getCurrentUser()]
    ↓
[Get user role from User object]
    ↓
┌─[Role Types]─┐
│ • CLUB_MANAGER│
│ • GRADE_11   │
│ • GRADE_9    │
└─────────────┘
    ↓
[NavigationToolbar.createButtons()]
    ↓
┌─[Role-Specific UI]─┐
│ • Manager: Full access   │
│ • Grade 11: Enhanced     │
│ • Grade 9: Simplified    │
└─────────────────────────┘
    ↓
[Load appropriate dashboard]
```

### Feature Access Control
```
[User attempts to access feature]
    ↓
[Check user role in AuthenticationService]
    ↓
┌─[Access Control Methods]─┐
│ • isClubManager()        │
│ • isGrade11()            │
│ • isGrade9()             │
└─────────────────────────┘
    ↓
┌─[If Authorized]─┐    ┌─[If Not Authorized]─┐
│ Load feature    │    │ Show error/ignore   │
│ Update UI       │    │ Redirect to home    │
└────────────────┘    └────────────────────┘
```

---

## 🔄 **ERROR HANDLING & VALIDATION FLOWS**

### Input Validation Flow
```
[User submits form]
    ↓
[Client-side validation]
    ↓
┌─[Validation Types]─┐
│ • Required fields │
│ • Format checks   │
│ • Length limits   │
│ • Pattern matching│
└──────────────────┘
    ↓
┌─[If Valid]─┐    ┌─[If Invalid]─┐
│ Continue   │    │ Show error    │
│ processing │    │ Highlight field│
└───────────┘    │ Focus field    │
    ↓            └───────────────┘
[Server-side validation]
    ↓
[Business rule validation]
    ↓
[Database constraints]
```

### Exception Handling Flow
```
[Method execution]
    ↓
[Try block]
    ↓
┌─[Possible Exceptions]─┐
│ • SQLException       │
│ • ClassNotFoundException│
│ • IllegalArgumentException│
│ • NullPointerException│
└─────────────────────┘
    ↓
[Catch specific exceptions]
    ↓
┌─[Error Response]─┐
│ • Log error     │
│ • User message  │
│ • Graceful fail │
│ • Cleanup       │
└────────────────┘
    ↓
[Finally block cleanup]
```