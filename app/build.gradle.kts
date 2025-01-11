plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 28
        this.targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        //noinspection DataBindingWithoutKapt
        dataBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.15.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("com.google.firebase:firebase-auth:23.1.0")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-auth-ktx:23.1.0")
    implementation("com.google.firebase:firebase-storage:21.0.1")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.1")
    implementation("com.google.android.libraries.places:places:4.1.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    //Signup with google
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    //lottie animation
    implementation ("com.airbnb.android:lottie:6.3.0")

    //image loader tool -> Picasso
    implementation("com.squareup.picasso:picasso:2.8")

    //ken burns effect for zoom on image in homepage
    implementation("com.flaviofaria:kenburnsview:1.0.7")
    //meow animation nav bar at bottom main component
    implementation ("com.github.Foysalofficial:NafisBottomNav:5.0")
    //library to crop the image during uploading
    implementation ("com.github.ictfoysal:Android-Image-Cropper-byFt:2.9")
    implementation ("com.github.yalantis:ucrop:2.2.8")

    // To navigate between the fragments
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.4")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.4")
    // Scratch card
    implementation ("com.github.AnupKumarPanwar:ScratchView:1.2")
    implementation("com.github.cooltechworks:ScratchView:v1.1")

    //gif
    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.25")


    implementation("jp.wasabeef:picasso-transformations:2.4.0")


}