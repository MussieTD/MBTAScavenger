package edu.bhcc.mussie.mbtascavenger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mussie on 12/2/2017.
 */

public class MbtaScavenger extends Fragment {

    public static MbtaScavenger newInstance()
    {
        return new MbtaScavenger();
    }

   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }
*/
   private static final String PREF_DISTANCE_UNIT = "userDistanceUnitPref";
   private RadioButton kmPref;
   private RadioButton milePref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.front_page_fragment, container, false);
        ImageView banner = (ImageView) v.findViewById(R.id.imageView);
        TextView scoreTv = (TextView) v.findViewById(R.id.score_textView);

        Button locateBtn = (Button) v.findViewById(R.id.locate_btn);
      /*  Button historyBtn = (Button) v.findViewById(R.id.history_btn);*/
        Button mapBtn = (Button) v.findViewById(R.id.map_btn);
        Button gloatBtn = (Button) v.findViewById(R.id.gloat_btn);

         kmPref = (RadioButton) v.findViewById(R.id.km_radio_btn);
         milePref = (RadioButton) v.findViewById(R.id.miles_radio_btn);

         kmPref.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                 onRadioButtonClicked(kmPref);
             }
         });
        milePref.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onRadioButtonClicked(milePref);
            }
        });

        if (getStoredPrefrence_prefKM(getContext()))
            kmPref.setChecked(true);
        else
            milePref.setChecked(true);
        Log.i("pref manager","checking " +getStoredPrefrence_prefKM(getContext()));

        final StationsLab stationsLab = StationsLab.get(getActivity());
        final List<Station> myStations = stationsLab.getStations();
        String x = getString(R.string.score,stationsLab.getNumberOfVisited(),myStations.size());
        Log.i("visited locs: ",x);
        scoreTv.setText(x);


        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Mbta_map_fragment llf = new Mbta_map_fragment();
                ft.replace(R.id.fragment_container, llf);
                ft.commit();

            }
        });

        locateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent locateIntent = new Intent(getActivity(),StationFoundActivity.class);
                startActivity(locateIntent);
            }
        });



        gloatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    // note that the onPause will be called for the hosting activity
                    // and that it may interfere with this intent if another is called

                    Intent shareIntent = ShareCompat.IntentBuilder.from(getActivity())
                            .setSubject(getString(R.string.text_title))
                            .setChooserTitle(R.string.send_report)
                            .setType("text/plain")
                            .setText(getString(R.string.text_content,stationsLab.getNumberOfVisited(),myStations.size()))
                            .getIntent();

                    if (shareIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        // if there is an app that can open this
                        startActivity(shareIntent);
                    }

                } catch (Exception k) {
                    System.out.println("Exception sending intent: " + k.toString());
                }
            }
        });

        return v;
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?

        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.km_radio_btn:
                if (checked)
                setStoredQuery_prefKm(getContext(),true);
                break;
            case R.id.miles_radio_btn:
                if (checked)
                setStoredQuery_prefKm(getContext(),false);
                break;
        }
        Log.i("radioButtons"," in here");


    }

    public static boolean getStoredPrefrence_prefKM(Context context) {


        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_DISTANCE_UNIT, true);

    }

    public static void setStoredQuery_prefKm(Context context, boolean prefKM) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit() // after here you add many changes and then apply
                .putBoolean(PREF_DISTANCE_UNIT, prefKM)
                .apply();
        Log.i("pref manager","settin query "+ prefKM);
    }



}
