package midi;

import java.util.List;

public interface MidiContentParser {
    List<MidiMessage> parse();
}
