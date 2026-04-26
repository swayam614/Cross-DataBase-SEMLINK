// import type { Product } from "../services/api"

// export default function ProductCard({ product }: { product: Product }) {
//   return (
//     <div className="border rounded-xl p-4 shadow hover:shadow-lg transition">
//       <h2 className="text-lg font-semibold">{product.productName}</h2>

//       <p className="text-sm text-gray-500">{product.source}</p>

//       <div className="mt-2 flex justify-between">
//         <span className="font-bold">₹{product.price}</span>
//         <span>⭐ {product.rating}</span>
//       </div>

//       <a
//         href={product.productLink}
//         target="_blank"
//         className="block mt-3 text-blue-500"
//       >
//         View Product →
//       </a>
//     </div>
//   )
// }

import type { Product } from "../services/api"

export default function ProductCard({ product }: { product: Product }) {

  // ✅ Filter valid prices only
  const validPrices = product.results
    .map(r => r.price)
    .filter((p): p is number => p !== null)

  const bestPrice = validPrices.length > 0 ? Math.min(...validPrices) : null

  return (
    <div className="border rounded-xl p-5 shadow hover:shadow-lg transition bg-white">

      <h2 className="text-xl font-bold mb-4">
        {product.productName}
      </h2>

      <div className="space-y-3">

        {product.results.map((item, index) => {

          const isBest = item.price !== null && item.price === bestPrice

          return (
            <div
              key={index}
              className={`flex justify-between items-center p-3 rounded-lg transition
                ${isBest ? "bg-green-100 border border-green-400" : "bg-gray-50 hover:bg-gray-100"}`}
            >

              {/* Left */}
              <div>
                <p className="font-medium">{item.source}</p>
                <p className="text-sm text-gray-500">
                  ⭐ {item.rating ?? "N/A"}
                </p>
              </div>

              {/* Middle */}
              <div className="text-center">

                <p className="font-bold text-lg">
                  {item.price !== null ? `₹${item.price}` : "N/A"}
                </p>

                {isBest && (
                  <p className="text-green-700 text-xs font-semibold">
                    Best Deal
                  </p>
                )}

                {!isBest && item.price !== null && bestPrice !== null && (
                  <p className="text-red-500 text-xs">
                    ₹{item.price - bestPrice} more
                  </p>
                )}

              </div>

              {/* Right */}
              <a
                href={item.productLink}
                target="_blank"
                className="text-blue-500 text-sm underline hover:text-blue-700"
              >
                View →
              </a>

            </div>
          )
        })}

      </div>
<<<<<<< HEAD

      {/* 🕒 TIMESTAMP (NEW) */}
      <p className="text-xs text-gray-400 mt-1">
        Updated: {new Date(product.createdAt).toLocaleString()}
      </p>

      <a
        href={product.productLink}
        target="_blank"
        className="block mt-3 text-blue-500"
      >
        View Product →
      </a>
=======
>>>>>>> 3683453 (Add manual mapping and remove the bug of duplication by making price and rating as class and assumed that this two data properties are changing so add in that time stamp and make it temporal)
    </div>
  )
}

