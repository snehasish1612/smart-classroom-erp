import { useEffect, useMemo, useState } from "react";
import { api } from "./api.js";

const STORAGE_KEY = "smart-classroom-demo-user";
const roles = ["ADMIN", "Faculty", "STUDENT"];
const deviceNames = ["Lights", "Projector", "AC", "Fan", "SmartBoard"];
const deviceStatuses = ["ON", "OFF"];
const classroomStatuses = ["AVAILABLE", "OCCUPIED"];
const days = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"];
const attendanceStatuses = ["PRESENT", "ABSENT"];
const targetRoles = ["ALL", "STUDENT", "FACULTY"];

const modules = [
  { id: "dashboard", label: "Dashboard" },
  { id: "students", label: "Students" },
  { id: "faculty", label: "Faculty" },
  { id: "classrooms", label: "Classrooms" },
  { id: "devices", label: "Devices" },
  { id: "timetable", label: "Timetable" },
  { id: "attendance", label: "Attendance" },
  { id: "notifications", label: "Notifications" },
  { id: "users", label: "Users" },
];

const moduleAccess = {
  ADMIN: ["dashboard", "students", "faculty", "classrooms", "devices", "timetable", "attendance", "notifications", "users"],
  Faculty: ["dashboard", "timetable", "attendance", "devices", "notifications"],
  STUDENT: ["dashboard", "timetable", "attendance", "notifications"],
};

const configs = {
  students: {
    title: "Students",
    path: "/api/students",
    empty: { name: "", email: "", rollNumber: "", department: "", semester: 1, phone: "" },
    fields: [
      { name: "name", label: "Name", required: true },
      { name: "email", label: "Email", type: "email", required: true },
      { name: "rollNumber", label: "Roll No.", required: true },
      { name: "department", label: "Department", required: true },
      { name: "semester", label: "Semester", type: "number", min: 1, max: 8, required: true },
      { name: "phone", label: "Phone" },
    ],
    columns: ["id", "name", "rollNumber", "department", "semester", "email", "phone"],
  },
  faculty: {
    title: "Faculty",
    path: "/api/faculty",
    empty: { name: "", email: "", department: "", phone: "", designation: "", subjectsTaught: "" },
    fields: [
      { name: "name", label: "Name", required: true },
      { name: "email", label: "Email", type: "email", required: true },
      { name: "department", label: "Department", required: true },
      { name: "phone", label: "Phone" },
      { name: "designation", label: "Designation", required: true },
      { name: "subjectsTaught", label: "Subjects" },
    ],
    columns: ["id", "name", "department", "designation", "subjectsTaught", "email", "phone"],
  },
  classrooms: {
    title: "Classrooms",
    path: "/api/classrooms",
    empty: { roomNumber: "", building: "", capacity: 40, status: "AVAILABLE" },
    fields: [
      { name: "roomNumber", label: "Room No.", required: true },
      { name: "building", label: "Building" },
      { name: "capacity", label: "Capacity", type: "number" },
      { name: "status", label: "Status", type: "select", options: classroomStatuses },
    ],
    columns: ["id", "roomNumber", "building", "capacity", "status"],
  },
  devices: {
    title: "Devices",
    path: "/api/devices",
    empty: { deviceName: "Lights", status: "OFF", classroomId: "" },
    fields: [
      { name: "deviceName", label: "Device", type: "select", options: deviceNames },
      { name: "status", label: "Status", type: "select", options: deviceStatuses },
      { name: "classroomId", label: "Classroom ID", type: "number", required: true },
    ],
    columns: ["id", "deviceName", "classroomId", "status"],
  },
  users: {
    title: "Users",
    path: "/api/users",
    empty: { name: "", email: "", password: "", role: "ADMIN" },
    fields: [
      { name: "name", label: "Name", required: true },
      { name: "email", label: "Email", type: "email", required: true },
      { name: "password", label: "Password", type: "password", required: true },
      { name: "role", label: "Role", type: "select", options: roles },
    ],
    columns: ["id", "name", "role", "email"],
  },
};

