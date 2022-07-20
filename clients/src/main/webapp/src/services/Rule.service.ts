import { AxiosInstance } from "axios"
import { ClaimTemplateDTO, RuleDTO } from "../models"

const MODEL = "rules"

const RuleService = {
    getAll: async (axios: AxiosInstance) => {
        const { data } = await axios.get(`/${MODEL}/`)
        return data
    },
    getByLinearId: async (axios: AxiosInstance, linearId: string) => {
        const { data } = await axios.get(`/${MODEL}/${linearId}/`)
        return data
    },
    create: async (axios: AxiosInstance, rule: RuleDTO) => {
        const { data } = await axios.post(`/${MODEL}/`, rule)
        return data
    },
    update: async (axios: AxiosInstance, rule: RuleDTO) => {
        const { data } = await axios.put(`/${MODEL}/`, rule)
        return data
    },
    deprecate: async (axios: AxiosInstance, linearId: string) => {
        const { data } = await axios.put(`/${MODEL}/deprecate/${linearId}/`)
        return data
    },
}

export default RuleService
