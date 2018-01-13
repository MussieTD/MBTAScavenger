package edu.bhcc.mussie.mbtascavenger.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;


import edu.bhcc.mussie.mbtascavenger.Station;
import edu.bhcc.mussie.mbtascavenger.database.StationDbSchema.StationsTable;



/**
 * Created by Mussie on 10/15/2017.
 */

public class StationCursorWrapper extends CursorWrapper {

    public StationCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Station getStation() {
        String name = getString(getColumnIndex(StationsTable.Cols.NAME));
        String location = getString(getColumnIndex(StationsTable.Cols.LOCATION));
        boolean visited = getString(getColumnIndex(StationsTable.Cols.VISITED)).equals("true") ? true : false;


        Station Station = new Station(name,location,visited);
         return Station;
    }
}
