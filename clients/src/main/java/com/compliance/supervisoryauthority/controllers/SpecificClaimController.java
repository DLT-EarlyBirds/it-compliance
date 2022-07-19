package com.compliance.supervisoryauthority.controllers;

import com.compliance.states.SpecificClaim;
import com.compliance.supervisoryauthority.NodeRPCConnection;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Define your API endpoints here.
 */
@RestController
@CrossOrigin(origins = "*")
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
    private ResponseEntity<SpecificClaim> getByLinearId(@PathVariable String linearId) {
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(
                null,
                Collections.singletonList(UniqueIdentifier.Companion.fromString(linearId)),
                Vault.StateStatus.UNCONSUMED,
                Collections.singleton(SpecificClaim.class)
        );
        List<SpecificClaim> specificClaims = proxy
                .vaultQueryByCriteria(queryCriteria, SpecificClaim.class)
                .getStates()
                .stream()
                .map(
                        claimTemplateStateAndRef -> claimTemplateStateAndRef.getState().getData()
                )
                .collect(Collectors.toList());

        if (specificClaims.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        else return ResponseEntity.status(HttpStatus.OK).body(specificClaims.get(0));
     }

    @GetMapping(value = "/{name}/", produces = APPLICATION_JSON_VALUE)
    private ResponseEntity<List<SpecificClaim>> getAllForOrg(@PathVariable String name) {
        if (!proxy.partiesFromName(name, true).isEmpty()) {
            List<Party> issuer = new ArrayList<>(proxy.partiesFromName(name, true));
            QueryCriteria criteria = new QueryCriteria.LinearStateQueryCriteria(
                    issuer,
                    null,
                    Vault.StateStatus.UNCONSUMED,
                    Collections.singleton(SpecificClaim.class)
            );
            List<SpecificClaim> specificClaims = proxy
                    .vaultQueryByCriteria(criteria, SpecificClaim.class)
                    .getStates()
                    .stream()
                    .map(
                            specificClaimStateAndRef -> specificClaimStateAndRef.getState().getData()
                    )
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK).body(specificClaims);
        } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping(value = "/{name}/{linearId}", produces = APPLICATION_JSON_VALUE)
    private ResponseEntity<SpecificClaim> getAllForOrgByLinearId(@PathVariable String name, @PathVariable String linearId) {
        if (!proxy.partiesFromName(name, true).isEmpty()) {
            List<Party> issuer = new ArrayList<>(proxy.partiesFromName(name, true));
            QueryCriteria criteria = new QueryCriteria.LinearStateQueryCriteria(
                    issuer,
                    Collections.singletonList(UniqueIdentifier.Companion.fromString(linearId)),
                    Vault.StateStatus.UNCONSUMED,
                    Collections.singleton(SpecificClaim.class)
            );
            List<SpecificClaim> specificClaims = proxy
                    .vaultQueryByCriteria(criteria, SpecificClaim.class)
                    .getStates()
                    .stream()
                    .map(
                            specificClaimStateAndRef -> specificClaimStateAndRef.getState().getData()
                    )
                    .collect(Collectors.toList());
            if (specificClaims.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            else return ResponseEntity.status(HttpStatus.OK).body(specificClaims.get(0));
        } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping("/attachment/{linearId}")
    private ResponseEntity<InputStreamResource> openAttachment(@PathVariable String linearId) throws ExecutionException, InterruptedException, IOException {
        UniqueIdentifier id = UniqueIdentifier.Companion.fromString(linearId);
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(
                null,
                Collections.singletonList(id),
                Vault.StateStatus.UNCONSUMED,
                Collections.singleton(SpecificClaim.class));
        // Get state with linear id
        List<SpecificClaim> specificClaims = proxy.vaultQueryByCriteria(queryCriteria, SpecificClaim.class).getStates().stream().map(
                specificClaimStateAndRef -> specificClaimStateAndRef.getState().getData()
        ).collect(Collectors.toList());
        // Check if state with that linear ID exists
        if (!specificClaims.isEmpty()) {
            SpecificClaim specificClaim = specificClaims.get(0);
            if (proxy.attachmentExists(specificClaim.getAttachmentID())) {
                InputStreamResource file = new InputStreamResource(proxy.openAttachment(specificClaim.getAttachmentID()));
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + specificClaim.getAttachmentID() + ".jar\"").body(file);
            } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}