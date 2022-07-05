package com.template.states;

import com.template.contracts.SpecificClaimContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
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
    private final String hashValue;

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
    @ConstructorForDeserialization
    public SpecificClaim(String hashValue, @NotNull Party financialServiceProvider, @NotNull Party supervisorAuthority, UniqueIdentifier claimTemplateLinearId, List<UniqueIdentifier> supportingClaimsLinearIds) {
        this.linearId = new UniqueIdentifier();
        this.financialServiceProvider = financialServiceProvider;
        this.supervisorAuthority = supervisorAuthority;
        this.hashValue = hashValue;
        this.claimTemplate = new LinearPointer<>(claimTemplateLinearId, ClaimTemplate.class);
        this.supportingClaims = supportingClaimsLinearIds.stream().map(claimLinearId -> {
            return new LinearPointer<>(claimLinearId, SpecificClaim.class);
        }).collect(Collectors.toList());


        this.participants = new ArrayList<>();
        this.participants.add(financialServiceProvider);
        this.participants.add(supervisorAuthority);
    }

    public SpecificClaim(@NotNull UniqueIdentifier linearId, String hashValue, @NotNull Party financialServiceProvider, @NotNull Party supervisorAuthority, UniqueIdentifier claimTemplateLinearId, List<UniqueIdentifier> supportedClaimsLinearIds) {
        this.linearId = linearId;
        this.hashValue = hashValue;
        this.financialServiceProvider = financialServiceProvider;
        this.supervisorAuthority = supervisorAuthority;
        this.claimTemplate = new LinearPointer<>(claimTemplateLinearId, ClaimTemplate.class);
        this.supportingClaims = supportedClaimsLinearIds.stream().map(claimLinearId -> {
            return new LinearPointer<>(claimLinearId, SpecificClaim.class);
        }).collect(Collectors.toList());

        this.participants = new ArrayList<>();
        this.participants.add(financialServiceProvider);
        this.participants.add(supervisorAuthority);

    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return this.participants;
    }

    //Getters
    public String getHashValue() {
        return hashValue;
    }

    @NotNull
    public Party getSupervisorAuthority() {
        return supervisorAuthority;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }

    @NotNull
    public Party getFinancialServiceProvider() {
        return financialServiceProvider;
    }

    public LinearPointer<ClaimTemplate> getClaimTemplate() {
        return claimTemplate;
    }

    public List<LinearPointer<SpecificClaim>> getSupportingClaims() {
        return supportingClaims;
    }
}