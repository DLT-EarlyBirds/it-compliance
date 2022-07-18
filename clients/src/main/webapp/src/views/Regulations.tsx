import React, { useState } from "react";
import { Table, Button } from "antd";
import CreateRegulation from "../components/CreateRegulation";
import UpdateRegulation from "../components/UpdateRegulation";
import { useData } from "../contexts/DataContext";
import { Regulation } from "models";
import { EditOutlined } from "@ant-design/icons";

function Regulations() {
  const { regulations } = useData();
  const [isDrawerVisible, setIsDrawerVisible] = useState(false);
  const [regulation, setCurrentRegulation] = useState<Regulation | undefined>(
    undefined
  );

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
      title: "Issuer",
      dateIndex: "issuer",
    },
    {
      title: "Deprecate",
      dataIndex: "isDeprecated",
      render: ({ isDeprecated, linearId }: Regulation) => {
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
    {
      title: "Actions",
      render: (_: string, regulation: Regulation) => {
        return (
          <Button
            type="primary"
            onClick={() => {
              setIsDrawerVisible(true);
              setCurrentRegulation(regulation);
            }}
          >
            <EditOutlined />
          </Button>
        );
      },
    },
  ];

  return (
    <div>
      <CreateRegulation />
      <Table columns={columns} dataSource={regulations} />
      {isDrawerVisible && (
        <UpdateRegulation
          regulation={regulation as Regulation}
          isVisible={isDrawerVisible}
          setIsVisible={setIsDrawerVisible}
        />
      )}
    </div>
  );
}

export default Regulations;
