
// import Card from "./Card";


// const CalendarCard = () => {
//   const today = new Date();

//   return (
//     <Card title="Today">
//       <div className="h-48 flex flex-col justify-center gap-3">
//         <span className="text-5xl font-bold">{date.getDate()}</span>
//         <span className="text-sm text-slate-500">
//           {date.toLocaleDateString([], {
//             weekday: "long",
//             month: "long",
//             year: "numeric",
//           })}
//         </span>
//       </div>
//     </Card>
//   );
// };

// export default CalendarCard;

import { useState } from "react";
import { Calendar } from "@/components/ui/calendar";
import Card from "./Card";

export default function CalendarPage() {
  const [date, setDate] = useState(new Date());

  return (
    <Card className="overflow-auto p-2">
      <div className="flex justify-center py-7  ">
        {/* Mobile */}
        <div className="block md:hidden">
          <Calendar
            mode="single"
            selected={date}
            onSelect={setDate}
            numberOfMonths={1}
            className="rounded-md border shadow"
          />
        </div>

        {/* Desktop */}
        <div className="hidden md:block">
          <Calendar
            mode="single"
            selected={date}
            onSelect={setDate}
            numberOfMonths={2}
            className="rounded-md border shadow"
          />
        </div>
      </div>
    </Card>
  );
}


