import React from "react"
import { useData } from "../contexts/DataContext"
import { ForceGraph2D, ForceGraph3D } from "react-force-graph"

function GraphRegulation() {
    const { regulations, rules, claimTemplates } = useData()

    console.log(regulations)
    console.log(rules)
    console.log(claimTemplates)
    const graphNodes = [
        ...regulations.map((regulation) => ({
            id: regulation.linearId.id,
            name: regulation.name,
            object: regulation,
            type: "regulation",
        })),
        ...rules.map((rule) => ({
            id: rule.linearId.id,
            name: rule.name,
            object: rule,
            type: "rule",
        })),
        ...claimTemplates.map((claimTemplate) => ({
            id: claimTemplate.linearId.id,
            name: claimTemplate.name,
            object: claimTemplate,
            type: "claimTemplate",
        })),
    ]

    const graphEdges = [
        ...rules.map((rule) => ({
            source: rule.linearId.id,
            target: rule.parentRegulation.pointer.id,
        })),
        ...claimTemplates.map((claimTemplate) => ({
            source: claimTemplate.linearId.id,
            target: claimTemplate.rule.pointer.id,
        })),
    ]

    return (
        <div>
            <ForceGraph2D
                width={1100}
                height={1000}
                linkDirectionalArrowLength={3.5}
                linkDirectionalArrowRelPos={1}
                nodeAutoColorBy="type"
                graphData={{ nodes: graphNodes, links: graphEdges }}
            />
        </div>
    )
}

export default GraphRegulation
