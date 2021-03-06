package com.compliance.states;

import com.compliance.contracts.SpecificClaimContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.crypto.SecureHash;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * A specific claim is a claim that is based on a claim template and is issued by a financial service provider to a
 * supervisory authority to show that its fulfilling an issued rule. The attachment contains evidence in form of structured data.
 */
@BelongsToContract(SpecificClaimContract.class)
public class SpecificClaim implements LinearState {

    // Linear ID is used to track the evolution of the state over multiple transactions
    @NotNull
    private final UniqueIdentifier linearId;

    private final String name;

    // This is the hash of the attachment that is stored on the ledger.
    private SecureHash attachmentID;

    private final String description;

    @NotNull
    private final Party financialServiceProvider;

    @NotNull
    private final Party supervisoryAuthority;

    @NotNull
    private final Party auditor;

    private final List<AbstractParty> participants;

    // A reference to the claim template that is implemented by this specific claim.
    private final LinearPointer<ClaimTemplate> claimTemplate;

    // References to other specific claims which can be used as evidence references when this claim is to be proven.
    private final List<LinearPointer<SpecificClaim>> supportingClaims;


    /* Constructor of your Corda state */
    public SpecificClaim(UniqueIdentifier linearId,
                         String name,
                         String description,
                         @NotNull Party financialServiceProvider,
                         @NotNull Party supervisoryAuthority,
                         Party auditor,
                         LinearPointer<ClaimTemplate> claimTemplate,
                         List<LinearPointer<SpecificClaim>> supportingClaims) {
        this.name = name;
        this.auditor = auditor;
        this.linearId = linearId;
        this.description = description;
        this.claimTemplate = claimTemplate;
        this.supportingClaims = supportingClaims;
        this.supervisoryAuthority = supervisoryAuthority;
        this.financialServiceProvider = financialServiceProvider;

        this.participants = new ArrayList<>();
        this.participants.add(auditor);
        this.participants.add(supervisoryAuthority);
        this.participants.add(financialServiceProvider);
    }

    @ConstructorForDeserialization
    public SpecificClaim(UniqueIdentifier linearId,
                         String name,
                         String description,
                         @NotNull Party financialServiceProvider,
                         @NotNull Party supervisoryAuthority,
                         Party auditor,
                         LinearPointer<ClaimTemplate> claimTemplate,
                         List<LinearPointer<SpecificClaim>> supportingClaims,
                         SecureHash attachmentID) {
        this.name = name;
        this.auditor = auditor;
        this.linearId = linearId;
        this.description = description;
        this.attachmentID = attachmentID;
        this.claimTemplate = claimTemplate;
        this.supportingClaims = supportingClaims;
        this.supervisoryAuthority = supervisoryAuthority;
        this.financialServiceProvider = financialServiceProvider;

        this.participants = new ArrayList<>();
        this.participants.add(auditor);
        this.participants.add(supervisoryAuthority);
        this.participants.add(financialServiceProvider);
    }

    @Override
    @NotNull
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    public String getName() {
        return name;
    }

    @NotNull
    public Party getFinancialServiceProvider() {
        return financialServiceProvider;
    }

    @NotNull
    public Party getsupervisoryAuthority() {
        return supervisoryAuthority;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return participants;
    }

    public LinearPointer<ClaimTemplate> getClaimTemplate() {
        return claimTemplate;
    }

    public List<LinearPointer<SpecificClaim>> getSupportingClaims() {
        return supportingClaims;
    }

    public SecureHash getAttachmentID() {
        return attachmentID;
    }

    public String getDescription() {
        return description;
    }

    public Party getAuditor(){return auditor;}
}