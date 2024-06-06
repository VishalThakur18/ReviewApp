import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.homeCards

class homeAdapter(private val cardList: MutableList<homeCards>) : RecyclerView.Adapter<homeAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val backgroundId: View = itemView.findViewById(R.id.cardBack)
        val cardTitle: TextView = itemView.findViewById(R.id.choiceName)
        val cardDescription: TextView = itemView.findViewById(R.id.choiceLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_home_choice, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = cardList[position]
        holder.backgroundId.setBackgroundResource(currentItem.backgroundId) // Use setBackgroundColor to set background color
        holder.cardTitle.text = currentItem.cardTitle // Use title instead of cardTitle
        holder.cardDescription.text = currentItem.cardDescription // Use description instead of cardDescription
    }
}
