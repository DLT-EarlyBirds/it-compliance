import React, { useState } from "react"
import { Table, Button } from "antd"
import { useData } from "../contexts/DataContext"
import { SpecificClaim } from "models"
import { EditOutlined } from "@ant-design/icons"
import UpdateSpecificClaim from "components/UpdateSpecificClaim"

function SpecificClaims() {
    const { specificClaims } = useData()

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
        {
            title: "Actions",
            render: (_: string, specificClaim: SpecificClaim) => {
                return (
                    <Button
                        type="primary"
                        onClick={() => {
                            setIsDrawerVisible(true)
                            setCurrentSpecificClaim(specificClaim)
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
            <Table columns={columns} dataSource={specificClaims} />
            {isDrawerVisible && <UpdateSpecificClaim specificClaim={specificClaim as SpecificClaim} isVisible={isDrawerVisible} setIsVisible={setIsDrawerVisible} />}
        </div>
    )
}

export default SpecificClaims
