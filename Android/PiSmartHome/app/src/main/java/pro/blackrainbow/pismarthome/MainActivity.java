/*
    developer Ehsan Rabiei --> Ehsan6454@gmail.com
    Apache V2 Licence
*/


package pro.blackrainbow.pismarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    Button loginsubmit ;
    EditText username , password ;
    TextView wrong ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        loginsubmit = (Button)  findViewById(R.id.LoginBtn) ;
        username    = (EditText) findViewById(R.id.username) ;
        password    = (EditText) findViewById(R.id.password) ;
        wrong       = (TextView) findViewById(R.id.wrong);

        username.setText("");
        password.setText("");
        wrong.setText("");


    }

    public void LoginBtn(View vl){
        String user = username.getText().toString();
        String pass = password.getText().toString();
        String userType = "guest" ;
        Intent intent = new Intent( MainActivity.this , Menu.class );
        if(user.length() < 1 || pass.length() < 1) return;

        //Log.i("INFO --- > ", "pass" + pass + " username " + user) ;
        if(user.toLowerCase().equals("admin")){
            if(pass.equals("12311")){
                //login
                userType = "admin" ;
                intent.putExtra("usertype" , userType) ;
                wrong.setText("");
                startActivity(intent);
                return;
            }
            else//worng pass
                wrong.setText("Wrong Username/Password ");

        }
        if (user.toLowerCase().equals("user1")) { // user2
            if (pass.equals("12322")) {
                userType = "user1";
                wrong.setText("");
                intent.putExtra("usertype", userType);
                startActivity(intent);
                return;
            }//user

            else {
                wrong.setText("Wrong Username/Password ");
            }
        }
        if (user.toLowerCase().equals("user2")) { // user2
            if (pass.equals("12345")) {
                userType = "user2";
                wrong.setText("");
                intent.putExtra("usertype", userType);
                startActivity(intent);
                return;
            }//user

            else {
                wrong.setText("Wrong Username/Password ");
            }
        }

        else{
            wrong.setText("Wrong Username/Password ");
            return;
        }

    }


}