package com.compliance.states;

import com.compliance.contracts.RegulationContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// *********
// * State *
// *********
@BelongsToContract(RegulationContract.class)
public class Regulation implements LinearState {

    // Private variables
    // Linear ID is used to track the evolution of the state over multiple transactions
    @NotNull
    private final UniqueIdentifier linearId;
    // Name of the regulation
    @NotNull
    private final String name;
    // A brief description of the regulation
    @NotNull
    private final String description;
    // The version of the regulation
    @NotNull
    private final String version;
    // The release date of the regulation
    @NotNull
    private final Date releaseDate;
    // The issuer who submits the regulation

    @NotNull
    private final boolean isDeprecated;

    @NotNull
    private final Party issuer;
    private final List<AbstractParty> participants;


    /* Constructor of RegulationDescription */
    @ConstructorForDeserialization
    public Regulation(@NotNull UniqueIdentifier linearId, @NotNull String name, @NotNull String description, @NotNull String version, @NotNull Date releaseDate, @NotNull Party issuer, @NotNull boolean isDeprecated) {
        this.linearId = linearId;
        this.isDeprecated = isDeprecated;
        this.name = name;
        this.description = description;
        this.version = version;
        this.releaseDate = releaseDate;
        this.issuer = issuer;
        this.participants = new ArrayList<>();
        this.participants.add(issuer);
    }

    public Regulation(@NotNull String name, @NotNull String description, @NotNull String version, @NotNull Date releaseDate, @NotNull Party issuer) {
        this.version = version;
        this.releaseDate = releaseDate;
        this.linearId = new UniqueIdentifier();
        this.name = name;
        this.description = description;
        this.issuer = issuer;
        this.isDeprecated = false;
        this.participants = new ArrayList<>();
        this.participants.add(issuer);
    }

    // Getters
    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getDescription() {
        return description;
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

    @NotNull
    public String getVersion() {
        return version;
    }

    @NotNull
    public Date getReleaseDate() {
        return releaseDate;
    }

    @NotNull
    public boolean getIsDeprecated() {
        return isDeprecated;
    }

}