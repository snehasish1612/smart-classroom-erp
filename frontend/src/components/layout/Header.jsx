import SearchBar from "./SearchBar";

const Header = ({ user, onLogout }) => {
  return (
    <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
      <div>
        <h1 className="text-3xl font-bold">
          {user?.name || "Smart Classroom"}
        </h1>
        <p className="text-sm text-slate-500">{user?.role}</p>
      </div>

      <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
        <SearchBar />
        <button
          onClick={onLogout}
          className="rounded-xl border px-4 py-2 text-sm font-medium hover:bg-white"
        >
          Logout
        </button>
      </div>
    </div>
  );
};

export default Header;
