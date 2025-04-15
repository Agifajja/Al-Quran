package com.example.al_quran
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SurahAdapter(
    private val onItemClick: (Surah) -> Unit
) : RecyclerView.Adapter<SurahAdapter.SurahViewHolder>() {

    private val surahList = mutableListOf<Surah>()

    fun submitList(list: List<Surah>) {
        surahList.clear()
        surahList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_surah, parent, false)
        return SurahViewHolder(view)
    }

    override fun onBindViewHolder(holder: SurahViewHolder, position: Int) {
        val surah = surahList[position]
        holder.bind(surah)
    }

    override fun getItemCount(): Int = surahList.size

    inner class SurahViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameArabic: TextView = itemView.findViewById(R.id.textArabicName)
        private val nameEnglish: TextView = itemView.findViewById(R.id.textEnglishName)
        private val revelationType: TextView = itemView.findViewById(R.id.textRevelationType)
        private val ayahCount: TextView = itemView.findViewById(R.id.textAyahCount)

        fun bind(surah: Surah) {
            nameArabic.text = surah.name
            nameEnglish.text = surah.englishName
            revelationType.text = surah.revelationType
            ayahCount.text = "${surah.numberOfAyahs} ayat"

            itemView.setOnClickListener {
                onItemClick(surah)
            }
        }
    }
}
