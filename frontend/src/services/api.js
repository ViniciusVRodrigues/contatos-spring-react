import axios from 'axios';

const API_URL = '/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Auth API
export const authAPI = {
  register: (data) => api.post('/auth/registro', data),
  login: (data) => api.post('/auth/login', data),
  verificarEmail: (email) => api.get('/auth/verificar-email', { params: { email } }),
};

// Contatos API
export const contatosAPI = {
  list: (params) => api.get('/contatos', { params }),
  get: (id) => api.get(`/contatos/${id}`),
  create: (data) => api.post('/contatos', data),
  update: (id, data) => api.put(`/contatos/${id}`, data),
  delete: (id) => api.delete(`/contatos/${id}`),
  verificarCpf: (cpf) => api.get('/contatos/verificar-cpf', { params: { cpf } }),
};

// Enderecos API
export const enderecosAPI = {
  buscarCep: (cep) => api.get(`/enderecos/cep/${cep}`),
  buscarEnderecos: (uf, cidade, logradouro) =>
    api.get('/enderecos/search', { params: { uf, cidade, logradouro } }),
};

// Conta API
export const contaAPI = {
  deletar: (senha) => api.post('/conta', { senha }),
};

export default api;
