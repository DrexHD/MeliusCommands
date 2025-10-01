plugins {
    id("dev.kikugie.stonecutter")
    id("org.jetbrains.changelog") version "2.2.1"
}
stonecutter active "1.21.6"

changelog {
    path = rootProject.file("CHANGELOG.md").path
}