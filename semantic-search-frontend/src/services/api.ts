export interface SourceItem {
  source: string
  price: number | null
  rating: number | null 
  productLink: string

  // 🕒 NEW FIELD
  createdAt: string
}

export interface Product {
  productName: string
  results: SourceItem[]
}
export const searchProducts = async (query: string): Promise<Product[]> => {
  const res = await fetch(`http://localhost:8080/api/products/search?q=${query}`)

  if (!res.ok) {
    throw new Error("Failed to fetch products")
  }

<<<<<<< HEAD
  return res.json()
}
=======
  const data = await res.json()

  const grouped: Record<string, Product> = {}

  data.forEach((item: any) => {
    if (!grouped[item.productName]) {
      grouped[item.productName] = {
        productName: item.productName,
        results: []
      }
    }

    grouped[item.productName].results.push({
      source: item.source,
      price: item.price ?? null,     // ✅ safe
      rating: item.rating ?? null,   // ✅ safe
      productLink: item.productLink
    })
  })

  return Object.values(grouped)
}
>>>>>>> 3683453 (Add manual mapping and remove the bug of duplication by making price and rating as class and assumed that this two data properties are changing so add in that time stamp and make it temporal)
