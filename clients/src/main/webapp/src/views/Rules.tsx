import React, { useState } from "react"
import { Table, Button } from "antd"
import CreateRule from "../components/CreateRule"
import { useData } from "../contexts/DataContext"
import { Rule } from "models"
import UpdateRule from "components/UpdateRule"
import { EditOutlined } from "@ant-design/icons"

function Rules() {
    const { rules } = useData()
    const [isDrawerVisible, setIsDrawerVisible] = useState(false)
    const [rule, setCurrentRule] = useState<Rule | undefined>(undefined)

    const columns = [
        {
            title: "Linear id",
            dataIndex: ["linearId", "id"],
        },
        {
            title: "Name",
            dataIndex: "name",
        },
        {
            title: "Rule Specification",
            dateIndex: "ruleSpecification",
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
        {
            title: "Deprecate",
            dataIndex: "isDeprecated",
            render: ({ isDeprecated, linearId }: Rule) => {
                return (
                    <Button type="primary" disabled={isDeprecated} onClick={() => console.log(linearId.id)}>
                        {isDeprecated ? "Deprecate" : "Deprecated"}
                    </Button>
                )
            },
        },
        {
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
        },
    ]

    return (
        <div>
            <CreateRule />
            <Table columns={columns} dataSource={rules} />
            {isDrawerVisible && <UpdateRule rule={rule as Rule} isVisible={isDrawerVisible} setIsVisible={setIsDrawerVisible} />}
        </div>
    )
}

export default Rules
