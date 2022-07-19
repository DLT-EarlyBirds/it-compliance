import React, { useState } from "react"
import { Modal, Button, Form, Input, Select } from "antd"
import { useData } from "contexts/DataContext"
import { Regulation } from "models"
import RuleService from "services/Rule.service"
import { useNode } from "contexts/NodeContext"
const { Option } = Select

const CreateRule = () => {
    const [isModalVisible, setIsModalVisible] = useState(false)
    const { regulations, rules, setRules } = useData()
    const [ruleForm] = Form.useForm()
    const { axiosInstance } = useNode()

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
        RuleService.create(axiosInstance, values).then((response) => {
            setRules([...rules, response])
            handleCancel()
        })
    }

    return (
        <>
            <Button type="primary" className="my-3" onClick={showModal}>
                Create Rule
            </Button>
            <Modal
                title="Create Regulation"
                visible={isModalVisible}
                onOk={handleOk}
                onCancel={handleCancel}
                footer={[
                    <Button className="btn-default" key="back" onClick={handleCancel}>
                        Cancel
                    </Button>,
                    <Button key="submit" type="primary" onClick={() => ruleForm.submit()}>
                        Create Rule
                    </Button>,
                ]}
            >
                <Form
                    form={ruleForm}
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
                        label="Rule Specification"
                        name="ruleSpecification"
                        rules={[
                            {
                                required: true,
                            },
                        ]}
                    >
                        <Input />
                    </Form.Item>
                    <Form.Item
                        label="Parent Regulation"
                        name="parentRegulation"
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
                            {regulations.map((regulation: Regulation) => (
                                <Option value={regulation.linearId.id}>{regulation.name}</Option>
                            ))}
                        </Select>
                    </Form.Item>
                </Form>
            </Modal>
        </>
    )
}

export default CreateRule
