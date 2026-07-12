import Card from "./Card";

const ExtraclassesCard = ({ count = 0 }) => {
  return (
    <Card title="Extraclass Load">
      <div className="h-full flex flex-col items-center justify-center gap-2 overflow-auto">
        <span className="text-5xl font-bold">{count}</span>
        <span className="text-sm text-slate-500">Extraclasses loaded</span>
      </div>
    </Card>
  );
};

export default ExtraclassesCard;
