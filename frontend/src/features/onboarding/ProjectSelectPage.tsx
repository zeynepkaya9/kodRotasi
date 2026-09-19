import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../../lib/api'
import { selectResumeStep, useAppStore } from '../../lib/store'

interface ProjectItem {
  id: number
  code: string
  title: string
  description: string
  minLevel: string
  taskCount: number
}

const icons: Record<string, string> = {
  BANK: '\uD83C\uDFE6',
  ECOMMERCE: '\uD83D\uDED2',
  LIBRARY: '\uD83D\uDCDA',
  TASK_TRACKER: '\u2705',
  RESERVATION: '\uD83D\uDCC5',
}

export default function ProjectSelectPage() {
  const [projects, setProjects] = useState<ProjectItem[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const { setActiveTasks, setActiveStepId } = useAppStore()
  const navigate = useNavigate()

  useEffect(() => {
    api.get('/projects').then(({ data }) => {
      setProjects(data)
    }).catch(() => setError('Projeler yüklenemedi. Sayfayı yenileyerek tekrar dene.')).finally(() => setLoading(false))
  }, [])

  const startProject = async (code: string) => {
    setLoading(true)
    setError('')
    try {
      const { data } = await api.post(`/projects/${code}/start`)
      const preferred = useAppStore.getState().activeStepId
      setActiveTasks(data)
      setActiveStepId(selectResumeStep(data, preferred))
      navigate('/workspace')
    } catch (error: any) {
      setError(error.response?.data?.message || 'Proje açılamadı. Tekrar dene.')
    } finally { setLoading(false) }
  }

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-gray-400 text-lg">Projeler yukleniyor...</div>
      </div>
    )
  }

  return (
    <div className="min-h-screen px-4 py-12">
      <div className="max-w-5xl mx-auto">
        {error && <p role="alert" className="text-red-300 mb-4">{error}</p>}
        <h1 className="text-3xl font-bold text-center mb-3">Proje Sec</h1>
        <p className="text-gray-400 text-center mb-12">Gercek bir proje gelistirerek ogren</p>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {projects.map((p) => (
            <div key={p.id} className="card hover:border-primary-500 transition-colors cursor-pointer group"
                 onClick={() => startProject(p.code)}>
              <div className="text-4xl mb-4">{icons[p.code] || '\uD83D\uDCBB'}</div>
              <h3 className="text-xl font-semibold mb-2 group-hover:text-primary-400 transition-colors">
                {p.title}
              </h3>
              <p className="text-gray-400 text-sm mb-4 line-clamp-3">{p.description}</p>
              <div className="flex justify-between text-sm">
                <span className="text-gray-500">{p.taskCount} gorev</span>
                <span className={`font-medium ${
                  p.minLevel === 'BEGINNER' ? 'text-success-500' :
                  p.minLevel === 'INTERMEDIATE' ? 'text-warning-500' : 'text-danger-500'
                }`}>
                  {p.minLevel === 'BEGINNER' ? 'Baslangic' : p.minLevel === 'INTERMEDIATE' ? 'Orta' : 'Ileri'}
                </span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
