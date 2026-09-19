import { create } from 'zustand'

interface User { email: string; displayName: string }
interface LearningPath { level: string; recommendedArchitecture: string; chosenArchitecture: string }
export interface StepProgress {
  id: number; orderIndex: number; instruction: string; learningNotes: string; starterCode: string
  status: string; attempts: number; hintsUsed: number; savedCode?: string | null; savedAt?: string | null
}
export interface TaskData {
  id: number; orderIndex: number; title: string; objective: string; conceptCodes: string; steps: StepProgress[]
}
interface LocalWork { codeCache: Record<number, string>; draftTimes: Record<number, number>; activeStepId: number | null }
interface AppState extends LocalWork {
  user: User | null; accessToken: string | null; learningPath: LearningPath | null
  activeTasks: TaskData[]; currentCode: string; totalXp: number; currentStreak: number; sessionReady: boolean
  setUser: (user: User | null) => void
  setAccessToken: (token: string | null) => void
  setLearningPath: (path: LearningPath | null) => void
  setActiveTasks: (tasks: TaskData[]) => void
  setActiveStepId: (id: number | null) => void
  setCurrentCode: (code: string) => void
  saveCodeForStep: (stepId: number, code: string) => void
  getCodeForStep: (stepId: number) => string | undefined
  setProgress: (progress: { totalXp: number; currentStreak: number }) => void
  setSessionReady: (ready: boolean) => void
  addXp: (xp: number) => void
  incrementStreak: () => void
  logout: () => void
}
function read<T>(key: string, fallback: T): T {
  try { return JSON.parse(localStorage.getItem(key) || 'null') ?? fallback } catch { return fallback }
}
function write(key: string, value: unknown) {
  try { localStorage.setItem(key, JSON.stringify(value)) } catch { /* Server autosave remains available. */ }
}
const emptyWork = (): LocalWork => ({ codeCache: {}, draftTimes: {}, activeStepId: null })
const keyFor = (user: User) => `jsy_work:${user.email}`
const initialUser = read<User | null>('jsy_user', null)
function loadWork(user: User | null): LocalWork {
  if (!user) return emptyWork()
  const saved = read<LocalWork | null>(keyFor(user), null)
  if (saved) return saved
  // One-time migration of drafts from the previous version, only for their recorded owner.
  if (read<User | null>('jsy_user', null)?.email === user.email) {
    const codeCache = read<Record<number, string>>('jsy_codeCache', {})
    const work = { codeCache, draftTimes: Object.fromEntries(Object.keys(codeCache).map(id => [id, Date.now()])), activeStepId: read<number | null>('jsy_activeStepId', null) }
    write(keyFor(user), work)
    return work
  }
  return emptyWork()
}
export function selectResumeStep(tasks: TaskData[], preferred: number | null): number | null {
  const steps = tasks.flatMap(task => task.steps)
  return steps.find(step => step.id === preferred && step.status !== 'LOCKED')?.id
    ?? steps.find(step => step.status === 'ACTIVE')?.id
    ?? [...steps].reverse().find(step => step.status === 'COMPLETED')?.id ?? null
}
const resetState = { learningPath: null, activeTasks: [], currentCode: '', totalXp: 0, currentStreak: 0, sessionReady: false }
export const useAppStore = create<AppState>((set, get) => {
  const persistWork = () => {
    const { user, codeCache, draftTimes, activeStepId } = get()
    if (user) write(keyFor(user), { codeCache, draftTimes, activeStepId })
  }
  return {
    ...resetState, ...loadWork(initialUser), user: initialUser, accessToken: localStorage.getItem('accessToken'),
    setUser: user => {
      set({ ...resetState, ...loadWork(user), user })
      write('jsy_user', user)
    },
    setAccessToken: accessToken => {
      if (accessToken) localStorage.setItem('accessToken', accessToken)
      else localStorage.removeItem('accessToken')
      set({ accessToken, sessionReady: false })
    },
    setSessionReady: sessionReady => set({ sessionReady }),
    setLearningPath: learningPath => set({ learningPath }),
    setActiveTasks: activeTasks => {
      const codeCache = { ...get().codeCache }
      const draftTimes = { ...get().draftTimes }
      for (const step of activeTasks.flatMap(task => task.steps)) {
        const serverTime = step.savedAt ? Date.parse(step.savedAt) : 0
        if (step.savedCode != null && (codeCache[step.id] === undefined || serverTime > (draftTimes[step.id] || 0))) {
          codeCache[step.id] = step.savedCode
          draftTimes[step.id] = serverTime
        }
      }
      set({ activeTasks, codeCache, draftTimes })
      persistWork()
    },
    setActiveStepId: activeStepId => { set({ activeStepId }); persistWork() },
    setCurrentCode: currentCode => set({ currentCode }),
    saveCodeForStep: (stepId, code) => {
      set({ codeCache: { ...get().codeCache, [stepId]: code }, draftTimes: { ...get().draftTimes, [stepId]: Date.now() } })
      persistWork()
    },
    getCodeForStep: stepId => get().codeCache[stepId],
    setProgress: ({ totalXp, currentStreak }) => set({ totalXp, currentStreak }),
    addXp: xp => set({ totalXp: Math.max(0, get().totalXp + xp) }),
    incrementStreak: () => set({ currentStreak: get().currentStreak + 1 }),
    logout: () => {
      persistWork()
      for (const key of ['accessToken', 'refreshToken', 'jsy_user', 'jsy_learningPath', 'jsy_activeTasks', 'jsy_activeStepId', 'jsy_codeCache', 'jsy_totalXp', 'jsy_currentStreak']) localStorage.removeItem(key)
      set({ ...resetState, ...emptyWork(), user: null, accessToken: null })
    },
  }
})
