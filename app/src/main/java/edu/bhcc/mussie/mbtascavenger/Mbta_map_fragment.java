package edu.bhcc.mussie.mbtascavenger;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Mussie on 12/2/2017.
 */

public class Mbta_map_fragment extends Fragment {


    public static MbtaScavenger newInstance()
    {
        return new MbtaScavenger();
    }
public Button backBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.i("here","");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mbta_map, container, false);
        ImageView mapIv = (ImageView) v.findViewById(R.id.map_imageView);
        mapIv.setImageResource(R.drawable.mbta_transit_map);

        backBtn = (Button) v.findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                MbtaScavenger llf = new MbtaScavenger();
                ft.replace(R.id.fragment_container, llf);
                ft.commit();
            }
        });

        Log.i("here","at");


        return v;
    }


}
