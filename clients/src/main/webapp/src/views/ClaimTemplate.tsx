import { useData } from "contexts/DataContext"
import React from "react"
import { useParams } from "react-router-dom"
import { ClaimTemplate as ClaimTemplateModel } from "models"

const ClaimTemplate = () => {
    const { id } = useParams()
    const { claimTemplates } = useData()

    const claimTemplate = claimTemplates.find((r: ClaimTemplateModel) => r.linearId.id === id)

    if (!claimTemplate) {
        return <h1>No Claim Template Found</h1>
    }

    console.log(claimTemplate)

    return <h1>Claim Template</h1>
}

export default ClaimTemplate
