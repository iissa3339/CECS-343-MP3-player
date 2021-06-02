# CECS-343-MP3-player
An mp3 player that uses a GUI to interact with pc users to play songs and create personalized playlists.\
Do NOT use MusicPlayerGUI.java, that was for iteration 1 of the project, use GUI.java instead!\
To use this program, you have to download XAMPP to connect to SQL and apache. After the download start Apache then start SQL.\
After that, click on Admin to the right of SQL, it will take you to phpmyadmin\
Create a new database and name it "mp3" then add a table called "songs" to it\
The table will have 8 columns with the following names in order and using this casing:\
SongId            VarChar       100         Default: NULL     A.I.\
Title             VarChar       100\
Artist            VarChar       100\
Genre             VarChar       100\
Release Year      Int           100\
Comments          VarChar       100\
Location          VarChar       100\
playlists         VarChar       100\
\
You can now run the program, also make sure to download the basicplayer package and add its files to the project\