export default function App() {
  const [session, setSession] = useStoredState(STORAGE_KEY, null);
  const [active, setActive] = useState("dashboard");
  const visibleModules = useMemo(() => modules.filter((item) => moduleAccess[session?.role]?.includes(item.id)), [session]);
  const isAdmin = session?.role === "ADMIN";
  const isFaculty = session?.role === "Faculty";

  useEffect(() => {
    if (session && visibleModules.length && !visibleModules.some((item) => item.id === active)) {
      setActive("dashboard");
    }
  }, [active, session, visibleModules]);

  if (!session) {
    return <Login onLogin={setSession} />;
  }

  if (!visibleModules.some((item) => item.id === active)) {
    return null;
  }

  return (
    <div className="shell">
      <aside className="sidebar">
        <div className="brand">
          <span>SC</span>
          <div>
            <strong>Smart Classroom</strong>
            <small>ERP Console</small>
          </div>
        </div>
        <nav>
          {visibleModules.map((item) => (
            <button className={active === item.id ? "active" : ""} key={item.id} onClick={() => setActive(item.id)}>
              {item.label}
            </button>
          ))}
        </nav>
      </aside>

      <main className="main">
        <header className="topbar">
          <div>
            <h1>{visibleModules.find((item) => item.id === active)?.label}</h1>
            <p>{portalSubtitle(session.role)}</p>
          </div>
          <div className="userbar">
            <span>{session.name}</span>
            <small>{session.role}</small>
            <button className="ghost" onClick={() => setSession(null)}>Logout</button>
          </div>
        </header>

        {active === "dashboard" && <Dashboard session={session} />}
        {active === "attendance" && <Attendance session={session} canManage={isAdmin || isFaculty} />}
        {active === "notifications" && <Notifications session={session} canCreate={isAdmin} />}
        {active === "timetable" && <Timetable session={session} canManage={isAdmin} />}
        {active === "devices" && <CrudPage config={configs.devices} readOnly={!isAdmin} allowDeviceControls={isAdmin || isFaculty} />}
        {configs[active] && active !== "devices" && <CrudPage config={configs[active]} readOnly={!isAdmin} />}
      </main>
    </div>
  );
}

function Login({ onLogin }) {
  const [users, setUsers] = useState([]);
  const [selected, setSelected] = useState("");
  const [manual, setManual] = useState({ name: "Admin", email: "admin@example.com", role: "ADMIN" });
  const [password, setPassword] = useState("admin123");
  const [state, setState] = useAsyncState();

  const loadUsers = () => {
    setState.loading();
    api.list("/api/users")
      .then((data) => {
        setUsers(data || []);
        setSelected(data?.[0]?.id ? String(data[0].id) : "");
        setState.ready();
      })
      .catch((error) => setState.error(error.message));
  };

  useEffect(loadUsers, []);

  const login = () => {
    const found = users.find((user) => String(user.id) === selected);
    onLogin(found || { id: "demo", ...manual });
  };

  const createAdmin = () => {
    setState.loading();
    api.create("/api/users", { ...manual, password })
      .then((user) => onLogin(user))
      .catch((error) => setState.error(error.message));
  };

  return (
    <div className="login-page">
      <section className="login-panel">
        <div className="brand large">
          <span>SC</span>
          <div>
            <strong>Smart Classroom ERP</strong>
            <small>Demo Admin Login</small>
          </div>
        </div>
        <div className="stack">
          <label>
            Existing user
            <select value={selected} onChange={(event) => setSelected(event.target.value)}>
              {users.map((user) => (
                <option key={user.id} value={user.id}>{user.name} - {user.role}</option>
              ))}
            </select>
          </label>
          {!users.length && (
            <div className="grid two">
              <Input label="Name" value={manual.name} onChange={(name) => setManual({ ...manual, name })} />
              <Select label="Role" value={manual.role} options={roles} onChange={(role) => setManual({ ...manual, role })} />
              <Input label="Email" value={manual.email} onChange={(email) => setManual({ ...manual, email })} />
              <Input label="Password" type="password" value={password} onChange={setPassword} />
            </div>
          )}
          {state.errorMessage && <div className="notice">Could not load users. You can still enter with demo data.</div>}
          {users.length ? (
            <button className="primary" onClick={login} disabled={state.isLoading}>Enter dashboard</button>
          ) : (
            <div className="login-actions">
              <button className="primary" onClick={createAdmin} disabled={state.isLoading}>Create admin and enter</button>
              <button className="ghost" onClick={login} disabled={state.isLoading}>Enter demo only</button>
            </div>
          )}
        </div>
      </section>
    </div>
  );
}

