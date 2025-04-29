package com.example.al_quran

import AyahAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.*

class SurahDetailsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AyahAdapter
    private lateinit var arabicNameText: TextView
    private lateinit var englishNameText: TextView
    private lateinit var revelationText: TextView

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private val api: QuranApi by lazy { RetrofitClient.instance }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surah_details)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AyahAdapter()
        recyclerView.adapter = adapter

        arabicNameText = findViewById(R.id.textArabicName)
        englishNameText = findViewById(R.id.textEnglishName)
        revelationText = findViewById(R.id.textRevelation)

        val imgProfile = findViewById<ImageView>(R.id.imgProfileDetail)
        imgProfile.setOnClickListener { showProfileDialog() }

        val surahId = intent.getIntExtra("SURAH_ID", 1)
        val surahNameArabic = intent.getStringExtra("SURAH_ARABIC_NAME")
        val surahNameEnglish = intent.getStringExtra("SURAH_NAME")
        val surahType = intent.getStringExtra("SURAH_TYPE")
        val ayahCount = intent.getIntExtra("SURAH_AYAH_COUNT", 0)

        title = surahNameEnglish ?: "Surah"
        arabicNameText.text = surahNameArabic
        englishNameText.text = surahNameEnglish
        revelationText.text = "$surahType • $ayahCount Ayat"

        fetchSurahDetails(surahId)
    }

    private fun fetchSurahDetails(surahId: Int) {
        uiScope.launch {
            try {
                val arabicDeferred = async(Dispatchers.IO) {
                    api.getSurahDetails(surahId, "ar.alafasy")
                }
                val translationDeferred = async(Dispatchers.IO) {
                    api.getSurahDetails(surahId, "id.indonesian")
                }

                val arabicResponse = arabicDeferred.await()
                val translationResponse = translationDeferred.await()

                val combined = arabicResponse.data.ayahs.zip(translationResponse.data.ayahs).map { (ar, tr) ->
                    Ayah(
                        number = ar.number,
                        numberInSurah = ar.numberInSurah,
                        arabicText = ar.text,
                        translationText = tr.text,
                        audioUrl = ar.audio
                    )
                }

                adapter.submitList(combined)

            } catch (e: Exception) {
                Toast.makeText(this@SurahDetailsActivity, "Gagal memuat ayat", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProfileDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_profile, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val account = GoogleSignIn.getLastSignedInAccount(this)
        val name = account?.displayName ?: "Unknown User"
        val email = account?.email ?: ""

        dialogView.findViewById<TextView>(R.id.textProfileName).text = name
        dialogView.findViewById<TextView>(R.id.textProfileEmail).text = email

        dialogView.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            signOut()
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
