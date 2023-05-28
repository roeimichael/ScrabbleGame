\# Scrabble Game

This is a Java-based Scrabble game developed with an MVVM
(Model-View-ViewModel) architectural pattern and client-server
connectivity. The game follows the original rules of Scrabble, where
players take turns forming words on a 15x15 game board.

\## Features

\- MVVM Structure: The project follows the MVVM architectural pattern,
which separates the application into three main components: Model, View,
and ViewModel. This structure promotes separation of concerns and
maintainability of the codebase.

\- Client-Server Connectivity: The game supports multiplayer
functionality through client-server connectivity. Multiple players can
connect to a central server and play the game simultaneously.

\- Letter Tiles and Bag: At the start of the game, each player receives
seven letter tiles randomly selected from a bag containing a total of 98
letters. The letter distribution adheres to the original rules of
Scrabble.

\- Word Validation: To ensure that words entered by players are valid,
the game uses a word validation mechanism. It scans multiple
dictionaries and checks if a given word exists. The validation process
utilizes various layers of memory, including Bloom Filters and IO
Searcher, to optimize the search performance.

\## Prerequisites

\- Java Development Kit (JDK) version 19 or higher

\## Installation

1\. Clone the repository:

\`\`\`shell git clone https://github.com/roeimichael/ScrabbleGame.git
\`\`\`

2\. run the Main class

\## Usage

1\. Launch the Scrabble game application. 2. Connect to the server by
entering the server IP address and port. 3. Wait for other players to
connect to the server. 4. Once all players have joined, the game begins.
5. On your turn, form words on the game board using your letter tiles.
6. Place the tiles in the correct place that you want to foramt the word
on the board. 7. The word will be validated against the dictionaries to
ensure its validity. 8. Points are calculated based on the letters used
and their corresponding values. 9. Play continues with each player
taking turns until the game ends. 10. The game ends when all letter
tiles are used or when no more valid words can be formed. 11. The player
with the highest score wins the game.

\## Contact

If you have any questions, suggestions, or feedback, please feel free to
contact us!

Happy Scrabble gaming!
