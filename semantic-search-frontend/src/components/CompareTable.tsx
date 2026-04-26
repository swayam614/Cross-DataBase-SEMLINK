import type { Product } from "../services/api"

export default function CompareTable({ product }: { product: Product }) {

  // ✅ Find best price
  const bestPrice = Math.min(...product.results.map(r => r.price))

  return (
    <div className="border rounded-xl p-4 shadow">

      {/* Product Name */}
      <h2 className="text-lg font-semibold mb-3">
        {product.productName}
      </h2>

      {/* Table */}
      <table className="w-full border rounded-lg overflow-hidden">

        <thead>
          <tr className="bg-gray-100 text-sm">
            <th className="p-2 text-left">Source</th>
            <th className="p-2 text-center">Price</th>
            <th className="p-2 text-center">Rating</th>
            <th className="p-2 text-center">Link</th>
          </tr>
        </thead>

        <tbody>
          {product.results.map((item, index) => {
            const isBest = item.price === bestPrice

            return (
              <tr
                key={index}
                className={`border-t text-sm ${
                  isBest ? "bg-green-100" : ""
                }`}
              >
                {/* Source */}
                <td className="p-2">{item.source}</td>

                {/* Price */}
                <td className="p-2 text-center font-semibold">
                  ₹{item.price}
                  {isBest && (
                    <span className="ml-2 text-green-700 text-xs">
                      Best
                    </span>
                  )}
                </td>

                {/* Rating */}
                <td className="p-2 text-center">
                  ⭐ {item.rating}
                </td>

                {/* Link */}
                <td className="p-2 text-center">
                  <a
                    href={item.productLink}
                    target="_blank"
                    className="text-blue-500 underline"
                  >
                    View
                  </a>
                </td>

              </tr>
            )
          })}
        </tbody>

      </table>

    </div>
  )
}