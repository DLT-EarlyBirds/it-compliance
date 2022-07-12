import React from "react";
import { Table, Button } from "antd";
import ruleData from "../data/rules.json";
import CreateRule from "../components/CreateRule";

const columns = [
  {
    title: "Name",
    dataIndex: "name",
  },
  {
    title: "Rule Specification",
    dataIndex: "ruleSpecification1",
  },
  {
    title: "Deprecate",
    dataIndex: "name",
    render: (id) => {
      return (
        <Button type="primary" onClick={() => console.log(id)}>
          Deprecate
        </Button>
      );
    },
  },
];

function Rule() {
  return (
    <div>
      <CreateRule />
      <Table columns={columns} dataSource={ruleData} />
    </div>
  );
}

export default Rule;
