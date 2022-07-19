import React from "react"
import "antd/dist/antd.min.css"
import "./index.css"
import Sidebar from "./components/Sidebar"
import Regulations from "./views/Regulations"
import GraphRegulation from "./views/GraphRegulation"
import Home from "./views/Home"
import Rules from "./views/Rules"
import ClaimTemplates from "./views/ClaimTemplates"
import SpecificClaims from "./views/SpecificClaims"
import ClaimTemplate from "views/ClaimTemplate"
import Regulation from "views/Regulation"
import Rule from "views/Rule"
import SpecificClaim from "views/SpecificClaim"
import { Routes, Route } from "react-router-dom"
import { NodeProvider } from "./contexts/NodeContext"
import { DataProvider } from "./contexts/DataContext"

const App = () => {
    return (
        <NodeProvider>
            <DataProvider>
                <Routes>
                    <Route path="/" element={<Home />}>
                        <Route path="/regulations" element={<Regulations />} />
                        <Route path="/rules" element={<Rules />} />
                        <Route path="/graph-regulation" element={<GraphRegulation />} />
                        <Route path="/claim-templates" element={<ClaimTemplates />} />
                        <Route path="/specific-claims" element={<SpecificClaims />} />
                        <Route path="/regulations/:id" element={<Regulation />} />
                        <Route path="/rules/:id" element={<Rule />} />
                        <Route path="/claim-templates/:id" element={<ClaimTemplate />} />
                        <Route path="/specific-claims/:id" element={<SpecificClaim />} />
                    </Route>
                </Routes>
            </DataProvider>
        </NodeProvider>
    )
}

export default App
