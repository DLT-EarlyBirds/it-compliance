import { Routes, Route } from "react-router-dom";
import Home from "./screens/Home";
import ClaimTemplate from "./screens/ClaimTemplate";
import Regulation from "./screens/Regulation";
import RegulationGraph from "./screens/RegulationGraph";
import "./App.css";

import { Breadcrumb, Layout, Menu } from "antd";
import React from "react";
const { Content, Footer, Header } = Layout;

const App = () => (
  <React.Fragment>
    <Layout>
      <Header
        style={{
          position: "fixed",
          zIndex: 1,
          width: "100%",
        }}
      >
        <Menu mode="horizontal" defaultSelectedKeys={["mail"]}>
          <Menu.Item key="mail">Navigation One</Menu.Item>
        </Menu>
      </Header>
      <Content
        className="site-layout"
        style={{
          padding: "0 50px",
          marginTop: 64,
        }}
      >
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/regulations" element={<Regulation />} />
          <Route path="/claim-template" element={<ClaimTemplate />} />
          <Route path="/regulation-graph" element={<RegulationGraph />} />
        </Routes>
      </Content>
      <Footer
        style={{
          textAlign: "center",
        }}
      >
        It compliance
      </Footer>
    </Layout>
  </React.Fragment>
);

export default App;
