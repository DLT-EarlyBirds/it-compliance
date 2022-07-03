package com.template.states;

import com.template.contracts.ClaimContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// *********
// * State *
// *********
@BelongsToContract(ClaimContract.class)
public class ClaimState implements ContractState {

    //private variables
    private String hashValue;
    private Party financialServiceProvider;
    private Party supervisorAuthority;
    private List<AbstractParty> participants;

    /* Constructor of your Corda state */
    @ConstructorForDeserialization
    public ClaimState(String hashValue, Party financialServiceProvider, Party supervisorAuthority) {
        this.financialServiceProvider = financialServiceProvider;
        this.supervisorAuthority = supervisorAuthority;
        this.hashValue = hashValue;
        
        this.participants = new ArrayList<AbstractParty>();
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

    public Party getFinancialServiceProiver() {
        return financialServiceProvider;
    }

    public Party getSupervisorAuthority() {
        return supervisorAuthority;
    }
}