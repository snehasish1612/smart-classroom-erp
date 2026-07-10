const SearchBar = () => {
  return (
    <input
      type="text"
      placeholder="Search..."
      aria-label="Search dashboard"
      className="w-full max-w-xs min-w-0 border rounded-xl px-4 py-2"
    />
  );
};

export default SearchBar;