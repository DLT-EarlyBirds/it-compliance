import React from "react";
import { useData } from "../contexts/DataContext";
import { ForceGraph2D } from "react-force-graph";

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

  return (
    <ForceGraph2D
      width={1100}
      height={1000}
      graphData={{ nodes: graphNodes, links: graphEdges }}
    />
  );
}

export default GraphRegulation;
