import React, { useState } from "react";
import { Menu, Button, Drawer, Radio } from "antd";
import {
  RadarChartOutlined,
  FileTextOutlined,
  OneToOneOutlined,
  HomeOutlined,
  FormOutlined,
} from "@ant-design/icons";
import { Link, useLocation } from "react-router-dom";
import { useNode } from "../contexts/NodeContext";

const Header = () => {
  const { pathname } = useLocation();
  const { currentNode, setCurrentNode } = useNode();

  const [visible, setVisible] = useState(false);

  const showDrawer = () => {
    setVisible(true);
  };

  const onClose = () => {
    setVisible(false);
  };

  const handleNodeChange = (e) => {
    setCurrentNode(e.target.value);
  };

  return (
    <>
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
        <Menu.Item>
          <div className="flex">
            <h2 className="text-green-600">Node: {currentNode}</h2>
            <Button className="ml-2 mt-2" type="primary" onClick={showDrawer}>
              Change Node
            </Button>
          </div>
        </Menu.Item>
      </Menu>
      <Drawer
        title="All nodes in the system"
        placement="bottom"
        closable={false}
        onClose={onClose}
        visible={visible}
        height="200"
      >
        <h2>You are currently connected to:</h2>
        <Radio.Group
          defaultValue={currentNode}
          buttonStyle="solid"
          onChange={handleNodeChange}
        >
          <Radio.Button value="Supervisory Authority">
            Supervisory Authority
          </Radio.Button>
          <Radio.Button value="Brain Finance">Brain Finance</Radio.Button>
          <Radio.Button value="Capitals Holding">Capitals Holding</Radio.Button>
          <Radio.Button value="Auditor">Auditor</Radio.Button>
        </Radio.Group>
      </Drawer>
    </>
  );
};

export default Header;