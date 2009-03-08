use strict;
use Getopt::Long;
use DE_EPAGES::Core::API::Script qw( RunScript );
use DE_EPAGES::Core::API::Log qw( GetLog LogDebug );
use DE_EPAGES::Core::API::File qw( GetFileContent GetFileNames);

sub StatisticPokerPrefix($) {
    my ($Prefix) = @_;
    my %Info;
    # my $Log = GetLog();
    # $Log->debug("Prefix: $Prefix");
    $Info{'BuyIn'} = $1 if $Prefix =~ /Buy-In\s*:\s*\$(\d+.?\d*)/;
    $Info{'Fee'}   = $1 if $Prefix =~ /Fee\s*:\s*\$(\d+.?\d*)/;
    $Info{'Date'} = $1 if $Prefix =~ /Played on\s*:\s*([^\r\n]+)/;
    $Info{'TableNo'} = $1 if $Prefix =~ /On Table\s+:\s+Table\s+(\d+)/;
    $Info{'Game'} = $1 if $Prefix =~ /Game Type\s+:\s+([^\r\n]+)/;
    $Info{'Position'} = $1 if $Prefix =~ /You finished in\s+position\s+(\d+)/;
    $Prefix =~ s/place\s+-\s+(\S+)\s+-\s+\$(\d+.?\d*)/$Info{'Winner'}{$1} = $2/eg;
    LogDebug('Prefix', \%Info);
    return \%Info;
}

use constant BET_PHASE_ALL     => 0;
use constant BET_PHASE_PREFLOP => 1;
use constant BET_PHASE_FLOP    => 2;
use constant BET_PHASE_TURN    => 3;
use constant BET_PHASE_RIVER   => 4;
my @BET_PHASE_NAMES = qw (All PreFlop Flop Turn River);

sub StatisticPokerHand {
    my ($Hand, $hHands) = @_;
    my $BetPhase = BET_PHASE_PREFLOP;
    my $PottSize = 0;
    my $CountPlayers = 99;
    my %PlayersCountPhase = ();
    foreach my $Line (split /\r?\n/, $Hand) {
        next if $Line !~ /\S/;
        next if $Line =~ /Tournament/;
        next if $Line =~ /is the button/;
        next if $Line =~ /is all\-In/;
        next if $Line =~ /^Dealt to/; # my cards
        next if $Line eq "** Summary **";
        next if $Line =~ /^Main Pot:/;
        next if $Line =~ /^Creating (Main|Side) Pot/;
        next if $Line =~ /^Board:/;
        if ($Line =~ /^Total number of players : (\d+)/) {
            $CountPlayers = $1;
        } elsif ($Line =~ /^Seat \d+: (\S+) \(\d+\)/) {
            $hHands->{$1}{$CountPlayers}[BET_PHASE_ALL]{'Games'}++;
        } elsif ($Line =~ /^(\S+)\s+finished in (\d+) place./) {
            $hHands->{$1}{$CountPlayers}[BET_PHASE_ALL]{'Place'} = $2;
        } elsif ($Line =~ /^(\S+)\s+posts ante \((\d+)\)/) {
            $PottSize += $2;
        } elsif ($Line =~ /^(\S+)\s+posts small blind \((\d+)\)/) {
            $PottSize += $2;
        } elsif ($Line =~ /^(\S+)\s+posts big blind \((\d+)\)/) {
            $PottSize += $2;
        } elsif ($Line =~ /^(\S+) folds\./) {
            $hHands->{$1}{$CountPlayers}[$BetPhase]{'Folds'}++;
            if (not exists $PlayersCountPhase{$1}) {
                $hHands->{$1}{$CountPlayers}[$BetPhase]{'Count'}++;
                $PlayersCountPhase{$1} = 1;
            }
        } elsif ($Line =~ /^(\S+) checks\./) {
            $hHands->{$1}{$CountPlayers}[$BetPhase]{'Checks'}++;
            if (not exists $PlayersCountPhase{$1}) {
                $hHands->{$1}{$CountPlayers}[$BetPhase]{'Count'}++;
                $PlayersCountPhase{$1} = 1;
            }
        } elsif ($Line =~ /^(\S+) calls \((\d+)\)/) {
            $hHands->{$1}{$CountPlayers}[$BetPhase]{'Calls'}++;
            if (not exists $PlayersCountPhase{$1}) {
                $hHands->{$1}{$CountPlayers}[$BetPhase]{'Count'}++;
                $PlayersCountPhase{$1} = 1;
            }
        } elsif ($Line =~ /^(\S+) (bets) \((\d+)\)/) {
            $hHands->{$1}{$CountPlayers}[$BetPhase]{'Bets'}++;
            if (not exists $PlayersCountPhase{$1}) {
                $hHands->{$1}{$CountPlayers}[$BetPhase]{'Count'}++;
                $PlayersCountPhase{$1} = 1;
            }
        } elsif ($Line =~ /^(\S+) (raises) \((\d+)\)/) {
            $hHands->{$1}{$CountPlayers}[$BetPhase]{'Raises'}++;
            if (not exists $PlayersCountPhase{$1}) {
                $hHands->{$1}{$CountPlayers}[$BetPhase]{'Count'}++;
                $PlayersCountPhase{$1} = 1;
            }
        } elsif ($Line =~ /\Q** Dealing down cards\E/) {
            $BetPhase = BET_PHASE_PREFLOP;
            $PottSize = 0;
            %PlayersCountPhase = ();
        } elsif ($Line =~ /\Q** Dealing Flop\E/) {
            $BetPhase = BET_PHASE_FLOP;
            %PlayersCountPhase = ();
        } elsif ($Line =~ /\Q** Dealing Turn\E/) {
            $BetPhase = BET_PHASE_TURN;
            %PlayersCountPhase = ();
        } elsif ($Line =~ /\Q** Dealing River\E/) {
            $BetPhase = BET_PHASE_RIVER;
            %PlayersCountPhase = ();
        } elsif ($Line =~ /^(\S+) balance \d+,\s+/) {
            my $Player = $1;
            my $Rest = $';
            #Kegio balance 2480, bet 200, collected 300, net +100
            #bianca70 balance 2240, didn't bet (folded)
            #john55113 balance 1620, lost 100 (folded)
            #Kegio balance 1540, bet 1500, collected 160, lost -1340

            if ($Rest eq "didn't bet (folded)") {
                $hHands->{$Player}{$CountPlayers}[BET_PHASE_ALL]{'Folded'}++;
                $hHands->{$Player}{$CountPlayers}[BET_PHASE_ALL]{'Count'}++;
                # nothing
            } elsif ($Rest =~ /bet \d+, collected \d+, net \+(\d+)/) {
                $hHands->{$Player}{$CountPlayers}[$BetPhase]{'Wins'}++;
                $hHands->{$Player}{$CountPlayers}[BET_PHASE_ALL]{'Wins'}++;
                $hHands->{$Player}{$CountPlayers}[BET_PHASE_ALL]{'Count'}++;
            } elsif ($Rest =~ /lost (\d+)/ or
                     $Rest =~ /bet \d+, collected \d+, lost \-(\d+)/) {
                $hHands->{$Player}{$CountPlayers}[BET_PHASE_ALL]{'Losts'}++;
                $hHands->{$Player}{$CountPlayers}[BET_PHASE_ALL]{'Count'}++;
            } else {
                die $Rest;
            }
        } else {
            warn $Line,"\n";
        }
    }
}

