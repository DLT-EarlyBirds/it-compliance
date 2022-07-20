package com.compliance.auditor.controllers;


import com.compliance.financialserviceprovider.models.ClaimTemplateSuggestionDTO;
import com.compliance.flows.CreateClaimTemplateSuggestion;
import com.compliance.states.ClaimTemplate;
import com.compliance.states.ClaimTemplateSuggestion;
import com.compliance.auditor.NodeRPCConnection;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


/**
 * It's a Spring Boot controller that exposes endpoints for the creation of claim template suggestions and the retrieval
 * of claim templates and claim template suggestions
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/claimtemplates") // The paths for HTTP requests are relative to this base path.
public class ClaimTemplateController {
    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(ClaimTemplateController.class);

    // It's a constructor that takes a NodeRPCConnection object as a parameter and assigns the proxy field to the proxy
    // field of the NodeRPCConnection object. The proxy is used to interact with the Corda node
    public ClaimTemplateController(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }

    /**
     * This endpoint returns a list of all the ClaimTemplate states in the vault
     *
     * @return A list of ClaimTemplate objects
     */
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

    /**
     * This endpoint returns a claim template by its linear ID
     *
     * @param linearId The linearId of the ClaimTemplate to be retrieved.
     * @return A ClaimTemplate object
     */
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

    /**
     * This endpoint returns a list of all the ClaimTemplateSuggestion states in the vault
     *
     * @return A list of ClaimTemplateSuggestion objects
     */
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

    /**
     * This endpoint returns a ClaimTemplateSuggestion object from the vault by its linearId
     *
     * @param linearId The linearId of the ClaimTemplateSuggestion to be retrieved.
     * @return A ClaimTemplateSuggestion object
     */
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

    /**
     * This endpoint creates a claim template suggestion, and returns it
     *
     * @param claimTemplateSuggestionDTO The DTO object that contains the data that will be used to create the
     * ClaimTemplateSuggestion.
     * @return A ClaimTemplateSuggestion object
     */
    @PostMapping("/suggestions/")
    private ResponseEntity<ClaimTemplateSuggestion> createSuggestion(@RequestBody ClaimTemplateSuggestionDTO claimTemplateSuggestionDTO) throws ExecutionException, InterruptedException {
        Set<Party> partySet = proxy.partiesFromName("Supervisory Authority", true);

        if (!partySet.isEmpty()) {
            Party supervisoryAuthority = new ArrayList<>(partySet).get(0);

            ClaimTemplateSuggestion claimTemplateSuggestion = (ClaimTemplateSuggestion) proxy.startTrackedFlowDynamic(
                    CreateClaimTemplateSuggestion.CreateClaimTemplateSuggestionInitiator.class,
                    claimTemplateSuggestionDTO.getName(),
                    claimTemplateSuggestionDTO.getTemplateDescription(),
                    supervisoryAuthority,
                    UniqueIdentifier.Companion.fromString(claimTemplateSuggestionDTO.getRule())
            ).getReturnValue().get().getTx().getOutput(0);
            return ResponseEntity.status(HttpStatus.CREATED).body(claimTemplateSuggestion);
        } else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}