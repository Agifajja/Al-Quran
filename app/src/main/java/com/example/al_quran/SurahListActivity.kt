package com.example.al_quran

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.al_quran.databinding.ActivitySurahListBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SurahListActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySurahListBinding
    private lateinit var adapter: SurahAdapter
    private lateinit var surahList: List<Surah>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurahListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerView
        adapter = SurahAdapter { surah -> openSurahDetails(surah) }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Set listener untuk tombol profil
        binding.imgProfile.setOnClickListener {
            showProfileDialog()
        }

        // Fetch data surat
        fetchSurahList()
    }

    private fun fetchSurahList() {
        RetrofitClient.instance.getSurahList().enqueue(object : Callback<SurahListResponse> {
            override fun onResponse(
                call: Call<SurahListResponse>,
                response: Response<SurahListResponse>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()
                    data?.data?.let {
                        surahList = it
                        adapter.submitList(it)
                    }
                } else {
                    Toast.makeText(this@SurahListActivity, "Gagal memuat data (response error)", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SurahListResponse>, t: Throwable) {
                Toast.makeText(this@SurahListActivity, "Gagal memuat data (jaringan)", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Kirim data lengkap ke detail activity
    private fun openSurahDetails(surah: Surah) {
        val intent = Intent(this, SurahDetailsActivity::class.java)
        intent.putExtra("SURAH_ID", surah.number)
        intent.putExtra("SURAH_NAME", surah.englishName)
        intent.putExtra("SURAH_TYPE", surah.revelationType)
        intent.putExtra("SURAH_AYAH_COUNT", surah.numberOfAyahs)
        startActivity(intent)
    }

    private fun showProfileDialog() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        val name = account?.displayName ?: "Unknown User"
        val email = account?.email ?: ""

        AlertDialog.Builder(this)
            .setTitle(name)
            .setMessage(email)
            .setPositiveButton("Logout") { _, _ -> signOut() }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun signOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        GoogleSignIn.getClient(this, gso)
            .signOut()
            .addOnCompleteListener {
                Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
    }
}
