package com.template.states;

import com.template.contracts.RuleContract;
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

// *********
// * State *
// *********
@BelongsToContract(RuleContract.class)
public class Rule implements LinearState {

    // Private variables
    // Linear ID is used to track the evolution of the state over multiple transactions
    @NotNull
    private final UniqueIdentifier linearId;
    // Name of the rule
    private final String name;
    // The specification that details what need to be fulfilled
    private final String ruleSpecification;
    // The issuer who submits the rule, usually the supervisory authority or part of the regulatory body
    @NotNull
    private final Party issuer;
    private final List<Party> involvedParties;
    private final List<AbstractParty> participants;

    private final LinearPointer<Regulation> parentRegulation;


    /* Constructor of RegulationDescription */
    @ConstructorForDeserialization
    public Rule(@NotNull UniqueIdentifier linearId, String name, String ruleSpecification, @NotNull Party issuer, List<Party> involvedParties, LinearPointer<Regulation> parentRegulation) {
        this.linearId = linearId;
        this.name = name;
        this.ruleSpecification = ruleSpecification;
        this.issuer = issuer;
        this.parentRegulation = parentRegulation;

        this.participants = new ArrayList<>();
        this.participants.add(issuer);
        this.participants.addAll(involvedParties);
        this.involvedParties = involvedParties;
    }


    // Getters
    public String getName() {
        return name;
    }

    public String getRuleSpecification() {
        return ruleSpecification;
    }

    @NotNull
    public Party getIssuer() {
        return issuer;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return this.participants;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }

    public LinearPointer<Regulation> getParentRegulation() {
        return parentRegulation;
    }
    public List<Party> getInvolvedParties() {
        return involvedParties;
    }

}