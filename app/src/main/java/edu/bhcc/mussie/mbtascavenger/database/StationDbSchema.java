package edu.bhcc.mussie.mbtascavenger.database;

/**
 * Created by Mussie on 10/15/2017.
 */

public class StationDbSchema {

    public static final class StationsTable {
        public static final String NAME = "stations";


        public static final class Cols {
            public static final String NAME = "name";
            public static final String LOCATION = "location";
            public static final String VISITED = "visited";

        }
    }
}
