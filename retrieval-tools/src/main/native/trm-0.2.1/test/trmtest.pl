#!/usr/bin/perl -w

use strict;
use Audio::CD;

my ($track, @tracks, $info, $trm, $outfile);
my ($tracklist, $cd, $cdinfo, $tinfo, $mins, $secs, $duration);

$outfile = shift;
if (not defined $outfile)
{
    print "Usage: trmtest.pl <output file>\n";
    exit(0);
}

$cd = Audio::CD->init;
$cdinfo = $cd->stat;
$tracklist = $cdinfo->tracks;

$track = 1;
foreach $tinfo (@{$tracklist})
{
    $info = {};

    ($mins, $secs) = $tinfo->length();
    $duration = ($mins * 60 + $secs) * 1000;

    my $ret = system("cdparanoia $track-$track:\[31.0] cdda.wav") / 256;
    last if ($ret);

    system("lame cdda.wav cdda.mp3");
    system("oggenc cdda.wav");

    print STDERR "TRM wav... ($duration) ";
    open TRM, "trm -d $duration cdda.wav|" or die;
    $trm = <TRM>;
    close TRM;
    chop($trm);

    if (defined $trm && $trm ne '')
    {
        $info->{wav} = $trm;
        print STDERR "$trm\n";
    }
    else
    {
        print STDERR "Cannot get WAV TRM from track $track\n";
    }

    print STDERR "TRM mp3... ($duration) ";
    open TRM, "trm -d $duration cdda.mp3|" or die;
    $trm = <TRM>;
    close TRM;
    chop($trm);

    if (defined $trm && $trm ne '')
    {
        $info->{mp3} = $trm;
        print STDERR "$trm\n";
    }
    else
    {
        print STDERR " Cannot get MP3 TRM from track $track\n";
    }

    print STDERR "TRM ogg... ($duration) ";
    open TRM, "trm -d $duration cdda.ogg|" or die;
    $trm = <TRM>;
    close TRM;
    chop($trm);

    if (defined $trm && $trm ne '')
    {
        $info->{ogg} = $trm;
        print STDERR "$trm\n";
    }
    else
    {
        print STDERR "Cannot get Ogg/Vorbis TRM from track $track\n";
    }

    unlink("cdda.wav");
    unlink("cdda.mp3");
    unlink("cdda.ogg");

    $info->{track} = $track;

    push @tracks, $info;

    $track++;
}

open OUT, ">$outfile" or die;

print OUT '"name","wav sig","ogg sig","mp3 sig"' . "\n";
foreach $track (@tracks)
{
   printf OUT "\"track%02d\", ", $track->{track}; 
   print OUT "\"$track->{wav}\", ";
   print OUT "\"$track->{ogg}\", ";
   print OUT "\"$track->{mp3}\"\n";
}
close OUT;
