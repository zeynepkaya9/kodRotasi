import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import api from '../../lib/api'
import { useAppStore } from '../../lib/store'

export default function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { setUser, setAccessToken } = useAppStore()
  const navigate = useNavigate()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const { data } = await api.post('/auth/login', { email, password })
      setAccessToken(data.accessToken)
      localStorage.setItem('refreshToken', data.refreshToken)
      setUser({ email: data.email, displayName: data.displayName })
      navigate('/dashboard')
    } catch (err: any) {
      setError(err.response?.data?.message || 'Giris basarisiz')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center px-4">
      <div className="card max-w-md w-full">
        <h1 className="text-2xl font-bold text-center mb-2">Giris Yap</h1>
        <p className="text-gray-400 text-center mb-8">Java Spring yolculuguna devam et</p>

        {error && (
          <div className="bg-danger-50/10 border border-danger-500/30 text-danger-500 px-4 py-3 rounded-lg mb-6 text-sm">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-1.5">Email</label>
            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)}
              className="input-field" placeholder="ornek@email.com" required />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-1.5">Sifre</label>
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)}
              className="input-field" placeholder="En az 6 karakter" required />
          </div>
          <button type="submit" disabled={loading} className="btn-primary w-full disabled:opacity-50">
            {loading ? 'Giris yapiliyor...' : 'Giris Yap'}
          </button>
        </form>

        <p className="text-center text-gray-400 text-sm mt-6">
          Hesabin yok mu? <Link to="/register" className="text-primary-400 hover:text-primary-300">Kayit ol</Link>
        </p>
      </div>
    </div>
  )
}
