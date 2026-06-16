plugins {
    id("dev.kikugie.stonecutter")
    id("org.jetbrains.changelog") version "2.2.1"
}
stonecutter active "26.2"

changelog {
    path = rootProject.file("CHANGELOG.md").path
}

stonecutter parameters {

    replacements.string(eval(current.version, "<=1.21.10")) {
        replace("Identifier", "ResourceLocation")
    }
}