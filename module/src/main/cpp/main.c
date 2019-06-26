#include <stdio.h>
#include <jni.h>
#include <dlfcn.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <android/log.h>

#include "hook.h"
#include "log.h"
#include "utils.h"
#include "inject.h"

#define EXPORT __attribute__((visibility("default")))

#define DEX_PATH           "/system/framework/boot-ifw-enhance.jar"
#define CONFIG_PATH_FORMAT "/data/misc/riru/modules/ibr/config.%s.json"

static char *config_data;

static void on_fork(JNIEnv *env,jstring *appDataDir, jstring *packageName) {
    static char config_path_buffer[256];
    char package_name[256];

    if ( appDataDir ) {
        const char *app_data_dir = (*env)->GetStringUTFChars(env, *appDataDir, NULL);
        int user = 0;
        if (sscanf(app_data_dir, "/data/%*[^/]/%d/%s", &user, package_name) != 2) {
            if (sscanf(app_data_dir, "/data/%*[^/]/%s", package_name) != 1) {
                package_name[0] = '\0';
                LOGW("can't parse %s", app_data_dir);
            }
        }
        (*env)->ReleaseStringChars(env, *appDataDir, (const jchar *) app_data_dir);
    }
    else if ( packageName ) {
        const char *package_name_const_str = (*env)->GetStringUTFChars(env, *packageName, NULL);
        if ( package_name_const_str )
            strncpy(package_name, package_name_const_str, sizeof(package_name));
        (*env)->ReleaseStringChars(env, &packageName, (const jchar *) package_name_const_str);
    }

    sprintf(config_path_buffer ,CONFIG_PATH_FORMAT ,package_name);
    config_data = malloc_and_load_file(config_path_buffer);

    if ( config_data == NULL )
        LOGI("Skip %s" ,package_name);
    else
        LOGI("Inject %s" ,package_name);
}

EXPORT
void nativeForkAndSpecializePre(
        JNIEnv *env, jclass clazz, jint *_uid, jint *gid, jintArray *gids, jint *runtime_flags,
        jobjectArray *rlimits, jint *_mount_external, jstring *se_info, jstring *se_name,
        jintArray *fdsToClose, jintArray *fdsToIgnore, jboolean *is_child_zygote,
        jstring *instructionSet, jstring *appDataDir, jstring *packageName,
        jobjectArray *packagesForUID, jstring *sandboxId) {

    //on_fork(env, appDataDir, NULL);
}

EXPORT
int nativeForkAndSpecializePost(JNIEnv *env, jclass clazz, jint res) {
    return 0;

    if ( res == 0 && config_data != NULL )
        invoke_inject_method(env, config_data);

    if ( config_data != NULL )
        free(config_data);

    return 0;
}

EXPORT
void specializeAppProcessPre(
        JNIEnv *env, jclass clazz, jint *_uid, jint *gid, jintArray *gids, jint *runtimeFlags,
        jobjectArray *rlimits, jint *mountExternal, jstring *seInfo, jstring *niceName,
        jboolean *startChildZygote, jstring *instructionSet, jstring *appDataDir,
        jstring *packageName, jobjectArray *packagesForUID, jstring *sandboxId) {
    // from Android Q beta 3
    // in zygote process
    //on_fork(env, NULL, packageName);
}

EXPORT
int specializeAppProcessPost(
        JNIEnv *env, jclass clazz) {
    // from Android Q beta 3
    // in app process
    return 0;

    if ( config_data != NULL ) {
        invoke_inject_method(env, config_data);
        free(config_data);
    }

    return 0;
}

EXPORT
int nativeForkSystemServerPost(JNIEnv *env, jclass clazz, jint res) {
    if (res != 0)
        return 0;

    invoke_inject_method(env, NULL);

    return 0;
}


EXPORT
void onModuleLoaded() {
    char buffer[4096];
    char *p = NULL;

    strcpy(buffer,(p = getenv("CLASSPATH")) ? p : "");
    strcat(buffer,":" DEX_PATH);
    setenv("CLASSPATH",buffer,1);

    hook_install(&find_inject_class_method);
}

