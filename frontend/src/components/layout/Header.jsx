import SearchBar from "./SearchBar";
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

const Header = ({ user, onLogout }) => {
  return (
    <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
      <div>
        <h1 className="text-3xl font-bold">
          {user?.name || "Smart Classroom"}
        </h1>
        <p className="text-sm text-slate-500">{user?.role}</p>
      </div>

      <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
        <button onClick={markAttendance} className="w-full rounded-md bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700">
          Make sure your Attandance
        </button>
        <SearchBar />
        <button
          onClick={onLogout}
          className="rounded-xl border px-4 py-2 text-sm font-medium text-white bg-blue-400 hover:bg-blue-600"
        >
          Logout
        </button>
      </div>
    </div>
  );
};

export default Header;
