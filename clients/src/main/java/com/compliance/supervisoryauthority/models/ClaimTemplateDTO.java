package com.compliance.supervisoryauthority.models;

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "templateDescription",
        "rule"
})
public class ClaimTemplateDTO implements Serializable
{

    @JsonProperty("linearId")
    private String linearId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("templateDescription")
    private String templateDescription;
    @JsonProperty("rule")
    private String rule;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<>();
    private final static long serialVersionUID = -7129508973474007122L;

    /**
     * No args constructor for use in serialization
     *
     */
    public ClaimTemplateDTO() {
    }

    /**
     *
     * @param linearId
     * @param name
     * @param templateDescription
     * @param rule
     */
    public ClaimTemplateDTO(String linearId, String name, String templateDescription, String rule) {
        super();
        this.linearId = linearId;
        this.name = name;
        this.templateDescription = templateDescription;
        this.rule = rule;
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

    @JsonProperty("templateDescription")
    public String getTemplateDescription() {
        return templateDescription;
    }

    @JsonProperty("templateDescription")
    public void setTemplateDescription(String templateDescription) {
        this.templateDescription = templateDescription;
    }

    @JsonProperty("rule")
    public String getRule() {
        return rule;
    }

    @JsonProperty("rule")
    public void setRule(String rule) {
        this.rule = rule;
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