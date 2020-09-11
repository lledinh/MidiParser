package midi;

public class MidiConstants {
    public enum MidiType {
        HeaderChunk, TrackChunk;
    }

    public enum MidiEvent {
        TimeSignature, KeySignature, SetTempo, NoteOff, NoteOn
    }
}
