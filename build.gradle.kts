import io.gitlab.arturbosch.detekt.extensions.DetektExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.dagger.hilt.android) apply false
    alias(libs.plugins.kapt) apply false
    alias(libs.plugins.detekt) apply false
}
// 1. Применяем detekt к корневому проекту
apply(plugin = "io.gitlab.arturbosch.detekt")

// 2. Настройка detekt для всех проектов
allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    afterEvaluate {
        extensions.configure<DetektExtension> {
            config.setFrom(rootProject.files("default-detekt-config.yml"))
            buildUponDefaultConfig = true
            autoCorrect = true

            reports {
                html {
                    enabled = true
                    destination = file("build/reports/detekt.html")
                }
            }
        }

        // Добавляем formatting правила
        dependencies {
            add("detektPlugins", "io.gitlab.arturbosch.detekt:detekt-formatting:1.23.0")
        }
    }
}