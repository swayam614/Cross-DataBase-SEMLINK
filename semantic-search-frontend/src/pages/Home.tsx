import { useState } from "react"
import SearchBar from "../components/SearchBar"
import ProductCard from "../components/ProductCard"
import { searchProducts } from "../services/api"
import type { Product } from "../services/api"

export default function Home() {

  const [products, setProducts] = useState<Product[]>([])
  const [loading, setLoading] = useState(false)

  const handleSearch = async (query: string) => {
    try {
      setLoading(true)
      const data = await searchProducts(query)
      setProducts(data)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="p-6 max-w-4xl mx-auto">

      <h1 className="text-2xl font-bold mb-4">
        🔍 Semantic Product Search
      </h1>

      <SearchBar onSearch={handleSearch} />

      {loading && <p>Loading...</p>}

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {products.map((p, i) => (
          <ProductCard key={i} product={p} />
        ))}
      </div>

    </div>
  )
}