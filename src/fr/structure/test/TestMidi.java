package fr.structure.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.structure.bo.Note;

public class TestMidi
{

    public static final String[] NOTE_NAMES_EN = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    public static final String[] NOTE_NAMES_FR = {"do", "do#", "ré", "ré#", "mi", "fa", "fa#", "sol", "sol#", "la", "la#", "si"};

    // META MESSAGE
    public static final int SEQUENCE_NUMBER = 0x00;
    public static final int TEXT = 0x01;
    public static final int COPYRIGHT_NOTICE = 0x02;
    public static final int TRACK_NAME = 0x03;
    public static final int INSTRUMENT_NAME = 0x04;
    public static final int LYRICS = 0x05;
    public static final int MARKER = 0x06;
    public static final int CUE_POINT = 0x07;
    public static final int CHANNEL_PREFIX = 0x20;
    public static final int END_OF_TRACK = 0x2F;
    public static final int SET_TEMPO = 0x51;
    public static final int SMPTE_OFFSET = 0x54;
    public static final int TIME_SIGNATURE = 0x58;
    public static final int KEY_SIGNATURE = 0x59;
    public static final int SEQUENCER_SPECIFIC = 0x7F;
    public static final int RESET = 0xFF;

    public static File midiIn01 = new File( "D:\\eclipse-working\\in\\Koto - Visitors.mid" );
    public static File xmlIn01 = new File( "D:\\eclipse-working\\in\\Koto - Visitors.xml" );
    
    public static File txtOut01 = new File( "D:\\eclipse-working\\out\\Koto - Visitors.txt" );
    public static File xmlOut01 = new File( "D:\\eclipse-working\\out\\Koto - Visitors.xml" );
    
    public static File midiIn02 = new File( "D:\\eclipse-working\\in\\ghost rider in the sky.midi" );
    
    
    
    
	public static void play() throws IOException, InvalidMidiDataException, MidiUnavailableException
	{

		Sequence sequence = MidiSystem.getSequence( midiIn02 );

    	// Create a sequencer for the sequence
    	Sequencer sequencer = MidiSystem.getSequencer();
    	sequencer.open();
    	sequencer.setSequence(sequence);

    	// Start playing
    	sequencer.start();

	}

