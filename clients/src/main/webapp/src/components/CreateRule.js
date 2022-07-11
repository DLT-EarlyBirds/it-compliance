import React, { useState } from "react";
import { Modal, Button, Form, Input } from "antd";

const CreateRule = () => {
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [ruleForm] = Form.useForm();

  const showModal = () => {
    setIsModalVisible(true);
  };

  const handleOk = () => {
    setIsModalVisible(false);
  };

  const handleCancel = () => {
    setIsModalVisible(false);
  };

  const onFinish = (values) => {
    console.log("Success:", values);
  };

  const onFinishFailed = (errorInfo) => {
    console.log("Failed:", errorInfo);
  };

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
          onFinishFailed={onFinishFailed}
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
        </Form>
      </Modal>
    </>
  );
};

export default CreateRule;
