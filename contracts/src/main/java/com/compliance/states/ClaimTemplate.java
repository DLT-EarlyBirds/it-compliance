package com.compliance.states;

import com.compliance.contracts.ClaimTemplateContract;
import com.compliance.contracts.RegulationContract;
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
 * A claim template is a description for how to comply to a rule that can be used to create a claim
 */
@BelongsToContract(ClaimTemplateContract.class)
public class ClaimTemplate implements LinearState {


    // Linear ID is used to track the evolution of the state over multiple transactions
    @NotNull
    private final UniqueIdentifier linearId;
    // Name of the claim template
    private final String name;

    // A brief description of the regulation
    private final String templateDescription;

    // The issuer who submits the claim template
    private final Party issuer;


    // A list of parties that are involved in the claim template.
    private final List<Party> involvedParties;

    // Participants that are used to sign and process the transaction
    private final List<AbstractParty> participants;

    // A reference to the rule that is fulfilled by a claim implementing this template.
    private final LinearPointer<Rule> rule;


    /* Constructor of RegulationDescription */
    @ConstructorForDeserialization
    public ClaimTemplate(@NotNull UniqueIdentifier linearId, String name, String templateDescription, Party issuer, List<Party> involvedParties, LinearPointer<Rule> rule) {
        this.linearId = linearId;
        this.name = name;
        this.templateDescription = templateDescription;
        this.issuer = issuer;
        this.involvedParties = involvedParties;
        this.rule = rule;

        this.participants = new ArrayList<>();
        this.participants.add(issuer);
        this.participants.addAll(involvedParties);
    }

    public ClaimTemplate(String name, String templateDescription, Party issuer, List<Party> involvedParties, LinearPointer<Rule> rule) {
        this.linearId = new UniqueIdentifier();
        this.name = name;
        this.templateDescription = templateDescription;
        this.issuer = issuer;
        this.involvedParties = involvedParties;
        this.rule = rule;

        this.participants = new ArrayList<>();
        this.participants.add(issuer);
        this.participants.addAll(involvedParties);
    }

    // Getters
    @Override
    @NotNull
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    public String getName() {
        return name;
    }

    public String getTemplateDescription() {
        return templateDescription;
    }

    public Party getIssuer() {
        return issuer;
    }

    public List<Party> getInvolvedParties() {
        return involvedParties;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return participants;
    }

    public LinearPointer<Rule> getRule() {
        return rule;
    }
}
