import React from "react";
import { Table } from "antd";
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
    title: "Issuer",
    dateIndex: "issuer",
  },
];

function ClaimTemplates() {
  const { claimTemplates } = useData();

  return (
    <div>
      <Table columns={columns} dataSource={claimTemplates} />
    </div>
  );
}

export default ClaimTemplates;
