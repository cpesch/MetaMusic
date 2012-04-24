/*
 * Copyright (c) 2000, Ronald Lenk
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice unmodified, this list of conditions, and the following
 *    disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

/*
 * The implementation of the DiscId.deviceRead method for Windows 95/98/NT/2000/XP.
 *
 * author Ron Lenk
 */

#include <windows.h>
#include "../discid.h"

JNIEXPORT jint JNICALL
Java_org_metamusic_discid_DiscId_deviceRead
(JNIEnv *javaEnv, jclass javaClass, jstring deviceName, jintArray trackArray)
{
  const char *device_name;
  jclass exception_class;
  jint *track_array;
  jint track_count;

  UINT wDeviceID;
  int iTrackIndex;
  MCI_OPEN_PARMS mciOpenParms;
  MCI_SET_PARMS mciSetParms;
  MCI_STATUS_PARMS mciStatusParms;
  char szErrorBuffer[MAXERRORLENGTH];
  DWORD status;

  device_name = (*javaEnv)->GetStringUTFChars(javaEnv, deviceName, 0);
  track_array = (*javaEnv)->GetIntArrayElements(javaEnv, trackArray, 0);
  mciOpenParms.lpstrDeviceType = device_name;

  if ((status = mciSendCommand(NULL, MCI_OPEN, MCI_OPEN_TYPE, (DWORD)(LPVOID)&mciOpenParms)) != 0) {
    (*javaEnv)->ReleaseStringUTFChars(javaEnv, deviceName, device_name);
    (*javaEnv)->ReleaseIntArrayElements(javaEnv, trackArray, track_array, 0);
    if (mciGetErrorString(status, (LPSTR)szErrorBuffer, MAXERRORLENGTH)) {
      exception_class = (*javaEnv)->FindClass(javaEnv, "java/io/IOException");
      (*javaEnv)->ThrowNew(javaEnv, exception_class, szErrorBuffer);
    } else {
      exception_class = (*javaEnv)->FindClass(javaEnv, "java/io/IOException");
      (*javaEnv)->ThrowNew(javaEnv, exception_class, "unknown error");
    }
    /* NOT REACHED */
    return -1;
  }
  
  wDeviceID = mciOpenParms.wDeviceID;
  
  mciSetParms.dwTimeFormat = MCI_FORMAT_MSF;
  if ((status = mciSendCommand(wDeviceID, MCI_SET, MCI_SET_TIME_FORMAT, (DWORD)(LPVOID)&mciSetParms)) != 0) {
    (*javaEnv)->ReleaseStringUTFChars(javaEnv, deviceName, device_name);
    (*javaEnv)->ReleaseIntArrayElements(javaEnv, trackArray, track_array, 0);
    mciSendCommand(wDeviceID, MCI_CLOSE, 0, NULL);
    if (mciGetErrorString(status, (LPSTR)szErrorBuffer, MAXERRORLENGTH)) {
      exception_class = (*javaEnv)->FindClass(javaEnv, "java/io/IOException");
      (*javaEnv)->ThrowNew(javaEnv, exception_class, szErrorBuffer);
    } else {
      exception_class = (*javaEnv)->FindClass(javaEnv, "java/io/IOException");
      (*javaEnv)->ThrowNew(javaEnv, exception_class, "unknown error");
    }
    /* NOT REACHED */
    return -1;
  }
  
  mciStatusParms.dwItem = MCI_STATUS_NUMBER_OF_TRACKS;
  if ((status = mciSendCommand(wDeviceID, MCI_STATUS, MCI_STATUS_ITEM, (DWORD)(LPVOID)&mciStatusParms)) != 0) {
    (*javaEnv)->ReleaseStringUTFChars(javaEnv, deviceName, device_name);
    (*javaEnv)->ReleaseIntArrayElements(javaEnv, trackArray, track_array, 0);
    mciSendCommand(wDeviceID, MCI_CLOSE, 0, NULL);
    if (mciGetErrorString(status, (LPSTR)szErrorBuffer, MAXERRORLENGTH)) {
      exception_class = (*javaEnv)->FindClass(javaEnv, "java/io/IOException");
      (*javaEnv)->ThrowNew(javaEnv, exception_class, szErrorBuffer);
    } else {
      exception_class = (*javaEnv)->FindClass(javaEnv, "java/io/IOException");
      (*javaEnv)->ThrowNew(javaEnv, exception_class, "unknown error");
    }
    /* NOT REACHED */
    return -1;
  }
  
  track_count = mciStatusParms.dwReturn;
  
  for (iTrackIndex = 0; iTrackIndex < track_count; ++iTrackIndex) {
    mciStatusParms.dwItem = MCI_STATUS_POSITION;
    mciStatusParms.dwTrack = iTrackIndex + 1;
    if ((status = mciSendCommand(wDeviceID, MCI_STATUS, MCI_STATUS_ITEM | MCI_TRACK, (DWORD)(LPVOID)&mciStatusParms)) != 0) {
      (*javaEnv)->ReleaseStringUTFChars(javaEnv, deviceName, device_name);
      (*javaEnv)->ReleaseIntArrayElements(javaEnv, trackArray, track_array, 0);
      mciSendCommand(wDeviceID, MCI_CLOSE, 0, NULL);
      if (mciGetErrorString(status, (LPSTR)szErrorBuffer, MAXERRORLENGTH)) {
  	    exception_class = (*javaEnv)->FindClass(javaEnv, "java/io/IOException");
   	    (*javaEnv)->ThrowNew(javaEnv, exception_class, szErrorBuffer);
      } else {
        exception_class = (*javaEnv)->FindClass(javaEnv, "java/io/IOException");
		(*javaEnv)->ThrowNew(javaEnv, exception_class, "unknown error");
      }
      /* NOT REACHED */
      return -1;
    }
    
    track_array[iTrackIndex] = FRAME(MCI_MSF_MINUTE(mciStatusParms.dwReturn),
				     MCI_MSF_SECOND(mciStatusParms.dwReturn),
				     MCI_MSF_FRAME(mciStatusParms.dwReturn));
  }
  
  mciStatusParms.dwItem = MCI_STATUS_LENGTH;
  mciStatusParms.dwTrack = track_count;
  
  if ((status = mciSendCommand(wDeviceID, MCI_STATUS, MCI_STATUS_ITEM | MCI_TRACK, (DWORD)(LPVOID)&mciStatusParms)) != 0) {
    (*javaEnv)->ReleaseStringUTFChars(javaEnv, deviceName, device_name);
    (*javaEnv)->ReleaseIntArrayElements(javaEnv, trackArray, track_array, 0);
    mciSendCommand(wDeviceID, MCI_CLOSE, 0, NULL);
    if (mciGetErrorString(status, (LPSTR)szErrorBuffer, MAXERRORLENGTH)) {
      exception_class = (*javaEnv)->FindClass(javaEnv, "java/io/IOException");
      (*javaEnv)->ThrowNew(javaEnv, exception_class, szErrorBuffer);
    } else {
      exception_class = (*javaEnv)->FindClass(javaEnv, "java/io/IOException");
      (*javaEnv)->ThrowNew(javaEnv, exception_class, "unknown error");
    }
    /* NOT REACHED */
    return -1;
  }

  track_array[track_count] = track_array[track_count - 1] +
    FRAME(MCI_MSF_MINUTE(mciStatusParms.dwReturn),
	  MCI_MSF_SECOND(mciStatusParms.dwReturn),
	  MCI_MSF_FRAME(mciStatusParms.dwReturn)) + 1;

  (*javaEnv)->ReleaseStringUTFChars(javaEnv, deviceName, device_name);
  (*javaEnv)->ReleaseIntArrayElements(javaEnv, trackArray, track_array, 0);

  mciSendCommand(wDeviceID, MCI_CLOSE, 0, NULL);

  return track_count;
}

