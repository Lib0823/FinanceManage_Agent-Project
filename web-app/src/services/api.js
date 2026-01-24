import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// Auth API
export const authApi = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
  logout: () => api.post('/auth/logout'),
  resetPassword: (data) => api.post('/auth/reset-password', data),
  verifyPhone: (phone) => api.post('/auth/verify-phone', { phone }),
  checkDuplicate: (id) => api.get(`/auth/check-duplicate/${id}`)
}

// User API
export const userApi = {
  getProfile: () => api.get('/user/profile'),
  updateProfile: (data) => api.put('/user/profile', data),
  getSettings: () => api.get('/user/settings'),
  updateSettings: (data) => api.put('/user/settings', data)
}

// Asset API
export const assetApi = {
  getSummary: () => api.get('/assets/summary'),
  getDetail: (type) => api.get(`/assets/detail/${type}`),
  getStocks: () => api.get('/assets/stocks'),
  getBonds: () => api.get('/assets/bonds'),
  getCoins: () => api.get('/assets/coins'),
  getCash: () => api.get('/assets/cash')
}

// Stock API
export const stockApi = {
  getList: (params) => api.get('/stocks', { params }),
  getDetail: (symbol) => api.get(`/stocks/${symbol}`),
  getPrice: (symbol) => api.get(`/stocks/${symbol}/price`),
  getChart: (symbol, period) => api.get(`/stocks/${symbol}/chart`, { params: { period } }),
  search: (query) => api.get('/stocks/search', { params: { q: query } })
}

// Company API
export const companyApi = {
  getInfo: (symbol) => api.get(`/companies/${symbol}`),
  getFinancials: (symbol) => api.get(`/companies/${symbol}/financials`),
  getDisclosures: (symbol) => api.get(`/companies/${symbol}/disclosures`),
  getAiAnalysis: (symbol) => api.get(`/companies/${symbol}/ai-analysis`)
}

// Trading API
export const tradingApi = {
  getOrders: () => api.get('/trading/orders'),
  getPendingOrders: () => api.get('/trading/orders/pending'),
  getHistory: (params) => api.get('/trading/history', { params }),
  placeOrder: (order) => api.post('/trading/orders', order),
  cancelOrder: (orderId) => api.delete(`/trading/orders/${orderId}`)
}

// News API
export const newsApi = {
  getList: (params) => api.get('/news', { params }),
  getDetail: (id) => api.get(`/news/${id}`),
  getByDate: (date) => api.get('/news/by-date', { params: { date } })
}

// Market API
export const marketApi = {
  getIndices: () => api.get('/market/indices'),
  getExchangeRates: () => api.get('/market/exchange-rates'),
  getTopNews: () => api.get('/market/top-news'),
  getAiRecommendations: () => api.get('/market/ai-recommendations')
}

// AI Bot API
export const botApi = {
  getStatus: () => api.get('/bot/status'),
  getAnalysis: (symbol) => api.get(`/bot/analysis/${symbol}`),
  toggleBot: (enabled) => api.post('/bot/toggle', { enabled }),
  updateSettings: (settings) => api.put('/bot/settings', settings)
}

export default api
