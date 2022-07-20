import { useData } from "contexts/DataContext"
import React, { FC, useState } from "react"
import { NavigateFunction, useNavigate, useParams } from "react-router-dom"
import { Regulation as RegulationModel, Rule } from "models"
import { Button, Card } from "antd"
import Meta from "antd/es/card/Meta"
import { EditOutlined } from "@ant-design/icons"
import RegulationService from "../services/Regulation.service"
import RuleService from "../services/Rule.service"
import { useNode } from "../contexts/NodeContext"
import UpdateRegulation from "../components/UpdateRegulation"
import createEngine, { DefaultLinkModel, DefaultNodeModel, DiagramModel } from "@projectstorm/react-diagrams"

import { CanvasWidget, Action, InputType } from "@projectstorm/react-canvas-core"
import { resolveX500Name } from "../services/resolveX500Name"
import { NodeEnum } from "../enums"

/**
 * Navigates to a selected node
 */
class CustomDeleteItemsAction extends Action {
    constructor(navigate: NavigateFunction) {
        super({
            type: InputType.MOUSE_UP,
            fire: () => {
                const selectedEntities = this.engine.getModel().getSelectedEntities()
                if (selectedEntities.length > 0) {
                    selectedEntities.forEach((model) => {
                        // only delete items which are not locked
                        if (!model.isLocked()) {
                            console.log(model.getID())
                            navigate(model.getID())
                        }
                    })
                    this.engine.repaintCanvas()
                }
            },
        })
    }
}

const Regulation: FC = () => {
    const { id } = useParams()
    const { regulations, rules, setRegulations, setRules } = useData()
    const { currentNode, axiosInstance } = useNode()
    const [isDrawerVisible, setIsDrawerVisible] = useState(false)
    const navigate = useNavigate()
    const isSupervisoryAuthority = currentNode === NodeEnum.SUPERVISORY_AUTHORITY

    const regulation = regulations.find((r: RegulationModel) => r.linearId.id === id)

    if (!regulation) {
        return <h1>No Regulation Found</h1>
    }

    const relatedRules = rules.filter((rule: Rule) => rule.parentRegulation.pointer.id === regulation.linearId.id)
    console.log(regulation)
    console.log(relatedRules)

    // create an instance of the engine with all the defaults
    const engine = createEngine()

    const regulationNode = new DefaultNodeModel({
        name: regulation.name,
        color: "rgb(255,165,0)",
        id: "/regulations/" + regulation.linearId.id,
    })
    let baseOffset = 50 * (relatedRules.length/2)
    if (baseOffset === 0 || relatedRules.length === 1) baseOffset = 50
    regulationNode.setPosition(250, baseOffset)
    let regulationPort = regulationNode.addOutPort("Released: " + new Date(Date.parse(regulation.releaseDate)).toDateString())

    const offset = 50 * (1 + relatedRules.length / 100)
    let offsetAcc = 50

    const ruleNodes = relatedRules.map((rule: Rule) => {
        const node = new DefaultNodeModel({
            name: rule.name,
            color: "rgb(0,192,255)",
            id: "/rules/" + rule.linearId.id,
        })
        node.setPosition(500, offsetAcc)
        offsetAcc += offset
        node.addInPort(resolveX500Name(rule.issuer))
        return node
    })

    const links = ruleNodes.map((node: DefaultNodeModel) => {
        return regulationPort.link<DefaultLinkModel>(node.getInPorts()[0])
    })

    const model = new DiagramModel()
    model.addAll(regulationNode, ...ruleNodes, ...links)
    engine.setModel(model)

    engine.getActionEventBus().registerAction(new CustomDeleteItemsAction(navigate))

    return (
        <>
            {isSupervisoryAuthority && (
                <div className={"flex justify-between py-3"}>
                    <Button
                        type="primary"
                        disabled={regulation.isDeprecated}
                        className={"mr-2"}
                        onClick={() => {
                            RegulationService.deprecate(axiosInstance, regulation.linearId.id)
                                .then((response) => {
                                    const updatedRegulations = regulations.map((r) => (r.linearId.id === response.linearId.id ? response : r))
                                    setRegulations(updatedRegulations)
                                })
                                .then(() => RuleService.getAll(axiosInstance).then((rulesResponse) => setRules(rulesResponse)))
                        }}
                        block
                        danger
                    >
                        {regulation.isDeprecated ? "Deprecated" : "Deprecate"}
                    </Button>
                    <Button
                        type="primary"
                        className={"ml-2"}
                        onClick={() => {
                            setIsDrawerVisible(true)
                        }}
                        block
                    >
                        <EditOutlined /> Edit
                    </Button>
                </div>
            )}
            <Card cover={<CanvasWidget className={"h-[50vh]"} engine={engine} />} bordered={true}>
                <Meta title={regulation.name} description={"Released: " + new Date(Date.parse(regulation.releaseDate)).toDateString()} />
                <p className="mt-5">{regulation.description}</p>
            </Card>

            {isDrawerVisible && <UpdateRegulation regulation={regulation} isVisible={isDrawerVisible} setIsVisible={setIsDrawerVisible} />}
        </>
    )
}

export default Regulation
