package midi;

public class MidiConstants {
    public enum MidiType {
        HeaderChunk, TrackChunk;
    }

    public enum MidiEvent {
        SequenceNumber, TimeSignature, KeySignature, SetTempo, NoteOff, NoteOn, Text, Copyright, TrackName, InstrumentName,
        Lyric, Marker, 	CuePoint, MIDIChannelPrefix, EndOfTrack, SequencerSpecificMetaEvent, F0SysexEvent, F7SysexEvent,
        NullEvent, ControllerChange, PolyphonicKeyPressure, ProgramChange, ChannelKeyPressure, PitchBend, MidiPort,
        ProgramName, DevicePort
    }
}
