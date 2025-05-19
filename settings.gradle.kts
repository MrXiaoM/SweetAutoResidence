import java.io.FileOutputStream
import java.net.URL

rootProject.name = "SweetAutoResidence"

if (File("neoworld").exists()) {
    include(":neoworld")
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

val residence = settingsDir.absoluteFile.resolve("libs/Residence.jar")
if (!residence.exists()) {
    println("Downloading Residence-5.1.5.2...")
    download("https://zrips.net/Residence/download.php?file=Residence5.1.5.2.jar" to residence)
}
val dominion = settingsDir.absoluteFile.resolve("libs/Dominion.jar")
if (!dominion.exists()) {
    println("Downloading Dominion-v4.2.0-beta...")
    download(modrinth("lunadeer-dominion", "v4.2.0-beta", "Dominion-4.2.0-beta-lite.jar") to dominion)
}
