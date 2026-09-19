import axios from 'axios'
import { mockApi } from './mock'
import { useAppStore } from './store'

const USE_MOCK = import.meta.env.VITE_USE_MOCK === 'true'
const API_URL = import.meta.env.VITE_API_URL || (import.meta.env.PROD ? '/api/v1' : 'http://localhost:8080/api/v1')

const realApi = axios.create({
  baseURL: API_URL,
  headers: { 'Content-Type': 'application/json' },
})

realApi.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token && !config.headers.Authorization) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

realApi.interceptors.response.use(
  (res) => res,
  (error) => {
    if (error.response?.status === 401) {
      const url = error.config?.url || ''
      if (!url.includes('/auth/') && error.config?.headers?.Authorization === `Bearer ${localStorage.getItem('accessToken')}`) {
        useAppStore.getState().logout()
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

const api = USE_MOCK ? mockApi : realApi

export default api

// Capture the originating token so an in-flight draft cannot be saved under a different account.
export function saveDraft(stepId: number, code: string, token: string) {
  if (USE_MOCK) return mockApi.put(`/steps/${stepId}/draft`, { code })
  return realApi.put(`/steps/${stepId}/draft`, { code }, { headers: { Authorization: `Bearer ${token}` } })
}
