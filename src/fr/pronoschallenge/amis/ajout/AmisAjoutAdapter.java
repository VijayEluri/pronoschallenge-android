package fr.pronoschallenge.amis.ajout;

import java.util.List;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import fr.pronoschallenge.R;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;


public class AmisAjoutAdapter extends ArrayAdapter<AmisAjoutEntry> {

    public AmisAjoutAdapter(Context context, int textViewResourceId,
                         List<AmisAjoutEntry> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //le layout repr�sentant la ligne dans le listView
            view = li.inflate(R.layout.amis_ajout_liste_item, null);
        }
        AmisAjoutEntry amisAjoutEntry = getItem(position);
        
        if (amisAjoutEntry != null) {
        	CheckBox pseudoCheckBox = (CheckBox) view.findViewById(R.id.ajoutAmisCheck);
            pseudoCheckBox.setChecked(amisAjoutEntry.getAmi());
            pseudoCheckBox.setVisibility(View.VISIBLE);
            pseudoCheckBox.setTag(R.id.objetAmi, amisAjoutEntry);
            pseudoCheckBox.setTag(R.id.idAmi, amisAjoutEntry.getPseudo());
            pseudoCheckBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View checkBoxView) {
            		String strAction;
            		String strChecked;
            		if (((CheckBox)checkBoxView).isChecked()) {
            			strAction = "add";
            			strChecked = "1";
            		} else  {
            			strAction = "del";
            			strChecked = "0";
            		}
                    new GererAmisTask(checkBoxView).execute(strAction);
                    // mise à jour de la valeur de l'objet amisAjoutEntry pour que les boutons
                    // reprennent un état correct si il disparraissent de l'écran puis réapparraissent
                    AmisAjoutEntry amisAjoutEntry = (AmisAjoutEntry) checkBoxView.getTag(R.id.objetAmi);
                    amisAjoutEntry.setAmi(strChecked);
                }
            });
            TextView pseudoTextView = (TextView) view.findViewById(R.id.ajoutAmisPseudo);
            pseudoTextView.setText(amisAjoutEntry.getPseudo());
            pseudoTextView.setVisibility(View.VISIBLE);
            TextView nomTextView = (TextView) view.findViewById(R.id.ajoutAmisNom);
            nomTextView.setText(amisAjoutEntry.getPrenom() + " " + amisAjoutEntry.getNom());
            nomTextView.setVisibility(View.VISIBLE);

        }

        return view;
    }


    class GererAmisTask extends AsyncTask<String, Void, Boolean> {

        private View checkBoxView;

        GererAmisTask(View checkBoxView) {
            this.checkBoxView = checkBoxView;
        }

        @Override
        protected Boolean doInBackground(String... args) {
        	View parentView = (View) checkBoxView.getParent();
        	TextView pseudoTextView = (TextView) parentView.findViewById(R.id.ajoutAmisPseudo);
            String strPseudoAmi = (String) pseudoTextView.getText();
            String strAction = args[0];

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("ami", strPseudoAmi);
            } catch (JSONException je) {
                je.printStackTrace();
            }
            JSONArray jsonDataArray = new JSONArray();
            jsonDataArray.put(jsonObject);

            String userName = PreferenceManager.getDefaultSharedPreferences(checkBoxView.getContext()).getString("username", null);
            String password = PreferenceManager.getDefaultSharedPreferences(checkBoxView.getContext()).getString("password", null);
            String url = new QueryBuilder(checkBoxView.getContext().getAssets(), "/rest/listeAmis/" + userName + "/?action=" + strAction + "&ami=" + strPseudoAmi).getUri();
            HttpResponse response = RestClient.postData(url, "", userName, password);

            if (response.getStatusLine().getStatusCode() != 200) {
                Toast toast = Toast.makeText(checkBoxView.getContext(), "Erreur lors de la mise � jour de votre ami : " + response.getStatusLine().getStatusCode(), 4);
                toast.show();
            } else {
                try {
                    String message = RestClient.convertStreamToString(response.getEntity().getContent());
                    if(message.length() > 0) {
                        Toast toast = Toast.makeText(checkBoxView.getContext(), "Erreur lors de la mise � jour de votre ami : " + message, 4);
                        toast.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return true;
        }
    }
    
}
