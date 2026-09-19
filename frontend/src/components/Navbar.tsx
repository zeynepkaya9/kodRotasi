import { Link, useNavigate } from 'react-router-dom'
import { useAppStore } from '../lib/store'

export default function Navbar() {
  const { user, logout, totalXp, currentStreak } = useAppStore()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <nav className="bg-dark-800 border-b border-dark-700 px-6 py-3">
      <div className="max-w-7xl mx-auto flex flex-wrap gap-3 items-center justify-between">
        <Link to="/" className="flex items-center gap-2">
          <span aria-hidden="true" className="h-9 w-9 rounded-xl bg-gradient-to-br from-primary-400 to-sky-500 text-dark-900 font-black flex items-center justify-center shadow-lg shadow-primary-500/20">{'{↗}'}</span>
          <span className="leading-tight">
            <span className="block text-xl font-bold bg-gradient-to-r from-primary-400 to-sky-400 bg-clip-text text-transparent">KodRotası</span>
            <span className="hidden sm:block text-[10px] uppercase tracking-[0.16em] text-gray-500">Java & Spring öğrenme yolu</span>
          </span>
        </Link>

        <div className="flex flex-wrap items-center gap-3 sm:gap-6">
          {user ? (
            <>
              <Link to="/dashboard" className="text-gray-300 hover:text-white transition-colors">
                Workspace
              </Link>
              <Link to="/projects" className="text-gray-300 hover:text-white">Projeler</Link>
              <Link to="/progress" className="text-gray-300 hover:text-white transition-colors">
                Ilerleme
              </Link>
              <div className="flex items-center gap-3 text-sm">
                <span className="text-primary-400 font-medium">{totalXp} XP</span>
                {currentStreak > 0 && (
                  <span className="text-warning-500 font-medium">{currentStreak} &#128293;</span>
                )}
              </div>
              <div className="flex items-center gap-3">
                <span className="text-sm text-gray-400">{user.displayName}</span>
                <button onClick={handleLogout} className="text-sm text-gray-400 hover:text-danger-500 transition-colors">
                  Cikis
                </button>
              </div>
            </>
          ) : (
            <>
              <Link to="/login" className="text-gray-300 hover:text-white transition-colors">Giris</Link>
              <Link to="/register" className="btn-primary text-sm">Kayit Ol</Link>
            </>
          )}
        </div>
      </div>
    </nav>
  )
}
