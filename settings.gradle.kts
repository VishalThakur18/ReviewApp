pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url =uri("https://jcenter.bintray.com")
            maven { url = uri("https://www.jitpack.io" ) }
        }

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url =uri("https://jcenter.bintray.com")
            maven { url = uri("https://www.jitpack.io" ) }
        }
    }
}

rootProject.name = "My Application"
include(":app")

