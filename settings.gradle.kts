import java.io.FileOutputStream
import java.net.URL
import java.util.Locale

rootProject.name = "SweetAutoResidence"

if (File("neoworld").exists()) {
    //include(":neoworld")
}
fun modrinth(resource: String, version: String, file: String): String {
    return "https://api.modrinth.com/maven/maven/modrinth/$resource/$version/$file"
}
fun download(pair: Pair<String, File>) {
    val input = URL(pair.first).openStream()
    input.use {
        FileOutputStream(pair.second).use { out ->
            val buffer = ByteArray(1024 * 1024)
            var length: Int
            while (it.read(buffer).also { length = it } != -1) {
                out.write(buffer, 0, length)
            }
        }
    }
}
val libs = settingsDir.absoluteFile.resolve("libs").also { it.mkdirs() }
val residence = libs.resolve("Residence.jar")
if (!residence.exists()) {
    val version = "6.0.1.4"
    println("Downloading Residence-$version...")
    download("https://zrips.net/Residence/download.php?file=Residence$version.jar" to residence)
}
val dominion = libs.resolve("Dominion.jar")
if (!dominion.exists()) {
    val version = "4.7.3-RLS"
    val url = "https://github.com/LunaDeerMC/Dominion/releases/download/v$version/Dominion-$version-full.jar"
    println("Downloading Dominion-v$version...")
    if (Locale.getDefault().country == "CN") {
        download("https://ghproxy.imciel.com/$url" to dominion)
    } else {
        download(url to dominion)
    }
}
