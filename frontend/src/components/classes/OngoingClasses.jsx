import ClassRow from "./ClassRow";

const OngoingClasses = ({ classes = [], isLoading }) => {
  return (
    <div className="bg-white rounded-3xl p-4 shadow">
      <div className="flex justify-between mb-4">
        <h2 className="font-semibold text-zinc-800">
          Ongoing Classes
        </h2>

        <button className="text-sm text-blue-600">
          Filter
        </button>
      </div>

      <div className="space-y-3">
        {isLoading && (
          <div className="p-4 bg-slate-100 rounded-xl text-sm text-slate-500">
            Loading timetable...
          </div>
        )}

        {!isLoading && classes.length === 0 && (
          <div className="p-4 bg-slate-100 rounded-xl text-sm text-slate-500">
            No active classes found.
          </div>
        )}

        {classes.map((item, index) => (
          <ClassRow key={item.id || index} {...item} />
        ))}
      </div>
    </div>
  );
};

export default OngoingClasses;
