package com.compliance.supervisoryauthority.controllers;



import com.compliance.flows.AcceptClaimTemplateSuggestion;
import com.compliance.flows.CreateClaimTemplate;
import com.compliance.flows.UpdateClaimTemplate;
import com.compliance.states.ClaimTemplate;
import com.compliance.states.ClaimTemplateSuggestion;
import com.compliance.supervisoryauthority.NodeRPCConnection;
import com.compliance.supervisoryauthority.models.ClaimTemplateDTO;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private ResponseEntity<ClaimTemplate> getByLinearId(@PathVariable String linearId) {
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(UniqueIdentifier.Companion.fromString(linearId)), Vault.StateStatus.UNCONSUMED, Collections.singleton(ClaimTemplate.class));
        List<ClaimTemplate> claimTemplates = proxy
                .vaultQueryByCriteria(queryCriteria, ClaimTemplate.class)
                .getStates()
                .stream()
                .map(
                        claimTemplateStateAndRef -> claimTemplateStateAndRef.getState().getData()
                )
                .collect(Collectors.toList());

        if (claimTemplates.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        else return ResponseEntity.status(HttpStatus.OK).body(claimTemplates.get(0));
    }

    @PutMapping(value = "/")
    private ResponseEntity<ClaimTemplate> update(@RequestBody ClaimTemplateDTO claimTemplateDTO) throws ExecutionException, InterruptedException {
        UniqueIdentifier id = UniqueIdentifier.Companion.fromString(claimTemplateDTO.getLinearId());
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(id), Vault.StateStatus.UNCONSUMED, Collections.singleton(ClaimTemplate.class));
        // Check if state with that linear ID exists
        if (!proxy.vaultQueryByCriteria(queryCriteria, ClaimTemplate.class).getStates().isEmpty()) {
            // Call the update flow
            ClaimTemplate claimTemplate = (ClaimTemplate) proxy.startTrackedFlowDynamic(
                    UpdateClaimTemplate.UpdateClaimTemplateInitiator.class,
                    id,
                    claimTemplateDTO.getName(),
                    claimTemplateDTO.getTemplateDescription(),
                    UniqueIdentifier.Companion.fromString(claimTemplateDTO.getRule())
            ).getReturnValue().get().getTx().getOutput(0);
            return ResponseEntity.status(HttpStatus.OK).body(claimTemplate);
        } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }


    @PostMapping("/")
    private ResponseEntity<ClaimTemplate> create(@RequestBody ClaimTemplateDTO claimTemplateDTO) throws ExecutionException, InterruptedException {
        ClaimTemplate claimTemplate = (ClaimTemplate) proxy.startTrackedFlowDynamic(
                CreateClaimTemplate.CreateClaimTemplateInitiator.class,
                claimTemplateDTO.getName(),
                claimTemplateDTO.getTemplateDescription(),
                UniqueIdentifier.Companion.fromString(claimTemplateDTO.getRule()),
                new Date()
        ).getReturnValue().get().getTx().getOutput(0);
        return ResponseEntity.status(HttpStatus.CREATED).body(claimTemplate);
    }

    @GetMapping(value = "/suggestions/", produces = APPLICATION_JSON_VALUE)
    private List<ClaimTemplateSuggestion> getAllSuggestions() {
        return proxy
                .vaultQuery(ClaimTemplateSuggestion.class)
                .getStates()
                .stream()
                .map(
                        claimTemplateSuggestionStateAndRef -> claimTemplateSuggestionStateAndRef.getState().getData()
                )
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/suggestions/{linearId}", produces = APPLICATION_JSON_VALUE)
    private ResponseEntity<ClaimTemplateSuggestion> getSuggestionByLinearId(@PathVariable String linearId) {
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(
                null,
                Collections.singletonList(UniqueIdentifier.Companion.fromString(linearId)),
                Vault.StateStatus.UNCONSUMED,
                Collections.singleton(ClaimTemplate.class)
        );
        List<ClaimTemplateSuggestion> claimTemplateSuggestions =  proxy
                .vaultQueryByCriteria(queryCriteria, ClaimTemplateSuggestion.class)
                .getStates()
                .stream()
                .map(
                        claimTemplateSuggestionStateAndRef -> claimTemplateSuggestionStateAndRef.getState().getData()
                )
                .collect(Collectors.toList());
        if (claimTemplateSuggestions.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        else return ResponseEntity.status(HttpStatus.OK).body(claimTemplateSuggestions.get(0));
    }

    @PostMapping("/suggestions/{linearId}")
    private ResponseEntity<ClaimTemplate> acceptSuggestion(@PathVariable String linearId) throws ExecutionException, InterruptedException {
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(
                null,
                Collections.singletonList(UniqueIdentifier.Companion.fromString(linearId)),
                Vault.StateStatus.UNCONSUMED,
                Collections.singleton(ClaimTemplate.class)
        );

        List<ClaimTemplateSuggestion> suggestions = proxy
                .vaultQueryByCriteria(queryCriteria, ClaimTemplateSuggestion.class)
                .getStates()
                .stream()
                .map(
                        claimTemplateSuggestionStateAndRef -> claimTemplateSuggestionStateAndRef.getState().getData()
                )
                .collect(Collectors.toList());

        if (!suggestions.isEmpty()) {
            ClaimTemplate claimTemplate = (ClaimTemplate) proxy.startTrackedFlowDynamic(
                    AcceptClaimTemplateSuggestion.AcceptClaimTemplateSuggestionInitiator.class,
                    UniqueIdentifier.Companion.fromString(linearId)
            ).getReturnValue().get().getTx().getOutput(0);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(claimTemplate);
        } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}