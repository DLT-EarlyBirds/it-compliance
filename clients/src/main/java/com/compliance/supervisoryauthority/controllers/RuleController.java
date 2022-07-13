package com.compliance.supervisoryauthority.controllers;

import com.compliance.flows.CreateRule;
import com.compliance.flows.DeprecateRegulation;
import com.compliance.flows.DeprecateRule;
import com.compliance.flows.UpdateRule;
import com.compliance.states.Rule;
import com.compliance.supervisoryauthority.NodeRPCConnection;
import com.compliance.supervisoryauthority.models.RuleDTO;
import net.corda.core.contracts.UniqueIdentifier;
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
    private final static Logger logger = LoggerFactory.getLogger(RuleController.class);

    public RuleController(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
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
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(
                null,
                Collections.singletonList(UniqueIdentifier.Companion.fromString(linearId)),
                Vault.StateStatus.UNCONSUMED,
                Collections.singleton(Rule.class)
        );
        return proxy
                .vaultQueryByCriteria(queryCriteria, Rule.class)
                .getStates()
                .stream()
                .map(
                        ruleStateAndRef -> ruleStateAndRef.getState().getData()
                )
                .collect(Collectors.toList());
    }

    @PutMapping(value = "/")
    private void update(@RequestBody RuleDTO ruleDTO) throws ExecutionException, InterruptedException {
        UniqueIdentifier id = UniqueIdentifier.Companion.fromString(ruleDTO.getLinearId());
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(
                null,
                Collections.singletonList(id),
                Vault.StateStatus.UNCONSUMED,
                Collections.singleton(Rule.class)
        );
        // Check if state with that linear ID exists
        // Todo: Should throw custom exception if no regulation with the ID exists
        if (!proxy.vaultQueryByCriteria(queryCriteria, Rule.class).getStates().isEmpty()) {
            // Call the update flow
            proxy.startTrackedFlowDynamic(
                    UpdateRule.UpdateRuleInitiator.class,
                    id,
                    ruleDTO.getName(),
                    ruleDTO.getRuleSpecification(),
                    UniqueIdentifier.Companion.fromString(ruleDTO.getParentRegulation())
            ).getReturnValue().get();
        }
    }


    @PostMapping("/")
    private Rule create(@RequestBody RuleDTO ruleDTO) {
        proxy.startTrackedFlowDynamic(
                CreateRule.CreateRuleInitiator.class,
                ruleDTO.getName(),
                ruleDTO.getRuleSpecification(),
                UniqueIdentifier.Companion.fromString(ruleDTO.getParentRegulation())
        );

        List<Rule> rules = proxy
                .vaultQuery(Rule.class)
                .getStates()
                .stream()
                .map(
                        ruleStateAndRef -> ruleStateAndRef.getState().getData())
                .collect(Collectors.toList());

        rules.forEach(rule -> logger.info(rule.getName()));

        return rules
                .stream()
                .filter(
                        rule -> rule.getName().equals(ruleDTO.getName()) && rule.getRuleSpecification().equals(ruleDTO.getRuleSpecification())
                )
                .collect(Collectors.toList())
                .get(0);
    }

    @PutMapping("/deprecate/{linearId}")
    private void deprecatedRule(@PathVariable String linearId) {
        proxy.startTrackedFlowDynamic(
                DeprecateRule.DeprecateRuleInitiator.class,
                UniqueIdentifier.Companion.fromString(linearId)
        );
    }
}