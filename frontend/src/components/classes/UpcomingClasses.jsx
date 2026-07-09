import ClassCard from "../cards/ClassCard";

const UpcomingClasses = ({ classes = [], isLoading }) => {
  return (
    <>
      <h2 className="font-semibold mb-3">
        Upcoming Classes
      </h2>

      <div className="grid grid-cols-3 gap-4">
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
    </>
  );
};

export default UpcomingClasses;
