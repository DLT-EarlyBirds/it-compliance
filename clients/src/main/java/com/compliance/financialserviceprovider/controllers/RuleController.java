package com.compliance.financialserviceprovider.controllers;

import com.compliance.states.Rule;
import com.compliance.financialserviceprovider.NodeRPCConnection;
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
 * Define your API endpoints here.
 */
@RestController
@CrossOrigin(origins = "localhost:3000")
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