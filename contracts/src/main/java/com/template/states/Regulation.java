package com.template.states;

import com.template.contracts.RegulationContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

// *********
// * State *
// *********
@BelongsToContract(RegulationContract.class)
public class Regulation implements ContractState {

    //private variables

    //Description as a string
    private String Description;
    private Party supervisoryAuthority;

    private List<AbstractParty> participants;


    /* Constructor of RegulationDescription */
    @ConstructorForDeserialization
    public Regulation(String Description, Party supervisoryAuthority) {
        this.Description = Description;
        this.supervisoryAuthority = supervisoryAuthority;

        this.participants = new ArrayList<AbstractParty>();
        this.participants.add(supervisoryAuthority);
    }

    //getters
    public String getDescription() { return Description; }
    public Party getSupervisoryAuthority() { return supervisoryAuthority; }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return this.participants;
    }
}