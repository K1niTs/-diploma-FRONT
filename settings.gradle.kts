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
    versionCatalogs {
        create("libs") {
            // Retrofit
            library("retrofit", "com.squareup.retrofit2:retrofit:2.9.0")
            library("retrofit-jackson", "com.squareup.retrofit2:converter-jackson:2.9.0")
            library("okhttp-logging", "com.squareup.okhttp3:logging-interceptor:4.9.0")
            // Material Components (MDC)
            library("material", "com.google.android.material:material:1.9.0")
            // AndroidX
            library("androidx-recyclerview", "androidx.recyclerview:recyclerview:1.3.0")
            library("androidx-appcompat",    "androidx.appcompat:appcompat:1.6.1")
        }
    }
}

rootProject.name = "MusicRental"
include(":app")
