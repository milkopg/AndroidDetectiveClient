SoftUni Android Development project description

“Android Detective” is a program which helps you to “track” the closest people around you. It contains two applications:
-	"Android Detective Client" is being installed on the client smartphone and it it’s sending information about calls, messages, contact lists, taken images
-	"Android Detective Server" is receiving above information. It could read separate data, sorting information, filter by date, deleting pictures, changing settings.
Communication between client and server is realized through queueing system RabbitMQ and it’s also separated into client and server part. There is additional backup of data in JSON Blob Free Cloud just in case that RabbitMQ data is corrupted. Also it’s implemented retry sending data functionality in cases when there is no internet connection on client or on server, or in case there is internet on both devices.

Android detective is very useful if you want to keep an eye on your child, or if your wife is cheating you, or if you employees are lying you.
