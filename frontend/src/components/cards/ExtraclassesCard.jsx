import Card from "./Card";

const ExtraclassesCard = ({ count = 0 }) => {
  return (
    <Card title="Routine">
      <div className="h-48 flex flex-col items-center justify-center">
        <span className="text-5xl font-bold">{count}</span>
        <span className="text-sm text-slate-500 mt-2">classes loaded</span>
      </div>
    </Card>
  );
};

export default ExtraclassesCard;
