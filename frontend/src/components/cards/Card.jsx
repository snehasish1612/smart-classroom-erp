const Card = ({ title, children, className = "" }) => {
  return (
    <div
      className={`bg-white rounded-3xl p-4 shadow ${className}`}
    >
      <h3 className="font-semibold mb-4">{title}</h3>
      {children}
    </div>
  );
};

export default Card;