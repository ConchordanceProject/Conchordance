package com.conchordance.api.resources;

import com.conchordance.Conchordance;
import com.conchordance.instrument.FretboardModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.LinkedList;
import java.util.List;

@Path("/fretboards")
@Produces(MediaType.APPLICATION_JSON)
public class FretboardResource {
    private final Conchordance conchordance;

    public FretboardResource(Conchordance conchordance) {
        this.conchordance = conchordance;
    }

    @GET
    public List<List<Boolean>> getFretboardModels(
            @QueryParam("instrument") String instrument,
            @QueryParam("type") String chordType,
            @QueryParam("root") String root) {
        FretboardModel model = conchordance.getFretboard(instrument, root, chordType);
        List<List<Boolean>> stringArray = new LinkedList<>();

        for (int s = 0; s < model.getInstrument().strings; s++) {
            List<Boolean> fretArray = new LinkedList<>();
            for (int f = 0; f < model.getInstrument().frets; f++) {
                fretArray.add(model.hasNoteAt(s, f));
            }
            stringArray.add(fretArray);
        }

        return stringArray;
    }
    
    @OPTIONS
    public void options() {
    }
}
