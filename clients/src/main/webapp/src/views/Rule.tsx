import { useData } from "contexts/DataContext"
import React from "react"
import { useParams } from "react-router-dom"
import { Rule as RuleModel, ClaimTemplate } from "models"

const Rule = () => {
    const { id } = useParams()
    const { rules, claimTemplates } = useData()

    const rule = rules.find((r: RuleModel) => r.linearId.id === id)

    if (!rule) {
        return <h1>No Rule Found</h1>
    }

    const relatedClaimTemplates = claimTemplates.filter((claimTemplate: ClaimTemplate) => claimTemplate.rule.pointer.id === claimTemplate.linearId.id)
    console.log(relatedClaimTemplates)

    console.log(rule)

    return <h1>Rule</h1>
}

export default Rule
