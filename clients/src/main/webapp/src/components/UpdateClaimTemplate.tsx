import React from "react"
import { Button, Form, Input, Drawer, Select } from "antd"
import { ClaimTemplateDTO, Rule, ClaimTemplate } from "models"
import { useNode } from "../contexts/NodeContext"
import { useData } from "contexts/DataContext"
import ClaimTemplateService from "services/ClaimTemplate.service"

const { Option } = Select

interface UpdateClaimTemplateProps {
    claimTemplate: ClaimTemplate
    isVisible: boolean
    setIsVisible: (isVisible: boolean) => void
}

const UpdateClaimTemplate = ({ claimTemplate, isVisible, setIsVisible }: UpdateClaimTemplateProps) => {
    const { axiosInstance } = useNode()
    const { claimTemplates, setClaimTemplates, rules } = useData()

    const onFinish = (values: ClaimTemplateDTO) => {
        ClaimTemplateService.update(axiosInstance, { ...values, linearId: claimTemplate.linearId.id }).then((response) => {
            const updatedClaimTemplates = claimTemplates.map((r) => (r.linearId.id === response.linearId.id ? response : r))
            setClaimTemplates(updatedClaimTemplates)
            setIsVisible(false)
        })
    }

    return (
        <Drawer title="Update Claim Template" placement="right" closable={false} onClose={() => setIsVisible(false)} visible={isVisible} height="200">
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
                    initialValue={claimTemplate.name}
                    rules={[
                        {
                            required: true,
                        },
                    ]}
                >
                    <Input />
                </Form.Item>
                <Form.Item
                    label="Template Description"
                    name="templateDescription"
                    initialValue={claimTemplate.templateDescription}
                    rules={[
                        {
                            required: true,
                        },
                    ]}
                >
                    <Input />
                </Form.Item>
                <Form.Item
                    label="Rule"
                    name="rule"
                    initialValue={claimTemplate.rule}
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
                        {rules.map((rule: Rule) => (
                            <Option value={rule.linearId.id}>{rule.name}</Option>
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

export default UpdateClaimTemplate