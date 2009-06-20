# Microsoft Developer Studio Project File - Name="trm_dll" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Dynamic-Link Library" 0x0102

CFG=trm_dll - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "trm_dll.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "trm_dll.mak" CFG="trm_dll - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "trm_dll - Win32 Release" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE "trm_dll - Win32 Debug" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe
MTL=midl.exe
RSC=rc.exe

!IF  "$(CFG)" == "trm_dll - Win32 Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "trm_dll___Win32_Release"
# PROP BASE Intermediate_Dir "trm_dll___Win32_Release"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "trm_dll___Win32_Release"
# PROP Intermediate_Dir "trm_dll___Win32_Release"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MT /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "TRM_DLL_EXPORTS" /YX /FD /c
# ADD CPP /nologo /MT /W3 /GX /O2 /I "..\..\oggvorbis-win32sdk-1.0\include" /I "..\..\musicbrainz-win32sdk-2.0.0\include" /I "..\..\mad-0.14.2b\msvc++\libmad" /D "NDEBUG" /D "WIN32" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "TRM_DLL_EXPORTS" /D "NOMINMAX" /D "HAVE_LIBMAD" /D "HAVE_LIBMUSICBRAINZ" /D "HAVE_OGGVORBIS" /YX /FD /c
# ADD BASE MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD BASE RSC /l 0x409 /d "NDEBUG"
# ADD RSC /l 0x409 /d "NDEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /dll /machine:I386
# ADD LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib musicbrainz.lib ogg.lib vorbis.lib vorbisfile.lib libmad.lib /nologo /dll /machine:I386 /out:"trm_dll___Win32_Release/trm.dll" /libpath:"..\..\mad-0.14.2b\msvc++\libmad\Release" /libpath:"..\..\musicbrainz-win32sdk-2.0.0\lib\\" /libpath:"..\..\oggvorbis-win32sdk-1.0\lib"

!ELSEIF  "$(CFG)" == "trm_dll - Win32 Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "trm_dll___Win32_Debug"
# PROP BASE Intermediate_Dir "trm_dll___Win32_Debug"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "trm_dll___Win32_Debug"
# PROP Intermediate_Dir "trm_dll___Win32_Debug"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MTd /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "TRM_DLL_EXPORTS" /YX /FD /GZ /c
# ADD CPP /nologo /MTd /W3 /Gm /GX /ZI /Od /I "..\..\oggvorbis-win32sdk-1.0\include" /I "..\..\musicbrainz-win32sdk-2.0.0\include" /I "..\..\mad-0.14.2b\msvc++\libmad" /D "_DEBUG" /D "WIN32" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "TRM_DLL_EXPORTS" /D "NOMINMAX" /D "HAVE_LIBMAD" /D "HAVE_LIBMUSICBRAINZ" /D "HAVE_OGGVORBIS" /YX /FD /GZ /c
# ADD BASE MTL /nologo /D "_DEBUG" /mktyplib203 /win32
# ADD MTL /nologo /D "_DEBUG" /mktyplib203 /win32
# ADD BASE RSC /l 0x409 /d "_DEBUG"
# ADD RSC /l 0x409 /d "_DEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /dll /debug /machine:I386 /pdbtype:sept
# ADD LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib musicbrainz.lib ogg.lib vorbis.lib vorbisfile.lib libmad.lib /nologo /dll /debug /machine:I386 /out:"trm_dll___Win32_Debug/trm.dll" /pdbtype:sept /libpath:"..\..\mad-0.14.2b\msvc++\libmad\Release" /libpath:"..\..\musicbrainz-win32sdk-2.0.0\lib\\" /libpath:"..\..\oggvorbis-win32sdk-1.0\lib"

!ENDIF 

# Begin Target

# Name "trm_dll - Win32 Release"
# Name "trm_dll - Win32 Debug"
# Begin Group "Source Files"

# PROP Default_Filter "cpp;c;cxx;rc;def;r;odl;idl;hpj;bat"
# Begin Source File

SOURCE=..\dllmain.cpp
# End Source File
# Begin Source File

SOURCE=..\mp3_trm.cpp
# End Source File
# Begin Source File

