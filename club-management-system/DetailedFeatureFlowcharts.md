# Club Management System - Detailed Inner Process Flowcharts

## 📋 **MANAGEMENT DASHBOARD - DETAILED INNER PROCESSES**

### 🏠 Dashboard Feature - Complete Process Flow
```
[Click Dashboard Button]
    ↓
[NavigationToolbar.notifyAction("dashboard")]
    ↓
[MainDashboard.handleToolbarAction()]
    ↓
[Case: "dashboard" → loadInitialContent()]
    ↓
[authService.isClubManager() = true]
    ↓
[showProposalManagement()]
    ↓
┌─[UI Process]─────────────────┐
│ • contentPanel.removeAll()  │
│ • Create ModernTheme card   │
│ • Initialize ProposalMgmt   │
│ • Add to CardLayout         │
│ • Show "proposalManagement" │
│ • Revalidate/repaint        │
└─────────────────────────────┘
    ↓
[ProposalManagementPanel Constructor]
    ↓
┌─[Panel Initialization]──────┐
│ • Create proposal table     │
│ • Load proposals from DB    │
│ • Setup filter buttons     │
│ • Create action buttons    │
│ • Setup event handlers     │
└─────────────────────────────┘
    ↓
[Database Loading Process]
    ↓
┌─[ProposalDAO.getAllProposals()]─┐
│ • Get database connection      │
│ • Execute: SELECT * FROM       │
│   proposals ORDER BY           │
│   submitted_date DESC          │
│ • Map ResultSet to objects     │
│ • Return List<Proposal>        │
└───────────────────────────────┘
    ↓
[UI Display Process]
    ↓
┌─[Table Population]──────────┐
│ Columns Displayed:          │
│ • ID | Title | Student      │
│ • Submitted Date | Status   │
│ • Actions (Approve/Reject)  │
│                             │
│ Filter Buttons:             │
│ • All | Pending | Approved  │
│ • Rejected | Under Review   │
│                             │
│ Action Buttons Per Row:     │
│ • 👁️ View Details          │
│ • ✅ Approve               │
│ • ❌ Reject                │
└─────────────────────────────┘
    ↓
[Status Update: "Dashboard loaded"]
    ↓
[Ready for User Interaction]

MANAGER INTERACTIONS:
┌─[View Details Click]────────┐
│ • Open proposal detail popup│
│ • Show full description     │
│ • Display attached files    │
│ • Show submission history   │
└─────────────────────────────┘

┌─[Approve Click]─────────────┐
│ • Show confirmation dialog  │
│ • Update status in database │
│ • Refresh table display     │
│ • Send notification         │
└─────────────────────────────┘

┌─[Reject Click]──────────────┐
│ • Show reason input dialog  │
│ • Update status with reason │
│ • Refresh table display     │
│ • Send notification         │
└─────────────────────────────┘
```

### 📋 Proposals Feature - Complete Process Flow
```
[Click Proposals Button]
    ↓
[Same as Dashboard - leads to ProposalManagementPanel]
    ↓
[Additional Proposal Management Features]
    ↓
┌─[Advanced Proposal Operations]──┐
│ Search & Filter:                │
│ • Text search in titles/desc    │
│ • Filter by student name        │
│ • Filter by submission date     │
│ • Filter by proposal category   │
│                                 │
│ Bulk Operations:                │
│ • Select multiple proposals     │
│ • Bulk approve/reject           │
│ • Export proposal list          │
│                                 │
│ Proposal Analytics:             │
│ • Approval rate statistics      │
│ • Most active students          │
│ • Proposal trends by month      │
└─────────────────────────────────┘
    ↓
[Detailed Proposal View Process]
    ↓
┌─[When View Details Clicked]─────┐
│ Database Query:                 │
│ • Get full proposal details     │
│ • Get student information       │
│ • Get submission history        │
│ • Get attached files            │
│                                 │
│ UI Display:                     │
│ • Modal dialog window           │
│ • Scrollable content area       │
│ • File download links           │
│ • Action history timeline       │
│ • Comment/feedback section      │
└─────────────────────────────────┘
    ↓
[Manager Decision Process]
    ↓
┌─[Approval Process]──────────────┐
│ 1. Confirmation Dialog:         │
│    "Approve this proposal?"     │
│                                 │
│ 2. Database Update:             │
│    UPDATE proposals SET         │
│    status = 'APPROVED',         │
│    reviewed_by = manager_id,    │
│    reviewed_date = NOW()        │
│                                 │
│ 3. Student Notification:        │
│    • Email notification         │
│    • In-app status update       │
│                                 │
│ 4. UI Refresh:                  │
│    • Update table row color     │
│    • Disable action buttons     │
│    • Show success message       │
└─────────────────────────────────┘
```

### 📊 Attendance Reports Feature - Complete Process Flow
```
[Click Attendance Reports Button]
    ↓
[showManagerAttendanceReports()]
    ↓
[ManagerAttendanceReportPanel Creation]
    ↓
┌─[Panel Initialization]──────────┐
│ UI Components:                  │
│ • Date range picker             │
│ • Student filter dropdown       │
│ • Club filter dropdown          │
│ • Report type radio buttons     │
│ • Generate button               │
│ • Export options                │
│ • Charts area                   │
│ • Data table area               │
└─────────────────────────────────┘
    ↓
[Initial Data Loading]
    ↓
┌─[Database Queries on Load]──────┐
│ 1. Load All Students:           │
│    SELECT id, full_name, role   │
│    FROM users                   │
│    ORDER BY full_name           │
│                                 │
│ 2. Load All Clubs:              │
│    SELECT id, name              │
│    FROM clubs                   │
│    ORDER BY name                │
│                                 │
│ 3. Load Recent Attendance:      │
│    SELECT * FROM attendance     │
│    WHERE date >= LAST_30_DAYS   │
│    ORDER BY date DESC           │
└─────────────────────────────────┘
    ↓
[Default Display Setup]
    ↓
┌─[Initial Report Display]────────┐
│ Default View:                   │
│ • Last 30 days attendance      │
│ • All students included         │
│ • Summary statistics at top     │
│ • Bar chart by date             │
│ • Detailed table below          │
│                                 │
│ Summary Statistics:             │
│ • Total attendance days: X      │
│ • Average attendance rate: Y%   │
│ • Most active student: Name     │
│ • Lowest attendance: Name       │
│ • Peak attendance day: Date     │
└─────────────────────────────────┘
    ↓
[User Filter Interaction Process]
    ↓
┌─[When Generate Report Clicked]──┐
│ 1. Validate Inputs:             │
│    • Check date range validity  │
│    • Ensure end > start date    │
│    • Validate filter selections │
│                                 │
│ 2. Build Dynamic Query:         │
│    SELECT a.*, u.full_name,     │
│           c.name as club_name   │
│    FROM attendance a            │
│    JOIN users u ON a.user_id    │
│    LEFT JOIN clubs c ON         │
│         u.assigned_club_id      │
│    WHERE a.date BETWEEN ? AND ? │
│    [Additional filters based    │
│     on user selections]         │
│                                 │
│ 3. Execute Query & Process:     │
│    • Get filtered results       │
│    • Calculate statistics       │
│    • Generate chart data        │
│    • Update UI components       │
└─────────────────────────────────┘
    ↓
[Report Display Update]
    ↓
┌─[Updated Display Components]────┐
│ Chart Updates:                  │
│ • Attendance trend line chart   │
│ • Student comparison bar chart  │
│ • Club-wise pie chart           │
│ • Daily pattern heat map        │
│                                 │
│ Table Updates:                  │
│ • Filtered attendance records   │
│ • Student name | Date | Status  │
│ • Club | Time In | Time Out     │
│ • Sortable columns              │
│ • Pagination if > 100 records   │
│                                 │
│ Statistics Panel:               │
│ • Updated percentages           │
│ • Comparison with prev period   │
│ • Trend indicators (↑↓)         │
└─────────────────────────────────┘
    ↓
[Export Functionality]
    ↓
┌─[Export Options Available]──────┐
│ PDF Export:                     │
│ • Generate formatted report     │
│ • Include charts and tables     │
│ • Add header with date range    │
│ • Save/download PDF file        │
│                                 │
│ Excel Export:                   │
│ • Export raw data to CSV        │
│ • Include calculated fields     │
│ • Multiple sheets if needed     │
│                                 │
│ Print Option:                   │
│ • Format for printer            │
│ • Scale charts appropriately    │
│ • Page break handling           │
└─────────────────────────────────┘
```

