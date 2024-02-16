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

//Adaptador para representar cada elemento de la lista
class MyAdapter(private var dataList: List<Vivienda>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    //Se llama cuando se crea un nuevo ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //Llama al archivo item_layout y coloca la vista en el diseño.
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return MyViewHolder(view)
    }

    // Se llama cuando se debe asignar datos a un ViewHolder
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Obtiene el objeto de datos correspondiente a la posición
        val item = dataList[position]

        // Asigna la descripción del objeto al TextView en el ViewHolder
        holder.textViewItem.text = item.descripcion

        // Obtiene los Id de los recursos para las imágenes de carga y de error
        val cargandoImagen = holder.itemView.context.resources.getIdentifier("loading_image", "drawable", holder.itemView.context.packageName)
        val imagenNoEncontradaId = holder.itemView.context.resources.getIdentifier("robot_no_encontrado", "drawable", holder.itemView.context.packageName)

        // Define un Callback para manejar la carga de imágenes
        val imageLoadListener = object : com.squareup.picasso.Callback {
            override fun onSuccess() { // Si se carga la imagen
                Log.w("Imagen", "La imagen se cargo correctamente")
            }
            override fun onError(e: Exception?) { // Si no se carga la imagen
                // Error al cargar la imagen, establece la imagen de error
                holder.imageView.setImageDrawable(null)
                Picasso.get()
                    .load(imagenNoEncontradaId)
                    .fit()
                    .centerCrop()
                    .into(holder.imageView)
            }
        }
        // Carga la imagen principal utilizando Picasso
        Picasso.get()
            .load(item.img)
            .fit()
            .centerCrop()
            .placeholder(cargandoImagen)
            //Se carga el callback cuando se carga la imagen en el xml
            .into(holder.imageView, imageLoadListener)

        // Carga el logo adicional basado en la URL del objeto
        if (item.url.contains("remax.com.ec")) {
            val resourceId = holder.itemView.context.resources.getIdentifier("remax_logo", "drawable", holder.itemView.context.packageName)
            Picasso.get()
                .load(resourceId)
                .fit()
                .centerCrop()
                .into(holder.imageLogo)
        } else if (item.url.contains("icasas.ec")) {
            val resourceId = holder.itemView.context.resources.getIdentifier("icasas_logo", "drawable", holder.itemView.context.packageName)
            Picasso.get()
                .load(resourceId)
                .fit()
                .centerCrop()
                .into(holder.imageLogo)
        }

        // Configura un OnClickListener para abrir la URL asociada al hacer clic en el elemento
        holder.itemView.setOnClickListener { itemView ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
            itemView.context.startActivity(intent) //Se abre el link asociado (URL) para navegar en internet al sitio
        }
    }

    // getItemCount devuelve la cantidad total de elementos en el conjunto de datos
    override fun getItemCount(): Int {
        return dataList.size
    }

    // Actualiza el conjunto de datos y notifica al adaptador del cambio
    fun updateData(newDataList: List<Vivienda>) {
        dataList = newDataList
        notifyDataSetChanged()
    }

    // Se establece un ViewHolder personalizado
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Define las vistas dentro del ViewHolder
        val textViewItem: TextView = itemView.findViewById(R.id.textViewItem)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val imageLogo: ImageView = itemView.findViewById(R.id.imageLogo)
    }

}
