import React, { useState } from "react"
import { Table, Button } from "antd"
import CreateRegulation from "../components/CreateRegulation"
import UpdateRegulation from "../components/UpdateRegulation"
import { useData } from "../contexts/DataContext"
import { useNode } from "../contexts/NodeContext"
import { Regulation } from "models"
import { NodeEnum } from "enums"
import { EditOutlined } from "@ant-design/icons"
import { insertIf } from "utils"

function Regulations() {
    const { regulations } = useData()
    const { currentNode } = useNode()
    const [isDrawerVisible, setIsDrawerVisible] = useState(false)
    const [regulation, setCurrentRegulation] = useState<Regulation | undefined>(undefined)
    const isSupervisoryAuthority = currentNode === NodeEnum.SUPERVISORY_AUTHORITY

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
            dataIndex: "description",
        },
        {
            title: "Release date",
            dataIndex: "releaseDate",
        },
        {
            title: "Version",
            dataIndex: "version",
        },
        {
            title: "Issuer",
            dataIndex: "issuer",
        },
        ...insertIf(isSupervisoryAuthority, {
            title: "Deprecate",
            dataIndex: "isDeprecated",
            render: ({ isDeprecated, linearId }: Regulation) => {
                return (
                    <Button type="primary" disabled={isDeprecated} onClick={() => console.log(linearId.id)}>
                        {isDeprecated ? "Deprecate" : "Deprecated"}
                    </Button>
                )
            },
        }),
        ...insertIf(isSupervisoryAuthority, {
            title: "Actions",
            render: (_: string, regulation: Regulation) => {
                return (
                    <Button
                        type="primary"
                        onClick={() => {
                            setIsDrawerVisible(true)
                            setCurrentRegulation(regulation)
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
            {isSupervisoryAuthority && <CreateRegulation />}
            <Table columns={columns} dataSource={regulations} />
            {isDrawerVisible && <UpdateRegulation regulation={regulation as Regulation} isVisible={isDrawerVisible} setIsVisible={setIsDrawerVisible} />}
        </div>
    )
}

export default Regulations
