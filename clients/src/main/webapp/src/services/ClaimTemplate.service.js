const MODEL = "claimtemplates";

const ClaimTemplateService = {
  getClaimTemplates: async (axios) => {
    const { data } = await axios.get(`/${MODEL}/`);
    return data;
  },
  createClaimTemplate: async (axios, claimTemplate) => {
    const { data } = await axios.post(`/${MODEL}/`, claimTemplate);
    return data;
  },
  createClaimTemplateSuggestion: async (axios, claimTemplateSuggestion) => {
    const { data } = await axios.post(
      `${MODEL}/suggestions`,
      claimTemplateSuggestion
    );
    return data;
  },
};

export default ClaimTemplateService;