### 👥 Club Assignments Feature - Complete Process Flow
```
[Click Club Assignments Button]
    ↓
[showManagerClubAssignments()]
    ↓
[ManagerClubAssignmentsViewPanel Creation]
    ↓
┌─[Panel Layout Initialization]───┐
│ Top Section:                    │
│ • Title: "Student Club          │
│   Assignments Overview"         │
│ • Refresh button                │
│ • Export button                 │
│ • Filter controls               │
│                                 │
│ Filter Section:                 │
│ • Grade filter (All/9/11)       │
│ • Club filter dropdown          │
│ • Assignment status filter      │
│ • Search box for student name   │
│                                 │
│ Main Content:                   │
│ • Data table with scroll        │
│ • Statistics panel on right     │
│ • Footer with totals            │
└─────────────────────────────────┘
    ↓
[Data Loading Process]
    ↓
┌─[Complex Database Query]────────┐
│ Main Query:                     │
│ SELECT u.id, u.full_name,       │
│        u.username, u.role,      │
│        u.assigned_club_id,      │
│        c.name as club_name,     │
│        c.description,           │
│        u.first_login_completed  │
│ FROM users u                    │
│ LEFT JOIN clubs c ON            │
│     u.assigned_club_id = c.id   │
│ WHERE u.role IN ('GRADE_9',     │
│                  'GRADE_11')    │
│ ORDER BY u.role, u.full_name    │
│                                 │
│ Additional Queries:             │
│ • Count total students by grade │
│ • Count assigned vs unassigned  │
│ • Get club capacity information │
└─────────────────────────────────┘
    ↓
[Data Processing & Organization]
    ↓
┌─[Data Transformation]───────────┐
│ Student Categorization:         │
│ • Grade 9 students list         │
│ • Grade 11 students list        │
│ • Assigned students             │
│ • Unassigned students           │
│                                 │
│ Club Statistics:                │
│ • Students per club             │
│ • Club capacity utilization     │
│ • Popular clubs ranking         │
│                                 │
│ Assignment Status:              │
│ • Completed assignments         │
│ • Pending assignments           │
│ • Override assignments          │
└─────────────────────────────────┘
    ↓
[UI Population Process]
    ↓
┌─[Table Display Setup]───────────┐
│ Table Columns:                  │
│ • Student ID                    │
│ • Full Name                     │
│ • Username                      │
│ • Grade Level                   │
│ • Assigned Club                 │
│ • Assignment Date               │
│ • Status                        │
│                                 │
│ Row Styling:                    │
│ • Grade 9: Light blue bg        │
│ • Grade 11: Light green bg      │
│ • Unassigned: Light red bg      │
│ • Completed: Normal bg          │
│                                 │
│ Interactive Elements:           │
│ • Sortable column headers       │
│ • Row selection highlighting    │
│ • Context menu (right-click)    │
└─────────────────────────────────┘
    ↓
[Statistics Panel Display]
    ↓
┌─[Live Statistics Display]───────┐
│ Summary Cards:                  │
│ ┌─[Total Students]──┐           │
│ │ Count: 150        │           │
│ │ Grade 9: 75       │           │
│ │ Grade 11: 75      │           │
│ └───────────────────┘           │
│                                 │
│ ┌─[Assignment Status]┐          │
│ │ Assigned: 140     │           │
│ │ Pending: 10       │           │
│ │ Rate: 93.3%       │           │
│ └───────────────────┘           │
│                                 │
│ ┌─[Popular Clubs]───┐           │
│ │ 1. Drama Club (25)│           │
│ │ 2. Science (22)   │           │
│ │ 3. Sports (20)    │           │
│ └───────────────────┘           │
└─────────────────────────────────┘
    ↓
[Filter Interaction Handling]
    ↓
┌─[Filter Application Process]────┐
│ When Grade Filter Changed:      │
│ • Re-query database with        │
│   WHERE role = selected_grade   │
│ • Update table display          │
│ • Recalculate statistics        │
│                                 │
│ When Club Filter Changed:       │
│ • Filter current data set       │
│ • Show only selected club       │
│ • Update assignment count       │
│                                 │
│ When Search Text Entered:       │
│ • Filter by student name        │
│ • Highlight matching text       │
│ • Update result count           │
│                                 │
│ Real-time Updates:              │
│ • No page refresh needed        │
│ • Smooth transitions           │
│ • Loading indicators            │
└─────────────────────────────────┘
    ↓
[Back to Dashboard Functionality]
    ↓
┌─[Return Navigation]─────────────┐
│ Back Button Click:              │
│ • setBackToDashboardCallback()  │
│   triggered                     │
│ • Return to loadInitialContent()│
│ • Show ProposalManagement       │
│ • Update status: "Returned to   │
│   Dashboard"                    │
│                                 │
│ Cleanup Process:                │
│ • Clear current panel           │
│ • Release resources             │
│ • Reset navigation state        │
└─────────────────────────────────┘
```

## 🎓 **GRADE 11 DASHBOARD - DETAILED INNER PROCESSES**

