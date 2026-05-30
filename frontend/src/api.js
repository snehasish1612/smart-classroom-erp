const API_BASE = import.meta.env.DEV ? "" : import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      ...(options.body instanceof FormData ? {} : { "Content-Type": "application/json" }),
      ...options.headers,
    },
    ...options,
  });

  const text = await response.text();
  const data = text ? tryJson(text) : null;

  if (!response.ok) {
    const message = data?.message || data?.error || text || `Request failed with ${response.status}`;
    throw new Error(message);
  }

  return data;
}

function tryJson(text) {
  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

const json = (body) => JSON.stringify(body);
const qs = (params) => new URLSearchParams(params).toString();

export const api = {
  list: (path) => request(path),
  create: (path, body) => request(path, { method: "POST", body: json(body) }),
  update: (path, body) => request(path, { method: "PUT", body: json(body) }),
  remove: (path) => request(path, { method: "DELETE" }),
  put: (path) => request(path, { method: "PUT" }),
  postParams: (path, params) => request(`${path}?${qs(params)}`, { method: "POST" }),
};
