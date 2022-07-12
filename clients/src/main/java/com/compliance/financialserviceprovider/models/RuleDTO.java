package com.compliance.financialserviceprovider.models;

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "ruleSpecification",
        "parentRegulation"
})
public class RuleDTO implements Serializable
{

    @JsonProperty("linearId")
    private String linearId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("ruleSpecification")
    private String ruleSpecification;
    @JsonProperty("parentRegulation")
    private String parentRegulation;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = -7129508973474007122L;

    /**
     * No args constructor for use in serialization
     *
     */
    public RuleDTO() {
    }

    /**
     *
     * @param linearId
     * @param name
     * @param ruleSpecification
     * @param parentRegulation
     */
    public RuleDTO(String linearId, String name, String ruleSpecification, String parentRegulation) {
        super();
        this.linearId = linearId;
        this.name = name;
        this.ruleSpecification = ruleSpecification;
        this.parentRegulation = parentRegulation;
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

    @JsonProperty("ruleSpecification")
    public String getRuleSpecification() {
        return ruleSpecification;
    }

    @JsonProperty("ruleSpecification")
    public void setRuleSpecification(String ruleSpecification) {
        this.ruleSpecification = ruleSpecification;
    }

    @JsonProperty("parentRegulation")
    public String getParentRegulation() {
        return parentRegulation;
    }

    @JsonProperty("parentRegulation")
    public void setParentRegulation(String parentRegulation) {
        this.parentRegulation = parentRegulation;
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