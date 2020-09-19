package midi;

import encoding.VariableLengthQuantity;

import java.util.ArrayList;
import java.util.List;

public class MidiFormat1Parser extends MidiParser {

    public MidiFormat1Parser() {

    }

    private List<Character> getChars(int numBytesToRead) {
        List<Character> bytesList = new ArrayList<>();

        for (int i = cursor; i < cursor + numBytesToRead; i++) {
            bytesList.add((char) bytes[i]);
        }

        advanceCursor(numBytesToRead);

        return bytesList;
    }

    private List<Integer> getBytes(int numBytesToRead) {
        List<Integer> nextBytes = new ArrayList<>();

        for (int i = cursor; i < cursor + numBytesToRead; i++) {
            nextBytes.add((int) bytes[i]);
        }

        return nextBytes;
    }

    private int getBytesAsInt(int numBytesToRead) {
        return getBytesAsInt(numBytesToRead, true);
    }

    private int getBytesAsInt(int numBytesToRead, boolean advanceCursor) {
        int value = 0;

        for (int i = cursor; i < cursor + numBytesToRead; i++) {
            value |= (((int) bytes[i] & 0xFF));
            if (i != (cursor + numBytesToRead - 1)) {
                value <<= 8;
            }
        }

        if (advanceCursor) {
            advanceCursor(numBytesToRead);
        }

        return value;
    }

    private void advanceCursor(int numBytes) {
        cursor += numBytes;
    }

    private void rewindCursor(int numBytes) {
        cursor -= numBytes;
    }

    private void readHeader() {
        typeHeader = getBytesAsInt(4);
        lengthHeader = getBytesAsInt(4);
        midiFileFormat = getBytesAsInt(2);
        tracks = getBytesAsInt(2);
        parseDivision(getBytesAsInt(2));
    }

    private int getVariableLengthQuantity() {
        List<Integer> currentBytes = new ArrayList<>();

        boolean isValidVLE = false;
        while(!isValidVLE) {
            int currentByte = getBytesAsInt(1);
            System.out.println("currentByte = " + currentByte);
            currentBytes.add(currentByte);
            isValidVLE = ((currentByte & 0x80) == 0);
        }

        int nb = 0;

        for (int hexByte : currentBytes) {
            if (nb != 0) {
                nb <<= 8;
            }

            nb += hexByte;
        }

        return VariableLengthQuantity.decode(nb);
    }

    private MidiMessage readTextEvent() {
        MidiMessage midiMessage = new MidiMessage();

        int len = getVariableLengthQuantity();
        List<Character> characters = getChars(len);
        for (Character c : characters) {
            midiMessage.addArgument((int) c);
        }

        return midiMessage;
    }

