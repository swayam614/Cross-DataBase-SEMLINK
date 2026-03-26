import { useState } from "react"

function OwlManager() {

  const [file, setFile] = useState<File | null>(null)
  const [message, setMessage] = useState("")
  const [loading, setLoading] = useState(false)

  // ✅ DOWNLOAD SCHEMA
  const downloadSchema = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/owl/download-schema")

      const blob = await response.blob()
      const url = window.URL.createObjectURL(blob)

      const a = document.createElement("a")
      a.href = url
      a.download = "schema.owl"
      a.click()
    } catch (error) {
      setMessage("❌ Failed to download schema")
    }
  }

  // ✅ UPLOAD FILE
  const uploadFile = async () => {
    if (!file) {
      setMessage("⚠️ Please select a file")
      return
    }

    setLoading(true)
    setMessage("")

    try {
      const formData = new FormData()
      formData.append("file", file)

      const response = await fetch("http://localhost:8080/api/owl/upload", {
        method: "POST",
        body: formData,
      })

      const result = await response.text()

      if (response.ok) {
        setMessage("✅ " + result)
      } else {
        setMessage("❌ " + result)
      }

    } catch (error) {
      setMessage("❌ Upload failed")
    }

    setLoading(false)
  }

  return (
    <div style={{ padding: "40px", fontFamily: "sans-serif" }}>
      
      <h2>OWL Manager</h2>

      {/* DOWNLOAD */}
      <div style={{ marginBottom: "20px" }}>
        <button onClick={downloadSchema}>
          📥 Download Schema
        </button>
      </div>

      {/* UPLOAD */}
      <div style={{ marginBottom: "20px" }}>
        <input
          type="file"
          accept=".owl,.rdf,.xml"
          onChange={(e) => {
            if (e.target.files) {
              setFile(e.target.files[0])
            }
          }}
        />

        <button onClick={uploadFile} disabled={loading}>
          {loading ? "Uploading..." : "📤 Upload OWL"}
        </button>
      </div>

      {/* MESSAGE */}
      {message && (
        <div style={{ marginTop: "20px" }}>
          {message}
        </div>
      )}

    </div>
  )
}

export default OwlManager