import React, { useEffect, useState } from "react"
import RegulationService from "../services/Regulation.service"
import RuleService from "../services/Rule.service"
import ClaimTemplateService from "../services/ClaimTemplate.service"
import { useNode } from "./NodeContext"
import { Spin } from "antd"
import { Regulation, Rule, ClaimTemplate, SpecificClaim, ClaimTemplateSuggestion } from "../models"
import SpecificClaimService from "../services/SpecificClaim.service"

interface DataContextInteface {
    regulations: Regulation[]
    rules: Rule[]
    claimTemplates: ClaimTemplate[]
    specificClaims: SpecificClaim[]
    claimTemplatesSuggestions: ClaimTemplateSuggestion[]
    setRegulations: (regulations: Regulation[]) => void
    setRules: (rules: Rule[]) => void
    setClaimTemplates: (claimTemplates: ClaimTemplate[]) => void
    setSpecificClaims: (specificClaims: SpecificClaim[]) => void
    setClaimTemplatesSuggestions: (claimTemplatesSuggestions: ClaimTemplateSuggestion[]) => void
    fetchData: () => void
}

const DataContext = React.createContext<DataContextInteface>({
    regulations: [],
    rules: [],
    claimTemplates: [],
    specificClaims: [],
    claimTemplatesSuggestions: [],
    setRegulations: () => {},
    setRules: () => {},
    setClaimTemplates: () => {},
    setSpecificClaims: () => {},
    setClaimTemplatesSuggestions: () => {},
    fetchData: () => {},
})

function DataProvider(props: any) {
    const { currentNode, axiosInstance } = useNode()
    const [regulations, setRegulations] = useState<Regulation[]>([])
    const [rules, setRules] = useState<Rule[]>([])
    const [claimTemplates, setClaimTemplates] = useState<ClaimTemplate[]>([])
    const [specificClaims, setSpecificClaims] = useState<SpecificClaim[]>([])
    const [claimTemplatesSuggestions, setClaimTemplatesSuggestions] = useState<ClaimTemplateSuggestion[]>([])
    const [isLoading, setIsLoading] = useState(false)

    const fetchData = async () => {
        setIsLoading(true)
        await Promise.allSettled([
            RegulationService.getAll(axiosInstance).then((response) => setRegulations(response)),
            RuleService.getAll(axiosInstance).then((response) => setRules(response)),
            ClaimTemplateService.getAll(axiosInstance).then((response) => setClaimTemplates(response)),
            ClaimTemplateService.getSuggestions(axiosInstance).then((response) => setClaimTemplatesSuggestions(response)),
            SpecificClaimService.getAll(axiosInstance).then((response) => setSpecificClaims(response)),
        ])

        setIsLoading(false)
    }

    const data: DataContextInteface = {
        regulations,
        rules,
        claimTemplates,
        claimTemplatesSuggestions,
        specificClaims,
        setRegulations,
        setRules,
        setClaimTemplates,
        setSpecificClaims,
        setClaimTemplatesSuggestions,
        fetchData,
    }

    useEffect(() => {
        fetchData()
    }, [currentNode])

    if (isLoading) {
        return (
            <div className="">
                <Spin size="large" />
            </div>
        )
    }

    return <DataContext.Provider {...props} value={data} />
}

function useData() {
    const context = React.useContext(DataContext)
    if (context === undefined) {
        throw new Error("useData must be used within a Data Provider")
    }

    return context
}

export { DataProvider, useData }
