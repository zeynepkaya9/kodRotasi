import { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import api from '../../lib/api'
import { selectResumeStep, useAppStore } from '../../lib/store'

export default function SessionGate({ children }: { children: React.ReactNode }) {
  const { accessToken, sessionReady } = useAppStore()
  const [error, setError] = useState('')
  const [retry, setRetry] = useState(0)
  useEffect(() => {
    if (!accessToken || sessionReady) return
    let cancelled = false
    setError('')
    const load = async () => {
      try {
        const [path, tasks, progress] = await Promise.all([
          api.get('/learning-path').catch(error => {
            if (error.response?.status === 404) return { data: null }
            throw error
          }),
          api.get('/my-tasks'), api.get('/progress'),
        ])
        if (cancelled || useAppStore.getState().accessToken !== accessToken) return
        const store = useAppStore.getState()
        const preferred = store.activeStepId
        store.setLearningPath(path.data)
        store.setActiveTasks(tasks.data)
        store.setActiveStepId(selectResumeStep(tasks.data, preferred))
        store.setProgress(progress.data)
        store.setSessionReady(true)
      } catch {
        if (!cancelled) setError('İlerlemen yüklenemedi. Bağlantını kontrol edip tekrar dene; kayıtların sıfırlanmadı.')
      }
    }
    void load()
    return () => { cancelled = true }
  }, [accessToken, sessionReady, retry])
  if (!accessToken) return <Navigate to="/login" replace />
  if (!sessionReady) return <div className="max-w-xl mx-auto py-20 text-center">
    <p role={error ? 'alert' : 'status'}>{error || 'Kaldığın yer yükleniyor…'}</p>
    {error && <button className="btn-primary mt-4" onClick={() => setRetry(value => value + 1)}>Tekrar dene</button>}
  </div>
  return <>{children}</>
}
