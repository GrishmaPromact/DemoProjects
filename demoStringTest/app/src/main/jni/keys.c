#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_promact_demostringtest_MainActivity_getNativeKey1(JNIEnv *env, jobject instance) {

    return (*env)->  NewStringUTF(env, "TmF0aXZlNWVjcmV0UEBzc3cwcmQy");
}

JNIEXPORT jstring JNICALL
Java_com_promact_demostringtest_MainActivity_getNativeKey2(JNIEnv *env, jobject instance) {

    return (*env)->NewStringUTF(env, "TmF0aXZlNWVjcmV0UEBzc3cwcmQy");
}