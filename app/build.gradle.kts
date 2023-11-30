plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.ccc.confiax"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ccc.confiax"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        buildToolsVersion = "34"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packagingOptions {
        jniLibs {
            excludes += setOf("META-INF/native-image/**")
        }
        resources {
            excludes += setOf("META-INF/native-image/**")
        }
    }
}




dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation(project(mapOf("path" to ":library")))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.17")
    implementation("org.mongodb:mongodb-driver-sync:4.11.0")
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("com.facebook.android:facebook-login:latest.release")
    implementation ("com.facebook.android:facebook-android-sdk:[8,9]")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("net.danlew:android.joda:2.12.5")
    //runtimeOnly("androidx.appcompat:appcompat:1.6.1")
    //implementation ("github.com.StackTipsLab:custom-calendar-view")
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation ("com.github.siyamed:android-shape-imageview:0.9.+@aar")

}