### 🏠 Home Feature - Complete Process Flow
```
[Click Home Button]
    ↓
[NavigationToolbar.notifyAction("dashboard")]
    ↓
[MainDashboard.handleToolbarAction()]
    ↓
[Case: "dashboard" → loadInitialContent()]
    ↓
[authService.isGrade11() = true]
    ↓
[showGrade11SelfAttendance()]
    ↓
┌─[Grade 11 Dashboard Creation]───┐
│ • contentPanel.removeAll()      │
│ • Create ModernTheme card       │
│ • Initialize Grade11Enhanced    │
│   Dashboard                     │
│ • Set logout callback          │
│ • Add to CardLayout             │
│ • Show dashboard panel          │
└─────────────────────────────────┘
    ↓
[Grade11EnhancedDashboard Constructor]
    ↓
┌─[Dashboard Initialization]──────┐
│ Components Created:             │
│ • CardLayout for content        │
│ • Self-attendance panel         │
│ • Proposal submission panel     │
│ • Proposal status panel         │
│ • Grade 9 students view panel   │
│                                 │
│ Navigation Buttons:             │
│ • 📊 Mark Attendance            │
│ • 📤 Upload Proposal            │
│ • 📋 Check Proposal Status      │
│ • 👥 View Grade 9 Students      │
│ • 🚪 Logout (red button)        │
│                                 │
│ Default View:                   │
│ • showAttendancePanel()         │
└─────────────────────────────────┘
    ↓
[Default Attendance Panel Display]
    ↓
┌─[Grade11SelfAttendancePanel]────┐
│ Welcome Section:                │
│ • "Welcome, [Student Name]!"    │
│ • Current date display          │
│ • Your club: [Club Name]        │
│                                 │
│ Today's Status Check:           │
│ • Query: SELECT * FROM          │
│   attendance WHERE user_id = ?  │
│   AND date = TODAY              │
│                                 │
│ Display Logic:                  │
│ IF already marked today:        │
│ • Show current status           │
│ • "You marked: PRESENT"         │
│ • Disable marking buttons       │
│ • Show change option            │
│                                 │
│ IF not marked today:            │
│ • Show marking buttons          │
│ • Large PRESENT button (green)  │
│ • Large ABSENT button (red)     │
│ • Time display: [Current Time]  │
└─────────────────────────────────┘
    ↓
[Attendance History Display]
    ↓
┌─[History Section at Bottom]─────┐
│ Recent Attendance (Last 7 days):│
│ ┌─[Mon]─┬─[Tue]─┬─[Wed]─┬─[Thu]┐│
│ │ ✅   │ ✅   │ ❌   │ ✅  ││
│ │Present│Present│Absent │Present││
│ └──────┴──────┴──────┴──────┘│
│                                 │
│ Monthly Statistics:             │
│ • This month: 85% attendance    │
│ • Days present: 17/20           │
│ • Days absent: 3/20             │
│ • Perfect weeks: 2/4            │
└─────────────────────────────────┘
    ↓
[Ready for User Interaction]

USER INTERACTIONS:
┌─[Mark Present Click]────────────┐
│ 1. Confirmation Dialog:         │
│    "Mark attendance as Present  │
│     for today?"                 │
│                                 │
│ 2. Database Insert:             │
│    INSERT INTO attendance       │
│    (user_id, date, status,      │
│     time_marked)                │
│    VALUES (?, TODAY, 'PRESENT', │
│            NOW())               │
│                                 │
│ 3. UI Updates:                  │
│    • Disable marking buttons    │
│    • Show success message       │
│    • Update status display      │
│    • Add to history grid        │
│    • Update statistics          │
└─────────────────────────────────┘

┌─[Mark Absent Click]─────────────┐
│ 1. Reason Dialog:               │
│    "Please provide reason for   │
│     absence (optional):"        │
│    [Text area for reason]       │
│                                 │
│ 2. Database Insert:             │
│    INSERT INTO attendance       │
│    (user_id, date, status,      │
│     reason, time_marked)        │
│    VALUES (?, TODAY, 'ABSENT',  │
│            reason, NOW())       │
│                                 │
│ 3. UI Updates:                  │
│    • Disable marking buttons    │
│    • Show acknowledgment        │
│    • Update status display      │
│    • Update statistics          │
└─────────────────────────────────┘
```

### 📤 Upload Proposal Feature - Complete Process Flow
```
[Click Upload Proposal Button]
    ↓
[Grade11Dashboard.showProposalSubmissionPanel()]
    ↓
[CardLayout.show("proposalSubmission")]
    ↓
┌─[ProposalSubmissionPanel Load]───┐
│ Panel Layout:                   │
│ • Header: "Submit New Proposal" │
│ • Form fields section          │
│ • File upload section          │
│ • Preview section              │
│ • Action buttons               │
│                                 │
│ Form Validation Setup:          │
│ • Real-time field validation    │
│ • File type checking           │
│ • Size limit enforcement       │
│ • Required field highlighting   │
└─────────────────────────────────┘
    ↓
[Form Field Initialization]
    ↓
┌─[Input Fields Created]──────────┐
│ Required Fields:                │
│ • 📝 Proposal Title             │
│   [Text field - max 100 chars] │
│                                 │
│ • 📋 Category                   │
│   [Dropdown: Academic, Sports,  │
│    Cultural, Technical, Other]  │
│                                 │
│ • 📄 Description               │
│   [Large text area - max 1000] │
│                                 │
│ • 📎 Supporting Documents       │
│   [File chooser - PDF, DOC,    │
│    DOCX, max 10MB]             │
│                                 │
│ Optional Fields:                │
│ • 🎯 Expected Outcome           │
│ • 💰 Budget Estimate            │
│ • 📅 Proposed Timeline          │
│ • 👥 Team Members               │
└─────────────────────────────────┘
    ↓
[Real-time Validation Setup]
    ↓
┌─[Field Validation Listeners]────┐
│ Title Field:                    │
│ • Check length on keyup         │
│ • Remove special characters     │
│ • Show character count          │
│                                 │
│ Description Field:              │
│ • Word count display            │
│ • Grammar check indicator       │
│ • Auto-save draft every 30s     │
│                                 │
│ File Upload:                    │
│ • Drag & drop support           │
│ • File type validation          │
│ • Size check before upload      │
│ • Preview for supported types   │
│                                 │
│ Form Status:                    │
│ • Progress indicator            │
│ • Save draft option             │
│ • Auto-completion percentage    │
└─────────────────────────────────┘
    ↓
[User Fills Form Process]
    ↓
┌─[As User Types/Selects]─────────┐
│ Title Field Interaction:        │
│ • Real-time character limit     │
│ • Duplicate title check         │
│ • Suggestion popup if similar   │
│                                 │
│ Category Selection:             │
│ • Update form fields based on   │
│   category (some categories     │
│   show additional fields)       │
│                                 │
│ Description Area:               │
│ • Spell check highlighting      │
│ • Word count: "156/1000"        │
│ • Auto-resize as content grows  │
│                                 │
│ File Upload Process:            │
│ • Show upload progress bar      │
│ • Virus scan indicator          │
│ • File preview thumbnail        │
│ • Replace file option           │
└─────────────────────────────────┘
    ↓
[Submit Button Click Process]
    ↓
┌─[Pre-Submission Validation]─────┐
│ 1. Client-side Checks:          │
│    • All required fields filled │
│    • File size within limits    │
│    • Description min 50 words   │
│    • Title not already used     │
│                                 │
│ 2. Confirmation Dialog:         │
│    "Review Your Proposal"       │
│    ┌─[Preview Modal]────────┐   │
│    │ Title: [Title]         │   │
│    │ Category: [Category]   │   │
│    │ Description: [First    │   │
│    │ 200 chars...]          │   │
│    │ File: [filename.pdf]   │   │
│    │                        │   │
│    │ [Edit] [Submit] [Cancel]│   │
│    └────────────────────────┘   │
└─────────────────────────────────┘
    ↓
[Final Submission Process]
    ↓
┌─[Database & File Operations]────┐
│ 1. File Upload:                 │
│    • Move file to secure folder │
│    • Generate unique filename   │
│    • Create file metadata entry │
│                                 │
│ 2. Proposal Database Insert:    │
│    INSERT INTO proposals        │
│    (user_id, title, category,   │
│     description, file_path,     │
│     submitted_date, status)     │
│    VALUES (?, ?, ?, ?, ?,       │
│            NOW(), 'PENDING')    │
│                                 │
│ 3. Generate Proposal ID:        │
│    • Format: PROP-YYYY-NNNN     │
│    • Example: PROP-2024-0156    │
│                                 │
│ 4. Email Notification:          │
│    • Send to student (confirm)   │
│    • Send to managers (review)   │
└─────────────────────────────────┘
    ↓
[Success Response Display]
    ↓
┌─[Success Page Display]──────────┐
│ Success Message:                │
│ ✅ "Proposal Submitted          │
│    Successfully!"               │
│                                 │
│ Submission Details:             │
│ • Proposal ID: PROP-2024-0156   │
│ • Submitted: [Date & Time]      │
│ • Status: Pending Review        │
│ • Expected Review: 5-7 days     │
│                                 │
│ Next Steps:                     │
│ • "You will receive email       │
│   notification when reviewed"   │
│ • "Check status anytime in      │
│   Proposal Status section"      │
│                                 │
│ Action Buttons:                 │
│ • 📋 Check Status               │
│ • 📤 Submit Another             │
│ • 🏠 Back to Dashboard          │
└─────────────────────────────────┘
    ↓
[Form Reset & Cleanup]
    ↓
[Ready for Next Proposal]
```

