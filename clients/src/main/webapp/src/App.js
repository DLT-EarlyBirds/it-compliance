import React from "react";
import "antd/dist/antd.min.css";
import "./index.css";
import Header from "./components/Header";
import Regulation from "./views/Regulation";
import GraphRegulation from "./views/GraphRegulation";
import Home from "./views/Home";
import ClaimTemplate from "./views/ClaimTemplate";
import { Routes, Route } from "react-router-dom";

const App = () => {
  return (
    <>
      <Header />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/regulation" element={<Regulation />} />
        <Route path="/graph-regulation" element={<GraphRegulation />} />
        <Route path="/claim-template" element={<ClaimTemplate />} />
      </Routes>
    </>
  );
};

export default App;
