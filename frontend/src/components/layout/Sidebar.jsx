import logo from "@/assets/surtech.png"
const Sidebar = ({ user, active, onSelect }) => {
  const baseMenu = [
    { key: "dashboard", label: "Dashboard" },
    { key: "classes", label: "Classes" },
    { key: "attendance", label: "Attendance" },
    { key: "messages", label: "Messages" },
    { key: "devices", label: "Devices" },
    { key: "reports", label: "Reports" },
  ];

  if (user?.role === "ADMIN") {
    baseMenu.push({ key: "admin", label: "Admin Portal" });
  }

  return (
    <aside className="w-full max-w-full rounded-3xl bg-white p-6 shadow md:w-64 md:min-h-[calc(100vh-2rem)]">


      <nav className="space-y-2">
        <div className="logo flex item-center my-10">
          <img
            src={logo}
            alt="SURTECH Logo"
            className="w-16 h-16 object-contain"
          />
          <h1 className="text-2xl font-bold mt-4">SURTECH</h1>
        </div>

        {baseMenu.map((item) => (
          <button
            key={item.key}
            onClick={() => onSelect && onSelect(item.key)}
            aria-pressed={active === item.key}
            className={`block w-full text-left text-sm font-medium px-3 py-2 rounded-lg ${active === item.key ? "bg-slate-100 text-slate-900" : "text-slate-700 hover:text-blue-600"
              }`}
          >
            {item.label}
          </button>
        ))}
      </nav>

      <div className="mt-60 text-left px-3 py-6 rounded-lg bg-slate-100 flex flex-col gap-4">
        <h2 className="text-lg text-blue-600">Admission Open for
          2026 Session</h2>
        <p className="text-xs">Limited seats available. Book your seat now.</p>
        <button
          className="rounded-xl border px-4 py-2 text-sm font-medium text-white bg-blue-600 hover:bg-blue-800"
        >
          Contact
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;