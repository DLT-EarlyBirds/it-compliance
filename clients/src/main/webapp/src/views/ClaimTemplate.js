import React from "react";
import { Table, Button } from "antd";
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
    title: "Template Description",
    dataIndex: "templateDescription",
  },
  {
    title: "Rule Specification",
    dateIndex: "ruleSpecification",
  },
  {
    title: "Rule",
    dataIndex: "rule",
  },
  {
    title: "Action",
    dataIndex: ["linearId", "id"],
    render: ({ linearId }) => {
      return (
        <>
          <Button onClick={() => console.log(linearId.id)}>Reject</Button>
          <Button type="primary" onClick={() => console.log(linearId.id)}>
            Approve
          </Button>
        </>
      );
    },
  },
];

function ClaimTemplate() {
  const { claimTemplates } = useData();

  return (
    <div>
      <Table columns={columns} dataSource={claimTemplates} />
    </div>
  );
}

export default ClaimTemplate;
