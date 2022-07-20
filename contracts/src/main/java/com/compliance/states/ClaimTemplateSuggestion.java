package com.compliance.states;

import com.compliance.contracts.ClaimTemplateSuggestionContract;
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
 * A ClaimTemplateSuggestion is a suggestion for a claim template that is submitted by an issuer and approved by a
 * supervisory authority
 */
@BelongsToContract(ClaimTemplateSuggestionContract.class)
public class ClaimTemplateSuggestion implements LinearState {


    // Linear ID is used to track the evolution of the state over multiple transactions
    @NotNull
    private final UniqueIdentifier linearId;
    // Name of the claim template
    private final String name;

    // A brief description of the regulation
    private final String templateDescription;

    // The issuer who submits the claim template
    private final Party issuer;

    // The supervisory authority that is responsible for the regulation.
    private final Party supervisoryAuthority;


    private final List<AbstractParty> participants;

    // A reference to the rule that is fulfilled by a claim implementing this template.
    private final LinearPointer<Rule> rule;

    /* Constructor of RegulationDescription */
    @ConstructorForDeserialization
    public ClaimTemplateSuggestion(@NotNull UniqueIdentifier linearId, String name, String templateDescription, Party issuer, Party supervisoryAuthority, LinearPointer<Rule> rule) {
        this.linearId = linearId;
        this.name = name;
        this.templateDescription = templateDescription;
        this.issuer = issuer;
        this.rule = rule;
        this.supervisoryAuthority = supervisoryAuthority;

        this.participants = new ArrayList<>();
        this.participants.add(issuer);
        this.participants.add(supervisoryAuthority);
    }

    public ClaimTemplateSuggestion(String name, String templateDescription, Party issuer, Party supervisoryAuthority, LinearPointer<Rule> rule) {
        this.linearId = new UniqueIdentifier();
        this.name = name;
        this.templateDescription = templateDescription;
        this.issuer = issuer;
        this.rule = rule;
        this.supervisoryAuthority = supervisoryAuthority;

        this.participants = new ArrayList<>();
        this.participants.add(issuer);
        this.participants.add(supervisoryAuthority);
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

    public Party getSupervisoryAuthority() {
        return supervisoryAuthority;
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
