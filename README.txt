
  This is the main driver for our project which runs games between
  an Oracle and a Guesser.  There are several optional
  command-line arguments for altering the number of pegs,
  colors, games, degree of output, whether or not to use
  Knuth's five guess algorithm as the Guesser, whether or not
  to use a Guesser which learns, and whether or not to have
  the Oracle use codes from a file or randomly generate its own.
  
   Usage:
   -v       	(Turns on verbose output)
   -k	     	(Limits the number of colors and pegs to the standard 6 and 4, and uses the Knuth Guesser)
   -l		 	(Uses the Learner Guesser which takes into account information across Games)
   -p <int> 	(Sets the number of pegs to the specified value)
   -c <int> 	(Sets the number of colors to the specified value)
   -g <int> 	(Sets the number of games to be played to the specified value)
   -b <String>	(Turns on bias logic for the specified bias by way of a bias tag String-- was used for testing)
   <String>	(If a valid Oracle file, sets the Oracle to use codes from this file-- the number of pegs, colors, and
                games will be determined by the file and the corresponding command-line arguments will be ignored; also
                overrides the Knuth Guesser option if the Oracle file doesn't contain standard (4x6) game codes)