### 📊 Proposal Status Feature - Complete Process Flow
```
[Click Proposal Status Button]
    ↓
[Grade11Dashboard.showProposalStatusPanel()]
    ↓
[CardLayout.show("proposalStatus")]
    ↓
┌─[ProposalStatusPanel Load]───────┐
│ Loading Process:                │
│ • Show loading spinner          │
│ • Query user's proposals        │
│ • Calculate statistics          │
│ • Setup refresh timer           │
│                                 │
│ Database Query:                 │
│ SELECT p.*, u.full_name as      │
│        reviewer_name            │
│ FROM proposals p                │
│ LEFT JOIN users u ON            │
│      p.reviewed_by = u.id       │
│ WHERE p.user_id = ?             │
│ ORDER BY p.submitted_date DESC  │
└─────────────────────────────────┘
    ↓
[Status Dashboard Creation]
    ↓
┌─[Dashboard Layout Setup]────────┐
│ Header Section:                 │
│ • Title: "My Proposal Status"   │
│ • Refresh button                │
│ • Last updated timestamp        │
│                                 │
│ Statistics Cards:               │
│ ┌─[Total]─┬─[Pending]─┬─[Approved]┐
│ │   5    │    2     │     2      │
│ │Proposals│ Pending  │  Approved  │
│ └────────┴──────────┴────────────┘
│ ┌─[Rejected]─┬─[Success Rate]───┐ │
│ │     1     │      80%         │ │
│ │ Rejected  │   Success Rate   │ │
│ └───────────┴──────────────────┘ │
└─────────────────────────────────┘
    ↓
[Proposals Table Setup]
    ↓
┌─[Proposals List Display]────────┐
│ Table Columns:                  │
│ • 🆔 Proposal ID                │
│ • 📝 Title                      │
│ • 📋 Category                   │
│ • 📅 Submitted Date             │
│ • 📊 Status                     │
│ • 👤 Reviewed By                │
│ • 🔧 Actions                    │
│                                 │
│ Row Status Styling:             │
│ • PENDING: Yellow border        │
│ • APPROVED: Green background    │
│ • REJECTED: Red background      │
│ • UNDER_REVIEW: Blue border     │
│                                 │
│ Interactive Elements:           │
│ • Click row to expand details   │
│ • Action buttons per status     │
│ • Sort by any column            │
└─────────────────────────────────┘
    ↓
[Status-Specific Actions Setup]
    ↓
┌─[Action Buttons by Status]──────┐
│ For PENDING Proposals:          │
│ • 👁️ View Details               │
│ • ✏️ Edit (if within 24hrs)     │
│ • ❌ Withdraw                   │
│                                 │
│ For APPROVED Proposals:         │
│ • 👁️ View Details               │
│ • 📥 Download Approval Letter   │
│ • 📋 View Implementation Plan   │
│                                 │
│ For REJECTED Proposals:         │
│ • 👁️ View Details               │
│ • 📝 View Rejection Reason      │
│ • 🔄 Resubmit Improved Version  │
│                                 │
│ For UNDER_REVIEW Proposals:     │
│ • 👁️ View Details               │
│ • 📞 Contact Reviewer           │
│ • ⏱️ View Review Progress       │
└─────────────────────────────────┘
    ↓
[Detail View Interaction]
    ↓
┌─[When Row Clicked - Detail View]─┐
│ Expanded Row Content:           │
│ ┌─[Proposal Details]─────────┐  │
│ │ Full Description:          │  │
│ │ [Complete proposal text]   │  │
│ │                            │  │
│ │ Submission Details:        │  │
│ │ • File: document.pdf       │  │
│ │ • Size: 2.3 MB             │  │
│ │ • Submitted: [DateTime]    │  │
│ │                            │  │
│ │ Review Timeline:           │  │
│ │ ✅ Submitted: Day 1        │  │
│ │ 🔄 Under Review: Day 3     │  │
│ │ ⏳ Decision Pending        │  │
│ │                            │  │
│ │ Comments/Feedback:         │  │
│ │ [Reviewer comments if any] │  │
│ └────────────────────────────┘  │
└─────────────────────────────────┘
    ↓
[Real-time Status Updates]
    ↓
┌─[Auto-Refresh Mechanism]────────┐
│ Background Process:             │
│ • Check for updates every 30s   │
│ • Compare with last known state │
│ • Show notification for changes │
│                                 │
│ Status Change Notifications:    │
│ • "🎉 Proposal PROP-2024-0156   │
│   has been APPROVED!"           │
│ • Toast notification with sound │
│ • Update table row styling      │
│ • Refresh statistics cards      │
│                                 │
│ Email Integration:              │
│ • Auto-sync with email notifs   │
│ • Mark as read when viewed      │
│ • Link to full email content    │
└─────────────────────────────────┘
```

