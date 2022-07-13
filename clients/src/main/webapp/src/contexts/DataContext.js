import React, { useEffect, useState } from "react";
import RegulationService from "../services/Regulation.service";
import { useNode } from "./NodeContext";

const DataContext = React.createContext();

function DataProvider(props) {
  const { node, axiosInstance } = useNode();
  const [regulations, setRegulations] = useState([]);

  const data = { regulations };

  useEffect(() => {
    RegulationService.getRegulations(axiosInstance).then((response) =>
      setRegulations(response)
    );
  }, [node]);

  return <DataContext.Provider {...props} value={data} />;
}

function useData() {
  const context = React.useContext(DataContext);
  if (context === undefined) {
    throw new Error("useData must be used within a Data Provider");
  }
  console.log(context, "hii");
  return context;
}

export { DataProvider, useData };