SOURCE=..\ov_trm.cpp
# End Source File
# Begin Source File

SOURCE=.\trm.def
# End Source File
# Begin Source File

SOURCE=..\wav_trm.cpp
# End Source File
# End Group
# Begin Group "Header Files"

# PROP Default_Filter "h;hpp;hxx;hm;inl"
# End Group
# Begin Group "Resource Files"

# PROP Default_Filter "ico;cur;bmp;dlg;rc2;rct;bin;rgs;gif;jpg;jpeg;jpe"
# End Group
# Begin Group "Dlls"

# PROP Default_Filter ""
# Begin Source File

SOURCE="..\..\musicbrainz-win32sdk-2.0.0\lib\musicbrainz.dll"

!IF  "$(CFG)" == "trm_dll - Win32 Release"

# Begin Custom Build
OutDir=.\trm_dll___Win32_Release
InputPath="..\..\musicbrainz-win32sdk-2.0.0\lib\musicbrainz.dll"
InputName=musicbrainz

"$(OutDir)\$(InputName).dll" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	copy $(InputPath) $(OutDir)

# End Custom Build

!ELSEIF  "$(CFG)" == "trm_dll - Win32 Debug"

# Begin Custom Build
OutDir=.\trm_dll___Win32_Debug
InputPath="..\..\musicbrainz-win32sdk-2.0.0\lib\musicbrainz.dll"
InputName=musicbrainz

"$(OutDir)\$(InputName).dll" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	copy $(InputPath) $(OutDir)

# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE="..\..\oggvorbis-win32sdk-1.0\bin\ogg.dll"

!IF  "$(CFG)" == "trm_dll - Win32 Release"

# Begin Custom Build
OutDir=.\trm_dll___Win32_Release
InputPath="..\..\oggvorbis-win32sdk-1.0\bin\ogg.dll"
InputName=ogg

"$(OutDir)\$(InputName).dll" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	copy $(InputPath) $(OutDir)

# End Custom Build

!ELSEIF  "$(CFG)" == "trm_dll - Win32 Debug"

# Begin Custom Build
OutDir=.\trm_dll___Win32_Debug
InputPath="..\..\oggvorbis-win32sdk-1.0\bin\ogg.dll"
InputName=ogg

"$(OutDir)\$(InputName).dll" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	copy $(InputPath) $(OutDir)

# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE="..\..\oggvorbis-win32sdk-1.0\bin\vorbis.dll"

!IF  "$(CFG)" == "trm_dll - Win32 Release"

# Begin Custom Build
OutDir=.\trm_dll___Win32_Release
InputPath="..\..\oggvorbis-win32sdk-1.0\bin\vorbis.dll"
InputName=vorbis

"$(OutDir)\$(InputName).dll" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	copy $(InputPath) $(OutDir)

# End Custom Build

!ELSEIF  "$(CFG)" == "trm_dll - Win32 Debug"

# Begin Custom Build
OutDir=.\trm_dll___Win32_Debug
InputPath="..\..\oggvorbis-win32sdk-1.0\bin\vorbis.dll"
InputName=vorbis

"$(OutDir)\$(InputName).dll" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	copy $(InputPath) $(OutDir)

# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE="..\..\oggvorbis-win32sdk-1.0\bin\vorbisfile.dll"

!IF  "$(CFG)" == "trm_dll - Win32 Release"

# Begin Custom Build
OutDir=.\trm_dll___Win32_Release
InputPath="..\..\oggvorbis-win32sdk-1.0\bin\vorbisfile.dll"
InputName=vorbisfile

"$(OutDir)\$(InputName).dll" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	copy $(InputPath) $(OutDir)

# End Custom Build

!ELSEIF  "$(CFG)" == "trm_dll - Win32 Debug"

# Begin Custom Build
OutDir=.\trm_dll___Win32_Debug
InputPath="..\..\oggvorbis-win32sdk-1.0\bin\vorbisfile.dll"
InputName=vorbisfile

"$(OutDir)\$(InputName).dll" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	copy $(InputPath) $(OutDir)

# End Custom Build

!ENDIF 

# End Source File
# End Group
# End Target
# End Project
