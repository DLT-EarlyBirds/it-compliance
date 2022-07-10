package com.template.states;

import com.template.contracts.SpecificClaimContract;
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
import java.util.stream.Collectors;

// *********
// * State *
// *********
@BelongsToContract(SpecificClaimContract.class)
public class SpecificClaim implements LinearState {

    // Private variables
    // Linear ID is used to track the evolution of the state over multiple transactions
    @NotNull
    private final UniqueIdentifier linearId;
    private final String name;

    private SecureHash attachmentID;

    private final String description;
    @NotNull
    private final Party financialServiceProvider;
    @NotNull
    private final Party supervisorAuthority;
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
                         @NotNull Party supervisorAuthority,
                         LinearPointer<ClaimTemplate> claimTemplate,
                         List<LinearPointer<SpecificClaim>> supportingClaims) {
        this.name = name;
        this.linearId = linearId;
        this.description = description;
        this.claimTemplate = claimTemplate;
        this.supportingClaims = supportingClaims;
        this.supervisorAuthority = supervisorAuthority;
        this.financialServiceProvider = financialServiceProvider;

        this.participants = new ArrayList<>();
        this.participants.add(supervisorAuthority);
        this.participants.add(financialServiceProvider);
    }

    @ConstructorForDeserialization
    public SpecificClaim(UniqueIdentifier linearId,
                         String name,
                         String description,
                         @NotNull Party financialServiceProvider,
                         @NotNull Party supervisorAuthority,
                         LinearPointer<ClaimTemplate> claimTemplate,
                         List<LinearPointer<SpecificClaim>> supportingClaims,
                         SecureHash attachmentID) {
        this.name = name;
        this.linearId = linearId;
        this.description = description;
        this.attachmentID = attachmentID;
        this.claimTemplate = claimTemplate;
        this.supportingClaims = supportingClaims;
        this.supervisorAuthority = supervisorAuthority;
        this.financialServiceProvider = financialServiceProvider;

        this.participants = new ArrayList<>();
        this.participants.add(supervisorAuthority);
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
    public Party getSupervisorAuthority() {
        return supervisorAuthority;
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
}