package de.muhmuhhum.einsamerwanderer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by tomuelle on 29.09.2016.
 */
public class Login extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button send = (Button) findViewById(R.id.btn_send);
        final EditText editText = (EditText) findViewById(R.id.editText_benuname);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().equals("")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Login.this);
                    builder1.setMessage("Bitte geben sie einen Benutzernamen ein");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });


                    AlertDialog alert11 = builder1.create();
                    alert11.show();


                } else {
                    sendMessage(v);
                }

            }


        });
    }


    public void sendMessage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText_benuname);
        String benuname = editText.getText().toString();
        intent.putExtra("benuname", benuname);
        startActivity(intent);


    }

}

