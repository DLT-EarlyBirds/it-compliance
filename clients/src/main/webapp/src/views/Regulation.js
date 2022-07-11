import React from "react";
import { Table, Button } from "antd";
import regulationData from "../data/regulations.json";
import CreateRegulation from "../components/CreateRegulation";

const columns = [
  {
    title: "Name",
    dataIndex: "name",
  },
  {
    title: "Name Abbreviated",
    dataIndex: "nameAbbreviated",
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
    dataIndex: "id",
    render: (id) => {
      return (
        <Button type="primary" onClick={() => console.log(id)}>
          Deprecate
        </Button>
      );
    },
  },
];

function Regulation() {
  return (
    <div>
      <CreateRegulation />
      <Table columns={columns} dataSource={regulationData} />
    </div>
  );
}

export default Regulation;
