package com.smartclassroom.erp.config;

import com.smartclassroom.erp.entity.Assignment;
import com.smartclassroom.erp.entity.Classroom;
import com.smartclassroom.erp.entity.Device;
import com.smartclassroom.erp.entity.Faculty;
import com.smartclassroom.erp.entity.Notification;
import com.smartclassroom.erp.entity.Note;
import com.smartclassroom.erp.entity.Section;
import com.smartclassroom.erp.entity.Student;
import com.smartclassroom.erp.entity.Stream;
import com.smartclassroom.erp.entity.Timetable;
import com.smartclassroom.erp.entity.User;
import com.smartclassroom.erp.repository.AssignmentRepository;
import com.smartclassroom.erp.repository.ClassroomRepository;
import com.smartclassroom.erp.repository.DeviceRepository;
import com.smartclassroom.erp.repository.FacultyRepository;
import com.smartclassroom.erp.repository.NotificationRepository;
import com.smartclassroom.erp.repository.NoteRepository;
import com.smartclassroom.erp.repository.SectionRepository;
import com.smartclassroom.erp.repository.StreamRepository;
import com.smartclassroom.erp.repository.StudentRepository;
import com.smartclassroom.erp.repository.TimetableRepository;
import com.smartclassroom.erp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DemoDataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final StreamRepository streamRepository;
    private final SectionRepository sectionRepository;
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;
    private final TimetableRepository timetableRepository;
    private final ClassroomRepository classroomRepository;
    private final DeviceRepository deviceRepository;
    private final AssignmentRepository assignmentRepository;
    private final NotificationRepository notificationRepository;
    private final NoteRepository noteRepository;

    public DemoDataInitializer(
            JdbcTemplate jdbcTemplate,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            StreamRepository streamRepository,
            SectionRepository sectionRepository,
            FacultyRepository facultyRepository,
            StudentRepository studentRepository,
            TimetableRepository timetableRepository,
            ClassroomRepository classroomRepository,
            DeviceRepository deviceRepository,
            AssignmentRepository assignmentRepository,
            NotificationRepository notificationRepository,
            NoteRepository noteRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.streamRepository = streamRepository;
        this.sectionRepository = sectionRepository;
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
        this.timetableRepository = timetableRepository;
        this.classroomRepository = classroomRepository;
        this.deviceRepository = deviceRepository;
        this.assignmentRepository = assignmentRepository;
        this.notificationRepository = notificationRepository;
        this.noteRepository = noteRepository;
    }

    @Override
    public void run(String... args) {
        repairStudentSchemaDefaults();
        jdbcTemplate.update("UPDATE users SET role = 'FACULTY' WHERE role = 'Faculty'");

        Stream cse = seedStream("CSE", "Computer Science and Engineering demo stream");
        Section cseA = seedSection("CSE-A", 8, cse);
        repairBrokenForeignKeys(cse, cseA);

        User admin = seedUser("Admin User", "admin@smartclassroom.local", "admin123", User.Role.ADMIN);
        seedUser("Student Portal", "student@smartclassroom.local", "student123", User.Role.STUDENT);
        seedUser("Teacher Portal", "teacher@smartclassroom.local", "teacher123", User.Role.FACULTY);
        seedUser("Rahul Roy", "rahul@student.local", "student123", User.Role.STUDENT);
        User teacherUser = seedUser("Dr. Ananya Sen", "ananya@faculty.local", "faculty123", User.Role.FACULTY);

        Faculty ananya = seedFaculty(
            "Dr. Ananya Sen",
            "ananya@faculty.local",
            "CSE",
            "9876543201",
            "Assistant Professor",
            "DBMS, Java, Operating Systems");
        seedFaculty(
            "Teacher Portal",
            "teacher@smartclassroom.local",
            "CSE",
            "9876543299",
            "Assistant Professor",
            "ERP, Web Technology");
        Faculty arindam = seedFaculty(
            "Prof. Arindam Das",
            "arindam@faculty.local",
            "CSE",
            "9876543202",
            "Professor",
            "Computer Networks, Software Engineering");

        Classroom lab301 = seedClassroom("Lab 301", "Academic Block", 60, Classroom.Status.AVAILABLE);
        Classroom room204 = seedClassroom("Room 204", "Academic Block", 50, Classroom.Status.AVAILABLE);
        Classroom room205 = seedClassroom("Room 205", "Academic Block", 50, Classroom.Status.AVAILABLE);
        Classroom lab302 = seedClassroom("Lab 302", "Academic Block", 60, Classroom.Status.AVAILABLE);
        repairTimetableClassrooms(lab301);

        seedStudent("Student Portal", "student@smartclassroom.local", "CSE-8-000", "9874574400", cseA);
        seedStudent("Rahul Roy", "rahul@student.local", "CSE-8-001", "9874574415", cseA);

        seedTimetable("Java", ananya, cse, lab301, Timetable.Day.THURSDAY, "09:00", "10:00", 8);
        seedTimetable("DBMS", ananya, cse, room204, Timetable.Day.THURSDAY, "10:15", "11:15", 8);
        seedTimetable("Computer Networks", arindam, cse, room205, Timetable.Day.THURSDAY, "11:30", "12:30", 8);
        seedTimetable("Operating Systems", ananya, cse, lab302, Timetable.Day.THURSDAY, "14:00", "15:00", 8);
        seedTimetable("Software Engineering", arindam, cse, room204, Timetable.Day.FRIDAY, "09:00", "10:00", 8);

        seedDevice("Projector", "ON", lab301.getId());
        seedDevice("SmartBoard", "ON", lab301.getId());
        seedDevice("AC", "OFF", room204.getId());
        seedDevice("Lights", "ON", room205.getId());

        seedAssignment("DBMS ER Diagram", "DBMS", "Draw an ER diagram for a college ERP module.", LocalDate.now().plusDays(3), ananya, cseA);
        seedAssignment("Java OOP Lab", "Java", "Submit the inheritance and interface lab work.", LocalDate.now().plusDays(5), ananya, cseA);
        seedNote("Smart Classroom ERP Overview", "ERP", teacherUser);
        seedNote("JWT Authentication Flow", "Web Technology", teacherUser);

        seedNotification(admin, "Demo routine ready", "Today routine data has been seeded for CSE semester 8.", Notification.TargetRole.ALL);
    }

    private Stream seedStream(String name, String description) {
        return streamRepository.findByName(name).orElseGet(() -> {
            Stream stream = new Stream();
            stream.setName(name);
            stream.setDescription(description);
            return streamRepository.save(stream);
        });
    }

    private Section seedSection(String name, Integer semester, Stream stream) {
        return sectionRepository.findByStreamAndSemester(stream, semester)
            .stream()
            .filter(section -> name.equals(section.getName()))
            .findFirst()
            .orElseGet(() -> {
                Section section = new Section();
                section.setName(name);
                section.setSemester(semester);
                section.setStream(stream);
                return sectionRepository.save(section);
            });
    }

    private void repairBrokenForeignKeys(Stream stream, Section section) {
        jdbcTemplate.update("UPDATE students SET section_id = ? WHERE section_id IS NULL OR section_id = 0", section.getId());
        jdbcTemplate.update("UPDATE students SET department = ? WHERE department IS NULL OR department = ''", stream.getName());
        jdbcTemplate.update("UPDATE students SET semester = ? WHERE semester IS NULL OR semester = 0", section.getSemester());
        jdbcTemplate.update("UPDATE attendance SET section_id = ? WHERE section_id IS NULL OR section_id = 0", section.getId());
        jdbcTemplate.update("UPDATE timetable SET stream_id = ? WHERE stream_id IS NULL OR stream_id = 0", stream.getId());
        jdbcTemplate.update("UPDATE timetable SET department = ? WHERE department IS NULL OR department = ''", stream.getName());
        jdbcTemplate.update("UPDATE assignments SET section_id = ? WHERE section_id = 0", section.getId());
    }

    private void repairStudentSchemaDefaults() {
        try {
            jdbcTemplate.execute("ALTER TABLE students MODIFY department VARCHAR(255) NOT NULL DEFAULT 'CSE'");
            jdbcTemplate.execute("ALTER TABLE students MODIFY semester INT NOT NULL DEFAULT 1");
        } catch (RuntimeException exception) {
            System.err.println("Could not apply student schema defaults: " + exception.getMessage());
        }
    }

    private void repairTimetableClassrooms(Classroom classroom) {
        jdbcTemplate.update("UPDATE timetable SET classroom_id = ? WHERE classroom_id IS NULL OR classroom_id = 0", classroom.getId());
    }

    private User seedUser(String name, String email, String password, User.Role role) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            return userRepository.save(user);
        });
    }

    private Faculty seedFaculty(String name, String email, String department, String phone, String designation, String subjectsTaught) {
        return facultyRepository.findByEmail(email).orElseGet(() -> {
            Faculty faculty = new Faculty();
            faculty.setName(name);
            faculty.setEmail(email);
            faculty.setDepartment(department);
            faculty.setPhone(phone);
            faculty.setDesignation(designation);
            faculty.setSubjectsTaught(subjectsTaught);
            return facultyRepository.save(faculty);
        });
    }

    private Student seedStudent(String name, String email, String rollNumber, String phone, Section section) {
        return studentRepository.findByEmail(email).orElseGet(() -> {
            Student student = new Student();
            student.setName(name);
            student.setEmail(email);
            student.setRollNumber(rollNumber);
            student.setDepartment("CSE");
            student.setSemester(section.getSemester());
            student.setPhone(phone);
            student.setSection(section);
            return studentRepository.save(student);
        });
    }

    private Classroom seedClassroom(String roomNumber, String building, Integer capacity, Classroom.Status status) {
        return classroomRepository.findByRoomNumber(roomNumber).orElseGet(() -> {
            Classroom classroom = new Classroom();
            classroom.setRoomNumber(roomNumber);
            classroom.setBuilding(building);
            classroom.setCapacity(capacity);
            classroom.setStatus(status);
            return classroomRepository.save(classroom);
        });
    }

    private Timetable seedTimetable(
            String subject,
            Faculty faculty,
            Stream stream,
            Classroom classroom,
            Timetable.Day day,
            String startTime,
            String endTime,
            Integer semester) {
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        return timetableRepository.findAll().stream()
            .filter(slot -> subject.equals(slot.getSubject())
                && day == slot.getDay()
                && start.equals(slot.getStartTime())
                && semester.equals(slot.getSemester()))
            .findFirst()
            .orElseGet(() -> {
                Timetable timetable = new Timetable();
                timetable.setSubject(subject);
                timetable.setFaculty(faculty);
                timetable.setStream(stream);
                timetable.setClassroom(classroom.getRoomNumber());
                timetable.setClassroomId(classroom.getId());
                timetable.setDay(day);
                timetable.setStartTime(start);
                timetable.setEndTime(end);
                timetable.setSemester(semester);
                timetable.setDepartment(stream.getName());
                return timetableRepository.save(timetable);
            });
    }

    private Device seedDevice(String deviceName, String status, Long classroomId) {
        return deviceRepository.findByClassroomId(classroomId).stream()
            .filter(device -> deviceName.equals(device.getDeviceName()))
            .findFirst()
            .orElseGet(() -> {
                Device device = new Device();
                device.setDeviceName(deviceName);
                device.setStatus(status);
                device.setClassroomId(classroomId);
                return deviceRepository.save(device);
            });
    }

    private Assignment seedAssignment(String title, String subject, String description, LocalDate dueDate, Faculty faculty, Section section) {
        return assignmentRepository.findAll().stream()
            .filter(assignment -> title.equals(assignment.getTitle()))
            .findFirst()
            .orElseGet(() -> {
                Assignment assignment = new Assignment();
                assignment.setTitle(title);
                assignment.setSubject(subject);
                assignment.setDescription(description);
                assignment.setDueDate(dueDate);
                assignment.setFaculty(faculty);
                assignment.setSection(section);
                return assignmentRepository.save(assignment);
            });
    }

    private Notification seedNotification(User sentBy, String title, String message, Notification.TargetRole targetRole) {
        return notificationRepository.findAll().stream()
            .filter(notification -> title.equals(notification.getTitle()))
            .findFirst()
            .orElseGet(() -> {
                Notification notification = new Notification();
                notification.setSentBy(sentBy);
                notification.setTitle(title);
                notification.setMessage(message);
                notification.setTargetRole(targetRole);
                notification.setIsRead(false);
                return notificationRepository.save(notification);
            });
    }

    private Note seedNote(String topic, String subject, User createdBy) {
        return noteRepository.findAll().stream()
            .filter(note -> topic.equals(note.getTopic()) && subject.equals(note.getSubject()))
            .findFirst()
            .orElseGet(() -> {
                Note note = new Note();
                note.setTopic(topic);
                note.setSubject(subject);
                note.setCreatedBy(createdBy);
                note.setContent("AI Generated Study Notes\n\nSubject: " + subject + "\nTopic: " + topic
                    + "\n\nOverview:\nThis note supports the Smart Classroom ERP report demo. It contains structured content for students to view and download from the Notes module.\n\nKey Points:\n- Centralized academic data management\n- Role-based dashboards for admin, teacher, and student\n- Attendance, assignment, routine, notification, and notes workflows\n- MySQL-backed persistence through Spring Boot REST APIs\n\nSummary:\nThe module demonstrates AI-assisted notes generation and instant student access.");
                return noteRepository.save(note);
            });
    }
}
