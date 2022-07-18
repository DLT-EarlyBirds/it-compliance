package com.compliance.financialserviceprovider.models;

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "claimSpecification",
        "claimTemplateLinearId"
})
public class SpecificClaimDTO implements Serializable
{
    @JsonProperty("linearId")
    private String linearId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("claimSpecification")
    private String claimSpecification;
    @JsonProperty("claimTemplateLinearId")
    private String claimTemplateLinearId;
    @JsonProperty("supportingClaimIds")
    private String[] supportingClaimIds;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<>();
    private final static long serialVersionUID = -7129508973474007122L;

    /**
     * No args constructor for use in serialization
     *
     */
    public SpecificClaimDTO() {
    }

    /**
     *  @param linearId
     * @param name
     * @param claimSpecification
     * @param claimTemplateLinearId
     * @param supportingClaimIds
     */
    public SpecificClaimDTO(String linearId, String name, String claimSpecification, String claimTemplateLinearId, String[] supportingClaimIds) {
        super();
        this.linearId = linearId;
        this.name = name;
        this.claimSpecification = claimSpecification;
        this.claimTemplateLinearId = claimTemplateLinearId;
        this.supportingClaimIds = supportingClaimIds;
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

    @JsonProperty("claimSpecification")
    public String getClaimSpecification() {
        return claimSpecification;
    }

    @JsonProperty("claimSpecification")
    public void setClaimSpecification(String claimSpecification) {
        this.claimSpecification = claimSpecification;
    }

    @JsonProperty("claimTemplateLinearId")
    public String getClaimTemplateLinearId() {
        return claimTemplateLinearId;
    }

    @JsonProperty("claimTemplateLinearId")
    public void setClaimTemplateLinearId(String claimTemplateLinearId) {
        this.claimTemplateLinearId = claimTemplateLinearId;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @JsonProperty("supportingClaimIds")
    public String[] getSupportingClaimIds() {
        return supportingClaimIds;
    }

    @JsonProperty("supportingClaimIds")
    public void setSupportingClaimIds(String[] supportingClaimIds) {
        this.supportingClaimIds = supportingClaimIds;
    }
}