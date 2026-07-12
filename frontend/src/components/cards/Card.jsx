const Card = ({ title, children, className = "" }) => {
  return (
    <div
      className={`bg-white rounded-2xl p-4 shadow transition-transform duration-200 ease-in-out hover:scale-101 ${className}`}
    >
      <h3 className="font-semibold text-zinc-800 mb-4">{title}</h3>
      {children}
    </div>
  );
};

export default Card;