import React from "react";
import { Table } from "antd";
import regulationData from "../data/regulations.json";

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
  return <Table columns={columns} dataSource={regulationData} />;
}

export default Regulation;
