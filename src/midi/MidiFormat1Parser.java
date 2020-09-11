package midi;

import java.util.ArrayList;
import java.util.List;

public class MidiFormat1Parser extends MidiParser {
    public MidiFormat1Parser() {

    }

    private int getBytes(int numBytesToRead) {
        int value = 0;

        for (int i = cursor; i < cursor + numBytesToRead; i++) {
            value |= (((int) bytes[i] & 0xFF));
            if (i != (cursor + numBytesToRead - 1)) {
                value <<= 8;
            }
        }

        advanceCursor(numBytesToRead);

        return value;
    }

    private void advanceCursor(int numBytes) {
        cursor += numBytes;
    }

    private void rewindCursor(int numBytes) {
        cursor -= numBytes;
    }

    private void readHeader() {
        typeHeader = getBytes(4);
        lengthHeader = getBytes(4);
        midiFileFormat = getBytes(2);
        tracks = getBytes(2);
        parseDivision(getBytes(2));
    }

    private List<MidiMessage> readMidiTrack() {
        List<MidiMessage> messages = new ArrayList<>();

        int typeTrack = getBytes(4);
        System.out.println("typeTrack = " + Integer.toHexString(typeTrack));
        int lengthTrack = getBytes(4);
        System.out.println("lengthTrack = " + lengthTrack);

        int deltaTime = getBytes(1);
        int timeSignatureEvent = getBytes(3);
        int timeSignatureNumerator = getBytes(1);
        int timeSignatureDenominator = getBytes(1);
        int timeSignatureMidiClocksPerMetronomeTick = getBytes(1);
        int timeSignatureNumber32thPer24MidiClocks = getBytes(1);
        System.out.println("deltaTime = " + deltaTime);

        System.out.println("timeSignatureEvent = " + Integer.toHexString(timeSignatureEvent));
        System.out.println("timeSignatureNumerator = " + timeSignatureNumerator);
        System.out.println("timeSignatureDenominator = " + timeSignatureDenominator);
        System.out.println("timeSignatureMidiClocksPerMetronomeTick = " + timeSignatureMidiClocksPerMetronomeTick);
        System.out.println("timeSignatureNumber32thPer24MidiClocks = " + timeSignatureNumber32thPer24MidiClocks);

        MidiMessage timeSignatureMidiMessage  = new MidiMessage();
        timeSignatureMidiMessage.setDeltaTime(deltaTime);
        timeSignatureMidiMessage.setMidiEvent(MidiConstants.MidiEvent.TimeSignature);
        timeSignatureMidiMessage.addArgument(timeSignatureNumerator);
        timeSignatureMidiMessage.addArgument(timeSignatureDenominator);
        timeSignatureMidiMessage.addArgument(timeSignatureMidiClocksPerMetronomeTick);
        timeSignatureMidiMessage.addArgument(timeSignatureNumber32thPer24MidiClocks);
        messages.add(timeSignatureMidiMessage);

        deltaTime = getBytes(1);
        int keySignatureEvent = getBytes(3);
        int numberSharpsOrFlats = getBytes(1);
        int majorOrMinor = getBytes(1);
        System.out.println("timeSignatureEvent = " + Integer.toHexString(keySignatureEvent));
        System.out.println("numberSharpsOrFlats = " + numberSharpsOrFlats);
        System.out.println("majorOrMinor = " + majorOrMinor);

        MidiMessage keySignatureMidiMessage  = new MidiMessage();
        keySignatureMidiMessage.setDeltaTime(deltaTime);
        keySignatureMidiMessage.setMidiEvent(MidiConstants.MidiEvent.KeySignature);
        keySignatureMidiMessage.addArgument(numberSharpsOrFlats);
        keySignatureMidiMessage.addArgument(majorOrMinor);
        messages.add(keySignatureMidiMessage);

//        FF 51 03 tt tt tt
        deltaTime = getBytes(1);
        int setTempoEvent = getBytes(3);
        int microSeconds = getBytes(3);
        System.out.println("setTempoEvent = " + Integer.toHexString(setTempoEvent));
        System.out.println("microSeconds = " + microSeconds);

        MidiMessage setTempoMidiMessage  = new MidiMessage();
        setTempoMidiMessage.setDeltaTime(deltaTime);
        setTempoMidiMessage.setMidiEvent(MidiConstants.MidiEvent.SetTempo);
        setTempoMidiMessage.addArgument(microSeconds);
        messages.add(setTempoMidiMessage);

        return messages;
    }

    @Override
    public List<MidiMessage> parse() {
        readHeader();
        System.out.println("typeHeader = " + typeHeader);
        System.out.println("typeHeader = " + Integer.toHexString(typeHeader));
        System.out.println("lengthHeader = " + lengthHeader);
        System.out.println("midiFileFormat = " + midiFileFormat);
        System.out.println("tracks = " + tracks);
        System.out.println("divisionFormat = " + divisionFormat);
        System.out.println("ticksQuarterNote = " + ticksQuarterNote);

       return readMidiTrack();
    }
}
