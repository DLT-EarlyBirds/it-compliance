package com.compliance.financialserviceprovider.controllers;

import com.compliance.flows.CreateSpecificClaim;
import com.compliance.flows.UpdateSpecificClaim;
import com.compliance.states.SpecificClaim;
import com.compliance.financialserviceprovider.NodeRPCConnection;
import com.compliance.financialserviceprovider.models.SpecificClaimDTO;
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
@RequestMapping("/claims") // The paths for HTTP requests are relative to this base path.
public class SpecificClaimController {
    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(SpecificClaimController.class);

    public SpecificClaimController(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }

    @GetMapping(value = "/", produces = APPLICATION_JSON_VALUE)
    private List<SpecificClaim> getAll() {
        return proxy
                .vaultQuery(SpecificClaim.class)
                .getStates()
                .stream()
                .map(
                        specificClaimStateAndRef -> specificClaimStateAndRef.getState().getData()
                )
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/{linearId}", produces = APPLICATION_JSON_VALUE)
    private List<SpecificClaim> getByLinearId(@PathVariable String linearId) {
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(
                null,
                Collections.singletonList(UniqueIdentifier.Companion.fromString(linearId)),
                Vault.StateStatus.ALL,
                Collections.singleton(SpecificClaim.class)
        );
        return proxy
                .vaultQueryByCriteria(queryCriteria, SpecificClaim.class)
                .getStates()
                .stream()
                .map(
                        claimTemplateStateAndRef -> claimTemplateStateAndRef.getState().getData()
                )
                .collect(Collectors.toList());
    }

    @PutMapping(value = "/")
    private void update(@RequestBody SpecificClaimDTO specificClaimDTO) throws ExecutionException, InterruptedException {
        UniqueIdentifier id = UniqueIdentifier.Companion.fromString(specificClaimDTO.getLinearId());
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(
                null,
                Collections.singletonList(id),
                Vault.StateStatus.ALL,
                Collections.singleton(SpecificClaim.class));
        // Check if state with that linear ID exists
        // Todo: Should throw custom exception if no regulation with the ID exists
        if (!proxy.vaultQueryByCriteria(queryCriteria, SpecificClaim.class).getStates().isEmpty()) {
            // Call the update flow
            proxy.startTrackedFlowDynamic(
                    UpdateSpecificClaim.UpdateSpecificClaimInitiator.class,
                    id,
                    specificClaimDTO.getName(),
                    specificClaimDTO.getClaimSpecification(),
                    UniqueIdentifier.Companion.fromString(specificClaimDTO.getClaimTemplateLinearId())
            ).getReturnValue().get();
        }
    }


    @PostMapping("/")
    private SpecificClaim create(@RequestBody SpecificClaimDTO specificClaimDTO) throws ExecutionException, InterruptedException {
        proxy.startTrackedFlowDynamic(
                CreateSpecificClaim.CreateSpecificClaimInitiator.class,
                specificClaimDTO.getName(),
                specificClaimDTO.getClaimSpecification(),
                UniqueIdentifier.Companion.fromString(specificClaimDTO.getClaimTemplateLinearId()),
                new Date()
        ).getReturnValue().get();

        List<SpecificClaim> specificClaims = proxy
                .vaultQuery(SpecificClaim.class)
                .getStates()
                .stream()
                .map(
                        specificClaimStateAndRef -> specificClaimStateAndRef.getState().getData())
                .collect(Collectors.toList());

        // Return regulation linear ID
        return specificClaims
                .stream()
                .filter(
                        specificClaim -> specificClaim.getName().equals(specificClaimDTO.getName())
                )
                .collect(Collectors.toList())
                .get(0);
    }
}