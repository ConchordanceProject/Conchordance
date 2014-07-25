package com.conchordance.sound;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import com.conchordance.fingers.ChordFingering;
import com.conchordance.music.Note;


public class ChordPlayer {

	private static final int STRUM_TRACK = 0;
	private static final int A_0_MIDI_NUMBER = 21;
	private static final int DECAY_TIME_CONTROLLER = 75;
	private static final int ACCOUSTIC_GUITAR_INSTRUMENT = 25;
	
	public void strumChord(ChordFingering chord) {
		if (sequencer == null)
			return;
		
		try {
			Sequence sequence = new Sequence(Sequence.PPQ, 100);
			Track track = sequence.createTrack();
			
			track.add(new MidiEvent(new ShortMessage(ShortMessage.PROGRAM_CHANGE, STRUM_TRACK, ACCOUSTIC_GUITAR_INSTRUMENT, 0), 0));
			
			// Increase the Decay Time for a string effect
			track.add(new MidiEvent(new ShortMessage(ShortMessage.CONTROL_CHANGE, STRUM_TRACK, DECAY_TIME_CONTROLLER, 100), 0));
			
			// A downward strum, starts at the highest-numbered string
			int time = 0;
			int duration = 100;
			for (int s = chord.numStrings-1; s >=0; --s) {
				// Play a note if this string is not muted
				if (chord.notes[s] != null) {
					Note n = chord.notes[s].note;
					int pitch = n.halfSteps + A_0_MIDI_NUMBER;
					MidiEvent onEvent = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, STRUM_TRACK, pitch, 100), time);
					track.add(onEvent);
					
					MidiEvent offEvent = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, STRUM_TRACK, pitch, 100), time+duration);
					track.add(offEvent);
					
					time += 5;
				}
			}
			
			sequencer.close();
			sequencer.open();
			sequencer.setSequence(sequence);
			sequencer.start();
		} catch (InvalidMidiDataException | MidiUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public ChordPlayer() {
		try {
			sequencer = MidiSystem.getSequencer();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	private Sequencer sequencer;
}
