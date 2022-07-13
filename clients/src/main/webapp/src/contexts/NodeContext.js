import React, { useState } from "react";
import { getAxiosInstance } from "../services/axiosInstance";

const NodeContext = React.createContext();

function NodeProvider(props) {
  const [currentNode, setCurrentNode] = useState("Supervisory Authority");
  console.log(currentNode);
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
