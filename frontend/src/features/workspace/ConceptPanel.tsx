import { useState, useEffect } from 'react'
import api from '../../lib/api'

interface ConceptData {
  code: string
  title: string
  realLifeAnalogy: string
  whyExplanation: string
  wrongExample: string
  wrongExampleExplanation: string
  rightExample: string
  rightExampleExplanation: string
}

interface Props {
  conceptCode: string
  onClose: () => void
}

export default function ConceptPanel({ conceptCode, onClose }: Props) {
  const [concept, setConcept] = useState<ConceptData | null>(null)
  const [activeTab, setActiveTab] = useState<'analogy' | 'wrong' | 'right' | 'why'>('analogy')

  useEffect(() => {
    api.get(`/concepts/${conceptCode}`).then(({ data }) => setConcept(data)).catch(() => {})
  }, [conceptCode])

  if (!concept) return null

  const tabs = [
    { key: 'analogy', label: 'Gercek Hayat Benzetmesi', icon: '\uD83C\uDF0D' },
    { key: 'wrong', label: 'Yanlis Kullanim', icon: '\u274C' },
    { key: 'right', label: 'Dogru Kullanim', icon: '\u2705' },
    { key: 'why', label: 'Neden Boyle?', icon: '\u2753' },
  ] as const

  return (
    <div className="p-4">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-bold text-primary-400">{concept.title}</h3>
        <button onClick={onClose} className="text-gray-500 hover:text-white text-xl">&times;</button>
      </div>

      <div className="flex gap-2 mb-4">
        {tabs.map(t => (
          <button key={t.key} onClick={() => setActiveTab(t.key)}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-sm font-medium transition-colors ${
              activeTab === t.key
                ? 'bg-primary-500/20 text-primary-300'
                : 'bg-dark-700 text-gray-400 hover:text-gray-200'
            }`}>
            <span>{t.icon}</span> {t.label}
          </button>
        ))}
      </div>

      <div className="bg-dark-700 rounded-lg p-4 text-sm text-gray-300 leading-relaxed">
        {activeTab === 'analogy' && <p>{concept.realLifeAnalogy}</p>}
        {activeTab === 'wrong' && (
          <div>
            <pre className="bg-dark-900 p-3 rounded text-danger-500 text-xs overflow-x-auto mb-3">
              {concept.wrongExample}
            </pre>
            <p className="text-gray-400">{concept.wrongExampleExplanation}</p>
          </div>
        )}
        {activeTab === 'right' && (
          <div>
            <pre className="bg-dark-900 p-3 rounded text-success-500 text-xs overflow-x-auto mb-3">
              {concept.rightExample}
            </pre>
            <p className="text-gray-400">{concept.rightExampleExplanation}</p>
          </div>
        )}
        {activeTab === 'why' && <p>{concept.whyExplanation}</p>}
      </div>
    </div>
  )
}
