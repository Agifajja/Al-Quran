package com.example.al_quran

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SurahListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SurahAdapter
    private lateinit var surahList: List<Surah>

    // Sticky header views
    private lateinit var textStickyArabicName: TextView
    private lateinit var textStickyEnglishName: TextView
    private lateinit var textStickyRevelation: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surah_list)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = SurahAdapter { surah -> openSurahDetails(surah) }
        recyclerView.adapter = adapter

        // Sticky header view references
        textStickyArabicName = findViewById(R.id.textStickyArabicName)
        textStickyEnglishName = findViewById(R.id.textStickyEnglishName)
        textStickyRevelation = findViewById(R.id.textStickyRevelation)

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
                    Log.d("API_RESPONSE", "Status: ${data?.status}, Total Surah: ${data?.data?.size}")

                    data?.data?.let {
                        surahList = it
                        adapter.submitList(it)

                        // Inisialisasi sticky header dengan surah pertama
                        updateStickyHeader(it[0])

                        setupScrollListener()
                    }
                } else {
                    Log.e("API_ERROR", "Response tidak sukses: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@SurahListActivity, "Gagal memuat data (response gagal)", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SurahListResponse>, t: Throwable) {
                Log.e("API_ERROR", "Retrofit gagal: ${t.message}")
                Toast.makeText(this@SurahListActivity, "Gagal memuat data (jaringan)", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val position = layoutManager.findFirstVisibleItemPosition()

                if (position != RecyclerView.NO_POSITION && position < surahList.size) {
                    updateStickyHeader(surahList[position])
                }
            }
        })
    }

    private fun updateStickyHeader(surah: Surah) {
        textStickyArabicName.text = surah.name
        textStickyEnglishName.text = surah.englishName
        textStickyRevelation.text = "${surah.revelationType} • ${surah.numberOfAyahs} Ayat"
    }

    private fun openSurahDetails(surah: Surah) {
        val intent = Intent(this, SurahDetailsActivity::class.java)
        intent.putExtra("SURAH_ID", surah.number)
        intent.putExtra("SURAH_NAME", surah.englishName)
        intent.putExtra("SURAH_TYPE", surah.revelationType)
        startActivity(intent)
    }
}
