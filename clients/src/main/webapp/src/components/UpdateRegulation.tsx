import React from "react";
import { Button, Form, Input, Drawer } from "antd";
import { Regulation } from "types";

interface UpdateRegulationProps {
  regulation: Regulation;
  isVisible: boolean;
  setIsVisible: (isVisible: boolean) => void;
}

const UpdateRegulation = ({
  regulation,
  isVisible,
  setIsVisible,
}: UpdateRegulationProps) => {
  const onFinish = (values: any) => {
    console.log("Success:", values);
  };

  return (
    <Drawer
      title="Update Regulation"
      placement="right"
      closable={false}
      onClose={() => setIsVisible(false)}
      visible={isVisible}
      height="200"
    >
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
          <Input />
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
          <Button type="primary" htmlType="submit">
            Submit
          </Button>
        </Form.Item>
      </Form>
    </Drawer>
  );
};

export default UpdateRegulation;
