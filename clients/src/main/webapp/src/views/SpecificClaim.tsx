import { useData } from "contexts/DataContext"
import React from "react"
import { useParams } from "react-router-dom"
import { SpecificClaim as SpecificClaimModel } from "models"

const SpecificClaim = () => {
    const { id } = useParams()
    const { specificClaims } = useData()

    const specificClaim = specificClaims.find((r: SpecificClaimModel) => r.linearId.id === id)

    if (!specificClaim) {
        return <h1>No Specific Claim Found</h1>
    }

    console.log(specificClaim)

    return <h1>Specific Claim</h1>
}

export default SpecificClaim
