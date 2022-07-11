package com.compliance.supervisoryauthority.controllers;

import com.compliance.flows.CreateRegulation;
import com.compliance.states.Rule;
import com.compliance.supervisoryauthority.NodeRPCConnection;
import com.compliance.supervisoryauthority.models.RuleDTO;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/rules") // The paths for HTTP requests are relative to this base path.
public class RuleController {
    private final CordaRPCOps proxy;
    private final CordaX500Name me;
    private final static Logger logger = LoggerFactory.getLogger(RuleController.class);

    public RuleController(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
        this.me = proxy.nodeInfo().getLegalIdentities().get(0).getName();
    }


    @GetMapping(value = "/", produces = APPLICATION_JSON_VALUE)
    private List<Rule> getAll() {
        return proxy
                .vaultQuery(Rule.class)
                .getStates()
                .stream()
                .map(
                        ruleStateAndRef -> ruleStateAndRef.getState().getData()
                )
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/{linearId}", produces = APPLICATION_JSON_VALUE)
    private List<Rule> getByLinearId(@PathVariable String linearId) {
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(UniqueIdentifier.Companion.fromString(linearId)), Vault.StateStatus.ALL, Collections.singleton(Rule.class));
        return proxy
                .vaultQueryByCriteria(queryCriteria, Rule.class)
                .getStates()
                .stream()
                .map(
                        ruleStateAndRef -> ruleStateAndRef.getState().getData()
                )
                .collect(Collectors.toList());
    }


    @PostMapping("/")
    private Rule createRule(@RequestBody RuleDTO ruleDTO) throws ExecutionException, InterruptedException {
        SignedTransaction tx = proxy.startTrackedFlowDynamic(
                CreateRegulation.CreateRegulationInitiator.class,
                ruleDTO.getName(),
                ruleDTO.getRuleSpecification(),
                UniqueIdentifier.Companion.fromString(ruleDTO.getParentRegulation()),
                new Date()
        ).getReturnValue().get();

        List<Rule> rules = proxy
                .vaultQuery(Rule.class)
                .getStates()
                .stream()
                .map(
                        ruleStateAndRef -> ruleStateAndRef.getState().getData())
                .collect(Collectors.toList());

        // Return regulation linear ID
        return rules
                .stream()
                .filter(
                        reg -> reg.getName().equals(ruleDTO.getName()) && reg.getRuleSpecification().equals(ruleDTO.getRuleSpecification())
                )
                .collect(Collectors.toList())
                .get(0);
    }
}