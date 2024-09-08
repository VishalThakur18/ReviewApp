import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.HomeCards
import com.example.myapplication.R
import com.google.firebase.firestore.FirebaseFirestore
import model.DishReview

class HomeAdapter(
    private var cardList: List<HomeCards>,
    private var context: Context
    ) : RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardTitle: TextView = itemView.findViewById(R.id.cardHomeTitle)
        val dishImage: ImageView = itemView.findViewById(R.id.dishHomeImage)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_home_choice, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = cardList[position]// Use setBackgroundColor to set background color
        holder.cardTitle.text = currentItem.dishName  // Use description instead of cardDescription
        Glide.with(holder.itemView.context)
            .load(currentItem.imageUrl)
            .into(holder.dishImage)
    }
    override fun getItemCount(): Int {
        return cardList.size
    }

    fun updateReviews(newReviews: List<HomeCards>) {
         cardList= newReviews
        notifyDataSetChanged()
    }


}
