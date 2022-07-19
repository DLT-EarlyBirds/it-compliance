import React, { useState } from "react"
import { Table, Button } from "antd"
import { useData } from "../contexts/DataContext"
import { useNode } from "../contexts/NodeContext"
import { NodeEnum } from "enums"
import { ClaimTemplate, Regulation } from "models"
import CreateClaimTemplate from "../components/CreateClaimTemplate"
import { EditOutlined } from "@ant-design/icons"
import UpdateClaimTemplate from "components/UpdateClaimTemplate"

const claimTemplateSuggestions = [
    {
        title: "Linear id",
        dataIndex: ["linearId", "id"],
    },
    {
        name: "Name",
        dataIndex: "name",
    },
    {
        title: "Template Description",
        dataIndex: "templateDescription",
    },
    {
        title: "Issuer",
        dataIndex: "issuer",
    },
    {
        title: "Actions",
        dataIndex: ["linearId", "id"],
        render: ({ linearId }: Regulation) => {
            return (
                <>
                    <Button onClick={() => console.log(linearId.id)}>Reject</Button>
                    <Button type="primary" onClick={() => console.log(linearId.id)}>
                        Accept
                    </Button>
                </>
            )
        },
    },
]

function ClaimTemplates() {
    // TODO: Add claim template suggestions
    const { claimTemplates } = useData()
    const { currentNode } = useNode()
    const [isDrawerVisible, setIsDrawerVisible] = useState(false)
    const [claimTemplate, setCurrentClaimTemplate] = useState<ClaimTemplate | undefined>(undefined)

    const claimTemplateColumns = [
        {
            title: "Linear id",
            dataIndex: ["linearId", "id"],
        },
        {
            title: "Name",
            dataIndex: "name",
        },
        {
            title: "Template Description",
            dataIndex: "templateDescription",
        },
        {
            title: "Issuer",
            dateIndex: "issuer",
        },
        {
            title: "Actions",
            render: (_: string, claimTemplate: ClaimTemplate) => {
                return (
                    <Button
                        type="primary"
                        onClick={() => {
                            setIsDrawerVisible(true)
                            setCurrentClaimTemplate(claimTemplate)
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
            <CreateClaimTemplate isClaimTemplateSuggestion={currentNode !== NodeEnum.SUPERVISORY_AUTHORITY} />

            {currentNode === NodeEnum.SUPERVISORY_AUTHORITY && <Table columns={claimTemplateSuggestions} dataSource={claimTemplates} />}
            <Table columns={claimTemplateColumns} dataSource={claimTemplates} />
            {isDrawerVisible && <UpdateClaimTemplate claimTemplate={claimTemplate as ClaimTemplate} isVisible={isDrawerVisible} setIsVisible={setIsDrawerVisible} />}
        </div>
    )
}

export default ClaimTemplates
