package com.compliance.auditor.controllers;

import com.compliance.states.Regulation;
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
 * A REST controller that exposes the Corda node's vault as a REST API to query information about regulations
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/regulations")
public class RegulationController {
    private final CordaRPCOps proxy;

    public RegulationController(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }

    /**
     * REST endpoint that returns a list of all the regulations in the ledger
     *
     * @return A list of all the regulations in the vault.
     */
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

    /**
     * Endpoint to gets a regulations by its linear ID (with status UNCONSUMED = the newest)
     *
     * @param linearId The linear ID of the regulation you want to retrieve.
     * @return The regulation with the given linear ID and status UNCONSUMED.
     */
    @GetMapping(value = "/{linearId}", produces = APPLICATION_JSON_VALUE)
    private ResponseEntity<Regulation> getByLinearId(@PathVariable String linearId) {
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(
                null,
                Collections.singletonList(UniqueIdentifier.Companion.fromString(linearId)),
                Vault.StateStatus.UNCONSUMED,
                Collections.singleton(Regulation.class)
        );

        // Get all regulations with this linear ID and status UNCONSUMED
        List<Regulation> regulations = proxy.vaultQueryByCriteria(queryCriteria, Regulation.class)
                .getStates()
                .stream()
                .map(
                        regulationStateAndRef -> regulationStateAndRef.getState().getData()
                )
                .collect(Collectors.toList());
        if (regulations.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        else return ResponseEntity.status(HttpStatus.OK).body(regulations.get(0));
    }
}