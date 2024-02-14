import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class Utils {

    companion object {

        fun uriToFile(context: Context, uri: Uri): File? {
            val inputStream: InputStream?
            var file: File? = null
            try {
                inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    file = File(context.cacheDir, "temp_image.png")
                    copyInputStreamToFile(inputStream, file)
                    inputStream.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return file
        }

        private fun copyInputStreamToFile(inputStream: InputStream, file: File) {
            try {
                val outputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}