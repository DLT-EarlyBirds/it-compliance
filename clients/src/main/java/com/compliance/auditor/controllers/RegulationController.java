package com.compliance.auditor.controllers;

import com.compliance.states.Regulation;
import com.compliance.auditor.NodeRPCConnection;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/regulations") // The paths for HTTP requests are relative to this base path.
public class RegulationController {
    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(RuleController.class);

    public RegulationController(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }

    @GetMapping(value = "/", produces = APPLICATION_JSON_VALUE)
    private List<Regulation> getAll() {
        return proxy
                .vaultQuery(Regulation.class)
                .getStates()
                .stream()
                .map(
                        regulationStateAndRef -> regulationStateAndRef.getState().getData()
                )
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/{linearId}", produces = APPLICATION_JSON_VALUE)
    private List<Regulation> getByLinearId(@PathVariable String linearId) {
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(UniqueIdentifier.Companion.fromString(linearId)), Vault.StateStatus.ALL, Collections.singleton(Regulation.class));
        return proxy
                .vaultQueryByCriteria(queryCriteria, Regulation.class)
                .getStates()
                .stream()
                .map(
                        regulationStateAndRef -> regulationStateAndRef.getState().getData()
                )
                .collect(Collectors.toList());
    }
}