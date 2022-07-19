import { AxiosInstance } from "axios"
import { RegulationDTO, SpecificClaimDTO } from "../models"

const MODEL = "network"

const NetworkService = {
    servertime: async (axios: AxiosInstance) => {
        const { data } = await axios.get(`/${MODEL}/servertime/`)
        return data
    },
    addresses: async (axios: AxiosInstance) => {
        const { data } = await axios.get(`/${MODEL}/addresses/`)
        return data
    },
    platformversion: async (axios: AxiosInstance) => {
        const { data } = await axios.get(`/${MODEL}/platformversion/`)
        return data
    },
    identities: async (axios: AxiosInstance) => {
        const { data } = await axios.get(`/${MODEL}/identities/`)
        return data
    },
    peers: async (axios: AxiosInstance) => {
        const { data } = await axios.get(`/${MODEL}/peers/`)
        return data
    },
    notaries: async (axios: AxiosInstance) => {
        const { data } = await axios.get(`/${MODEL}/notaries/`)
        return data
    },
    flows: async (axios: AxiosInstance) => {
        const { data } = await axios.get(`/${MODEL}/flows/`)
        return data
    },
    bootstrapGraph: async (axios: AxiosInstance) => {
        const { data } = await axios.post(`/${MODEL}/bootstrapGraph/`)
        return data
    },
}

export default NetworkService
