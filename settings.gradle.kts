rootProject.name = "DesktopPluginDemoApp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/getzoop/zoop-package-public")
            credentials {
                val username = System.getenv("GITHUB_USER")
                val password = System.getenv("GITHUB_PAT")
                credentials {
                    this.username = username
                    this.password = password
                }
            }
        }
    }
}

include(":composeApp")