### 👥 View Grade 9 Feature - Complete Process Flow
```
[Click View Grade 9 Button]
    ↓
[Grade11Dashboard.showGrade9StudentsViewPanel()]
    ↓
[CardLayout.show("grade9StudentsView")]
    ↓
┌─[Grade9StudentsViewPanel Load]───┐
│ Authorization Check:            │
│ • Verify user is Grade 11       │
│ • Check viewing permissions     │
│ • Log access attempt            │
│                                 │
│ Initial Loading State:          │
│ • Show loading animation        │
│ • Display "Loading Grade 9      │
│   students..." message          │
│ • Initialize empty table        │
└─────────────────────────────────┘
    ↓
[Data Loading Process]
    ↓
┌─[Complex Multi-table Query]─────┐
│ Main Student Query:             │
│ SELECT u.id, u.full_name,       │
│        u.username, u.email,     │
│        u.assigned_club_id,      │
│        c.name as club_name,     │
│        c.description,           │
│        u.first_login_completed  │
│ FROM users u                    │
│ LEFT JOIN clubs c ON            │
│     u.assigned_club_id = c.id   │
│ WHERE u.role = 'GRADE_9'        │
│ ORDER BY u.full_name            │
│                                 │
│ Attendance Summary Query:       │
│ SELECT user_id,                 │
│        COUNT(*) as total_days,  │
│        SUM(CASE WHEN status =   │
│            'PRESENT' THEN 1     │
│            ELSE 0 END) as       │
│            present_days         │
│ FROM attendance                 │
│ WHERE user_id IN (Grade9_IDs)   │
│   AND date >= CURRENT_MONTH     │
│ GROUP BY user_id                │
└─────────────────────────────────┘
    ↓
[Data Processing & Enrichment]
    ↓
┌─[Student Data Enhancement]──────┐
│ For Each Grade 9 Student:       │
│ • Calculate attendance rate     │
│ • Determine assignment status   │
│ • Get recent activity           │
│ • Format contact information    │
│                                 │
│ Calculations:                   │
│ • Attendance % = (present_days  │
│   / total_days) * 100           │
│ • Assignment status = Club      │
│   assigned ? "Assigned" :       │
│   "Unassigned"                  │
│ • Last activity = Most recent   │
│   attendance date               │
│                                 │
│ Data Grouping:                  │
│ • Group by club assignment      │
│ • Sort by attendance rate       │
│ • Flag students needing help    │
└─────────────────────────────────┘
    ↓
[UI Layout Creation]
    ↓
┌─[Grade 9 Overview Interface]────┐
│ Header Section:                 │
│ • Title: "Grade 9 Students      │
│   Overview"                     │
│ • Total count: "75 students"    │
│ • Quick stats summary           │
│                                 │
│ Filter & Search Section:        │
│ • 🔍 Search by name             │
│ • 🏫 Filter by club             │
│ • 📊 Filter by attendance rate  │
│ • 📋 Filter by assignment status│
│                                 │
│ Action Buttons:                 │
│ • 📊 Generate Report            │
│ • 📧 Contact All               │
│ • 📋 Export List               │
│ • 🔄 Refresh Data              │
└─────────────────────────────────┘
    ↓
[Main Data Table Display]
    ↓
┌─[Students Table Layout]─────────┐
│ Column Headers:                 │
│ • 👤 Student Name (sortable)    │
│ • 📧 Email Address              │
│ • 🏫 Assigned Club              │
│ • 📊 Attendance Rate            │
│ • 📅 Last Activity              │
│ • 📋 Status                     │
│ • 🔧 Actions                    │
│                                 │
│ Row Data Example:               │
│ ┌──────────────────────────────┐ │
│ │ John Smith                   │ │
│ │ john.smith@school.edu        │ │
│ │ Drama Club                   │ │
│ │ 95% (19/20 days)            │ │
│ │ Today, 2:30 PM               │ │
│ │ ✅ Active                    │ │
│ │ [View] [Contact]             │ │
│ └──────────────────────────────┘ │
│                                 │
│ Row Styling by Status:          │
│ • High attendance (>90%): Green │
│ • Medium attendance (70-90%):   │
│   Yellow                        │
│ • Low attendance (<70%): Red    │
│ • Unassigned students: Gray     │
└─────────────────────────────────┘
    ↓
[Interactive Features Setup]
    ↓
┌─[Student Interaction Options]───┐
│ Row Click Actions:              │
│ • Click name → Student details  │
│ • Click club → Club information │
│ • Click attendance → History    │
│                                 │
│ Action Buttons Per Row:         │
│ • 👁️ View Details:              │
│   - Personal information        │
│   - Complete attendance record  │
│   - Club participation history  │
│                                 │
│ • 📧 Contact Student:           │
│   - Pre-filled email template   │
│   - Common message templates    │
│   - Send notification option    │
│                                 │
│ • 📊 View Attendance:           │
│   - Monthly calendar view       │
│   - Trend analysis              │
│   - Comparison with peers       │
└─────────────────────────────────┘
    ↓
[Detail View Modal System]
    ↓
┌─[Student Detail Modal]──────────┐
│ When "View Details" clicked:    │
│                                 │
│ Modal Window Content:           │
│ ┌─[Student Profile]──────────┐  │
│ │ 📷 [Photo placeholder]     │  │
│ │ Name: John Smith           │  │
│ │ Email: john.smith@...      │  │
│ │ Username: jsmith_grade9    │  │
│ │ Club: Drama Club           │  │
│ │ Joined: September 2024     │  │
│ │                            │  │
│ │ 📊 Attendance Summary:     │  │
│ │ • This month: 95%          │  │
│ │ • Last month: 90%          │  │
│ │ │ • Overall: 92%           │  │
│ │                            │  │
│ │ 📅 Recent Activity:        │  │
│ │ • Today: Present (2:30 PM) │  │
│ │ • Yesterday: Present       │  │
│ │ • Monday: Absent           │  │
│ │                            │  │
│ │ 🏫 Club Information:       │  │
│ │ • Meeting days: Tue, Thu   │  │
│ │ • Room: A-205              │  │
│ │ • Coordinator: Ms. Johnson │  │
│ └────────────────────────────┘  │
│                                 │
│ Modal Actions:                  │
│ • 📧 Send Email                 │
│ • 📋 Generate Report            │
│ • 🔄 Refresh Data               │
│ • ❌ Close                      │
└─────────────────────────────────┘
    ↓
[Export & Reporting Features]
    ↓
┌─[Export Options Available]──────┐
│ Report Generation:              │
│ • 📊 Grade 9 Summary Report     │
│ • 📈 Attendance Analytics       │
│ • 🏫 Club Assignment Report     │
│ • 📧 Contact List Export        │
│                                 │
│ Export Formats:                 │
│ • PDF: Formatted report         │
│ • Excel: Data spreadsheet       │
│ • CSV: Raw data export          │
│ • Print: Printer-friendly       │
│                                 │
│ Email Integration:              │
│ • Bulk email to filtered list   │
│ • Template messages available   │
│ • Track email delivery status   │
│ • Schedule recurring updates    │
└─────────────────────────────────┘
```

## 🎒 **GRADE 9 DASHBOARD - DETAILED INNER PROCESSES**

