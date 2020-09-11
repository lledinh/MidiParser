package midi;

import java.util.ArrayList;
import java.util.List;

public class MidiMessage {
    private int deltaTime;
    private MidiConstants.MidiEvent midiEvent;
    private List<Integer> arguments;

    public MidiMessage() {
        arguments = new ArrayList<>();
    }

    public int getDeltaTime() {
        return deltaTime;
    }

    public void setDeltaTime(int deltaTime) {
        this.deltaTime = deltaTime;
    }

    public MidiConstants.MidiEvent getMidiEvent() {
        return midiEvent;
    }

    public void setMidiEvent(MidiConstants.MidiEvent midiEvent) {
        this.midiEvent = midiEvent;
    }

    public List<Integer> getArguments() {
        return arguments;
    }

    public void addArgument(Integer argument) {
        this.arguments.add(argument);
    }
}
