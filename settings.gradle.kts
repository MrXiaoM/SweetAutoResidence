rootProject.name = "SweetAutoResidence"

if (File("neoworld").exists()) {
    include(":neoworld")
}
include(":res-ext")