### 🏠 Home Feature - Complete Process Flow
```
[Click Home Button]
    ↓
[NavigationToolbar.notifyAction("dashboard")]
    ↓
[MainDashboard.handleToolbarAction()]
    ↓
[Case: "dashboard" → loadInitialContent()]
    ↓
[authService.isGrade9() = true]
    ↓
[showAttendanceMarking()]
    ↓
┌─[Grade 9 Dashboard Creation]────┐
│ • contentPanel.removeAll()      │
│ • Create ModernTheme card       │
│ • Initialize Grade9Simple       │
│   DashboardPanel                │
│ • Focus on attendance only      │
│ • Simplified interface          │
└─────────────────────────────────┘
    ↓
[Grade9SimpleDashboardPanel Constructor]
    ↓
┌─[Simplified Dashboard Setup]────┐
│ Layout Design:                  │
│ • Large, clear welcome message  │
│ • Today's date prominently      │
│ • Big attendance buttons        │
│ • Simple status indicators      │
│ • Minimal distractions          │
│                                 │
│ Component Sizing:               │
│ • Extra large fonts (18px+)     │
│ • Wide button spacing           │
│ • Clear visual hierarchy        │
│ • High contrast colors          │
│                                 │
│ User-Friendly Features:         │
│ • Tooltips on everything        │
│ • Clear instructions            │
│ • Visual feedback               │
│ • Error prevention              │
└─────────────────────────────────┘
    ↓
[Welcome Section Creation]
    ↓
┌─[Welcome Display Setup]─────────┐
│ Header Content:                 │
│ • "Welcome to Club Management!" │
│ • Student name: [Full Name]     │
│ • Today's date: [Day, Date]     │
│ • Current time: [HH:MM AM/PM]   │
│                                 │
│ Personalized Information:       │
│ • Your username: [username]     │
│ • Grade level: 9th Grade        │
│ • School year: 2024-2025        │
│                                 │
│ Daily Reminder:                 │
│ • "Remember to mark your        │
│   attendance today!"            │
│ • Visual reminder icon          │
│ • Friendly, encouraging tone    │
└─────────────────────────────────┘
    ↓
[Today's Status Check Process]
    ↓
┌─[Attendance Status Query]───────┐
│ Database Query:                 │
│ SELECT status, time_marked,     │
│        reason                   │
│ FROM attendance                 │
│ WHERE user_id = ?               │
│   AND date = CURRENT_DATE       │
│                                 │
│ Status Determination:           │
│ IF record exists:               │
│ • Already marked today          │
│ • Show current status           │
│ • Display confirmation message  │
│ • Show change option            │
│                                 │
│ IF no record:                   │
│ • Not marked yet                │
│ • Show marking buttons          │
│ • Encourage to mark attendance  │
│ • Show current time             │
└─────────────────────────────────┘
    ↓
[Attendance Interface Display]
    ↓
┌─[Attendance Marking Interface]──┐
│ IF NOT MARKED TODAY:            │
│ ┌─[Marking Buttons]───────────┐ │
│ │ ✅ PRESENT                  │ │
│ │ [Large green button]        │ │
│ │ "Click if you are here      │ │
│ │  today"                     │ │
│ │                             │ │
│ │ ❌ ABSENT                   │ │
│ │ [Large red button]          │ │
│ │ "Click if you cannot        │ │
│ │  attend today"              │ │
│ └─────────────────────────────┘ │
│                                 │
│ Current Time Display:           │
│ • "Current time: 2:45 PM"       │
│ • "Please mark before 5:00 PM"  │
│                                 │
│ IF ALREADY MARKED:              │
│ ┌─[Status Display]────────────┐ │
│ │ ✅ "You marked PRESENT       │ │
│ │    today at 2:30 PM"        │ │
│ │                             │ │
│ │ [Change Attendance] button   │ │
│ │ (smaller, secondary style)   │ │
│ └─────────────────────────────┘ │
└─────────────────────────────────┘
    ↓
[Attendance History Preview]
    ↓
┌─[Simple History Display]────────┐
│ "Your Recent Attendance:"       │
│                                 │
│ This Week:                      │
│ ┌─[Mon]─┬─[Tue]─┬─[Wed]─┬─[Thu]─┬─[Fri]┐
│ │ ✅   │ ✅   │ ❌   │ ✅   │ ?    │
│ │Present│Present│Absent │Present│Today │
│ └──────┴──────┴──────┴──────┴──────┘
│                                 │
│ This Month So Far:              │
│ • Days attended: 16 days        │
│ • Days missed: 2 days           │
│ • Your attendance: 89%          │
│ • Keep up the good work! 👍     │
└─────────────────────────────────┘
    ↓
[Ready for User Interaction]

USER INTERACTIONS:
┌─[Present Button Click Process]──┐
│ 1. Button Click Handler:        │
│    • Show loading animation     │
│    • Disable button temporarily │
│    • Prepare confirmation       │
│                                 │
│ 2. Simple Confirmation:         │
│    "Mark yourself as PRESENT    │
│     for today?"                 │
│    [Large YES button]           │
│    [Cancel button]              │
│                                 │
│ 3. Database Operation:          │
│    INSERT INTO attendance       │
│    (user_id, date, status,      │
│     time_marked)                │
│    VALUES (?, CURRENT_DATE,     │
│            'PRESENT', NOW())    │
│                                 │
│ 4. Success Response:            │
│    • Large checkmark animation  │
│    • "Great! You're marked      │
│      PRESENT for today! ✅"     │
│    • Update interface           │
│    • Show time marked           │
│    • Disable marking buttons    │
│    • Update history display     │
│                                 │
│ 5. Celebration Feedback:        │
│    • Brief success animation    │
│    • Encouraging message        │
│    • Update attendance streak   │
└─────────────────────────────────┘

┌─[Absent Button Click Process]───┐
│ 1. Gentle Confirmation:         │
│    "Are you sure you need to    │
│     mark yourself as ABSENT?"   │
│                                 │
│ 2. Optional Reason Dialog:      │
│    "Would you like to tell us   │
│     why? (Optional)"            │
│    ┌─[Reason Options]────────┐  │
│    │ ○ Sick                  │  │
│    │ ○ Family emergency      │  │
│    │ ○ Doctor appointment    │  │
│    │ ○ Other (please specify)│  │
│    │ [Text box for other]    │  │
│    └─────────────────────────┘  │
│                                 │
│ 3. Understanding Message:       │
│    "That's okay! We hope        │
│     everything is alright."     │
│                                 │
│ 4. Database Operation:          │
│    INSERT INTO attendance       │
│    (user_id, date, status,      │
│     reason, time_marked)        │
│    VALUES (?, CURRENT_DATE,     │
│            'ABSENT', reason,    │
│            NOW())               │
│                                 │
│ 5. Supportive Response:         │
│    • "Your absence is recorded" │
│    • "Hope to see you tomorrow!"│
│    • Update interface           │
│    • Show supportive message    │
│    • Update attendance display  │
└─────────────────────────────────┘

┌─[Change Attendance Feature]─────┐
│ If student wants to change:     │
│                                 │
│ 1. Change Button Click:         │
│    "Change today's attendance?" │
│                                 │
│ 2. Current Status Display:      │
│    "Currently marked: PRESENT   │
│     at 2:30 PM"                 │
│                                 │
│ 3. Change Options:              │
│    "What would you like to      │
│     change it to?"              │
│    [PRESENT] [ABSENT]           │
│                                 │
│ 4. Confirmation:                │
│    "Change from PRESENT to      │
│     ABSENT?"                    │
│    [Yes, Change] [Cancel]       │
│                                 │
│ 5. Database Update:             │
│    UPDATE attendance            │
│    SET status = ?,              │
│        time_marked = NOW(),     │
│        reason = ?               │
│    WHERE user_id = ?            │
│      AND date = CURRENT_DATE    │
│                                 │
│ 6. Confirmation Message:        │
│    "Attendance updated          │
│     successfully!"              │
└─────────────────────────────────┘
```