function Dashboard({ session }) {
  const endpoints = useMemo(() => {
    if (session.role === "ADMIN") {
      return [
        ["Students", "/api/students"],
        ["Faculty", "/api/faculty"],
        ["Classrooms", "/api/classrooms"],
        ["Devices", "/api/devices"],
        ["Timetable", "/api/timetable"],
        ["Attendance", "/api/attendance"],
        ["Notifications", "/api/notifications"],
        ["Users", "/api/users"],
      ];
    }
    if (session.role === "Faculty") {
      return [
        ["My Classes", "/api/timetable"],
        ["Attendance", "/api/attendance"],
        ["Devices", "/api/devices"],
        ["Notifications", "/api/notifications"],
      ];
    }
    return [
      ["My Timetable", "/api/timetable"],
      ["My Attendance", "/api/attendance"],
      ["Notifications", "/api/notifications"],
    ];
  }, [session.role]);
  const [data, setData] = useState({});
  const [state, setState] = useAsyncState();

  useEffect(() => {
    setState.loading();
    Promise.all(endpoints.map(([label, path]) => api.list(path).then((items) => [label, items || []])))
      .then((pairs) => {
        setData(Object.fromEntries(pairs));
        setState.ready();
      })
      .catch((error) => setState.error(error.message));
  }, [endpoints]);

  return (
    <section className="content">
      {state.errorMessage && <ErrorBox message={state.errorMessage} />}
      <div className="stats">
        {endpoints.map(([label]) => (
          <article className="stat" key={label}>
            <span>{label}</span>
            <strong>{data[label]?.length ?? "-"}</strong>
          </article>
        ))}
      </div>
      <div className="panel">
        <h2>Recent Notifications</h2>
        <Table
          rows={(data.Notifications || []).slice(0, 5)}
          columns={["id", "title", "targetRole", "isRead", "createdAt"]}
          emptyText="No notifications yet."
        />
      </div>
    </section>
  );
}

function CrudPage({ config, readOnly = false, allowDeviceControls = false }) {
  const [rows, setRows] = useState([]);
  const [form, setForm] = useState(config.empty);
  const [editing, setEditing] = useState(null);
  const [classrooms, setClassrooms] = useState([]);
  const [state, setState] = useAsyncState();
  const fields = useMemo(() => {
    if (config.title !== "Devices") return config.fields;
    return config.fields.map((field) => {
      if (field.name !== "classroomId") return field;
      return {
        ...field,
        type: "select",
        options: classrooms.map((room) => ({
          value: room.id,
          label: `${room.roomNumber || `Room ${room.id}`}${room.building ? `, ${room.building}` : ""}`,
        })),
        placeholder: classrooms.length ? "Select classroom" : "Create a classroom first",
      };
    });
  }, [classrooms, config]);

  const load = () => {
    setState.loading();
    api.list(config.path)
      .then((data) => {
        setRows(data || []);
        setState.ready();
      })
      .catch((error) => setState.error(error.message));
  };

  useEffect(() => {
    load();
    if (config.title === "Devices") {
      api.list("/api/classrooms").then((data) => setClassrooms(data || [])).catch(() => setClassrooms([]));
    }
  }, [config.path]);

  const reset = () => {
    setEditing(null);
    setForm(config.empty);
  };

  const submit = (event) => {
    event.preventDefault();
    const payload = coercePayload(form, fields);
    const action = editing ? api.update(`${config.path}/${editing.id}`, payload) : api.create(config.path, payload);
    setState.loading();
    action.then(() => {
      reset();
      load();
    }).catch((error) => setState.error(error.message));
  };

  const remove = (row) => {
    if (!window.confirm(`Delete ${config.title.slice(0, -1).toLowerCase()} #${row.id}?`)) return;
    setState.loading();
    api.remove(`${config.path}/${row.id}`).then(load).catch((error) => setState.error(error.message));
  };

  return (
    <section className={`content ${readOnly ? "" : "split"}`}>
      <div className="panel">
        <div className="panel-head">
          <h2>{config.title}</h2>
          <button className="ghost" onClick={load}>Refresh</button>
        </div>
        {state.errorMessage && <ErrorBox message={state.errorMessage} />}
        <Table
          rows={rows}
          columns={config.columns}
          emptyText={`No ${config.title.toLowerCase()} found.`}
          actions={(row) => (
            <>
              {config.title === "Devices" && allowDeviceControls && (
                <>
                  <button onClick={() => api.put(`/api/devices/${row.id}/on`).then(load).catch((error) => setState.error(error.message))}>ON</button>
                  <button onClick={() => api.put(`/api/devices/${row.id}/off`).then(load).catch((error) => setState.error(error.message))}>OFF</button>
                </>
              )}
              {!readOnly && (
                <>
                  <button onClick={() => { setEditing(row); setForm(projectRow(row, config.empty)); }}>Edit</button>
                  <button className="danger" onClick={() => remove(row)}>Delete</button>
                </>
              )}
            </>
          )}
        />
      </div>
      {!readOnly && (
        <EntityForm
          title={editing ? `Edit ${config.title}` : `Create ${config.title}`}
          fields={fields}
          form={form}
          onChange={(name, value) => setForm({ ...form, [name]: value })}
          onSubmit={submit}
          onCancel={reset}
          isEditing={Boolean(editing)}
        />
      )}
    </section>
  );
}

