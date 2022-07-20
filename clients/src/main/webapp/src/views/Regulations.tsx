import React, { useState } from "react"
import { Table, Button } from "antd"
import CreateRegulation from "../components/CreateRegulation"
import UpdateRegulation from "../components/UpdateRegulation"
import { useData } from "../contexts/DataContext"
import { useNode } from "../contexts/NodeContext"
import { Regulation, Rule } from "models"
import { NodeEnum } from "enums"
import { EditOutlined, EyeOutlined } from "@ant-design/icons"
import { insertIf } from "utils"
import RegulationService from "../services/Regulation.service"
import RuleService from "../services/Rule.service"
import { Link } from "react-router-dom"
import { resolveX500Name } from "../services/resolveX500Name"

function Regulations() {
    const { regulations, setRegulations, setRules } = useData()
    const { currentNode, axiosInstance } = useNode()
    const [isDrawerVisible, setIsDrawerVisible] = useState(false)
    const [regulation, setCurrentRegulation] = useState<Regulation | undefined>(undefined)
    const isSupervisoryAuthority = currentNode === NodeEnum.SUPERVISORY_AUTHORITY
    const filter = regulations.map((regulation) => {
        return { text: regulation.name.split(" ")[0], value: regulation }
    })
    console.log(filter)

    const columns = [
        {
            title: "Name",
            dataIndex: "name",
        },
        {
            title: "Release date",
            dataIndex: "releaseDate",
            render: (_: string, regulation: Regulation) => {
                return new Date(Date.parse(regulation.releaseDate)).toDateString()
            },
        },
        {
            title: "Version",
            dataIndex: "version",
        },
        {
            title: "Issuer",
            dataIndex: "issuer",
            render: (_: string, regulation: Regulation) => {
                return resolveX500Name(regulation.issuer)
            },
        },
        {
            title: "View",
            render: (_: string, regulation: Regulation) => {
                return (
                    <Link className={"flex items-center justify-evenly"} to={`/regulations/${regulation.linearId.id}`}>
                        <EyeOutlined /> Open Regulation
                    </Link>
                )
            },
        },
    ]

    return (
        <div>
            {isSupervisoryAuthority && <CreateRegulation />}
            <Table columns={columns} dataSource={regulations} pagination={{ pageSize: 5 }} />
            {isDrawerVisible && <UpdateRegulation regulation={regulation as Regulation} isVisible={isDrawerVisible} setIsVisible={setIsDrawerVisible} />}
        </div>
    )
}

export default Regulations
