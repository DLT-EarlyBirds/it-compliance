package com.compliance.supervisoryauthority.controllers;

import com.compliance.states.SpecificClaim;
import com.compliance.supervisoryauthority.NodeRPCConnection;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/claims") // The paths for HTTP requests are relative to this base path.
public class SpecificClaimController {
    private final CordaRPCOps proxy;
    private final CordaX500Name me;
    private final static Logger logger = LoggerFactory.getLogger(SpecificClaimController.class);

    public SpecificClaimController(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
        this.me = proxy.nodeInfo().getLegalIdentities().get(0).getName();
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

    @GetMapping(value = "/{name}/", produces = APPLICATION_JSON_VALUE)
    private List<SpecificClaim> getAllForOrg(@PathVariable String name) {
        if (!proxy.partiesFromName(name, true).isEmpty()) {
            List<Party> issuer = new ArrayList<>(proxy.partiesFromName(name, true));
            QueryCriteria criteria = new QueryCriteria.LinearStateQueryCriteria(
                    issuer,
                    null,
                    Vault.StateStatus.ALL,
                    Collections.singleton(SpecificClaim.class)
            );
            return proxy
                    .vaultQueryByCriteria(criteria, SpecificClaim.class)
                    .getStates()
                    .stream()
                    .map(
                            specificClaimStateAndRef -> specificClaimStateAndRef.getState().getData()
                    )
                    .collect(Collectors.toList());
        } else return new ArrayList<>();
    }

    @GetMapping(value = "/{name}/{linearId}", produces = APPLICATION_JSON_VALUE)
    private List<SpecificClaim> getAllForOrg(@PathVariable String name, @PathVariable String linearId) {
        if (!proxy.partiesFromName(name, true).isEmpty()) {
            List<Party> issuer = new ArrayList<>(proxy.partiesFromName(name, true));
            QueryCriteria criteria = new QueryCriteria.LinearStateQueryCriteria(
                    issuer,
                    Collections.singletonList(UniqueIdentifier.Companion.fromString(linearId)),
                    Vault.StateStatus.ALL,
                    Collections.singleton(SpecificClaim.class)
            );
            return proxy
                    .vaultQueryByCriteria(criteria, SpecificClaim.class)
                    .getStates()
                    .stream()
                    .map(
                            specificClaimStateAndRef -> specificClaimStateAndRef.getState().getData()
                    )
                    .collect(Collectors.toList());
        } else return new ArrayList<>();
    }
}