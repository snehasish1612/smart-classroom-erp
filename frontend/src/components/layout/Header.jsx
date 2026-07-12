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
        <h1 className="text-3xl font-sans font-bold text-zinc-800 pt-2">
          {user?.name?.toUpperCase() || "Smart Classroom"}
        </h1>
        <p className="text-sm text-blue-600 pl-1">{user?.role?.toLowerCase()}</p>
      </div>

      <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
        <button onClick={markAttendance} className="w-full rounded-md bg-green-600 px-4 py-2 text-sm font-semibold text-white hover:bg-green-700">
          Mark Attendance via GPS
        </button>
        <SearchBar />
        <button
          onClick={onLogout}
          className="rounded-xl border-red px-4 py-2 text-sm font-medium text-white bg-orange-600 hover:bg-orange-700"
        >
          Logout
        </button>
      </div>
    </div>
  );
};

export default Header;
