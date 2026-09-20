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
      setError(err.response?.data?.message || 'Giriş başarısız')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center px-4 py-8">
      <div className="w-full max-w-5xl grid items-stretch gap-6 lg:grid-cols-[1.15fr_0.85fr]">
        <section className="relative hidden overflow-hidden rounded-2xl border border-primary-500/20 bg-gradient-to-br from-dark-800 via-dark-900 to-primary-950 p-8 lg:flex lg:flex-col">
          <div className="absolute -right-20 -top-20 h-64 w-64 rounded-full bg-primary-500/15 blur-3xl" />
          <div className="absolute -bottom-24 -left-16 h-56 w-56 rounded-full bg-secondary-500/10 blur-3xl" />
          <div className="relative">
            <div className="mb-8 flex items-center gap-3">
              <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-primary-500 text-lg font-bold text-white shadow-lg shadow-primary-500/25">&lt;/&gt;</div>
              <div><p className="font-semibold text-white">KodRotası</p><p className="text-xs text-primary-200">Java &amp; Spring Boot öğrenme yolu</p></div>
            </div>
            <p className="mb-3 text-sm font-semibold uppercase tracking-[0.18em] text-primary-300">Kod yazarak öğren</p>
            <h2 className="max-w-lg text-3xl font-bold leading-tight text-white">Gerçek projeler geliştirirken Java ve Spring Boot’u anlayarak öğren.</h2>
            <p className="mt-4 max-w-lg leading-7 text-gray-300">KodRotası seviyeni belirler; sana uygun görevleri, öğretici notları ve kademeli ipuçlarını sunar.</p>
            <div className="mt-7 grid gap-3 sm:grid-cols-3 lg:grid-cols-1">
              {[
                ['1', 'Seviyeni belirle', 'Kısa quiz ile sana uygun başlangıç noktasını bul.'],
                ['2', 'Projeni geliştir', 'Banka, e-ticaret ve kütüphane gibi uygulamalar yaz.'],
                ['3', 'Takıldığında öğren', 'İpuçları ve notlarla çözümü kendin keşfet.'],
              ].map(([step, title, description]) => (
                <div key={step} className="flex gap-3 rounded-xl border border-white/10 bg-white/5 p-3">
                  <span className="flex h-7 w-7 shrink-0 items-center justify-center rounded-full bg-primary-500/20 text-sm font-bold text-primary-200">{step}</span>
                  <div><p className="text-sm font-semibold text-white">{title}</p><p className="mt-0.5 text-xs leading-5 text-gray-400">{description}</p></div>
                </div>
              ))}
            </div>
          </div>
          <div className="relative mt-auto rounded-xl border border-primary-300/15 bg-dark-950/60 p-4 font-mono text-xs shadow-xl">
            <div className="mb-3 flex gap-1.5"><span className="h-2.5 w-2.5 rounded-full bg-danger-500" /><span className="h-2.5 w-2.5 rounded-full bg-yellow-400" /><span className="h-2.5 w-2.5 rounded-full bg-secondary-400" /></div>
            <p className="text-primary-300">@Service</p><p className="mt-1 text-gray-300">class <span className="text-secondary-300">LearningJourney</span> {'{'}</p><p className="pl-4 text-gray-400">progress.learnByBuilding();</p><p className="text-gray-300">{'}'}</p>
          </div>
        </section>
        <div className="card w-full max-w-md self-center justify-self-center">
          <div className="mb-6 rounded-xl border border-primary-500/20 bg-primary-500/5 p-3 lg:hidden"><p className="text-sm font-semibold text-primary-300">KodRotası ile öğren</p><p className="mt-1 text-xs leading-5 text-gray-400">Seviyene uygun görevler, öğretici notlar ve ipuçlarıyla gerçek projeler geliştir.</p></div>
          <h1 className="text-2xl font-bold text-center mb-2">Giriş Yap</h1>
          <p className="text-gray-400 text-center mb-8">Java ve Spring Boot yolculuğuna devam et</p>

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
            <label className="block text-sm font-medium text-gray-300 mb-1.5">Şifre</label>
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)}
              className="input-field" placeholder="En az 6 karakter" required />
          </div>
          <button type="submit" disabled={loading} className="btn-primary w-full disabled:opacity-50">
            {loading ? 'Giriş yapılıyor...' : 'Giriş Yap'}
          </button>
        </form>

        <p className="text-center text-gray-400 text-sm mt-6">
          Hesabın yok mu? <Link to="/register" className="text-primary-400 hover:text-primary-300">Kayıt ol</Link>
        </p>
        </div>
      </div>
    </div>
  )
}
