package com.template.states;

import com.template.contracts.TemplateContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;

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
    public TemplateState(String hashValue, Party financialServiceProvider, Party supervisorAuthority) {
        this.financialServiceProvider = financialServiceProvider;
        this.supervisorAuthority = supervisorAuthority;
        this.hashValue = hashValue;
        
        this.participants = new ArrayList<AbstractParty>();
        this.participants.add(issuer);
        this.participants.add(holder);

    }

    /* This method will indicate who are the participants and required signers when
     * this state is used in a transaction. */
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(sender,receiver);
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