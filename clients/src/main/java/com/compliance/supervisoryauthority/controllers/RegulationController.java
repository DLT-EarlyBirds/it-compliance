package com.compliance.supervisoryauthority.controllers;

import com.compliance.flows.CreateRegulation;
import com.compliance.flows.DeprecateRegulation;
import com.compliance.flows.UpdateRegulation;
import com.compliance.states.Regulation;
import com.compliance.supervisoryauthority.NodeRPCConnection;
import com.compliance.supervisoryauthority.models.RegulationDTO;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import org.checkerframework.checker.signedness.qual.Signed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
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
        else return ResponseEntity.status(HttpStatus.FOUND).body(regulations.get(0));
    }

    @PutMapping(value = "/")
    private ResponseEntity<Regulation> updateRegulation(@RequestBody RegulationDTO regulationDTO) throws ExecutionException, InterruptedException {
        UniqueIdentifier id = UniqueIdentifier.Companion.fromString(regulationDTO.getLinearId());
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(
                null,
                Collections.singletonList(id),
                Vault.StateStatus.UNCONSUMED,
                Collections.singleton(Regulation.class)
        );
        // Check if state with that linear ID exists
        if (!proxy.vaultQueryByCriteria(queryCriteria, Regulation.class).getStates().isEmpty()) {
            // Call the update flow
            Regulation regulation = (Regulation) proxy.startTrackedFlowDynamic(
                    UpdateRegulation.UpdateRegulationInitiator.class,
                    id,
                    regulationDTO.getName(),
                    regulationDTO.getDescription(),
                    regulationDTO.getVersion(),
                    regulationDTO.getReleaseDate()
            ).getReturnValue().get().getTx().getOutput(0);
            return ResponseEntity.status(HttpStatus.OK).body(regulation);
        } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PostMapping("/")
    private ResponseEntity<Regulation> createRegulation(@RequestBody RegulationDTO regulationDTO) throws ExecutionException, InterruptedException {
        SignedTransaction tx = proxy.startTrackedFlowDynamic(
                CreateRegulation.CreateRegulationInitiator.class,
                regulationDTO.getName(),
                regulationDTO.getDescription(),
                regulationDTO.getVersion(),
                regulationDTO.getReleaseDate()
        ).getReturnValue().get();
        return ResponseEntity.status(HttpStatus.CREATED).body((Regulation) tx.getTx().getOutput(0));
    }

    @PutMapping("/deprecate/{linearId}")
    private ResponseEntity<Regulation> deprecatedRegulation(@PathVariable String linearId) throws ExecutionException, InterruptedException {
        SignedTransaction tx = proxy.startTrackedFlowDynamic(
                DeprecateRegulation.DeprecateRegulationInitiator.class,
                UniqueIdentifier.Companion.fromString(linearId)
        ).getReturnValue().get();
        return ResponseEntity.status(HttpStatus.OK).body((Regulation) tx.getTx().getOutput(0));
    }


}