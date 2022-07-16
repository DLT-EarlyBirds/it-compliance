import React, { useEffect, useState } from "react";
import RegulationService from "../services/Regulation.service";
import RuleService from "../services/Rule.service";
import ClaimTemplateService from "../services/ClaimTemplate.service";
import { useNode } from "./NodeContext";
import { Spin } from "antd";

const DataContext = React.createContext();

function DataProvider(props) {
  const { currentNode, axiosInstance } = useNode();
  const [regulations, setRegulations] = useState([]);
  const [rules, setRules] = useState([]);
  const [claimTemplates, setClaimTemplates] = useState([]);
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
