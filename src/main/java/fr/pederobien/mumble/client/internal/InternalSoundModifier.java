package fr.pederobien.mumble.client.internal;

import java.util.function.Consumer;

import fr.pederobien.mumble.client.event.SoundModifierNameChangePostEvent;
import fr.pederobien.mumble.client.event.SoundModifierNameChangePreEvent;
import fr.pederobien.mumble.client.impl.MumbleConnection;
import fr.pederobien.mumble.client.interfaces.IChannel;
import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.client.interfaces.ISoundModifier;
import fr.pederobien.utils.event.EventManager;

public class InternalSoundModifier implements ISoundModifier {
	private MumbleConnection connection;
	private IChannel channel;
	private String name;

	public InternalSoundModifier(MumbleConnection connection, IChannel channel, String name) {
		this.connection = connection;
		this.channel = channel;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name, Consumer<IResponse> callback) {
		EventManager.callEvent(new SoundModifierNameChangePreEvent(this, name), () -> connection.setChannelModifierName(channel.getName(), name, callback));
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof ISoundModifier))
			return false;

		ISoundModifier other = (ISoundModifier) obj;
		return name.equals(other.getName());
	}

	public void internalSetName(String name) {
		if (this.name.equals(name))
			return;
		String oldName = new String(this.name);
		this.name = name;
		EventManager.callEvent(new SoundModifierNameChangePostEvent(this, oldName));
	}
}
