import java.io.FileOutputStream
import java.net.URI
import java.util.Locale

rootProject.name = "SweetAutoResidence"

if (File("neoworld").exists()) {
    //include(":neoworld")
}
fun modrinth(resource: String, version: String, file: String): String {
    return "https://api.modrinth.com/maven/maven/modrinth/$resource/$version/$file"
}
fun download(pair: Pair<String, File>) {
    val input = URI(pair.first).toURL().openStream()
    input.use {
        val tmpFile = pair.second.parentFile.resolve(pair.second.name + ".tmp")
        FileOutputStream(tmpFile).use { out ->
            val buffer = ByteArray(1024 * 1024)
            var length: Int
            while (it.read(buffer).also { length = it } != -1) {
                out.write(buffer, 0, length)
            }
        }
        tmpFile.renameTo(pair.second)
    }
}
val libs = settingsDir.absoluteFile.resolve("libs").also { it.mkdirs() }
val dominion = libs.resolve("Dominion.jar")
if (!dominion.exists()) {
    val version = "4.8.3-release"
    val url = "https://github.com/LunaDeerMC/Dominion/releases/download/v$version/Dominion-$version-full.jar"
    println("Downloading Dominion-v$version...")
    if (Locale.getDefault().country == "CN") {
        download("https://ghproxy.imciel.com/$url" to dominion)
    } else {
        download(url to dominion)
    }
}
