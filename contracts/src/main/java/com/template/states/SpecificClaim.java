package com.template.states;

import com.template.contracts.SpecificClaimContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

    /* Constructor of your Corda state */
    @ConstructorForDeserialization
    public SpecificClaim(String hashValue, @NotNull Party financialServiceProvider, @NotNull Party supervisorAuthority) {
        this.linearId = new UniqueIdentifier();
        this.financialServiceProvider = financialServiceProvider;
        this.supervisorAuthority = supervisorAuthority;
        this.hashValue = hashValue;
        this.participants = new ArrayList<>();
        this.participants.add(financialServiceProvider);
        this.participants.add(supervisorAuthority);
    }

    public SpecificClaim(@NotNull UniqueIdentifier linearId, String hashValue, @NotNull Party financialServiceProvider, @NotNull Party supervisorAuthority) {
        this.linearId = linearId;
        this.hashValue = hashValue;
        this.financialServiceProvider = financialServiceProvider;
        this.supervisorAuthority = supervisorAuthority;
        this.participants = new ArrayList<>();
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
    public Party getFinancialServiceProiver() {
        return financialServiceProvider;
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
}