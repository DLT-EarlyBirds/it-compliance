import React, { useState } from "react"
import { Modal, Button, Form, Input } from "antd"
import RegulationService from "../services/Regulation.service"
import NetworkService from "../services/Network.service"
import { useNode } from "../contexts/NodeContext"
import { useData } from "../contexts/DataContext"

const CreateRegulation = () => {
    const [isModalVisible, setIsModalVisible] = useState(false)
    const [isBootstrapVisible, setIsBootstrapVisible] = useState(false)

    const [regulationForm] = Form.useForm()
    const { axiosInstance } = useNode()
    const { regulations, setRegulations, fetchData } = useData()

    const showCreateModal = () => {
        setIsModalVisible(true)
    }

    const handleCreateOk = () => {
        setIsModalVisible(false)
    }

    const handleCreateCancel = () => {
        setIsModalVisible(false)
    }

    const showBootstrapModal = () => {
        setIsBootstrapVisible(true)
    }

    const handleBootstrapOk = () => {
        bootstrapGraph()
        setIsBootstrapVisible(false)
    }

    const handleBootstrapCancel = () => {
        setIsBootstrapVisible(false)
    }

    const onFinish = (values: any) => {
        RegulationService.create(axiosInstance, values).then((response) => {
            setRegulations([...regulations, response])
            handleCreateCancel()
        })
    }

    const bootstrapGraph = () => {
        NetworkService.bootstrapGraph(axiosInstance).then(() => fetchData())
    }

    return (
        <>
            <Button type="primary" className="my-3" onClick={showCreateModal}>
                Create Regulation
            </Button>
            <Button type="primary" className="my-3" danger onClick={showBootstrapModal}>
                Bootstrap Graph
            </Button>
            <Modal
                title="Create Regulation"
                visible={isModalVisible}
                onOk={handleCreateOk}
                onCancel={handleCreateCancel}
                footer={[
                    <Button className="btn-default" key="back" onClick={handleCreateCancel}>
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
            <Modal
                title="Bootstrap Regulation Graph"
                visible={isBootstrapVisible}
                onOk={handleBootstrapOk}
                onCancel={handleBootstrapCancel}
                footer={[
                    <Button className="btn-default" key="back" onClick={handleBootstrapCancel}>
                        Cancel
                    </Button>,
                    <Button key="submit" type="primary" onClick={handleBootstrapOk}>
                        Bootstrap Regulation Graph
                    </Button>,
                ]}
            ></Modal>
        </>
    )
}

export default CreateRegulation
