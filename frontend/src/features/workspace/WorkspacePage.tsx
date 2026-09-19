import { useState, useEffect, useCallback, useRef } from 'react'
import Editor from '@monaco-editor/react'
import api, { saveDraft } from '../../lib/api'
import { Navigate } from 'react-router-dom'
import { useAppStore } from '../../lib/store'
import TaskPanel from './TaskPanel'
import FeedbackPanel from './FeedbackPanel'
import ConceptPanel from './ConceptPanel'

interface Feedback {
  ruleId: string
  status: string
  message: string
  why: string | null
  alternative: string | null
  realWorld: string | null
}

interface EvalResult {
  passed: boolean
  feedback: Feedback[]
  xpEarned: number
  conceptsToReview: string[]
}

interface HintData {
  hintLevel: string
  content: string
  xpPenalty: number
  remainingHints: number
}

export default function WorkspacePage() {
  const {
    activeTasks, activeStepId, setActiveStepId,
    currentCode, setCurrentCode, setActiveTasks,
    saveCodeForStep, getCodeForStep,
    addXp, incrementStreak, totalXp, currentStreak
  } = useAppStore()

  const [evalResult, setEvalResult] = useState<EvalResult | null>(null)
  const [hints, setHints] = useState<HintData[]>([])
  const [remainingHints, setRemainingHints] = useState<number | null>(null)
  const [hintHistoryLoading, setHintHistoryLoading] = useState(false)
  const [activeConcept, setActiveConcept] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)
  const [hintLoading, setHintLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [stepInstruction, setStepInstruction] = useState('')
  const [showSuccess, setShowSuccess] = useState(false)
  const [saveStatus, setSaveStatus] = useState('')
  const saveTimer = useRef<ReturnType<typeof setTimeout> | null>(null)
  const pendingDraft = useRef<{ id: number; code: string; token: string } | null>(null)
  const saveQueue = useRef<Promise<unknown>>(Promise.resolve())
  const flushDraft = useCallback(() => {
    if (saveTimer.current) clearTimeout(saveTimer.current)
    const draft = pendingDraft.current
    pendingDraft.current = null
    if (draft) {
      saveQueue.current = saveQueue.current.catch(() => {}).then(() => saveDraft(draft.id, draft.code, draft.token))
      void saveQueue.current.then(() => setSaveStatus('Taslak kaydedildi'), () => setSaveStatus('Sunucuya kaydedilemedi; bu tarayıcıda saklandı. Tekrar düzenleyerek yeniden deneyebilirsin.'))
    }
    return saveQueue.current
  }, [])
  useEffect(() => () => { void flushDraft().catch(() => {}) }, [flushDraft])

  const allSteps = activeTasks.flatMap(t => t.steps)
  const activeStep = allSteps.find(s => s.id === activeStepId)
  const stepIndex = allSteps.findIndex(s => s.id === activeStepId)
  const currentTaskIndex = activeTasks.findIndex(t => t.steps.some(s => s.id === activeStepId))
  const currentTask = activeTasks[currentTaskIndex]

  useEffect(() => {
    if (activeStep && activeStepId != null) {
      const cached = getCodeForStep(activeStepId)
      if (cached !== undefined) {
        setCurrentCode(cached)
      } else {
        setCurrentCode(activeStep.starterCode || '')
      }
      setStepInstruction(activeStep.instruction)
      setError(null)
      setEvalResult(null)
      setHints([])
      setRemainingHints(null)
      setActiveConcept(null)
      setShowSuccess(false)
    }
  }, [activeStepId])

  useEffect(() => {
    if (!activeStepId || activeStep?.status === 'LOCKED') return
    let cancelled = false
    setHintHistoryLoading(true)
    api.get(`/steps/${activeStepId}/hints`)
      .then(({ data }) => {
        if (!cancelled) {
          setHints(data.revealedHints || [])
          setRemainingHints(data.remainingHints)
        }
      })
      .catch((err: any) => {
        if (!cancelled) setError(err.response?.data?.message || 'İpucu geçmişi yüklenemedi.')
      })
      .finally(() => { if (!cancelled) setHintHistoryLoading(false) })
    return () => { cancelled = true }
  }, [activeStepId, activeStep?.status])

  const handleCodeChange = (val: string | undefined) => {
    const code = val || ''
    setCurrentCode(code)
    if (activeStepId != null) {
      if (getCodeForStep(activeStepId) === code) return
      saveCodeForStep(activeStepId, code)
      const token = useAppStore.getState().accessToken
      if (token) {
        if (pendingDraft.current && pendingDraft.current.id !== activeStepId) void flushDraft().catch(() => {})
        pendingDraft.current = { id: activeStepId, code, token }
        setSaveStatus('Kaydediliyor…')
        if (saveTimer.current) clearTimeout(saveTimer.current)
        saveTimer.current = setTimeout(() => { void flushDraft().catch(() => {}) }, 600)
      }
    }
  }

  const handleEvaluate = useCallback(async () => {
    if (!activeStepId || submitting || hintLoading || activeStep?.status === 'LOCKED') return
    const requestToken = useAppStore.getState().accessToken
    setError(null)
    setSubmitting(true)
    try {
      await flushDraft()
      if (useAppStore.getState().accessToken !== requestToken) return
      const { data } = await api.post(`/steps/${activeStepId}/evaluate`, { code: currentCode })
      if (useAppStore.getState().accessToken !== requestToken) return
      setEvalResult(data)

      if (data.passed) {
        saveCodeForStep(activeStepId, currentCode)

        if (data.xpEarned > 0) {
          addXp(data.xpEarned)
          incrementStreak()
        }

        if (data.conceptsToReview.length > 0) {
          setActiveConcept(data.conceptsToReview[0])
        }

        const updatedTasks = activeTasks.map(task => ({
          ...task,
          steps: task.steps.map(step => {
            if (step.id === activeStepId) {
              return { ...step, status: 'COMPLETED' }
            }
            return step
          })
        }))

        const allUpdatedSteps = updatedTasks.flatMap(t => t.steps)
        const currentIdx = allUpdatedSteps.findIndex(s => s.id === activeStepId)
        if (currentIdx >= 0 && currentIdx < allUpdatedSteps.length - 1) {
          const nextStep = allUpdatedSteps[currentIdx + 1]
          if (nextStep.status === 'LOCKED') {
            const taskIdx = updatedTasks.findIndex(t => t.steps.some(s => s.id === nextStep.id))
            const sIdx = updatedTasks[taskIdx].steps.findIndex(s => s.id === nextStep.id)
            updatedTasks[taskIdx].steps[sIdx] = { ...nextStep, status: 'ACTIVE' }
          }
        }

        setActiveTasks(updatedTasks)
        setShowSuccess(true)
      }
      const [tasks, progress] = await Promise.all([api.get('/my-tasks'), api.get('/progress')])
      if (useAppStore.getState().accessToken === requestToken) {
        setActiveTasks(tasks.data)
        useAppStore.getState().setProgress(progress.data)
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Değerlendirme yapılamadı. Lütfen tekrar dene.')
    } finally {
      setSubmitting(false)
    }
  }, [activeStepId, currentCode, activeTasks, submitting, hintLoading])

  const goToNextStep = () => {
    const currentIdx = allSteps.findIndex(s => s.id === activeStepId)
    if (currentIdx >= 0 && currentIdx < allSteps.length - 1) {
      setActiveStepId(allSteps[currentIdx + 1].id)
    }
  }

  const handleHint = async () => {
    if (!activeStepId || hintLoading || hintHistoryLoading || submitting || activeStep?.status !== 'ACTIVE') return
    const requestToken = useAppStore.getState().accessToken
    setHintLoading(true)
    setError(null)
    try {
      const { data } = await api.post(`/steps/${activeStepId}/hint`)
      if (useAppStore.getState().accessToken !== requestToken) return
      setHints(previous => {
        const alreadyShown = previous.some(item =>
          item.hintLevel === data.hintLevel && item.content === data.content)
        return alreadyShown ? previous : [...previous, data]
      })
      setRemainingHints(data.remainingHints)
      addXp(-Math.min(useAppStore.getState().totalXp, data.xpPenalty))
      const progress = await api.get('/progress')
      if (useAppStore.getState().accessToken === requestToken) useAppStore.getState().setProgress(progress.data)
    } catch (err: any) {
      setError(err.response?.data?.message || 'İpucu alınamadı. Lütfen tekrar dene.')
    } finally {
      setHintLoading(false)
    }
  }

  const nextStepAvailable = (() => {
    const idx = allSteps.findIndex(s => s.id === activeStepId)
    return idx >= 0 && idx < allSteps.length - 1
  })()

  const completedCount = allSteps.filter(s => s.status === 'COMPLETED').length

  if (!activeTasks.length) return <Navigate to="/projects" replace />

  return (
    <div className="min-h-[calc(100dvh-56px)] xl:h-[calc(100dvh-56px)] flex flex-col">
      {/* Header bar */}
      <div className="bg-dark-800 border-b border-dark-700 px-4 py-2 flex flex-wrap gap-3 items-center justify-between">
        <div className="flex items-center gap-3">
          <span className="text-primary-400 font-semibold">
            {currentTask?.title || 'Gorev'}
          </span>
          <span className="text-gray-500 text-sm">
            Adim {stepIndex + 1} / {allSteps.length}
          </span>
        </div>
        <div className="flex flex-wrap items-center gap-4">
          <div className="flex items-center gap-3 text-sm">
            <span className="text-primary-400 font-medium">{totalXp} XP</span>
            <span className="text-warning-500 font-medium">{currentStreak} Seri</span>
            <span className="text-success-500 font-medium">{completedCount}/{allSteps.length}</span>
          </div>

          <button onClick={handleHint} disabled={hintLoading || hintHistoryLoading || submitting || activeStep?.status !== 'ACTIVE' || remainingHints === 0}
            className="flex items-center gap-1.5 bg-warning-500/10 text-warning-500 px-3 py-1.5 rounded-lg hover:bg-warning-500/20 transition-colors text-sm font-medium disabled:opacity-50 disabled:cursor-not-allowed">
            <span>&#128161;</span> {hintHistoryLoading ? 'İpuçları yükleniyor…' : remainingHints === 0 ? 'Tüm ipuçları açık' : 'İpucu göster'}
            {remainingHints != null && <span className="text-xs">({remainingHints} kaldı)</span>}
          </button>

          {showSuccess && nextStepAvailable ? (
            <button onClick={goToNextStep}
              className="bg-success-500 hover:bg-success-700 text-white font-medium text-sm py-1.5 px-4 rounded-lg transition-colors animate-pulse">
              Sonraki Adima Gec &#8594;
            </button>
          ) : (
            <button onClick={handleEvaluate} disabled={submitting || hintLoading || !activeStep || activeStep.status === 'LOCKED'}
              className="btn-primary text-sm py-1.5 disabled:opacity-50">
              {submitting ? 'Degerlendiriliyor...' : 'Gonder'}
            </button>
          )}
        </div>
      </div>

      {error && <div role="alert" className="bg-red-900/30 text-red-200 px-4 py-3">{error}</div>}

      {saveStatus && <div role="status" className="text-xs text-gray-400 px-4 py-1">{saveStatus}</div>}
      {completedCount === allSteps.length && <div className="text-green-300 px-4 py-2">Bu projeyi tamamladın! Adımları tekrar çalışabilir veya Projeler menüsünden yeni bir proje seçebilirsin.</div>}
      {/* Main layout */}
      <div className="flex-1 min-h-0 flex flex-col xl:flex-row xl:overflow-hidden">
        {/* Left: Task panel */}
        <div className="w-full xl:w-80 xl:shrink-0 bg-dark-800 border-r border-dark-700 max-h-80 xl:max-h-none overflow-y-auto">
          <TaskPanel
            tasks={activeTasks}
            activeStepId={activeStepId}
            onStepSelect={(id) => { if (!submitting && !hintLoading) setActiveStepId(id) }}
            instruction={stepInstruction}
            learningNotes={activeStep?.learningNotes || ''}
          />
        </div>

        {/* Center: Code editor */}
        <div className="min-w-0 h-[55dvh] min-h-80 xl:h-auto xl:flex-1 flex flex-col" aria-label="Java kod editörü">
          <Editor
            height="100%"
            defaultLanguage="java"
            theme="vs-dark"
            value={currentCode}
            onChange={handleCodeChange}
            options={{
              readOnly: submitting || !activeStep || activeStep.status === 'LOCKED',
              fontSize: 14,
              minimap: { enabled: false },
              lineNumbers: 'on',
              wordWrap: 'on',
              scrollBeyondLastLine: false,
              padding: { top: 12, bottom: 12 },
              automaticLayout: true,
            }}
          />
        </div>

        {/* Right: Feedback panel */}
        <div className="w-full xl:w-80 xl:shrink-0 bg-dark-800 border-l border-dark-700 xl:overflow-y-auto">
          <FeedbackPanel
            evalResult={evalResult}
            hints={hints}
            onConceptClick={setActiveConcept}
          />
        </div>
      </div>

      {/* Bottom: Concept panel */}
      {activeConcept && (
        <div className="bg-dark-800 border-t border-dark-700 max-h-72 overflow-y-auto">
          <ConceptPanel
            conceptCode={activeConcept}
            onClose={() => setActiveConcept(null)}
          />
        </div>
      )}
    </div>
  )
}
