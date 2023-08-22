@file:Suppress("UnstableApiUsage", "UNUSED_VARIABLE")

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("base-gradle")
    id("app.cash.sqldelight")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":data"))
                implementation(project(":platformspecific"))
                implementation(Kotlin.Stdlib.common)
                implementation(Jetbrains.Kotlinx.serialization)
                implementation(Jetbrains.Kotlinx.immutable)
                implementation(Jetbrains.Kotlinx.coroutines)
                implementation(Touchlab.kermit)
                implementation(Koin.core)
                implementation(Square.okio)
                implementation(CashApp.Sqldelight.coroutines)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Kotlin.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(CashApp.Sqldelight.android)
                //only for migration
                implementation("com.github.requery:sqlite-android:_")
                implementation("com.russhwolf:multiplatform-settings-no-arg:_")
                implementation("com.russhwolf:multiplatform-settings-serialization:_")
            }
        }
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(CashApp.Sqldelight.ios)
            }
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    namespace = "org.rhasspy.mobile.settings"
}

sqldelight {
    databases {
        create("Database") {
            dialect("app.cash.sqldelight:sqlite-3-30-dialect:_")
            packageName.set("org.rhasspy.mobile.settings")
            schemaOutputDirectory.set(file("src/main/sqldelight/databases"))
        }
    }
}