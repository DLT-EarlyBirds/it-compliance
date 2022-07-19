import { NodeEnum } from "enums";
import React, { useState } from "react";
import { getAxiosInstance } from "../services/axiosInstance";

interface NodeContextInterface {
  currentNode: NodeEnum;
  axiosInstance: any;
  setCurrentNode: (node: NodeEnum) => void;
}

const NodeContext = React.createContext<NodeContextInterface>({
  currentNode: NodeEnum.SUPERVISORY_AUTHORITY,
  axiosInstance: null,
  setCurrentNode: () => {},
});

function NodeProvider(props: any) {
  const [currentNode, setCurrentNode] = useState(
    NodeEnum.SUPERVISORY_AUTHORITY
  );

  const axiosInstance = getAxiosInstance(currentNode);

  return (
    <NodeContext.Provider
      {...props}
      value={{ currentNode, setCurrentNode, axiosInstance }}
    />
  );
}

function useNode() {
  const context = React.useContext(NodeContext);
  if (context === undefined) {
    throw new Error("useNode must be used within a Node Provider");
  }
  return context;
}

export { NodeProvider, useNode };
