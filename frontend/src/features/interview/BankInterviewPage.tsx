import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useAppStore } from '../../lib/store'
import { bankTopics, StudyLevel } from './bankTopics'
import { notebookKey, readNotebook, saveNotebook, TopicWork } from './notebook'

export default function BankInterviewPage() {
  const email = useAppStore(s => s.user?.email)
  const key = notebookKey(email)
  return <InterviewNotebook key={key} storageKey={key} />
}

function InterviewNotebook({ storageKey }: { storageKey: string }) {
  const [selected, setSelected] = useState(0)
  const [level, setLevel] = useState<StudyLevel>('Başlangıç')
  const [notebook, setNotebook] = useState(() => readNotebook(storageKey))
  const [saved, setSaved] = useState(true)
  const [revealed, setRevealed] = useState(false)
  const [choice, setChoice] = useState<number | null>(null)
  const topic = bankTopics[selected]
  const work = notebook[topic.id] || { draft: '', checked: [] }
  const completed = bankTopics.filter(t => t.checklist.every((_, i) => notebook[t.id]?.checked.includes(i))).length
  function select(index: number) { setSelected(index); setRevealed(false); setChoice(null) }
  function update(patch: Partial<TopicWork>) {
    const next = { ...notebook, [topic.id]: { ...work, ...patch } }
    setNotebook(next)
    setSaved(saveNotebook(storageKey, next))
  }
  return (
    <main className="mx-auto max-w-7xl px-4 py-8 sm:px-6">
      <header className="mb-7 rounded-2xl border border-primary-500/30 bg-gradient-to-br from-primary-900/40 to-dark-800 p-6 sm:p-8">
        <p className="text-sm font-semibold uppercase tracking-widest text-primary-300">Banka teknik mülakatı • Software Engineer</p>
        <h1 className="mt-3 text-3xl font-bold sm:text-4xl">Kodunu yaz. Kararını açıkla.</h1>
        <p className="mt-3 max-w-3xl leading-7 text-gray-300">Java ve Spring’den Redis, Kafka ve mikroservislere: bir transferin doğru, güvenli ve izlenebilir çalışmasını tasarla. DDD, tasarım desenleri, test, CI/CD ve ekip iletişimi üzerine teknik mülakat pratiği yap.</p>
        <div className="mt-5 flex flex-wrap gap-3 text-sm">
          <Link to="/projects" className="btn-primary">Banka kod görevlerini aç</Link>
          <a href="#study" className="rounded-lg border border-dark-700 px-4 py-2 text-gray-200 hover:bg-dark-700">Mülakat provasına başla</a>
        </div>
        <p className="mt-4 text-xs leading-5 text-gray-400">14 konu · 12 banka kod görevi · Kendi değerlendirmene göre {completed}/14 konu tekrar edildi. Bu bir mülakat başarı puanı değildir.</p>
      </header>

      <section aria-label="Örnek banka mimarisi" className="mb-7 rounded-xl border border-dark-700 bg-dark-800 p-5">
        <h2 className="font-semibold">Redis ve Kafka bu tasarımın neresinde?</h2>
        <div className="mt-4 grid gap-3 text-sm md:grid-cols-3">
          <div className="rounded-lg bg-dark-900 p-4"><p className="font-semibold text-primary-300">1 · Transferin ana yolu</p><p className="mt-2 leading-6 text-gray-300">React → API → yetki ve idempotency → transaction → PostgreSQL hareketleri + outbox</p></div>
          <div className="rounded-lg bg-dark-900 p-4"><p className="font-semibold text-emerald-300">2 · Hızlı okuma</p><p className="mt-2 leading-6 text-gray-300">Şube bilgisi → Redis cache → miss durumunda DB → TTL ile cache. Para çekme kararı cache bakiyesinden verilmez.</p></div>
          <div className="rounded-lg bg-dark-900 p-4"><p className="font-semibold text-amber-300">3 · Transfer sonrası</p><p className="mt-2 leading-6 text-gray-300">Outbox relay → Kafka → ayrı bildirim ve raporlama grupları → tekrar teslim için kalıcı inbox</p></div>
        </div>
        <p className="mt-3 text-xs leading-5 text-gray-400">Bu bir hedef tasarım örneğidir. Eğitim platformu Kafka broker’ı çalıştırmaz; Redis görevi istemci kodunu, Kafka görevi tekrar teslim davranışını öğretir. Gerçek altyapı entegrasyonu ayrıca test edilir.</p>
      </section>

      <div id="study" className="grid items-start gap-6 lg:grid-cols-[280px_minmax(0,1fr)]">
        <aside className="rounded-xl border border-dark-700 bg-dark-800 p-4">
          <h2 className="mb-3 font-semibold">Çalışma rotan</h2>
          <p className="mb-4 text-xs leading-5 text-gray-400">Her konuda önce anlat, sonra örnek yanıtla karşılaştır. Bir konuyu kod, hata senaryosu ve test ile açıklayabiliyorsan işaretle.</p>
          <nav aria-label="Mülakat konuları" className="max-h-64 space-y-1 overflow-y-auto lg:max-h-none">
            {bankTopics.map((t, i) => <button key={t.id} onClick={() => select(i)} aria-current={i === selected ? 'step' : undefined}
              className={`w-full rounded-lg p-3 text-left text-sm transition-colors ${i === selected ? 'bg-primary-500/20 text-primary-200' : 'text-gray-300 hover:bg-dark-700'}`}>
              <span className="block text-xs text-gray-400">{t.category} · {t.minutes} dk</span>
              <span className="mt-1 block">{i + 1}. {t.title}{t.checklist.every((_, n) => notebook[t.id]?.checked.includes(n)) ? ' ✓' : ''}</span>
            </button>)}
          </nav>
        </aside>

        <article className="min-w-0 rounded-xl border border-dark-700 bg-dark-800 p-5 sm:p-7">
          <div className="flex flex-wrap items-center justify-between gap-4">
            <h2 className="text-2xl font-bold">{topic.title}</h2>
            <label className="text-sm text-gray-300">Anlatım seviyesi
              <select value={level} onChange={e => setLevel(e.target.value as StudyLevel)} className="input-field mt-1">
                <option>Başlangıç</option><option>Orta</option><option>İleri</option>
              </select>
            </label>
          </div>
          <p className="mt-5 rounded-lg border border-sky-500/20 bg-sky-500/10 p-4 leading-7 text-gray-200">{topic.notes[level]}</p>

          <section className="mt-6">
            <h3 className="font-semibold text-primary-300">Mülakat sorusu</h3>
            <p className="mt-2 text-lg leading-7">{topic.question}</p>
            <label htmlFor="interview-answer" className="mt-4 block text-sm text-gray-300">Kendi yanıtın — önce 2 dakika sesli anlat, sonra not al</label>
            <textarea id="interview-answer" value={work.draft} onChange={e => update({ draft: e.target.value })} rows={6} className="input-field mt-2 resize-y" placeholder="Varsayımım… Seçtiğim çözüm… Hata durumunda… Nasıl test ederim…" />
            <p role="status" className={`mt-2 text-xs ${saved ? 'text-gray-400' : 'text-amber-300'}`}>{saved ? 'Yanıtlar ve işaretler bu tarayıcıda saklanır; cihazlar arasında eşitlenmez. Metin otomatik puanlanmaz.' : 'Tarayıcı kaydı başarısız. Sayfadan ayrılmadan yanıtını kopyala.'}</p>
            <button onClick={() => setRevealed(!revealed)} aria-expanded={revealed} aria-controls="answer-guide" className="mt-4 rounded-lg border border-primary-500/40 px-4 py-2 text-sm text-primary-200 hover:bg-primary-500/10">{revealed ? 'Yanıt rehberini gizle' : 'Yanıt rehberini göster'}</button>
            {revealed && <div id="answer-guide" className="mt-3 rounded-lg bg-dark-900 p-4"><p className="leading-7 text-gray-300">{topic.answer}</p><p className="mt-3 text-sm text-amber-200">Derinleştirme: {topic.followUp}</p></div>}
          </section>

          <section className="mt-6 rounded-lg border border-dark-700 p-4">
            <h3 className="font-semibold">Kısa kontrol</h3>
            <p className="my-3 text-sm leading-6 text-gray-300">{topic.quiz.question}</p>
            <div className="space-y-2">{topic.quiz.options.map((option, i) => <button key={option} aria-pressed={choice === i} onClick={() => setChoice(i)} className={`block w-full rounded-lg border px-3 py-2 text-left text-sm ${choice === i ? 'border-primary-400 bg-primary-500/10' : 'border-dark-700 hover:bg-dark-700'}`}>{option}</button>)}</div>
            {choice !== null && <p role="status" className={`mt-3 text-sm leading-6 ${choice === topic.quiz.correct ? 'text-emerald-300' : 'text-amber-300'}`}>{choice === topic.quiz.correct ? 'Doğru. ' : 'Tekrar düşün. '}{topic.quiz.explanation}</p>}
          </section>

          <section className="mt-6">
            <h3 className="font-semibold text-primary-300">Uygulama ve öz değerlendirme</h3>
            <p className="my-3 text-sm leading-7 text-gray-300">{topic.exercise}</p>
            <div className="space-y-3">{topic.checklist.map((item, i) => <label key={item} className="flex items-start gap-3 text-sm leading-6 text-gray-300"><input type="checkbox" checked={work.checked.includes(i)} onChange={e => update({ checked: e.target.checked ? [...work.checked, i] : work.checked.filter(n => n !== i) })} className="mt-1 h-4 w-4 shrink-0" />{item}</label>)}</div>
            {topic.source && <a className="mt-5 inline-block text-sm text-primary-300 underline" href={topic.source.url} target="_blank" rel="noreferrer">Resmî kaynak: {topic.source.label}</a>}
          </section>
          <div className="mt-7 flex justify-between gap-4 border-t border-dark-700 pt-4">
            <button disabled={selected === 0} onClick={() => select(selected - 1)} className="rounded-lg px-3 py-2 text-sm hover:bg-dark-700 disabled:opacity-30">← Önceki konu</button>
            <button disabled={selected === bankTopics.length - 1} onClick={() => select(selected + 1)} className="btn-primary text-sm disabled:opacity-30">Sonraki konu →</button>
          </div>
        </article>
      </div>
      <section className="mt-7 rounded-xl border border-dark-700 p-5">
        <h2 className="font-semibold">45 dakikalık deneme mülakatı</h2>
        <p className="mt-2 text-sm leading-7 text-gray-300">5 dk kendini ve bir projeyi anlat · 10 dk Java/SQL · 15 dk transfer sistemi tasarla · 10 dk Redis/Kafka kesinti senaryosu · 5 dk test ve ekip iletişimi. Yanıtını kaydet, eksik kalan kontrol maddelerini ertesi gün tekrar et. Süreler çalışma önerisidir.</p>
      </section>
    </main>
  )
}
