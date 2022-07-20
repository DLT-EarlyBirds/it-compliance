import React from "react"
import { Button, Form, Input, Drawer, Select } from "antd"
import { Regulation, Rule, RuleDTO } from "models"
import { useNode } from "../contexts/NodeContext"
import { useData } from "contexts/DataContext"
import RuleService from "services/Rule.service"
import TextArea from "antd/es/input/TextArea"

const { Option } = Select

interface UpdateRuleProps {
    rule: Rule
    isVisible: boolean
    setIsVisible: (isVisible: boolean) => void
}

const UpdateRule = ({ rule, isVisible, setIsVisible }: UpdateRuleProps) => {
    const { axiosInstance } = useNode()
    const { rules, regulations, setRules } = useData()

    const onFinish = (values: RuleDTO) => {
        RuleService.update(axiosInstance, { ...values, linearId: rule.linearId.id }).then((response) => {
            const updatedRules = rules.map((r) => (r.linearId.id === response.linearId.id ? response : r))
            setRules(updatedRules)
            setIsVisible(false)
        })
    }

    return (
        <Drawer size={"large"} title="Update Rule" placement="right" closable={false} onClose={() => setIsVisible(false)} visible={isVisible} height="200">
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
                    initialValue={rule.name}
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
                    initialValue={rule.parentRegulation}
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

                <Form.Item
                    label="Rule Specification"
                    name="ruleSpecification"
                    initialValue={rule.ruleSpecification}
                    rules={[
                        {
                            required: true,
                        },
                    ]}
                >
                    <TextArea rows={10} />
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

export default UpdateRule
