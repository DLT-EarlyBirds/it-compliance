import React, { useCallback, useEffect, useMemo, useRef, useState } from "react"
import { useData } from "../contexts/DataContext"
import { ForceGraph2D, ForceGraph3D } from "react-force-graph"
import { useNavigate } from "react-router-dom"
import THREE from "three"
import { CSS2DObject, CSS2DRenderer } from "three/examples/jsm/renderers/CSS2DRenderer"

function GraphRegulation() {
    const { regulations, rules, claimTemplates } = useData()

    const graphNodes = [
        ...regulations.map((regulation) => ({
            id: regulation.linearId.id,
            name: regulation.name,
            object: regulation,
            link: "regulations",
            neighbors: [],
        })),
        ...rules.map((rule) => ({
            id: rule.linearId.id,
            name: rule.name,
            object: rule,
            link: "rules",
            neighbors: [],
        })),
        ...claimTemplates.map((claimTemplate) => ({
            id: claimTemplate.linearId.id,
            name: claimTemplate.name,
            object: claimTemplate,
            link: "claim-templates",
            neighbors: [],
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
    let navigate = useNavigate()

    const data = useMemo(() => {
        const gData = { nodes: graphNodes, links: graphEdges }
        // cross-link node objects
        gData.links.forEach((link) => {
            //@ts-ignore
            const a = gData.nodes.filter((node) => node.id === link.source)[0]

            //@ts-ignore
            const b = gData.nodes.filter((node) => node.id === link.target)[0]
            // @ts-ignore
            a.neighbors.push(b)
            // @ts-ignore
            b.neighbors.push(a)
            // @ts-ignore
            !a.links && (a.links = [])
            // @ts-ignore
            !b.links && (b.links = [])

            // @ts-ignore
            a.links.push(link)
            // @ts-ignore
            b.links.push(link)
        })

        return gData
    }, [])

    const NODE_R = 4

    const [highlightNodes, setHighlightNodes] = useState(new Set())
    const [highlightLinks, setHighlightLinks] = useState(new Set())
    const [hoverNode, setHoverNode] = useState(null)

    const updateHighlight = () => {
        setHighlightNodes(highlightNodes)
        setHighlightLinks(highlightLinks)
    }

    const handleNodeHover = (node: any) => {
        highlightNodes.clear()
        highlightLinks.clear()
        if (node) {
            highlightNodes.add(node)
            //@ts-ignore
            node.neighbors.forEach((neighbor) => highlightNodes.add(neighbor))
            //@ts-ignore
            node.links.forEach((link) => highlightLinks.add(link))
        }

        setHoverNode(node || null)
        updateHighlight()
    }

    //@ts-ignore
    const handleLinkHover = (link) => {
        highlightNodes.clear()
        highlightLinks.clear()

        if (link) {
            highlightLinks.add(link)
            highlightNodes.add(link.source)
            highlightNodes.add(link.target)
        }

        updateHighlight()
    }

    const paintRing = useCallback(
        (node: any, ctx: any) => {
            // add ring just for highlighted nodes
            ctx.beginPath()
            ctx.arc(node.x, node.y, NODE_R * 1.2, 0, 2 * Math.PI, false)
            ctx.fillStyle = node === hoverNode ? "red" : "black"
            ctx.fillText(node.name, node.x - 30, node.y + 20)
            ctx.fill()
        },
        [hoverNode]
    )

    const fgRef = useRef()

    useEffect(() => {
        //@ts-ignore
        fgRef.current.d3Force("link").distance(100).iterations(20)
    }, [])
    // @ts-ignore
    // @ts-ignore
    return (
        <div>
            <ForceGraph2D
                ref={fgRef}
                width={1100}
                height={1000}
                linkDirectionalArrowLength={6.5}
                linkDirectionalArrowRelPos={1}
                nodeRelSize={NODE_R}
                autoPauseRedraw={false}
                linkWidth={(link) => (highlightLinks.has(link) ? 5 : 1)}
                linkDirectionalParticles={4}
                linkDirectionalParticleWidth={(link) => (highlightLinks.has(link) ? 4 : 0)}
                nodeAutoColorBy="link"
                //@ts-ignore
                onNodeClick={(node, event) => {
                    //@ts-ignore
                    navigate("/" + node.link + "/" + node.id)
                }}
                //@ts-ignore
                nodeCanvasObjectMode={(node) => (highlightNodes.has(node) ? "before" : "before")}
                nodeCanvasObject={paintRing}
                onNodeHover={handleNodeHover}
                onLinkHover={handleLinkHover}
                graphData={data}
            />
        </div>
    )
}

export default GraphRegulation
