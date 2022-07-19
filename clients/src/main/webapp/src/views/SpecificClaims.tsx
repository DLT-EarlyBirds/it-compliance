import React, { useState } from "react"
import { Table, Button, message, Upload } from "antd"
import { useData } from "../contexts/DataContext"
import { SpecificClaim } from "models"
import { EditOutlined } from "@ant-design/icons"
import UpdateSpecificClaim from "components/UpdateSpecificClaim"
import { UploadOutlined } from "@ant-design/icons"
import type { UploadProps } from "antd"
import { useNode } from "contexts/NodeContext"
import { NodeEnum } from "enums"
import CreateSpecificClaim from "components/CreateSpecificClaim"
import { insertIf } from "utils"

function SpecificClaims() {
    const { specificClaims } = useData()
    const { currentNode, axiosInstance } = useNode()
    const isSupervisoryAuthority = currentNode === NodeEnum.SUPERVISORY_AUTHORITY

    const [isDrawerVisible, setIsDrawerVisible] = useState(false)
    const [specificClaim, setCurrentSpecificClaim] = useState<SpecificClaim | undefined>(undefined)

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
            title: "Description",
            dataIndex: "claimSpecification",
        },
        {
            title: "Financial Service Provider",
            dateIndex: "financialServiceProvider",
        },
        {
            title: "Claim Template",
            dataIndex: ["claimTemplate", "pointer", "id"],
        },
        ...insertIf(!isSupervisoryAuthority, {
            title: "Actions",
            render: (_: string, specificClaim: SpecificClaim) => {
                const uploadProps: UploadProps = {
                    name: "file",
                    action: `${axiosInstance.defaults.baseURL}/claims/${specificClaim.linearId.id}`,
                    method: "PUT",
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
