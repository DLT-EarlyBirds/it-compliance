import {useData} from "contexts/DataContext"
import React, {useState} from "react"
import {NavigateFunction, useNavigate, useParams} from "react-router-dom"
import {ClaimTemplate as ClaimTemplateModel, Rule, SpecificClaim} from "models"
import {useNode} from "../contexts/NodeContext";
import {NodeEnum} from "../enums";
import createEngine, {DefaultLinkModel, DefaultNodeModel, DiagramModel} from "@projectstorm/react-diagrams";
import {resolveX500Name} from "../services/resolveX500Name";
import {CanvasWidget, Action, InputType} from "@projectstorm/react-canvas-core"
import {Button, Card} from "antd";
import RuleService from "../services/Rule.service";
import {EditOutlined} from "@ant-design/icons";
import Meta from "antd/es/card/Meta";
import UpdateRule from "../components/UpdateRule";
import UpdateClaimTemplate from "../components/UpdateClaimTemplate";

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

const ClaimTemplate = () => {
    const {id} = useParams()
    const {claimTemplates, specificClaims, setClaimTemplates, rules} = useData()
    const {currentNode, axiosInstance} = useNode()
    const [isDrawerVisible, setIsDrawerVisible] = useState(false)
    const navigate = useNavigate()
    const isSupervisoryAuthority = currentNode === NodeEnum.SUPERVISORY_AUTHORITY

    const claimTemplate = claimTemplates.find((r: ClaimTemplateModel) => r.linearId.id === id)

    if (!claimTemplate) {
        return <h1>No Claim Template Found</h1>
    }

    const parentRule = rules.find((rule: Rule) => rule.linearId.id === claimTemplate.rule.pointer.id)
    const relatedSpecificClaims = specificClaims.filter((specificClaim: SpecificClaim) => specificClaim.claimTemplate.pointer.id === claimTemplate.linearId.id)

    // create an instance of the engine with all the defaults
    const engine = createEngine()
    const model = new DiagramModel()

    const claimtemplateNode = new DefaultNodeModel({
        name: claimTemplate.name,
        color: "rgb(255,165,0)",
        id: "/claim-templates/" + claimTemplate.linearId.id,
    })

    let baseOffset = 50 * (relatedSpecificClaims.length / 2)
    if (baseOffset === 0) baseOffset = 50
    claimtemplateNode.setPosition(400, baseOffset)
    let claimTemplatePort = claimtemplateNode.addOutPort("Released by: " + resolveX500Name(claimTemplate.issuer))

    if (parentRule) {
        let inPort = claimtemplateNode.addInPort('');
        const ruleNode = new DefaultNodeModel({
            name: parentRule.name,
            color: "rgb(0,192,255)",
            id: "/rules/" + parentRule.linearId.id
        })
        ruleNode.setPosition(100, baseOffset)
        const rulePort = ruleNode.addOutPort(resolveX500Name(parentRule.issuer))
        const link = rulePort.link(inPort);
        model.addAll(ruleNode, link)
    }

    const offset = 50 * (1 + relatedSpecificClaims.length / 100)
    let offsetAcc = 50

    const specifcClaimNodes = relatedSpecificClaims.map((specificClaim: SpecificClaim) => {
        const node = new DefaultNodeModel({
            name: specificClaim.name,
            color: "rgb(0,192,255)",
            id: "/claim-templates/" + specificClaim.linearId.id,
        })
        node.setPosition(800, offsetAcc)
        offsetAcc += offset
        node.addInPort(resolveX500Name(specificClaim.financialServiceProvider))
        return node
    })

    const links = specifcClaimNodes.map((node: DefaultNodeModel) => {
        return claimTemplatePort.link<DefaultLinkModel>(node.getInPorts()[0])
    })

    model.addAll(claimtemplateNode, ...specifcClaimNodes, ...links)
    engine.setModel(model)
    engine.getActionEventBus().registerAction(new CustomNavigateAction(navigate))


    return (
        <>
            {isSupervisoryAuthority && (
                <div className={"flex justify-between py-3"}>
                    <Button
                        className={"ml-2"}
                        type="primary"
                        onClick={() => {
                            setIsDrawerVisible(true)
                        }}
                        block
                    >
                        <EditOutlined/> Edit
                    </Button>
                </div>
            )}
            <Card cover={<CanvasWidget className={"h-[50vh]"} engine={engine}/>} bordered={true}>
                <Meta title={claimTemplate.name} description={"Released by: " + resolveX500Name(claimTemplate.issuer)}/>
                <p className="mt-5">{claimTemplate.templateDescription}</p>

                <p className="mt-5">{claimTemplate.involvedParties.map((party) => resolveX500Name(party) + ", ")}</p>
            </Card>

            {isDrawerVisible && <UpdateClaimTemplate claimTemplate={claimTemplate} isVisible={isDrawerVisible}
                                                     setIsVisible={setIsDrawerVisible}/>}
        </>)
}

export default ClaimTemplate
