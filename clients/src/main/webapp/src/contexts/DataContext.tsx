import React, { useEffect, useState } from "react";
import RegulationService from "../services/Regulation.service";
import RuleService from "../services/Rule.service";
import ClaimTemplateService from "../services/ClaimTemplate.service";
import { useNode } from "./NodeContext";
import { Spin } from "antd";
import { Regulation, Rule, ClaimTemplate } from "../models";

interface DataContextInteface {
  regulations: Regulation[];
  rules: Rule[];
  claimTemplates: ClaimTemplate[];
  setRegulations: (regulations: Regulation[]) => void;
  setRules: (rules: Rule[]) => void;
  setClaimTemplates: (claimTemplates: ClaimTemplate[]) => void;
}

const DataContext = React.createContext<DataContextInteface>({
  regulations: [],
  rules: [],
  claimTemplates: [],
  setRegulations: () => {},
  setRules: () => {},
  setClaimTemplates: () => {},
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
      RegulationService.getRegulations(axiosInstance).then((response) =>
        setRegulations(response)
      ),
      RuleService.getRules(axiosInstance).then((response) =>
        setRules(response)
      ),
      ClaimTemplateService.getClaimTemplates(axiosInstance).then((response) =>
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
