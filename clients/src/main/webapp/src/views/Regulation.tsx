import { useData } from "contexts/DataContext"
import React from "react"
import { useParams } from "react-router-dom"
import { Regulation as RegulationModel, Rule } from "models"

const Regulation = () => {
    const { id } = useParams()
    const { regulations, rules } = useData()

    const regulation = regulations.find((r: RegulationModel) => r.linearId.id === id)

    if (!regulation) {
        return <h1>No Regulation Found</h1>
    }

    const relatedRules = rules.filter((rule: Rule) => rule.parentRegulation.pointer.id === regulation.linearId.id)
    console.log(regulation)
    console.log(relatedRules)

    return <h1>Regulation</h1>
}

export default Regulation
