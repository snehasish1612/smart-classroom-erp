import React, { useCallback, useEffect, useMemo, useState } from "react";
import { api, getStoredSession } from "../api";
import Sidebar from "../components/layout/Sidebar";
import Header from "../components/layout/Header";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import UpcomingClasses from "../components/classes/UpcomingClasses";
import AttendanceCard from "../components/cards/AttendanceCard";
import CalendarCard from "../components/cards/CalendarCard";
import TimeCard from "../components/cards/TimeCard";
import OngoingClasses from "../components/classes/OngoingClasses";
import ExtraclassesCard from "../components/cards/ExtraclassesCard";


const days = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"];

const getMinutes = (time) => {
  if (!time) return 0;
  const [hours, minutes] = time.split(":").map(Number);
  return hours * 60 + minutes;
};

const formatTime = (time) => {
  if (!time) return "TBA";
  const [hours, minutes] = time.split(":").map(Number);
  const date = new Date();
  date.setHours(hours, minutes, 0, 0);
  return date.toLocaleTimeString([], { hour: "numeric", minute: "2-digit" });
};

const getClassStatus = (item) => {
  const now = new Date();
  const currentMinutes = now.getHours() * 60 + now.getMinutes();
  const start = getMinutes(item.startTime);
  const end = getMinutes(item.endTime);

  if (currentMinutes >= start && currentMinutes <= end) return "Live";
  return currentMinutes < start ? "Upcoming" : "Completed";
};

const toRole = (role) => {
  const normalizedRole = String(role || "").toUpperCase();
  return normalizedRole === "FACULTY" ? "FACULTY" : normalizedRole === "ADMIN" ? "ALL" : "STUDENT";
};

const normalizeUserRole = (role) => {
  const normalizedRole = String(role || "").toUpperCase();
  if (normalizedRole === "ADMIN" || normalizedRole === "FACULTY") return normalizedRole;
  return "STUDENT";
};

const todayDay = () =>
  new Date().toLocaleDateString("en-US", { weekday: "long" }).toUpperCase();

const Panel = ({ title, children, action }) => (
  <section className="bg-white rounded-lg p-4 shadow-sm border border-slate-100">
    <div className="flex items-center justify-between gap-3 mb-4">
      <h2 className="font-semibold text-slate-900">{title}</h2>
      {action}
    </div>
    {children}
  </section>
);

