package com.mega.matrimony.Network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.mega.matrimony.Application.MyApplication;

public class NetworkChangeReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, final Intent intent) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        if (!isConnected)
            checkNet(false);
        else
            MyApplication.getInstance().getCurrentActivity().recreate();

    }

    private void checkNet(final boolean isConnect){
        View view=MyApplication.getInstance().getCurrentView();
        String msg;
        int length;
        if (isConnect){
            msg="You are Connected to the Internet";
            length=Snackbar.LENGTH_SHORT;
        }else {
            msg="No Internet Connection.Please Connect to Internet.";
            length=Snackbar.LENGTH_INDEFINITE;
        }

        Snackbar snackbar=Snackbar.make(view,msg,length );
        if (!isConnect){
            snackbar.setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    checkNet(isConnect);

                }
            });
        }
        snackbar.show();
    }

}