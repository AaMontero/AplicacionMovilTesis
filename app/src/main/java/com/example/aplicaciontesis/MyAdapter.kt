import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicaciontesis.R
import com.example.aplicaciontesis.dataClass.Vivienda
import com.squareup.picasso.Picasso

class MyAdapter(private var dataList: List<Vivienda>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = dataList[position]
        holder.textViewItem.text = item.descripcion
        Picasso.get()
            .load(item.img) // Reemplaza "imageUrl" con el campo que contiene la URL de la imagen en tu objeto de datos
            //.placeholder(R.drawable.placeholder) // Puedes establecer un placeholder mientras se carga la imagen
            //.error(R.drawable.error) // Puedes establecer una imagen de error en caso de que la carga falle
            .fit() // Ajusta la imagen al tamaño del ImageView
            .centerCrop() // Centra y recorta la imagen según sea necesario
            .into(holder.imageView)

        holder.itemView.setOnClickListener { itemView ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
            itemView.context.startActivity(intent)

        }
        // Aquí puedes manejar la carga de imágenes si es necesario
        // holder.imageView.setImageResource(item.img)

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun updateData(newDataList: List<Vivienda>) {
        dataList = newDataList
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewItem: TextView = itemView.findViewById(R.id.textViewItem)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
}
