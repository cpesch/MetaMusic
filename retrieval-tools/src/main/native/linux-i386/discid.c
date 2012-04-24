/*
 * The implementation of the DiscId.deviceRead method for Linux
 *
 * author Christian Pesch
 *
 * based on work from Ron Lenk and Jeremy D. Zawodny
 */

#include <sys/ioctl.h>
#include <linux/cdrom.h>

#include <errno.h>
#include <fcntl.h>
#include <string.h>
#include <unistd.h>

#include "../discid.h"

JNIEXPORT jint JNICALL 
Java_org_metamusic_discid_DiscId_deviceRead
(JNIEnv *javaEnv, jclass javaClass, jstring deviceName, jintArray trackArray)
{
  const char *device_name;
  jclass exception_class;
  jint *track_array;
  jint track_count;
  
  struct cdrom_tochdr toc_header;
  struct cdrom_tocentry toc_entry;
  int device, track_index;

  device_name = (*javaEnv)->GetStringUTFChars(javaEnv, deviceName, 0);
  track_array = (*javaEnv)->GetIntArrayElements(javaEnv, trackArray, 0);

  if ((device = open(device_name, O_RDONLY)) == -1) {
    (*javaEnv)->ReleaseStringUTFChars(javaEnv, deviceName, device_name);
    (*javaEnv)->ReleaseIntArrayElements(javaEnv, trackArray, track_array, 0);
    exception_class = (*javaEnv)->FindClass(javaEnv, "java/io/IOException");
    (*javaEnv)->ThrowNew(javaEnv, exception_class, strerror(errno));
    /* NOT REACHED */
    return -1;
  }

  if (ioctl(device, CDROMREADTOCHDR, &toc_header) == -1) {
    (*javaEnv)->ReleaseStringUTFChars(javaEnv, deviceName, device_name);
    (*javaEnv)->ReleaseIntArrayElements(javaEnv, trackArray, track_array, 0);
    close(device);
    exception_class = (*javaEnv)->FindClass(javaEnv, "java/io/IOException");
    (*javaEnv)->ThrowNew(javaEnv, exception_class, strerror(errno));
    /* NOT REACHED */
    return -1;
  }

  track_count = toc_header.cdth_trk1 - toc_header.cdth_trk0 + 1;

  for (track_index = 0; track_index < track_count; ++track_index) {
    toc_entry.cdte_track = toc_header.cdth_trk0 + track_index;
    toc_entry.cdte_format = CDROM_MSF;
    
    if (ioctl(device, CDROMREADTOCENTRY, &toc_entry) == -1) {
      (*javaEnv)->ReleaseStringUTFChars(javaEnv, deviceName, device_name);
      (*javaEnv)->ReleaseIntArrayElements(javaEnv, trackArray, track_array, 0);
      close(device);
      exception_class = (*javaEnv)->FindClass(javaEnv, "java/io/IOException");
      (*javaEnv)->ThrowNew(javaEnv, exception_class, strerror(errno));
      /* NOT REACHED */
      return -1;
    }
    track_array[track_index] = FRAME(toc_entry.cdte_addr.msf.minute,
				     toc_entry.cdte_addr.msf.second,
				     toc_entry.cdte_addr.msf.frame);
  }

  toc_entry.cdte_track = CDROM_LEADOUT;
  toc_entry.cdte_format = CDROM_MSF;

  if (ioctl(device, CDROMREADTOCENTRY, &toc_entry) == -1) {
    (*javaEnv)->ReleaseStringUTFChars(javaEnv, deviceName, device_name);
    (*javaEnv)->ReleaseIntArrayElements(javaEnv, trackArray, track_array, 0);
    close(device);
    exception_class = (*javaEnv)->FindClass(javaEnv, "java/io/IOException");
    (*javaEnv)->ThrowNew(javaEnv, exception_class, strerror(errno));
    /* NOT REACHED */
    return -1;
  }

  track_array[track_count] = FRAME(toc_entry.cdte_addr.msf.minute,
				   toc_entry.cdte_addr.msf.second,
				   toc_entry.cdte_addr.msf.frame);

  (*javaEnv)->ReleaseStringUTFChars(javaEnv, deviceName, device_name);
  (*javaEnv)->ReleaseIntArrayElements(javaEnv, trackArray, track_array, 0);
  close(device);
  return track_count;
}
