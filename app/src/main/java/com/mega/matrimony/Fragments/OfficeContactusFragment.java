package com.mega.matrimony.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class OfficeContactusFragment extends Fragment{// implements OnMapReadyCallback
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
//    SupportMapFragment mMapFragment;
//    private GoogleMap mMap;
    private TextView tv_mobile,tv_address,tv_email;
    Common common;
    SessionManager session;
    private ProgressDialog pd;
    Context context;
    String addres="";

    public OfficeContactusFragment() {
        // Required empty public constructor
    }

    public static OfficeContactusFragment newInstance(String param1, String param2) {
        OfficeContactusFragment fragment = new OfficeContactusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    //789481358657-l11g9nccvju47gvnvfhsv53hu44n9vu4.apps.googleusercontent.com

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_office_contactus, container, false);

        common=new Common(getActivity());
        session=new SessionManager(getActivity());
        context=getActivity();

        tv_mobile=(TextView)view.findViewById(R.id.tv_mobile);
        tv_address=(TextView)view.findViewById(R.id.tv_address);
        tv_email=(TextView)view.findViewById(R.id.tv_email);

        getData();

       // mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        return view;
    }

    private void getData() {
        pd=new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
        pd.setIndeterminate(true);
        pd.show();

        common.makePostRequest(Utils.site_data, new HashMap<String ,String>(), response -> {
            Log.d("resp",response);
            pd.dismiss();
            try {
                JSONObject object=new JSONObject(response);
                JSONObject config_data=object.getJSONObject("config_data");

                tv_address.setText(config_data.getString("full_address"));
                tv_email.setText(config_data.getString("contact_email"));
                tv_mobile.setText(config_data.getString("contact_no"));

                addres=config_data.getString("map_address");

//                    mMapFragment.getMapAsync(new OnMapReadyCallback() {
//                        @Override
//                        public void onMapReady(GoogleMap googleMap) {
//                            mMap = googleMap;
//
//                            LatLng sydney = getLocationFromAddress(context,addres);//22.995106699999997, 72.49887009999999
//                            if (sydney==null){
//                                sydney=new LatLng(22.9929931, 72.4989465);
//
//                            }
//                            Log.d("resp",sydney+" ");
//                            mMap.addMarker(new MarkerOptions().position(sydney).title("Narjis Infotech"));
//                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//                            mMap.setMinZoomPreference(12f);
//                        }
//                    });

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
            }
        });

    }

//    public LatLng getLocationFromAddress(Context context, String strAddress)
//    {
//        Geocoder coder= new Geocoder(context);
//        List<Address> address;
//        LatLng p1 = null;
//
//        try
//        {
//            address = coder.getFromLocationName(strAddress, 5);
//            if(address==null)
//            {
//                return null;
//            }
//            Address location = address.get(0);
//            location.getLatitude();
//            location.getLongitude();
//
//            p1 = new LatLng(location.getLatitude(), location.getLongitude());
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        return p1;
//
//    }

}
