#ifndef MBID3_H
#define MBID3_H

#ifdef HAVE_LIBID3

#include "id3.h"

#include <string>
using namespace std;

#include "defs.h"


class ID3
{
    public:

           ID3(bool writeV1, bool writeV2);
          ~ID3(void) {};

       bool write(const string   &fileName,
                  const Metadata &data);
       bool read (const string   &fileName,
                  Metadata       &data);

       void getError(string &error) { error = errString; };

    private:

       bool readV1(const string &file, Metadata &data);

       bool       writeV1, writeV2;
       void       setError(ID3_Err err);
       string errString;
};

#endif
#endif
