import Card from "./Card";

const CalendarCard = () => {
  const today = new Date();

  return (
    <Card title="Today">
      <div className="h-48 flex flex-col justify-center gap-3">
        <span className="text-5xl font-bold">{today.getDate()}</span>
        <span className="text-sm text-slate-500">
          {today.toLocaleDateString([], {
            weekday: "long",
            month: "long",
            year: "numeric",
          })}
        </span>
      </div>
    </Card>
  );
};

export default CalendarCard;
