import React from "react";
import { Table, Button } from "antd";
import CreateRule from "../components/CreateRule";
import { useData } from "../contexts/DataContext";

const columns = [
  {
    title: "Linear id",
    dataIndex: ["linearId", "id"],
  },
  {
    title: "Name",
    dataIndex: "name",
  },
  {
    title: "Rule Specification",
    dateIndex: "ruleSpecification",
  },
  {
    title: "Parent regulation",
    dataIndex: "parentRegulation",
  },
  {
    title: "Deprecate",
    dataIndex: "isDeprecated",
    render: ({ isDeprecated, linearId }) => {
      return (
        <Button
          type="primary"
          disabled={isDeprecated}
          onClick={() => console.log(linearId.id)}
        >
          {isDeprecated ? "Deprecate" : "Deprecated"}
        </Button>
      );
    },
  },
];

function Rule() {
  const { rules } = useData();

  return (
    <div>
      <CreateRule />
      <Table columns={columns} dataSource={rules} />
    </div>
  );
}

export default Rule;
