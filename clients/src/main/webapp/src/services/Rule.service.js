const MODEL = "rules";

const RuleService = {
  getRules: async (axios) => {
    const { data } = await axios.get(`/${MODEL}`);
    return data;
  },
  createRule: async (axios, rule) => {
    const { data } = await axios.post(`/${MODEL}`, rule);
    return data;
  },
};

export default RuleService;
