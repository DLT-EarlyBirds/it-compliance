import React from "react";
import { Menu } from "antd";
import {
  RadarChartOutlined,
  FileTextOutlined,
  OneToOneOutlined,
  HomeOutlined,
  FormOutlined,
} from "@ant-design/icons";
import { Link, useLocation } from "react-router-dom";

const Header = () => {
  const { pathname } = useLocation();

  return (
    <Menu
      mode="horizontal"
      selectedKeys={[pathname]}
      defaultSelectedKeys={["home"]}
    >
      <Menu.Item key="/" icon={<HomeOutlined />}>
        <Link to="/">Home</Link>
      </Menu.Item>
      <Menu.Item key="/graph-regulation" icon={<RadarChartOutlined />}>
        <Link to="/graph-regulation">Graph Regulation</Link>
      </Menu.Item>
      <Menu.Item key="/regulations" icon={<OneToOneOutlined />}>
        <Link to="/regulations">Regulations</Link>
      </Menu.Item>
      <Menu.Item key="/rules" icon={<FormOutlined />}>
        <Link to="/rules">Rules</Link>
      </Menu.Item>
      <Menu.Item key="/claim-templates" icon={<FileTextOutlined />}>
        <Link to="/claim-templates">Claim Templates</Link>
      </Menu.Item>
    </Menu>
  );
};

export default Header;
