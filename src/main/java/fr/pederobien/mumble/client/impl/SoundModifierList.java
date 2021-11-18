package fr.pederobien.mumble.client.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import fr.pederobien.mumble.client.interfaces.ISoundModifier;
import fr.pederobien.mumble.client.interfaces.ISoundModifierList;

public class SoundModifierList implements ISoundModifierList {
	private Map<String, ISoundModifier> soundModifiers;

	public SoundModifierList() {
		soundModifiers = new HashMap<String, ISoundModifier>();
	}

	@Override
	public Iterator<ISoundModifier> iterator() {
		return soundModifiers.values().iterator();
	}

	@Override
	public void register(ISoundModifier soundModifier) {
		soundModifiers.put(soundModifier.getName(), soundModifier);
	}

	@Override
	public void unregister(String name) {
		soundModifiers.remove(name);
	}

	@Override
	public Optional<ISoundModifier> getByName(String name) {
		ISoundModifier modifier = soundModifiers.get(name);
		return Optional.ofNullable(modifier == null ? null : modifier.clone());
	}

	@Override
	public Map<String, ISoundModifier> getSoundModifiers() {
		return soundModifiers;
	}
}
