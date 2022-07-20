import {useData} from "contexts/DataContext"
import React, {useState} from "react"
import {NavigateFunction, useNavigate, useParams} from "react-router-dom"
import {ClaimTemplate, Regulation, Rule, SpecificClaim as SpecificClaimModel} from "models"
import {useNode} from "../contexts/NodeContext";
import {NodeEnum} from "../enums";
import createEngine, {DefaultLinkModel, DefaultNodeModel, DiagramModel} from "@projectstorm/react-diagrams";
import {resolveX500Name} from "../services/resolveX500Name";
import {Button, Card} from "antd";
import RuleService from "../services/Rule.service";
import {EditOutlined} from "@ant-design/icons";
import Meta from "antd/es/card/Meta";
import UpdateRule from "../components/UpdateRule";
import {CanvasWidget, Action, InputType} from "@projectstorm/react-canvas-core"

/**
 * Navigates to a selected node
 */
class CustomNavigateAction extends Action {
    constructor(navigate: NavigateFunction) {
        super({
            type: InputType.MOUSE_UP,
            fire: () => {
                const selectedEntities = this.engine.getModel().getSelectedEntities()
                if (selectedEntities.length > 0) {
                    selectedEntities.forEach((model) => {
                        // only delete items which are not locked
                        if (!model.isLocked()) {
                            navigate(model.getID())
                        }
                    })
                    this.engine.repaintCanvas()
                }
            },
        })
    }
}

const SpecificClaim = () => {
    const {id} = useParams()
    const {specificClaims, claimTemplates, rules} = useData()
    const {currentNode, axiosInstance} = useNode()
    const [isDrawerVisible, setIsDrawerVisible] = useState(false)
    const navigate = useNavigate()
    const isSupervisoryAuthority = currentNode === NodeEnum.SUPERVISORY_AUTHORITY


    const specificClaim = specificClaims.find((r: SpecificClaimModel) => r.linearId.id === id)

    if (!specificClaim) {
        return <h1>No Specific Claim Found</h1>
    }
    const parentClaim = claimTemplates.find((claimtemplate: ClaimTemplate) => claimtemplate.linearId.id === specificClaim.claimTemplate.pointer.id)
    const grandparentRule = rules.find((rule: Rule) => rule.linearId.id === parentClaim?.rule.pointer.id)

    const engine = createEngine()
    const model = new DiagramModel()

    const ruleNode = new DefaultNodeModel({
        name: grandparentRule?.name,
        color: "rgb(0,192,255)",
        id: "/rules/" + grandparentRule?.linearId.id,
    })

    ruleNode.setPosition(100, 100)
    let rulePort = ruleNode.addOutPort("Released by: " + resolveX500Name(grandparentRule?.issuer ?? ''))
    const claimTemplateNode = new DefaultNodeModel({
        name: parentClaim?.name,
        color: "rgb(0,192,255)",
        id: "/claim-templates/" + parentClaim?.linearId.id
    })
    claimTemplateNode.setPosition(400, 100)
    let inPort = claimTemplateNode.addInPort('');
    const claimTemplatePort = claimTemplateNode.addOutPort(resolveX500Name(parentClaim?.issuer ?? ''))
    const link1 = rulePort.link(inPort);

    const specificClaimNode = new DefaultNodeModel({
        name: specificClaim.name,
        color: "rgb(255,165,0)",
        id: "/specific-claims/" + specificClaim.linearId.id,
    })
    specificClaimNode.setPosition(700, 100)
    specificClaimNode.addInPort(resolveX500Name(specificClaim.financialServiceProvider))

    const link2 = claimTemplatePort.link<DefaultLinkModel>(specificClaimNode.getInPorts()[0])

    model.addAll(ruleNode, claimTemplateNode, specificClaimNode, link1, link2)
    engine.setModel(model)
    engine.getActionEventBus().registerAction(new CustomNavigateAction(navigate))


    return (
        <>
            <Card cover={<CanvasWidget className={"h-[30vh]"} engine={engine}/>} bordered={true}>
                <Meta title={specificClaim.name}
                      description={"Released by: " + resolveX500Name(specificClaim.financialServiceProvider)}/>
                <p className="mt-5">{specificClaim.description}</p>
                <p className="mt-5">{'Supervisor: ' + resolveX500Name(specificClaim.supervisoryAuthority) + ", Auditor: " + resolveX500Name(specificClaim.auditor)}</p>
            </Card>
        </>
    )
}

export default SpecificClaim
