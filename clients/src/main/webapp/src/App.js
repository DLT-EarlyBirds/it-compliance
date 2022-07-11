import React from "react";
import "antd/dist/antd.min.css";
import "./index.css";
import Header from "./components/Header";
import Regulation from "./views/Regulation";
import GraphRegulation from "./views/GraphRegulation";
import Home from "./views/Home";
import Rule from "./views/Rule";
import ClaimTemplate from "./views/ClaimTemplate";
import { Routes, Route } from "react-router-dom";

const App = () => {
  return (
    <>
      <Header />
      <div className="mx-10">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/regulations" element={<Regulation />} />
          <Route path="/rules" element={<Rule />} />
          <Route path="/graph-regulations" element={<GraphRegulation />} />
          <Route path="/claim-templates" element={<ClaimTemplate />} />
        </Routes>
      </div>
    </>
  );
};

export default App;
