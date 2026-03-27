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
  return (
    <div className="border rounded-xl p-4 shadow hover:shadow-lg transition">
      <h2 className="text-lg font-semibold">{product.productName}</h2>

      <p className="text-sm text-gray-500">{product.source}</p>

      <div className="mt-2 flex justify-between">
        <span className="font-bold">₹{product.price}</span>
        <span>⭐ {product.rating}</span>
      </div>

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
    </div>
  )
}

