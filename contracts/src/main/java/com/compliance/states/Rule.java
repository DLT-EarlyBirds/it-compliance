package com.compliance.states;

import com.compliance.contracts.RuleContract;
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


/**
 * A rule is part of a regulation and specifies a terms that need to be complied to
 */
@BelongsToContract(RuleContract.class)
public class Rule implements LinearState {

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

    // A list of parties that are involved in the rule.
    private final List<Party> involvedParties;

    private final List<AbstractParty> participants;

    // This is a boolean value that is used to check if the rule is deprecated or not.
    private final boolean isDeprecated;

    // A pointer to the parent regulation that is used to create a reference state in the transaction.
    private final LinearPointer<Regulation> parentRegulation;


    /* Constructor of RegulationDescription */
    @ConstructorForDeserialization
    public Rule(@NotNull UniqueIdentifier linearId, String name, String ruleSpecification, @NotNull Party issuer,
                List<Party> involvedParties, LinearPointer<Regulation> parentRegulation, boolean isDeprecated) {
        this.linearId = linearId;
        this.name = name;
        this.ruleSpecification = ruleSpecification;
        this.issuer = issuer;
        this.parentRegulation = parentRegulation;
        this.isDeprecated = isDeprecated;

        this.participants = new ArrayList<>();
        this.participants.add(issuer);
        this.participants.addAll(involvedParties);
        this.involvedParties = involvedParties;
    }

    // TODO [MOH - 08.07.2022]: Why do we have involvedParties here but not in Regulation?
    public Rule(String name, String ruleSpecification, @NotNull Party issuer,
                List<Party> involvedParties, LinearPointer<Regulation> parentRegulation) {
        this.linearId = new UniqueIdentifier();
        this.name = name;
        this.ruleSpecification = ruleSpecification;
        this.issuer = issuer;
        this.parentRegulation = parentRegulation;
        this.isDeprecated = false;
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

    public boolean getIsDeprecated() {
        return isDeprecated;
    }

}