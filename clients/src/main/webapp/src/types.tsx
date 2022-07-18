export interface LinearId {
  id: string;
}

export interface Regulation {
  linearId: LinearId;
  name: string;
  description: string;
  version: string;
  releaseDate: string;
  isDeprecated: boolean;
}

export interface Rule {
  linearId: LinearId;
  name: string;
  ruleSpecification: string;
  parentRegulation: string;
  isDeprecated: boolean;
}

export interface ClaimTemplate {
  linearId: LinearId;
  name: string;
  templateDescription: string;
  rule: string;
}
