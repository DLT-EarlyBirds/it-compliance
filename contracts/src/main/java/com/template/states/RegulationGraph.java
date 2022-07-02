package com.template.states;

import com.template.contracts.RegulationGraphContract;
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
@BelongsToContract(RegulationGraphContract.class)
public class RegulationGraph implements ContractState {

    //private variables

    //Graph as a json string i.e: {regulation_1: {rule_1:{claim_template_1:""}, rule_2:...}}
    private String graph;
    private Party supervisoryAuthority;

    private List<AbstractParty> participants;


    /* Constructor of RegulationGraph */
    @ConstructorForDeserialization
    public RegulationGraph(String graph, Party supervisoryAuthority) {
        this.graph = graph;
        this.supervisoryAuthority = supervisoryAuthority;

        this.participants = new ArrayList<AbstractParty>();
        this.participants.add(supervisoryAuthority);
    }

    //getters
    public String getgraph() { return graph; }
    public Party getsupervisoryAuthority() { return supervisoryAuthority; }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return this.participants;
    }
}