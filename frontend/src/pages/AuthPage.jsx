import { useState } from "react";
import { api, clearSession, saveSession } from "../api";

const initialForm = {
  name: "",
  email: "",
  password: "",
  role: "STUDENT",
  rollNumber: "",
  department: "",
  semester: "1",
  phone: "",
  designation: "",
  subjectsTaught: "",
};

const AuthPage = ({ onAuthenticated }) => {
  const [mode, setMode] = useState("login");
  const [form, setForm] = useState(initialForm);
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const isRegistering = mode === "register";

  const updateField = (event) => {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  };

  const submit = async (event) => {
    event.preventDefault();
    setError("");
    setIsSubmitting(true);

    try {
      if (isRegistering) {
        try {
          await api.register({
            name: form.name,
            email: form.email,
            password: form.password,
            role: form.role,
          });
        } catch (registerError) {
          if (!registerError.message.includes("Email already exists")) {
            throw registerError;
          }
        }
      }

      const session = saveSession(
        await api.login({
          email: form.email,
          password: form.password,
        }),
      );

      if (isRegistering && form.role === "STUDENT") {
        const students = await api.getStudents();
        const existingStudent = students.find((item) => item.email === form.email);

        if (existingStudent) {
          onAuthenticated(session);
          return;
        }

        const sections = await api.getSections();
        const section = sections.find(
          (item) => Number(item.semester) === Number(form.semester),
        ) || sections[0];

        if (!section) {
          throw new Error("No section found. Please create a section before registering students.");
        }

        await api.createStudent(section.id, {
          name: form.name,
          email: form.email,
          rollNumber: form.rollNumber,
          department: form.department,
          semester: Number(form.semester),
          phone: form.phone || null,
        });
      }

      if (isRegistering && form.role === "FACULTY") {
        const faculty = await api.getFaculty();
        const existingFaculty = faculty.find((item) => item.email === form.email);

        if (!existingFaculty) {
          await api.createFaculty({
            name: form.name,
            email: form.email,
            department: form.department,
            phone: form.phone || null,
            designation: form.designation,
            subjectsTaught: form.subjectsTaught || null,
          });
        }
      }

      onAuthenticated(session);
    } catch (authError) {
      if (isRegistering) {
        clearSession();
      }

      setError(authError.message);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-6">
      <form
        onSubmit={submit}
        className="w-full max-w-md bg-white rounded-2xl shadow p-8 space-y-5"
      >
        <div>
          <p className="text-sm font-semibold text-blue-600">SURTECH ERP</p>
          <h1 className="text-3xl font-bold mt-2">
            {isRegistering ? "Create account" : "Sign in"}
          </h1>
        </div>

        {isRegistering && (
          <label className="block">
            <span className="text-sm font-medium text-slate-700">Name</span>
            <input
              name="name"
              value={form.name}
              onChange={updateField}
              className="mt-1 w-full border rounded-xl px-4 py-3"
              required
            />
          </label>
        )}

        <label className="block">
          <span className="text-sm font-medium text-slate-700">Email</span>
          <input
            type="email"
            name="email"
            value={form.email}
            onChange={updateField}
            className="mt-1 w-full border rounded-xl px-4 py-3"
            required
          />
        </label>

        <label className="block">
          <span className="text-sm font-medium text-slate-700">Password</span>
          <input
            type="password"
            name="password"
            value={form.password}
            onChange={updateField}
            className="mt-1 w-full border rounded-xl px-4 py-3"
            minLength={6}
            required
          />
        </label>

        {isRegistering && (
          <>
            <label className="block">
              <span className="text-sm font-medium text-slate-700">Role</span>
              <select
                name="role"
                value={form.role}
                onChange={updateField}
                className="mt-1 w-full border rounded-xl px-4 py-3 bg-white"
              >
                <option value="STUDENT">Student</option>
                <option value="FACULTY">Faculty</option>
                <option value="ADMIN">Admin</option>
              </select>
            </label>

            {form.role === "STUDENT" && (
              <>
                <label className="block">
                  <span className="text-sm font-medium text-slate-700">
                    Roll number
                  </span>
                  <input
                    name="rollNumber"
                    value={form.rollNumber}
                    onChange={updateField}
                    className="mt-1 w-full border rounded-xl px-4 py-3"
                    required
                  />
                </label>

                <label className="block">
                  <span className="text-sm font-medium text-slate-700">
                    Department
                  </span>
                  <input
                    name="department"
                    value={form.department}
                    onChange={updateField}
                    className="mt-1 w-full border rounded-xl px-4 py-3"
                    required
                  />
                </label>

                <div className="grid grid-cols-2 gap-3">
                  <label className="block">
                    <span className="text-sm font-medium text-slate-700">
                      Semester
                    </span>
                    <input
                      type="number"
                      name="semester"
                      value={form.semester}
                      onChange={updateField}
                      min="1"
                      max="8"
                      className="mt-1 w-full border rounded-xl px-4 py-3"
                      required
                    />
                  </label>

                  <label className="block">
                    <span className="text-sm font-medium text-slate-700">
                      Phone
                    </span>
                    <input
                      name="phone"
                      value={form.phone}
                      onChange={updateField}
                      pattern="[0-9]{10}"
                      className="mt-1 w-full border rounded-xl px-4 py-3"
                    />
                  </label>
                </div>
              </>
            )}

            {form.role === "FACULTY" && (
              <>
                <label className="block">
                  <span className="text-sm font-medium text-slate-700">
                    Department
                  </span>
                  <input
                    name="department"
                    value={form.department}
                    onChange={updateField}
                    className="mt-1 w-full border rounded-xl px-4 py-3"
                    required
                  />
                </label>

                <label className="block">
                  <span className="text-sm font-medium text-slate-700">
                    Designation
                  </span>
                  <input
                    name="designation"
                    value={form.designation}
                    onChange={updateField}
                    className="mt-1 w-full border rounded-xl px-4 py-3"
                    required
                  />
                </label>

                <label className="block">
                  <span className="text-sm font-medium text-slate-700">
                    Subjects taught
                  </span>
                  <input
                    name="subjectsTaught"
                    value={form.subjectsTaught}
                    onChange={updateField}
                    className="mt-1 w-full border rounded-xl px-4 py-3"
                  />
                </label>

                <label className="block">
                  <span className="text-sm font-medium text-slate-700">
                    Phone
                  </span>
                  <input
                    name="phone"
                    value={form.phone}
                    onChange={updateField}
                    pattern="[0-9]{10}"
                    className="mt-1 w-full border rounded-xl px-4 py-3"
                  />
                </label>
              </>
            )}
          </>
        )}

        {error && (
          <p className="rounded-xl bg-red-50 text-red-700 px-4 py-3 text-sm">
            {error}
          </p>
        )}

        <button
          type="submit"
          disabled={isSubmitting}
          className="w-full rounded-xl bg-blue-600 text-white font-semibold py-3 disabled:opacity-60"
        >
          {isSubmitting
            ? "Please wait..."
            : isRegistering
              ? "Create and sign in"
              : "Sign in"}
        </button>

        <button
          type="button"
          onClick={() => {
            setMode(isRegistering ? "login" : "register");
            setError("");
          }}
          className="w-full text-sm text-blue-700"
        >
          {isRegistering
            ? "Already have an account? Sign in"
            : "Need an account? Register"}
        </button>
      </form>
    </div>
  );
};

export default AuthPage;
