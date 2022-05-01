package fr.pederobien.mumble.client.external.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import fr.pederobien.mumble.client.external.interfaces.ISoundModifier;
import fr.pederobien.mumble.client.external.interfaces.ISoundModifierList;

public class SoundModifierList implements ISoundModifierList {
	private static final String DEFAULT_SOUND_MODIFIER_NAME = "default";
	private Map<String, ISoundModifier> soundModifiers;

	public SoundModifierList() {
		soundModifiers = new HashMap<String, ISoundModifier>();
	}

	@Override
	public Iterator<ISoundModifier> iterator() {
		return soundModifiers.values().iterator();
	}

	@Override
	public Optional<ISoundModifier> get(String name) {
		ISoundModifier modifier = soundModifiers.get(name);
		return Optional.ofNullable(modifier == null ? null : modifier.clone());
	}

	@Override
	public ISoundModifier getDefaultSoundModifier() {
		return get(DEFAULT_SOUND_MODIFIER_NAME).get();
	}

	@Override
	public Stream<ISoundModifier> stream() {
		return toList().stream();
	}

	@Override
	public List<ISoundModifier> toList() {
		return new ArrayList<ISoundModifier>(soundModifiers.values());
	}

	protected void register(ISoundModifier soundModifier) {
		soundModifiers.put(soundModifier.getName(), soundModifier);
	}

	protected void unregister(String name) {
		soundModifiers.remove(name);
	}
}
