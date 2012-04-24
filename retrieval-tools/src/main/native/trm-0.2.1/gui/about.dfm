object AboutForm: TAboutForm
  Left = 500
  Top = 496
  Width = 379
  Height = 242
  Caption = 'About TRM '
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -11
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  OldCreateOrder = False
  Position = poMainFormCenter
  PixelsPerInch = 96
  TextHeight = 13
  object Label3: TLabel
    Left = 6
    Top = 28
    Width = 358
    Height = 20
    Alignment = taCenter
    AutoSize = False
    Caption = '(c) 2001 Robert Kaye'
    Font.Charset = DEFAULT_CHARSET
    Font.Color = clWindowText
    Font.Height = -15
    Font.Name = 'MS Sans Serif'
    Font.Style = []
    ParentFont = False
    WordWrap = True
  end
  object infoLabel: TLabel
    Left = -2
    Top = 108
    Width = 368
    Height = 38
    Alignment = taCenter
    AutoSize = False
    Caption = 
      'For more information about the TRM Generator or the MusicBrainz ' +
      'project, please visit:'
    Font.Charset = DEFAULT_CHARSET
    Font.Color = clWindowText
    Font.Height = -15
    Font.Name = 'MS Sans Serif'
    Font.Style = []
    ParentFont = False
    WordWrap = True
  end
  object mbUrl: TLabel
    Left = 84
    Top = 148
    Width = 194
    Height = 19
    Alignment = taCenter
    AutoSize = False
    Caption = 'http://www.musicbrainz.org'
    Font.Charset = DEFAULT_CHARSET
    Font.Color = clBlue
    Font.Height = -15
    Font.Name = 'MS Sans Serif'
    Font.Style = [fsUnderline]
    ParentFont = False
  end
  object Label1: TLabel
    Left = 6
    Top = 9
    Width = 358
    Height = 16
    Alignment = taCenter
    AutoSize = False
    Caption = 'TRM Generator 0.1.0'
    Font.Charset = DEFAULT_CHARSET
    Font.Color = clWindowText
    Font.Height = -15
    Font.Name = 'MS Sans Serif'
    Font.Style = [fsBold]
    ParentFont = False
    WordWrap = True
  end
  object Label2: TLabel
    Left = 2
    Top = 59
    Width = 368
    Height = 40
    Alignment = taCenter
    AutoSize = False
    Caption = 
      'Acoustic fingerprint technology provided by Relatable(TM), Copyr' +
      'ight (C) 2000-2002, Relatable LLC, All Rights Reserved'
    Font.Charset = DEFAULT_CHARSET
    Font.Color = clWindowText
    Font.Height = -15
    Font.Name = 'MS Sans Serif'
    Font.Style = []
    ParentFont = False
    WordWrap = True
  end
  object Button1: TButton
    Left = 148
    Top = 180
    Width = 75
    Height = 25
    Caption = 'OK'
    Default = True
    TabOrder = 0
    OnClick = Button1Click
  end
end
