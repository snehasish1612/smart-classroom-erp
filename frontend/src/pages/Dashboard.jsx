import { useEffect, useMemo, useState } from "react";
import { api } from "../api";
import Sidebar from "../components/layout/Sidebar";
import Header from "../components/layout/Header";
import UpcomingClasses from "../components/classes/UpcomingClasses";
import AttendanceCard from "../components/cards/AttendanceCard";
import CalendarCard from "../components/cards/CalendarCard";
import TimeCard from "../components/cards/TimeCard";
import OngoingClasses from "../components/classes/OngoingClasses";
import Assignments from "../components/assignments/Assignments";
import ExtraclassesCard from "../components/cards/ExtraclassesCard";

const getMinutes = (time) => {
  if (!time) {
    return 0;
  }

  const [hours, minutes] = time.split(":").map(Number);
  return hours * 60 + minutes;
};

const formatTime = (time) => {
  if (!time) {
    return "TBA";
  }

  const [hours, minutes] = time.split(":").map(Number);
  const date = new Date();
  date.setHours(hours, minutes, 0, 0);

  return date.toLocaleTimeString([], {
    hour: "numeric",
    minute: "2-digit",
  });
};

const getClassStatus = (item) => {
  const now = new Date();
  const currentMinutes = now.getHours() * 60 + now.getMinutes();
  const start = getMinutes(item.startTime);
  const end = getMinutes(item.endTime);

  if (currentMinutes >= start && currentMinutes <= end) {
    return "Live";
  }

  return currentMinutes < start ? "Upcoming" : "Completed";
};

const Dashboard = ({ user, onLogout }) => {
  const [timetable, setTimetable] = useState([]);
  const [attendance, setAttendance] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let isMounted = true;

    Promise.all([api.getTimetable(), api.getAttendance()])
      .then(([timetableData, attendanceData]) => {
        if (!isMounted) {
          return;
        }

        setTimetable(Array.isArray(timetableData) ? timetableData : []);
        setAttendance(Array.isArray(attendanceData) ? attendanceData : []);
      })
      .catch((dashboardError) => {
        if (isMounted) {
          setError(dashboardError.message);
        }
      })
      .finally(() => {
        if (isMounted) {
          setIsLoading(false);
        }
      });

    return () => {
      isMounted = false;
    };
  }, []);

  const classes = useMemo(
    () =>
      timetable
        .map((item) => ({
          id: item.id,
          subject: item.subject,
          faculty: item.faculty?.name || "Faculty TBA",
          room: item.classroom?.roomNumber || "Room TBA",
          startTime: formatTime(item.startTime),
          endTime: formatTime(item.endTime),
          startMinutes: getMinutes(item.startTime),
          status: getClassStatus(item),
        }))
        .sort((first, second) => first.startMinutes - second.startMinutes),
    [timetable],
  );

  const upcomingClasses = classes
    .filter((item) => item.status === "Upcoming")
    .slice(0, 3);

  const visibleClasses = classes
    .filter((item) => item.status !== "Completed")
    .slice(0, 4);

  const presentCount = attendance.filter(
    (item) => item.status === "PRESENT",
  ).length;
  const attendancePercent = attendance.length
    ? Math.round((presentCount / attendance.length) * 100)
    : 0;

  return (
    <div className="flex min-h-screen bg-slate-100 p-4 gap-4">
      <Sidebar />

      <main className="flex-1 overflow-auto">
        <Header user={user} onLogout={onLogout} />

        {error && (
          <div className="mt-4 rounded-xl bg-red-50 text-red-700 px-4 py-3">
            {error}
          </div>
        )}

        <div className="grid grid-cols-12 gap-4 mt-4">
          <div className="col-span-9">
            <UpcomingClasses classes={upcomingClasses} isLoading={isLoading} />
          </div>

          <div className="col-span-3">
            <TimeCard />
          </div>

          <div className="col-span-4">
            <AttendanceCard
              percentage={attendancePercent}
              total={attendance.length}
            />
          </div>

          <div className="col-span-4">
            <ExtraclassesCard count={classes.length} />
          </div>

          <div className="col-span-4">
            <CalendarCard />
          </div>

          <div className="col-span-9">
            <OngoingClasses classes={visibleClasses} isLoading={isLoading} />
          </div>

          <div className="col-span-3">
            <Assignments />
          </div>
        </div>
      </main>
    </div>
  );
};

export default Dashboard;
