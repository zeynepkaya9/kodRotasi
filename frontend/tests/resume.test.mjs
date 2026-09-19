import { build } from 'esbuild'
import { test, beforeEach } from 'node:test'
import assert from 'node:assert/strict'
const memory = new Map()
globalThis.localStorage = { getItem: key => memory.get(key) ?? null, setItem: (key, value) => memory.set(key, String(value)), removeItem: key => memory.delete(key) }
const compiled = await build({ entryPoints: ['src/lib/store.ts'], bundle: true, platform: 'browser', format: 'esm', write: false, define: { 'process.env.NODE_ENV': '"production"' } })
const { useAppStore: store, selectResumeStep } = await import(`data:text/javascript;base64,${Buffer.from(compiled.outputFiles[0].text).toString('base64')}`)
const alice = { email: 'alice@example.com', displayName: 'Alice' }
const bob = { email: 'bob@example.com', displayName: 'Bob' }
beforeEach(() => { store.getState().logout(); memory.clear() })
const tasks = [{ id: 1, steps: [{ id: 1, status: 'COMPLETED' }, { id: 2, status: 'ACTIVE' }, { id: 3, status: 'LOCKED' }] }]
test('logout and login retain only the correct account draft and selection', () => {
  store.getState().setUser(alice)
  store.getState().saveCodeForStep(2, 'class Draft {}')
  store.getState().setActiveStepId(2)
  store.getState().logout()
  assert.equal(store.getState().getCodeForStep(2), undefined)
  store.getState().setUser(bob)
  assert.equal(store.getState().getCodeForStep(2), undefined)
  store.getState().setUser(alice)
  assert.equal(store.getState().getCodeForStep(2), 'class Draft {}')
  assert.equal(store.getState().activeStepId, 2)
})
test('server code restores on a new browser, including deliberately empty drafts', () => {
  store.getState().setUser(alice)
  store.getState().setActiveTasks([{ id: 1, steps: [{ id: 2, savedCode: '', savedAt: new Date().toISOString() }] }])
  assert.equal(store.getState().getCodeForStep(2), '')
})
test('older server response cannot replace newer local typing', () => {
  store.getState().setUser(alice)
  store.getState().saveCodeForStep(2, 'new')
  store.getState().setActiveTasks([{ id: 1, steps: [{ id: 2, savedCode: 'old', savedAt: '2020-01-01T00:00:00Z' }] }])
  assert.equal(store.getState().getCodeForStep(2), 'new')
})
test('resume selects an accessible step, including fully completed projects', () => {
  assert.equal(selectResumeStep(tasks, null), 2)
  assert.equal(selectResumeStep(tasks, 3), 2)
  assert.equal(selectResumeStep(tasks, 1), 1)
  assert.equal(selectResumeStep([{ id: 1, steps: [{ id: 1, status: 'COMPLETED' }] }], null), 1)
  assert.equal(selectResumeStep([], 2), null)
})
test('a new session reloads XP from the server instead of retaining another account', () => {
  store.getState().setUser(alice)
  store.getState().setProgress({ totalXp: 100, currentStreak: 3 })
  store.getState().setUser(bob)
  assert.equal(store.getState().totalXp, 0)
  assert.equal(store.getState().sessionReady, false)
})
