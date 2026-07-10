const ClassRow = ({
  subject,
  faculty,
  room,
  startTime,
  endTime,
  status,
}) => {
  return (
    <div className="flex flex-col gap-3 p-4 bg-slate-100 rounded-xl md:flex-row md:items-center md:justify-between">
      <div className="min-w-0">
        <h3 className="font-semibold truncate">{subject}</h3>
        <p className="text-sm text-slate-500 truncate">{faculty}</p>
      </div>

      <div className="text-sm text-slate-600">
        {room}
      </div>

      <div className="text-sm text-slate-600">
        {startTime} - {endTime}
      </div>

      <span
        className={`inline-block px-3 py-1 rounded-full text-xs font-medium ${
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
