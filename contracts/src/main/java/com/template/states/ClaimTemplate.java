package com.template.states;

import com.template.contracts.RegulationContract;
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
@BelongsToContract(RegulationContract.class)
public class ClaimTemplate implements LinearState {

    // Private variables

    // Linear ID is used to track the evolution of the state over multiple transactions
    @NotNull
    private final UniqueIdentifier linearId;
    // Name of the claim template
    private final String name;

    // A brief description of the regulation
    private final String templateDescription;

    // The issuer who submits the claim template
    private final Party issuer;
    // The party that approves the submission of the claim template, usually the supervisory authority
    private final Party approver;
    private final List<AbstractParty> participants;

    // A reference to the rule that is fulfilled by a claim implementing this template.
    private final LinearPointer<Rule> rule;


    /* Constructor of RegulationDescription */
    @ConstructorForDeserialization
    public ClaimTemplate(@NotNull UniqueIdentifier linearId, String name, String description, Party issuer, Party approver, UniqueIdentifier ruleLinearId) {
        this.linearId = linearId;
        this.name = name;
        this.templateDescription = description;
        this.issuer = issuer;
        this.approver = approver;
        this.rule = new LinearPointer<>(ruleLinearId, Rule.class);

        this.participants = new ArrayList<AbstractParty>();
        this.participants.add(issuer);
        this.participants.add(approver);
    }

    public ClaimTemplate(String name, String templateDescription, Party issuer, Party approver, UniqueIdentifier ruleLinearId) {
        this.linearId = new UniqueIdentifier();
        this.name = name;
        this.templateDescription = templateDescription;
        this.issuer = issuer;
        this.approver = approver;
        this.rule = new LinearPointer<>(ruleLinearId, Rule.class);

        this.participants = new ArrayList<>();
        this.participants.add(issuer);
        this.participants.add(approver);
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getTemplateDescription() {
        return templateDescription;
    }

    public Party getIssuer() {
        return issuer;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return this.participants;
    }

    @Override
    @NotNull
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    public Party getApprover() {
        return approver;
    }

    public LinearPointer<Rule> getRule() {
        return rule;
    }
}
