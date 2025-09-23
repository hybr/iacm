# Club Management System - UML Class Diagrams

## Relationship Legend:
- **▲** = Inheritance (extends/implements)
- **◊** = Aggregation (has-a relationship)
- **-** = Private attribute/method
- **+** = Public attribute/method
- **#** = Protected attribute/method

---

## 1. COMPLETE APPLICATION UML DIAGRAM

### Core Framework Classes

```
┌─────────────────────────────┐    ┌─────────────────────────────┐    ┌─────────────────────────────┐
│           JFrame            │    │         JPanel              │    │     AbstractCellEditor      │
├─────────────────────────────┤    ├─────────────────────────────┤    ├─────────────────────────────┤
│ - title: String             │    │ - visible: boolean          │    │ - listeners: String         │
│ - width: int                │    │ - enabled: boolean          │    │ - editing: boolean          │
│ - height: int               │    │ - background: String        │    ├─────────────────────────────┤
├─────────────────────────────┤    ├─────────────────────────────┤    │ + addListener(): void       │
│ + setTitle(): void          │    │ + setVisible(): void        │    │ + removeListener(): void    │
│ + setSize(): void           │    │ + setEnabled(): void        │    │ + fireEditingStopped(): void│
│ + dispose(): void           │    │ + add(): void               │    │ + getCellEditorValue(): String│
│ + pack(): void              │    │ + remove(): void            │    └─────────────────────────────┘
└─────────────────────────────┘    └─────────────────────────────┘                     ▲
           ▲                                  ▲                                         │ extends
           │ extends                          │ extends                                 │
┌─────────────────────────────┐    ┌─────────────────────────────┐    ┌─────────────────────────────┐
│        LoginFrame           │    │    AttendanceDashboard      │    │      ActionCellEditor       │
├─────────────────────────────┤    ├─────────────────────────────┤    ├─────────────────────────────┤
│ - username: String          │    │ - studentCount: int         │    │ - currentRow: int           │
│ - password: String          │    │ - presentCount: int         │    │ - editButton: String        │
│ - isVisible: boolean        │    │ - clubName: String          │    │ - panel: String             │
├─────────────────────────────┤    │ - dateSelected: String      │    ├─────────────────────────────┤
│ + validateLogin(): boolean  │    ├─────────────────────────────┤    │ + getComponent(): String    │
│ + clearFields(): void       │    │ + loadData(): void          │    │ + getCellValue(): String    │
│ + showMessage(): void       │    │ + refreshTable(): void      │    │ + stopEditing(): boolean    │
│ + dispose(): void           │    │ + exportData(): void        │    └─────────────────────────────┘
└─────────────────────────────┘    │ + calculateStats(): void    │
           ◊                       └─────────────────────────────┘
           │ has-a                             ◊
           │                                   │ has-a
┌─────────────────────────────┐               │
│   AuthenticationService     │←──────────────┘
├─────────────────────────────┤
│ - currentUser: String       │
│ - isLoggedIn: boolean       │
│ - sessionToken: String      │
├─────────────────────────────┤
│ + login(): boolean          │
│ + logout(): void            │
│ + getCurrentUser(): String  │
│ + validateSession(): boolean│
└─────────────────────────────┘
           ◊
           │ has-a
           │
┌─────────────────────────────┐
│          UserDAO            │
├─────────────────────────────┤
│ - connection: String        │
│ - tableName: String         │
│ - isConnected: boolean      │
├─────────────────────────────┤
│ + findByUsername(): String  │
│ + save(): boolean           │
│ + delete(): boolean         │
│ + authenticate(): boolean   │
└─────────────────────────────┘
           ◊
           │ has-a
           │
┌─────────────────────────────┐
│            User             │
├─────────────────────────────┤
│ - id: int                   │
│ - username: String          │
│ - password: String          │
│ - email: String             │
│ - role: String              │
│ - isActive: boolean         │
│ - clubId: int               │
├─────────────────────────────┤
│ + getId(): int              │
│ + setId(): void             │
│ + getUsername(): String     │
│ + setUsername(): void       │
│ + getRole(): String         │
│ + setRole(): void           │
│ + isActive(): boolean       │
└─────────────────────────────┘
```

