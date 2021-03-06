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
 * A REST controller that exposes the Corda node's vault as a REST API to query information about regulations
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/regulations") // The paths for HTTP requests are relative to this base path.
public class RegulationController {
    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(RuleController.class);

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
     * Endpoint to get a regulations by its linear ID (with status UNCONSUMED = the newest)
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

    /**
     * Endpoint to update a regulation:
     * We check if the state with the given linear ID exists, and if it does, we call the update flow
     *
     * @param regulationDTO The DTO object that contains the data to be updated.
     * @return The updated regulation state.
     */
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

    /**
     * Endpoint that creates a new regulation and returns the newly created regulation
     *
     * @param regulationDTO The object that contains the data that will be used to create the regulation.
     * @return The regulation that was created.
     */
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

    /**
     *
     * Endpoint to deprecate a regulation:
     * The function takes a linearId as a parameter and returns a ResponseEntity with the updated Regulation
     *
     * @param linearId The linearId of the regulation to be deprecated.
     * @return The Regulation object that was depreciated.
     */
    @PutMapping("/deprecate/{linearId}")
    private ResponseEntity<Regulation> deprecatedRegulation(@PathVariable String linearId) throws ExecutionException, InterruptedException {
        SignedTransaction tx = proxy.startTrackedFlowDynamic(
                DeprecateRegulation.DeprecateRegulationInitiator.class,
                UniqueIdentifier.Companion.fromString(linearId)
        ).getReturnValue().get();
        return ResponseEntity.status(HttpStatus.OK).body((Regulation) tx.getTx().getOutput(0));
    }


}