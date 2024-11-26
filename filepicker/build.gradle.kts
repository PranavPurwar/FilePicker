plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "dev.pranav.filepicker"
    compileSdk = 35

    defaultConfig {
        minSdk = 21

        consumerProguardFiles("consumer-rules.pro")

        aarMetadata {
            version = "1.0.0"
            minCompileSdk = 21
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    viewBinding.enable = true

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

group = "com.github.PranavPurwar"
version = "1.0.0"


afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                groupId = "com.github.PranavPurwar"
                artifactId = "filepicker"
                version = "1.0.0"

                afterEvaluate {
                    from(components["release"])
                }
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}