const TodayTimetable = ({ classes = [], isLoading }) => (
  <Panel title="Today's Timetable">
    <div className="overflow-x-auto">
      <table className="w-full min-w-full sm:min-w-[680px] text-left text-sm">
        <thead>
          <tr className="border-b border-slate-200 text-xs uppercase text-slate-500">
            <th className="py-2 pr-3">Time</th>
            <th className="py-2 pr-3">Subject</th>
            <th className="py-2 pr-3">Faculty</th>
            <th className="py-2 pr-3">Room</th>
            <th className="py-2 pr-3">Status</th>
          </tr>
        </thead>
        <tbody>
          {isLoading && (
            <tr>
              <td className="py-4 text-slate-500" colSpan="5">Loading today's timetable...</td>
            </tr>
          )}

          {!isLoading && classes.length === 0 && (
            <tr>
              <td className="py-4 text-slate-500" colSpan="5">No classes scheduled for today.</td>
            </tr>
          )}

          {!isLoading && classes.map((item) => (
            <tr key={item.id} className="border-b border-slate-100 last:border-0">
              <td className="py-3 pr-3 font-medium text-slate-800">{item.startTime} - {item.endTime}</td>
              <td className="py-3 pr-3">{item.subject}</td>
              <td className="py-3 pr-3 text-slate-600">{item.faculty}</td>
              <td className="py-3 pr-3 text-slate-600">{item.room}</td>
              <td className="py-3 pr-3">
                <span className={`rounded-full px-2 py-1 text-xs font-semibold ${item.status === "Live"
                  ? "bg-green-100 text-green-700"
                  : item.status === "Upcoming"
                    ? "bg-yellow-100 text-yellow-700"
                    : "bg-slate-200 text-slate-600"
                  }`}>
                  {item.status}
                </span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  </Panel>
);

const Field = ({ label, ...props }) => (
  <label className="text-xs font-medium text-slate-600">
    {label}
    <input
      {...props}
      className="mt-1 w-full rounded-md border border-slate-200 px-3 py-2 text-sm outline-none focus:border-blue-500"
    />
  </label>
);

const SelectField = ({ label, children, ...props }) => (
  <label className="text-xs font-medium text-slate-600">
    {label}
    <select
      {...props}
      className="mt-1 w-full rounded-md border border-slate-200 px-3 py-2 text-sm outline-none focus:border-blue-500"
    >
      {children}
    </select>
  </label>
);

const Dashboard = ({ user, onLogout }) => {
  const userRole = normalizeUserRole(user?.role);
  const userEmail = user?.email;
  const userId = user?.id;
  const [data, setData] = useState({
    timetable: [],
    attendance: [],
    assignments: [],
    notes: [],
    devices: [],
    notifications: [],
    users: [],
    students: [],
    faculty: [],
    streams: [],
    sections: [],
  });
  const [conversation, setConversation] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const [activeSection, setActiveSection] = useState("dashboard");
  const [selectedClassId, setSelectedClassId] = useState("");
  const [selectedChatUserId, setSelectedChatUserId] = useState("");
  const [message, setMessage] = useState("");
  const [submissionText, setSubmissionText] = useState("");
  const [selectedAssignmentId, setSelectedAssignmentId] = useState("");
  const [routineForm, setRoutineForm] = useState({
    streamId: "",
    subject: "",
    facultyId: "",
    classroom: "",
    day: todayDay(),
    startTime: "09:00",
    endTime: "10:00",
    semester: 1,
  });
  const [assignmentForm, setAssignmentForm] = useState({
    title: "",
    subject: "",
    description: "",
    dueDate: "",
    sectionId: "",
  });
  const [noteForm, setNoteForm] = useState({
    subject: "",
    topic: "",
  });
  const [adminTab, setAdminTab] = useState("users");
  const [adminUserForm, setAdminUserForm] = useState({
    id: null,
    name: "",
    email: "",
    role: "STUDENT",
    password: "",
  });
  const [editingUserId, setEditingUserId] = useState(null);
  const [adminTimetableForm, setAdminTimetableForm] = useState({
    streamId: "",
    subject: "",
    facultyId: "",
    classroom: "",
    day: todayDay(),
    startTime: "09:00",
    endTime: "10:00",
    semester: 1,
  });
  const [editingTimetableId, setEditingTimetableId] = useState(null);

  const loadDashboard = useCallback(() => {
    setIsLoading(true);
    setError("");

    const requests = {
      timetable: api.getTimetable(),
      attendance: api.getAttendance(),
      assignments: api.getAssignments(),
      notes: api.getNotes(),
      devices: api.getDevices(),
      notifications: api.getNotifications(toRole(userRole)),
      users: api.getUsers(),
      students: api.getStudents(),
      faculty: api.getFaculty(),
      streams: api.getStreams(),
      sections: api.getSections(),
    };

    Promise.allSettled(Object.entries(requests).map(async ([key, promise]) => [key, await promise]))
      .then((results) => {
        const nextData = {
          timetable: [],
          attendance: [],
          assignments: [],
          notes: [],
          devices: [],
          notifications: [],
          users: [],
          students: [],
          faculty: [],
          streams: [],
          sections: [],
        };
        const failures = [];

        results.forEach((result) => {
          if (result.status === "fulfilled") {
            const [key, value] = result.value;
            nextData[key] = Array.isArray(value) ? value : [];
            return;
          }

          failures.push(result.reason?.message || "A dashboard request failed");
        });

        setData({
          ...nextData,
        });

        if (failures.length > 0) {
          setError([...new Set(failures)].join(" "));
        }
      })
      .catch((dashboardError) => setError(dashboardError.message))
      .finally(() => setIsLoading(false));
  }, [userRole]);

  useEffect(() => {
    const timer = window.setTimeout(loadDashboard, 0);
    return () => window.clearTimeout(timer);
  }, [loadDashboard]);

  const currentStudent = data.students.find((item) => item.email === userEmail) || data.students[0];
  const currentFaculty = data.faculty.find((item) => item.email === userEmail) || data.faculty[0];

  const classes = useMemo(
    () =>
      data.timetable
        .map((item) => ({
          id: item.id,
          subject: item.subject,
          faculty: item.faculty?.name || "Faculty TBA",
          facultyId: item.faculty?.id,
          streamId: item.stream?.id,
          room: item.classroom || "Room TBA",
          day: item.day,
          startTimeRaw: item.startTime,
          endTimeRaw: item.endTime,
          startTime: formatTime(item.startTime),
          endTime: formatTime(item.endTime),
          startMinutes: getMinutes(item.startTime),
          status: getClassStatus(item),
        }))
        .sort((first, second) => first.startMinutes - second.startMinutes),
    [data.timetable],
  );

  const selectedClass = classes.find((item) => String(item.id) === String(selectedClassId)) || classes[0];
  const presentCount = data.attendance.filter((item) => item.status === "PRESENT").length;
  const attendancePercent = data.attendance.length
    ? Math.round((presentCount / data.attendance.length) * 100)
    : 0;
  const unreadCount = data.notifications.filter((item) => !item.isRead).length;
  const onlineDevices = data.devices.filter((item) => item.status === "ON").length;
  const todayClasses = classes.filter((item) => item.day === todayDay());
  const dashboardClasses = userRole === "STUDENT" ? todayClasses : classes;
  const visibleClasses = dashboardClasses.filter((item) => item.status !== "Completed").slice(0, 4);
  const upcomingClasses = todayClasses.filter((item) => item.status === "Upcoming").slice(0, 3);
  const chatTargets = data.users.filter((item) => {
    const targetRole = normalizeUserRole(item.role);

    if (item.id === userId) return false;
    if (userRole === "STUDENT") return targetRole === "FACULTY";
    if (userRole === "FACULTY") return targetRole === "STUDENT";
    return true;
  });
  const teacherClasses = currentFaculty
    ? classes.filter((item) => item.facultyId === currentFaculty.id)
    : classes;

  const withAction = async (action, successMessage) => {
    setError("");
    setNotice("");
    try {
      await action();
      setNotice(successMessage);
      loadDashboard();
    } catch (actionError) {
      setError(actionError.message);
    }
  };

  const markAttendance = () => {
    if (!navigator.geolocation) {
      setError("Geolocation is not supported by this browser.");
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        withAction(
          () =>
            api.markAttendanceByLocation({
              studentId: currentStudent?.id || 1,
              facultyId: selectedClass?.facultyId || currentFaculty?.id || 1,
              sectionId: currentStudent?.section?.id || data.sections[0]?.id || 1,
              subject: selectedClass?.subject || "General",
              latitude: position.coords.latitude,
              longitude: position.coords.longitude,
            }),
          "Attendance marked with your current latitude and longitude.",
        );
      },
      () => setError("Location permission was denied."),
    );
  };

  const saveRoutine = (event) => {
    event.preventDefault();
    const faculty = data.faculty.find((item) => String(item.id) === String(routineForm.facultyId)) || currentFaculty;
    const streamId = routineForm.streamId || data.streams[0]?.id;

    if (!faculty?.id) {
      setError("Please add at least one faculty record before saving routine.");
      return;
    }

    if (!streamId) {
      setError("Please add at least one stream before saving routine.");
      return;
    }

    withAction(
      () =>
        api.createTimetable(streamId, {
          subject: routineForm.subject,
          faculty,
          classroom: routineForm.classroom,
          day: routineForm.day,
          startTime: routineForm.startTime,
          endTime: routineForm.endTime,
          semester: Number(routineForm.semester),
        }),
      "Routine updated successfully.",
    );
  };

  const resetAdminUserForm = () => {
    setEditingUserId(null);
    setAdminUserForm({ id: null, name: "", email: "", role: "STUDENT", password: "" });
  };

  const saveAdminUser = async (event) => {
    event.preventDefault();
    setError("");
    setNotice("");

    if (!adminUserForm.name || !adminUserForm.email) {
      setError("Name and email are required.");
      return;
    }

    if (!editingUserId && !adminUserForm.password) {
      setError("Password is required for new users.");
      return;
    }

    const payload = {
      name: adminUserForm.name,
      email: adminUserForm.email,
      role: adminUserForm.role,
      password: adminUserForm.password || undefined,
    };

    await withAction(
      () =>
        editingUserId
          ? api.updateUser(editingUserId, payload)
          : api.createUser(payload),
      editingUserId ? "User updated successfully." : "User created successfully.",
    );

    resetAdminUserForm();
  };

  const editAdminUser = (user) => {
    setAdminTab("users");
    setEditingUserId(user.id);
    setAdminUserForm({
      id: user.id,
      name: user.name,
      email: user.email,
      role: user.role,
      password: "",
    });
  };

  const removeAdminUser = (userId) => {
    withAction(() => api.deleteUser(userId), "User deleted successfully.");
  };

  const editAdminTimetable = (item) => {
    setAdminTab("timetables");
    setEditingTimetableId(item.id);
    setAdminTimetableForm({
      streamId: item.stream?.id || "",
      subject: item.subject || "",
      facultyId: item.faculty?.id || "",
      classroom: item.classroom || item.room || "",
      day: item.day || todayDay(),
      startTime: item.startTime || item.startTimeRaw || "09:00",
      endTime: item.endTime || item.endTimeRaw || "10:00",
      semester: item.semester || 1,
    });
  };

  const saveAdminTimetable = async (event) => {
    event.preventDefault();
    setError("");
    setNotice("");

    const faculty = data.faculty.find((item) => String(item.id) === String(adminTimetableForm.facultyId));
    const streamId = adminTimetableForm.streamId || data.streams[0]?.id;

    if (!faculty?.id) {
      setError("Please select a faculty for the timetable entry.");
      return;
    }

    if (!streamId) {
      setError("Please select a stream for the timetable entry.");
      return;
    }

    await withAction(
      () =>
        editingTimetableId
          ? api.updateTimetable(editingTimetableId, {
              subject: adminTimetableForm.subject,
              faculty,
              classroom: adminTimetableForm.classroom,
              day: adminTimetableForm.day,
              startTime: adminTimetableForm.startTime,
              endTime: adminTimetableForm.endTime,
              semester: Number(adminTimetableForm.semester),
            })
          : api.createTimetable(streamId, {
              subject: adminTimetableForm.subject,
              faculty,
              classroom: adminTimetableForm.classroom,
              day: adminTimetableForm.day,
              startTime: adminTimetableForm.startTime,
              endTime: adminTimetableForm.endTime,
              semester: Number(adminTimetableForm.semester),
            }),
      editingTimetableId ? "Timetable updated successfully." : "Timetable created successfully.",
    );

    setEditingTimetableId(null);
    setAdminTimetableForm({
      streamId: "",
      subject: "",
      facultyId: "",
      classroom: "",
      day: todayDay(),
      startTime: "09:00",
      endTime: "10:00",
      semester: 1,
    });
  };

  const deleteAdminTimetable = (id) => {
    withAction(() => api.deleteTimetable(id), "Timetable deleted successfully.");
  };

  const createAssignment = (event) => {
    event.preventDefault();
    withAction(
      () =>
        api.createAssignment({
          ...assignmentForm,
          facultyId: currentFaculty?.id || data.faculty[0]?.id || 1,
          sectionId: assignmentForm.sectionId || null,
        }),
      "Assignment created successfully.",
    );
    setAssignmentForm({ title: "", subject: "", description: "", dueDate: "", sectionId: "" });
  };

  const submitAssignment = (event) => {
    event.preventDefault();
    const assignmentId = selectedAssignmentId || data.assignments[0]?.id;
    if (!assignmentId) {
      setError("Please select an assignment before submitting.");
      return;
    }

    withAction(
      () =>
        api.submitAssignment(assignmentId, {
          studentId: currentStudent?.id || 1,
          content: submissionText,
        }),
      "Assignment submitted successfully.",
    );
    setSubmissionText("");
  };

  const generateNote = (event) => {
    event.preventDefault();
    withAction(
      () =>
        api.generateNote({
          subject: noteForm.subject,
          topic: noteForm.topic,
          createdByUserId: userId,
        }),
      "AI notes generated and published for students.",
    );
    setNoteForm({ subject: "", topic: "" });
  };

  const downloadNote = async (note) => {
    try {
      const content = await api.downloadNote(note.id);
      const blob = new Blob([content], { type: "text/plain" });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = `${note.subject}-${note.topic}.txt`.replace(/[^a-z0-9_.-]/gi, "_");
      link.click();
      window.URL.revokeObjectURL(url);
    } catch (noteError) {
      setError(noteError.message);
    }
  };

  const sendMessage = (event) => {
    event.preventDefault();
    const receiverId = selectedChatUserId || chatTargets[0]?.id;
    if (!receiverId) {
      setError(userRole === "STUDENT" ? "No faculty users are available to chat with." : "No chat users are available.");
      return;
    }

    withAction(
      async () => {
        await api.sendMessage({ senderId: userId, receiverId, message });
        const messages = await api.getConversation(userId, receiverId);
        setConversation(Array.isArray(messages) ? messages : []);
      },
      "Message sent.",
    );
    setMessage("");
  };

  const loadConversation = (receiverId) => {
    setSelectedChatUserId(receiverId);
    if (!receiverId) {
      setConversation([]);
      return;
    }

    api.getConversation(userId, receiverId)
      .then((messages) => setConversation(Array.isArray(messages) ? messages : []))
      .catch((chatError) => setError(chatError.message));
  };

  // Poll selected conversation when messages view is active
  useEffect(() => {
    if (activeSection !== "messages" || !selectedChatUserId) return;

    let mounted = true;
    const fetchConv = () => {
      api.getConversation(userId, selectedChatUserId)
        .then((messages) => {
          if (!mounted) return;
          setConversation(Array.isArray(messages) ? messages : []);
        })
        .catch(() => { });
    };

    fetchConv();
    const id = window.setInterval(fetchConv, 5000);
    return () => {
      mounted = false;
      window.clearInterval(id);
    };
  }, [activeSection, selectedChatUserId, userId]);

  // Poll notifications to surface incoming message notifications
  useEffect(() => {
    let mounted = true;
    const fetchNotifs = () => {
      api.getNotifications(toRole(userRole))
        .then((notifs) => {
          if (!mounted) return;
          setData((prev) => ({ ...prev, notifications: Array.isArray(notifs) ? notifs : [] }));
        })
        .catch(() => { });
    };

    fetchNotifs();
    const nid = window.setInterval(fetchNotifs, 15000);
    return () => {
      mounted = false;
      window.clearInterval(nid);
    };
  }, [userRole]);

  // WebSocket: connect to receive real-time messages and notifications
  useEffect(() => {
    if (!userId) return;

    const session = getStoredSession();
    const token = session?.token;

    const stomp = new Client({
      webSocketFactory: () => new SockJS('/ws'),
      reconnectDelay: 5000,
      connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
    });

    stomp.onConnect = (frame) => {
      try {
        // subscribe to personal message queue (use /user/queue/messages so broker maps by Principal)
        stomp.subscribe(`/user/queue/messages`, (msg) => {
          try {
            const payload = JSON.parse(msg.body);
            // if conversation with sender is open, append, else add a notification
            if (String(selectedChatUserId) === String(payload.sender?.id) && activeSection === 'messages') {
              setConversation((prev) => [...prev, payload]);
            } else {
              // create a notification-shaped object for incoming chat messages so UI can show "Open chat"
              const chatNotif = {
                id: null,
                title: `New message from ${payload.sender?.name || 'Unknown'}`,
                message: payload.message,
                sentBy: payload.sender,
                isRead: false,
              };
              setData((prev) => ({ ...prev, notifications: [chatNotif, ...(prev.notifications || [])] }));
            }
          } catch (e) { }
        });

        // subscribe to personal notifications
        stomp.subscribe(`/user/queue/notifications`, (msg) => {
          try {
            const payload = JSON.parse(msg.body);
            setData((prev) => ({ ...prev, notifications: [payload, ...(prev.notifications || [])] }));
          } catch (e) { }
        });
      } catch (e) {
        // ignore
      }
    };

    stomp.activate();
    return () => stomp.deactivate();
  }, [userId, activeSection, selectedChatUserId]);

  return (
    <div className="flex flex-col md:flex-row min-h-screen bg-slate-100 p-4 gap-4  text-blue-600">
      <Sidebar user={user} active={activeSection} onSelect={setActiveSection} />

      <main className="flex-1 overflow-auto min-w-0">
        <Header user={user} onLogout={onLogout} />

        {error && <div className="mt-4 rounded-lg bg-red-50 text-red-700 px-4 py-3">{error}</div>}
        {notice && <div className="mt-4 rounded-lg bg-green-50 text-green-700 px-4 py-3">{notice}</div>}

        {activeSection === "dashboard" ? (
          <div className="grid grid-cols-12 gap-4 mt-4">
            <div className="col-span-12 xl:col-span-9">
              <Panel title="Upcoming Classes">
                <UpcomingClasses classes={upcomingClasses} isLoading={isLoading} />
              </Panel>
            </div>

            <div className="col-span-12 sm:col-span-6 xl:col-span-3">
              <TimeCard />
            </div>

            <div className="col-span-12 sm:col-span-6 xl:col-span-5">
              <AttendanceCard
                percentage={attendancePercent} total={data.attendance.length

                } />
            </div>

            <div className="col-span-12 sm:col-span-6 xl:col-span-3 ">
              <ExtraclassesCard count={classes.length} />
            </div>

            <div className="col-span-12 sm:col-span-6 xl:col-span-4">
              <CalendarCard />
            </div>

            <div className="col-span-12 xl:col-span-8">
              <OngoingClasses classes={visibleClasses} isLoading={isLoading} />
            </div>

            {userRole === "STUDENT" && (
              <div className="col-span-12">
                <TodayTimetable classes={todayClasses} isLoading={isLoading} />
              </div>
            )}

            <div className="col-span-12 xl:col-span-4">
              <Panel title="Location Attendance">
                <div className="space-y-3 flex flex-col justify-between">
                  <SelectField
                    label="Class"
                    value={selectedClass?.id || ""}
                    onChange={(event) => setSelectedClassId(event.target.value)}
                  >
                    {classes.length === 0 && (
                      <option value="">No classes loaded</option>
                    )}
                    {classes.map((item) => (
                      <option key={item.id} value={item.id}>
                        {item.subject} - {item.faculty}
                      </option>
                    ))}
                  </SelectField>
                  <button onClick={markAttendance} className="w-full rounded-md bg-green-600 px-4 py-2 text-sm font-semibold text-white hover:bg-green-700">
                    Mark With Latitude And Longitude
                  </button>
                </div>
              </Panel>
            </div>

            {userRole === "FACULTY" && (
              <div className="col-span-12">
                <Panel title="Customized Teacher Dashboard">
                  <div className="grid gap-3 md:grid-cols-4">
                    <div className="rounded-lg bg-slate-50 p-3">
                      <p className="text-xs text-slate-500">My classes</p>
                      <p className="text-2xl font-bold">{teacherClasses.length}</p>
                    </div>
                    <div className="rounded-lg bg-slate-50 p-3">
                      <p className="text-xs text-slate-500">Assignments</p>
                      <p className="text-2xl font-bold">{data.assignments.length}</p>
                    </div>
                    <div className="rounded-lg bg-slate-50 p-3">
                      <p className="text-xs text-slate-500">Unread notices</p>
                      <p className="text-2xl font-bold">{unreadCount}</p>
                    </div>
                    <div className="rounded-lg bg-slate-50 p-3">
                      <p className="text-xs text-slate-500">Devices online</p>
                      <p className="text-2xl font-bold">{onlineDevices}</p>
                    </div>
                  </div>
                </Panel>
              </div>
            )}

            {userRole !== "STUDENT" && (
              <div className="col-span-12 xl:col-span-6">
                <Panel title="Routine Updation">
                  <form onSubmit={saveRoutine} className="grid gap-3 md:grid-cols-2 pt-3">
                    <Field label="Subject" required value={routineForm.subject} onChange={(e) => setRoutineForm({ ...routineForm, subject: e.target.value })} />
                    <Field label="Classroom" required value={routineForm.classroom} onChange={(e) => setRoutineForm({ ...routineForm, classroom: e.target.value })} />
                    <SelectField label="Faculty" value={routineForm.facultyId} onChange={(e) => setRoutineForm({ ...routineForm, facultyId: e.target.value })}>
                      <option value="">Use current faculty</option>
                      {data.faculty.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}
                    </SelectField>
                    <SelectField label="Stream" value={routineForm.streamId} onChange={(e) => setRoutineForm({ ...routineForm, streamId: e.target.value })}>
                      <option value="">Default stream</option>
                      {data.streams.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}
                    </SelectField>
                    <SelectField label="Day" value={routineForm.day} onChange={(e) => setRoutineForm({ ...routineForm, day: e.target.value })}>
                      {days.map((item) => <option key={item}>{item}</option>)}
                    </SelectField>
                    <Field label="Semester" type="number" min="1" value={routineForm.semester} onChange={(e) => setRoutineForm({ ...routineForm, semester: e.target.value })} />
                    <Field label="Start Time" type="time" value={routineForm.startTime} onChange={(e) => setRoutineForm({ ...routineForm, startTime: e.target.value })} />
                    <Field label="End Time" type="time" value={routineForm.endTime} onChange={(e) => setRoutineForm({ ...routineForm, endTime: e.target.value })} />
                    <button className="md:col-span-2 rounded-md bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700 mt-12">Save Routine</button>
                  </form>
                </Panel>
              </div>
            )}

            <div className="col-span-12 xl:col-span-6">
              <Panel title="Assignments">
                <div className="grid gap-4 lg:grid-cols-1">

                  {userRole !== "STUDENT" && (
                    <form onSubmit={createAssignment} className="space-y-3">
                      <Field label="Title" required value={assignmentForm.title} onChange={(e) => setAssignmentForm({ ...assignmentForm, title: e.target.value })} />
                      <Field label="Subject" required value={assignmentForm.subject} onChange={(e) => setAssignmentForm({ ...assignmentForm, subject: e.target.value })} />
                      <Field label="Due Date" required type="date" value={assignmentForm.dueDate} onChange={(e) => setAssignmentForm({ ...assignmentForm, dueDate: e.target.value })} />
                      <SelectField label="Section" value={assignmentForm.sectionId} onChange={(e) => setAssignmentForm({ ...assignmentForm, sectionId: e.target.value })}>
                        <option value="">All sections</option>
                        {data.sections.map((item) => <option key={item.id} value={item.id}>{item.name || item.sectionName || `Section ${item.id}`}</option>)}
                      </SelectField>
                      <Field label="Description" value={assignmentForm.description} onChange={(e) => setAssignmentForm({ ...assignmentForm, description: e.target.value })} />
                      <button className="w-full rounded-md bg-blue-600 px-4 py-2 mt-2 text-sm font-semibold hover:bg-blue-800 text-white">Create Assignment</button>
                    </form>
                  )}
                  {userRole !== "FACULTY" && (
                    <form onSubmit={submitAssignment} className="space-y-3">
                      <SelectField label="Assignment" value={selectedAssignmentId} onChange={(e) => setSelectedAssignmentId(e.target.value)}>
                        {data.assignments.map((item) => <option key={item.id} value={item.id}>{item.title} - {item.subject}</option>)}
                      </SelectField>
                      <textarea
                        required
                        value={submissionText}
                        onChange={(e) => setSubmissionText(e.target.value)}
                        className="min-h-28 w-full rounded-md border border-slate-200 px-3 py-2 text-sm outline-none focus:border-blue-500"
                        placeholder="Write submission notes or paste a document link"
                      />
                      <button className="w-full rounded-md bg-indigo-600 px-4 py-2 text-sm font-semibold text-white hover:bg-indigo-700">Submit Assignment</button>
                    </form>
                  )}

                </div>
              </Panel>
            </div>

            <div className="col-span-12 xl:col-span-7">
              <Panel title="AI Notes">
                <div className="grid gap-4 overflow-auto h-70 lg:grid-cols-2">
                  {userRole !== "STUDENT" && (
                    <form onSubmit={generateNote} className="space-y-3 flex flex-col gap-2">
                      <Field label="Subject" required value={noteForm.subject} onChange={(e) => setNoteForm({ ...noteForm, subject: e.target.value })} />
                      <Field label="Topic" required value={noteForm.topic} onChange={(e) => setNoteForm({ ...noteForm, topic: e.target.value })} />
                      <button className="w-full rounded-md bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-800">Generate And Publish Notes</button>
                    </form>
                  )}

                  <div className="w-full flex flex-wrap justify-between align-center gap-2 max-h-18">
                    {data.notes.map((note) => (
                      <div key={note.id} className="rounded-lg bg-slate-50 p-3">
                        <div className="flex items-start justify-between gap-3">
                          <div>
                            <p className="font-medium text-slate-900">{note.topic}</p>
                            <p className="text-xs text-slate-500">{note.subject}</p>
                          </div>
                          <button type="button" onClick={() => downloadNote(note)} className="rounded-md bg-white px-3 py-1 text-xs font-semibold text-blue-600 shadow-sm">Download</button>
                        </div>
                        <p className="mt-2 line-clamp-3 text-xs text-slate-600">{note.content}</p>
                      </div>
                    ))}
                    {data.notes.length === 0 && <p className="text-sm text-slate-500">No notes published yet.</p>}
                  </div>
                </div>
              </Panel>
            </div>

            <div className="col-span-12 xl:col-span-5">
              <Panel title="Device Status" action={<span className="text-xs text-slate-500">{onlineDevices}/{data.devices.length} ON</span>}>
                <div className="space-y-2">
                  {data.devices.map((device) => (
                    <div key={device.id} className="flex items-center justify-between rounded-lg bg-slate-50 p-3">
                      <div>
                        <p className="font-medium">{device.deviceName}</p>
                        <p className="text-xs text-slate-500">Classroom {device.classroomId}</p>
                      </div>
                      <button
                        onClick={() => withAction(() => (device.status === "ON" ? api.turnDeviceOff(device.id) : api.turnDeviceOn(device.id)), "Device status updated.")}
                        className={`rounded-md px-3 py-1 text-xs font-semibold ${device.status === "ON" ? "bg-green-100 text-green-700" : "bg-slate-200 text-slate-700"}`}
                      >
                        {device.status}
                      </button>
                    </div>
                  ))}
                </div>
              </Panel>
            </div>

            <div className="col-span-12 xl:col-span-4">
              <Panel
                title="Notification Status"
                action={<button onClick={() => withAction(() => api.markAllNotificationsRead(toRole(userRole)), "Notifications marked as read.")} className="text-xs font-semibold text-blue-600">Read All</button>}
              >
                <div className="space-y-2">
                  {data.notifications.slice(0, 8).map((item) => (
                    <div key={item.id} className="block w-full rounded-lg bg-slate-50 p-3 text-left">
                      <div className="flex justify-between gap-2 items-start">
                        <div>
                          <p className="font-medium">{item.title}</p>
                          <p className="mt-1 text-xs text-slate-500">{item.message}</p>
                        </div>
                        <div className="space-y-1 text-right">
                          <button onClick={() => { withAction(() => api.markNotificationRead(item.id), "Notification marked as read."); }} className="text-xs text-slate-500">Mark read</button>
                          {item.sentBy && (
                            <button onClick={() => { setActiveSection('messages'); loadConversation(item.sentBy.id); withAction(() => api.markNotificationRead(item.id), "Notification marked as read."); }} className="text-xs text-blue-600">Open chat</button>
                          )}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </Panel>
            </div>

            <div className="col-span-12 xl:col-span-8">
              <Panel title="Chat">
                <form onSubmit={sendMessage} className="space-y-3">
                  <SelectField label="Chat With" value={selectedChatUserId} onChange={(e) => loadConversation(e.target.value)}>
                    <option value="">Select user</option>
                    {chatTargets.map((item) => <option key={item.id} value={item.id}>{item.name} ({normalizeUserRole(item.role)})</option>)}
                  </SelectField>
                  <div className="max-h-40 space-y-2 overflow-auto rounded-lg bg-slate-50 p-3">
                    {conversation.map((item) => (
                      <p key={item.id} className={`rounded-md px-3 py-2 text-xs ${item.sender?.id === userId ? "bg-blue-600 text-white" : "bg-white text-slate-700"}`}>
                        {item.message}
                      </p>
                    ))}
                    {conversation.length === 0 && <p className="text-xs text-slate-500">No messages selected.</p>}
                  </div>
                  <textarea required value={message} onChange={(e) => setMessage(e.target.value)} className="min-h-20 w-full rounded-md border border-slate-200 px-3 py-2 text-sm outline-none focus:border-blue-500" placeholder="Type a message" />
                  <button className="w-full rounded-md bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700">Send Message</button>
                </form>
              </Panel>
            </div>
          </div>
        ) : activeSection === "messages" ? (
          <div className="mt-4">
            <Panel title="Chat">
              <form onSubmit={sendMessage} className="space-y-3">
                <SelectField label="Chat With" value={selectedChatUserId} onChange={(e) => loadConversation(e.target.value)}>
                  <option value="">Select user</option>
                  {chatTargets.map((item) => <option key={item.id} value={item.id}>{item.name} ({normalizeUserRole(item.role)})</option>)}
                </SelectField>
                <div className="max-h-64 space-y-2 overflow-auto rounded-lg bg-slate-50 p-3">
                  {conversation.map((item) => (
                    <p key={item.id} className={`rounded-md px-3 py-2 text-xs ${item.sender?.id === userId ? "bg-blue-600 text-white" : "bg-white text-slate-700"}`}>
                      {item.message}
                    </p>
                  ))}
                  {conversation.length === 0 && <p className="text-xs text-slate-500">No messages selected.</p>}
                </div>
                <textarea required value={message} onChange={(e) => setMessage(e.target.value)} className="min-h-20 w-full rounded-md border border-slate-200 px-3 py-2 text-sm outline-none focus:border-blue-500" placeholder="Type a message" />
                <button className="w-full rounded-md bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700">Send Message</button>
              </form>
            </Panel>
          </div>
        ) : activeSection === "classes" ? (
          <div className="mt-4 grid grid-cols-1 gap-4">
            <Panel title="Upcoming Classes">
              <UpcomingClasses classes={upcomingClasses} isLoading={isLoading} />
            </Panel>
            <Panel title="Ongoing Classes">
              <OngoingClasses classes={visibleClasses} isLoading={isLoading} />
            </Panel>
          </div>
        ) : activeSection === "attendance" ? (
          <div className="mt-4">
            <Panel title="Attendance Overview">
              <AttendanceCard percentage={attendancePercent} total={data.attendance.length} />
            </Panel>
            <Panel title="Today's Timetable">
              <TodayTimetable classes={todayClasses} isLoading={isLoading} />
            </Panel>
          </div>
        ) : activeSection === "devices" ? (
          <div className="mt-4">
            <Panel title="Device Status" action={<span className="text-xs text-slate-500">{onlineDevices}/{data.devices.length} ON</span>}>
              <div className="space-y-2">
                {data.devices.map((device) => (
                  <div key={device.id} className="flex items-center justify-between rounded-lg bg-slate-50 p-3">
                    <div>
                      <p className="font-medium">{device.deviceName}</p>
                      <p className="text-xs text-slate-500">Classroom {device.classroomId}</p>
                    </div>
                    <button
                      onClick={() => withAction(() => (device.status === "ON" ? api.turnDeviceOff(device.id) : api.turnDeviceOn(device.id)), "Device status updated.")}
                      className={`rounded-md px-3 py-1 text-xs font-semibold ${device.status === "ON" ? "bg-green-100 text-green-700" : "bg-slate-200 text-slate-700"}`}
                    >
                      {device.status}
                    </button>
                  </div>
                ))}
              </div>
            </Panel>
          </div>
        ) : activeSection === "admin" ? (
          <div className="mt-4 grid grid-cols-12 gap-4">
            <div className="col-span-12 xl:col-span-4">
              <Panel title="Admin Portal">
                <div className="space-y-3">
                  <p className="text-sm text-slate-600">Admin actions:</p>
                  <div className="flex flex-col gap-2">
                    <button onClick={() => setAdminTab('users')} className={`rounded-md px-3 py-2 text-left text-sm font-semibold ${adminTab === 'users' ? 'bg-blue-600 text-white' : 'bg-slate-100 text-slate-700'}`}>
                      Manage Users
                    </button>
                    <button onClick={() => setAdminTab('timetables')} className={`rounded-md px-3 py-2 text-left text-sm font-semibold ${adminTab === 'timetables' ? 'bg-blue-600 text-white' : 'bg-slate-100 text-slate-700'}`}>
                      Manage Timetables
                    </button>
                  </div>
                </div>
              </Panel>
            </div>

            <div className="col-span-12 xl:col-span-8">
              {adminTab === 'users' ? (
                <Panel title="Users">
                  <div className="grid gap-4 lg:grid-cols-2">
                    <form onSubmit={saveAdminUser} className="space-y-3 rounded-lg bg-slate-50 p-4">
                      <h3 className="text-sm font-semibold">{editingUserId ? 'Edit User' : 'Create User'}</h3>
                      <Field label="Name" required value={adminUserForm.name} onChange={(e) => setAdminUserForm({ ...adminUserForm, name: e.target.value })} />
                      <Field label="Email" required type="email" value={adminUserForm.email} onChange={(e) => setAdminUserForm({ ...adminUserForm, email: e.target.value })} />
                      <SelectField label="Role" value={adminUserForm.role} onChange={(e) => setAdminUserForm({ ...adminUserForm, role: e.target.value })}>
                        <option value="ADMIN">Admin</option>
                        <option value="FACULTY">Faculty</option>
                        <option value="STUDENT">Student</option>
                      </SelectField>
                      <Field label="Password" type="password" value={adminUserForm.password} onChange={(e) => setAdminUserForm({ ...adminUserForm, password: e.target.value })} />
                      <div className="flex gap-2">
                        <button type="submit" className="rounded-md bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700">
                          {editingUserId ? 'Update User' : 'Create User'}
                        </button>
                        <button type="button" onClick={resetAdminUserForm} className="rounded-md border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100">
                          Reset
                        </button>
                      </div>
                    </form>

                    <div className="overflow-auto rounded-lg bg-slate-50 p-4">
                      <h3 className="text-sm font-semibold mb-3">User List</h3>
                      <table className="w-full text-left text-sm">
                        <thead>
                          <tr className="border-b border-slate-200 text-xs uppercase text-slate-500">
                            <th className="py-2 pr-2">Name</th>
                            <th className="py-2 pr-2">Email</th>
                            <th className="py-2 pr-2">Role</th>
                            <th className="py-2">Actions</th>
                          </tr>
                        </thead>
                        <tbody>
                          {data.users.map((userItem) => (
                            <tr key={userItem.id} className="border-b border-slate-200 last:border-0">
                              <td className="py-2 pr-2">{userItem.name}</td>
                              <td className="py-2 pr-2">{userItem.email}</td>
                              <td className="py-2 pr-2">{userItem.role}</td>
                              <td className="py-2 space-x-1">
                                <button type="button" onClick={() => editAdminUser(userItem)} className="rounded-md bg-yellow-100 px-2 py-1 text-xs font-semibold text-yellow-700 hover:bg-yellow-200">Edit</button>
                                <button type="button" onClick={() => removeAdminUser(userItem.id)} className="rounded-md bg-red-100 px-2 py-1 text-xs font-semibold text-red-700 hover:bg-red-200">Delete</button>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                </Panel>
              ) : (
                <Panel title="Timetables">
                  <div className="grid gap-4 lg:grid-cols-2">
                    <form onSubmit={saveAdminTimetable} className="space-y-3 rounded-lg bg-slate-50 p-4">
                      <h3 className="text-sm font-semibold">{editingTimetableId ? 'Edit Timetable' : 'Create Timetable'}</h3>
                      <SelectField label="Stream" value={adminTimetableForm.streamId} onChange={(e) => setAdminTimetableForm({ ...adminTimetableForm, streamId: e.target.value })}>
                        <option value="">Select stream</option>
                        {data.streams.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}
                      </SelectField>
                      <SelectField label="Faculty" value={adminTimetableForm.facultyId} onChange={(e) => setAdminTimetableForm({ ...adminTimetableForm, facultyId: e.target.value })}>
                        <option value="">Select faculty</option>
                        {data.faculty.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}
                      </SelectField>
                      <Field label="Subject" required value={adminTimetableForm.subject} onChange={(e) => setAdminTimetableForm({ ...adminTimetableForm, subject: e.target.value })} />
                      <Field label="Classroom" required value={adminTimetableForm.classroom} onChange={(e) => setAdminTimetableForm({ ...adminTimetableForm, classroom: e.target.value })} />
                      <SelectField label="Day" value={adminTimetableForm.day} onChange={(e) => setAdminTimetableForm({ ...adminTimetableForm, day: e.target.value })}>
                        {days.map((item) => <option key={item}>{item}</option>)}
                      </SelectField>
                      <Field label="Start Time" type="time" value={adminTimetableForm.startTime} onChange={(e) => setAdminTimetableForm({ ...adminTimetableForm, startTime: e.target.value })} />
                      <Field label="End Time" type="time" value={adminTimetableForm.endTime} onChange={(e) => setAdminTimetableForm({ ...adminTimetableForm, endTime: e.target.value })} />
                      <Field label="Semester" type="number" min="1" value={adminTimetableForm.semester} onChange={(e) => setAdminTimetableForm({ ...adminTimetableForm, semester: e.target.value })} />
                      <div className="flex gap-2">
                        <button type="submit" className="rounded-md bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700">
                          {editingTimetableId ? 'Update Timetable' : 'Create Timetable'}
                        </button>
                        <button type="button" onClick={() => { setEditingTimetableId(null); setAdminTimetableForm({ streamId: "", subject: "", facultyId: "", classroom: "", day: todayDay(), startTime: "09:00", endTime: "10:00", semester: 1 }); }} className="rounded-md border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100">
                          Reset
                        </button>
                      </div>
                    </form>
                    <div className="overflow-auto rounded-lg bg-slate-50 p-4">
                      <h3 className="text-sm font-semibold mb-3">Timetable Entries</h3>
                      <table className="w-full text-left text-sm">
                        <thead>
                          <tr className="border-b border-slate-200 text-xs uppercase text-slate-500">
                            <th className="py-2 pr-2">Subject</th>
                            <th className="py-2 pr-2">Faculty</th>
                            <th className="py-2 pr-2">Day</th>
                            <th className="py-2 pr-2">Time</th>
                            <th className="py-2">Actions</th>
                          </tr>
                        </thead>
                        <tbody>
                          {data.timetable.map((item) => (
                            <tr key={item.id} className="border-b border-slate-200 last:border-0">
                              <td className="py-2 pr-2">{item.subject}</td>
                              <td className="py-2 pr-2">{item.faculty?.name || 'N/A'}</td>
                              <td className="py-2 pr-2">{item.day}</td>
                              <td className="py-2 pr-2">{item.startTime} - {item.endTime}</td>
                              <td className="py-2 space-x-1">
                                <button type="button" onClick={() => editAdminTimetable(item)} className="rounded-md bg-yellow-100 px-2 py-1 text-xs font-semibold text-yellow-700 hover:bg-yellow-200">Edit</button>
                                <button type="button" onClick={() => deleteAdminTimetable(item.id)} className="rounded-md bg-red-100 px-2 py-1 text-xs font-semibold text-red-700 hover:bg-red-200">Delete</button>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                </Panel>
              )}
            </div>
          </div>
        ) : (
          <div className="mt-4">
            <Panel title="Section">
              <p className="text-sm text-slate-600">This section is under construction.</p>
            </Panel>
          </div>
        )}
      </main>
    </div>
  );
};

export default Dashboard;
