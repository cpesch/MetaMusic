/****************************************************************************
 
   trm - the TRM digital finger print utilty 
  
   Copyright (C) 2002 Robert Kaye

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

   $Id: dllmain.cpp 9 2003-10-13 12:45:19Z cpesch $

 ***************************************************************************/

#include <windows.h>

BOOL APIENTRY DllMain( HANDLE hModule, 
                        DWORD ul_reason_for_call, 
                        LPVOID lpReserved )
{
    switch( ul_reason_for_call ) {
    case DLL_PROCESS_ATTACH:
    
    case DLL_THREAD_ATTACH:
    
    case DLL_THREAD_DETACH:
    
    case DLL_PROCESS_DETACH:
		;
    
    };
    return TRUE;
}
