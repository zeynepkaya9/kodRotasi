import { Navigate } from 'react-router-dom'
import { useAppStore } from '../../lib/store'
export default function DashboardRedirect() {
  const { learningPath, activeTasks } = useAppStore()
  return <Navigate to={!learningPath ? '/onboarding' : activeTasks.length ? '/workspace' : '/projects'} replace />
}
