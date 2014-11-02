package com.conchordance.api.resources;

import com.conchordance.Conchordance;
import com.conchordance.api.ExceptionResponse;
import com.conchordance.fretted.fingering.ChordFingering;
import com.conchordance.music.Chord;
import com.conchordance.music.ChordType;
import com.conchordance.music.ChordTypeBank;
import com.conchordance.music.MusicException;

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
        try {
            return conchordance.getChord(root, chordType);
        } catch (MusicException muse) {
            throw new ExceptionResponse(400, muse.getMessage());
        }
    }

    @GET
    @Path("/fingering")
    public ChordFingering getChordFingering(
            @QueryParam("instrument") String instrumentName,
            @QueryParam("frets") String frets,
            @QueryParam("type") String chordType,
            @QueryParam("root") String root) {
        try {
            return conchordance.getChordFingering(root, chordType, frets, instrumentName);
        } catch (MusicException muse) {
            throw new ExceptionResponse(400, muse.getMessage());
        }
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
        try {
            return conchordance.getChords(instrumentName, root, chordType);
        } catch (MusicException muse) {
            throw new ExceptionResponse(400, muse.getMessage());
        }
    }

    @GET
    @Path("/alternate-fingerings")
    public ChordFingering[] getAlternateFingerings(
            @QueryParam("instrument") String instrumentName,
            @QueryParam("frets") String frets,
            @QueryParam("type") String chordType,
            @QueryParam("root") String root) {
        try {
            return conchordance.getChords(instrumentName, root, chordType, frets);
        } catch (MusicException muse) {
            throw new ExceptionResponse(400, muse.getMessage());
        }
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
