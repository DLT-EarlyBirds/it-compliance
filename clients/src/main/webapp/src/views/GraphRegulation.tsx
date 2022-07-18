import React from "react";
import { useData } from "../contexts/DataContext";

function GraphRegulation() {
  const { regulations, rules, claimTemplates } = useData();

  const graphNodes = [
    ...regulations.map((regulation) => ({
      id: regulation.linearId.id,
      name: regulation.name,
    })),
    ...rules.map((rule) => ({ id: rule.linearId.id, name: rule.name })),
    ...claimTemplates.map((claimTemplate) => ({
      id: claimTemplate.linearId.id,
      name: claimTemplate.name,
    })),
  ];

  const graphEdges = [
    ...rules.map((rule) => ({
      source: rule.parentRegulation,
      target: rule.linearId.id,
    })),
    ...claimTemplates.map((claimTemplate) => ({
      source: claimTemplate.rule,
      target: claimTemplate.linearId.id,
    })),
  ];

  return <h1>Graph Regulation</h1>;
}

export default GraphRegulation;
