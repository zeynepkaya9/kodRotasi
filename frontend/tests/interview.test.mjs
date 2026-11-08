import { build } from 'esbuild'
import { test } from 'node:test'
import assert from 'node:assert/strict'
const compile = async path => {
  const built = await build({ entryPoints: [path], bundle: true, platform: 'browser', format: 'esm', write: false })
  return import(`data:text/javascript;base64,${Buffer.from(built.outputFiles[0].text).toString('base64')}`)
}
const { notebookKey, readNotebook, saveNotebook } = await compile('src/features/interview/notebook.ts')
const { bankTopics } = await compile('src/features/interview/bankTopics.ts')
const values = new Map()
globalThis.localStorage = { getItem: key => values.get(key) ?? null, setItem: (key, value) => values.set(key, value) }
test('interview notes restore across reload without crossing account boundaries', () => {
  const a = notebookKey('alice@example.com'), b = notebookKey('bob@example.com')
  assert.equal(saveNotebook(a, { redis: { draft: 'TTL tutarlılık garantisi değildir', checked: [0, 2] } }), true)
  assert.deepEqual(readNotebook(a).redis.checked, [0, 2])
  assert.match(readNotebook(a).redis.draft, /TTL/)
  assert.deepEqual(readNotebook(b), {})
  assert.deepEqual(readNotebook(notebookKey()), {})
})
test('corrupt or blocked browser storage does not crash the study page', () => {
  values.set('bad', '{')
  assert.deepEqual(readNotebook('bad'), {})
  values.set('bad', JSON.stringify({redis:{draft: 'safe', checked: [0, -1, 500, '2']}, kafka: null}))
  assert.deepEqual(readNotebook('bad'), {redis:{draft:'safe',checked:[0]}})
  const original = localStorage.setItem
  localStorage.setItem = () => { throw new Error('quota') }
  try { assert.equal(saveNotebook('full', {}), false) } finally { localStorage.setItem = original }
})
test('all interview questions have valid answer choices and level-specific notes', () => {
  assert.equal(new Set(bankTopics.map(t => t.id)).size, bankTopics.length)
  for (const t of bankTopics) {
    assert.ok(t.quiz.correct >= 0 && t.quiz.correct < t.quiz.options.length, t.id)
    assert.equal(t.checklist.length, 3)
    for (const level of ['Başlangıç', 'Orta', 'İleri']) assert.ok(t.notes[level].length > 30, `${t.id}: ${level}`)
  }
})
