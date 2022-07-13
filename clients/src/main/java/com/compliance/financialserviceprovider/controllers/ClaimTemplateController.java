package com.compliance.financialserviceprovider.controllers;


import com.compliance.financialserviceprovider.models.ClaimTemplateSuggestionDTO;
import com.compliance.flows.CreateClaimTemplateSuggestion;
import com.compliance.states.ClaimTemplate;
import com.compliance.states.ClaimTemplateSuggestion;
import com.compliance.financialserviceprovider.NodeRPCConnection;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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
    private List<ClaimTemplateSuggestion> getSuggestionByLinearId(@PathVariable String linearId) {
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(
                null,
                Collections.singletonList(UniqueIdentifier.Companion.fromString(linearId)),
                Vault.StateStatus.ALL,
                Collections.singleton(ClaimTemplate.class)
        );
        return proxy
                .vaultQueryByCriteria(queryCriteria, ClaimTemplateSuggestion.class)
                .getStates()
                .stream()
                .map(
                        claimTemplateSuggestionStateAndRef -> claimTemplateSuggestionStateAndRef.getState().getData()
                )
                .collect(Collectors.toList());
    }

    @PostMapping("/suggestions/")
    private ClaimTemplateSuggestion createSuggestion(@RequestBody ClaimTemplateSuggestionDTO claimTemplateSuggestionDTO) throws ExecutionException, InterruptedException {
        Set<Party> partySet = proxy.partiesFromName("Supervisory Authority", true);

        if (!partySet.isEmpty()) {
            Party supervisoryAuthority = new ArrayList<>(partySet).get(0);

            proxy.startTrackedFlowDynamic(
                    CreateClaimTemplateSuggestion.CreateClaimTemplateSuggestionInitiator.class,
                    claimTemplateSuggestionDTO.getName(),
                    claimTemplateSuggestionDTO.getTemplateDescription(),
                    supervisoryAuthority,
                    UniqueIdentifier.Companion.fromString(claimTemplateSuggestionDTO.getRule())
                    ).getReturnValue().get();

            List<ClaimTemplateSuggestion> claimTemplateSuggestions = proxy
                    .vaultQuery(ClaimTemplateSuggestion.class)
                    .getStates()
                    .stream()
                    .map(
                            claimTemplateSuggestionStateAndRef -> claimTemplateSuggestionStateAndRef.getState().getData())
                    .collect(Collectors.toList());

            // Return regulation linear ID
            return claimTemplateSuggestions
                    .stream()
                    .filter(
                            claimTemplateSuggestion -> claimTemplateSuggestion.getName().equals(claimTemplateSuggestionDTO.getName()) && claimTemplateSuggestion.getTemplateDescription().equals(claimTemplateSuggestionDTO.getTemplateDescription())
                    )
                    .collect(Collectors.toList())
                    .get(0);
        } else return null;
    }
}