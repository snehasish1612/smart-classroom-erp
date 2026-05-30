package com.smartclassroom.erp;

import com.smartclassroom.erp.entity.Attendance;
import com.smartclassroom.erp.entity.Classroom;
import com.smartclassroom.erp.entity.Device;
import com.smartclassroom.erp.entity.Faculty;
import com.smartclassroom.erp.entity.Notification;
import com.smartclassroom.erp.entity.Student;
import com.smartclassroom.erp.entity.Timetable;
import com.smartclassroom.erp.entity.User;
import com.smartclassroom.erp.repository.AttendanceRepository;
import com.smartclassroom.erp.repository.ClassroomRepository;
import com.smartclassroom.erp.repository.DeviceRepository;
import com.smartclassroom.erp.repository.FacultyRepository;
import com.smartclassroom.erp.repository.NotificationRepository;
import com.smartclassroom.erp.repository.StudentRepository;
import com.smartclassroom.erp.repository.TimetableRepository;
import com.smartclassroom.erp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class SeedDatabaseTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void seedSmartClassroomDemoData() {
        User admin = seedUser("Admin User", "admin@smartclassroom.local", "admin123", User.Role.ADMIN);
        seedUser("Student Portal", "student@smartclassroom.local", "student123", User.Role.STUDENT);
        seedUser("Faculty Portal", "faculty@smartclassroom.local", "faculty123", User.Role.Faculty);

        Student sneha = seedStudent("Sneha Das", "sneha.student@smartclassroom.local", "CSE001", "CSE", 5, "9876543210");
        Student rahul = seedStudent("Rahul Sen", "rahul.student@smartclassroom.local", "CSE002", "CSE", 5, "9876543211");
        Student ananya = seedStudent("Ananya Roy", "ananya.student@smartclassroom.local", "ECE001", "ECE", 3, "9876543212");

        Faculty arjun = seedFaculty("Dr. Arjun Mehta", "arjun.faculty@smartclassroom.local", "CSE", "9876543220", "Professor", "Data Structures");
        Faculty nisha = seedFaculty("Prof. Nisha Rao", "nisha.faculty@smartclassroom.local", "ECE", "9876543221", "Assistant Professor", "Digital Electronics");

        Classroom cseLab = seedClassroom("A-101", "Academic Block A", 60, Classroom.Status.AVAILABLE);
        Classroom seminarHall = seedClassroom("B-204", "Academic Block B", 45, Classroom.Status.OCCUPIED);

        seedDevice("Projector", "OFF", cseLab.getId());
        seedDevice("SmartBoard", "ON", cseLab.getId());
        seedDevice("AC", "ON", seminarHall.getId());
        seedDevice("Lights", "ON", seminarHall.getId());

        seedTimetable("Data Structures", arjun, cseLab, Timetable.Day.MONDAY, "09:00", "10:00", 5, "CSE");
        seedTimetable("Database Systems", arjun, cseLab, Timetable.Day.TUESDAY, "10:00", "11:00", 5, "CSE");
        seedTimetable("Digital Electronics", nisha, seminarHall, Timetable.Day.WEDNESDAY, "11:00", "12:00", 3, "ECE");

        seedAttendance(sneha, arjun, "Data Structures", LocalDate.now().minusDays(2), Attendance.Status.PRESENT);
        seedAttendance(rahul, arjun, "Data Structures", LocalDate.now().minusDays(2), Attendance.Status.ABSENT);
        seedAttendance(ananya, nisha, "Digital Electronics", LocalDate.now().minusDays(1), Attendance.Status.PRESENT);

        seedNotification(admin, "Welcome to Smart Classroom ERP", "Demo data is ready for students, faculty, classrooms, timetable, attendance, and devices.", Notification.TargetRole.ALL);
        seedNotification(admin, "Attendance reminder", "Faculty can mark and update attendance from the Attendance screen.", Notification.TargetRole.FACULTY);

        assertFalse(userRepository.findAll().isEmpty());
        assertFalse(studentRepository.findAll().isEmpty());
        assertFalse(facultyRepository.findAll().isEmpty());
        assertFalse(classroomRepository.findAll().isEmpty());
        assertFalse(deviceRepository.findAll().isEmpty());
        assertFalse(timetableRepository.findAll().isEmpty());
        assertFalse(attendanceRepository.findAll().isEmpty());
        assertFalse(notificationRepository.findAll().isEmpty());
    }

    private User seedUser(String name, String email, String password, User.Role role) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole(role);
            return userRepository.save(user);
        });
    }

    private Student seedStudent(String name, String email, String rollNumber, String department, Integer semester, String phone) {
        return studentRepository.findByRollNumber(rollNumber).orElseGet(() -> {
            Student student = new Student();
            student.setName(name);
            student.setEmail(email);
            student.setRollNumber(rollNumber);
            student.setDepartment(department);
            student.setSemester(semester);
            student.setPhone(phone);
            return studentRepository.save(student);
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

    private Timetable seedTimetable(String subject, Faculty faculty, Classroom classroom, Timetable.Day day,
                                    String startTime, String endTime, Integer semester, String department) {
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        return timetableRepository.findAll().stream()
                .filter(slot -> subject.equals(slot.getSubject())
                        && day == slot.getDay()
                        && start.equals(slot.getStartTime())
                        && department.equals(slot.getDepartment()))
                .findFirst()
                .orElseGet(() -> {
                    Timetable timetable = new Timetable();
                    timetable.setSubject(subject);
                    timetable.setFaculty(faculty);
                    timetable.setClassroom(classroom);
                    timetable.setDay(day);
                    timetable.setStartTime(start);
                    timetable.setEndTime(end);
                    timetable.setSemester(semester);
                    timetable.setDepartment(department);
                    return timetableRepository.save(timetable);
                });
    }

    private Attendance seedAttendance(Student student, Faculty faculty, String subject, LocalDate date, Attendance.Status status) {
        return attendanceRepository.findByStudentAndSubject(student, subject).stream()
                .filter(attendance -> date.equals(attendance.getDate()))
                .findFirst()
                .orElseGet(() -> {
                    Attendance attendance = new Attendance();
                    attendance.setStudent(student);
                    attendance.setFaculty(faculty);
                    attendance.setSubject(subject);
                    attendance.setDate(date);
                    attendance.setStatus(status);
                    return attendanceRepository.save(attendance);
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
}
