export interface LinearId {
    externalId: string
    id: string
}

export interface LinearPointer {
    pointer: LinearId
    type: string
    isResolved: boolean
}

export interface Regulation {
    linearId: LinearId
    name: string
    description: string
    version: string
    releaseDate: string
    isDeprecated: boolean
    issuer: string
}

export interface RegulationDTO {
    linearId?: string
    name: string
    description: string
    version: string
    releaseDate: Date
}

export interface Rule {
    linearId: LinearId
    name: string
    ruleSpecification: string
    issuer: string
    involvedParties: string[]
    isDeprecated: boolean
    parentRegulation: LinearPointer
}

export interface RuleDTO {
    linearId?: string
    name: string
    ruleSpecification: string
    parentRegulation: string // parent regulation linear id
}

export interface ClaimTemplate {
    linearId: LinearId
    name: string
    templateDescription: string
    issuer: string
    involvedParties: string[]
    rule: LinearPointer
}

export interface ClaimTemplateDTO {
    linearId?: string
    name: string
    templateDescription: string
    rule: string // linear id of the rule
}

export interface ClaimTemplateSuggestion {
    linearId: LinearId
    name: string
    templateDescription: string
    issuer: string
    supervisoryAuthority: string
    rule: LinearPointer
}

export interface ClaimTemplateSuggestionDTO {
    linearId?: string
    name: string
    templateDescription: string
    rule: string // linear id of the rule
}

export interface SpecificClaim {
    linearId: LinearId
    name: string
    attachmentID?: string
    description: string
    financialServiceProvider: string
    supervisoryAuthority: string
    auditor: string
    claimTemplate: LinearPointer
    supportingClaims: LinearPointer[]
}

export interface SpecificClaimDTO {
    linearId?: string
    name: string
    claimSpecification: string
    claimTemplateLinearId: string // claim template linear id
    supportingClaimIds: string[] // supporting claims linear ids
}
