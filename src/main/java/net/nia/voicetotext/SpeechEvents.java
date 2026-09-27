package net.nia.voicetotext;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class SpeechEvents {
    public static final Event<SpeechListener> SPEECH_RECOGNIZED =
            EventFactory.createArrayBacked(SpeechListener.class,
                    listeners -> text -> {
                        for (SpeechListener l : listeners) {
                            l.onSpeechRecognized(text);
                        }
                    });
}