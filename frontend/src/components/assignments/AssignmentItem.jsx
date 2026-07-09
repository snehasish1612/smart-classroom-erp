const AssignmentItem = ({
  title,
  subject,
  dueDate,
  status,
}) => {
  return (
    <div className="p-3 bg-slate-100 rounded-xl">
      <h3 className="font-medium">{title}</h3>

      <p className="text-sm text-gray-500 mt-1">
        {subject}
      </p>

      <div className="flex justify-between items-center mt-3">
        <span className="text-xs text-gray-600">
          Due: {dueDate}
        </span>

        <span
          className={`text-xs px-2 py-1 rounded-full ${
            status === "Pending"
              ? "bg-red-100 text-red-600"
              : "bg-green-100 text-green-600"
          }`}
        >
          {status}
        </span>
      </div>
    </div>
  );
};

export default AssignmentItem;