### ✔️ Attendance Feature - Complete Process Flow
```
[Click Attendance Button]
    ↓
[NavigationToolbar.notifyAction("selfAttendance")]
    ↓
[MainDashboard.handleToolbarAction()]
    ↓
[Case: "selfattendance" → showAttendanceMarking()]
    ↓
[authService.isGrade9() = true]
    ↓
[Shows same Grade9SimpleDashboardPanel]
    ↓
[Focus on Attendance Section]
    ↓
┌─[Dedicated Attendance View]─────┐
│ Interface Highlights:           │
│ • Scroll to attendance section  │
│ • Highlight attendance buttons  │
│ • Show detailed instructions    │
│ • Emphasize time reminders      │
│                                 │
│ Enhanced Features:              │
│ • Larger button animations      │
│ • More detailed time display    │
│ • Clearer status indicators     │
│ • Step-by-step guidance         │
└─────────────────────────────────┘
    ↓
[Enhanced Attendance Interface]
    ↓
┌─[Focused Attendance Display]────┐
│ Header Emphasis:                │
│ • "📋 MARK YOUR ATTENDANCE"     │
│ • Larger, bold typography       │
│ • Clear visual separation       │
│                                 │
│ Time Information:               │
│ • Current time: [Large display] │
│ • Cutoff time: "5:00 PM"        │
│ • Time remaining: "2h 15m left" │
│ • Gentle urgency indicators     │
│                                 │
│ Button Enhancements:            │
│ • Larger touch targets          │
│ • Clear hover effects           │
│ • Disabled state styling        │
│ • Loading state animations      │
│                                 │
│ Help Text:                      │
│ • "Choose one option below"     │
│ • "You can change it later"     │
│ • "Contact help if problems"    │
└─────────────────────────────────┘
    ↓
[Same interaction processes as Home feature]
    ↓
[Additional Help Features]
    ↓
┌─[Grade 9 Support Features]──────┐
│ Help Button:                    │
│ • "Need help?" button           │
│ • Step-by-step instructions     │
│ • FAQ for common issues         │
│                                 │
│ Error Prevention:               │
│ • Confirm before major actions  │
│ • Clear undo options            │
│ • Simple error messages         │
│                                 │
│ Contact Support:                │
│ • "Having trouble?" link        │
│ • Direct contact to help desk   │
│ • Screenshot assistance         │
│                                 │
│ Accessibility:                  │
│ • High contrast mode available  │
│ • Keyboard navigation support   │
│ • Screen reader friendly        │
│ • Simple language throughout    │
└─────────────────────────────────┘
```

---

## 👤 **COMMON FEATURES - DETAILED INNER PROCESSES**

### 👤 Profile Feature - Complete Process Flow
```
[Click Profile Button]
    ↓
[NavigationToolbar.notifyAction("profile")]
    ↓
[MainDashboard.handleToolbarAction()]
    ↓
[Case: "profile" → showProfile()]
    ↓
[SwingUtilities.invokeLater()]
    ↓
[new MyProfileFrame(authService).setVisible(true)]
    ↓
┌─[MyProfileFrame Constructor]────┐
│ Frame Initialization:           │
│ • Create modal dialog           │
│ • Set appropriate size          │
│ • Center on parent window       │
│ • Apply modern theme            │
│ • Set close operation           │
│                                 │
│ Data Loading:                   │
│ • Get current user from auth    │
│ • Load additional user details  │
│ • Query assigned club info      │
│ • Calculate join duration       │
│ • Get last login information    │
└─────────────────────────────────┘
    ↓
[Profile Data Gathering]
    ↓
┌─[User Information Collection]───┐
│ Basic Information:              │
│ • Full name from User object    │
│ • Username (login ID)           │
│ • Email address                 │
│ • Role (formatted display)      │
│                                 │
│ Additional Database Query:      │
│ SELECT u.*, c.name as club_name,│
│        c.description,           │
│        u.created_date           │
│ FROM users u                    │
│ LEFT JOIN clubs c ON            │
│     u.assigned_club_id = c.id   │
│ WHERE u.id = ?                  │
│                                 │
│ Calculated Fields:              │
│ • Member since: [Date calc]     │
│ • Days active: [Date diff]      │
│ • Account status: Active/Inactive│
│ • Profile completion: X%        │
└─────────────────────────────────┘
    ↓
[Profile UI Layout Creation]
    ↓
┌─[Profile Display Layout]────────┐
│ Header Section:                 │
│ • Profile title with icon       │
│ • User avatar placeholder       │
│ • "View Only" indicator         │
│                                 │
│ Information Grid:               │
│ ┌─[Personal Info]─────────────┐ │
│ │ Full Name: [Name]           │ │
│ │ Username: [Username]        │ │
│ │ Email: [Email]              │ │
│ │ Role: [Formatted Role]      │ │
│ └─────────────────────────────┘ │
│                                 │
│ ┌─[Account Details]───────────┐ │
│ │ Member Since: [Date]        │ │
│ │ Last Login: [DateTime]      │ │
│ │ Account Status: Active      │ │
│ │ Profile ID: [User ID]       │ │
│ └─────────────────────────────┘ │
│                                 │
│ ┌─[Club Information]──────────┐ │
│ │ Assigned Club: [Club Name]  │ │
│ │ Club Description: [Desc]    │ │
│ │ Assignment Date: [Date]     │ │
│ │ (Only if applicable)        │ │
│ └─────────────────────────────┘ │
└─────────────────────────────────┘
    ↓
[Role-Specific Information Display]
    ↓
┌─[Role-Based Content]────────────┐
│ For CLUB_MANAGER:               │
│ • Manager privileges indicator  │
│ • Managed proposals count       │
│ • Last management action        │
│ • Contact information           │
│                                 │
│ For GRADE_11:                   │
│ • Assigned club details         │
│ • Proposal submission history   │
│ • Attendance summary            │
│ • Peer interaction stats        │
│                                 │
│ For GRADE_9:                    │
│ • Simplified information        │
│ • Basic club assignment         │
│ • Attendance streak             │
│ • Encouragement messages        │
└─────────────────────────────────┘
    ↓
[Interactive Elements Setup]
    ↓
┌─[Profile Interaction Features]──┐
│ Read-Only Display:              │
│ • All fields are view-only      │
│ • Clear indication of readonly  │
│ • No edit buttons visible       │
│                                 │
│ Copy-to-Clipboard Features:     │
│ • Click email to copy           │
│ • Click username to copy        │
│ • Visual feedback on copy       │
│                                 │
│ Navigation Actions:             │
│ • [Close] button                │
│ • [Change Password] link        │
│ • [Help] link                   │
│                                 │
│ Visual Enhancements:            │
│ • Hover effects on copyable     │
│ • Tooltips for additional info  │
│ • Icons for each information    │
│   category                      │
└─────────────────────────────────┘
    ↓
[Profile Frame Display]
    ↓
┌─[Final Profile Window]──────────┐
│ Window Properties:              │
│ • Modal (blocks parent)         │
│ • Centered positioning          │
│ • Fixed size (400x500)          │
│ • Professional appearance       │
│                                 │
│ Accessibility Features:         │
│ • Keyboard navigation           │
│ • High contrast readable        │
│ • Screen reader compatible      │
│ • Logical tab order             │
│                                 │
│ Close Actions:                  │
│ • X button in corner            │
│ • Close button at bottom        │
│ • ESC key handler               │
│ • Click outside to close        │
└─────────────────────────────────┘
    ↓
[Status Update: "Profile information displayed"]
    ↓
[Profile Window Ready for User Interaction]
```

