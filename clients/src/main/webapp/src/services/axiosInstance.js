import axios from "axios";

const axiosFSA = axios.create({
  baseURL: "",
});

const axiosFSP = axios.create({
  baseURL: "",
});

export { axiosFSA, axiosFSP };
