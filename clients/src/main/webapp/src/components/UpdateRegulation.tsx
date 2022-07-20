import React from "react"
import { Button, Form, Input, Drawer } from "antd"
import { Regulation, RegulationDTO } from "models"
import { useNode } from "../contexts/NodeContext"
import RegulationService from "../services/Regulation.service"
import { useData } from "contexts/DataContext"
import TextArea from "antd/es/input/TextArea";

interface UpdateRegulationProps {
    regulation: Regulation
    isVisible: boolean
    setIsVisible: (isVisible: boolean) => void
}

const UpdateRegulation = ({ regulation, isVisible, setIsVisible }: UpdateRegulationProps) => {
    const { axiosInstance } = useNode()
    const { regulations, setRegulations } = useData()

    const onFinish = (values: RegulationDTO) => {
        RegulationService.update(axiosInstance, { ...values, linearId: regulation.linearId.id }).then((response) => {
            const updatedRegulations = regulations.map((r) => (r.linearId.id === response.linearId.id ? response : r))
            setRegulations(updatedRegulations)
            setIsVisible(false)
        })
    }

    return (
        <Drawer size={"large"} title="Update Regulation" placement="right" closable={false} onClose={() => setIsVisible(false)} visible={isVisible} height="200">
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
                    initialValue={regulation.name}
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
                    initialValue={regulation.description}
                    rules={[
                        {
                            required: true,
                        },
                    ]}
                >
                    <TextArea rows={10} />
                </Form.Item>
                <Form.Item
                    label="Version"
                    name="version"
                    initialValue={regulation.version}
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
                    initialValue={regulation.releaseDate}
                    rules={[
                        {
                            required: true,
                        },
                    ]}
                >
                    <Input />
                </Form.Item>
                <Form.Item>
                    <Button block type="primary" htmlType="submit">
                        Submit
                    </Button>
                </Form.Item>
            </Form>
        </Drawer>
    )
}

export default UpdateRegulation
