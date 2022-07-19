import React, { useState } from "react"
import { Modal, Button, Form, Input, Select } from "antd"
import { useNode } from "../contexts/NodeContext"
import { useData } from "../contexts/DataContext"

const { Option } = Select

const CreateSpecificClaim = () => {
    const [isModalVisible, setIsModalVisible] = useState(false)
    const [specificClaimForm] = Form.useForm()
    const { axiosInstance } = useNode()
    const { setSpecificClaims } = useData()

    const showModal = () => {
        setIsModalVisible(true)
    }

    const handleOk = () => {
        setIsModalVisible(false)
    }

    const handleCancel = () => {
        setIsModalVisible(false)
    }

    const onFinish = (values: any) => {}

    return (
        <>
            <Button type="primary" className="my-3" onClick={showModal}>
                Create Specific Claim
            </Button>
            <Modal
                title="Create Specific Claim"
                visible={isModalVisible}
                onOk={handleOk}
                onCancel={handleCancel}
                footer={[
                    <Button className="btn-default" key="back" onClick={handleCancel}>
                        Cancel
                    </Button>,
                    <Button key="submit" type="primary" onClick={() => specificClaimForm.submit()}>
                        Create Specific Claim
                    </Button>,
                ]}
            >
                <Form
                    form={specificClaimForm}
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
                        label="Description"
                        name="description"
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
                            <Option value="m1">M1</Option>
                            <Option value="m2">M2</Option>
                        </Select>
                    </Form.Item>
                </Form>
            </Modal>
        </>
    )
}

export default CreateSpecificClaim
