# Simple Calculator

Tugas P10 Pemrograman Mobile - SharedPreferences pada Aplikasi Mobile
Prodi STI, Universitas Muhammadiyah Kotabumi
Dosen: Ryan Aji Wijaya, S.Kom., M.M.S.I

Nama  : Kholil Anwar Perkasa
NPM   : 2459201048
Kelas : 5D

Aplikasi kalkulator sederhana (Java, Android) yang mengingat rumus terakhir dan status layar pembuka memakai SharedPreferences.

## Masalah yang diselesaikan
Kalkulator biasa kehilangan rumus yang sedang diketik saat aplikasi ditutup. Dengan SharedPreferences, pengguna bisa melanjutkan hitungan dari terakhir kali ia berhenti.

## Data di SharedPreferences
- `last_expression` (String): rumus terakhir, mis. `78-65`
- `calculator_opened` (boolean): menandai tombol "Buka Kalkulator" sudah ditekan
- Alasan: datanya kecil dan berbentuk key-value

## Data yang tidak disimpan
- Hasil sementara: dihitung ulang dari rumus
- Riwayat perhitungan: belum ada fitur ini, jadi database belum diperlukan

## Cara kerja
- `PrefsManager.java`: pembungkus SharedPreferences, data ditulis dengan `apply()`
- `mainActivity.java`: menyimpan rumus di `updateDisplay()` dan membacanya di `onResume()`

## Bukti
Setelah Force Stop (Settings > Apps > Simple Calculator), aplikasi dibuka lagi dan rumus terakhir serta layar kalkulator tetap muncul.
