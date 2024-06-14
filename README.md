# Tile Based Engine
This was a collaboratory final project for my Data Structures and Algorithms course at UC Berkeley. This project is a randomly generated 2D tile game where you, the player, explore hallways and rooms, engaging with numerous events throughout your journey to graduate UC Berkeley. Your health is displayed as your GPA, as you engage with events in this randomly generated world, you are forced to answer questions from well-known professors at Cal. If you fail too many of these events, your GPA drops too low, subsequently forcing you to become a drop-out and lose the game.

Java is, to say it nicely, on it's last leg in the modern world, however, it was a nice language to implement a lot of the DSA strategies I learned in this course. As a collaboratory project, I should emphasize that my main contributions will be found in: Engine.java, World.java, Builder.java, Room.java, PathSearch.java, Sound.java, TileMap.java, and UserInterface.java. Of these, I would suggest an interested party should examine World.java, Builder.java, Room.java, and PathSearch.java, as those were the most fun for me, and make up the majority of the world generation, which was in part my main focus on this project.

Two things to note, which I found neat in this project, was my implementation of the mapping of our 2D world array to a hashmap in TileMap.java, for faster retrieval of tile information, as well as my implementation of the A* search algorithm in PathSearch.java, which was used to connect randomly generated rooms with hallways in this game. 

At a high level, World introduces all rooms, hallways, events, and players into the world array, which is the final output a player would be seeing on their screen. To build these components, the World class has a Builder, which builds and verifies the validity of all of these objects before introducing them into the final world array. This Builder relies on classes such as Room and PathSearch, which again provide another layer of secure and tested generation of objects before passing them on to their final representations in the output world array. World and Builder were very interesting to design, as they rely on abstraction from classes they utilize quite heavily. Room was also a lot of fun to build as I remember, due to having to solve the conflict of overlapping rooms being undesirable, as well as the generation and storage of random, valid entrances to these rooms. 

Verification of valid placement of objects in this game was a reoccuring theme. Not only did I have to tackle this issue with regards to room placement, but I also had to ensure the hallways connecting these rooms were valid as well. This was handled quite simply in a boolean method found in PathSearch. 

It should be noted that I also designed the UI, sound system, and game state saving system from scratch with no experience working in any of those domains. They are definitely not the most beautiful, and perhaps the most efficeint things I've ever made, but the final product was aesthetically something I was quite proud of, so I don't beat myself up too bad over it.

Overall, this was a fun and interesting project for me. I was able to demonstrate and implement different data structures and algorithms I learned in this course, and the end product was something both I and my partner were very proud of.

# How to Play

Run main.java in byow Core, your avatar is represented by an "@" symbol. To begin, click to generate a new world and enter a seed. A seed must be a string of integers beginning with an "N" and ending with an "S". Used WASD to move around, and "E" to interact with events.

# Contributions
I would like to give thanks to my partner Antonio Sanchez for his contributions on this project, as well as the CS Department as UC Berkeley for providing supporting libraries.




