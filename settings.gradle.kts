pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ComposeCleanBlueprint"
include(":app")
include(":core:common")
include(":core:domain")
include(":core:navigation")
include(":core:network")
include(":core:database")
include(":core:data")
include(":core:designsystem")
include(":feature:users-mvvm")
include(":feature:users-mvi")
