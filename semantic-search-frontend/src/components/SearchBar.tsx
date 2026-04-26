import { useState } from "react"

interface Props {
  onSearch: (query: string) => void
}

export default function SearchBar({ onSearch }: Props) {
  const [query, setQuery] = useState("")

  const handleSearch = () => {
    if (!query.trim()) return
    onSearch(query)
  }

  return (
    <div className="flex gap-2 mb-6">

      <input
        type="text"
        placeholder="Search products (e.g. iPhone 15, Sony TV...)"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        onKeyDown={(e) => e.key === "Enter" && handleSearch()} // ✅ Enter support
        className="border p-3 rounded-lg w-full focus:outline-none focus:ring-2 focus:ring-blue-400"
      />

      <button
        onClick={handleSearch}
        className="bg-blue-500 hover:bg-blue-600 text-white px-5 rounded-lg transition"
      >
        Search
      </button>

    </div>
  )
}