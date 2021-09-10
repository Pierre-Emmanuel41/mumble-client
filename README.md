# 1) Presentation

The project Mumble is a client-server application used essentially to link the audio volume of players and their positions in game. It has been developed in order to be independent from games. What matter is the X,Y,Z-coordinates, yaw and pitch orientation. In order to update those values for Mumble, developers need to implement a plugin for the game (whatever it is) in order to link the player coordinates in the game and the player coordinates in Mumble.  
This project correspond to the model layer for the client side. It integrates the project [sound](https://github.com/Pierre-Emmanuel41/sound) in order to get access to the microphone and to the speakers, and the project [mumble-common](https://github.com/Pierre-Emmanuel41/mumble-common) in order to send and receive requests from the mumble server.

# 2) Download

First you need to download this project on your computer. To do so, you can use the following command line :

```git
git clone https://github.com/Pierre-Emmanuel41/mumble-common.git --recursive
```

and then double click on the deploy.bat file. This will deploy this project and all its dependencies on your computer. Which means it generates the folder associated to this project and its dependencies in your .m2 folder. Once this has been done, you can add the project as maven dependency on your maven project :

```xml
<dependency>
	<groupId>fr.pederobien</groupId>
	<artifactId>mumble-client</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```

# 3) Tutorial

In order to create a connection with the Mumble server, the developer needs to create a unique object : <code>IMumbleServer</code>. From this object, it is very simple to send requests to the server. To create a mumble server, the developer needs to instantiate one :

```java
// Server name
String name = "Mumble Server";

// Server address
String address = "127.0.0.1";

// Tcp port
int port = 50000;
IMumbleServer mumbleServer = new MumbleServer(name, address, port);
```

Then the server needs to be opened (using the method <code>open</code>). This has for consequences to throw several events:  

- [ConnectionCompleteEvent](https://github.com/Pierre-Emmanuel41/communication/blob/master/src/main/java/fr/pederobien/communication/event/ConnectionCompleteEvent.java) if the server is running.
- [ServerReachableChangeEvent](https://github.com/Pierre-Emmanuel41/mumble-client/blob/master/src/main/java/fr/pederobien/mumble/client/event/ServerReachableChangeEvent.java) if the server is reachable.

From this interface, the developer has access to :  

- [IPlayer](https://github.com/Pierre-Emmanuel41/mumble-client/blob/master/src/main/java/fr/pederobien/mumble/client/interfaces/IPlayer.java) it correspond to the associated to player currently connected in game.
- [IChannelList](https://github.com/Pierre-Emmanuel41/mumble-client/blob/master/src/main/java/fr/pederobien/mumble/client/interfaces/IChannelList.java) it correspond to the list of channel registered on the server.
- [IAudioConnection](https://github.com/Pierre-Emmanuel41/mumble-client/blob/master/src/main/java/fr/pederobien/mumble/client/interfaces/IAudioConnection.java) It is the object that receive from the microphone and send it to server using UDP protocol and send data to the speakers when receiving data from the server.

Those classes throws many event the developer can find [this package](https://github.com/Pierre-Emmanuel41/mumble-client/tree/master/src/main/java/fr/pederobien/mumble/client/event).

To catch those events, the developer needs a class that implements the interface <code>IEventListener</code> :

``` java
public class EventListener implements IEventListener {

	public ConnectionEventListener() {
		EventManager.registerListener(this);
	}

	@EventHandler
	private void onConnectionCompleteEvent(ConnectionCompleteEvent event) {

	}

	@EventHandler
	private void onConnectionDisposedEvent(ConnectionDisposedEvent event) {

	}

	@EventHandler
	private void onDataReceivedEvent(DataReceivedEvent event) {

	}
}
```