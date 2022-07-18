import React, { useEffect, useState } from "react";
import RegulationService from "../services/Regulation.service";
import RuleService from "../services/Rule.service";
import ClaimTemplateService from "../services/ClaimTemplate.service";
import { useNode } from "./NodeContext";
import { Spin } from "antd";
import { Regulation, Rule, ClaimTemplate, SpecificClaim } from "../models";

interface DataContextInteface {
  regulations: Regulation[];
  rules: Rule[];
  claimTemplates: ClaimTemplate[];
  specificClaims: SpecificClaim[];
  setRegulations: (regulations: Regulation[]) => void;
  setRules: (rules: Rule[]) => void;
  setClaimTemplates: (claimTemplates: ClaimTemplate[]) => void;
  setSpecificClaims: (specificClaims: SpecificClaim[]) => void;
}

const DataContext = React.createContext<DataContextInteface>({
  regulations: [],
  rules: [],
  claimTemplates: [],
  specificClaims: [],
  setRegulations: () => {},
  setRules: () => {},
  setClaimTemplates: () => {},
  setSpecificClaims: () => {},
});

function DataProvider(props: any) {
  const { currentNode, axiosInstance } = useNode();
  const [regulations, setRegulations] = useState<Regulation[]>([]);
  const [rules, setRules] = useState<Rule[]>([]);
  const [claimTemplates, setClaimTemplates] = useState<ClaimTemplate[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const data = { regulations, rules, claimTemplates, setRegulations };

  const fetchData = async () => {
    setIsLoading(true);
    await Promise.allSettled([
      RegulationService.getAll(axiosInstance).then((response) =>
        setRegulations(response)
      ),
      RuleService.getAll(axiosInstance).then((response) =>
        setRules(response)
      ),
      ClaimTemplateService.getAll(axiosInstance).then((response) =>
        setClaimTemplates(response)
      ),
    ]);

    setIsLoading(false);
  };

  useEffect(() => {
    fetchData();
  }, [currentNode]);

  if (isLoading) {
    return (
      <div className="">
        <Spin size="large" />
      </div>
    );
  }

  return <DataContext.Provider {...props} value={data} />;
}

function useData() {
  const context = React.useContext(DataContext);
  if (context === undefined) {
    throw new Error("useData must be used within a Data Provider");
  }

  return context;
}

export { DataProvider, useData };
