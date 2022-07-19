import React from "react"
import { Table, Button } from "antd"
import { useData } from "../contexts/DataContext"
import { useNode } from "../contexts/NodeContext"
import { NodeEnum } from "enums"
import { Regulation } from "models"
import CreateClaimTemplate from "../components/CreateClaimTemplate"

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
]

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

    return (
        <div>
            <CreateClaimTemplate isClaimTemplateSuggestion={currentNode !== NodeEnum.SUPERVISORY_AUTHORITY} />

            {currentNode === NodeEnum.SUPERVISORY_AUTHORITY && <Table columns={claimTemplateSuggestions} dataSource={claimTemplates} />}
            <Table columns={claimTemplateColumns} dataSource={claimTemplates} />
        </div>
    )
}

export default ClaimTemplates