### Model Classes

```
┌─────────────────────────────┐    ┌─────────────────────────────┐    ┌─────────────────────────────┐
│           Club              │    │        Attendance           │    │       ClubAllocation        │
├─────────────────────────────┤    ├─────────────────────────────┤    ├─────────────────────────────┤
│ - id: int                   │    │ - id: int                   │    │ - id: int                   │
│ - name: String              │    │ - studentId: int            │    │ - studentId: int            │
│ - capacity: int             │    │ - clubId: int               │    │ - clubId: int               │
│ - isActive: boolean         │    │ - isPresent: boolean        │    │ - isActive: boolean         │
├─────────────────────────────┤    │ - dateMarked: String        │    │ - assignedDate: String      │
│ + getId(): int              │    │ - notes: String             │    ├─────────────────────────────┤
│ + setId(): void             │    ├─────────────────────────────┤    │ + getId(): int              │
│ + getName(): String         │    │ + getId(): int              │    │ + setId(): void             │
│ + setName(): void           │    │ + setId(): void             │    │ + getStudentId(): int       │
│ + getCapacity(): int        │    │ + getStudentId(): int       │    │ + setStudentId(): void      │
│ + setCapacity(): void       │    │ + setStudentId(): void      │    │ + getClubId(): int          │
│ + isActive(): boolean       │    │ + getClubId(): int          │    │ + setClubId(): void         │
└─────────────────────────────┘    │ + setClubId(): void         │    │ + isActive(): boolean       │
                                   │ + isPresent(): boolean      │    └─────────────────────────────┘
                                   │ + setPresent(): void        │
                                   └─────────────────────────────┘
```

---

