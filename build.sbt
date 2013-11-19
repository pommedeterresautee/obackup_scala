// so we can use keywords from Android, such as 'Android' and 'proguardOptions'
import android.Keys._
import android.Dependencies.{aar,apklib}

// load the android plugin into the build
android.Plugin.androidBuild

 
// project name, completely optional
name := "OBackup"
 
// pick the version of scala you want to use
scalaVersion := "2.10.2"
 
// scala 2.10 flag for feature warnings
scalacOptions in Compile += "-feature"
 
// for non-ant-based projects, you'll need this for the specific build target:
platformTarget in Android := "android-19"

resolvers ++= Seq("Bugsense repository" at "http://www.bugsense.com/gradle/")

libraryDependencies ++= Seq (
  "org.scaloid" %% "scaloid" % "latest.integration",
  "com.netflix.rxjava" % "rxjava-scala" % "latest.integration",
  "com.netflix.rxjava" % "rxjava-android" % "latest.integration",
  "com.android.support" % "support-v4" % "19.0.0",
  "com.google.code.gson" % "gson" % "latest.integration",
  "org.joda" % "joda-convert" % "1.5",
  "joda-time" % "joda-time" % "2.3",
  "com.googlecode.json-simple" % "json-simple" % "1.1" intransitive(),
  "de.keyboardsurfer.android.widget" % "crouton" % "latest.integration" intransitive(),
  "com.bugsense.trace" % "bugsense" % "3.5",
  "com.google.android.gms" % "play-services" % "latest.integration" intransitive(),
  "com.google.apis" % "google-api-services-drive" % "latest.integration" intransitive(),
  "com.google.api-client" % "google-api-client" % "latest.integration" intransitive(),
  "com.google.api-client" % "google-api-client-android" % "latest.integration"  intransitive(),
  "com.google.http-client" % "google-http-client-jackson" % "latest.integration" intransitive(),
  "com.google.http-client" % "google-http-client-gson" % "latest.integration" intransitive()
)

ideaExcludeFolders ++= Seq (
    ".idea",
    ".idea_modules",
    "target",
    "project"
)

proguardCache in Android ++= Seq(
  ProguardCache("org.scaloid") % "org.scaloid" %% "scaloid",
  ProguardCache("rx") % "com.netflix.rxjava" %% "rxjava-scala"
)


// call install and run without having to prefix with android:
run <<= run in Android
 
install <<= install in Android
