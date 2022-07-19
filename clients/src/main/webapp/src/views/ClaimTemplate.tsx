import { useData } from "contexts/DataContext"
import React from "react"
import { useParams } from "react-router-dom"
import { ClaimTemplate as ClaimTemplateModel, SpecificClaim } from "models"

const ClaimTemplate = () => {
    const { id } = useParams()
    const { claimTemplates, specificClaims } = useData()

    const claimTemplate = claimTemplates.find((r: ClaimTemplateModel) => r.linearId.id === id)

    if (!claimTemplate) {
        return <h1>No Claim Template Found</h1>
    }

    const relatedSpecificClaims = specificClaims.filter((specificClaim: SpecificClaim) => specificClaim.claimTemplate.pointer.id === claimTemplate.linearId.id)
    console.log(relatedSpecificClaims)

    console.log(claimTemplate)

    return <h1>Claim Template</h1>
}

export default ClaimTemplate
