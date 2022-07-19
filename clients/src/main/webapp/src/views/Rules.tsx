import React, { useState } from "react"
import {Table, Button, TableProps} from "antd"
import CreateRule from "../components/CreateRule"
import { useData } from "../contexts/DataContext"
import {Regulation, Rule} from "models"
import UpdateRule from "components/UpdateRule"
import { EditOutlined, EyeOutlined } from "@ant-design/icons"
import { useNode } from "../contexts/NodeContext"
import { NodeEnum } from "enums"
import { insertIf } from "utils"
import RuleService from "../services/Rule.service"
import { Link } from "react-router-dom"

function Rules() {
    const { rules, setRules } = useData()
    const [isDrawerVisible, setIsDrawerVisible] = useState(false)
    const [rule, setCurrentRule] = useState<Rule | undefined>(undefined)
    const { currentNode, axiosInstance } = useNode()
    const isSupervisoryAuthority = currentNode === NodeEnum.SUPERVISORY_AUTHORITY
    const filter = rules.map((rule) => {
        return {text: rule.name.split(' ')[0], value: rule}
    })

    const columns = [
        {
            title: "Name",
            dataIndex: "name",
            filters: [...filter],
            onFilter: (value: string, record: Rule) => record.name.includes(value),
            filterMultiple: true,
            sorter: (a: Rule, b: Rule) => a.name.length - b.name.length,
            sortDirections: ['descend']
        },
        {
            title: "Rule Specification",
            dataIndex: "ruleSpecification",
        },
        {
            title: "Parent regulation",
            dataIndex: ["parentRegulation", "pointer", "id"],
        },
        {
            issuer: "Issuer",
            dataIndex: "issuer",
        },
        {
            title: "Involved parties",
            dataIndex: "involvedParties",
        },
        ...insertIf(isSupervisoryAuthority, {
            title: "Deprecate",
            dataIndex: "isDeprecated",
            render: (_: string, { isDeprecated, linearId }: Rule) => {
                return (
                    <Button
                        type="primary"
                        disabled={isDeprecated}
                        onClick={() => {
                            RuleService.deprecate(axiosInstance, linearId.id).then((response) => {
                                const updatedRules = rules.map((r) => (r.linearId.id === response.linearId.id ? response : r))
                                setRules(updatedRules)
                            })
                        }}
                    >
                        {isDeprecated ? "Deprecate" : "Deprecated"}
                    </Button>
                )
            },
        }),
        ...insertIf(isSupervisoryAuthority, {
            title: "Actions",
            render: (_: string, rule: Rule) => {
                return (
                    <Button
                        type="primary"
                        onClick={() => {
                            setIsDrawerVisible(true)
                            setCurrentRule(rule)
                        }}
                    >
                        <EditOutlined />
                    </Button>
                )
            },
        }),
        {
            title: "View",
            render: (_: string, rule: Rule) => {
                return (
                    <Link to={`/rules/${rule.linearId.id}`}>
                        <EyeOutlined />
                    </Link>
                )
            },
        },
    ]

    const onChange: TableProps<Rule>['onChange'] = (pagination, filters, sorter, extra) => {
        console.log('params', pagination, filters, sorter, extra);
    };

    return (
        <div>
            {isSupervisoryAuthority && <CreateRule />}
            <Table columns={columns} dataSource={rules} onChange={onChange} />
            {isDrawerVisible && <UpdateRule rule={rule as Rule} isVisible={isDrawerVisible} setIsVisible={setIsDrawerVisible} />}
        </div>
    )
}

export default Rules
