package edu.bhcc.mussie.mbtascavenger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import edu.bhcc.mussie.mbtascavenger.database.*;
import edu.bhcc.mussie.mbtascavenger.database.StationDbSchema.StationsTable;

/**
 * Created by Mussie on 12/2/2017.
 */

public class StationsLab {


        private static StationsLab sStationsLab;

        private Context mContext;
        private SQLiteDatabase mDatabase;
        private List<Station> mStations;

        public static StationsLab get(Context context) {
            if (sStationsLab == null) {
                sStationsLab = new StationsLab(context);
            }
            return sStationsLab;
        }

      /*  private StationsLab(Context context) {
            mContext = context.getApplicationContext();
            mDatabase = new StationBaseHelper(mContext)
                    .getWritableDatabase();

        }*/


public int getCount()
{
    return mStations.size();
}
    private StationsLab(Context context) {
        mContext = context.getApplicationContext();

        // get a database helper
        StationBaseHelper myDbHelper = new StationBaseHelper(mContext);

        // try to create a database
        try {
            myDbHelper.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        // try to get a readable database
        try {
            //mDatabase = myDbHelper.openDataBase();
            mDatabase = myDbHelper.getReadableDatabase();
            mStations = new ArrayList<>();

            // get all medicines from a database to an array
            StationCursorWrapper cursor = queryStations(null, null, StationDbSchema.StationsTable.Cols.NAME);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                mStations.add(cursor.getStation());
                cursor.moveToNext();
            }
            cursor.close();


        }catch(SQLException sqle){

            throw sqle;

        }
    }

    public List<Station> getStations() {
        return mStations;
    }

    public Station getDummy()
    {
        Log.i("location from","dummy");
        return mStations.get(0);
    }
    public Station getClosestStation(String currentLocation)
    {
        double latitude = getLatitude(currentLocation);
        double  longitude = getLongitude(currentLocation);
        double distance = getDistanceFromLatLonInKm(currentLocation,mStations.get(0).getLocation());
        String tempStationName = mStations.get(0).getName();

        for (int i=0; i < mStations.size(); i++)
        {
            Log.i("locat","mSt size: " + mStations.size() +" comparing " + getDistanceFromLatLonInKm(currentLocation,mStations.get(i).getLocation())
            + " with " + distance);
            Log.i("locat"," comparing: " + mStations.get(i).getName() + " with " + tempStationName);
            if (getDistanceFromLatLonInKm(currentLocation,mStations.get(i).getLocation()) > distance)
            {
                distance = getDistanceFromLatLonInKm(currentLocation,mStations.get(i).getLocation());
                tempStationName = mStations.get(i).getName();
            }
        }

Log.i("location from: " , "getClosestSTation");

     return getStation(tempStationName);
    }

    public void setStationVisited(Station station)
    {

        String name = station.getName();
        ContentValues values = getContentValues(station);
        mDatabase.update(StationsTable.NAME, values,
                StationsTable.Cols.NAME + " = ?",
                new String[] { name });
        // kind of saying update in this table where row's UUID is the passed UUID string

    }

    public double getClosestStationDistance(String currentLocation)
    {
        double latitude = getLatitude(currentLocation);
        double  longitude = getLongitude(currentLocation);
        double distance = getDistanceFromLatLonInKm(currentLocation,mStations.get(0).getLocation());
        String tempStationName = mStations.get(0).getName();
        int x=0;
        for (int i=0; i < mStations.size(); i++)
        {
            Log.i("locat","mSt size: " + mStations.size() +" comparing " + getDistanceFromLatLonInKm(currentLocation,mStations.get(i).getLocation())
                    + " with " + distance);
            Log.i("locat"," comparing: " + mStations.get(i).getName() + " with " + tempStationName);
            if (getDistanceFromLatLonInKm(currentLocation,mStations.get(i).getLocation()) > distance)
            {
                distance = getDistanceFromLatLonInKm(currentLocation,mStations.get(i).getLocation());
              x=i;
            }
        }

        Log.i("location from: " , "currentLocation: " + currentLocation + " closestStationLocation: " +
        mStations.get(x).getLocation() + " final closest diatance: " + distance);


        return distance;

    }


public double getLatitude(String location)
{
    int i =0;
    String longitude = "";

    while(location.charAt(i) != ',') {
        longitude += (location).charAt(i);
        i++;
    }

    double longitudeValue = Double.parseDouble(longitude);
    Log.i("loc getLongitude","input: " + location + " longitude " + longitudeValue);
    return longitudeValue;
}

    public double getLongitude(String location)
    {
        int i =0;
        String latitude = "";

        while(location.charAt(i) != ',') {
          //  latitude += (location).charAt(i);
            i++;
        }

        double latitudeValue = Double.parseDouble(location.substring(i+1));
        Log.i("loc getLatitude","input: " + location + " latitude " + latitudeValue);
        return latitudeValue;
    }

