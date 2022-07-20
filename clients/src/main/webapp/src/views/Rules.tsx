import React, { useState } from "react"
import { Table, Button, TableProps } from "antd"
import CreateRule from "../components/CreateRule"
import { useData } from "../contexts/DataContext"
import { Regulation, Rule } from "models"
import UpdateRule from "components/UpdateRule"
import { EditOutlined, EyeOutlined } from "@ant-design/icons"
import { useNode } from "../contexts/NodeContext"
import { NodeEnum } from "enums"
import { insertIf } from "utils"
import RuleService from "../services/Rule.service"
import { Link } from "react-router-dom"
import { resolveX500Name } from "../services/resolveX500Name"

function Rules() {
    const { rules, regulations, setRules } = useData()
    const [isDrawerVisible, setIsDrawerVisible] = useState(false)
    const [rule, setCurrentRule] = useState<Rule | undefined>(undefined)
    const { currentNode, axiosInstance } = useNode()
    const isSupervisoryAuthority = currentNode === NodeEnum.SUPERVISORY_AUTHORITY

    const columns = [
        {
            title: "Name",
            dataIndex: "name",
        },
        {
            title: "Issuer",
            dataIndex: "issuer",
            render: (_: string, rule: Rule) => {
                return resolveX500Name(rule.issuer)
            },
        },
        {
            title: "Involved parties",
            dataIndex: "involvedParties",
            render: (_: string, rule: Rule) => {
                let output = ""
                rule.involvedParties.forEach((party) => {
                    output += resolveX500Name(party) + ", "
                })
                return output.substring(0, output.length - 2)
            },
        },
        {
            title: "Regulation",
            render: (_: string, rule: Rule) => {
                const regulation = regulations.find((regulation: Regulation) => rule.parentRegulation.pointer.id === regulation.linearId.id)
                return regulation ? regulation.name : ""
            },
        },
        {
            title: "View",
            render: (_: string, rule: Rule) => {
                return (
                    <Link className={"flex items-center justify-evenly"} to={`/rules/${rule.linearId.id}`}>
                        <EyeOutlined /> Open Rule
                    </Link>
                )
            },
        },
    ]

    return (
        <div>
            {isSupervisoryAuthority && <CreateRule />}
            <Table columns={columns} dataSource={rules} />
            {isDrawerVisible && <UpdateRule rule={rule as Rule} isVisible={isDrawerVisible} setIsVisible={setIsDrawerVisible} />}
        </div>
    )
}

export default Rules
