package com.compliance.financialserviceprovider.controllers;



import com.compliance.flows.AcceptClaimTemplateSuggestion;
import com.compliance.states.ClaimTemplate;
import com.compliance.states.ClaimTemplateSuggestion;
import com.compliance.supervisoryauthority.NodeRPCConnection;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @PostMapping("/suggestions/{linearId}")
    private ClaimTemplate acceptSuggestion(@PathVariable String linearId) throws ExecutionException, InterruptedException {
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(
                null,
                Collections.singletonList(UniqueIdentifier.Companion.fromString(linearId)),
                Vault.StateStatus.ALL,
                Collections.singleton(ClaimTemplate.class)
        );

        ClaimTemplateSuggestion suggestion;

        List<ClaimTemplateSuggestion> suggestions = proxy
                .vaultQueryByCriteria(queryCriteria, ClaimTemplateSuggestion.class)
                .getStates()
                .stream()
                .map(
                        claimTemplateSuggestionStateAndRef -> claimTemplateSuggestionStateAndRef.getState().getData()
                )
                .collect(Collectors.toList());

        if (!suggestions.isEmpty()) {

            suggestion = suggestions.get(0);

            proxy.startTrackedFlowDynamic(
                    AcceptClaimTemplateSuggestion.AcceptClaimTemplateSuggestionInitiator.class,
                    UniqueIdentifier.Companion.fromString(linearId)
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
                            claimTemplate -> claimTemplate.getName().equals(suggestion.getName()) && claimTemplate.getTemplateDescription().equals(suggestion.getTemplateDescription())
                    )
                    .collect(Collectors.toList())
                    .get(0);

        } else return null;
    }
}