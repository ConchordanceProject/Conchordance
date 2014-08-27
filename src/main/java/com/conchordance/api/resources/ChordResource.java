package com.conchordance.api.resources;

import com.conchordance.Conchordance;
import com.conchordance.fingers.ChordFingering;
import com.conchordance.music.Chord;
import com.conchordance.music.ChordType;
import com.conchordance.music.ChordTypeBank;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/chords")
@Produces(MediaType.APPLICATION_JSON)
public class ChordResource {
    private final Conchordance conchordance;

    public ChordResource(Conchordance conchordance) {
        this.conchordance = conchordance;
    }

    @GET
    public Chord getChord(
            @QueryParam("type") String chordType,
            @QueryParam("root") String root) {
        return conchordance.getChord(root, chordType);
    }
    
    @OPTIONS
    public void options() {
    }

    @GET
    @Path("/fingerings")
    public ChordFingering[] getChordFingering(
            @QueryParam("instrument") String instrumentName,
            @QueryParam("type") String chordType,
            @QueryParam("root") String root) {
        return conchordance.getChords(instrumentName, root, chordType);
    }
    
    @OPTIONS
    @Path("/fingerings")
    public void fingeringOptions() {
    }

    @GET
    @Path("/types")
    public List<ChordType> getChordTypes() {
        return ChordTypeBank.DEFAULT_BANK.getChordTypes();
    }
    
    @OPTIONS
    @Path("/types")
    public void typeOptions() {
    }
}
