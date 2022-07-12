package com.compliance.supervisoryauthority.models;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "description",
        "version"
})
public class RegulationDTO implements Serializable
{
    @JsonProperty("linearId")
    private String linearId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("version")
    private String version;
    @JsonProperty("releaseDate")
    private Date releaseDate;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = -7129508973474007122L;

    /**
     * No args constructor for use in serialization
     *
     */
    public RegulationDTO() {
    }

    /**
     * @param linearId
     * @param name
     * @param description
     * @param version
     * @param releaseDate
     */
    public RegulationDTO(String linearId, String name, String description, String version, Date releaseDate) {
        super();
        this.linearId = linearId;
        this.name = name;
        this.description = description;
        this.version = version;
        this.releaseDate = releaseDate;
    }

    @JsonProperty("linearId")
    public String getLinearId() {
        return linearId;
    }

    @JsonProperty("linearId")
    public void setLinearId(String linearId) {
        this.linearId = linearId;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty("releaseDate")
    public Date getReleaseDate() {
        return releaseDate;
    }

    @JsonProperty("releaseDate")
    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}