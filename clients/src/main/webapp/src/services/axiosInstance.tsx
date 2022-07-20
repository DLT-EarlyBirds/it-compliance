import axios, { AxiosInstance } from "axios"

import { NodeEnum } from "enums"

const axiosFSA = axios.create({
    baseURL: 'http://localhost:10050/',
})

const axiosBrainFinance = axios.create({
    baseURL: 'http://localhost:10051/',
})

const axiosCapitalHolding = axios.create({
    baseURL: 'http://localhost:10053/',
})

const axiosAuditor = axios.create({
    baseURL: 'http://localhost:10052/',
})

const getAxiosInstance = (node: NodeEnum) => {
    switch (node) {
        case NodeEnum.SUPERVISORY_AUTHORITY:
            return axiosFSA
        case NodeEnum.BRAIN_FINANCE:
            return axiosBrainFinance
        case NodeEnum.CAPITALS_HOLDING:
            return axiosCapitalHolding
        case NodeEnum.AUDITOR:
            return axiosAuditor
        default:
            return axiosFSA
    }
}

export { getAxiosInstance }
