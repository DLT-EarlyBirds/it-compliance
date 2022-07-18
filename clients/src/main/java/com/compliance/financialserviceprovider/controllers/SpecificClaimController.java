package com.compliance.financialserviceprovider.controllers;

import com.compliance.flows.CreateSpecificClaim;
import com.compliance.flows.UpdateSpecificClaim;
import com.compliance.states.SpecificClaim;
import com.compliance.financialserviceprovider.NodeRPCConnection;
import com.compliance.financialserviceprovider.models.SpecificClaimDTO;
import liquibase.util.file.FilenameUtils;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
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

    @PutMapping(value = "/")
    private ResponseEntity<SpecificClaim> update(@RequestBody SpecificClaimDTO specificClaimDTO) throws ExecutionException, InterruptedException {
        UniqueIdentifier id = UniqueIdentifier.Companion.fromString(specificClaimDTO.getLinearId());
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(
                null,
                Collections.singletonList(id),
                Vault.StateStatus.UNCONSUMED,
                Collections.singleton(SpecificClaim.class));
        // Check if state with that linear ID exists
        if (!proxy.vaultQueryByCriteria(queryCriteria, SpecificClaim.class).getStates().isEmpty()) {
            // Call the update flow
            Set<Party> authority = proxy.partiesFromName("Supervisory Authority", true);
            Set<Party> auditor = proxy.partiesFromName("Auditor", true);


            if (!authority.isEmpty()) {
                SpecificClaim specificClaim = (SpecificClaim) proxy.startTrackedFlowDynamic(
                        UpdateSpecificClaim.UpdateSpecificClaimInitiator.class,
                        id,
                        specificClaimDTO.getName(),
                        specificClaimDTO.getClaimSpecification(),
                        new ArrayList<>(authority).get(0),
                        new ArrayList<>(auditor).get(0),
                        UniqueIdentifier.Companion.fromString(specificClaimDTO.getClaimTemplateLinearId()),
                        new ArrayList<UniqueIdentifier>(),
                        null
                ).getReturnValue().get().getTx().getOutput(0);
                return ResponseEntity.status(HttpStatus.OK).body(specificClaim);
            } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }


    @PostMapping("/")
    private ResponseEntity<SpecificClaim> create(@RequestBody SpecificClaimDTO specificClaimDTO) throws ExecutionException, InterruptedException {
        Set<Party> authority = proxy.partiesFromName("Supervisory Authority", true);
        Set<Party> auditor = proxy.partiesFromName("Auditor", true);

        if (!authority.isEmpty() && !auditor.isEmpty()) {
            SpecificClaim specificClaim = (SpecificClaim) proxy.startTrackedFlowDynamic(
                    CreateSpecificClaim.CreateSpecificClaimInitiator.class,
                    specificClaimDTO.getName(),
                    specificClaimDTO.getClaimSpecification(),
                    new ArrayList<>(authority).get(0),
                    new ArrayList<>(auditor).get(0),
                    UniqueIdentifier.Companion.fromString(specificClaimDTO.getClaimTemplateLinearId()),
                    new ArrayList<UniqueIdentifier>()
            ).getReturnValue().get().getTx().getOutput(0);
            return ResponseEntity.status(HttpStatus.CREATED).body(specificClaim);
        } else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @PostMapping("/attachment/{linearId}")
    private ResponseEntity<SpecificClaim> addAttachment(@RequestBody MultipartFile file, @PathVariable String linearId) throws ExecutionException, InterruptedException, IOException {
        if (FilenameUtils.getExtension(file.getOriginalFilename()).equals("jar")) {
            SecureHash secureHash = proxy.uploadAttachment(file.getInputStream());

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
                Set<Party> partySet = proxy.partiesFromName("Supervisory Authority", true);

                if (!partySet.isEmpty()) {
                    Party supervisoryAuthority = new ArrayList<>(partySet).get(0);
                    SpecificClaim specificClaim = specificClaims.get(0);
                    // Call the update flow
                    SpecificClaim output = (SpecificClaim) proxy.startTrackedFlowDynamic(
                            UpdateSpecificClaim.UpdateSpecificClaimInitiator.class,
                            specificClaim.getLinearId(),
                            specificClaim.getName(),
                            specificClaim.getDescription(),
                            supervisoryAuthority,
                            specificClaim.getClaimTemplate().getPointer(),
                            new ArrayList<UniqueIdentifier>(),
                            secureHash
                    ).getReturnValue().get().getTx().getOutput(0);
                    return ResponseEntity.status(HttpStatus.OK).body(output);
                } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(null);
    }
}