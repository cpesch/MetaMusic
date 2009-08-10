/*
 * The implementation of the TRM.generateTRM methods
 *
 * author Christian Pesch
 */

#include "trm_wrapper.h"

/*
 * Utility procedures.
 */

void setLongJavaMember(JNIEnv *env, jobject obj, char *field, unsigned long value)
{
  jclass cls   = (*env)->GetObjectClass(env, obj);
  jfieldID fid = (*env)->GetFieldID(env, cls, field, "J");
  if(fid == 0) {
    return;
  }
  (*env)->SetLongField(env, obj, fid, value);
}

void setStringJavaMember(JNIEnv * env, jobject obj, char * field, char * value)
{
  jstring string = (*env)->NewStringUTF(env, value);
  jclass cls   = (*env)->GetObjectClass(env, obj);
  jfieldID fid = (*env)->GetFieldID(env, cls, field, "Ljava/lang/String;");
  if(fid == 0) {
    return;
  }
  (*env)->SetObjectField(env, obj, fid, string);
}

/* ----------------------------------------------------------------------- */
  /* Convert utf8-string to ucs1-string                                      */
  /* ----------------------------------------------------------------------- */

  char* utf2ucs(const char *utf8, char *ucs, size_t n) {
    const char *pin;
    char *pout;
   unsigned char current, next;
    int i;

   for (i=0,pin=utf8,pout=ucs; i < n && *pin; i++,pin++,pout++) {
     current = *pin;
     if (current >= 0xE0) {                /* we support only two-byte utf8 */
       return NULL;
     } else if ((current & 0x80) == 0)     /* one-byte utf8                 */
       *pout = current;
      else {                                /* two-byte utf8                 */
       next = *(++pin);
       if (next >= 0xC0) {                 /* illegal coding                */
 	return NULL;
       }
       *pout = ((current & 3) << 6) +      /* first two bits of first byte  */
 	(next & 63);                         /* last six bits of second byte  */
     }
   }
   if (i < n)
     *pout = '\0';
   return ucs;
   }

JNIEXPORT jint JNICALL
Java_org_metamusic_trm_TRM_generateTRMforMP3
(JNIEnv *javaEnv, jclass javaClass, jstring fileName, jlong durationL, jstring proxyServer, jint proxyPort, jobject result)
{
  const char *file_name;
  const char *proxy_server;
  char signature[37];
  unsigned long duration = durationL;
  int ret;

  file_name = (*javaEnv)->GetStringUTFChars(javaEnv, fileName, 0);
  proxy_server = (*javaEnv)->GetStringUTFChars(javaEnv, proxyServer, 0);

  // printf("file_name %s\n", file_name);

  ret = MP3_generateTRM(file_name, signature, &duration, proxy_server, proxyPort);

  // printf("duration %lu\n", duration);

  setLongJavaMember(javaEnv, result, "duration", duration);
  setStringJavaMember(javaEnv, result, "signature", signature);

  (*javaEnv)->ReleaseStringUTFChars(javaEnv, fileName, file_name);
  if(proxyServer != NULL)
    (*javaEnv)->ReleaseStringUTFChars(javaEnv, proxyServer, proxy_server);

  return ret;
}

JNIEXPORT jint JNICALL 
Java_org_metamusic_trm_TRM_generateTRMforWAV
(JNIEnv *javaEnv, jclass javaClass, jstring fileName, jlong durationL, jstring proxyServer, jint proxyPort, jobject result)
{
  const char *file_name;
  const char *proxy_server;
  char signature[37];
  unsigned long duration = durationL;
  int ret;

  file_name = (*javaEnv)->GetStringUTFChars(javaEnv, fileName, 0);
  proxy_server = (*javaEnv)->GetStringUTFChars(javaEnv, proxyServer, 0);

  ret = Wav_generateTRM(file_name, signature, &duration, proxy_server, proxyPort);

  setLongJavaMember(javaEnv, result, "duration", duration);
  setStringJavaMember(javaEnv, result, "signature", signature);

  (*javaEnv)->ReleaseStringUTFChars(javaEnv, fileName, file_name);
  if(proxyServer != NULL)
    (*javaEnv)->ReleaseStringUTFChars(javaEnv, proxyServer, proxy_server);

  return ret;
}

JNIEXPORT jint JNICALL 
Java_org_metamusic_trm_TRM_generateTRMforOggVorbis
(JNIEnv *javaEnv, jclass javaClass, jstring fileName, jlong durationL, jstring proxyServer, jint proxyPort, jobject result)
{
  const char *file_name;
  const char *proxy_server;
  char signature[37];
  unsigned long duration = durationL;
  int ret;

  file_name = (*javaEnv)->GetStringUTFChars(javaEnv, fileName, 0);
  proxy_server = (*javaEnv)->GetStringUTFChars(javaEnv, proxyServer, 0);

  ret = OggVorbis_generateTRM(file_name, signature, &duration, proxy_server, proxyPort);

  setLongJavaMember(javaEnv, result, "duration", duration);
  setStringJavaMember(javaEnv, result, "signature", signature);

  (*javaEnv)->ReleaseStringUTFChars(javaEnv, fileName, file_name);
  if(proxyServer != NULL)
    (*javaEnv)->ReleaseStringUTFChars(javaEnv, proxyServer, proxy_server);

  return ret;
}