## 2. MULTIPLE INHERITANCE UML DIAGRAM
*(Interface Implementation - Java's Multiple Inheritance Equivalent)*

### Interface Implementation Hierarchy

```
┌─────────────────────────────┐         ┌─────────────────────────────┐
│      <<interface>>          │         │      <<interface>>          │
│    TableCellRenderer        │         │     TableCellEditor         │
├─────────────────────────────┤         ├─────────────────────────────┤
│ + getTableComponent(): String│         │ + getEditorComponent(): String│
│ + setValue(): void          │         │ + getCellValue(): String    │
│ + isValid(): boolean        │         │ + stopEditing(): boolean    │
└─────────────────────────────┘         │ + cancelEditing(): void     │
               ▲                         └─────────────────────────────┘
               │ implements                            ▲
               │                                       │ implements
┌─────────────────────────────┐                       │
│    DefaultTableCellRenderer │         ┌─────────────────────────────┐
├─────────────────────────────┤         │    AbstractCellEditor       │
│ - text: String              │         ├─────────────────────────────┤
│ - alignment: int            │         │ - listeners: String         │
│ - foreground: String        │         │ - editing: boolean          │
│ - background: String        │         │ - clickCount: int           │
├─────────────────────────────┤         ├─────────────────────────────┤
│ + setText(): void           │         │ + addListener(): void       │
│ + getText(): String         │         │ + removeListener(): void    │
│ + setAlignment(): void      │         │ + fireEditingStopped(): void│
│ + getAlignment(): int       │         │ + isCellEditable(): boolean │
│ + setForeground(): void     │         └─────────────────────────────┘
│ + setBackground(): void     │                        ▲
└─────────────────────────────┘                        │ extends
               ▲                                        │
               │ extends                                │
               │                          ┌─────────────────────────────┐
┌─────────────────────────────┐           │      ActionCellEditor       │
│     StatusCellRenderer      │           ├─────────────────────────────┤
├─────────────────────────────┤           │ - editButton: String        │
│ - statusText: String        │           │ - viewButton: String        │
│ - statusColor: String       │           │ - currentRow: int           │
│ - iconPath: String          │           │ - parentPanel: String       │
├─────────────────────────────┤           ├─────────────────────────────┤
│ + setStatus(): void         │           │ + getComponent(): String    │
│ + getStatus(): String       │           │ + getCellValue(): String    │
│ + setStatusColor(): void    │           │ + stopEditing(): boolean    │
│ + getStatusColor(): String  │           │ + cancelEditing(): void     │
│ + setIcon(): void           │           │ + addActionListener(): void │
│ + getIcon(): String         │           └─────────────────────────────┘
└─────────────────────────────┘
```

### Panel with Interface Implementation

```
┌─────────────────────────────┐
│           JPanel            │
├─────────────────────────────┤
│ - width: int                │
│ - height: int               │
│ - visible: boolean          │
│ - enabled: boolean          │
├─────────────────────────────┤
│ + setSize(): void           │
│ + getSize(): String         │
│ + setVisible(): void        │
│ + isVisible(): boolean      │
│ + add(): void               │
│ + remove(): void            │
└─────────────────────────────┘
               ▲
               │ extends & implements TableCellRenderer
               │
┌─────────────────────────────┐
│    ActionButtonRenderer     │
├─────────────────────────────┤
│ - editButton: String        │
│ - deleteButton: String      │
│ - buttonPanel: String       │
│ - isHovered: boolean        │
├─────────────────────────────┤
│ + getComponent(): String    │
│ + setButtonText(): void     │
│ + getButtonText(): String   │
│ + setHover(): void          │
│ + isHovered(): boolean      │
│ + addClickListener(): void  │
└─────────────────────────────┘
```

---

## 3. SINGLE INHERITANCE UML DIAGRAM

### JFrame Inheritance Hierarchy

```
┌─────────────────────────────┐
│           JFrame            │
├─────────────────────────────┤
│ - title: String             │
│ - width: int                │
│ - height: int               │
│ - visible: boolean          │
│ - resizable: boolean        │
│ - defaultCloseOperation: int│
├─────────────────────────────┤
│ + setTitle(): void          │
│ + getTitle(): String        │
│ + setSize(): void           │
│ + getSize(): String         │
│ + setVisible(): void        │
│ + isVisible(): boolean      │
│ + setResizable(): void      │
│ + dispose(): void           │
│ + pack(): void              │
└─────────────────────────────┘
               ▲
               │ extends
               │
┌─────────────────────────────┐
│         LoginFrame          │
├─────────────────────────────┤
│ - usernameField: String     │
│ - passwordField: String     │
│ - loginButton: String       │
│ - signUpButton: String      │
│ - forgotPasswordButton: String│
│ - isAuthenticated: boolean  │
│ - attemptCount: int         │
├─────────────────────────────┤
│ + initComponents(): void    │
│ + setupLayout(): void       │
│ + handleLogin(): void       │
│ + handleSignUp(): void      │
│ + handleForgotPassword(): void│
│ + validateInput(): boolean  │
│ + clearFields(): void       │
│ + showErrorMessage(): void  │
│ + getUsername(): String     │
│ + getPassword(): String     │
└─────────────────────────────┘
               ◊
               │ has-a
               │
┌─────────────────────────────┐
│   AuthenticationService     │
├─────────────────────────────┤
│ - currentUser: String       │
│ - isLoggedIn: boolean       │
│ - sessionTimeout: long      │
│ - maxAttempts: int          │
│ - lockoutDuration: long     │
├─────────────────────────────┤
│ + login(): boolean          │
│ + logout(): void            │
│ + isAuthenticated(): boolean│
│ + getCurrentUser(): String  │
│ + validateCredentials(): boolean│
│ + resetPassword(): boolean  │
│ + changePassword(): boolean │
│ + lockAccount(): void       │
│ + unlockAccount(): void     │
└─────────────────────────────┘
               ◊
               │ has-a
               │
┌─────────────────────────────┐
│          UserDAO            │
├─────────────────────────────┤
│ - databaseUrl: String       │
│ - username: String          │
│ - password: String          │
│ - connectionPool: String    │
│ - maxConnections: int       │
│ - timeoutSeconds: int       │
├─────────────────────────────┤
│ + connect(): boolean        │
│ + disconnect(): void        │
│ + findById(): String        │
│ + findByUsername(): String  │
│ + save(): boolean           │
│ + update(): boolean         │
│ + delete(): boolean         │
│ + authenticate(): boolean   │
│ + getAllUsers(): String     │
│ + countUsers(): int         │
└─────────────────────────────┘
```

### Service Layer Dependencies

```
┌─────────────────────────────┐
│  AttendanceTrackingFrame    │
├─────────────────────────────┤
│ - attendanceTable: String   │
│ - clubComboBox: String      │
│ - dateChooser: String       │
│ - searchField: String       │
│ - totalStudents: int        │
│ - presentCount: int         │
│ - absentCount: int          │
├─────────────────────────────┤
│ + initComponents(): void    │
│ + loadStudentList(): void   │
│ + markAttendance(): void    │
│ + markBulkAttendance(): void│
│ + exportToExcel(): void     │
│ + generateReport(): void    │
│ + filterByClub(): void      │
│ + filterByDate(): void      │
│ + refreshData(): void       │
└─────────────────────────────┘
               ◊
               │ has-a
               │
┌─────────────────────────────┐
│      AttendanceService      │
├─────────────────────────────┤
│ - attendanceRecords: String │
│ - currentSession: String    │
│ - sessionDate: String       │
│ - defaultStatus: String     │
│ - autoSave: boolean         │
├─────────────────────────────┤
│ + markStudent(): boolean    │
│ + markBulkAttendance(): boolean│
│ + getAttendanceByClub(): String│
│ + getAttendanceByDate(): String│
│ + updateAttendance(): boolean│
│ + deleteAttendance(): boolean│
│ + exportAttendance(): String │
│ + calculateStatistics(): String│
│ + validateAttendance(): boolean│
└─────────────────────────────┘
               ◊
               │ has-a
               │
┌─────────────────────────────┐
│       AttendanceDAO         │
├─────────────────────────────┤
│ - tableName: String         │
│ - primaryKey: String        │
│ - foreignKeys: String       │
│ - indexFields: String       │
│ - cacheEnabled: boolean     │
├─────────────────────────────┤
│ + create(): boolean         │
│ + findById(): String        │
│ + findByStudent(): String   │
│ + findByClub(): String      │
│ + findByDate(): String      │
│ + update(): boolean         │
│ + delete(): boolean         │
│ + bulkInsert(): boolean     │
│ + countRecords(): int       │
└─────────────────────────────┘
```

---

## Key Relationships Summary:

### Inheritance Relationships:
1. `LoginFrame`, `AttendanceTrackingFrame` **extends** `JFrame`
2. `AttendanceDashboard` **extends** `JPanel`
3. `StatusCellRenderer` **extends** `DefaultTableCellRenderer`
4. `ActionCellEditor` **extends** `AbstractCellEditor`
5. `ActionButtonRenderer` **extends** `JPanel` **implements** `TableCellRenderer`

### Aggregation Relationships:
1. `LoginFrame` **has-a** `AuthenticationService`
2. `AuthenticationService` **has-a** `UserDAO`
3. `AttendanceTrackingFrame` **has-a** `AttendanceService`
4. `AttendanceService` **has-a** `AttendanceDAO`
5. All GUI classes **have-a** various DAO and Service objects

### Data Types Used:
- **String**: For text fields, names, descriptions
- **int**: For IDs, counts, numbers
- **boolean**: For flags, status indicators
- **long**: For timestamps, timeouts
- **void**: For methods that don't return values

All classes are connected through either inheritance (extending parent classes) or aggregation (containing other objects as attributes), creating a cohesive object-oriented design with proper separation of concerns across the GUI, Service, DAO, and Model layers.