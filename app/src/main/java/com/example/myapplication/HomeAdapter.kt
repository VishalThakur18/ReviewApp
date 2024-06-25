
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.HomeCards
import com.example.myapplication.R

class HomeAdapter(private val cardList: MutableList<HomeCards>) : RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_home_choice, parent, false)
        return MyViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        return cardList.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = cardList[position]// Use setBackgroundColor to set background color
        holder.cardTitle.text = currentItem.cardTitle  // Use description instead of cardDescription
        holder.dishImage.setImageResource(currentItem.dishImage)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardTitle: TextView = itemView.findViewById(R.id.cardHomeTitle)
        val dishImage: ImageView = itemView.findViewById(R.id.dishHomeImage)
    }

}