function Timetable({ session, canManage }) {
  const config = {
    empty: { subject: "", facultyId: "", classroomId: "", day: "MONDAY", startTime: "09:00", endTime: "10:00", semester: 1, department: "" },
    fields: [
      { name: "subject", label: "Subject", required: true },
      { name: "facultyId", label: "Faculty ID", type: "number", required: true },
      { name: "classroomId", label: "Classroom ID", type: "number", required: true },
      { name: "day", label: "Day", type: "select", options: days },
      { name: "startTime", label: "Start", type: "time", required: true },
      { name: "endTime", label: "End", type: "time", required: true },
      { name: "semester", label: "Semester", type: "number", min: 1, max: 8 },
      { name: "department", label: "Department", required: true },
    ],
  };
  const [rows, setRows] = useState([]);
  const [faculty, setFaculty] = useState([]);
  const [classrooms, setClassrooms] = useState([]);
  const [students, setStudents] = useState([]);
  const [form, setForm] = useState(config.empty);
  const [editing, setEditing] = useState(null);
  const [state, setState] = useAsyncState();
  const fields = useMemo(() => config.fields.map((field) => {
    if (field.name === "facultyId") {
      return {
        ...field,
        type: "select",
        options: faculty.map((item) => ({ value: item.id, label: `${item.name} - ${item.department}` })),
        placeholder: faculty.length ? "Select faculty" : "Create faculty first",
      };
    }
    if (field.name === "classroomId") {
      return {
        ...field,
        type: "select",
        options: classrooms.map((item) => ({ value: item.id, label: `${item.roomNumber} - ${item.status || "AVAILABLE"}` })),
        placeholder: classrooms.length ? "Select classroom" : "Create classroom first",
      };
    }
    return field;
  }), [classrooms, faculty]);

  const load = () => {
    setState.loading();
    api.list("/api/timetable")
      .then((data) => {
        setRows(filterTimetableForRole(data || [], session, faculty, students));
        setState.ready();
      })
      .catch((error) => setState.error(error.message));
  };

  useEffect(() => {
    setState.loading();
    Promise.all([
      api.list("/api/timetable"),
      api.list("/api/faculty"),
      api.list("/api/classrooms"),
      api.list("/api/students"),
    ])
      .then(([timetableData, facultyData, classroomData, studentData]) => {
        setFaculty(facultyData || []);
        setClassrooms(classroomData || []);
        setStudents(studentData || []);
        setRows(filterTimetableForRole(timetableData || [], session, facultyData || [], studentData || []));
        setState.ready();
      })
      .catch((error) => setState.error(error.message));
  }, [session]);

  const submit = (event) => {
    event.preventDefault();
    const payload = {
      subject: form.subject,
      faculty: { id: Number(form.facultyId) },
      classroom: { id: Number(form.classroomId) },
      day: form.day,
      startTime: form.startTime,
      endTime: form.endTime,
      semester: Number(form.semester),
      department: form.department,
    };
    const action = editing ? api.update(`/api/timetable/${editing.id}`, payload) : api.create("/api/timetable", payload);
    setState.loading();
    action.then(() => { setEditing(null); setForm(config.empty); load(); }).catch((error) => setState.error(error.message));
  };

  return (
    <section className={`content ${canManage ? "split" : ""}`}>
      <div className="panel">
        <div className="panel-head"><h2>Timetable</h2><button className="ghost" onClick={load}>Refresh</button></div>
        {state.errorMessage && <ErrorBox message={state.errorMessage} />}
        <Table
          rows={rows}
          columns={["id", "subject", "faculty.name", "classroom.roomNumber", "day", "startTime", "endTime", "department", "semester"]}
          emptyText="No timetable slots found."
          actions={canManage ? (row) => (
              <>
                <button onClick={() => {
                  setEditing(row);
                  setForm({
                    subject: row.subject || "",
                    facultyId: row.faculty?.id || "",
                    classroomId: row.classroom?.id || "",
                    day: row.day || "MONDAY",
                    startTime: row.startTime || "09:00",
                    endTime: row.endTime || "10:00",
                    semester: row.semester || 1,
                    department: row.department || "",
                  });
                }}>Edit</button>
                <button className="danger" onClick={() => api.remove(`/api/timetable/${row.id}`).then(load).catch((error) => setState.error(error.message))}>Delete</button>
              </>
            ) : null}
        />
      </div>
      {canManage && (
        <EntityForm
          title={editing ? "Edit Timetable" : "Create Timetable"}
          fields={fields}
          form={form}
          onChange={(name, value) => setForm({ ...form, [name]: value })}
          onSubmit={submit}
          onCancel={() => { setEditing(null); setForm(config.empty); }}
          isEditing={Boolean(editing)}
        />
      )}
    </section>
  );
}

