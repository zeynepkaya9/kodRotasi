interface Step {
  id: number
  orderIndex: number
  instruction: string
  status: string
}

interface Task {
  id: number
  orderIndex: number
  title: string
  objective: string
  steps: Step[]
}

interface Props {
  tasks: Task[]
  activeStepId: number | null
  onStepSelect: (id: number) => void
  instruction: string
  learningNotes: string
}

export default function TaskPanel({ tasks, activeStepId, onStepSelect, instruction, learningNotes }: Props) {
  const statusIcon = (status: string) => {
    if (status === 'COMPLETED') return '\u2705'
    if (status === 'ACTIVE') return '\u25B6\uFE0F'
    return '\uD83D\uDD12'
  }

  return (
    <div className="p-4">
      <h3 className="text-sm font-semibold text-gray-400 uppercase tracking-wider mb-4">Gorevler</h3>

      {/* Step list */}
      <div className="space-y-1 mb-6">
        {tasks.map((task) => (
          <div key={task.id}>
            <div className="text-xs font-semibold text-gray-500 uppercase mt-3 mb-1">{task.title}</div>
            {task.steps.map((step) => (
              <button key={step.id}
                onClick={() => step.status !== 'LOCKED' && onStepSelect(step.id)}
                disabled={step.status === 'LOCKED'}
                className={`w-full text-left px-3 py-2 rounded-lg text-sm flex items-center gap-2 transition-colors ${
                  step.id === activeStepId
                    ? 'bg-primary-500/15 text-primary-300 border border-primary-500/30'
                    : step.status === 'LOCKED'
                    ? 'text-gray-600 cursor-not-allowed'
                    : 'text-gray-300 hover:bg-dark-700'
                }`}>
                <span className="text-xs">{statusIcon(step.status)}</span>
                <span className="truncate">Adim {step.orderIndex + 1}</span>
              </button>
            ))}
          </div>
        ))}
      </div>

      {/* Active instruction */}
      <div className="border-t border-dark-700 pt-4">
        <h4 className="text-sm font-semibold text-primary-400 mb-3">Görev Talimatı</h4>
        <div className="text-sm text-gray-300 whitespace-pre-wrap leading-relaxed">
          {instruction}
        </div>
      </div>

      {learningNotes && (
        <div className="mt-5 rounded-xl border border-sky-500/25 bg-sky-500/10 p-4">
          <h4 className="text-sm font-semibold text-sky-300 mb-3 flex items-center gap-2">
            <span aria-hidden="true">📘</span> Öğretici Notlar
          </h4>
          <div className="text-sm text-gray-300 whitespace-pre-wrap leading-relaxed">
            {learningNotes}
          </div>
        </div>
      )}
    </div>
  )
}
