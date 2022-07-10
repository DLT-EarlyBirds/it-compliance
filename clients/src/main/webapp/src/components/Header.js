import React from "react";
import { Menu } from "antd";
import {
  RadarChartOutlined,
  FileTextOutlined,
  OneToOneOutlined,
  HomeOutlined,
} from "@ant-design/icons";
import { Link } from "react-router-dom";

const Header = () => (
  <Menu mode="horizontal" defaultSelectedKeys={["home"]}>
    <Menu.Item key="home" icon={<HomeOutlined />}>
      <Link to="/">Home</Link>
    </Menu.Item>
    <Menu.Item key="graph-regulation" icon={<RadarChartOutlined />}>
      <Link to="/graph-regulation">Graph Regulation</Link>
    </Menu.Item>
    <Menu.Item key="regulation" icon={<OneToOneOutlined />}>
      <Link to="/regulation">Regulations</Link>
    </Menu.Item>
    <Menu.Item key="claim-template" icon={<FileTextOutlined />}>
      <Link to="/claim-template">Claim Templates</Link>
    </Menu.Item>
  </Menu>
);

export default Header;
