package btremote999.geofencetest;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import btremote999.geofencetest.data.MyCircle;
import btremote999.geofencetest.data.MyGeoFenceData;
import btremote999.geofencetest.utils.Logger;

public class GeofenceAddDialog extends DialogFragment {
    public static final String TAG = "GeofenceAddDialog";

    EditText edWifi;
    EditText edLat;
    EditText edLng;
    EditText edRadius;

    public MainVM mMainVM;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context = getActivity();

        // sample only
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.add_geofence)
                .setView(R.layout.geofence_add_dialog)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null);


        // Create the AlertDialog object and return it
        Dialog dlg = builder.create();
        // prevent cancel on outside touch
        dlg.setCanceledOnTouchOutside(false);
        dlg.setOnShowListener(dialogInterface -> {
            mMainVM = ViewModelProviders.of(getActivity()).get(MainVM.class);

            View map = dlg.findViewById(R.id.grpMap);

            edWifi = dlg.findViewById(R.id.edWifi);
            edLat = dlg.findViewById(R.id.edLat);
            edLng = dlg.findViewById(R.id.edLng);
            edRadius = dlg.findViewById(R.id.edRadius);

            bindView(dlg);
            if (map != null)
                map.setOnClickListener(this::onMapClicked);

            Button btnPositive = ((AlertDialog)dlg).getButton(AlertDialog.BUTTON_POSITIVE);
            Button btnNegative = ((AlertDialog) dlg).getButton(AlertDialog.BUTTON_NEGATIVE);

            btnPositive.setOnClickListener((view)-> onDialogResult(DialogInterface.BUTTON_POSITIVE));
            btnNegative.setOnClickListener((view)-> onDialogResult(DialogInterface.BUTTON_NEGATIVE));

        });

        return dlg;
    }



//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
////        mMainVM = ViewModelProviders.of(getActivity()).get(MainVM.class);
//
////        new Handler(Looper.getMainLooper()).post(() -> {
////            Dialog dlg = getDialog();
////            View map = dlg.findViewById(R.id.grpMap);
////
////            edWifi = dlg.findViewById(R.id.edWifi);
////            edLat = dlg.findViewById(R.id.edLat);
////            edLng = dlg.findViewById(R.id.edLng);
////            edRadius = dlg.findViewById(R.id.edRadius);
////
////
////            bindView(dlg);
////            if (map != null)
////                map.setOnClickListener(this::onMapClicked);
////        });
//        return super.onCreateView(inflater, container, savedInstanceState);
//    }

    private void bindView(Dialog dlg) {
        if (mMainVM == null) {
            Logger.w(TAG, "bindView: mMainVM is null -> abort");
            return;
        }

        MyGeoFenceData data = mMainVM.mDialogData;
        if (data == null) {
            Logger.d(TAG, "bindView: no dialog data ->  abort");
            return;
        }

        if (edWifi == null) {
            edWifi = dlg.findViewById(R.id.edWifi);
            edLat = dlg.findViewById(R.id.edLat);
            edLng = dlg.findViewById(R.id.edLng);
            edRadius = dlg.findViewById(R.id.edRadius);
        }

        edWifi.setText(data.name);
        edLat.setText(String.valueOf(data.lat));
        edLng.setText(String.valueOf(data.lng));

        if (data.surface != null) {
            if (data.surface instanceof MyCircle) {
                MyCircle myCircle = (MyCircle) data.surface;
                edRadius.setText(String.valueOf(myCircle.radius));
            }
        }

    }


    private void onMapClicked(View view) {
        // backup data and changed state
        mMainVM.mDialogData = saveData();

        mMainVM.mGeofenceEditState.setValue(StateFlow.GEO_FENCE_PICK_LOCATION);

        // close dialog
        dismiss();
    }

    private void onDialogResult(int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE:
                // cancel -> do nothing
                dismiss();
                mMainVM.mDialogData = null;
                mMainVM.mGeofenceEditState.setValue(StateFlow.NONE);
                break;


            case DialogInterface.BUTTON_POSITIVE:
                if (validateData()) {
                    dismiss();

                    mMainVM.mDialogData = saveData();
                    mMainVM.mGeofenceEditState.setValue(StateFlow.GEO_FENCE_ADD_COMPLETED);
                }else {
                    Logger.w(TAG, "onDialogResult: data invalid");

                }
                break;
        }


    }

    private boolean validateData() {
        try {
            // wifi name
            boolean isWifiNameOk = true;
            if(edWifi.getText() == null ||
                    edWifi.getText().toString().isEmpty()){
                isWifiNameOk = false;
                edWifi.setError("Wifi Name is empty");
            }else {
                edWifi.setError(null);
            }


            // Latitude
            boolean isLatOk = true;
            String sLat = null;
            if(edLat.getText() != null)
                sLat = edLat.getText().toString();

            if(sLat == null || sLat.isEmpty()) {
                edLat.setError("Latitude is empty");
                isLatOk = false;
            }else{
                edLat.setError(null);
            }

            if(isLatOk) {
                double dblLat = Double.valueOf(sLat);
                if (dblLat < -90.0 || dblLat > 90.0) {
                    edLat.setError("Invalid latitude value");
                    isLatOk = false;
                } else {
                    edLat.setError(null);
                }
            }


            // Longitude
            boolean isLngOk =true;
            String sLng = null;
            if(edLng.getText() != null)
                sLng = edLng.getText().toString();

            if(sLng == null || sLng.isEmpty()){
                edLng.setError("Longitude is empty");
                isLngOk = false;
            }else{
                edLng.setError(null);
            }

            if(isLngOk){
                double dblLat = Double.valueOf(sLng);
                if (dblLat < -180.0 || dblLat > 180.0) {
                    edLng.setError("Invalid longitude value");
                    isLngOk = false;
                } else {
                    edLng.setError(null);
                }
            }

            // Radius
            boolean isRadiusOk = true;
            String sRadius = null;
            if(edRadius.getText() != null)
                sRadius = edRadius.getText().toString();

            if(sRadius == null  || sRadius.isEmpty()){
                isRadiusOk = false;
                edRadius.setError("Radius is empty");
            }else {
                edRadius.setError(null);
            }

            if(isRadiusOk){
                int iRadius = Integer.valueOf(sRadius);
                if(iRadius > 200){
                    isRadiusOk = false;
                    edRadius.setError("Radius max is 200");
                }else{
                    edRadius.setError(null);
                }
            }

            return isLatOk && isLngOk && isRadiusOk && isWifiNameOk;
        }catch(Exception e){
            Logger.e(TAG, "validateData: Exception-cause[%s] msg[%s]", e.getCause(), e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private MyGeoFenceData saveData() {
        MyGeoFenceData ret = new MyGeoFenceData();

        if(edWifi.getText() != null && !edWifi.getText().toString().isEmpty())
            ret.name = edWifi.getText().toString();

        if(edLat.getText() != null && !edLat.getText().toString().isEmpty())
            ret.lat = Double.valueOf(edLat.getText().toString());
        if(edLng.getText() != null && !edLng.getText().toString().isEmpty())
            ret.lng = Double.valueOf(edLng.getText().toString());


        if(edRadius.getText() != null && !edRadius.getText().toString().isEmpty()) {
            MyCircle circle = new MyCircle();
            circle.radius = Float.valueOf(edRadius.getText().toString());
            ret.surface = circle;
        }
        return ret;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
