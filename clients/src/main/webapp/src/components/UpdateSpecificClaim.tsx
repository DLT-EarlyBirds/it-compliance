import React from "react"
import { Button, Form, Input, Drawer, Select } from "antd"
import { SpecificClaimDTO, ClaimTemplate, SpecificClaim } from "models"
import { useNode } from "../contexts/NodeContext"
import { useData } from "contexts/DataContext"
import SpecificClaimService from "services/SpecificClaim.service"

const { Option } = Select

interface UpdateSpecificClaimProps {
    specificClaim: SpecificClaim
    isVisible: boolean
    setIsVisible: (isVisible: boolean) => void
}

const UpdateSpecificClaim = ({ specificClaim, isVisible, setIsVisible }: UpdateSpecificClaimProps) => {
    const { axiosInstance } = useNode()
    const { specificClaims, setSpecificClaims, claimTemplates } = useData()

    const onFinish = (values: SpecificClaimDTO) => {
        SpecificClaimService.update(axiosInstance, { ...values, linearId: specificClaim.linearId.id }).then((response) => {
            const updatedSpecificClaims = specificClaims.map((r) => (r.linearId.id === response.linearId.id ? response : r))
            setSpecificClaims(updatedSpecificClaims)
            setIsVisible(false)
        })
    }

    return (
        <Drawer title="Update Specific Claim" placement="right" closable={false} onClose={() => setIsVisible(false)} visible={isVisible} height="200">
            <Form
                name="basic"
                labelCol={{
                    span: 8,
                }}
                onFinish={onFinish}
            >
                <Form.Item
                    label="Name"
                    name="name"
                    initialValue={specificClaim.name}
                    rules={[
                        {
                            required: true,
                        },
                    ]}
                >
                    <Input />
                </Form.Item>
                <Form.Item
                    label="Description"
                    name="description"
                    initialValue={specificClaim.description}
                    rules={[
                        {
                            required: true,
                        },
                    ]}
                >
                    <Input />
                </Form.Item>
                <Form.Item
                    label="Claim Template"
                    name="claimTemplate"
                    initialValue={specificClaim.claimTemplate}
                    rules={[
                        {
                            required: true,
                        },
                    ]}
                >
                    <Select
                        style={{
                            width: 120,
                        }}
                    >
                        {claimTemplates.map((claimTemplate: ClaimTemplate) => (
                            <Option value={claimTemplate.linearId.id}>{claimTemplate.name}</Option>
                        ))}
                    </Select>
                </Form.Item>
                <Form.Item>
                    <Button type="primary" htmlType="submit">
                        Submit
                    </Button>
                </Form.Item>
            </Form>
        </Drawer>
    )
}

export default UpdateSpecificClaim
