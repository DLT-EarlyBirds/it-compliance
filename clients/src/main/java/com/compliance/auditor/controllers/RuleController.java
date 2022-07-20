package com.compliance.auditor.controllers;

import com.compliance.states.Rule;
import com.compliance.auditor.NodeRPCConnection;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


/**
 * Spring Boot controller that exposes a REST API for querying the vault for rules
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/rules") // The paths for HTTP requests are relative to this base path.
public class RuleController {
    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(RuleController.class);

    public RuleController(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }


    /**
     * Endpoint that returns a list of all the rules in the vault
     *
     * @return A list of all the rules in the vault.
     */
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

    /**
     * Endpoint that queries the vault for a rule with the given linearId and returns it as a JSON object
     *
     * @param linearId The unique identifier of the rule.
     * @return A Rule object
     */
    @GetMapping(value = "/{linearId}", produces = APPLICATION_JSON_VALUE)
    private ResponseEntity<Rule> getByLinearId(@PathVariable String linearId) {
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(
                null,
                Collections.singletonList(UniqueIdentifier.Companion.fromString(linearId)),
                Vault.StateStatus.UNCONSUMED,
                Collections.singleton(Rule.class)
        );

        List<Rule> rules = proxy
                .vaultQueryByCriteria(queryCriteria, Rule.class)
                .getStates()
                .stream()
                .map(
                        ruleStateAndRef -> ruleStateAndRef.getState().getData()
                )
                .collect(Collectors.toList());

        if (rules.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        else return ResponseEntity.status(HttpStatus.OK).body(rules.get(0));
    }

}