	public static void describe() throws IOException, InvalidMidiDataException, MidiUnavailableException
	{

		
		FileWriter writer = new FileWriter( txtOut01 );

		// Create a synthesizer for the sequence		
		Synthesizer synthesizer = MidiSystem.getSynthesizer();
		synthesizer.open();

		MidiChannel[] midiChannels = synthesizer.getChannels();

		int j = 0;

		for( MidiChannel midiChannel : midiChannels )
		{

			System.out.println( "midiChannel " + j + " : " + midiChannel.getProgram() );
			writer.write( "midiChannel " + j + " : " + midiChannel.getProgram() + "\n" );

			j++;

		}

		Sequence sequence = MidiSystem.getSequence( midiIn01 );

		System.out.println( "Division type : " + sequence.getDivisionType() );
		System.out.println( "Timing resolution : " + sequence.getResolution() );

		int trackNumber = 0;

		for (Track track :  sequence.getTracks())
	    {

			System.out.println("Track " + trackNumber + ": size = " + track.size());
	        System.out.println();

	        writer.write( "Track " + trackNumber + ": size = " + track.size() + "\n" );
	        writer.write( "\n" );

	        for (int i=0; i < track.size(); i++) 
	        {
	        	
	        	MidiEvent event = track.get(i);
	            
	        	long tick = event.getTick();
            
	            MidiMessage message = event.getMessage();

	            if (message instanceof ShortMessage) 
	            {

	            	ShortMessage sm = (ShortMessage) message;
	            	
	            	if (sm.getCommand() == ShortMessage.NOTE_ON )
	            	{

	            		int channel = sm.getChannel();
	            		int key = sm.getData1();
	                    int octave = (key / 12)-1;
	                    int note = key % 12;
	                    String noteName = NOTE_NAMES_EN[note];
	                    int velocity = sm.getData2();

	                    System.out.println("NOTE ON [channel = " + channel + ", note = " + noteName + octave + ", key = " + key + ", velocity = " + velocity + ", tick = " + tick + "]");
	                    writer.write("NOTE ON [channel = " + channel + ", note = " + noteName + octave + ", key = " + key + ", velocity = " + velocity + ", tick = " + tick + "]" + "\n");

	            	} 
	                else if (sm.getCommand() == ShortMessage.NOTE_OFF )
	                {

	                	int channel = sm.getChannel();
	                	int key = sm.getData1();
	                    int octave = (key / 12)-1;
	                    int note = key % 12;
	                    String noteName = NOTE_NAMES_EN[note];
	                    int velocity = sm.getData2();

	                    System.out.println("NOTE OFF [channel = " + channel + ", note = " + noteName + octave + ", key = " + key + ", velocity = " + velocity + ", tick = " + tick + "]");
	                    writer.write("NOTE OFF [channel = " + channel + ", note = " + noteName + octave + ", key = " + key + ", velocity = " + velocity + ", tick = " + tick + "]" + "\n");
	                    
                    }
	                else if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE)
	                {
	                	
	                	int channel = sm.getChannel();
	                	int instrument = sm.getData1();
	                	int data2 = sm.getData2();

	                    System.out.println("PROGRAM_CHANGE [channel = " + channel + ", instrument = " + instrument + ", data2 = " + data2 + ", tick = " + tick + "]");
	                    writer.write("PROGRAM_CHANGE [channel = " + channel + ", instrument = " + instrument + ", data2 = " + data2 + ", tick = " + tick + "]" + "\n" );
	                    
                    }
	                else
	                {
	                	
	                	int channel = sm.getChannel();
	                	int data1 = sm.getData1();
	                	int data2 = sm.getData2();

	                	System.out.println("Other ShortMessage [message = "  + sm.getMessage() + "]" );
	                    writer.write("Other ShortMessage [message = "  + sm.getMessage() + "]" + "\n");

	                }
	            	

	            }
	            else if(message instanceof MetaMessage)
                {

                	MetaMessage mm = (MetaMessage) message;

                	System.out.println("MetaMessage [message = "  + mm.getMessage() + "]" );
                    writer.write("MetaMessage [message = "  + mm.getMessage() + "]" + "\n");

                }
                else
                {

                    System.out.println("Other message: " + message.getClass());
                    writer.write( "Other message: " + message.getClass() + "\n" );
                    
                }

	        }

			trackNumber++;
	        
	    }
		
	}
	
	public static void listInstruments() throws IOException, InvalidMidiDataException, MidiUnavailableException
	{

		Synthesizer synthesizer = MidiSystem.getSynthesizer();
		
		synthesizer.open();

		// Orchestra

		Instrument[] orchestra = synthesizer.getAvailableInstruments();

		System.out.println("-------------------------");
		System.out.println("- Liste des instruments -");
		
	    for (Instrument instrument : orchestra)
	    {
	    	
	    	System.out.println("Instrument : " + instrument.toString() );
	    }
	    
	    // LoadedInstruments
	    
	    Instrument[] loadedInstruments = synthesizer.getLoadedInstruments();

		System.out.println("-------------------------");
		System.out.println("- Loaded instruments    -");
				
	    for (Instrument instrument : loadedInstruments)
	    {
	    	
	    	System.out.println("Instrument : " + instrument.toString() );
	    }
	    
	    // DefaultInstruments
	    
	    Instrument[] defaultInstruments = synthesizer.getDefaultSoundbank().getInstruments();

		System.out.println("-------------------------");
		System.out.println("- Default instruments   -");
				
	    for (Instrument instrument : defaultInstruments)
	    {
	    	
	    	System.out.println("Instrument : " + instrument.toString() + " " + instrument.getSoundbank() );
	    }

	    synthesizer.close();		

	}

	public static void compose01() throws IOException, InvalidMidiDataException, MidiUnavailableException
	{
	
		int resolution = 96;
		
		Sequence sequence = new Sequence(Sequence.PPQ, resolution);
    	
		Track track = sequence.createTrack();
				
		ArrayList<Note> partition = new ArrayList<Note>();
		
		partition.add( new Note( 60, 127, 1) );
		partition.add( new Note( 60, 127, 1) );
		partition.add( new Note( 60, 127, 1) );
		partition.add( new Note( 62, 127, 1) );
		
		partition.add( new Note( 64, 127, 2) );
		partition.add( new Note( 62, 127, 2) );
		
		partition.add( new Note( 60, 127, 1) );
		partition.add( new Note( 64, 127, 1) );
		partition.add( new Note( 62, 127, 1) );
		partition.add( new Note( 62, 127, 1) );
		
		partition.add( new Note( 60, 127, 4) );
		
		partition.add( new Note( 60, 127, 1) );
		partition.add( new Note( 60, 127, 1) );
		partition.add( new Note( 60, 127, 1) );
		partition.add( new Note( 62, 127, 1) );
		
		partition.add( new Note( 64, 127, 2) );
		partition.add( new Note( 62, 127, 2) );
		
		partition.add( new Note( 60, 127, 1) );
		partition.add( new Note( 64, 127, 1) );
		partition.add( new Note( 62, 127, 1) );
		partition.add( new Note( 62, 127, 1) );
		
		partition.add( new Note( 60, 127, 4) );

		partition.add( new Note( 62, 127, 1) );
		partition.add( new Note( 62, 127, 1) );
		partition.add( new Note( 62, 127, 1) );
		partition.add( new Note( 62, 127, 1) );
		
		partition.add( new Note( 57, 127, 2) );
		partition.add( new Note( 57, 127, 2) );
		
		partition.add( new Note( 62, 127, 1) );
		partition.add( new Note( 60, 127, 1) );
		partition.add( new Note( 59, 127, 1) );
		partition.add( new Note( 57, 127, 1) );
		
		partition.add( new Note( 55, 127, 4) );
		
		partition.add( new Note( 60, 127, 1) );
		partition.add( new Note( 60, 127, 1) );
		partition.add( new Note( 60, 127, 1) );
		partition.add( new Note( 62, 127, 1) );
		
		partition.add( new Note( 64, 127, 2) );
		partition.add( new Note( 62, 127, 2) );
		
		partition.add( new Note( 60, 127, 1) );
		partition.add( new Note( 64, 127, 1) );
		partition.add( new Note( 62, 127, 1) );
		partition.add( new Note( 62, 127, 1) );
		
		partition.add( new Note( 60, 127, 4) );

		long tick = 0;
		
		for( Note note : partition )
		{

			track.add( new MidiEvent( new ShortMessage( ShortMessage.NOTE_ON, note.getNote() , note.getVelocity() ), tick ) );
			
			tick = (long) (tick + resolution * note.getDuration() );
			
			track.add( new MidiEvent( new ShortMessage( ShortMessage.NOTE_OFF, note.getNote() , 0 ), tick ) );
		}
		
    	// Create a sequencer for the sequence
    	Sequencer sequencer = MidiSystem.getSequencer();
    	sequencer.open();

    	sequencer.setSequence(sequence);

    	// Start playing
    	sequencer.start();
		
	}

	public static void compose02() throws IOException, InvalidMidiDataException, MidiUnavailableException
	{

		int resolution = 96; 
		
		// Create a sequencer for the sequence
    	Sequencer sequencer = MidiSystem.getSequencer();
    	sequencer.open();
		
    	// Create a sequence
		Sequence sequence = new Sequence(Sequence.PPQ, resolution);
    	
		// Track 00 - Channel 1 (0)		
		Track track00 = sequence.createTrack();
		
		ArrayList<Note> partition00 = new ArrayList<Note>();
		
		partition00.add( new Note( 60, 127, 1) );
		partition00.add( new Note( 60, 127, 1) );
		partition00.add( new Note( 60, 127, 1) );
		partition00.add( new Note( 62, 127, 1) );
		
		partition00.add( new Note( 64, 127, 2) );
		partition00.add( new Note( 62, 127, 2) );
		
		partition00.add( new Note( 60, 127, 1) );
		partition00.add( new Note( 64, 127, 1) );
		partition00.add( new Note( 62, 127, 1) );
		partition00.add( new Note( 62, 127, 1) );
		
		partition00.add( new Note( 60, 127, 4) );

		track00.add( new MidiEvent( new ShortMessage( ShortMessage.PROGRAM_CHANGE, 0, 10 , 0 ), 0 ) );
		
		long tick00 = 0;
				
		for( Note note : partition00 )
		{

			track00.add( new MidiEvent( new ShortMessage( ShortMessage.NOTE_ON, 0, note.getNote() , note.getVelocity() ), tick00 ) );

			tick00 = (long) (tick00 + resolution * note.getDuration() );

			track00.add( new MidiEvent( new ShortMessage( ShortMessage.NOTE_OFF, 0, note.getNote() , 0 ), tick00 ) );

		}

		// Track 09 - Channel 10 (9) 
		Track track09 = sequence.createTrack();

		ArrayList<Note> partition09 = new ArrayList<Note>();

		partition09.add( new Note( 36, 127, 2) );
		partition09.add( new Note( 44, 127, 2) );

		partition09.add( new Note( 36, 127, 2) );
		partition09.add( new Note( 44, 127, 2) );
		
		partition09.add( new Note( 36, 127, 2) );
		partition09.add( new Note( 44, 127, 2) );

		partition09.add( new Note( 36, 127, 2) );
		partition09.add( new Note( 44, 127, 2) );
		
		track00.add( new MidiEvent( new ShortMessage( ShortMessage.PROGRAM_CHANGE, 9, 10 , 0 ), 0 ) );
		
		long tick09 = 0;
				
		for( Note note : partition09 )
		{

			track09.add( new MidiEvent( new ShortMessage( ShortMessage.NOTE_ON, 9, note.getNote() , note.getVelocity() ), tick09 ) );

			tick09 = (long) (tick09 + resolution * note.getDuration() );

			track09.add( new MidiEvent( new ShortMessage( ShortMessage.NOTE_OFF, 9, note.getNote() , 0 ), tick09 ) );

		}
		
    	// Start playing 01
    	
    	sequencer.setSequence(sequence);
    	sequencer.start();

    	if( !sequencer.isRunning() )
    	{
    		sequencer.close();	
    	}

	}
	
	public static void compose03() throws IOException, InvalidMidiDataException, MidiUnavailableException
	{
		
		int resolution = 96;

		// Create a sequencer for the sequence
    	Sequencer sequencer = MidiSystem.getSequencer();
    	sequencer.open();
		
    	// Create a sequence
		Sequence sequence = new Sequence(Sequence.PPQ, 96);
    	
		// Track 00 - Channel 1 (0)		
		Track track00 = sequence.createTrack();

		track00.add( new MidiEvent( new ShortMessage( ShortMessage.PROGRAM_CHANGE, 0, 10 , 0 ), 0 ) );
		
		ArrayList<Note> partition00 = new ArrayList<Note>();
		
		partition00.add( new Note( 60, 127, 1) );
		partition00.add( new Note( 60, 127, 1) );
		partition00.add( new Note( 60, 127, 1) );
		partition00.add( new Note( 62, 127, 1) );
		
		partition00.add( new Note( 64, 127, 2) );
		partition00.add( new Note( 62, 127, 2) );
		
		partition00.add( new Note( 60, 127, 1) );
		partition00.add( new Note( 64, 127, 1) );
		partition00.add( new Note( 62, 127, 1) );
		partition00.add( new Note( 62, 127, 1) );
		
		partition00.add( new Note( 60, 127, 4) );
		
		long tick00 = 0;
		
		for( Note note : partition00 )
		{

			track00.add( new MidiEvent( new ShortMessage( ShortMessage.NOTE_ON, 0, note.getNote() , note.getVelocity() ), tick00 ) );

			tick00 = (long) (tick00 + resolution * note.getDuration() );

			track00.add( new MidiEvent( new ShortMessage( ShortMessage.NOTE_OFF, 0, note.getNote() , 0 ), tick00 ) );

		}

		// Track 09 - Channel 10 (9) 
		Track track09 = sequence.createTrack();

		ArrayList<Note> partition09 = new ArrayList<Note>();

		partition09.add( new Note( 36, 127, 2) );
		partition09.add( new Note( 44, 127, 2) );

		partition09.add( new Note( 36, 127, 2) );
		partition09.add( new Note( 44, 127, 2) );
		
		partition09.add( new Note( 36, 127, 2) );
		partition09.add( new Note( 44, 127, 2) );

		partition09.add( new Note( 36, 127, 2) );
		partition09.add( new Note( 44, 127, 2) );
		
		long tick09 = 0;
				
		for( Note note : partition09 )
		{

			track09.add( new MidiEvent( new ShortMessage( ShortMessage.NOTE_ON, 9, note.getNote() , note.getVelocity() ), tick09 ) );

			tick09 = (long) (tick09 + resolution * note.getDuration() );

			track09.add( new MidiEvent( new ShortMessage( ShortMessage.NOTE_OFF, 9, note.getNote() , 0 ), tick09 ) );

		}
		
		sequencer.setSequence(sequence);

    	// Start playing
    	sequencer.start();
		
	}

	public static void importXML() throws IOException, InvalidMidiDataException, MidiUnavailableException, ParserConfigurationException, SAXException, TransformerException
	{

		// Instantiate the Factory
	    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	    
	    documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        // parse XML file
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document document = documentBuilder.parse( xmlIn01 );
        
        Element elementSequence = document.getDocumentElement();
        
        if( elementSequence.getNodeName().equals("sequence") )
        {
        	
        	NodeList nodeListDivision = elementSequence.getElementsByTagName("division");
        	
        	if( nodeListDivision.getLength() == 1 )
        	{
        		
        		Node node = nodeListDivision.item(0);
        		
        		
        		System.out.println(node.getNodeName());
        		System.out.println(node.getTextContent());
        		
        	}
        		
        	NodeList nodeListResolution = elementSequence.getElementsByTagName("resolution");
        	
        	if( nodeListResolution.getLength() == 1 )
        	{
        		
        		Node node = nodeListResolution.item(0);
        		
        		
        		System.out.println(node.getNodeName());
        		System.out.println(node.getTextContent());
        		
        	}
        	
        	NodeList nodeListTrack = elementSequence.getElementsByTagName("tracks");

        	System.out.println( nodeListTrack.getLength()   );
        	
        }
        
        
	}
	
	public static void exportXML() throws IOException, InvalidMidiDataException, MidiUnavailableException, ParserConfigurationException, TransformerException
	{

		// En entrée
		Sequence sequence = MidiSystem.getSequence( midiIn01 );
		
		
		// En sortie
		
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();
		
		// Element racine

		Element elementSequence = document.createElement("sequence");

		
		// Division Type
		if(sequence.getDivisionType() == 0.0f)
		{
			
			elementSequence.setAttribute("division", "PPQ");

		}
		else if(sequence.getDivisionType() == 24.0f)
		{
			
			elementSequence.setAttribute("division", "SMPTE_24");			

		}
		else if(sequence.getDivisionType() == 25.0f)
		{
			
			elementSequence.setAttribute("division", "SMPTE_25");

		}
		else if(sequence.getDivisionType() == 29.97f)
		{
			
			elementSequence.setAttribute("division", "SMPTE_30DROP");

		}
		else if(sequence.getDivisionType() == 30.0f)
		{
			
			elementSequence.setAttribute("division", "SMPTE_30");

		}

		
		// Resolution
		elementSequence.setAttribute("resolution","" + sequence.getResolution() ); 

		// Collection des tracks
		
		Track[] tracks = sequence.getTracks();
		
		for (Track track : tracks )
	    {

			Element elementTrack  = document.createElement("track");
			
	        for (int i=0; i < track.size(); i++) 
	        {

	        	MidiEvent event = track.get(i);

	        	Element elementEvent  = document.createElement("event");	
			
	        	elementEvent.setAttribute("tick", "" + event.getTick() );
			
	            MidiMessage message = event.getMessage();

	            if (message instanceof MetaMessage)
	            {

	            	MetaMessage mm = (MetaMessage) message;
	            	
		        	Element elementMetaMessage  = document.createElement("meta");
	            	
		        	// int to hexa
		        	String status = String.valueOf( Integer.toHexString( mm.getStatus() ) ).toUpperCase();

		        	elementMetaMessage.setAttribute("meta", status );
		        	
		        	if( mm.getType() == SEQUENCE_NUMBER )
		        	{
		        		
		        		elementMetaMessage.setAttribute("type", "SEQUENCE_NUMBER" );

		        		elementMetaMessage.setAttribute("data", "" );

		        	}
		        	else if( mm.getType() == TEXT  )
		        	{
		        		
		        		elementMetaMessage.setAttribute("type", "TEXT" );

		        		elementMetaMessage.setAttribute("data", new String( mm.getData() ) );

		        	}
		        	else if( mm.getType() == COPYRIGHT_NOTICE )
		        	{

		        		elementMetaMessage.setAttribute("type", "COPYRIGHT_NOTICE" );

		        		elementMetaMessage.setAttribute("data", new String( mm.getData() ) );

		        	}
		        	else if( mm.getType() == TRACK_NAME )
		        	{
		        		
		        		elementMetaMessage.setAttribute("type", "TRACK_NAME" );
	
		        		elementMetaMessage.setAttribute("data", new String( mm.getData() ) );
		        		
		        	}
		        	else if( mm.getType() == INSTRUMENT_NAME )
		        	{

		        		elementMetaMessage.setAttribute("type", "INSTRUMENT_NAME" );

		        		elementMetaMessage.setAttribute("data", new String( mm.getData() ) );

		        	}
		        	else if( mm.getType() == LYRICS )
		        	{

		        		elementMetaMessage.setAttribute("type", "LYRICS" );
		        		
		        		elementMetaMessage.setAttribute("data", new String( mm.getData() ) );

		        	}
		        	else if( mm.getType() == MARKER )
		        	{
		        		
		        		elementMetaMessage.setAttribute("type", "MARKER" );
		        		
		        		elementMetaMessage.setAttribute("data", new String( mm.getData() ) );

		        	}
		        	else if( mm.getType() == CUE_POINT )
		        	{
		        		
		        		elementMetaMessage.setAttribute("type", "CUE_POINT" );
		        		
		        		elementMetaMessage.setAttribute("data", new String( mm.getData() ) );

		        	}
		        	
		        	
		        	
		        	
		        	
		        	
		        	
		        	else if( mm.getType() == CHANNEL_PREFIX )
		        	{

		        		elementMetaMessage.setAttribute("type", "CHANNEL_PREFIX" );

		        		elementMetaMessage.setAttribute("data", "" );

		        	}
		        	else if( mm.getType() == END_OF_TRACK )
		        	{

		        		elementMetaMessage.setAttribute("type", "END_OF_TRACK" );

		        	}
		        	else if( mm.getType() == SET_TEMPO )
		        	{

		        		elementMetaMessage.setAttribute("type", "SET_TEMPO" );

		        		elementMetaMessage.setAttribute("data", new String( mm.getData() ) );

		        	}
		        	else if( mm.getType() == SMPTE_OFFSET )
		        	{

		        		elementMetaMessage.setAttribute("type", "SMPTE_OFFSET" );

		        		elementMetaMessage.setAttribute("data", "" );

		        	}
		        	else if( mm.getType() == TIME_SIGNATURE )
		        	{

		        		elementMetaMessage.setAttribute("type", "TIME_SIGNATURE" );
		        		
		        		elementMetaMessage.setAttribute("data", new String( mm.getData() ) );

		        	}
		        	else if( mm.getType() == KEY_SIGNATURE )
		        	{
		        		
		        		elementMetaMessage.setAttribute("type", "KEY_SIGNATURE" );
		        		
		        		elementMetaMessage.setAttribute("data", "" );

		        	}
		        	else if( mm.getType() == SEQUENCER_SPECIFIC )
		        	{

		        		elementMetaMessage.setAttribute("type", "SEQUENCER_SPECIFIC" );

		        		elementMetaMessage.setAttribute("data", "" );

		        	}
		        	else if( mm.getType() == RESET )
		        	{

		        		elementMetaMessage.setAttribute("type", "RESET" );

		        		elementMetaMessage.setAttribute("data", "" );

		        	}

		        	/*

Field				Length		Starts at byte	Value
Status byte			One byte	0x00			0xFF always
Meta type			One byte	0x01			Variable, see below
Length				Variable	0x02			0-255
Data				Variable	0x03			variable

Message				Meta type	Data length		Contains															Occurs at
Sequence number		0x00		2 bytes			The number of a sequence											At delta time 0
Text				0x01		variable		Some text															Anywhere
Copyright notice	0x02		variable		A copyright notice													At delta time 0 in the first track
Track name			0x03		variable		A track name														At delta time 0
Instrument name		0x04		variable		The name of an instrument in the current track						Anywhere
Lyrics				0x05		variable		Lyrics, usually a syllable per quarter note							Anywhere
Marker				0x06		variable		The text of a marker												Anywhere
Cue point			0x07		variable		The text of a cue, usually to prompt for some action from the user	Anywhere
Channel prefix		0x20		1 byte			A channel number (following meta events will apply to this channel)	Anywhere
End of track		0x2F		0 byte			At the end of each track
Set tempo			0x51		3 bytes			The number of microseconds per beat	Anywhere, but usually in the first track
SMPTE offset		0x54		5 bytes			SMPTE time to denote playback offset from the beginning				Anywhere
Time signature		0x58		4 bytes			Time signature, metronome clicks, and size of a beat in 32nd notes	Anywhere
Key signature		0x59		2 bytes			A key signature														Anywhere
Sequencer specific	0x7F		variable		Something specific to the MIDI device manufacturer					Anywhere

		        	*/

		        	elementEvent.appendChild(elementMetaMessage);

	            }
	            else if (message instanceof ShortMessage) 
	            {

	            	ShortMessage sm = (ShortMessage) message;
	            	
		        	Element elementShortMessage  = document.createElement("short");
	            	
	            	if (sm.getCommand() == ShortMessage.MIDI_TIME_CODE)
	            	{

	            		elementShortMessage.setAttribute("command", "MIDI_TIME_CODE");
	            		elementShortMessage.setAttribute("channel", "" + sm.getChannel() );
	            		elementShortMessage.setAttribute("data1", "" + sm.getData1() );
	            		elementShortMessage.setAttribute("data2", "" + sm.getData2() );
	            		
	            		elementEvent.appendChild(elementShortMessage);

	            	} 
	            	else if (sm.getCommand() == ShortMessage.SONG_POSITION_POINTER)
	            	{

	            		elementShortMessage.setAttribute("command", "SONG_POSITION_POINTER");
	            		elementShortMessage.setAttribute("channel", "" + sm.getChannel() );
	            		elementShortMessage.setAttribute("data1", "" + sm.getData1() );
	            		elementShortMessage.setAttribute("data2", "" + sm.getData2() );

	            		elementEvent.appendChild(elementShortMessage);
	            		
	            	} 
	            	else if (sm.getCommand() == ShortMessage.SONG_SELECT)
	            	{

	            		elementShortMessage.setAttribute("command", "SONG_SELECT");
	            		elementShortMessage.setAttribute("channel", "" + sm.getChannel() );
	            		elementShortMessage.setAttribute("data1", "" + sm.getData1() );
	            		elementShortMessage.setAttribute("data2", "" + sm.getData2() );

	            		elementEvent.appendChild(elementShortMessage);
	            		
	            	} 
	            	else if (sm.getCommand() == ShortMessage.TUNE_REQUEST)
	            	{

	            		elementShortMessage.setAttribute("command", "TUNE_REQUEST");
	            		elementShortMessage.setAttribute("channel", "" + sm.getChannel() );
	            		elementShortMessage.setAttribute("data1", "" + sm.getData1() );
	            		elementShortMessage.setAttribute("data2", "" + sm.getData2() );

	            		elementEvent.appendChild(elementShortMessage);
	            		
	            	} 
	            	else if (sm.getCommand() == ShortMessage.END_OF_EXCLUSIVE)
	            	{

	            		elementShortMessage.setAttribute("command", "END_OF_EXCLUSIVE");
	            		elementShortMessage.setAttribute("channel", "" + sm.getChannel() );
	            		elementShortMessage.setAttribute("data1", "" + sm.getData1() );
	            		elementShortMessage.setAttribute("data2", "" + sm.getData2() );

	            		elementEvent.appendChild(elementShortMessage);
	            		
	            	} 
	            	else if (sm.getCommand() == ShortMessage.TIMING_CLOCK)
	            	{

	            		elementShortMessage.setAttribute("command", "TIMING_CLOCK");
	            		elementShortMessage.setAttribute("channel", "" + sm.getChannel() );
	            		elementShortMessage.setAttribute("data1", "" + sm.getData1() );
	            		elementShortMessage.setAttribute("data2", "" + sm.getData2() );

	            		elementEvent.appendChild(elementShortMessage);
	            		
	            	} 
	            	else if (sm.getCommand() == ShortMessage.START)
	            	{

	            		elementShortMessage.setAttribute("command", "START");
	            		elementShortMessage.setAttribute("channel", "" + sm.getChannel() );
	            		elementShortMessage.setAttribute("data1", "" + sm.getData1() );
	            		elementShortMessage.setAttribute("data2", "" + sm.getData2() );

	            		elementEvent.appendChild(elementShortMessage);
	            		
	            	} 
	            	else if (sm.getCommand() == ShortMessage.CONTINUE)
	            	{

	            		elementShortMessage.setAttribute("command", "CONTINUE");
	            		elementShortMessage.setAttribute("channel", "" + sm.getChannel() );
	            		elementShortMessage.setAttribute("data1", "" + sm.getData1() );
	            		elementShortMessage.setAttribute("data2", "" + sm.getData2() );

	            		elementEvent.appendChild(elementShortMessage);
	            		
	            	} 

	            	else if (sm.getCommand() == ShortMessage.STOP)
	            	{

	            		elementShortMessage.setAttribute("command", "STOP");
	            		elementShortMessage.setAttribute("channel", "" + sm.getChannel() );
	            		elementShortMessage.setAttribute("data1", "" + sm.getData1() );
	            		elementShortMessage.setAttribute("data2", "" + sm.getData2() );

	            		elementEvent.appendChild(elementShortMessage);
	            		
	            	} 
	            	else if (sm.getCommand() == ShortMessage.ACTIVE_SENSING)
	            	{

	            		elementShortMessage.setAttribute("command", "ACTIVE_SENSING");
	            		elementShortMessage.setAttribute("channel", "" + sm.getChannel() );
	            		elementShortMessage.setAttribute("data1", "" + sm.getData1() );
	            		elementShortMessage.setAttribute("data2", "" + sm.getData2() );

	            		elementEvent.appendChild(elementShortMessage);
	            		
	            	} 
	            	else if (sm.getCommand() == ShortMessage.SYSTEM_RESET)
	            	{

	            		elementShortMessage.setAttribute("command", "SYSTEM_RESET");
	            		elementShortMessage.setAttribute("channel", "" + sm.getChannel() );
	            		elementShortMessage.setAttribute("data1", "" + sm.getData1() );
	            		elementShortMessage.setAttribute("data2", "" + sm.getData2() );

	            		elementEvent.appendChild(elementShortMessage);
	            		
	            	} 
					else if (sm.getCommand() == ShortMessage.NOTE_OFF)
					{

	            		elementShortMessage.setAttribute("command", "NOTE_OFF");
	            		elementShortMessage.setAttribute("channel", "" + sm.getChannel() );
	            		elementShortMessage.setAttribute("key", "" + sm.getData1() );
	            		elementShortMessage.setAttribute("velocity", "" + sm.getData2() );

	            		elementEvent.appendChild(elementShortMessage);
	            		
					}
					else if (sm.getCommand() == ShortMessage.NOTE_ON)
	            	{

	            		elementShortMessage.setAttribute("command", "NOTE_ON" );
	            		elementShortMessage.setAttribute("channel", "" + sm.getChannel() );
	            		elementShortMessage.setAttribute("key", "" + sm.getData1() );
	            		elementShortMessage.setAttribute("velocity", "" + sm.getData2() );

	            		elementEvent.appendChild(elementShortMessage);
	            		
	            	} 
	                else if (sm.getCommand() == ShortMessage.POLY_PRESSURE)
	                {

	            		elementShortMessage.setAttribute("command", "POLY_PRESSURE" );
	            		elementShortMessage.setAttribute("channel", "" + sm.getChannel() );
	            		elementShortMessage.setAttribute("data1", "" + sm.getData1() );
	            		elementShortMessage.setAttribute("data2", "" + sm.getData2() );

	            		elementEvent.appendChild(elementShortMessage);
	            		
                    }
					else if (sm.getCommand() == ShortMessage.CONTROL_CHANGE)
	                {

	            		elementShortMessage.setAttribute("command", "CONTROL_CHANGE" );
	            		elementShortMessage.setAttribute("channel", "" + sm.getChannel() );
	            		elementShortMessage.setAttribute("data1", "" + sm.getData1() );
	            		elementShortMessage.setAttribute("data2", "" + sm.getData2() );

	            		elementEvent.appendChild(elementShortMessage);
	            		
                    }
	                else if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE)
	                {
	                	
	            		elementShortMessage.setAttribute("command", "PROGRAM_CHANGE" );
	            		elementShortMessage.setAttribute("channel", "" + sm.getChannel() );
	            		elementShortMessage.setAttribute("instrument", "" + sm.getData1() );

	            		elementEvent.appendChild(elementShortMessage);
	            		
                    }
	                else if (sm.getCommand() == ShortMessage.CHANNEL_PRESSURE)
	                {

	                	elementShortMessage.setAttribute("command", "CHANNEL_PRESSURE" );
	            		elementShortMessage.setAttribute("channel", "" + sm.getChannel() );
	            		elementShortMessage.setAttribute("value", "" + sm.getData1() );
	            		elementShortMessage.setAttribute("data2", "" + sm.getData2() );
	            		
	            		elementEvent.appendChild(elementShortMessage);

                    }
	                else if (sm.getCommand() == ShortMessage.PITCH_BEND)
	                {

	                	elementShortMessage.setAttribute("command", "PITCH_BEND" );
	            		elementShortMessage.setAttribute("channel", "" + sm.getChannel() );
	            		elementShortMessage.setAttribute("data1", "" + sm.getData1() );
	            		elementShortMessage.setAttribute("data2", "" + sm.getData2() );

	            		elementEvent.appendChild(elementShortMessage);
	            		
                    }
	                else
	                {

	                	elementShortMessage.setAttribute("command", "" + sm.getCommand() );
	            		elementShortMessage.setAttribute("channel", "" + sm.getChannel() );
	            		elementShortMessage.setAttribute("data1", "" + sm.getData1() );
	            		elementShortMessage.setAttribute("data2", "" + sm.getData2() );

	            		elementEvent.appendChild(elementShortMessage);
	            		
                    }

	            }
	            else if (message instanceof SysexMessage)
	            {
	            	
	            	SysexMessage sm = (SysexMessage) message;
	            	
	            }

	            elementTrack.appendChild(elementEvent);

	        }

	        elementSequence.appendChild(elementTrack);

	    }
		
		document.appendChild(elementSequence);

		// Ecriture du fichier
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer = transformerFactory.newTransformer();

	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");

	    DOMSource source = new DOMSource( document );

	    StreamResult result = new StreamResult( xmlOut01 );

	    transformer.transform(source, result);

	}

	public static void main(String[] args)
	{

    	try
    	{

    		play();
    		// describe();
    		// listInstruments();
    		// compose01();
    		// compose02();
    		// compose03();
    		// importXML();
    		// exportXML();

    	}
    	catch (Exception e)
    	{

    		System.out.println( e.getMessage() );
		}

	}

}
