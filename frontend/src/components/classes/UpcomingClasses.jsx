import ClassCard from "../cards/ClassCard";

const UpcomingClasses = ({ classes = [], isLoading }) => {
  return (
    <div className="grid gap-4 grid-cols-1 sm:grid-cols-2 xl:grid-cols-3">
      {isLoading && <ClassCard subject="Loading..." time="Please wait" />}

      {!isLoading && classes.length === 0 && (
        <ClassCard subject="No upcoming classes" time="Today" />
      )}

      {!isLoading &&
        classes.map((item) => (
          <ClassCard
            key={item.id}
            subject={item.subject}
            time={item.startTime}
          />
        ))}
    </div>
  );
};

export default UpcomingClasses;
