import Card from "./Card";

const TimeCard = () => {
  const now = new Date();

  return (
    <Card title="Time Now">
      <div className="text-4xl font-bold">
        {now.toLocaleTimeString([], {
          hour: "numeric",
          minute: "2-digit",
        })}
      </div>
    </Card>
  );
};

export default TimeCard;
