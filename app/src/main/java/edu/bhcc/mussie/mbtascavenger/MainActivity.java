package edu.bhcc.mussie.mbtascavenger;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return MbtaScavenger.newInstance();
    }

    private static final int REQUEST_ERROR = 0;

    @Override
    protected void onResume() {
        super.onResume();

        // checking for Google play services
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int errorCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = apiAvailability
                    .getErrorDialog(this, errorCode, REQUEST_ERROR,
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void
                                onCancel(DialogInterface dialog) {
                                    // Leave if  services are unavailable.
                                    finish();
                                }
                            });
            errorDialog.show();
        }
    }


}
