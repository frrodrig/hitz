pluginManagement {
    includeBuild("build-logic")
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
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "hitz"

include(":app")
include(":core:model")
include(":core:domain")
include(":core:data")
include(":core:designsystem")
include(":core:testing")
include(":feature:path")
include(":feature:lesson")
include(":feature:profile")
include(":feature:leaderboard")
include(":feature:shop")
include(":feature:onboarding")
