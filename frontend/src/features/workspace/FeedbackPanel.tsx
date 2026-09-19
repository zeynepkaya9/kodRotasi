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

interface Props {
  evalResult: EvalResult | null
  hints: HintData[]
  onConceptClick: (code: string) => void
}

const hintLevelLabels: Record<string, string> = {
  SMALL: 'Küçük İpucu',
  GUIDE: 'Yönlendirme',
  CODE: 'Örnek Kod',
  SOLUTION: 'Tam Çözüm'
}

export default function FeedbackPanel({ evalResult, hints, onConceptClick }: Props) {
  return (
    <div className="p-4 space-y-4">
      <h3 className="text-sm font-semibold text-gray-400 uppercase tracking-wider">Canli Geri Bildirim</h3>

      {/* Hint display */}
      {hints.length > 0 && (
        <div className="space-y-3">
          <p className="text-xs text-gray-500">Açtığın ipuçları burada kalır ve sayfa yenilendiğinde geri yüklenir.</p>
          {hints.map((hint, index) => (
            <div key={`${hint.hintLevel}-${index}`} className="bg-warning-500/10 border border-warning-500/30 rounded-lg p-4">
              <div className="flex items-center justify-between mb-2">
                <span className="text-warning-500 font-semibold text-sm">
                  &#128161; {index + 1}. {hintLevelLabels[hint.hintLevel] || hint.hintLevel}
                </span>
                <span className="text-xs text-gray-500">
                  {hint.xpPenalty > 0 ? `Açma bedeli: -${hint.xpPenalty} XP` : 'Ücretsiz'}
                </span>
              </div>
              <div className="text-sm text-gray-300 whitespace-pre-wrap">{hint.content}</div>
            </div>
          ))}
        </div>
      )}

      {/* Evaluation result */}
      {evalResult && (
        <>
          <div className={`rounded-lg p-4 ${
            evalResult.passed
              ? 'bg-success-500/10 border border-success-500/30'
              : 'bg-danger-500/10 border border-danger-500/30'
          }`}>
            <div className="flex items-center justify-between mb-2">
              <span className={`font-bold ${evalResult.passed ? 'text-success-500' : 'text-danger-500'}`}>
                {evalResult.passed ? '\u2705 Basarili!' : '\u274C Henuz tamamlanmadi'}
              </span>
              {evalResult.xpEarned > 0 && (
                <span className="text-success-500 font-semibold">+{evalResult.xpEarned} XP</span>
              )}
            </div>
          </div>

          {/* Line-by-line feedback */}
          <div className="space-y-3">
            {evalResult.feedback.map((f, i) => (
              <div key={i} className={`rounded-lg p-3 text-sm ${
                f.status === 'PASS'
                  ? 'bg-dark-700 border-l-4 border-success-500'
                  : 'bg-dark-700 border-l-4 border-danger-500'
              }`}>
                <p className="font-medium text-gray-200 mb-1">{f.message}</p>

                {f.why && (
                  <div className="mt-2 pl-3 border-l-2 border-primary-500/30">
                    <p className="text-xs text-primary-400 font-semibold mb-0.5">Neden?</p>
                    <p className="text-xs text-gray-400">{f.why}</p>
                  </div>
                )}

                {f.alternative && (
                  <div className="mt-2 pl-3 border-l-2 border-warning-500/30">
                    <p className="text-xs text-warning-400 font-semibold mb-0.5">Alternatif</p>
                    <p className="text-xs text-gray-400">{f.alternative}</p>
                  </div>
                )}

                {f.realWorld && (
                  <div className="mt-2 pl-3 border-l-2 border-success-500/30">
                    <p className="text-xs text-success-500 font-semibold mb-0.5">Gercek Projede</p>
                    <p className="text-xs text-gray-400">{f.realWorld}</p>
                  </div>
                )}
              </div>
            ))}
          </div>

          {/* Concept links */}
          {evalResult.conceptsToReview.length > 0 && (
            <div>
              <h4 className="text-xs font-semibold text-gray-400 uppercase mb-2">Ilgili Kavramlar</h4>
              <div className="flex flex-wrap gap-2">
                {evalResult.conceptsToReview.map((code) => (
                  <button key={code} onClick={() => onConceptClick(code)}
                    className="bg-primary-500/10 text-primary-400 px-3 py-1 rounded-full text-xs font-medium hover:bg-primary-500/20 transition-colors">
                    {code.replace(/_/g, ' ')}
                  </button>
                ))}
              </div>
            </div>
          )}
        </>
      )}

      {!evalResult && hints.length === 0 && (
        <div className="text-center py-12 text-gray-500">
          <div className="text-4xl mb-3">&#128187;</div>
          <p className="text-sm">Kodunu yaz ve "Gonder" butonuna bas.</p>
          <p className="text-xs mt-1">Java sözdizimi ve görevdeki yapısal kurallar kontrol edilir. Kodun çalışması ve tüm iş kuralları ayrıca test edilmelidir.</p>
        </div>
      )}
    </div>
  )
}
