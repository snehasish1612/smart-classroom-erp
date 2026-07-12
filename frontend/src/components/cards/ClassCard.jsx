import Card from "./Card";

const ClassCard = ({ subject, time }) => {
  return (
    <Card>
      <h4 className="font-semibold text-orange-600">{subject}</h4>
      <p className="text-sm text-slate-500 mt-1">{time}</p>
    </Card>
  );
};

export default ClassCard;
