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
    const message = await response.text();
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
  createStudent(student) {
    return request("/api/students", {
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
  getTimetable() {
    return request("/api/timetable");
  },
  getAttendance() {
    return request("/api/attendance");
  },
};
