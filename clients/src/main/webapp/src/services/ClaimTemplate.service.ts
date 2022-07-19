import {AxiosInstance} from "axios";
import {ClaimTemplateDTO, ClaimTemplateSuggestionDTO} from "../models";

const MODEL = "claimtemplates";

const ClaimTemplateService = {
  getAll: async (axios: AxiosInstance) => {
    const { data } = await axios.get(`/${MODEL}/`);
    return data;
  },
  getByLinearId: async (axios: AxiosInstance, linearId: string) => {
    const { data } = await axios.get(`/${MODEL}/${linearId}`);
    return data;
  },
  update: async (axios: AxiosInstance, claimTemplate: ClaimTemplateDTO) => {
    const { data } = await axios.put(`/${MODEL}/`, claimTemplate);
    return data;
  },
  create: async (axios: AxiosInstance, claimTemplate: ClaimTemplateDTO) => {
    const { data } = await axios.post(`/${MODEL}/`, claimTemplate);
    return data;
  },
  createSuggestion: async (axios: AxiosInstance, claimTemplateSuggestion: ClaimTemplateSuggestionDTO) => {
    const { data } = await axios.post(
      `${MODEL}/suggestions/`,
      claimTemplateSuggestion
    );
    return data;
  },
  getSuggestionByLinearId: async (axios: AxiosInstance, linearId: string) => {
    const { data } = await axios.get(`/${MODEL}/suggestions/${linearId}/`);
    return data;
  },
  getSuggestions: async (axios: AxiosInstance) => {
    const { data } = await axios.get(`/${MODEL}/suggestions/`);
    return data;
  },
  acceptSuggestion: async (axios: AxiosInstance, linearId: string) => {
    const { data } = await axios.post(
        `${MODEL}/suggestions/${linearId}/`
    );
    return data;
  },
  rejectSuggestion: async (axios: AxiosInstance, linearId: string) => {
    const { data } = await axios.delete(
        `${MODEL}/suggestions/${linearId}/`
    );
    return data;
  },


};

export default ClaimTemplateService;
