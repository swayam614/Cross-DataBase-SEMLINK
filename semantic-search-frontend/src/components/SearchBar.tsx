import { useState } from "react"

interface Props {
  onSearch: (query: string) => void
}

export default function SearchBar({ onSearch }: Props) {
  const [query, setQuery] = useState("")

  return (
    <div className="flex gap-2 mb-6">
      <input
        type="text"
        placeholder="Search products..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        className="border p-2 rounded w-full"
      />
      <button
        onClick={() => onSearch(query)}
        className="bg-blue-500 text-white px-4 rounded"
      >
        Search
      </button>
    </div>
  )
}