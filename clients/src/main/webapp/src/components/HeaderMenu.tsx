import React, {useState} from "react"
import {Menu, Button, Drawer, Radio} from "antd"
import {RadarChartOutlined, FileTextOutlined, OneToOneOutlined, HomeOutlined, FormOutlined} from "@ant-design/icons"
import {Link, useLocation} from "react-router-dom"
import {useNode} from "../contexts/NodeContext"
import {NodeEnum} from "enums"

const HeaderMenu = () => {
    const {pathname} = useLocation()
    const {currentNode, setCurrentNode} = useNode()

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
            <div className='flex justify-between'>
                <Menu className='justify-between w-1/4' theme={'dark'} mode="horizontal" selectedKeys={[pathname]} defaultSelectedKeys={["home"]}>
                    <Menu.Item key="/" icon={<HomeOutlined />}>
                        <Link to="/">Home</Link>
                    </Menu.Item>
                </Menu>
                <div className="flex items-center px-5">
                    <h2 className="text-green-600 my-0">Node: {currentNode}</h2>
                    <Button className="mx-5" type="primary" onClick={showDrawer}>
                        Change Node
                    </Button>
                </div>
            </div>

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
export default HeaderMenu
