import React, { useState } from "react"
import { Modal, Button, Form, Input, Select } from "antd"
import { useNode } from "../contexts/NodeContext"
import { useData } from "../contexts/DataContext"
import ClaimTemplateService from "services/ClaimTemplate.service"

const { Option } = Select

interface CreateClaimTemplateProps {
    isClaimTemplateSuggestion: boolean
}

const CreateClaimTemplate = ({ isClaimTemplateSuggestion }: CreateClaimTemplateProps) => {
    const [isModalVisible, setIsModalVisible] = useState(false)
    const [claimTemplateForm] = Form.useForm()
    const { axiosInstance } = useNode()
    const { rules, claimTemplates, setClaimTemplates, claimTemplatesSuggestions, setClaimTemplatesSuggestions } = useData()

    const showModal = () => {
        setIsModalVisible(true)
    }

    const handleOk = () => {
        setIsModalVisible(false)
    }

    const handleCancel = () => {
        setIsModalVisible(false)
    }

    const onFinish = (values: any) => {
        if (isClaimTemplateSuggestion) {
            ClaimTemplateService.createSuggestion(axiosInstance, {...values, linearId: ''}).then((response) => {
                setClaimTemplatesSuggestions([...claimTemplatesSuggestions, response])
                handleCancel()
            })
        } else {
            ClaimTemplateService.create(axiosInstance, values).then((response) => {
                setClaimTemplates([...claimTemplates, response])
                handleCancel()
            })
        }
    }

    return (
        <>
            <Button type="primary" className="my-3" onClick={showModal}>
                {isClaimTemplateSuggestion ? "Create Claim Template" : "Create Claim Template Suggestion"}
            </Button>
            <Modal
                title={isClaimTemplateSuggestion ? "Create Claim Template" : "Create Claim Template Suggestion"}
                visible={isModalVisible}
                onOk={handleOk}
                onCancel={handleCancel}
                footer={[
                    <Button className="btn-default" key="back" onClick={handleCancel}>
                        Cancel
                    </Button>,
                    <Button key="submit" type="primary" onClick={() => claimTemplateForm.submit()}>
                        Create Claim Template
                    </Button>,
                ]}
            >
                <Form
                    form={claimTemplateForm}
                    name="basic"
                    labelCol={{
                        span: 8,
                    }}
                    onFinish={onFinish}
                >
                    <Form.Item
                        label="Name"
                        name="name"
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
                            {rules.map((rule) => (
                                <Option value={rule.linearId.id}>{rule.name}</Option>
                            ))}
                        </Select>
                    </Form.Item>
                </Form>
            </Modal>
        </>
    )
}

export default CreateClaimTemplate
