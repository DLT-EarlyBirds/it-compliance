import React, { useState } from "react"
import { Table, Button, message, Upload } from "antd"
import { useData } from "../contexts/DataContext"
import { SpecificClaim } from "models"
import { EditOutlined, DownloadOutlined, EyeOutlined } from "@ant-design/icons"
import UpdateSpecificClaim from "components/UpdateSpecificClaim"
import { UploadOutlined } from "@ant-design/icons"
import type { UploadProps } from "antd"
import { useNode } from "contexts/NodeContext"
import { NodeEnum } from "enums"
import CreateSpecificClaim from "components/CreateSpecificClaim"
import { insertIf } from "utils"
import SpecificClaimService from "services/SpecificClaim.service"
import { Link } from "react-router-dom"
import { resolveX500Name } from "../services/resolveX500Name"

function SpecificClaims() {
    const { specificClaims } = useData()
    const { currentNode, axiosInstance } = useNode()
    const isSupervisoryAuthority = currentNode === NodeEnum.SUPERVISORY_AUTHORITY

    const [isDrawerVisible, setIsDrawerVisible] = useState(false)
    const [specificClaim, setCurrentSpecificClaim] = useState<SpecificClaim | undefined>(undefined)

    const columns = [
        {
            title: "Name",
            dataIndex: "name",
        },
        {
            title: "Description",
            dataIndex: "description",
        },
        {
            title: "Financial Service Provider",
            dataIndex: "financialServiceProvider",
            filters: [
                {
                    text: NodeEnum.BRAIN_FINANCE,
                    value: NodeEnum.BRAIN_FINANCE,
                },
                {
                    text: NodeEnum.CAPITALS_HOLDING,
                    value: NodeEnum.CAPITALS_HOLDING,
                },
            ],
            onFilter: (value: string, specificClaim: SpecificClaim) => resolveX500Name(specificClaim.financialServiceProvider) === value,
            render: (_: string, specificClaim1: SpecificClaim) => {
                return resolveX500Name(specificClaim1.financialServiceProvider)
            },
        },
        {
            title: "Attachment",
            render: (_: string, specificClaim: SpecificClaim) => {
                return specificClaim?.attachmentID ? (
                    <DownloadOutlined onClick={() => SpecificClaimService.downloadAttachment(axiosInstance, specificClaim.linearId.id)} />
                ) : (
                    <span>No attachment</span>
                )
            },
        },
        ...insertIf(!isSupervisoryAuthority, {
            title: "Actions",
            render: (_: string, specificClaim: SpecificClaim) => {
                const uploadProps: UploadProps = {
                    name: "file",
                    action: `${axiosInstance.defaults.baseURL}/claims/attachment/${specificClaim.linearId.id}`,
                    method: "POST",
                    headers: {
                        authorization: "authorization-text",
                    },
                    onChange(info) {
                        if (info.file.status !== "uploading") {
                            console.log(info.file, info.fileList)
                        }
                        if (info.file.status === "done") {
                            message.success(`${info.file.name} file uploaded successfully`)
                        } else if (info.file.status === "error") {
                            message.error(`${info.file.name} file upload failed.`)
                        }
                    },
                }

                return (
                    <>
                        <Upload {...uploadProps}>
                            <Button icon={<UploadOutlined />}>Upload</Button>
                        </Upload>
                        <Button
                            type="primary"
                            onClick={() => {
                                setIsDrawerVisible(true)
                                setCurrentSpecificClaim(specificClaim)
                            }}
                        >
                            <EditOutlined />
                        </Button>
                    </>
                )
            },
        }),
        {
            title: "View",
            render: (_: string, specificClaim: SpecificClaim) => {
                return (
                    <Link to={`/specific-claims/${specificClaim.linearId.id}`}>
                        <EyeOutlined />
                    </Link>
                )
            },
        },
    ]

    return (
        <div>
            {!isSupervisoryAuthority && <CreateSpecificClaim />}
            <Table columns={columns} dataSource={specificClaims} />
            {isDrawerVisible && <UpdateSpecificClaim specificClaim={specificClaim as SpecificClaim} isVisible={isDrawerVisible} setIsVisible={setIsDrawerVisible} />}
        </div>
    )
}

export default SpecificClaims
