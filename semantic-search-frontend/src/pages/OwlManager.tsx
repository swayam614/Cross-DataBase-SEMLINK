import { useState } from "react"

function OwlManager() {

    const [file, setFile] = useState<File | null>(null)
    const [message, setMessage] = useState("")
    const [loading, setLoading] = useState(false)

    const [entities, setEntities] = useState<Entities | null>(null)
    const [selectedSource, setSelectedSource] = useState("")
    const [selectedTarget, setSelectedTarget] = useState("")
    const [relation, setRelation] = useState("sameAs")
    const [mappings, setMappings] = useState<Mapping[]>([])

    type Entities = {
        schemaClasses: string[]
        schemaProperties: string[]
        uploadedClasses: string[]
        uploadedProperties: string[]
        uploadedFileName: string[]
    }

    type Mapping = {
        sourceURI: string
        targetURI: string
        relation: string
    }

    const uploadFile = async () => {
        if (!file) {
            setMessage("Select a file")
            return
        }

        setLoading(true)
        setMessage("")

        try {
            const formData = new FormData()
            formData.append("file", file)

            const res = await fetch("http://localhost:8080/api/owl/upload", {
                method: "POST",
                body: formData
            })

            const text = await res.text()

            if (res.ok) {
                setMessage("✅ Uploaded")

                const data = await fetch("http://localhost:8080/api/owl/entities")
                    .then(res => res.json())

                setEntities(data)
            } else {
                setMessage("❌ " + text)
            }

        } catch {
            setMessage("❌ Upload failed")
        }

        setLoading(false)
    }

    // ✅ ADD MAPPING (NO DUPLICATES)
    const addMapping = () => {
        if (!selectedSource || !selectedTarget) return

        const exists = mappings.some(
            m =>
                m.sourceURI === selectedSource &&
                m.targetURI === selectedTarget &&
                m.relation === relation
        )

        if (exists) {
            setMessage("⚠️ Mapping already added")
            return
        }

        setMappings(prev => [
            ...prev,
            { sourceURI: selectedSource, targetURI: selectedTarget, relation }
        ])

        setSelectedSource("")
        setSelectedTarget("")
    }

    // ✅ SAVE MAPPINGS
    const submitMappings = async () => {
        if (mappings.length === 0) return

        const res = await fetch("http://localhost:8080/api/owl/map", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(mappings)
        })

        if (res.ok) {
            setMessage("✅ Mappings saved")
            setMappings([])
        } else {
            setMessage("❌ Failed to save mappings")
        }
    }

    const getName = (uri: string) => uri.split("#")[1] || uri

    return (
        <div className="p-6 bg-gray-100 min-h-screen">

            <h2 className="text-2xl font-bold mb-6">OWL Manager</h2>

            {/* Upload */}
            <div className="bg-white p-4 rounded-xl shadow mb-6 flex gap-3 items-center">
                <input
                    type="file"
                    onChange={(e) => {
                        if (e.target.files?.length) {
                            setFile(e.target.files[0])
                        }
                    }}
                />

                <button
                    onClick={uploadFile}
                    className="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600"
                >
                    {loading ? "Uploading..." : "Upload"}
                </button>

                {message && <p className="ml-4 text-sm">{message}</p>}
            </div>

            {entities && (
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">

                    {/* SCHEMA */}
                    <div className="bg-white p-4 rounded-xl shadow overflow-auto">
                        <h3 className="font-semibold text-center mb-3 bg-gray-100 p-2 rounded">
                            Schema
                        </h3>

                        <p className="font-medium mb-2">Classes</p>
                        {entities.schemaClasses.map(c => (
                            <div key={c}
                                onClick={() => setSelectedTarget(c)}
                                className="p-2 cursor-pointer hover:bg-gray-100 text-sm">
                                {getName(c)}
                            </div>
                        ))}

                        <p className="font-medium mt-4 mb-2">Properties</p>
                        {entities.schemaProperties.map(p => (
                            <div key={p}
                                onClick={() => setSelectedTarget(p)}
                                className="p-2 cursor-pointer hover:bg-gray-100 text-sm">
                                {getName(p)}
                            </div>
                        ))}
                    </div>

                    {/* MAPPING */}
                    <div className="bg-white p-4 rounded-xl shadow">

                        <h3 className="text-center font-semibold mb-3 bg-gray-100 p-2 rounded">
                            Mapping
                        </h3>

                        <p><b>Source:</b> {getName(selectedSource)}</p>
                        <p><b>Target:</b> {getName(selectedTarget)}</p>

                        <select
                            value={relation}
                            onChange={(e) => setRelation(e.target.value)}
                            className="w-full mt-3 p-2 border rounded"
                        >
                            <option value="sameAs">sameAs</option>
                            <option value="subPropertyOf">subPropertyOf</option>
                            <option value="equivalentClass">equivalentClass</option>
                        </select>

                        <button
                            onClick={addMapping}
                            className="w-full bg-blue-500 text-white py-2 mt-3 rounded"
                        >
                            Add Mapping
                        </button>

                        <button
                            onClick={submitMappings}
                            className="w-full bg-green-500 text-white py-2 mt-3 rounded"
                        >
                            Save Mappings
                        </button>

                        {/* 🔥 NEW: MAPPING LIST */}
                        {mappings.length > 0 && (
                            <div className="mt-4 border-t pt-3">
                                <h4 className="font-semibold mb-2 text-sm">Added Mappings</h4>

                                {mappings.map((m, index) => (
                                    <div
                                        key={index}
                                        className="flex justify-between items-center bg-gray-50 p-2 rounded mb-2"
                                    >
                                        <div className="text-xs">
                                            <b>{getName(m.sourceURI)}</b> →{" "}
                                            <i>{m.relation}</i> →{" "}
                                            <b>{getName(m.targetURI)}</b>
                                        </div>

                                        <button
                                            onClick={() =>
                                                setMappings(prev => prev.filter((_, i) => i !== index))
                                            }
                                            className="text-red-500 text-xs"
                                        >
                                            Delete
                                        </button>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>

                    {/* UPLOADED */}
                    <div className="bg-white p-4 rounded-xl shadow overflow-auto">

                        <h3 className="font-semibold text-center mb-3 bg-gray-100 p-2 rounded">
                            {entities.uploadedFileName?.[0] || "Uploaded File"}
                        </h3>

                        <p className="font-medium mb-2">Classes</p>
                        {entities.uploadedClasses.map(c => (
                            <div key={c}
                                onClick={() => setSelectedSource(c)}
                                className="p-2 cursor-pointer hover:bg-blue-100 text-sm">
                                {getName(c)}
                            </div>
                        ))}

                        <p className="font-medium mt-4 mb-2">Properties</p>
                        {entities.uploadedProperties.map(p => (
                            <div key={p}
                                onClick={() => setSelectedSource(p)}
                                className="p-2 cursor-pointer hover:bg-blue-100 text-sm">
                                {getName(p)}
                            </div>
                        ))}
                    </div>

                </div>
            )}
        </div>
    )
}

export default OwlManager