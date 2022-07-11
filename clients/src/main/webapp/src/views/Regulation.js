import React from "react";
import { Table } from "antd";
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
