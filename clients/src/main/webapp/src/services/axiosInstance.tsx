import axios, { AxiosInstance } from "axios"

import { NodeEnum } from "enums"

const axiosFSA = axios.create({
    baseURL: process.env.SUPERVISORY_AUTHORITY_NODE,
})

const axiosBrainFinance = axios.create({
    baseURL: process.env.BRAIN_FINANCE_NODE,
})

const axiosCapitalHolding = axios.create({
    baseURL: process.env.CAPITALS_HOLDING_NODE,
})

const axiosAuditor = axios.create({
    baseURL: process.env.AUDITOR_NODE,
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
