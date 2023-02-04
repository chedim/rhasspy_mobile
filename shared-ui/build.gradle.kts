plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.compose")
}

version = Version.toString()

kotlin {
    targets {
        android()
        iosX64()
        iosArm64()
        iosSimulatorArm64()
    }

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "16.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared_ui"
            configureFramework()
            export("dev.icerock.moko:resources:_")
        }
    }

    sourceSets {
        all {
            //Warning: This class can only be used with the compiler argument '-opt-in=kotlin.RequiresOptIn'
            languageSettings.optIn("kotlin.RequiresOptIn")
        }

        val commonMain by getting {
            dependencies {
                api(Icerock.Resources)
                implementation(project(":shared-viewmodel"))
                implementation(project(":shared-resources"))
                implementation(Icerock.Mvvm.core)
                implementation(Jetbrains.Compose.ui)
                implementation(Jetbrains.Compose.ui)
                implementation(Jetbrains.Compose.full)
                implementation(Jetbrains.Compose.foundation)
                implementation(Jetbrains.Compose.material3)
                implementation(Jetbrains.Compose.runtime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Kotlin.test)
            }
        }
        val androidMain by getting
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
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
    namespace = "org.rhasspy.mobile"
    compileSdk = 33
    defaultConfig {
        minSdk = 23
    }
}

compose {
    //necessary to use the androidx compose compiler for multiplatform in order to use kotlin 1.8
    kotlinCompilerPlugin.set(AndroidX.Compose.compiler.toString())
}

fun org.jetbrains.kotlin.gradle.plugin.mpp.Framework.configureFramework() {
    isStatic = true
    freeCompilerArgs = listOf(
        "-linker-options",
        "-U _FIRCLSExceptionRecordNSException " +
                "-U _OBJC_CLASS_\$_FIRStackFrame " +
                "-U _OBJC_CLASS_\$_FIRExceptionModel " +
                "-U _OBJC_CLASS_\$_FIRCrashlytics"
    )
}