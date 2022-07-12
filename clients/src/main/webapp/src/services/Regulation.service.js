import { axiosFSA } from "./axiosInstance";

const MODEL = "regulations";

const RegulationService = {
  getRegulations: async () => {
    const { data } = await axiosFSA.get(`/${MODEL}`);
    return data;
  },
};

export default RegulationService;
