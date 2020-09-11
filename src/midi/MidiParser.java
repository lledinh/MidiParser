package midi;

public abstract class MidiParser implements MidiContentParser {
    protected byte bytes[];
    protected int cursor = 0;

    protected int typeHeader;
    protected int lengthHeader;
    protected int midiFileFormat;
    protected int tracks;

    protected int divisionFormat;
    protected int ticksQuarterNote;

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getCursor() {
        return cursor;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    protected void parseDivision(int division) {
        divisionFormat = division & 0x8000;
        if (divisionFormat == 0) {
            ticksQuarterNote = division & 0x07FF;
        }
    }
}
