package com.mega.matrimony.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mega.matrimony.R;

public class SuccessActivity extends AppCompatActivity {
    private Button btn_login;//,btn_partner;
    String ragistered_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        btn_login=(Button)findViewById(R.id.btn_id);
       // btn_partner=(Button)findViewById(R.id.btn_partner);

        Bundle b=getIntent().getExtras();
        if (b!=null){
            if (b.containsKey("ragistered_id")){
                ragistered_id=b.getString("ragistered_id");
            }
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SuccessActivity.this,LoginActivity.class));
            }
        });


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SuccessActivity.this,LoginActivity.class));
    }
}
