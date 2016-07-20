#include <jni.h>

JNIEXPORT jstring JNICALL
Java_jagsc_dlfa_osushi_Tensorflow_test
    (JNIEnv *env, jobject instance)
{
   return (*env)->NewStringUTF(env, "UNKO");
}