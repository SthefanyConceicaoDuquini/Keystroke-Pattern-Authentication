plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.messager"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.messager"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        packagingOptions {
            resources {
                exclude("META-INF/LICENSE.md")
                exclude("META-INF/NOTICE.md")
            }
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/DEPENDENCIES"
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("junit:junit:4.13.2")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("org.apache.commons:commons-csv:1.8")
    implementation("commons-io:commons-io:2.6")
    implementation("org.jpmml:pmml-evaluator:1.6.4")
    implementation("com.google.guava:guava:30.1.1-jre")
    // implementation (group ="jakarta.xml.bind", name = "jakarta.xml.bind-api", version= "4.0.1")
    //implementation("javax.xml.bind:jaxb-api:2.3.0")
    //implementation("javax.activation:activation:1.1")
    //implementation("org.glassfish.jaxb:jaxb-runtime:2.3.0")
    /*dependencies {
        implementation("jakarta.xml.bind:jakarta.xml.bind-api:3.0.0") {
        exclude(group= "com.sun.xml.bind", module= "jaxb-impl")
        }

        implementation("com.sun.xml.bind:jaxb-impl:3.0.0")
    }*/


}
