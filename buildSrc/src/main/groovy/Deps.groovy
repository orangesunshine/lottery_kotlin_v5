def config = [:]
config.put(":app", ["COMMON,KOTLIN"])

interface AndroidConfigVersion {
    def compileSdkVersion = 29
    def minSdkVersion = 16
    def targetSdkVersion = 29
    def versionCode = 1
    def versionName = "1.0"
    def buildToolsVersion = "30.0.2"
    def appId = "com.orange.DM"
}

interface LibVersion {
    def appcompat_version = "1.2.0"
    def espresso_core_version = "3.3.0"
    def junit_test_version = "1.1.2"
    def junit_version = "4.12"
    def core_ktx_version = "1.3.1"
    def kotlin_version = "1.4.10"
    def retrofit_version = "2.9.0"
    def rxandroid3_version = "3.0.0"
    def rxjava3_version = "3.0.4"

    def lifecycle_version = "2.2.0"
    def arch_version = "2.1.0"
    def arch_lifecycle_version = "1.1.1"
    def activity_version = "1.1.0"
    def fragment_version = "1.2.5"
    def collection_version = "1.1.0"
    def nav_version = "2.3.0"
    def paging_version = "2.1.2"
    def room_version = "2.2.5"
    def work_version = "2.4.0"
    def navigation_version = "2.1.0"


    def gson = "2.8.0"
}

interface PluginVersion {
    def gradle_version = "4.0.1"
}


def DEPS = [:]
def COMMON = []
DEPS.COMMON = COMMON
COMMON << "classpath__com.android.tools.build:gradle:$PluginVersion.gradle_version"
COMMON << "junit:junit:$LibVersion.junit_version"
COMMON << "androidx.test.ext:$LibVersion.junit_test_version"
COMMON << "androidx.test.espresso:espresso-core:$LibVersion.espresso_core_version"
COMMON << "androidx.appcompat:appcompat:$LibVersion.appcompat_version"
// For loading and tinting drawables on older versions of the platform
COMMON << "androidx.appcompat:appcompat-resources:$LibVersion.appcompat_version"

if (ktx) {
    def KOTLIN = []
    DEPS.KOTLIN = KOTLIN
    KOTLIN << "classpath__org.jetbrains.kotlin:kotlin-gradle-plugin:$LibVersion.kotlin_version"
    KOTLIN << "plugin__kotlin-android-extensions"
    KOTLIN << "plugin__kotlin-android"
    KOTLIN << "androidx.core:core-ktx:$LibVersion.core_ktx_version"
    KOTLIN << "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$LibVersion.kotlin_version"
}

def RETROFIT = []
DEPS.RETROFIT = RETROFIT
RETROFIT << "com.squareup.retrofit2:retrofit:$LibVersion.retrofit_version"
RETROFIT << "com.squareup.retrofit2:adapter-rxjava2:$LibVersion.retrofit_version"
RETROFIT << "com.squareup.retrofit2:converter-gson:$LibVersion.retrofit_version"

DEPS.RXANDROID3 << "io.reactivex.rxjava3:rxandroid:$LibVersion.rxandroid3_version"
DEPS.RXJAVA3 << "io.reactivex.rxjava3:rxjava:$LibVersion.rxjava3_version"

DEPS.GSON << "com.google.code.gson:gson:$LibVersion.gson"
return DEPS

def handleDep = { String depUrl ->
    DepsConfig.config

}

//单个dep获取对应依赖url
def getSingleDeps = { String dep ->
    def deps = []
    if (null != dep && !dep.isEmpty()) {
        def value = DEPS[dep]
        if (null != value) {
            if (value instanceof List && !value.isEmpty()) {
                deps.addAll(value)
            } else if (value instanceof CharSequence) {
                deps.add(value)
            }
        }
    }
    return deps
}

//根据模块依赖配置获取依赖库url
def getModuleDeps = { List deps ->
    def moduelDeps = []
    if (null != deps && !deps.isEmpty()) {
        deps.each {
            if (null != it && it.length > 0) {
                def singleDeps = getSingleDeps(it)
                if (null != singleDeps && !singleDeps.isEmpty()) {
                    moduelDeps.addAll(singleDeps)
                }
            }
        }
    }
    return moduelDeps
}

def getAllDeps = {
    if(null != config && !config.isEmpty()){
        config.each {
            
        }
    }
}

def getClassPath() {

}

get getPlugins() {

}