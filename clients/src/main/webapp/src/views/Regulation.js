import React from "react";
import { Table, Button } from "antd";
import CreateRegulation from "../components/CreateRegulation";
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
    dataIndex: "description",
  },
  {
    title: "Release date",
    dataIndex: "releaseDate",
  },
  {
    title: "Version",
    dataIndex: "version",
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

function Regulation() {
  const { regulations } = useData();

  return (
    <div>
      <CreateRegulation />
      <Table columns={columns} dataSource={regulations} />
    </div>
  );
}

export default Regulation;
