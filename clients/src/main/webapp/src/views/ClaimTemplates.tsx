import React, { useState } from "react"
import { Table, Button } from "antd"
import { useData } from "../contexts/DataContext"
import { useNode } from "../contexts/NodeContext"
import { NodeEnum } from "enums"
import { ClaimTemplate, Regulation } from "models"
import CreateClaimTemplate from "../components/CreateClaimTemplate"
import { EditOutlined, EyeOutlined } from "@ant-design/icons"
import UpdateClaimTemplate from "components/UpdateClaimTemplate"
import { insertIf } from "utils"
import ClaimTemplateService from "services/ClaimTemplate.service"
import { Link } from "react-router-dom"
import {resolveX500Name} from "../services/resolveX500Name";

const commonColumns = [
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
        dataIndex: "issuer",
        render: (_:string, claimTemplate: ClaimTemplate) => {
            return resolveX500Name(claimTemplate.issuer);
        }
    },
]

function ClaimTemplates() {
    const { claimTemplates, claimTemplatesSuggestions, setClaimTemplatesSuggestions, setClaimTemplates } = useData()
    const { currentNode, axiosInstance } = useNode()
    const [isDrawerVisible, setIsDrawerVisible] = useState(false)
    const [claimTemplate, setCurrentClaimTemplate] = useState<ClaimTemplate | undefined>(undefined)
    const isSupervisoryAuthority = currentNode === NodeEnum.SUPERVISORY_AUTHORITY

    const claimTemplateSuggestionsColumns = [
        ...commonColumns,
        ...insertIf(isSupervisoryAuthority, {
            title: "Actions",
            dataIndex: ["linearId", "id"],
            render: (_: string, { linearId }: Regulation) => {
                return (
                    <>
                        <Button
                            onClick={() => {
                                ClaimTemplateService.rejectSuggestion(axiosInstance, linearId.id).then(() =>
                                    ClaimTemplateService.getSuggestions(axiosInstance).then(setClaimTemplatesSuggestions)
                                )
                            }}
                        >
                            Reject
                        </Button>
                        <Button
                            type="primary"
                            onClick={() => {
                                ClaimTemplateService.acceptSuggestion(axiosInstance, linearId.id).then(() => {
                                    ClaimTemplateService.getSuggestions(axiosInstance).then(setClaimTemplatesSuggestions)
                                    ClaimTemplateService.getAll(axiosInstance).then(setClaimTemplates)
                                })
                            }}
                        >
                            Accept
                        </Button>
                    </>
                )
            },
        }),
    ]

    const claimTemplateColumns = [
        ...commonColumns,
        ...insertIf(isSupervisoryAuthority, {
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
        }),
        {
            title: "View",
            render: (_: string, claimTemplate: ClaimTemplate) => {
                return (
                    <Link to={`/claim-templates/${claimTemplate.linearId.id}`}>
                        <EyeOutlined />
                    </Link>
                )
            },
        },
    ]

    return (
        <div>
            <CreateClaimTemplate isClaimTemplateSuggestion={!isSupervisoryAuthority} />
            <h2>Claim Template Suggestions</h2>
            <Table columns={claimTemplateSuggestionsColumns} dataSource={claimTemplatesSuggestions} />
            <h2>Claim Templates </h2>
            <Table columns={claimTemplateColumns} dataSource={claimTemplates} />
            {isDrawerVisible && <UpdateClaimTemplate claimTemplate={claimTemplate as ClaimTemplate} isVisible={isDrawerVisible} setIsVisible={setIsDrawerVisible} />}
        </div>
    )
}

export default ClaimTemplates
