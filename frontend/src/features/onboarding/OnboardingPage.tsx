import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../../lib/api'
import { useAppStore } from '../../lib/store'

interface Question {
  id: number
  question: string
  options: string[]
  topic: string
}

export default function OnboardingPage() {
  const [questions, setQuestions] = useState<Question[]>([])
  const [answers, setAnswers] = useState<number[]>([])
  const [currentQ, setCurrentQ] = useState(0)
  const [result, setResult] = useState<any>(null)
  const [loading, setLoading] = useState(true)
  const { setLearningPath } = useAppStore()
  const navigate = useNavigate()

  useEffect(() => {
    api.get('/assessment/questions').then(({ data }) => {
      setQuestions(data)
      setAnswers(new Array(data.length).fill(-1))
      setLoading(false)
    }).catch(err => {
      console.error('Failed to load questions:', err)
      setLoading(false)
    })
  }, [])

  const selectAnswer = (index: number) => {
    const updated = [...answers]
    updated[currentQ] = index
    setAnswers(updated)
  }

  const next = () => {
    if (currentQ < questions.length - 1) setCurrentQ(currentQ + 1)
  }

  const prev = () => {
    if (currentQ > 0) setCurrentQ(currentQ - 1)
  }

  const submit = async () => {
    setLoading(true)
    try {
      const response = await api.post('/assessment/submit', { answers })
      const data = response.data
      setResult(data)
      setLearningPath({
        level: data.level,
        recommendedArchitecture: data.recommendedArchitecture,
        chosenArchitecture: data.recommendedArchitecture,
      })
    } catch (err) {
      console.error('Assessment submit error:', err)
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-gray-400 text-lg">Yukleniyor...</div>
      </div>
    )
  }

  if (result) {
    const levelLabels: Record<string, string> = {
      BEGINNER: 'Baslangic',
      INTERMEDIATE: 'Orta',
      ADVANCED: 'Ileri'
    }
    const levelColors: Record<string, string> = {
      BEGINNER: 'text-success-500',
      INTERMEDIATE: 'text-warning-500',
      ADVANCED: 'text-danger-500'
    }
    const archLabels: Record<string, string> = {
      LAYERED: 'Katmanli Mimari',
      HEXAGONAL: 'Hexagonal Architecture',
      CLEAN: 'Clean Architecture',
      MICROSERVICE: 'Microservice',
      MODULAR_MONOLITH: 'Moduler Monolith'
    }

    const projectSuggestions: Record<string, { name: string; icon: string; desc: string }[]> = {
      BEGINNER: [
        { name: 'Banka Uygulamasi', icon: '\uD83C\uDFE6', desc: 'Entity, Repository, Service, Controller katmanlarini adim adim ogreneceksin.' },
        { name: 'Kutuphane Yonetim Sistemi', icon: '\uD83D\uDCDA', desc: 'Kitap ve uye yonetimi ile temel CRUD islemlerini ogreneceksin.' }
      ],
      INTERMEDIATE: [
        { name: 'E-ticaret Sistemi', icon: '\uD83D\uDED2', desc: 'Urun katalogu, kategori iliskileri ve is mantigi yonetimini ogreneceksin.' },
        { name: 'Banka Uygulamasi', icon: '\uD83C\uDFE6', desc: 'Transaction yonetimi ve domain-driven design prensiplerini derinlestir.' }
      ],
      ADVANCED: [
        { name: 'E-ticaret Sistemi', icon: '\uD83D\uDED2', desc: 'Clean Architecture prensipleriyle moduler bir e-ticaret backend\'i kur.' },
        { name: 'Banka Uygulamasi', icon: '\uD83C\uDFE6', desc: 'Hexagonal Architecture ile bankaclik domain\'ini isola et.' }
      ]
    }

    const levelFeatures: Record<string, string[]> = {
      BEGINNER: [
        'Her annotation ve her satir detayli aciklanacak',
        'Gercek hayat benzetmeleri ile kavramlar pekistirilecek',
        '4 kademeli ipucu sistemi (kucuk ipucu → tam cozum)',
        'İpucu kullanımında düşük XP cezası'
      ],
      INTERMEDIATE: [
        'Onemli noktalar vurgulanacak, temeller kisaca hatirlatirilacak',
        'Yonlendirme seviyesinden baslayan 3 kademeli ipucu sistemi',
        'Daha az aciklama, daha cok pratik',
        'Orta duzeyde XP cezasi'
      ],
      ADVANCED: [
        'Sadece gorev verilecek, minimum aciklama',
        'Yalnizca 1 ipucu hakki (kod ornegi seviyesinde)',
        'Sadece hatali satirlar geri bildirim alacak',
        'Yuksek XP cezasi — kendi bilginle coz!'
      ]
    }

    const suggestions = projectSuggestions[result.level] || projectSuggestions['BEGINNER']
    const features = levelFeatures[result.level] || levelFeatures['BEGINNER']

    return (
      <div className="min-h-screen flex items-center justify-center px-4 py-12">
        <div className="card max-w-2xl w-full">
          <div className="text-center mb-8">
            <div className="text-5xl mb-4">&#127942;</div>
            <h1 className="text-2xl font-bold mb-2">Seviye Belirlendi!</h1>
            <div className={`text-3xl font-bold mb-2 ${levelColors[result.level]}`}>
              {levelLabels[result.level]}
            </div>
            <p className="text-gray-400">Puan: %{Math.round(result.score * 100)}</p>
          </div>

          <div className="bg-dark-700 rounded-lg p-4 mb-6">
            <h3 className="font-semibold text-primary-400 mb-2">Onerilen Mimari</h3>
            <p className="text-lg font-medium mb-2">{archLabels[result.recommendedArchitecture]}</p>
            <p className="text-gray-400 text-sm whitespace-pre-line">{result.architectureReasoning}</p>
          </div>

          <div className="bg-dark-700 rounded-lg p-4 mb-6">
            <h3 className="font-semibold text-primary-400 mb-3">Senin icin nasil olacak?</h3>
            <ul className="space-y-2">
              {features.map((f, i) => (
                <li key={i} className="flex items-start gap-2 text-sm text-gray-300">
                  <span className="text-primary-400 mt-0.5">&#8226;</span>
                  {f}
                </li>
              ))}
            </ul>
          </div>

          <div className="bg-dark-700 rounded-lg p-4 mb-6">
            <h3 className="font-semibold text-primary-400 mb-3">Onerilen Projeler</h3>
            <div className="space-y-3">
              {suggestions.map((s, i) => (
                <div key={i} className="flex items-start gap-3 bg-dark-800 rounded-lg p-3">
                  <span className="text-2xl">{s.icon}</span>
                  <div>
                    <p className="font-medium text-gray-200">{s.name}</p>
                    <p className="text-xs text-gray-400 mt-0.5">{s.desc}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>

          <button onClick={() => navigate('/projects')} className="btn-primary w-full">
            Proje Sec ve Basla
          </button>
        </div>
      </div>
    )
  }

  const q = questions[currentQ]
  if (!q) return null
  const progress = ((currentQ + 1) / questions.length) * 100

  return (
    <div className="min-h-screen flex items-center justify-center px-4">
      <div className="card max-w-2xl w-full">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-lg font-semibold">Seviye Tespiti</h2>
          <span className="text-sm text-gray-400">{currentQ + 1} / {questions.length}</span>
        </div>

        <div className="w-full bg-dark-700 rounded-full h-2 mb-8">
          <div className="bg-primary-500 h-2 rounded-full transition-all duration-300"
               style={{ width: `${progress}%` }}></div>
        </div>

        <p className="text-lg mb-6">{q.question}</p>

        <div className="space-y-3 mb-8">
          {q.options.map((opt, i) => (
            <button key={i} onClick={() => selectAnswer(i)}
              className={`w-full text-left p-4 rounded-lg border transition-all duration-200 ${
                answers[currentQ] === i
                  ? 'border-primary-500 bg-primary-500/10 text-white'
                  : 'border-dark-300 bg-dark-700 hover:border-primary-400 text-gray-300'
              }`}>
              <span className="font-medium mr-3 text-primary-400">{String.fromCharCode(65 + i)}</span>
              {opt}
            </button>
          ))}
        </div>

        <div className="flex justify-between">
          <button onClick={prev} disabled={currentQ === 0}
            className="btn-secondary disabled:opacity-30">Onceki</button>
          {currentQ === questions.length - 1 ? (
            <button onClick={submit} disabled={answers.includes(-1)}
              className="btn-primary disabled:opacity-30">Sonuclari Gor</button>
          ) : (
            <button onClick={next} disabled={answers[currentQ] === -1}
              className="btn-primary disabled:opacity-30">Sonraki</button>
          )}
        </div>
      </div>
    </div>
  )
}
