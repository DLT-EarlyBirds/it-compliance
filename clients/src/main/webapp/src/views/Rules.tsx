import React, { useState } from "react"
import { Table, Button } from "antd"
import CreateRule from "../components/CreateRule"
import { useData } from "../contexts/DataContext"
import { Rule } from "models"
import UpdateRule from "components/UpdateRule"
import { EditOutlined } from "@ant-design/icons"
import { useNode } from "../contexts/NodeContext"
import { NodeEnum } from "enums"
import { insertIf } from "utils"
import RuleService from "../services/Rule.service"

function Rules() {
    const { rules, setRules } = useData()
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
            render: (_ :string, { isDeprecated, linearId }: Rule) => {
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
