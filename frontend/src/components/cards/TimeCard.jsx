import { useEffect, useState } from "react";
import Card from "./Card";

const TimeCard = () => {
  const [now, setNow] = useState(new Date());

  useEffect(() => {
    const timer = window.setInterval(() => setNow(new Date()), 60000);
    return () => window.clearInterval(timer);
  }, []);

  return (
    <Card title="Current Time">
      <div className="text-4xl font-bold py-4">
        {now.toLocaleTimeString([], {
          hour: "numeric",
          minute: "2-digit",
        })}
      </div>
      <p className="mt-2 text-sm text-slate-500">
        {now.toLocaleDateString([], {
          weekday: "long",
          month: "long",
          day: "numeric",
        })}
      </p>
    </Card>
  );
};

export default TimeCard;
