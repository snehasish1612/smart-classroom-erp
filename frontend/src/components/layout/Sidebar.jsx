const Sidebar = () => {
  const menu = [
    "Attendance",
    "Routine",
    "Classes",
    "Assignments",
    "Faculty",
    "Students",
    "Settings",
  ];

  return (
    <aside className="w-64 bg-white rounded-3xl p-6 shadow flex flex-col justify-between">
      <h1 className="text-2xl font-bold mb-8">SURTECH</h1>

      <nav className="space-y-4">
        {menu.map((item) => (
          <button
            key={item}
            className="block w-full text-left hover:text-blue-600"
          >
            {item}
          </button>
        ))}
      </nav>

      <div className="mt-auto h-24 bg-slate-200 rounded-2xl"></div>
    </aside>
  );
};

export default Sidebar;