### 🔑 Password Feature - Complete Process Flow
```
[Click Password Button]
    ↓
[NavigationToolbar.notifyAction("changepassword")]
    ↓
[MainDashboard.handleToolbarAction()]
    ↓
[Case: "changepassword" → openChangePasswordFrame()]
    ↓
[SwingUtilities.invokeLater()]
    ↓
[new ChangePasswordFrame(authService).setVisible(true)]
    ↓
┌─[ChangePasswordFrame Constructor]┐
│ Security Validation:            │
│ • Verify user is authenticated  │
│ • Check session validity        │
│ • Log password change attempt   │
│                                 │
│ Frame Setup:                    │
│ • Create secure modal dialog    │
│ • Apply security-focused theme  │
│ • Disable screenshot (if possible)│
│ • Set appropriate size          │
│ • Center on parent              │
└─────────────────────────────────┘
    ↓
[Password Form Creation]
    ↓
┌─[Password Change Form Layout]───┐
│ Security Header:                │
│ • "🔐 Change Your Password"     │
│ • Security reminder message     │
│ • Current user indicator        │
│                                 │
│ Form Fields:                    │
│ ┌─[Current Password]──────────┐ │
│ │ • JPasswordField            │ │
│ │ • "Enter current password"   │ │
│ │ • Show/hide toggle           │ │
│ │ • Real-time validation       │ │
│ └─────────────────────────────┘ │
│                                 │
│ ┌─[New Password]──────────────┐ │
│ │ • JPasswordField            │ │
│ │ • "Enter new password"       │ │
│ │ • Strength indicator         │ │
│ │ • Requirements display       │ │
│ └─────────────────────────────┘ │
│                                 │
│ ┌─[Confirm New Password]──────┐ │
│ │ • JPasswordField            │ │
│ │ • "Confirm new password"     │ │
│ │ • Match indicator            │ │
│ │ • Real-time comparison       │ │
│ └─────────────────────────────┘ │
└─────────────────────────────────┘
    ↓
[Password Requirements Display]
    ↓
┌─[Security Requirements Panel]───┐
│ Requirements Header:            │
│ • "Password must contain:"      │
│                                 │
│ Requirement List:               │
│ • ❌ At least 8 characters      │
│ • ❌ One uppercase letter (A-Z) │
│ • ❌ One lowercase letter (a-z) │
│ • ❌ One number (0-9)           │
│ • ❌ One special character      │
│   (!@#$%^&*)                   │
│                                 │
│ Real-time Updates:              │
│ • ✅ changes to green when met  │
│ • Visual progress indicator     │
│ • Overall strength meter        │
│                                 │
│ Strength Levels:                │
│ • 🔴 Weak (0-2 criteria)       │
│ • 🟡 Fair (3 criteria)         │
│ • 🟢 Strong (4+ criteria)      │
│ • 🔵 Excellent (5+ chars)      │
└─────────────────────────────────┘
    ↓
[Real-time Validation Setup]
    ↓
┌─[Field Validation Listeners]────┐
│ Current Password Field:         │
│ • onKeyUp: Clear error message  │
│ • onBlur: Validate immediately  │
│ • Show loading indicator        │
│                                 │
│ New Password Field:             │
│ • onKeyUp: Check all requirements│
│ • Update strength meter         │
│ • Update requirement checklist  │
│ • Compare with confirm field    │
│                                 │
│ Confirm Password Field:         │
│ • onKeyUp: Compare with new     │
│ • Show match/mismatch indicator │
│ • Update submit button state    │
│                                 │
│ Form State Management:          │
│ • Enable submit only when valid │
│ • Clear states on field changes │
│ • Persistent validation errors  │
└─────────────────────────────────┘
    ↓
[User Input Process]
    ↓
┌─[As User Types in Fields]───────┐
│ Current Password Typing:        │
│ • No visual feedback until blur │
│ • Mask characters immediately   │
│ • Clear any previous errors     │
│                                 │
│ New Password Typing:            │
│ • Live character count          │
│ • Requirements check on each    │
│   keystroke                     │
│ • Strength meter updates        │
│ • Color coding for strength     │
│                                 │
│ Example Progress:               │
│ User types: "P"                 │
│ • ❌ Length: 1/8                │
│ • ✅ Uppercase: Yes             │
│ • ❌ Lowercase: No              │
│ • ❌ Number: No                 │
│ • ❌ Special: No                │
│ • Strength: 🔴 Weak             │
│                                 │
│ User types: "Password123!"      │
│ • ✅ Length: 12/8               │
│ • ✅ Uppercase: Yes             │
│ • ✅ Lowercase: Yes             │
│ • ✅ Number: Yes                │
│ • ✅ Special: Yes               │
│ • Strength: 🔵 Excellent        │
└─────────────────────────────────┘
    ↓
[Submit Button Click Process]
    ↓
┌─[Password Change Submission]────┐
│ 1. Pre-submission Validation:   │
│    • All fields filled          │
│    • Current password not empty │
│    • New password meets reqs    │
│    • Passwords match            │
│    • New ≠ current password     │
│                                 │
│ 2. Current Password Verification:│
│    • Get user from auth service │
│    • Hash entered current pwd   │
│    • Compare with stored hash   │
│    • Verify salt matches        │
│                                 │
│ 3. Security Check:              │
│    boolean currentValid =       │
│    PasswordHasher.verifyPassword(│
│        currentPassword,         │
│        user.getPasswordHash(),  │
│        user.getPasswordSalt()   │
│    );                           │
│                                 │
│ 4. If Current Password Wrong:   │
│    • Show error message         │
│    • Clear current pwd field    │
│    • Focus on current pwd       │
│    • Log failed attempt         │
│    • Security delay (1-2 sec)   │
└─────────────────────────────────┘
    ↓
[New Password Processing]
    ↓
┌─[Password Hash Generation]──────┐
│ 1. Generate New Hash:           │
│    PasswordHasher.HashedPassword│
│    newHashedPassword =          │
│    PasswordHasher.hashPassword( │
│        newPassword);            │
│                                 │
│ 2. Extract Components:          │
│    String newHash =             │
│        newHashedPassword        │
│        .getHash();              │
│    String newSalt =             │
│        newHashedPassword        │
│        .getSalt();              │
│                                 │
│ 3. Database Update:             │
│    boolean success =            │
│    userDAO.updatePasswordWithSalt(│
│        user.getId(),            │
│        newHash,                 │
│        newSalt                  │
│    );                           │
│                                 │
│ 4. Update Query:                │
│    UPDATE users                 │
│    SET password_hash = ?,       │
│        password_salt = ?,       │
│        last_password_change =   │
│        NOW()                    │
│    WHERE id = ?                 │
└─────────────────────────────────┘
    ↓
[Success/Failure Handling]
    ↓
┌─[Response Processing]───────────┐
│ If Update Successful:           │
│ • Show success dialog:          │
│   "✅ Password changed          │
│    successfully!"               │
│ • Clear all password fields     │
│ • Log successful change         │
│ • Update last change date       │
│ • Show security tips            │
│ • Auto-close dialog (5 sec)     │
│                                 │
│ If Update Failed:               │
│ • Show error dialog:            │
│   "❌ Failed to update          │
│    password. Please try again." │
│ • Keep form open                │
│ • Log error details             │
│ • Suggest contacting admin      │
│                                 │
│ Security Measures:              │
│ • Force logout after change     │
│   (optional security policy)    │
│ • Send email notification       │
│ • Update security audit log     │
│ • Clear sensitive data          │
└─────────────────────────────────┘
    ↓
[Cleanup & Close]
    ↓
┌─[Form Cleanup Process]──────────┐
│ Sensitive Data Clearing:        │
│ • Clear all password fields     │
│ • Null out password variables   │
│ • Clear clipboard if applicable │
│ • Reset form state              │
│                                 │
│ Security Logging:               │
│ • Log successful password change│
│ • Include timestamp             │
│ • Record user ID (not password) │
│ • Note IP address if available  │
│                                 │
│ UI Cleanup:                     │
│ • Reset all validation states   │
│ • Clear error messages          │
│ • Restore default button states │
│ • Close dialog window           │
│                                 │
│ Return to Main Dashboard:       │
│ • Focus returns to parent       │
│ • Update status message         │
│ • Refresh user session if needed│
└─────────────────────────────────┘
```

This completes the detailed inner process flowcharts for all features across all roles, showing exactly what happens after clicking each feature button, including database operations, UI updates, user interactions, and error handling processes.