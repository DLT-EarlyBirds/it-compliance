import { useData } from "contexts/DataContext"
import React, {useState} from "react"
import {NavigateFunction, useNavigate, useParams} from "react-router-dom"
import { Rule as RuleModel, ClaimTemplate } from "models"
import createEngine, {DefaultLinkModel, DefaultNodeModel, DiagramModel} from "@projectstorm/react-diagrams";
import {resolveX500Name} from "../services/resolveX500Name";
import {Button, Card} from "antd";
import RegulationService from "../services/Regulation.service";
import RuleService from "../services/Rule.service";
import {EditOutlined} from "@ant-design/icons";
import Meta from "antd/es/card/Meta";
import UpdateRegulation from "../components/UpdateRegulation";
import {
    CanvasWidget,
    Action,
    InputType
} from '@projectstorm/react-canvas-core';
import {useNode} from "../contexts/NodeContext";
import UpdateRule from "../components/UpdateRule";
import {NodeEnum} from "../enums";

/**
 * Navigates to a selected node
 */
class CustomDeleteItemsAction extends Action {

    constructor(navigate: NavigateFunction) {

        super({
            type: InputType.MOUSE_UP,
            fire: () => {
                const selectedEntities = this.engine.getModel().getSelectedEntities();
                if (selectedEntities.length > 0) {
                    selectedEntities.forEach((model) => {
                        // only delete items which are not locked
                        if (!model.isLocked()) {
                            console.log(model.getID())
                            navigate(model.getID())
                        }
                    });
                    this.engine.repaintCanvas();

                }
            }
        });
    }
}

const Rule = () => {
    const { id } = useParams()
    const { rules, claimTemplates, setClaimTemplates, setRules } = useData()
    const {currentNode, axiosInstance} = useNode()
    const [isDrawerVisible, setIsDrawerVisible] = useState(false)
    const navigate = useNavigate();
    const isSupervisoryAuthority = currentNode === NodeEnum.SUPERVISORY_AUTHORITY



    const rule = rules.find((r: RuleModel) => r.linearId.id === id)

    if (!rule) {
        return <h1>No Rule Found</h1>
    }

    const relatedClaimTemplates = claimTemplates.filter((claimTemplate: ClaimTemplate) => claimTemplate.rule.pointer.id === rule.linearId.id)
    console.log(relatedClaimTemplates)

    console.log(rule)

    // create an instance of the engine with all the defaults
    const engine = createEngine();

    const regulationNode = new DefaultNodeModel({
        name: rule.name,
        color: 'rgb(0,192,255)',
        id: '/rules/' + rule.linearId.id
    });
    regulationNode.setPosition(250, 150);
    let regulationPort = regulationNode.addOutPort('Released by: ' + resolveX500Name(rule.issuer));

    const offset = (50 * (1 + rules.length / 100))
    let offsetAcc = 50


    const ruleNodes = relatedClaimTemplates.map((claimTemplate: ClaimTemplate) => {
        const node = new DefaultNodeModel({
            name: claimTemplate.name,
            color: 'rgb(0,192,255)',
            id: '/claim-templates/' + claimTemplate.linearId.id
        });
        node.setPosition(500, offsetAcc);
        offsetAcc += offset
        node.addInPort(resolveX500Name(claimTemplate.issuer));
        return node
    })

    const links = ruleNodes.map((node: DefaultNodeModel) => {
        return regulationPort.link<DefaultLinkModel>(node.getInPorts()[0])
    })


    const model = new DiagramModel();
    model.addAll(regulationNode, ...ruleNodes, ...links);
    engine.setModel(model);

    engine.getActionEventBus().registerAction(new CustomDeleteItemsAction(navigate));

    return (
        <>
            {isSupervisoryAuthority && <div className={'flex justify-between py-3'}>
                <Button
                    type="primary"
                    className={'mr-2'}
                    disabled={rule.isDeprecated}
                    onClick={() => {
                        RuleService.deprecate(axiosInstance, rule.linearId.id).then((response) => {
                            const updatedRules = rules.map((r) => (r.linearId.id === response.linearId.id ? response : r))
                            setRules(updatedRules)
                        })
                    }}
                    block
                    danger
                >
                    {rule.isDeprecated ? "Deprecated" : "Deprecate"}
                </Button>
                <Button
                    className={'ml-2'}
                    type="primary"
                    onClick={() => {
                        setIsDrawerVisible(true)
                    }}
                    block
                >
                    <EditOutlined/> Edit
                </Button>
            </div>}
        <Card
            cover={
                <CanvasWidget className={'h-[50vh]'} engine={engine}/>
            }
            bordered={true}
        >
            <Meta
                title={rule.name}
                description={'Released by: ' + resolveX500Name(rule.issuer)}
            />
            <p className='mt-5'>
                {rule.ruleSpecification}
            </p>

            <p className='mt-5'>
                {rule.involvedParties.map(party => resolveX500Name(party)+', ')}
            </p>
        </Card>

        {isDrawerVisible && <UpdateRule rule={rule} isVisible={isDrawerVisible} setIsVisible={setIsDrawerVisible} />}

    </>)
}

export default Rule
