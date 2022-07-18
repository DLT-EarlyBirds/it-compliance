import React from "react";
import { Table, Button } from "antd";
import CreateRule from "../components/CreateRule";
import { useData } from "../contexts/DataContext";
import { Rule } from "models";

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
    dataIndex: ["parentRegulation", "pointer", "id"],
  },
  {
    issuer: "Issuer",
    dataIndex: "issuer",
  },
  {
    title: "Involved parties",
    dataIndex: "involvedParties",
  },
  {
    title: "Deprecate",
    dataIndex: "isDeprecated",
    render: ({ isDeprecated, linearId }: Rule) => {
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

function Rules() {
  const { rules } = useData();

  return (
    <div>
      <CreateRule />
      <Table columns={columns} dataSource={rules} />
    </div>
  );
}

export default Rules;
