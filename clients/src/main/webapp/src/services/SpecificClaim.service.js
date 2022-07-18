const MODEL = "claims";

const RuleService = {
  getSpecificClaims: async (axios) => {
    const { data } = await axios.get(`/${MODEL}/`);
    return data;
  },
  createSpecificClaim: async (axios, rule) => {
    const { data } = await axios.post(`/${MODEL}/`, rule);
    return data;
  },
};

export default RuleService;
