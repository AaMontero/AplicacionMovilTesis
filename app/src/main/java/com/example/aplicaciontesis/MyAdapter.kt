import android.content.Intent
import android.net.Uri
import android.util.Log
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
        val cargandoImagen = holder.itemView.context.resources.getIdentifier("loading_image", "drawable", holder.itemView.context.packageName)
        val imagenNoEncontradaId =  holder.itemView.context.resources.getIdentifier("robot_no_encontrado", "drawable", holder.itemView.context.packageName)
        val imageLoadListener = object : com.squareup.picasso.Callback {
            override fun onSuccess() {
                // La imagen se cargó correctamente
                // Puedes realizar otras operaciones aquí si es necesario
            }

            override fun onError(e: Exception?) {
                // Error al cargar la imagen, no hagas nada o realiza acciones adicionales si es necesario
                holder.imageView.setImageDrawable(null)
                Picasso.get()
                    .load(imagenNoEncontradaId)
                    .fit()
                    .centerCrop()
                    .into(holder.imageView)
            }
        }
        Picasso.get()
            .load(item.img)
            .fit()
            .centerCrop()
            .placeholder(cargandoImagen)
            .into(holder.imageView, imageLoadListener)
        if(item.url.contains("remax.com.ec")){
            val resourceId = holder.itemView.context.resources.getIdentifier("remax_logo", "drawable", holder.itemView.context.packageName)
            Picasso.get()
                .load(resourceId)
                .fit()
                .centerCrop()
                .into(holder.imageLogo)

        }else if(item.url.contains("icasas.ec")){
            val resourceId = holder.itemView.context.resources.getIdentifier("icasas_logo", "drawable", holder.itemView.context.packageName)
            Picasso.get()
                .load(resourceId)
                .fit()
                .centerCrop()
                .into(holder.imageLogo)
        }
        holder.itemView.setOnClickListener { itemView ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
            itemView.context.startActivity(intent)
        }

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
        val imageLogo: ImageView = itemView.findViewById(R.id.imageLogo)
    }
}
