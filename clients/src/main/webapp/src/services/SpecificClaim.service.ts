import { AxiosInstance } from "axios"
import fileDownload from "js-file-download"
import { RegulationDTO, SpecificClaimDTO } from "../models"

const MODEL = "claims"

const SpecificClaimService = {
    getAll: async (axios: AxiosInstance) => {
        const { data } = await axios.get(`/${MODEL}/`)
        return data
    },
    getAllForOrg: async (axios: AxiosInstance, orgName: string) => {
        const { data } = await axios.get(`/${MODEL}/${orgName}/`)
        return data
    },
    getByLinearId: async (axios: AxiosInstance, linearId: string) => {
        const { data } = await axios.get(`/${MODEL}/${linearId}/`)
        return data
    },
    getForOrgByLinearId: async (axios: AxiosInstance, linearId: string, orgName: string) => {
        const { data } = await axios.get(`/${MODEL}/${orgName}/${linearId}/`)
        return data
    },
    create: async (axios: AxiosInstance, specificClaimDTO: SpecificClaimDTO) => {
        const { data } = await axios.post(`/${MODEL}/`, specificClaimDTO)
        return data
    },
    update: async (axios: AxiosInstance, specificClaimDTO: SpecificClaimDTO) => {
        const { data } = await axios.put(`/${MODEL}/`, specificClaimDTO)
        return data
    },
    // The file property should be form data
    uploadAttachment: async (axios: AxiosInstance, linearId: string, file: string) => {
        const { data } = await axios.put(`/${MODEL}/${linearId}/`, file)
        return data
    },
    downloadAttachment: async (axios: AxiosInstance, linearId: string) => {
        const response = await axios.get(`/${MODEL}/attachment/${linearId}`, { responseType: "blob" })

        fileDownload(response.data, linearId + ".jar")
    },
}

export default SpecificClaimService
