import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

// Clase de utilidades para funciones comunes relacionadas con archivos y URI
class Utils {

    companion object {

        // Convierte una Uri a un objeto File
        fun uriToFile(context: Context, uri: Uri): File? {
            // Inicializa un InputStream y un objeto File
            val inputStream: InputStream?
            var file: File? = null

            try {
                // Abre un InputStream desde la Uri proporcionada
                inputStream = context.contentResolver.openInputStream(uri)
                // Si el InputStream no es nulo, crea un archivo temporal y copia el contenido
                if (inputStream != null) {
                    // Crea un archivo temporal en el caché
                    file = File(context.cacheDir, "temp_image.png")
                    copyInputStreamToFile(inputStream, file)
                    inputStream.close()
                }
            } catch (e: IOException) { // Manejo de excepciones
                e.printStackTrace()
            }
            // Devuelve el objeto File
            return file
        }

        // Copia el contenido de un InputStream a un archivo
        private fun copyInputStreamToFile(inputStream: InputStream, file: File) {
            try {
                // Inicializa un OutputStream y un búfer de bytes
                val outputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var length: Int

                // Lee del InputStream y escribe en el OutputStream hasta que no haya más datos
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
                outputStream.close()
            } catch (e: IOException) { //Manejo de excepciones
                e.printStackTrace()
            }
        }
    }
}
