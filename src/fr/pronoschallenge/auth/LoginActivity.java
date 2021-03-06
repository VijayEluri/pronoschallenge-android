package fr.pronoschallenge.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import fr.pronoschallenge.R;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;
import fr.pronoschallenge.util.SecurityUtil;
import greendroid.app.GDActivity;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Delhoménie
 * Date: 18/12/10
 * Time: 17:08
 */
public class LoginActivity extends GDActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // check if an account exists
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);// getPreferences(Context.MODE_PRIVATE);
        if(preferences.contains("username") && preferences.contains("password")) {
            setActionBarContentView(R.layout.account_view);

            final TextView userNameText = (TextView) findViewById(R.id.account_view_username);
            userNameText.setText(preferences.getString("username", ""));

            final Button changeAccountButton = (Button) findViewById(R.id.button_logout);
            changeAccountButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    SharedPreferences.Editor prefEditor = preferences.edit();
                    prefEditor.remove("username");
                    prefEditor.remove("password");
                    prefEditor.commit();

                    refresh();
                }
            });

        } else {
            setActionBarContentView(R.layout.account_form);

            // retrieve account data
            final EditText userNameText = (EditText) findViewById(R.id.input_username);
            userNameText.setText(preferences.getString("username", ""));
            final EditText passwordText = (EditText) findViewById(R.id.input_password);
            passwordText.setText(preferences.getString("password", ""));

            // submit button
            final Button submitButton = (Button) findViewById(R.id.button_login);
            submitButton.setOnClickListener(new View.OnClickListener() {
                 public void onClick(View v) {
                     String userName = null;
                     String password = null;
                     final EditText userNameText = (EditText) findViewById(R.id.input_username);
                     if(userNameText != null) {
                        userName = userNameText.getText().toString();
                        if(userName != null && !"".equals(userName)) {
                            // TODO Display error message
                        }
                     }
                     final EditText passwordText = (EditText) findViewById(R.id.input_password);
                     if(passwordText != null) {
                        password = passwordText.getText().toString();
                        if(password != null && !"".equals(password)) {
                            // TODO Display error message
                        }
                     }

                     if(checkAccount(userName, password)) {
                         SharedPreferences.Editor prefEditor = preferences.edit();
                         prefEditor.putString("username", userName);
                         prefEditor.putString("password", SecurityUtil.encodeMD5(password));
                         prefEditor.commit();

                         showMessage("Compte valide");

                         refresh();
                     } else {
                         showMessage("Compte invalide");
                     }
                 }
            });

        }

    }

    @Override
    protected void onStart() {
        setTitle(getString(R.string.title_login));

        super.onStart();
    }

    private void showMessage(String message) {
         Toast msg = Toast.makeText(this, message, Toast.LENGTH_LONG);
         msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2);
         msg.show();
    }

    /**
     * Check the validity of the userName/password
     * @param userName
     * @param password
     * @return true if the account is correct
     */
    private boolean checkAccount(String userName, String password) {
        boolean isValidAccount = false;

        String strCompte = RestClient.get(new QueryBuilder(this.getAssets(), "/rest/compte/" + userName + "/" + SecurityUtil.encodeMD5(password)).getUri());

        try {
            JSONObject json = new JSONObject(strCompte);
            String result = json.getString("compte");
            isValidAccount = "ok".equals(result);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return isValidAccount;
    }

    /**
     * Refresh this activity
     */
    private void refresh() {
        Intent refresh = new Intent();
        refresh.setClassName("fr.pronoschallenge", "fr.pronoschallenge.auth.LoginActivity");
        startActivity(refresh);
        finish();
    }
}