function Attendance({ session, canManage }) {
  const empty = { studentId: "", facultyId: "", subject: "", date: new Date().toISOString().slice(0, 10), status: "PRESENT" };
  const [rows, setRows] = useState([]);
  const [students, setStudents] = useState([]);
  const [faculty, setFaculty] = useState([]);
  const [form, setForm] = useState(empty);
  const [state, setState] = useAsyncState();

  const load = () => {
    setState.loading();
    api.list("/api/attendance")
      .then((data) => {
        setRows(filterAttendanceForRole(data || [], session, students, faculty));
        setState.ready();
      })
      .catch((error) => setState.error(error.message));
  };

  useEffect(() => {
    setState.loading();
    Promise.all([api.list("/api/attendance"), api.list("/api/students"), api.list("/api/faculty")])
      .then(([attendanceData, studentData, facultyData]) => {
        setStudents(studentData || []);
        setFaculty(facultyData || []);
        setRows(filterAttendanceForRole(attendanceData || [], session, studentData || [], facultyData || []));
        setState.ready();
      })
      .catch((error) => setState.error(error.message));
  }, [session]);

  const submit = (event) => {
    event.preventDefault();
    setState.loading();
    api.postParams("/api/attendance/mark", form).then(() => { setForm(empty); load(); }).catch((error) => setState.error(error.message));
  };

  return (
    <section className={`content ${canManage ? "split" : ""}`}>
      <div className="panel">
        <div className="panel-head"><h2>Attendance</h2><button className="ghost" onClick={load}>Refresh</button></div>
        {state.errorMessage && <ErrorBox message={state.errorMessage} />}
        <Table
          rows={rows}
          columns={["id", "student.name", "faculty.name", "subject", "date", "status"]}
          emptyText="No attendance records found."
          actions={canManage ? (row) => (
              <>
                <button onClick={() => api.put(`/api/attendance/${row.id}?status=PRESENT`).then(load).catch((error) => setState.error(error.message))}>Present</button>
                <button onClick={() => api.put(`/api/attendance/${row.id}?status=ABSENT`).then(load).catch((error) => setState.error(error.message))}>Absent</button>
                <button className="danger" onClick={() => api.remove(`/api/attendance/${row.id}`).then(load).catch((error) => setState.error(error.message))}>Delete</button>
              </>
            ) : null}
        />
      </div>
      {canManage && (
        <EntityForm
          title="Mark Attendance"
          fields={[
            {
              name: "studentId",
              label: "Student",
              type: "select",
              required: true,
              options: students.map((student) => ({ value: student.id, label: `${student.name} - ${student.rollNumber}` })),
              placeholder: students.length ? "Select student" : "Create a student first",
            },
            {
              name: "facultyId",
              label: "Faculty",
              type: "select",
              required: true,
              options: faculty.map((item) => ({ value: item.id, label: `${item.name} - ${item.department}` })),
              placeholder: faculty.length ? "Select faculty" : "Create faculty first",
            },
            { name: "subject", label: "Subject", required: true },
            { name: "date", label: "Date", type: "date", required: true },
            { name: "status", label: "Status", type: "select", options: attendanceStatuses },
          ]}
          form={form}
          onChange={(name, value) => setForm({ ...form, [name]: value })}
          onSubmit={submit}
        />
      )}
    </section>
  );
}

