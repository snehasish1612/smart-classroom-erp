import Card from "./Card";

const CalendarCard = () => {
  const today = new Date();

  return (
    <Card title="Calendar">
      <div className="h-48 flex flex-col justify-center">
        <span className="text-5xl font-bold">{today.getDate()}</span>
        <span className="text-slate-500">
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
