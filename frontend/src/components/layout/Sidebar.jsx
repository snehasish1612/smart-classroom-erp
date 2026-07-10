const Sidebar = ({ user, active, onSelect }) => {
  const baseMenu = [
    { key: "dashboard", label: "Dashboard" },
    { key: "classes", label: "Classes" },
    { key: "attendance", label: "Attendance" },
    { key: "messages", label: "Messages" },
    { key: "devices", label: "Devices" },
    { key: "reports", label: "Reports" },
    { key: "settings", label: "Settings" },
  ];

  if (user?.role === "ADMIN") {
    baseMenu.push({ key: "admin", label: "Admin Portal" });
  }

  return (
    <aside className="w-full max-w-full rounded-3xl bg-white p-6 shadow md:w-64 md:min-h-[calc(100vh-2rem)]">
      <h1 className="text-2xl font-bold mb-8">SURTECH</h1>

      <nav className="space-y-2">
        {baseMenu.map((item) => (
          <button
            key={item.key}
            onClick={() => onSelect && onSelect(item.key)}
            aria-pressed={active === item.key}
            className={`block w-full text-left text-sm font-medium px-3 py-2 rounded-lg ${
              active === item.key ? "bg-slate-100 text-slate-900" : "text-slate-700 hover:text-blue-600"
            }`}
          >
            {item.label}
          </button>
        ))}
      </nav>

      <div className="mt-auto h-24 rounded-2xl bg-slate-200"></div>
    </aside>
  );
};

export default Sidebar;