function Notifications({ session, canCreate }) {
  const empty = { userId: session.id || "", title: "", message: "", role: "ALL" };
  const [rows, setRows] = useState([]);
  const [form, setForm] = useState(empty);
  const [state, setState] = useAsyncState();

  const load = () => {
    setState.loading();
    const rolePath = session.role === "ADMIN" ? "/api/notifications" : `/api/notifications/role/${notificationRole(session.role)}`;
    api.list(rolePath).then((data) => { setRows(data || []); setState.ready(); }).catch((error) => setState.error(error.message));
  };

  useEffect(load, []);

  const submit = (event) => {
    event.preventDefault();
    setState.loading();
    api.postParams("/api/notifications", form).then(() => { setForm(empty); load(); }).catch((error) => setState.error(error.message));
  };

  return (
    <section className={`content ${canCreate ? "split" : ""}`}>
      <div className="panel">
        <div className="panel-head"><h2>Notifications</h2><button className="ghost" onClick={load}>Refresh</button></div>
        {state.errorMessage && <ErrorBox message={state.errorMessage} />}
        <Table
          rows={rows}
          columns={["id", "title", "message", "sentBy.name", "targetRole", "isRead", "createdAt"]}
          emptyText="No notifications found."
          actions={(row) => (
            <>
              <button onClick={() => api.put(`/api/notifications/${row.id}/read`).then(load).catch((error) => setState.error(error.message))}>Read</button>
              {canCreate && <button className="danger" onClick={() => api.remove(`/api/notifications/${row.id}`).then(load).catch((error) => setState.error(error.message))}>Delete</button>}
            </>
          )}
        />
      </div>
      {canCreate && (
        <EntityForm
          title="Create Notification"
          fields={[
            { name: "userId", label: "Admin User ID", type: "number", required: true },
            { name: "title", label: "Title", required: true },
            { name: "message", label: "Message", type: "textarea", required: true },
            { name: "role", label: "Target", type: "select", options: targetRoles },
          ]}
          form={form}
          onChange={(name, value) => setForm({ ...form, [name]: value })}
          onSubmit={submit}
        />
      )}
    </section>
  );
}

function EntityForm({ title, fields, form, onChange, onSubmit, onCancel, isEditing }) {
  return (
    <form className="panel form-panel" onSubmit={onSubmit}>
      <div className="panel-head">
        <h2>{title}</h2>
        {isEditing && <button type="button" className="ghost" onClick={onCancel}>Cancel</button>}
      </div>
      <div className="grid two">
        {fields.map((field) => (
          <Field key={field.name} field={field} value={form[field.name] ?? ""} onChange={(value) => onChange(field.name, value)} />
        ))}
      </div>
      <button className="primary" type="submit">{isEditing ? "Save changes" : "Create"}</button>
    </form>
  );
}

function Field({ field, value, onChange }) {
  if (field.type === "select") {
    return <Select label={field.label} value={value} options={field.options} onChange={onChange} />;
  }
  if (field.type === "textarea") {
    return (
      <label className="wide">
        {field.label}
        <textarea value={value} required={field.required} onChange={(event) => onChange(event.target.value)} />
      </label>
    );
  }
  return (
    <Input
      label={field.label}
      type={field.type || "text"}
      value={value}
      min={field.min}
      max={field.max}
      required={field.required}
      onChange={onChange}
    />
  );
}

function Input({ label, value, onChange, type = "text", required, min, max }) {
  return (
    <label>
      {label}
      <input type={type} value={value} min={min} max={max} required={required} onChange={(event) => onChange(event.target.value)} />
    </label>
  );
}

