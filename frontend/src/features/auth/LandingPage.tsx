import { Link } from 'react-router-dom'

export default function LandingPage() {
  return (
    <div className="min-h-screen">
      {/* Hero */}
      <div className="max-w-5xl mx-auto px-4 py-24 text-center">
        <h1 className="text-5xl font-bold mb-6 leading-tight">
          Java ve Spring Boot'u
          <br />
          <span className="bg-gradient-to-r from-primary-400 to-primary-600 bg-clip-text text-transparent">
            Gercek Projelerle
          </span>
          {' '}Ogren
        </h1>
        <p className="text-xl text-gray-400 mb-10 max-w-2xl mx-auto">
          Sadece kod yazmak degil — her satirin neden o sekilde yazildigini, alternatif yaklasimlarin ne oldugunu
          ve gercek projelerde hangisinin tercih edildigini ogren.
        </p>
        <div className="flex gap-4 justify-center">
          <Link to="/register" className="btn-primary text-lg px-8 py-3">
            Ucretsiz Basla
          </Link>
          <Link to="/login" className="btn-secondary text-lg px-8 py-3">
            Giris Yap
          </Link>
        </div>
      </div>

      {/* Features */}
      <div className="max-w-5xl mx-auto px-4 pb-24">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div className="card text-center">
            <div className="text-4xl mb-4">&#127919;</div>
            <h3 className="text-lg font-semibold mb-2">Seviye Bazli Ogrenme</h3>
            <p className="text-gray-400 text-sm">
              Baslangic, Orta, Ileri — seviyene gore aciklamalar ve gorevler otomatik ayarlanir.
            </p>
          </div>
          <div className="card text-center">
            <div className="text-4xl mb-4">&#128161;</div>
            <h3 className="text-lg font-semibold mb-2">Akıllı İpucu Sistemi</h3>
            <p className="text-gray-400 text-sm">
              4 kademeli ipucu: kucuk hint, yonlendirme, ornek kod, tam cozum.
              Takildigin yerde destek alirsin.
            </p>
          </div>
          <div className="card text-center">
            <div className="text-4xl mb-4">&#129504;</div>
            <h3 className="text-lg font-semibold mb-2">"Neden?" Motoru</h3>
            <p className="text-gray-400 text-sm">
              Her satir icin: Neden dogru? Alternatif ne? Gercek projede hangi yaklasim tercih edilir?
            </p>
          </div>
          <div className="card text-center">
            <div className="text-4xl mb-4">&#127959;</div>
            <h3 className="text-lg font-semibold mb-2">Gercek Projeler</h3>
            <p className="text-gray-400 text-sm">
              Banka, E-ticaret, Kutuphane — gercek projeler gelistirerek uygulamali ogrenme.
            </p>
          </div>
          <div className="card text-center">
            <div className="text-4xl mb-4">&#127942;</div>
            <h3 className="text-lg font-semibold mb-2">XP ve Rozetler</h3>
            <p className="text-gray-400 text-sm">
              Gorevileri tamamla, XP kazan, seri olustur ve rozetler topla.
            </p>
          </div>
          <div className="card text-center">
            <div className="text-4xl mb-4">&#128218;</div>
            <h3 className="text-lg font-semibold mb-2">Kavram Kartlari</h3>
            <p className="text-gray-400 text-sm">
              Her kavram icin gercek hayat benzetmesi, yanlis/dogru ornek ve "neden boyle?" aciklamasi.
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}
