# Distributed Movie Watch (--- IN PROGRESS ---)
Distributed system for watching and uploading videos (movies mostly). Software operates in a system consisted of multiple computers that are connected in LAN or WAN.

## System architecture
In this system there are 3 types of programs:
  1. Main (central) server: who is controlling subservers and is responsible for communication between them.
  2. Subservers: which are used for keeping user's credentials and are responsible for communication with users.
  3. User application: which is used by user to login and register into system, view and read notifications, and last but not least, to upload and watch movies.

## Movie watching
When it comes to watching a movie, user has two possibilities:
  - to watch it alone, by him- or herself.
  - to create a room and watch it with other users.

Standalone watching is completely similiar to any online movie/video watching. 

But, room watching is much more interesting. It aims to provide a feeling of cinema watching. All users in the room are watching movie live-stream, but from the moment where user who had created the room has reached (all users are watching the same scene at the moment).
Also, only room admin (user who has created the room) has controls over movie stream. For example: when room admin presses pause, it pauses for everyone in the room.


## Credits
Icons used within this project have been downloaded from [Flaticon.com]

Preloaders used within this project have been downloaded from [loading.io]