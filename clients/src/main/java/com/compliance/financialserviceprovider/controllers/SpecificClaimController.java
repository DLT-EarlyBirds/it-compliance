package com.compliance.financialserviceprovider.controllers;

import com.compliance.flows.CreateSpecificClaim;
import com.compliance.flows.UpdateSpecificClaim;
import com.compliance.states.SpecificClaim;
import com.compliance.financialserviceprovider.NodeRPCConnection;
import com.compliance.financialserviceprovider.models.SpecificClaimDTO;
import liquibase.util.file.FilenameUtils;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


/**
 * A REST controller that handles HTTP requests for the `SpecificClaim` state
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

    /**
     * REST endpoint that returns a list of all the SpecificClaims that are stored on the ledger of the fsp
     *
     * @return A list of SpecificClaim objects
     */
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

    /**
     * REST endpoint finds a specific claim by its linearId
     *
     * @param linearId The linearId of the claim you want to retrieve as a path variable.
     * @return A list of SpecificClaims
     */
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

    /**
     * Updates a specific claim with the values passed in the DTO.
     * Attachments can not be changed via this endpoint. The linearId must be passed to find the object
     *
     * @param specificClaimDTO The DTO object that contains the updated information for the specific claim.
     * @return The updated SpecificClaim state.
     */
    @PutMapping(value = "/")
    private ResponseEntity<SpecificClaim> update(@RequestBody SpecificClaimDTO specificClaimDTO) throws ExecutionException, InterruptedException {
        UniqueIdentifier id = UniqueIdentifier.Companion.fromString(specificClaimDTO.getLinearId());
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(
                null,
                Collections.singletonList(id),
                Vault.StateStatus.UNCONSUMED,
                Collections.singleton(SpecificClaim.class));

        List<StateAndRef<SpecificClaim>> linearIdClaims = proxy.vaultQueryByCriteria(queryCriteria, SpecificClaim.class).getStates();
        // Check if state with that linear ID exists
        if (!linearIdClaims.isEmpty()) {
            // Call the update flow
            Set<Party> authority = proxy.partiesFromName("Supervisory Authority", true);
            Set<Party> auditor = proxy.partiesFromName("Auditor", true);
            List<UniqueIdentifier> supportingClaims = new ArrayList<>();

            if (specificClaimDTO.getSupportingClaimIds() != null) {
                Arrays.stream(specificClaimDTO.getSupportingClaimIds()).forEach(s -> {
                    supportingClaims.add(UniqueIdentifier.Companion.fromString(s));
                });
            }

            if (!authority.isEmpty() && !auditor.isEmpty()) {
                SpecificClaim specificClaim = (SpecificClaim) proxy.startTrackedFlowDynamic(
                        UpdateSpecificClaim.UpdateSpecificClaimInitiator.class,
                        id,
                        specificClaimDTO.getName(),
                        specificClaimDTO.getClaimSpecification(),
                        new ArrayList<>(authority).get(0),
                        new ArrayList<>(auditor).get(0),
                        UniqueIdentifier.Companion.fromString(specificClaimDTO.getClaimTemplateLinearId()),
                        supportingClaims
                ).getReturnValue().get().getTx().getOutput(0);
                return ResponseEntity.status(HttpStatus.OK).body(specificClaim);
            } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }


    /**
     * It creates a new specific claim, and returns it.
     *
     * @param specificClaimDTO The DTO object that contains the parameters for the creation flow. Has an empty string as linearId
     * @return A ResponseEntity object is being returned.
     */
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

    /**
     * Endpoint to upload a file to the node and adds the attachment to the passed specific claim via its linearId
     *
     * @param file The file to be uploaded
     * @param linearId The linear ID of the claim you want to add an attachment to.
     * @return The updated state
     */
    @PostMapping(value = "/attachment/{linearId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    private ResponseEntity<SpecificClaim> addAttachment(@RequestBody MultipartFile file, @PathVariable String linearId) throws ExecutionException, InterruptedException, IOException {
        if (Objects.equals(FilenameUtils.getExtension(file.getOriginalFilename()), "jar") || file.getOriginalFilename() != null) {

            // upload the file with my common name as uploader and the current date as file name
            SecureHash secureHash = proxy.uploadAttachmentWithMetadata(
                    file.getInputStream(),
                    Objects.requireNonNull(proxy.nodeInfo().getLegalIdentities().get(0).getName().toString()),
                    new Date().toString() + "." + FilenameUtils.getExtension(file.getOriginalFilename())
            );

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
                Set<Party> authority = proxy.partiesFromName("Supervisory Authority", true);
                Set<Party> auditor = proxy.partiesFromName("Auditor", true);

                if (!authority.isEmpty() && !auditor.isEmpty()) {
                    SpecificClaim specificClaim = specificClaims.get(0);
                    // Call the update flow
                    SpecificClaim output = (SpecificClaim) proxy.startTrackedFlowDynamic(
                            UpdateSpecificClaim.UpdateSpecificClaimInitiator.class,
                            specificClaim.getLinearId(),
                            specificClaim.getName(),
                            specificClaim.getDescription(),
                            new ArrayList<>(authority).get(0),
                            new ArrayList<>(auditor).get(0),
                            specificClaim.getClaimTemplate().getPointer(),
                            specificClaim.getSupportingClaims(),
                            secureHash
                    ).getReturnValue().get().getTx().getOutput(0);
                    return ResponseEntity.status(HttpStatus.OK).body(output);
                } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(null);
    }

    /**
     * Endpoint that takes a linear ID to download the attachment of a specific claim. For this it finds the state with
     * that linear ID, checks if the attachment exists, and if it does, returns the attachment
     *
     * @param linearId The linear ID of the state that contains the attachment ID.
     * @return The file is being returned.
     */
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