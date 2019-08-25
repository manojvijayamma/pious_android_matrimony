package com.mega.matrimony.retrofit;




import com.mega.matrimony.Utility.Utils;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Initialize retrofit object
 * Created by Nasirali on 02-02-2019.
 */

public class RetrofitClient {
    private static Retrofit retrofit = null;


    //TODO Retrofit Use for simple get request
    public static Retrofit getClient() {
        if (retrofit == null) {
            //TODO Retrofit End add headers in api call

            retrofit = new Retrofit.Builder()
                    .baseUrl(Utils.Base_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
