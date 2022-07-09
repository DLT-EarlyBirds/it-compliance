package com.compliance.supervisoryauthority.controllers;

import com.compliance.flows.CreateClaimTemplate;
import com.compliance.flows.CreateRegulation;
import com.compliance.flows.CreateRule;
import com.compliance.states.ClaimTemplate;
import com.compliance.states.Regulation;
import com.compliance.states.Rule;
import com.compliance.supervisoryauthority.NodeRPCConnection;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.transactions.SignedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/regulation") // The paths for HTTP requests are relative to this base path.
public class RegulationController {
    private final CordaRPCOps proxy;
    private final CordaX500Name me;
    private final static Logger logger = LoggerFactory.getLogger(RuleController.class);

    public RegulationController(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
        this.me = proxy.nodeInfo().getLegalIdentities().get(0).getName();
    }

    @GetMapping(value = "/regulations", produces = APPLICATION_JSON_VALUE)
    private List<StateAndRef<Regulation>> regulations() {
        return proxy.vaultQuery(Regulation.class).getStates();
    }

    @GetMapping(value = "/rules", produces = APPLICATION_JSON_VALUE)
    private List<StateAndRef<Rule>> rules() {
        return proxy.vaultQuery(Rule.class).getStates();
    }

    @GetMapping(value = "/claimtemplates", produces = APPLICATION_JSON_VALUE)
    private List<StateAndRef<ClaimTemplate>> claimTemplates() {
        return proxy.vaultQuery(ClaimTemplate.class).getStates();
    }

    @GetMapping(value = "/bootstrapGraph", produces = APPLICATION_JSON_VALUE)
    private void bootstrapGraph() {
        // Issue the first regulation
        logger.info("Bootstrapping regulation graph");

        try {
            logger.info("Creating regulation");
            SignedTransaction regulation = proxy.startTrackedFlowDynamic(
                    CreateRegulation.CreateRegulationInitiator.class,
                    "MaRisk AT 7",
                    "Provides a flexible and practical framework for structuring institutions' risk management [on the basis of Kreditwesengestz §25a and §25b]",
                    "0.1",
                    new Date()
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
                    "MaRisk AT 7.2 p. 3",
                    "The IT systems shall be tested before their first use and after any material changes and approved by both the responsible organisational unit staff and IT staff. To this end, a standard process of development, testing, approval and implementation in the production processes shall be established. The production and testing environments shall be segregated.",
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
                    "Developer Access",
                    "Proof that Developers only accessed development/testing systems.",
                    ruleLinearId
            ).getReturnValue().get();

            logger.info("Creating admin claim template");
            SignedTransaction adminsClaimTemplate = proxy.startTrackedFlowDynamic(
                    CreateClaimTemplate.CreateClaimTemplateInitiator.class,
                    "MaRisk AT 7.2 p. 3",
                    "Proof that IT admins only accessed production systems.",
                    ruleLinearId
            ).getReturnValue().get();

        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed bootstrapping regulation graph", e);
            e.printStackTrace();
        }


    }
}