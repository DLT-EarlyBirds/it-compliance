package com.compliance.supervisoryauthority.controllers;



import com.compliance.flows.CreateClaimTemplate;
import com.compliance.flows.UpdateClaimTemplate;
import com.compliance.states.ClaimTemplate;
import com.compliance.supervisoryauthority.NodeRPCConnection;
import com.compliance.supervisoryauthority.models.ClaimTemplateDTO;
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
@RequestMapping("/claimtemplates") // The paths for HTTP requests are relative to this base path.
public class ClaimTemplateController {
    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(ClaimTemplateController.class);

    public ClaimTemplateController(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }

    @GetMapping(value = "/", produces = APPLICATION_JSON_VALUE)
    private List<ClaimTemplate> getAll() {
        return proxy
                .vaultQuery(ClaimTemplate.class)
                .getStates()
                .stream()
                .map(
                        claimTemplateStateAndRef -> claimTemplateStateAndRef.getState().getData()
                )
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/{linearId}", produces = APPLICATION_JSON_VALUE)
    private List<ClaimTemplate> getByLinearId(@PathVariable String linearId) {
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(UniqueIdentifier.Companion.fromString(linearId)), Vault.StateStatus.ALL, Collections.singleton(ClaimTemplate.class));
        return proxy
                .vaultQueryByCriteria(queryCriteria, ClaimTemplate.class)
                .getStates()
                .stream()
                .map(
                        claimTemplateStateAndRef -> claimTemplateStateAndRef.getState().getData()
                )
                .collect(Collectors.toList());
    }

    @PutMapping(value = "/")
    private void update(@RequestBody ClaimTemplateDTO claimTemplateDTO) throws ExecutionException, InterruptedException {
        UniqueIdentifier id = UniqueIdentifier.Companion.fromString(claimTemplateDTO.getLinearId());
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(id), Vault.StateStatus.ALL, Collections.singleton(ClaimTemplate.class));
        // Check if state with that linear ID exists
        // Todo: Should throw custom exception if no regulation with the ID exists
        if (!proxy.vaultQueryByCriteria(queryCriteria, ClaimTemplate.class).getStates().isEmpty()) {
            // Call the update flow
            SignedTransaction tx = proxy.startTrackedFlowDynamic(
                    UpdateClaimTemplate.UpdateClaimTemplateInitiator.class,
                    id,
                    claimTemplateDTO.getName(),
                    claimTemplateDTO.getTemplateDescription(),
                    UniqueIdentifier.Companion.fromString(claimTemplateDTO.getRule())
            ).getReturnValue().get();
        }
    }


    @PostMapping("/")
    private ClaimTemplate create(@RequestBody ClaimTemplateDTO claimTemplateDTO) throws ExecutionException, InterruptedException {
        SignedTransaction tx = proxy.startTrackedFlowDynamic(
                CreateClaimTemplate.CreateClaimTemplateInitiator.class,
                claimTemplateDTO.getName(),
                claimTemplateDTO.getTemplateDescription(),
                UniqueIdentifier.Companion.fromString(claimTemplateDTO.getRule()),
                new Date()
        ).getReturnValue().get();

        List<ClaimTemplate> claimTemplates = proxy
                .vaultQuery(ClaimTemplate.class)
                .getStates()
                .stream()
                .map(
                        ruleStateAndRef -> ruleStateAndRef.getState().getData())
                .collect(Collectors.toList());

        // Return regulation linear ID
        return claimTemplates
                .stream()
                .filter(
                        claimTemplate -> claimTemplate.getName().equals(claimTemplateDTO.getName()) && claimTemplate.getTemplateDescription().equals(claimTemplateDTO.getTemplateDescription())
                )
                .collect(Collectors.toList())
                .get(0);
    }
}