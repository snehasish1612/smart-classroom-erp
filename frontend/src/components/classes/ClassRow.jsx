const ClassRow = ({
  subject,
  faculty,
  room,
  startTime,
  endTime,
  status,
}) => {
  return (
    <div className="flex items-center justify-between p-4 bg-slate-100 rounded-xl">
      <div>
        <h3 className="font-semibold">{subject}</h3>
        <p className="text-sm text-gray-500">{faculty}</p>
      </div>

      <div className="text-sm text-gray-600">
        {room}
      </div>

      <div className="text-sm">
        {startTime} - {endTime}
      </div>

      <span
        className={`px-3 py-1 rounded-full text-xs font-medium ${
          status === "Live"
            ? "bg-green-100 text-green-600"
            : status === "Upcoming"
              ? "bg-yellow-100 text-yellow-600"
              : "bg-slate-200 text-slate-600"
        }`}
      >
        {status}
      </span>
    </div>
  );
};

export default ClassRow;
