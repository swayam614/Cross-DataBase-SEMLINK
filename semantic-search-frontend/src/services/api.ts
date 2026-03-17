export interface Product {
  productName: string
  source: string
  price: number
  rating: number
  productLink: string
}

export const searchProducts = async (query: string): Promise<Product[]> => {
  const res = await fetch(`http://localhost:8080/api/products/search?q=${query}`)

  if (!res.ok) {
    throw new Error("Failed to fetch products")
  }

  return res.json()
}