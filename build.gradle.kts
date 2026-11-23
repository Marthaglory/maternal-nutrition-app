// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Top-level build file

plugins {
    alias(libs.plugins.android.application) apply false

    id("com.google.devtools.ksp") version "2.3.12" apply false
}