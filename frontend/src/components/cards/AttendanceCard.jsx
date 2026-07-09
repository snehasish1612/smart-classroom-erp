import Card from "./Card";

const AttendanceCard = ({ percentage = 0, total = 0 }) => {
  return (
    <Card title="Attendance">
      <div className="h-48 flex flex-col items-center justify-center">
        <span className="text-5xl font-bold">{percentage}%</span>
        <span className="text-sm text-slate-500 mt-2">
          {total} records synced
        </span>
      </div>
    </Card>
  );
};

export default AttendanceCard;
