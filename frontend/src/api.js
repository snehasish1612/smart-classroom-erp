const TOKEN_KEY = "smartClassroomToken";
const USER_KEY = "smartClassroomUser";

export function getStoredSession() {
  const token = localStorage.getItem(TOKEN_KEY);
  const userJson = localStorage.getItem(USER_KEY);

  if (!token || !userJson) {
    return null;
  }

  try {
    return {
      token,
      user: JSON.parse(userJson),
    };
  } catch {
    clearSession();
    return null;
  }
}

export function saveSession(authResponse) {
  const user = {
    id: authResponse.id,
    name: authResponse.name,
    email: authResponse.email,
    role: authResponse.role,
  };

  localStorage.setItem(TOKEN_KEY, authResponse.token);
  localStorage.setItem(USER_KEY, JSON.stringify(user));

  return {
    token: authResponse.token,
    user,
  };
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
}

async function request(path, options = {}) {
  const token = localStorage.getItem(TOKEN_KEY);
  const headers = new Headers(options.headers);

  if (!headers.has("Content-Type") && options.body) {
    headers.set("Content-Type", "application/json");
  }

  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  const response = await fetch(path, {
    ...options,
    headers,
  });

  if (response.status === 401) {
    clearSession();
    throw new Error("Session expired. Please sign in again.");
  }

  if (!response.ok) {
    const contentType = response.headers.get("content-type") || "";
    const message = contentType.includes("application/json")
      ? (await response.json()).message
      : await response.text();
    throw new Error(message || "Request failed");
  }

  if (response.status === 204) {
    return null;
  }

  const contentType = response.headers.get("content-type") || "";
  return contentType.includes("application/json")
    ? response.json()
    : response.text();
}

export const api = {
  login(credentials) {
    return request("/api/auth/login", {
      method: "POST",
      body: JSON.stringify(credentials),
    });
  },
  register(user) {
    return request("/api/users", {
      method: "POST",
      body: JSON.stringify(user),
    });
  },
  createStudent(sectionId, student) {
    return request(`/api/students/section/${sectionId}`, {
      method: "POST",
      body: JSON.stringify(student),
    });
  },
  createFaculty(faculty) {
    return request("/api/faculty", {
      method: "POST",
      body: JSON.stringify(faculty),
    });
  },
  createAuthority(authority) {
  return request("/api/authority", {
    method: "POST",
    body: JSON.stringify(authority),
  });
},
  getTimetable() {
    return request("/api/timetable");
  },
  createTimetable(streamId, payload) {
    return request(`/api/timetable/stream/${streamId}`, {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  updateTimetable(id, payload) {
    return request(`/api/timetable/${id}`, {
      method: "PUT",
      body: JSON.stringify(payload),
    });
  },
  getAttendance() {
    return request("/api/attendance");
  },
  markAttendanceByLocation(payload) {
    return request("/api/attendance/mark-by-location", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  getUsers() {
    return request("/api/users");
  },
  getStudents() {
    return request("/api/students");
  },
  getFaculty() {
    return request("/api/faculty");
  },
  getStreams() {
    return request("/api/streams");
  },
  getSections() {
    return request("/api/sections");
  },
  getDevices() {
    return request("/api/devices");
  },
  turnDeviceOn(id) {
    return request(`/api/devices/${id}/on`, { method: "PUT" });
  },
  turnDeviceOff(id) {
    return request(`/api/devices/${id}/off`, { method: "PUT" });
  },
  getNotifications(role) {
    return request(`/api/notifications/role/${role}`);
  },
  markNotificationRead(id) {
    return request(`/api/notifications/${id}/read`, { method: "PUT" });
  },
  markAllNotificationsRead(role) {
    return request(`/api/notifications/role/${role}/read-all`, {
      method: "PUT",
    });
  },
  createNotification(payload) {
    const params = new URLSearchParams(payload);
    return request(`/api/notifications/send/admin?${params.toString()}`, {
      method: "POST",
    });
  },
  getAssignments() {
    return request("/api/assignments");
  },
  createAssignment(payload) {
    return request("/api/assignments", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  submitAssignment(assignmentId, payload) {
    return request(`/api/assignments/${assignmentId}/submissions`, {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  getAssignmentSubmissions(assignmentId) {
    return request(`/api/assignments/${assignmentId}/submissions`);
  },
  reviewAssignmentSubmission(submissionId, payload) {
    return request(`/api/assignments/submissions/${submissionId}/review`, {
      method: "PUT",
      body: JSON.stringify(payload),
    });
  },
  getNotes(subject = "") {
    const params = subject ? `?subject=${encodeURIComponent(subject)}` : "";
    return request(`/api/notes${params}`);
  },
  generateNote(payload) {
    return request("/api/notes/generate", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  downloadNote(id) {
    return request(`/api/notes/${id}/download`);
  },
  sendMessage(payload) {
    return request("/api/chat/messages", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  getConversation(firstUserId, secondUserId) {
    return request(`/api/chat/conversation/${firstUserId}/${secondUserId}`);
  },
};
