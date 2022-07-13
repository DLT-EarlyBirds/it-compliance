const MODEL = "regulations";

const RegulationService = {
  getRegulations: async (axios) => {
    const { data } = await axios.get(`/${MODEL}/`);
    return data;
  },
  createRegulation: async (axios, regulation) => {
    const { data } = await axios.post(`/${MODEL}/`, regulation);
    return data;
  },
};

export default RegulationService;
