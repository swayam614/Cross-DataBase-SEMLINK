// import { useState } from "react"
// import SearchBar from "../components/SearchBar"
// import ProductCard from "../components/ProductCard"
// import { searchProducts } from "../services/api"
// import type { Product } from "../services/api"

// export default function Home() {

//   const [products, setProducts] = useState<Product[]>([])
//   const [loading, setLoading] = useState(false)

//   const handleSearch = async (query: string) => {
//     try {
//       setLoading(true)
//       const data = await searchProducts(query)
//       setProducts(data)
//     } catch (err) {
//       console.error(err)
//     } finally {
//       setLoading(false)
//     }
//   }

//   return (
//     <div className="p-6 max-w-4xl mx-auto">

//       <h1 className="text-2xl font-bold mb-4">
//         🔍 Semantic Product Search
//       </h1>

//       <SearchBar onSearch={handleSearch} />

//       {loading && <p>Loading...</p>}

//       <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
//         {products.map((p, i) => (
//           <ProductCard key={i} product={p} />
//         ))}
//       </div>

//     </div>
//   )
// }

import { useState } from "react"
import SearchBar from "../components/SearchBar"
import ProductCard from "../components/ProductCard"
import CompareTable from "../components/CompareTable"
import { searchProducts } from "../services/api"
import type { Product } from "../services/api"

export default function Home() {

  const [products, setProducts] = useState<Product[]>([])
  const [loading, setLoading] = useState(false)

  // ✅ View Toggle
  const [viewType, setViewType] = useState<"card" | "table">("card")

  // ✅ Sort state
  const [sortType, setSortType] = useState<"price" | "rating" | "">("")

  const handleSearch = async (query: string) => {
    try {
      setLoading(true)
      const data = await searchProducts(query)

      // 🕒 SORT BY LATEST (NEW)
      const sorted = data.sort(
        (a, b) =>
          new Date(b.createdAt).getTime() -
          new Date(a.createdAt).getTime()
      )

      setProducts(sorted)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  // ✅ Sorting logic
  const sortedProducts = products.map(product => {
    const sortedResults = [...product.results]
  
    if (sortType === "price") {
      sortedResults.sort((a, b) => {
        if (a.price === null) return 1
        if (b.price === null) return -1
        return a.price - b.price
      })
    }
  
    if (sortType === "rating") {
      sortedResults.sort((a, b) => {
        if (a.rating === null) return 1
        if (b.rating === null) return -1
        return b.rating - a.rating
      })
    }
  
    return { ...product, results: sortedResults }
  })

  return (
    <div className="p-6 max-w-4xl mx-auto">

      <h1 className="text-2xl font-bold mb-4">
        🔍 Semantic Product Search
      </h1>

      <SearchBar onSearch={handleSearch} />

      {/* 🔘 SORT + VIEW CONTROLS */}
      <div className="flex flex-wrap gap-3 mt-4 mb-4">

        {/* Sort */}
        <button
          onClick={() => setSortType("price")}
          className="px-3 py-1 bg-gray-200 rounded"
        >
          Price ↑
        </button>

        <button
          onClick={() => setSortType("rating")}
          className="px-3 py-1 bg-gray-200 rounded"
        >
          Rating ↓
        </button>

        <button
          onClick={() => setSortType("")}
          className="px-3 py-1 bg-gray-100 rounded"
        >
          Reset
        </button>

        {/* View Toggle */}
        <button
          onClick={() => setViewType("card")}
          className={`px-3 py-1 rounded ${
            viewType === "card" ? "bg-blue-500 text-white" : "bg-gray-200"
          }`}
        >
          Card View
        </button>

        <button
          onClick={() => setViewType("table")}
          className={`px-3 py-1 rounded ${
            viewType === "table" ? "bg-blue-500 text-white" : "bg-gray-200"
          }`}
        >
          Table View
        </button>

      </div>

      {loading && <p>Loading...</p>}

      {/* ✅ RENDER BASED ON VIEW */}
      <div className="space-y-4">
        {sortedProducts.map((p, i) =>
          viewType === "card" ? (
            <ProductCard key={i} product={p} />
          ) : (
            <CompareTable key={i} product={p} />
          )
        )}
      </div>

    </div>
  )
}

