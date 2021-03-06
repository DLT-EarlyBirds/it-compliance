import { NodeEnum } from "enums"
import React, { useState } from "react"
import { AxiosInstance } from "axios"
import { getAxiosInstance } from "../services/axiosInstance"

interface NodeContextInterface {
    currentNode: NodeEnum
    axiosInstance: AxiosInstance
    setCurrentNode: (node: NodeEnum) => void
}

const NodeContext = React.createContext<NodeContextInterface>({
    currentNode: NodeEnum.SUPERVISORY_AUTHORITY,
    axiosInstance: getAxiosInstance(NodeEnum.SUPERVISORY_AUTHORITY),
    setCurrentNode: () => {},
})

function NodeProvider(props: any) {
    const [currentNode, setCurrentNode] = useState(NodeEnum.SUPERVISORY_AUTHORITY)

    const axiosInstance = getAxiosInstance(currentNode)

    return <NodeContext.Provider {...props} value={{ currentNode, setCurrentNode, axiosInstance }} />
}

function useNode() {
    const context = React.useContext(NodeContext)
    if (context === undefined) {
        throw new Error("useNode must be used within a Node Provider")
    }
    return context
}

export { NodeProvider, useNode }
