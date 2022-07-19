import { useData } from "contexts/DataContext"
import React from "react"
import { useParams } from "react-router-dom"
import { Regulation as RegulationModel } from "models"

const Regulation = () => {
    const { id } = useParams()
    const { regulations } = useData()

    const regulation = regulations.find((r: RegulationModel) => r.linearId.id === id)

    if (!regulation) {
        return <h1>No Regulation Found</h1>
    }

    console.log(regulation)

    return <h1>Regulation</h1>
}

export default Regulation
