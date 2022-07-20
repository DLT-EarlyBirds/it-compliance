import { AxiosInstance } from "axios"
import { ClaimTemplateDTO, RegulationDTO } from "../models"

const MODEL = "regulations"

const RegulationService = {
    getAll: async (axios: AxiosInstance) => {
        const { data } = await axios.get(`/${MODEL}/`)
        return data
    },
    getByLinearId: async (axios: AxiosInstance, linearId: string) => {
        const { data } = await axios.get(`/${MODEL}/${linearId}/`)
        return data
    },
    create: async (axios: AxiosInstance, regulation: RegulationDTO) => {
        const { data } = await axios.post(`/${MODEL}/`, regulation)
        return data
    },
    update: async (axios: AxiosInstance, regulation: RegulationDTO) => {
        const { data } = await axios.put(`/${MODEL}/`, regulation)
        return data
    },
    deprecate: async (axios: AxiosInstance, linearId: string) => {
        const { data } = await axios.put(`/${MODEL}/deprecate/${linearId}/`)
        return data
    },
}

export default RegulationService
