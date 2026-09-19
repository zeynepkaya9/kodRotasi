import { Routes, Route, Navigate } from 'react-router-dom'
import SessionGate from './features/auth/SessionGate'
import Navbar from './components/Navbar'
import LandingPage from './features/auth/LandingPage'
import LoginPage from './features/auth/LoginPage'
import RegisterPage from './features/auth/RegisterPage'
import DashboardRedirect from './features/auth/DashboardRedirect'
import OnboardingPage from './features/onboarding/OnboardingPage'
import ProjectSelectPage from './features/onboarding/ProjectSelectPage'
import WorkspacePage from './features/workspace/WorkspacePage'
import ProgressPage from './features/progress/ProgressPage'

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  return <SessionGate>{children}</SessionGate>
}

export default function App() {
  return (
    <div className="min-h-screen bg-dark-900">
      <Navbar />
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/dashboard" element={
          <ProtectedRoute><DashboardRedirect /></ProtectedRoute>
        } />
        <Route path="/onboarding" element={
          <ProtectedRoute><OnboardingPage /></ProtectedRoute>
        } />
        <Route path="/projects" element={
          <ProtectedRoute><ProjectSelectPage /></ProtectedRoute>
        } />
        <Route path="/workspace" element={
          <ProtectedRoute><WorkspacePage /></ProtectedRoute>
        } />
        <Route path="/progress" element={
          <ProtectedRoute><ProgressPage /></ProtectedRoute>
        } />
      </Routes>
    </div>
  )
}
