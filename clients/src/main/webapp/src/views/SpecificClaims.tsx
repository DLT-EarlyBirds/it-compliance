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
    title: "Description",
    dataIndex: "claimSpecification",
  },
  {
    title: "Financial Service Provider",
    dateIndex: "financialServiceProvider",
  },
  {
    title: "Claim Template",
    dataIndex: ["claimTemplate", "pointer", "id"],
  },
];

function SpecificClaims() {
  // TODO: Change to specific claims
  const { claimTemplates } = useData();

  return (
    <div>
      <Table columns={columns} dataSource={claimTemplates} />
    </div>
  );
}

export default SpecificClaims;