    private List<MidiMessage> readMidiTrack() {
        List<MidiMessage> messages = new ArrayList<>();

        int typeTrack = getBytesAsInt(4);
        int lengthTrack = getBytesAsInt(4);

        int deltaTime = getVariableLengthQuantity();
        System.out.println("variableLengthQuantity = " + deltaTime);

        List<Integer> readBytes = new ArrayList<>();
        // 1st byte
        readBytes.add(getBytesAsInt(1));
        int byteTypeMessage = readBytes.get(0);



        MidiMessage midiMessage = new MidiMessage();

        if (byteTypeMessage == 0xF0) {
            int len = getVariableLengthQuantity();
            List<Integer> bytes = getBytes(len);

            midiMessage.setMidiEvent(MidiConstants.MidiEvent.F0SysexEvent);
            for (int b : bytes) {
                midiMessage.addArgument(b);
            }
        }

        if (byteTypeMessage == 0xF7) {
            int len = getVariableLengthQuantity();
            List<Integer> bytes = getBytes(len);

            midiMessage.setMidiEvent(MidiConstants.MidiEvent.F7SysexEvent);
            for (int b : bytes) {
                midiMessage.addArgument(b);
            }
        }

        if (byteTypeMessage == 0xFF) {
            // 2nd byte
            readBytes.add(getBytesAsInt(1));

            switch(readBytes.get(1)) {
                case 0x00:
                    // 3rd
                    readBytes.add(getBytesAsInt(1));
                    // 4th
                    readBytes.add(getBytesAsInt(1));
                    // 5th
                    readBytes.add(getBytesAsInt(1));
                    midiMessage.setMidiEvent(MidiConstants.MidiEvent.SequenceNumber);
                    midiMessage.addArgument(readBytes.get(3));
                    midiMessage.addArgument(readBytes.get(4));
                    break;
                case 0x01:
                    midiMessage = readTextEvent();
                    midiMessage.setMidiEvent(MidiConstants.MidiEvent.Text);
                    break;
                case 0x02:
                    midiMessage = readTextEvent();
                    midiMessage.setMidiEvent(MidiConstants.MidiEvent.Copyright);
                    break;
                case 0x03:
                    midiMessage = readTextEvent();
                    midiMessage.setMidiEvent(MidiConstants.MidiEvent.TrackName);
                    break;
                case 0x04:
                    midiMessage = readTextEvent();
                    midiMessage.setMidiEvent(MidiConstants.MidiEvent.InstrumentName);
                    break;
                case 0x05:
                    midiMessage = readTextEvent();
                    midiMessage.setMidiEvent(MidiConstants.MidiEvent.Lyric);
                    break;
                case 0x06:
                    midiMessage = readTextEvent();
                    midiMessage.setMidiEvent(MidiConstants.MidiEvent.Marker);
                    break;
                case 0x07:
                    midiMessage = readTextEvent();
                    midiMessage.setMidiEvent(MidiConstants.MidiEvent.CuePoint);
                    break;

                case 0x20:
                    // 3rd
                    readBytes.add(getBytesAsInt(1));
                    // 4th
                    readBytes.add(getBytesAsInt(1));
                    midiMessage.setMidiEvent(MidiConstants.MidiEvent.MIDIChannelPrefix);
                    midiMessage.addArgument(readBytes.get(3));
                    break;

                case 0x2F:
                    readBytes.add(getBytesAsInt(1));
                    if (readBytes.get(1) == 0) {
                        midiMessage.setMidiEvent(MidiConstants.MidiEvent.EndOfTrack);
                    }
                    break;
                case 0x51:
                    // 3rd
                    readBytes.add(getBytesAsInt(1));
                    // 4th
                    readBytes.add(getBytesAsInt(1));
                    // 5th
                    readBytes.add(getBytesAsInt(1));
                    // 6th
                    readBytes.add(getBytesAsInt(1));

                    midiMessage.setMidiEvent(MidiConstants.MidiEvent.SetTempo);
                    int tempo = (readBytes.get(3) << 16) + (readBytes.get(4) << 8) + readBytes.get(5);
                    midiMessage.addArgument(tempo);
                    break;
                case 0x54:
                    advanceCursor(6);
                    midiMessage.setMidiEvent(MidiConstants.MidiEvent.NullEvent);
                    break;
                case 0x58:
                    // 3rd
                    readBytes.add(getBytesAsInt(1));
                    // 4th
                    readBytes.add(getBytesAsInt(1));
                    // 5th
                    readBytes.add(getBytesAsInt(1));
                    // 6th
                    readBytes.add(getBytesAsInt(1));
                    // 7th
                    readBytes.add(getBytesAsInt(1));

                    midiMessage.setMidiEvent(MidiConstants.MidiEvent.TimeSignature);

                    midiMessage.addArgument(readBytes.get(3));
                    midiMessage.addArgument(readBytes.get(4));
                    midiMessage.addArgument(readBytes.get(5));
                    midiMessage.addArgument(readBytes.get(6));
                    break;

                case 0x59:
                    // 3rd
                    readBytes.add(getBytesAsInt(1));
                    // 4th
                    readBytes.add(getBytesAsInt(1));
                    // 5th
                    readBytes.add(getBytesAsInt(1));

                    midiMessage.setMidiEvent(MidiConstants.MidiEvent.KeySignature);
                    midiMessage.addArgument(readBytes.get(3));
                    midiMessage.addArgument(readBytes.get(4));
                    break;

                case 0x7F:
                    int len = getVariableLengthQuantity();

                    List<Integer> bytes = getBytes(len);
                    midiMessage.setMidiEvent(MidiConstants.MidiEvent.SequencerSpecificMetaEvent);
                    for (int b : bytes) {
                        midiMessage.addArgument(b);
                    }

                    break;
            }

            messages.add(midiMessage);
        }

        if ((byteTypeMessage & 0xF0) == 0x80) {
            int midiChannel = (byteTypeMessage & 0x0F);
            midiMessage.setMidiEvent(MidiConstants.MidiEvent.NoteOff);
            // 2nd
            readBytes.add(getBytesAsInt(1));
            // 3rd
            readBytes.add(getBytesAsInt(1));
            midiMessage.addArgument(midiChannel);
            midiMessage.addArgument(readBytes.get(1));
            midiMessage.addArgument(readBytes.get(2));
        }

        if ((byteTypeMessage & 0xF0) == 0x90) {
            int midiChannel = (byteTypeMessage & 0x0F);
            midiMessage.setMidiEvent(MidiConstants.MidiEvent.NoteOn);
            // 2nd
            readBytes.add(getBytesAsInt(1));
            // 3rd
            readBytes.add(getBytesAsInt(1));
            midiMessage.addArgument(midiChannel);
            midiMessage.addArgument(readBytes.get(1));
            midiMessage.addArgument(readBytes.get(2));
        }

        if ((byteTypeMessage & 0xF0) == 0xA0) {
            int midiChannel = (byteTypeMessage & 0x0F);
            midiMessage.setMidiEvent(MidiConstants.MidiEvent.PolyphonicKeyPressure);
            // 2nd
            readBytes.add(getBytesAsInt(1));
            // 3rd
            readBytes.add(getBytesAsInt(1));
            midiMessage.addArgument(midiChannel);
            midiMessage.addArgument(readBytes.get(1));
            midiMessage.addArgument(readBytes.get(2));
        }

        if ((byteTypeMessage & 0xF0) == 0xB0) {
            int midiChannel = (byteTypeMessage & 0x0F);
            midiMessage.setMidiEvent(MidiConstants.MidiEvent.ControllerChange);
            // 2nd
            readBytes.add(getBytesAsInt(1));
            // 3rd
            readBytes.add(getBytesAsInt(1));
            midiMessage.addArgument(midiChannel);
            midiMessage.addArgument(readBytes.get(1));
            midiMessage.addArgument(readBytes.get(2));
        }

        if ((byteTypeMessage & 0xF0) == 0xC0) {
            int midiChannel = (byteTypeMessage & 0x0F);
            midiMessage.setMidiEvent(MidiConstants.MidiEvent.ProgramChange);
            // 2nd
            readBytes.add(getBytesAsInt(1));
            midiMessage.addArgument(midiChannel);
            midiMessage.addArgument(readBytes.get(1));
        }

        if ((byteTypeMessage & 0xF0) == 0xD0) {
            int midiChannel = (byteTypeMessage & 0x0F);
            midiMessage.setMidiEvent(MidiConstants.MidiEvent.ChannelKeyPressure);
            // 2nd
            readBytes.add(getBytesAsInt(1));
            midiMessage.addArgument(midiChannel);
            midiMessage.addArgument(readBytes.get(1));
        }

        if ((byteTypeMessage & 0xF0) == 0xE0) {
            int midiChannel = (byteTypeMessage & 0x0F);
            midiMessage.setMidiEvent(MidiConstants.MidiEvent.PitchBend);
            // 2nd
            readBytes.add(getBytesAsInt(1));
            // 3rd
            readBytes.add(getBytesAsInt(1));
            midiMessage.addArgument(midiChannel);
            midiMessage.addArgument(readBytes.get(1));
            midiMessage.addArgument(readBytes.get(2));
        }


        if (byteTypeMessage == 0x0a) {

        }

        if (byteTypeMessage == 0x5b) {

        }

        if (byteTypeMessage == 0x5d) {

        }



        System.out.println("byteTypeMessage = " + byteTypeMessage);

        System.out.println("typeTrack = " + Integer.toHexString(typeTrack));
        System.out.println("lengthTrack = " + lengthTrack);

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



//    private List<MidiMessage> readMidiTrack2() {
//        List<MidiMessage> messages = new ArrayList<>();
//
//        int typeTrack = getBytes(4);
//        System.out.println("typeTrack = " + Integer.toHexString(typeTrack));
//        int lengthTrack = getBytes(4);
//        System.out.println("lengthTrack = " + lengthTrack);
//
//        int deltaTime = getBytes(1);
//        int timeSignatureEvent = getBytes(3);
//        int timeSignatureNumerator = getBytes(1);
//        int timeSignatureDenominator = getBytes(1);
//        int timeSignatureMidiClocksPerMetronomeTick = getBytes(1);
//        int timeSignatureNumber32thPer24MidiClocks = getBytes(1);
//        System.out.println("deltaTime = " + deltaTime);
//
//        System.out.println("timeSignatureEvent = " + Integer.toHexString(timeSignatureEvent));
//        System.out.println("timeSignatureNumerator = " + timeSignatureNumerator);
//        System.out.println("timeSignatureDenominator = " + timeSignatureDenominator);
//        System.out.println("timeSignatureMidiClocksPerMetronomeTick = " + timeSignatureMidiClocksPerMetronomeTick);
//        System.out.println("timeSignatureNumber32thPer24MidiClocks = " + timeSignatureNumber32thPer24MidiClocks);
//
//        MidiMessage timeSignatureMidiMessage  = new MidiMessage();
//        timeSignatureMidiMessage.setDeltaTime(deltaTime);
//        timeSignatureMidiMessage.setMidiEvent(MidiConstants.MidiEvent.TimeSignature);
//        timeSignatureMidiMessage.addArgument(timeSignatureNumerator);
//        timeSignatureMidiMessage.addArgument(timeSignatureDenominator);
//        timeSignatureMidiMessage.addArgument(timeSignatureMidiClocksPerMetronomeTick);
//        timeSignatureMidiMessage.addArgument(timeSignatureNumber32thPer24MidiClocks);
//        messages.add(timeSignatureMidiMessage);
//
//        deltaTime = getBytes(1);
//        int keySignatureEvent = getBytes(3);
//        int numberSharpsOrFlats = getBytes(1);
//        int majorOrMinor = getBytes(1);
//        System.out.println("timeSignatureEvent = " + Integer.toHexString(keySignatureEvent));
//        System.out.println("numberSharpsOrFlats = " + numberSharpsOrFlats);
//        System.out.println("majorOrMinor = " + majorOrMinor);
//
//        MidiMessage keySignatureMidiMessage  = new MidiMessage();
//        keySignatureMidiMessage.setDeltaTime(deltaTime);
//        keySignatureMidiMessage.setMidiEvent(MidiConstants.MidiEvent.KeySignature);
//        keySignatureMidiMessage.addArgument(numberSharpsOrFlats);
//        keySignatureMidiMessage.addArgument(majorOrMinor);
//        messages.add(keySignatureMidiMessage);
//
////        FF 51 03 tt tt tt
//        deltaTime = getBytes(1);
//        int setTempoEvent = getBytes(3);
//        int microSeconds = getBytes(3);
//        System.out.println("setTempoEvent = " + Integer.toHexString(setTempoEvent));
//        System.out.println("microSeconds = " + microSeconds);
//
//        MidiMessage setTempoMidiMessage  = new MidiMessage();
//        setTempoMidiMessage.setDeltaTime(deltaTime);
//        setTempoMidiMessage.setMidiEvent(MidiConstants.MidiEvent.SetTempo);
//        setTempoMidiMessage.addArgument(microSeconds);
//        messages.add(setTempoMidiMessage);
//
//        return messages;
//    }
}
