import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.al_quran.Ayah
import com.example.al_quran.R

class AyahAdapter : ListAdapter<Ayah, AyahAdapter.AyahViewHolder>(AyahDiffCallback()) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentlyPlayingPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AyahViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ayah, parent, false)
        return AyahViewHolder(view)
    }

    override fun onBindViewHolder(holder: AyahViewHolder, position: Int) {
        val adapterPosition = holder.adapterPosition
        if (adapterPosition == RecyclerView.NO_POSITION) return

        val ayah = getItem(adapterPosition)
        holder.bind(ayah)

        val isPlaying = currentlyPlayingPosition == adapterPosition
        holder.updatePlayButton(isPlaying)

        holder.buttonPlay.setOnClickListener {
            if (currentlyPlayingPosition == adapterPosition) {
                stopAudio()
                currentlyPlayingPosition = null
                notifyItemChanged(adapterPosition)
            } else {
                stopAudio()
                playAudio(ayah.number, holder)
                val prevPosition = currentlyPlayingPosition
                currentlyPlayingPosition = adapterPosition
                prevPosition?.let { notifyItemChanged(it) }
                notifyItemChanged(adapterPosition)
            }
        }
    }

    private fun playAudio(ayahNumber: Int, holder: AyahViewHolder) {
        val audioUrl = "https://cdn.islamic.network/quran/audio/64/ar.alafasy/$ayahNumber.mp3"
        mediaPlayer = MediaPlayer().apply {
            setDataSource(audioUrl)
            prepareAsync()
            setOnPreparedListener {
                start()
                holder.updatePlayButton(true)
            }
            setOnCompletionListener {
                stopAudio()
                notifyItemChanged(currentlyPlayingPosition ?: return@setOnCompletionListener)
                currentlyPlayingPosition = null
            }
        }
    }

    private fun stopAudio() {
        mediaPlayer?.apply {
            stop()
            reset()
            release()
        }
        mediaPlayer = null
    }

    class AyahViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val arabicText: TextView = itemView.findViewById(R.id.textArabic)
        private val translationText: TextView = itemView.findViewById(R.id.textTranslation)
        val buttonPlay: ImageButton = itemView.findViewById(R.id.buttonPlay)

        fun bind(ayah: Ayah) {
            val arabicNumber = convertToArabicNumber(ayah.numberInSurah)
            arabicText.text = "$arabicNumber. ${ayah.arabicText}"
            translationText.text = ayah.translationText
        }

        fun updatePlayButton(isPlaying: Boolean) {
            buttonPlay.setImageResource(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            )
        }

        private fun convertToArabicNumber(number: Int): String {
            val arabicDigits = arrayOf('٠','١','٢','٣','٤','٥','٦','٧','٨','٩')
            return number.toString().map { arabicDigits[it.toString().toInt()] }.joinToString("")
        }
    }

    class AyahDiffCallback : DiffUtil.ItemCallback<Ayah>() {
        override fun areItemsTheSame(oldItem: Ayah, newItem: Ayah): Boolean {
            return oldItem.number == newItem.number
        }

        override fun areContentsTheSame(oldItem: Ayah, newItem: Ayah): Boolean {
            return oldItem == newItem
        }
    }
}
