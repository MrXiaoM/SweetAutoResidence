import java.io.FileOutputStream
import java.net.URL

rootProject.name = "SweetAutoResidence"

if (File("neoworld").exists()) {
    include(":neoworld")
}
val residence = File("libs/Residence.jar")
if (!residence.exists()) {
    println("Downloading Residence-5.1.5.2...")
    val input = URL("https://zrips.net/Residence/download.php?file=Residence5.1.5.2.jar").openStream()
    input.use {
        FileOutputStream(residence).use { out ->
            val buffer = ByteArray(1024 * 1024)
            var length: Int
            while (it.read(buffer).also { length = it } != -1) {
                out.write(buffer, 0, length)
            }
        }
    }
}
