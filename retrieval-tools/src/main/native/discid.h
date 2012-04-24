/*
 * The implementation of the DiscId.deviceRead method
 *
 * author Christian Pesch
 *
 * based on work from Ron Lenk 
 */

#include <jni.h>
#include "org_metamusic_discid_DiscId.h"

#define FRAME(m, s, f) (((m) * 60 + (s)) * 75 + (f))
