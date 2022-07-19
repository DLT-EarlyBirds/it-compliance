import React, { useState } from "react"
import { Modal, Button, Form, Input } from "antd"
import RegulationService from "../services/Regulation.service"
import NetworkService from "../services/Network.service"
import { useNode } from "../contexts/NodeContext"
import { useData } from "../contexts/DataContext"

const CreateRegulation = () => {
    const [isModalVisible, setIsModalVisible] = useState(false)
    const [regulationForm] = Form.useForm()
    const { axiosInstance } = useNode()
    const { regulations, setRegulations } = useData()

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
        RegulationService.create(axiosInstance, values).then((response) => setRegulations([...regulations, response]))
    }

    const bootstrapGraph = () => {
        NetworkService.bootstrapGraph(axiosInstance)
    }

    return (
        <>
            <Button type="primary" className="my-3" onClick={showModal}>
                Create Regulation
            </Button>
            <Button type="primary" className="my-3" onClick={bootstrapGraph}>
                Bootstrap Graph
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
                    <Button key="submit" type="primary" onClick={() => regulationForm.submit()}>
                        Create Regulation
                    </Button>,
                ]}
            >
                <Form
                    form={regulationForm}
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
                        label="Version"
                        name="version"
                        rules={[
                            {
                                required: true,
                            },
                        ]}
                    >
                        <Input />
                    </Form.Item>
                    <Form.Item
                        label="Release Date"
                        name="releaseDate"
                        rules={[
                            {
                                required: true,
                            },
                        ]}
                    >
                        <Input />
                    </Form.Item>
                </Form>
            </Modal>
        </>
    )
}

export default CreateRegulation
