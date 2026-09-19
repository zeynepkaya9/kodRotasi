import { useAppStore } from '../../lib/store'

export default function ProgressPage() {
  const { totalXp, currentStreak, activeTasks, learningPath } = useAppStore()

  const allSteps = activeTasks.flatMap(t => t.steps)
  const completedSteps = allSteps.filter(s => s.status === 'COMPLETED').length
  const totalSteps = allSteps.length
  const completionPct = totalSteps > 0 ? Math.round((completedSteps / totalSteps) * 100) : 0

  const levelLabels: Record<string, string> = {
    BEGINNER: 'Baslangic',
    INTERMEDIATE: 'Orta',
    ADVANCED: 'Ileri'
  }

  const archLabels: Record<string, string> = {
    LAYERED: 'Katmanli Mimari',
    HEXAGONAL: 'Hexagonal Architecture',
    CLEAN: 'Clean Architecture',
    MICROSERVICE: 'Microservice',
    MODULAR_MONOLITH: 'Moduler Monolith'
  }

  return (
    <div className="min-h-screen px-4 py-12">
      <div className="max-w-3xl mx-auto">
        <h1 className="text-3xl font-bold text-center mb-8">Ilerleme Durumun</h1>

        {/* Stats */}
        <div className="grid grid-cols-3 gap-6 mb-8">
          <div className="card text-center">
            <div className="text-3xl font-bold text-primary-400">{totalXp}</div>
            <div className="text-sm text-gray-400 mt-1">Toplam XP</div>
          </div>
          <div className="card text-center">
            <div className="text-3xl font-bold text-warning-500">{currentStreak}</div>
            <div className="text-sm text-gray-400 mt-1">Seri</div>
          </div>
          <div className="card text-center">
            <div className="text-3xl font-bold text-success-500">{completedSteps}</div>
            <div className="text-sm text-gray-400 mt-1">Tamamlanan Adim</div>
          </div>
        </div>

        {/* Level & Architecture */}
        {learningPath && (
          <div className="card mb-6">
            <div className="flex justify-between items-center">
              <div>
                <h3 className="font-semibold mb-1">Seviyen</h3>
                <span className="text-primary-400 font-bold text-lg">{levelLabels[learningPath.level] || learningPath.level}</span>
              </div>
              <div className="text-right">
                <h3 className="font-semibold mb-1">Mimari</h3>
                <span className="text-gray-300">{archLabels[learningPath.chosenArchitecture] || learningPath.chosenArchitecture}</span>
              </div>
            </div>
          </div>
        )}

        {/* Progress bar */}
        <div className="card mb-6">
          <div className="flex justify-between items-center mb-3">
            <h3 className="font-semibold">Genel Ilerleme</h3>
            <span className="text-primary-400 font-bold">{completionPct}%</span>
          </div>
          <div className="w-full bg-dark-700 rounded-full h-3">
            <div className="bg-gradient-to-r from-primary-500 to-primary-400 h-3 rounded-full transition-all duration-500"
                 style={{ width: `${completionPct}%` }}></div>
          </div>
          <p className="text-sm text-gray-400 mt-2">
            {completedSteps} / {totalSteps} adim tamamlandi
          </p>
        </div>

        {/* Task breakdown */}
        <div className="card">
          <h3 className="font-semibold mb-4">Gorev Detayi</h3>
          <div className="space-y-3">
            {activeTasks.map(task => {
              const taskCompleted = task.steps.filter(s => s.status === 'COMPLETED').length
              const taskTotal = task.steps.length
              const taskPct = taskTotal > 0 ? Math.round((taskCompleted / taskTotal) * 100) : 0
              return (
                <div key={task.id} className="flex items-center justify-between py-2 border-b border-dark-700 last:border-0">
                  <div className="flex items-center gap-3">
                    <span className="text-lg">
                      {taskPct === 100 ? '\u2705' : task.steps.some(s => s.status === 'ACTIVE') ? '\u25B6\uFE0F' : taskPct > 0 ? '\u{1F504}' : '\u{1F512}'}
                    </span>
                    <span className={taskPct === 100 ? 'text-gray-400 line-through' : 'text-gray-200'}>
                      {task.title}
                    </span>
                  </div>
                  <span className="text-sm text-gray-500">{taskCompleted}/{taskTotal}</span>
                </div>
              )
            })}
          </div>
        </div>
      </div>
    </div>
  )
}
