import React from "react"
import "antd/dist/antd.min.css"
import "./index.css"
import Header from "./components/Header"
import Regulations from "./views/Regulations"
import GraphRegulation from "./views/GraphRegulation"
import Home from "./views/Home"
import Rules from "./views/Rules"
import ClaimTemplates from "./views/ClaimTemplates"
import SpecificClaims from "./views/SpecificClaims"
import { Routes, Route } from "react-router-dom"
import { NodeProvider } from "./contexts/NodeContext"
import { DataProvider } from "./contexts/DataContext"

const App = () => {
    return (
        <NodeProvider>
            <Header />
            <div className="mx-10">
                <DataProvider>
                    <Routes>
                        <Route path="/" element={<Home />} />
                        <Route path="/regulations" element={<Regulations />} />
                        <Route path="/rules" element={<Rules />} />
                        <Route path="/graph-regulation" element={<GraphRegulation />} />
                        <Route path="/claim-templates" element={<ClaimTemplates />} />
                        <Route path="/specific-claims" element={<SpecificClaims />} />
                    </Routes>
                </DataProvider>
            </div>
        </NodeProvider>
    )
}

export default App
