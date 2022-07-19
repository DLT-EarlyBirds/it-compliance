import React, { useState } from "react"
import { Menu, Button, Drawer, Radio } from "antd"
import { RadarChartOutlined, FileTextOutlined, OneToOneOutlined, HomeOutlined, FormOutlined } from "@ant-design/icons"
import { Link, useLocation } from "react-router-dom"
import { useNode } from "../contexts/NodeContext"
import { NodeEnum } from "enums"

const Sidebar = () => {
    const { pathname } = useLocation()
    const { currentNode, setCurrentNode } = useNode()

    const [visible, setVisible] = useState(false)

    const showDrawer = () => {
        setVisible(true)
    }

    const onClose = () => {
        setVisible(false)
    }

    const handleNodeChange = (e: any) => {
        setCurrentNode(e.target.value)
    }

    return (
        <>
            <Menu mode="vertical" selectedKeys={[pathname]} defaultSelectedKeys={["home"]}>
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
                <Menu.Item key="/specific-claims" icon={<FileTextOutlined />}>
                    <Link to="/specific-claims">Specific Claims</Link>
                </Menu.Item>
            </Menu>
            <Drawer title="All nodes in the system" placement="bottom" closable={false} onClose={onClose} visible={visible} height="200">
                <h2>You are currently connected to:</h2>
                <Radio.Group defaultValue={currentNode} buttonStyle="solid" onChange={handleNodeChange}>
                    <Radio.Button value={NodeEnum.SUPERVISORY_AUTHORITY}>{NodeEnum.SUPERVISORY_AUTHORITY}</Radio.Button>
                    <Radio.Button value={NodeEnum.BRAIN_FINANCE}>{NodeEnum.BRAIN_FINANCE}</Radio.Button>
                    <Radio.Button value={NodeEnum.CAPITALS_HOLDING}>{NodeEnum.CAPITALS_HOLDING}</Radio.Button>
                    <Radio.Button value={NodeEnum.AUDITOR}>{NodeEnum.AUDITOR}</Radio.Button>
                </Radio.Group>
            </Drawer>
        </>
    )
}

export default Sidebar
