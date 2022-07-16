import axios from "axios";

const axiosFSA = axios.create({
  baseURL: "http://localhost:10050",
});

const axiosBrainFinance = axios.create({
  baseURL: "http://localhost:10051",
});

const axiosCapitalHolding = axios.create({
  baseURL: "",
});

const axiosNotary = axios.create({
  baseURL: "",
});

const axiosAuditor = axios.create({
  baseURL: "",
});

const getAxiosInstance = (node) => {
  if (node === "Supervisory Authority") {
    return axiosFSA;
  } else if (node === "Brain Finance") {
    return axiosBrainFinance;
  } else if (node === "Capitals Holding") {
    return axiosCapitalHolding;
  } else if (node === "Notary") {
    return axiosNotary;
  } else if (node === "Auditor") {
    return axiosAuditor;
  } else {
    return axiosFSA;
  }
};

export { getAxiosInstance };