sub StatisticPokerFile ($$) {
    my ($FileName, $rPlayer) = @_;
    my $rContent = GetFileContent($FileName);
    my @Hands = split /\*\*\*\*\* Hand History for Game \d+ \*\*\*\*\*/, $$rContent;
    my $hPrefix = StatisticPokerPrefix(shift @Hands);

    # statistic hands
    StatisticPokerHand($_, $rPlayer) foreach @Hands;
    LogDebug('$rPlayer', $rPlayer);
    return;
}

my $USER;
sub PrintSummaryPokerFiles ($){
    my ($Summary) = @_;
    printf "\t%7s: %3s %2s %2s %2s %2s %2s\t(%4s %4s %4s %4s %4s %4s)\n", qw (Phase Co Fo Ch Ca Be Ra Co Fo Ch Ca Be Ra );
    foreach my $NickName (sort keys %$Summary) {
        next if defined $USER and $USER ne $NickName;
        my $hCountPlayer = $Summary->{$NickName};
        delete $hCountPlayer->{99};
        foreach my $CountPlayers (sort {$b <=> $a }keys %$hCountPlayer) {
            printf "%s: %2d Players\n", $NickName, $CountPlayers;
            my $hPlayer = $hCountPlayer->{$CountPlayers};
            foreach my $BetPhase (BET_PHASE_PREFLOP..BET_PHASE_RIVER) {
                if ($hPlayer->[$BetPhase]{'Count'} > 0) {
                    printf "\t%7s: %3d %2d %2d %2d %2d %2d", $BET_PHASE_NAMES[$BetPhase], map { $hPlayer->[$BetPhase]{$_}} qw(Count Folds Checks Calls Bets Raises);
                    printf "\t(%.1f%% %.1f%% %.1f%% %.1f%% %.1f%% %.1f%%)\n", $hPlayer->[$BetPhase]{'Count'} / $hPlayer->[0]{'Count'} * 100, map { $hPlayer->[$BetPhase]{$_} / $hPlayer->[$BetPhase]{'Count'} * 100} qw( Folds Checks Calls Bets Raises);
                }
            }
        }
    }
}

sub Main {

    local $| = 1;

    my ($Help, $IsFile);
    GetOptions(
        'help' => \$Help,
        'file' => \$IsFile,
        'user=s' => \$USER,
    );

    usage() if $Help;
    my %Player;
    if ($IsFile) {
        StatisticPokerFile($_, \%Player) foreach @ARGV;
    } else {
        foreach my $Directory (@ARGV) {
            my $aFileNames = GetFileNames($Directory);
            StatisticPokerFile($_, \%Player) foreach grep { /\.txt$/ } @$aFileNames;
        }
    }
    PrintSummaryPokerFiles(\%Player);
}

sub usage {
    print <<END_USAGE;
Description:
    Finds the object id by object path

Usage:
    perl $0 [options] [flags] dir1 [dir2 ...]
    perl E:\\Projects\\Eclipse\\Poker\\statistics\\pokerStatistic.pl . >D:\\test.csv
options:

flags:
    -help       show the command line options

Example:
    perl $0 20070503_33226063.txt
END_USAGE
    exit 2;
}

RunScript(
    'Sub' => \&Main
);