    /*public double getGroundDistance(String loc1, String loc2)
    {
        double loc1Long, loc1Lat, loc2Long, loc2Lat;

        loc1Long = getLongitude(loc1);
        loc1Lat = getLatitude(loc1);
        loc2Long = getLongitude(loc2);
        loc2Lat = getLatitude(loc2);

       // c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));


        Log.i("location","firstDistance: " + getDistance(loc1Lat,loc2Lat) + " between loc1lat: " + loc1Lat +
        " loc2Lat " + loc2Lat);
        Log.i("location: ","distance " + (getDistance(loc1Lat,loc2Lat) + getDistance(loc1Long,loc2Long))/2);
        return ((getDistance(loc1Lat,loc2Lat) + getDistance(loc1Long,loc2Long))/2);
    }*/


    public double getDistanceFromLatLonInKm(String loc1, String loc2) {
        double loc1Long, loc1Lat, loc2Long, loc2Lat;

        loc1Long = getLongitude(loc1);
        loc1Lat = getLatitude(loc1);
        loc2Long = getLongitude(loc2);
        loc2Lat = getLatitude(loc2);

        Log.i("values in distanceKM","cur: Long: " + loc1Long + " Lat: " + loc1Lat + " \n statitonLoc: long: " +
        loc2Long + " lat: " + loc2Lat);

        double R = 6371; // Radius of the earth in km
        double dLat = deg2rad(loc2Lat-loc1Lat);  // deg2rad below
        double dLon = deg2rad(loc2Long-loc1Long);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(loc1Lat)) * Math.cos(deg2rad(loc2Lat)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in km
        return d;
    }

    public double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }



    public boolean isCloseEnough (double distance, double largestDistance)
    {
        return (distance <= largestDistance ? true : false);
    }

    public double getDistance(double x, double y)
    {
        Log.i("location"," Math.pow(x,2) " + Math.pow(x,2) + "  Math.pow(y,2) " +  Math.pow(y,2)
        + " square rooted: " + Math.pow(( Math.pow(x,2) + Math.pow(y,2)),(0.5)));
        return Math.pow(( Math.pow(x,2) + Math.pow(y,2))  ,  (0.5));
    }


    public int getNumberOfVisited()
    {
        StationCursorWrapper cursor = queryStations(
                StationsTable.Cols.VISITED + " = ?",
                new String[] { "true" },
                StationsTable.Cols.VISITED
        );

        try {
            Log.i("visited locs","cursor.getCount: "+ cursor.getCount());
            if (cursor.getCount() == 0) {
                return 0;
            }

           return cursor.getCount();
        } finally {
            cursor.close();
        }

    }

    public Station getStation(String name) {

        StationCursorWrapper cursor = queryStations(
                StationDbSchema.StationsTable.Cols.NAME + " = ?",
                new String[] { name },
                StationDbSchema.StationsTable.Cols.NAME
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getStation();
        } finally {
            cursor.close();
        }
    }
    private StationCursorWrapper queryStations(String whereClause, String[] whereArgs, String orderBy) {
        Cursor cursor = mDatabase.query(
                StationDbSchema.StationsTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                orderBy  // orderBy
        );

        return new StationCursorWrapper(cursor);
    }


    public List<Station> getSpecificMedicines(String whereClause, String[] whereArgs, String orderBy) {
        StationCursorWrapper cursor = queryStations(whereClause, whereArgs, orderBy);
        List<Station> list = new ArrayList<>();

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(cursor.getStation());
                cursor.moveToNext();
            }

            return list;
        } finally {
            cursor.close();
        }
    }

    public double getDistance(String myCurrentLocation, boolean inKm)
    {
        return getClosestStationDistance(myCurrentLocation) *
                (inKm ? 1 : 0.621371);

    }

    public List<Station> getStationsWithRawSql(String rawSql, String[] whereArgs) {
        StationCursorWrapper cursor = rawQueryCrimes(rawSql, whereArgs);
        List<Station> list = new ArrayList<>();

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(cursor.getStation());
                cursor.moveToNext();
            }

            return list;
        } finally {
            cursor.close();
        }
    }

    private StationCursorWrapper rawQueryCrimes(String rawSql, String[] whereArgs) {
        Cursor cursor = mDatabase.rawQuery(rawSql, whereArgs);

        return new StationCursorWrapper(cursor);
    }
    private static ContentValues getContentValues(Station station) {
            ContentValues values = new ContentValues();
            values.put(StationsTable.Cols.NAME, station.getName());
            values.put(StationsTable.Cols.LOCATION, station.getLocation());
            values.put(StationsTable.Cols.VISITED,station.isVisited() ? "true" : "false");

            return values;
        }

        public void updateStation(Station station) {
            String name = station.getName();
            ContentValues values = getContentValues(station);
            mDatabase.update(StationsTable.NAME, values,
                    StationsTable.Cols.NAME + " = ?",
                    new String[] { name });
            // kind of saying update in this table where row's UUID is the passed UUID string
        }

    //    return new StationCursorWrapper(cursor);
   // }

        public File getPhotoFile(Station station) {
            File externalFilesDir = mContext
                    .getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (externalFilesDir == null) {
                System.out.println("external files dir is null");  // doesn't work for API 18
                return null;
            }
            System.out.println("returning file name: " + new File(externalFilesDir, station.getPhotoFilename()));
            return new File(externalFilesDir, station.getPhotoFilename());
        }
    }