function Select({ label, value, options, onChange }) {
  const normalized = options.map((option) => (
    typeof option === "object" ? option : { value: option, label: option }
  ));
  return (
    <label>
      {label}
      <select value={value} onChange={(event) => onChange(event.target.value)}>
        {!normalized.length && <option value="">No options available</option>}
        {normalized.map((option) => <option key={option.value} value={option.value}>{option.label}</option>)}
      </select>
    </label>
  );
}

function Table({ rows, columns, actions, emptyText }) {
  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            {columns.map((column) => <th key={column}>{columnLabel(column)}</th>)}
            {actions && <th>Actions</th>}
          </tr>
        </thead>
        <tbody>
          {!rows.length && (
            <tr><td className="empty" colSpan={columns.length + (actions ? 1 : 0)}>{emptyText}</td></tr>
          )}
          {rows.map((row) => (
            <tr key={row.id}>
              {columns.map((column) => <td key={column}>{formatValue(getPath(row, column))}</td>)}
              {actions && <td className="actions">{actions(row)}</td>}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function ErrorBox({ message }) {
  return <div className="error">{message}</div>;
}

function useStoredState(key, initialValue) {
  const [value, setValue] = useState(() => {
    const stored = localStorage.getItem(key);
    return stored ? JSON.parse(stored) : initialValue;
  });

  const setStored = (next) => {
    setValue(next);
    if (next) localStorage.setItem(key, JSON.stringify(next));
    else localStorage.removeItem(key);
  };

  return [value, setStored];
}

function useAsyncState() {
  const [state, setState] = useState({ isLoading: false, errorMessage: "" });
  return [state, {
    loading: () => setState({ isLoading: true, errorMessage: "" }),
    ready: () => setState({ isLoading: false, errorMessage: "" }),
    error: (message) => setState({ isLoading: false, errorMessage: message }),
  }];
}

function getPath(row, path) {
  return path.split(".").reduce((value, key) => value?.[key], row);
}

function formatValue(value) {
  if (value === true) return "Yes";
  if (value === false) return "No";
  if (value == null || value === "") return "-";
  return String(value);
}

function columnLabel(column) {
  return column.split(".").at(-1).replace(/([A-Z])/g, " $1").replace(/^./, (letter) => letter.toUpperCase());
}

function coercePayload(form, fields) {
  return Object.fromEntries(Object.entries(form).map(([key, value]) => {
    const field = fields.find((item) => item.name === key);
    if ((field?.type === "number" || key.endsWith("Id")) && value !== "") return [key, Number(value)];
    return [key, value];
  }));
}

function projectRow(row, empty) {
  return Object.fromEntries(Object.keys(empty).map((key) => [key, row[key] ?? empty[key]]));
}

function portalSubtitle(role) {
  if (role === "ADMIN") return "Administrative workspace";
  if (role === "Faculty") return "Teaching workspace";
  return "Student workspace";
}

function notificationRole(role) {
  if (role === "Faculty") return "FACULTY";
  if (role === "STUDENT") return "STUDENT";
  return "ALL";
}

function filterTimetableForRole(rows, session, faculty = [], students = []) {
  if (session.role === "ADMIN") return rows;
  if (session.role === "Faculty") {
    const currentFaculty = faculty.find((item) => sameEmail(item.email, session.email)) || faculty[0];
    return currentFaculty ? rows.filter((row) => row.faculty?.id === currentFaculty.id) : rows;
  }
  const currentStudent = students.find((item) => sameEmail(item.email, session.email)) || students[0];
  return currentStudent
    ? rows.filter((row) => row.department === currentStudent.department && row.semester === currentStudent.semester)
    : rows;
}

function filterAttendanceForRole(rows, session, students = [], faculty = []) {
  if (session.role === "ADMIN") return rows;
  if (session.role === "Faculty") {
    const currentFaculty = faculty.find((item) => sameEmail(item.email, session.email)) || faculty[0];
    return currentFaculty ? rows.filter((row) => row.faculty?.id === currentFaculty.id) : rows;
  }
  const currentStudent = students.find((item) => sameEmail(item.email, session.email)) || students[0];
  return currentStudent ? rows.filter((row) => row.student?.id === currentStudent.id) : rows;
}

function sameEmail(left, right) {
  return String(left || "").toLowerCase() === String(right || "").toLowerCase();
}
