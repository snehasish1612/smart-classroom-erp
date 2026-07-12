// import Card from "./Card";

// const AttendanceCard = ({ percentage = 0, total = 0 }) => {
//   return (
//     <Card title="Attendance">
//       <div className="h-48 flex flex-col items-center justify-center">
//         <span className="text-5xl font-bold">{percentage}%</span>
//         <span className="text-sm text-slate-500 mt-2">
//           {total} records synced
//         </span>
//       </div>
//     </Card>
//   );
// };

// export default AttendanceCard;

import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
} from "recharts";

const attendanceData = [
  { month: "Jan", attendance: 20 },
  { month: "Feb", attendance: 25 },
  { month: "Mar", attendance: 22 },
  { month: "Apr", attendance: 18 },
  { month: "May", attendance: 24 },
  { month: "Jun", attendance: 21 },
  { month: "Jul", attendance: 15 },
  { month: "Aug", attendance: 19 },
  { month: "Sep", attendance: 16 },
  { month: "Oct", attendance: 23 },
  { month: "Nov", attendance: 20 },
  { month: "Dec", attendance: 17 },
];

function AttendanceCard() {
  return (
    <div className="bg-white p-5 rounded-xl shadow-md w-full h-[400px]">
      <h2 className="text-l font-semibold mb-5 text-zinc-800">
        Monthly Attendance
      </h2>

      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={attendanceData}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="month" />
          <YAxis />
          <Tooltip />

          <Bar
            dataKey="attendance"
            fill="#3c73eb"
            radius={[8, 8, 0, 0]}
            barSize={25}
          />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}

export default AttendanceCard;