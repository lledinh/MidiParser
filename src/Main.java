import encoding.VariableLengthQuantity;
import file.BytesFileReader;
import midi.MidiFormat1Parser;
import midi.MidiMessage;

import java.io.IOException;
import java.util.List;

public class Main {

    private static void test() {
        int encode = VariableLengthQuantity.encode(106903);
        System.out.println(encode);
        int decode = VariableLengthQuantity.decode(0x00008347);
        System.out.println(decode);
        decode = VariableLengthQuantity.decode(33607);
        System.out.println(decode);
        decode = VariableLengthQuantity.decode(new int[] { 0x47, 0x83, 0x00, 0x00 });
        System.out.println(decode);
    }

    public static void main(String[] args) throws IOException {
        byte[] bytes = BytesFileReader.read("./doc/TestG14.mid");
        bytes = BytesFileReader.read("./doc/darude-sandstorm.mid");
        bytes = BytesFileReader.read("./doc/HALLIWELL.It's raining man K.mid");
        bytes = BytesFileReader.read("./doc/Super Mario 64 - Medley.mid");

        System.out.println("-------- parsing --------");
        MidiFormat1Parser midiFormat1Parser = new MidiFormat1Parser();
        midiFormat1Parser.setBytes(bytes);
        List<MidiMessage> midiMessages = midiFormat1Parser.parse();

        int i = 0;
        for (MidiMessage midiMessage: midiMessages) {
            System.out.println("------------");
            System.out.println("i = " + i);
            System.out.println(midiMessage.getMidiEvent().toString());
            System.out.println("deltaTime --- " + midiMessage.getDeltaTime());

            System.out.println("midiMessage.getArguments().size --- " + midiMessage.getArguments().size());
            for (int arg: midiMessage.getArguments()) {
                System.out.println("arg --- " + arg);
            }

            i++;
        }


        //////// TestG14.mid /////////
        // 4d54 6864
        // 0000 0006 0001 0002 01e0
        // 4d54 726b
        // 0000 0075                    Length
        // 00ff 0306 5069 616e 6f00     Track Name
        // 00ff 5804 0402 1808          Time Signature
        // 00ff 5902 0100               Key Signature
        // 00ff 5103 07a120             Set Tempo
        // 00 b0 7900
        // 00 c0 00
        // 00 b0 0764
        // 00 0a 40
        // 00 5b 00
        // 00 5d 00
        // 00 ff21 0100
        // 0090 4250
        // 8347 4200
        // 19   4350
        // 8347 4300
        // 19   4550
        // 8347 4500
        // 19   4750
        // 8347 4700
        // 19   4250
        // 8347 4200
        // 19   4350
        // 8347 4300
        // 19   4550
        // 8347 4500
        // 19   4750
        // 8347 4700
        // 01   ff2f00
        // --------- 2nd track -----------
        // 4d54 726b
        // 000000 52
        // 00 ff03 0650 6961 6e6f 00
        // 00 ff59 0201
        // 0000 ff21 0100
        // 0090 3650
        // 8347 3600
        // 19   3450
        // 8347 3400
        // 19   3250
        // 8347 3200
        // 19   3050
        // 8347 3000
        // 19   3050
        // 8347 3000
        // 19   3250
        // 8347 3200
        // 19   3450
        // 8347 3400
        // 19   3650
        // 8347 3600
        // 01   ff2f 00



        // 00 ff 51 03 07a1 20  FF 51 03 tttttt Set Tempo
        // 00 b0 79 00 Reset All Controllers
        // 00 c0 00 Program Change
        // 00 b0 07 64 Controller Change
        // 00 0a 40 Pan
        // 00 5b 00 Effects 1 Depth
        // 00 5d 00 Effects 3 Depth
        // 00 FF 21 01 00 MIDI port
        // 00 90 45 50 note on LA Velocity 50
        // 8347 45 00 note on LA Velocity 00 (OFF)
        // 01 ff 2f 00 End of Track

        // 69 71 72 74 0x45 0x47 0x48 0x4a

        // 19 47 50 83
        // 4747 0019 4850 8347 4800 194a 5083 474a
        //

        // 0090 45 50 LA ON 50V
        // 8347 45 00 LA ON 0V
        // 19   47 50
        // 8347 47 00
        // 19   48 50
        // 8347 48 00
        // 19   4a 50
        // 8347 4a 00
        // 01   ff2f00

        ////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////// Megalovania N°1 ////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        // 4d54726b                                      Track begin
        // 0000 002d                                     Length
        // 00ff03 0b 4d65 6761 6c6f 7661 6e69 61         Sequence/Track name Megalovania
        // 00ff02 08 546f 6279 2046 6f78                 Copyright Notice
        // 00ff5804 04 02 18 08                          Time signature
        // 00ff5902 ff 01                                Key signature
        // 00ff2f00                                      End of track
        // 4d 5472 6b00 0000 0b00 ff51 0303
        // fb06 00ff 2f00
        //////////////
        // 4d54726b                         Track begin
        // 0000 26b9                        Length
        // 00b9 0a40                        Controller Change
        // 00b9 0769                        Controller Change
        // 00e9 0040                        Pitch Bend
        // 00b9 6500                        Controller Change
        // 00b9 6400                        Controller Change
        // 00b9 060c                        Controller Change
        // 00c9 00                          Program Change
        // 00b9 5b13                        Controller Change
        // 00b9 4000                        Controller Change

        // ad00 99 37 1f                    Note On Channel 9
        // 3089 3740 3099 3730 3089
        //3740 3099 3741 3089 3740 3099 3752 3089
        //3740 3099 3954 0099 2e31 0099 2452 3089
        //2440 0089 2e40 0089 3940 3099 2852 0099
        //2e31 3089 2e40 0089 2840 3099 2e31 0099
        //2452 3089 2440 0089 2e40 3099 2851 0099
        //2e31 3089 2840 0089 2e40 0099 2451 3089
        //2440 0099 2e31 3089 2e40 0099 2451 3089
        //2440 0099 2851 0099 2e31 3089 2e40 0089
        //2840 0099 2450 3089 2440 0099 2e31 0099


        ////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////// Megalovania N°2 ////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////

        //////////////////////////////////////////////////////////////////
        ////////////////////////////// HEADER ////////////////////////////
        //////////////////////////////////////////////////////////////////
        // 4d54 6864                                Header begin
        // 0000 0006                                Length
        // 0001                                     Format
        // 0003                                     Track
        // 01e0                                     Division
        //////////////////////////////////////////////////////////////////
        ////////////////////////// TRACK BATTERIE ////////////////////////
        //////////////////////////////////////////////////////////////////
        // 4d54 726b                                Track begin
        // 0000 1fd2                                Length
        // 00ff 5804 0402 1808                      Time signature
        // 00ff 5902 0000                           Key signature
        // 00ff 5103 03fb06                         Set Tempo
        // 00b9 7900                                Controller Change
        // 00c9 00                                  Program Change
        // 00b9 0769                                Controller Change
        // 000a 40
        // 005b 00                                  Effects 1 Depth
        // 005d 00                                  Effects 3 Depth
        // 00ff 21 0100                             MIDI port
        // 81e100 99 37 1f                          Note ON Channel 9 G3
        // 835f      37 00                          Note OFF Channel 9 G3
        // 01        37 30                          ON  G3
        // 835f      37 00                          OFF G3
        // 01        37 41                          ON  G3
        // 835f      37 00                          OFF G3
        // 01        37 52                          ON  G3
        // 835f      37 00                          OFF G3
        // 01        2e 31
        // 00        39 54
        // 00        24 52
        // 835f      2e00
        //0039 0000 2400 0128 5200 2e31 835f 2800
        //002e 0001 2e31 0024 5283 5f2e 0000 2400
        //0128 5100 2e31 8170 2451 816f 2800 002e
        //0000 2400 012e 3181 7024 5181 6f2e 0000
        //2400 0128 5100 2e31 8170 2450 816f 2800
        //002e 0000 2400 012e 3100 2450 835f 2e00
        //0024 0001 2850 002e 3100 2450 835f 2800
        //002e 0000 2400 012e 3100 2452 835f 2e00
        //0024 0001 2852 002e 3183 5f28 0000 2e00
        //012e 3100 2452 835f 2e00 0024 0001 2851
        //002e 3181 7024 5181 6f28 0000 2e00 0024
        //0001 2e31 8170 2451 816f 2e00 0024 0001
        //2851 002e 3181 7024 5081 6f28 0000 2e00
        //0024 0001 2e31 0024 5083 5f2e 0000 2400
        //0128 5000 2e31 0024 5083 5f28 0000 2e00
        //0024 0001 2e31 0024 5283 5f2e 0000 2400
        //0128 5200 2e31 835f 2800 002e 0001 2e31


        //////////////////////////////////////////////////////////////////
        /////////////////// TRACK PIANO CLEF SOL /////////////////////////
        //////////////////////////////////////////////////////////////////

        // 4d54 726b                Track begin
        // 0000 152e                Length
        // 00ff 5902 fc 00          Key signature
        // 00b0 7900                Reset all Controller Change
        // 00c0 00                  Program Change
        // 00b0 0764                Controller Change
        // 000a 40
        // 005b 00                  Effects 1 Depth
        // 005d 00                  Effects 3 Depth
        // 00ff 2101 00             MIDI port
        // 00 90 3e 51              Note ON Channel 0 D4 Vel 51
        // 8163 3e 00               Note OFF Channel 0 D4 Vel 00
        // 0d   3e 52               ON D4
        // 8163 3e 00               OFF D4
        // 0d   4a 52               ON D5
        // 8347 4a 00               OFF D5
        // 19 4552 852b 4500 2544 5283
        //4744 0019 4352 8347 4300 1941 5283 4741
        //0019 3e52 8163 3e00 0d41 5281 6341 000d
        //4352 8163 4300 0d3c 5281 633c 000d 3c52
        //8163 3c00 0d4a 5283 474a 0019 4552 852b
        //4500 2544 5283 4744 0019 4352 8347 4300
        //1941 5283 4741 0019 3e52 8163 3e00 0d41
        //5281 6341 000d 4352 8163 4300 0d3b 5281
        //633b 000d 3b52 8163 3b00 0d4a 5283 474a
        //0019 4552 852b 4500 2544 5283 4744 0019
        //4352 8347 4300 1941 5283 4741 0019 3e52
        //8163 3e00 0d41 5281 6341 000d 4352 8163
        //4300 0d3a 5281 633a 000d 3a52 8163 3a00
        //0d4a 5283 474a 0019 4552 852b 4500 2544
        //5283 4744 0019 4352 8347 4300 1941 5283
        //4741 0019 3e52 8163 3e00 0d41 5281 6341
        //000d 4352 8163 4300 0d3e 5281 633e 000d
        //3e52 8163 3e00 0d4a 5283 474a 0019 4552
        //852b 4500 2544 5283 4744 0019 4352 8347
        //4300 1941 5283 4741 0019 3e52 8163 3e00
        //0d41 5281 6341 000d 4352 8163 4300 0d3c
        //5281 633c 000d 3c52 8163 3c00 0d4a 5283


    }
}
