package edu.bhcc.mussie.mbtascavenger;

/**
 * Created by Mussie on 12/2/2017.
 */

public class Station {


    String name;
    String location;
    boolean visited;

public Station(String mName, String mLocation, boolean mVisited)
{
    name = mName;
    location = mLocation;
    visited = mVisited;

}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public String getPhotoFilename() {
        return "IMG_" + getName() + ".jpg";
    }

}
