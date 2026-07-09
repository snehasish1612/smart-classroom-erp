import AssignmentItem from "./AssignmentItem";

const Assignments = () => {
  const assignments = [
    {
      title: "ER Diagram Design",
      subject: "DBMS",
      dueDate: "12 June",
      status: "Pending",
    },
    {
      title: "OSI Model Report",
      subject: "CN",
      dueDate: "15 June",
      status: "Submitted",
    },
    {
      title: "CPU Scheduling",
      subject: "OS",
      dueDate: "18 June",
      status: "Pending",
    },
  ];

  return (
    <div className="bg-white rounded-3xl p-4 shadow">
      <h2 className="font-semibold mb-4">
        Assignments
      </h2>

      <div className="space-y-3">
        {assignments.map((assignment, index) => (
          <AssignmentItem
            key={index}
            {...assignment}
          />
        ))}
      </div>
    </div>
  );
};

export default Assignments;