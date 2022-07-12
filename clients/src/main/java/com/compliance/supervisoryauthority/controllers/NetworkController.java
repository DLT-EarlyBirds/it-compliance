package com.compliance.supervisoryauthority.controllers;

import com.compliance.flows.CreateClaimTemplate;
import com.compliance.flows.CreateRegulation;
import com.compliance.flows.CreateRule;
import com.compliance.states.Regulation;
import com.compliance.states.Rule;
import com.compliance.supervisoryauthority.NodeRPCConnection;
import com.compliance.supervisoryauthority.models.ClaimTemplateDTO;
import com.compliance.supervisoryauthority.models.RegulationDTO;
import com.compliance.supervisoryauthority.models.RuleDTO;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import net.corda.core.transactions.SignedTransaction;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/network") // The paths for HTTP requests are relative to this base path.
public class NetworkController {
    private final CordaRPCOps proxy;
    private final CordaX500Name me;
    private final static Logger logger = LoggerFactory.getLogger(RuleController.class);

    public NetworkController(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
        this.me = proxy.nodeInfo().getLegalIdentities().get(0).getName();
    }

    /**
     * Helpers for filtering the network map cache.
     */
    public String toDisplayString(X500Name name) {
        return BCStyle.INSTANCE.toString(name);
    }

    private boolean isNotary(NodeInfo nodeInfo) {
        return !(proxy.notaryIdentities()
                .stream().noneMatch(nodeInfo::isLegalIdentity));
    }

    private boolean isMe(NodeInfo nodeInfo) {
        return nodeInfo.getLegalIdentities().get(0).getName().equals(me);
    }

    private boolean isNetworkMap(NodeInfo nodeInfo) {
        return nodeInfo.getLegalIdentities().get(0).getName().getOrganisation().equals("Network Map Service");
    }

    @GetMapping(value = "/servertime", produces = TEXT_PLAIN_VALUE)
    private String serverTime() {
        return (LocalDateTime.ofInstant(proxy.currentNodeTime(), ZoneId.of("UTC"))).toString();
    }

    @GetMapping(value = "/addresses", produces = TEXT_PLAIN_VALUE)
    private String addresses() {
        return proxy.nodeInfo().getAddresses().toString();
    }

    @GetMapping(value = "/identities", produces = TEXT_PLAIN_VALUE)
    private String identities() {
        return proxy.nodeInfo().getLegalIdentities().toString();
    }

    @GetMapping(value = "/platformversion", produces = TEXT_PLAIN_VALUE)
    private String platformVersion() {
        return Integer.toString(proxy.nodeInfo().getPlatformVersion());
    }

    @GetMapping(value = "/peers", produces = APPLICATION_JSON_VALUE)
    public HashMap<String, List<String>> getPeers() {
        HashMap<String, List<String>> myMap = new HashMap<>();

        // Find all nodes that are not notaries, ourself, or the network map.
        Stream<NodeInfo> filteredNodes = proxy.networkMapSnapshot().stream()
                .filter(el -> !isNotary(el) && !isMe(el) && !isNetworkMap(el));
        // Get their names as strings
        List<String> nodeNames = filteredNodes.map(el -> el.getLegalIdentities().get(0).getName().toString())
                .collect(Collectors.toList());

        myMap.put("peers", nodeNames);
        return myMap;
    }

    @GetMapping(value = "/notaries", produces = TEXT_PLAIN_VALUE)
    private String notaries() {
        return proxy.notaryIdentities().toString();
    }

    @GetMapping(value = "/flows", produces = TEXT_PLAIN_VALUE)
    private String flows() {
        return proxy.registeredFlows().toString();
    }

    @GetMapping(value = "/me", produces = APPLICATION_JSON_VALUE)
    private HashMap<String, String> whoami() {
        HashMap<String, String> myMap = new HashMap<>();
        myMap.put("me", me.toString());
        return myMap;
    }

    @GetMapping(value = "/bootstrapGraph", produces = APPLICATION_JSON_VALUE)
    private void bootstrapGraph() {
        RegulationDTO mockRegulation1 = new RegulationDTO(
                "",
                "MaRisk AT 7",
                "Provides a flexible and practical framework for structuring institutions' risk management [on the basis of Kreditwesengestz §25a and §25b]",
                "0.1",
                new Date()
        );
        RuleDTO mockRule1 = new RuleDTO(
                "",
                "MaRisk AT 7.2 p. 3",
                "The IT systems shall be tested before their first use and after any material changes and approved by both the responsible organisational unit staff and IT staff. To this end, a standard process of development, testing, approval and implementation in the production processes shall be established. The production and testing environments shall be segregated.",
                ""
        );
        ClaimTemplateDTO mockClaimTemplate1a = new ClaimTemplateDTO(
                "",
                "Admin Access",
                "Proof that IT admins only accessed production systems.",
                ""
        );
        ClaimTemplateDTO mockClaimTemplate1b = new ClaimTemplateDTO(
                "",
                "Developer Access",
                "Proof that Developers only accessed development/testing systems.",
                ""
        );




        // Issue the first regulation
        logger.info("Bootstrapping regulation graph");

        try {
            logger.info("Creating regulation");
            SignedTransaction regulation = proxy.startTrackedFlowDynamic(
                    CreateRegulation.CreateRegulationInitiator.class,
                    mockRegulation1.getName(),
                    mockRegulation1.getDescription(),
                    mockRegulation1.getVersion(),
                    mockRegulation1.getReleaseDate()
            ).getReturnValue().get();

            // Get regulation linear ID
            logger.info("Fetching regulation linear ID from vault");
            UniqueIdentifier regulationLinearId = proxy
                    .vaultQuery(Regulation.class)
                    .getStates()
                    .get(0)
                    .getState()
                    .getData()
                    .getLinearId();

            // Create sample rules
            logger.info("Creating rule");
            SignedTransaction rule = proxy.startTrackedFlowDynamic(
                    CreateRule.CreateRuleInitiator.class,
                    mockRule1.getName(),
                    mockRule1.getRuleSpecification(),
                    regulationLinearId
            ).getReturnValue().get();

            // Create claim templates
            logger.info("Fetching rule linear ID from vault");
            UniqueIdentifier ruleLinearId = proxy
                    .vaultQuery(Rule.class)
                    .getStates()
                    .get(0)
                    .getState()
                    .getData()
                    .getLinearId();

            logger.info("Creating developer claim template");
            SignedTransaction developersClaimTemplate = proxy.startTrackedFlowDynamic(
                    CreateClaimTemplate.CreateClaimTemplateInitiator.class,
                    mockClaimTemplate1a.getName(),
                    mockClaimTemplate1a.getTemplateDescription(),
                    ruleLinearId
            ).getReturnValue().get();

            logger.info("Creating admin claim template");
            SignedTransaction adminsClaimTemplate = proxy.startTrackedFlowDynamic(
                    CreateClaimTemplate.CreateClaimTemplateInitiator.class,
                    mockClaimTemplate1b.getName(),
                    mockClaimTemplate1b.getTemplateDescription(),
                    ruleLinearId
            ).getReturnValue().get();

        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed bootstrapping regulation graph", e);
            e.printStackTrace();
        }
    }

}