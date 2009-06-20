/****************************************************************************
 
   trm - the TRM digital finger print utilty 
  
   Copyright (C) 2001, 2002 Bertrand Petit                                          *
   Portions Copyright (C) 2002 Myers Carpenter
   Portions Copyright (C) 2002 Robert Kaye

   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License
   as published by the Free Software Foundation; either version 2
   of the License, or (at your option) any later version. 
   
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details. 
   
   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software 
   Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111, USA.

   $Id: trmgenbase.h 9 2003-10-13 12:45:19Z cpesch $

 ***************************************************************************/

#ifndef _TRM_GEN_BASE_H
#define _TRM_GEN_BASE_H


#include <string>

using namespace std;

/*-------------------------------------------------------------------------*/

class TRMGeneratorBase
{
   public:

                    TRMGeneratorBase(const string &fileName)
                          { this->fileName = fileName; };
     virtual       ~TRMGeneratorBase(void) {};

     virtual int    Generate(string &trmId, unsigned long *duration,
                             char *proxyServer, int proxyPort) = 0;
     virtual void   GetError(string &error)
                      {
                          if (errorString == string(""))
                          {
                              error = string("No error, or unknown error.");
                              return;
                          }

                          error = string(errorString);
                      };

   protected:

     string  fileName, errorString;
};

#endif
