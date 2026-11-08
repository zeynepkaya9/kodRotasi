export interface TopicWork { draft: string; checked: number[] }
export type Notebook = Record<string, TopicWork>
export function notebookKey(email?: string) { return `kodrotasi:bank-interview:v1:${email || 'guest'}` }
export function readNotebook(key: string): Notebook {
  try {
    const value: unknown = JSON.parse(localStorage.getItem(key) || '{}')
    if (!value || typeof value !== 'object' || Array.isArray(value)) return {}
    const result: Notebook = {}
    for (const [id, work] of Object.entries(value)) {
      if (work && typeof work === 'object' && typeof work.draft === 'string' && Array.isArray(work.checked)) {
        result[id] = { draft: work.draft, checked: work.checked.filter((n: unknown) => Number.isInteger(n) && Number(n) >= 0 && Number(n) < 3) }
      }
    }
    return result
  } catch { return {} }
}

export function saveNotebook(key: string, notebook: Notebook): boolean {
  try { localStorage.setItem(key, JSON.stringify(notebook)); return true } catch { return false }
}
