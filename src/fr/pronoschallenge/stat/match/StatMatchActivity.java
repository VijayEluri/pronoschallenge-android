package fr.pronoschallenge.stat.match;

import fr.pronoschallenge.R;
import fr.pronoschallenge.classement.club.ClassementClubEntry;
import fr.pronoschallenge.rest.QueryBuilder;
import fr.pronoschallenge.rest.RestClient;
import fr.pronoschallenge.util.NetworkUtil;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.AsyncImageView;
import greendroid.widget.NormalActionBarItem;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class StatMatchActivity extends GDActivity {

	private String nomClubDomicile;
	private String nomClubExterieur;
	private String idMatch;
	static int PAGE_COUNT = 2;
	static int NUM_PAGE_COTE = 1;
	static int NUM_PAGE_CLASSEMENT = 2;	
	
	private StatMatchActivity statMatchActivity;
    private int currentPage;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setActionBarContentView(R.layout.stat_match);
    	
    	//On récupère l'objet Bundle envoyé par l'autre Activity
        Bundle objetbunble  = this.getIntent().getExtras();
        setNomClubDomicile((String) objetbunble.get("clubDomicile"));
        setNomClubExterieur((String) objetbunble.get("clubExterieur"));
        setIdMatch((String) objetbunble.get("idMatch"));
        
		if(NetworkUtil.isConnected(this.getApplicationContext())) {
            setTitle(getString(R.string.title_stat));
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Connexion Internet indisponible")
                .setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            finish();
                                       }
                                   });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
		statMatchActivity = this;
		
        ActionBarItem itemNext = getActionBar().newActionBarItem(NormalActionBarItem.class);
        itemNext.setDrawable(android.R.drawable.arrow_up_float);
        getActionBar().addItem(itemNext);		
		
		afficherPage(NUM_PAGE_COTE);
	}
	
	private void afficherPage(int numPage) {

		LinearLayout statMatchConfrontationLayout = (LinearLayout) statMatchActivity.findViewById(R.id.statMatchConfrontationLayout);
		LinearLayout statMatchClassementLayout = (LinearLayout) statMatchActivity.findViewById(R.id.statMatchClassementLayout);
		LinearLayout statMatchCoteLayout = (LinearLayout) statMatchActivity.findViewById(R.id.statMatchCoteLayout);
		LinearLayout statMatchFormeLayout = (LinearLayout) statMatchActivity.findViewById(R.id.statMatchFormeLayout);
		LinearLayout statMatchDerniersMatchLayout = (LinearLayout) statMatchActivity.findViewById(R.id.statMatchDerniersMatchLayout);		
		
		statMatchCoteLayout.setVisibility(View.GONE);
		statMatchFormeLayout.setVisibility(View.GONE);
		statMatchDerniersMatchLayout.setVisibility(View.GONE);
		statMatchConfrontationLayout.setVisibility(View.GONE);
		statMatchClassementLayout.setVisibility(View.GONE);
		
		currentPage = numPage;
		new InfoClubTask().execute("");
		
		if (currentPage == NUM_PAGE_COTE) {
			new CoteMatchTask().execute("");
			new StatMatchTask().execute("");
		} else if (currentPage == NUM_PAGE_CLASSEMENT) {
			new ConfrontationTask().execute("");
		}
	}

    @Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
    	if (currentPage == NUM_PAGE_COTE) {
    		getActionBar().getItem(0).setDrawable(android.R.drawable.arrow_down_float);
    		afficherPage(NUM_PAGE_CLASSEMENT);
    	} else {
    		getActionBar().getItem(0).setDrawable(android.R.drawable.arrow_up_float);
    		afficherPage(NUM_PAGE_COTE);    		
    	}    	
    	
        return true;
    }
	public String getNomClubDomicile() {
		return nomClubDomicile;
	}

	public void setNomClubDomicile(String nomClubDomicile) {
		this.nomClubDomicile = nomClubDomicile;
	}

	public String getNomClubExterieur() {
		return nomClubExterieur;
	}

	public void setNomClubExterieur(String nomClubExterieur) {
		this.nomClubExterieur = nomClubExterieur;
	}

	public String getIdMatch() {
		return idMatch;
	}

	public void setIdMatch(String idMatch) {
		this.idMatch = idMatch;
	}
	
	
	// S�rie en cours d'un club
	private List<StatMatchSerieEntry> getMatchSerie(String nomClub) {
		List<StatMatchSerieEntry> statMatchSerieEntries = new ArrayList<StatMatchSerieEntry>();

		String strMatchSerie = RestClient.get(new QueryBuilder(statMatchActivity.getAssets(), "/rest/serieClub/" + nomClub + "/?nbMatch=5").getUri());

		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strMatchSerie);

	        // A Simple JSONObject Parsing
	        JSONArray matchSerieArray = json.getJSONArray("serieClub");
	        for(int i = 0; i < matchSerieArray.length(); i++) {
	        	JSONObject jsonSerieEntry = matchSerieArray.getJSONObject(i);

	        	StatMatchSerieEntry statMatchSerieEntry = new StatMatchSerieEntry();	        	
	        	statMatchSerieEntry.setButDom(jsonSerieEntry.getInt("butDom"));
	        	statMatchSerieEntry.setButExt(jsonSerieEntry.getInt("butExt"));
	        	statMatchSerieEntry.setMatchDomExt(jsonSerieEntry.getString("type"));
        		statMatchSerieEntry.setNomClubDom(jsonSerieEntry.getString("clubDom"));
	        	statMatchSerieEntry.setNomClubExt(jsonSerieEntry.getString("clubExt"));
	        	if (statMatchSerieEntry.getMatchDomExt().compareTo("D") == 0) {
	        		if (statMatchSerieEntry.getButDom() == statMatchSerieEntry.getButExt()) { 
	        			statMatchSerieEntry.setTypeResultat("N");
	        		} else if (statMatchSerieEntry.getButDom() > statMatchSerieEntry.getButExt()) {
	        			statMatchSerieEntry.setTypeResultat("V");
	        		} else {
	        			statMatchSerieEntry.setTypeResultat("D");
	        		}
	        	} else {
	        		if (statMatchSerieEntry.getButDom() == statMatchSerieEntry.getButExt()) { 
	        			statMatchSerieEntry.setTypeResultat("N");
	        		} else if (statMatchSerieEntry.getButDom() > statMatchSerieEntry.getButExt()) {
	        			statMatchSerieEntry.setTypeResultat("D");
	        		} else {
	        			statMatchSerieEntry.setTypeResultat("V");
	        		}
	        	}
	        	
	        	statMatchSerieEntries.add(statMatchSerieEntry);
	        }

		} catch (JSONException e) {
            e.printStackTrace();
        }

		return statMatchSerieEntries;
	}
	
	
	// S�rie en cours d'un club
	private List<StatMatchSerieEntry> getConfrontation(String nomClubDom, String nomClubExt) {
		List<StatMatchSerieEntry> statMatchSerieEntries = new ArrayList<StatMatchSerieEntry>();

		String strMatchSerie = RestClient.get(new QueryBuilder(statMatchActivity.getAssets(), "/rest/confrontationClub/" + nomClubDom + "/?clubAdverse=" + nomClubExt).getUri());

		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strMatchSerie);

	        // A Simple JSONObject Parsing
	        JSONArray matchSerieArray = json.getJSONArray("confrontationClub");
	        for(int i = 0; i < matchSerieArray.length(); i++) {
	        	JSONObject jsonSerieEntry = matchSerieArray.getJSONObject(i);

	        	StatMatchSerieEntry statMatchSerieEntry = new StatMatchSerieEntry();	        	
	        	statMatchSerieEntry.setButDom(jsonSerieEntry.getInt("butDom"));
	        	statMatchSerieEntry.setButExt(jsonSerieEntry.getInt("butExt"));
        		statMatchSerieEntry.setNomClubDom(jsonSerieEntry.getString("clubDom"));
	        	statMatchSerieEntry.setNomClubExt(jsonSerieEntry.getString("clubExt"));	        	
	        	statMatchSerieEntries.add(statMatchSerieEntry);
	        }

		} catch (JSONException e) {
            e.printStackTrace();
        }

		return statMatchSerieEntries;
	}

	
	// Cote d'un match
	private List<CoteMatchEntry> getCoteMatch(int idMatch) {
		List<CoteMatchEntry> coteMatchEntries = new ArrayList<CoteMatchEntry>();

		String strCoteMatch = RestClient.get(new QueryBuilder(statMatchActivity.getAssets(), "/rest/coteMatch/" + String.valueOf(idMatch) + "/").getUri());

		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strCoteMatch);

	        // A Simple JSONObject Parsing
	        JSONArray coteMatchArray = json.getJSONArray("coteMatch");
	        for(int i = 0; i < coteMatchArray.length(); i++) {
	        	JSONObject jsonCoteMatchEntry = coteMatchArray.getJSONObject(i);

	        	CoteMatchEntry coteMatchEntry = new CoteMatchEntry();	        	
	        	coteMatchEntry.setTypeMatch(jsonCoteMatchEntry.getString("type"));
	        	coteMatchEntry.setCote(jsonCoteMatchEntry.getInt("cote"));
	        	coteMatchEntry.setUrlLogo(jsonCoteMatchEntry.getString("url_logo"));
	        	
	        	coteMatchEntries.add(coteMatchEntry);
	        }

		} catch (JSONException e) {
            e.printStackTrace();
        }

		return coteMatchEntries;
	}

	
	// Information d'un club
	private ClassementClubEntry getInfoClub(String nomClub) {
		
		ClassementClubEntry infoClubEntry = new ClassementClubEntry(); 

		String strInfoClub = RestClient.get(new QueryBuilder(statMatchActivity.getAssets(), "/rest/infoClub/" + nomClub + "/").getUri());

		try {
			// A Simple JSONObject Creation
	        JSONObject json = new JSONObject(strInfoClub);

	        // A Simple JSONObject Parsing
	        JSONArray matchSerieArray = json.getJSONArray("infoClub");
	        for(int i=0;i<matchSerieArray.length();i++) {
	        	JSONObject jsonInfoClubEntry = matchSerieArray.getJSONObject(i);

	        	infoClubEntry.setClub(jsonInfoClubEntry.getString("club"));
	        	infoClubEntry.setPlace(jsonInfoClubEntry.getInt("place"));
	        	infoClubEntry.setPoints(jsonInfoClubEntry.getInt("points"));
	        	infoClubEntry.setMatchJoue(jsonInfoClubEntry.getInt("j"));
	        	infoClubEntry.setMatchGagne(jsonInfoClubEntry.getInt("g"));
	        	infoClubEntry.setMatchNul(jsonInfoClubEntry.getInt("n"));
	        	infoClubEntry.setMatchPerdu(jsonInfoClubEntry.getInt("p"));
	        	infoClubEntry.setButsPour(jsonInfoClubEntry.getInt("bp"));
	        	infoClubEntry.setButsContre(jsonInfoClubEntry.getInt("bc"));
	        	infoClubEntry.setDiff(jsonInfoClubEntry.getInt("diff"));
	        	infoClubEntry.setMatchGagneDom(jsonInfoClubEntry.getInt("domg"));
	        	infoClubEntry.setMatchNulDom(jsonInfoClubEntry.getInt("domn"));
	        	infoClubEntry.setMatchPerduDom(jsonInfoClubEntry.getInt("domp"));
	        	infoClubEntry.setMatchGagneExt(jsonInfoClubEntry.getInt("extg"));
	        	infoClubEntry.setMatchNulExt(jsonInfoClubEntry.getInt("extn"));
	        	infoClubEntry.setMatchPerduExt(jsonInfoClubEntry.getInt("extp"));
	        	infoClubEntry.setUrlLogo(jsonInfoClubEntry.getString("url_logo"));
	        }

		} catch (JSONException e) {
            e.printStackTrace();
        }

		return infoClubEntry;
	}
	
	
	// Derniers matchs effectu�s par les 2 clubs
    private class StatMatchTask extends AsyncTask<String, Void, Boolean> {

        private List<StatMatchSerieEntry> statMatchSerieEntriesDom;
        private List<StatMatchSerieEntry> statMatchSerieEntriesExt;
        private ProgressDialog dialog;

        private StatMatchTask() {
            dialog = new ProgressDialog(statMatchActivity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {

            statMatchSerieEntriesDom = getMatchSerie(statMatchActivity.getNomClubDomicile());
            statMatchSerieEntriesExt = getMatchSerie(statMatchActivity.getNomClubExterieur());

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            
        	LinearLayout statMatchDerniersMatchLayout = (LinearLayout) statMatchActivity.findViewById(R.id.statMatchDerniersMatchLayout);
        	ListView statMatchSerieListViewDom = (ListView) statMatchActivity.findViewById(R.id.statMatchSerieListDom);;
        	TextView messageStatMatchSerieTextView = (TextView) statMatchActivity.findViewById(R.id.statMatchSerieMessage);
        	ListView statMatchSerieListViewExt = (ListView) statMatchActivity.findViewById(R.id.statMatchSerieListExt);
        	
        	statMatchDerniersMatchLayout.setVisibility(View.VISIBLE);
        	
            if(statMatchSerieEntriesDom.size() > 0) {
                StatMatchSerieAdapter adapter = new StatMatchSerieAdapter(statMatchActivity, R.layout.stat_match_serie_item, statMatchSerieEntriesDom);
                statMatchSerieListViewDom.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                messageStatMatchSerieTextView.setVisibility(View.GONE);
                statMatchSerieListViewDom.setVisibility(View.VISIBLE);
            }
            
            if(statMatchSerieEntriesExt.size() > 0) {
                StatMatchSerieAdapter adapter = new StatMatchSerieAdapter(statMatchActivity, R.layout.stat_match_serie_item, statMatchSerieEntriesExt);
                statMatchSerieListViewExt.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                messageStatMatchSerieTextView.setVisibility(View.GONE);
                statMatchSerieListViewExt.setVisibility(View.VISIBLE);
            }
            
            if(statMatchSerieEntriesDom.size() == 0 && statMatchSerieEntriesExt.size() == 0) {
                statMatchSerieListViewDom.setVisibility(View.GONE);
                statMatchSerieListViewExt.setVisibility(View.GONE);
                messageStatMatchSerieTextView.setText("S�rie non disponible");
                messageStatMatchSerieTextView.setVisibility(View.VISIBLE);
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }	
	
	
	// Derniers matchs effectu�s par les 2 clubs
    private class ConfrontationTask extends AsyncTask<String, Void, Boolean> {

        private List<StatMatchSerieEntry> statMatchSerieEntries;
        private ProgressDialog dialog;
     
        private ConfrontationTask() {
            dialog = new ProgressDialog(statMatchActivity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {
            statMatchSerieEntries = getConfrontation(statMatchActivity.getNomClubDomicile(), statMatchActivity.getNomClubExterieur());
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        	double nbMatchJoue = statMatchSerieEntries.size();
        	double nbMatchG = 0, nbMatchN = 0, nbMatchP = 0;
        	String titreCompl;

        	LinearLayout statMatchConfrontationLayout = (LinearLayout) statMatchActivity.findViewById(R.id.statMatchConfrontationLayout);
        	TextView statMatchConfrontationTitreTextView = (TextView) statMatchActivity.findViewById(R.id.statMatchConfrontationTitre);	
        	TextView statMatchConfrontation1TextView = (TextView) statMatchActivity.findViewById(R.id.statMatchConfrontation1);
        	TextView statMatchConfrontationNTextView = (TextView) statMatchActivity.findViewById(R.id.statMatchConfrontationN);
        	TextView statMatchConfrontation2TextView = (TextView) statMatchActivity.findViewById(R.id.statMatchConfrontation2);
        	TextView statMatchConfrontationMessageTextView = (TextView) statMatchActivity.findViewById(R.id.statMatchConfrontationMessage);	

        	android.view.Display display = ((android.view.WindowManager)statMatchActivity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        	int intLargeur = display.getWidth() - 10;        	
        	
        	statMatchConfrontationLayout.setVisibility(View.VISIBLE);
        	statMatchConfrontation1TextView.setBackgroundColor(Color.GREEN);
        	statMatchConfrontation1TextView.setTextColor(Color.BLACK);
        	statMatchConfrontationNTextView.setBackgroundColor(Color.YELLOW);
        	statMatchConfrontationNTextView.setTextColor(Color.BLACK);
        	statMatchConfrontation2TextView.setBackgroundColor(Color.RED);
        	statMatchConfrontation2TextView.setTextColor(Color.BLACK);
        	
            if(nbMatchJoue > 0) {
            	for (StatMatchSerieEntry statMatchSerie : statMatchSerieEntries) {
            		if (statMatchSerie.getButDom() == statMatchSerie.getButExt()) {
            			nbMatchN += 1;
            		} else if ((statMatchSerie.getButDom() > statMatchSerie.getButExt() && statMatchSerie.getNomClubDom().compareTo(statMatchActivity.getNomClubDomicile()) == 0)
            				|| (statMatchSerie.getButExt() > statMatchSerie.getButDom() && statMatchSerie.getNomClubExt().compareTo(statMatchActivity.getNomClubDomicile()) == 0)) {
            			nbMatchG += 1;
            		} else {
            			nbMatchP += 1;
            		}
            	}
            	statMatchConfrontation1TextView.setWidth((int) ((nbMatchG / nbMatchJoue) * intLargeur));
            	statMatchConfrontationNTextView.setWidth((int) ((nbMatchN / nbMatchJoue) * intLargeur));
            	statMatchConfrontation2TextView.setWidth((int) ((nbMatchP / nbMatchJoue) * intLargeur));
            	if (nbMatchJoue > 1) {
            		titreCompl = "s)";
            	} else {
            		titreCompl = ")";
            	}
            	statMatchConfrontationTitreTextView.setText("Confrontations (" + String.valueOf((int) nbMatchJoue) + " match" + titreCompl);
            } else { 
            	statMatchConfrontation1TextView.setVisibility(View.GONE);
            	statMatchConfrontationNTextView.setVisibility(View.GONE);
            	statMatchConfrontation2TextView.setVisibility(View.GONE);
                statMatchConfrontationMessageTextView.setText("Historique non disponible");
                statMatchConfrontationMessageTextView.setVisibility(View.VISIBLE);
            }
        
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }	
	
	
	// affiche la forme domicile/ext�rieur sur la saison pour les 2 clubs
    private void afficherForme(ClassementClubEntry infoClubEntryDom, ClassementClubEntry infoClubEntryExt) {

    	TextView statMatchFormeDom1TextView = (TextView) statMatchActivity.findViewById(R.id.statMatchFormeDom1);
    	TextView statMatchFormeDomNTextView = (TextView) statMatchActivity.findViewById(R.id.statMatchFormeDomN);
    	TextView statMatchFormeDom2TextView = (TextView) statMatchActivity.findViewById(R.id.statMatchFormeDom2);
    	TextView statMatchFormeExt1TextView = (TextView) statMatchActivity.findViewById(R.id.statMatchFormeExt1);
    	TextView statMatchFormeExtNTextView = (TextView) statMatchActivity.findViewById(R.id.statMatchFormeExtN);
    	TextView statMatchFormeExt2TextView = (TextView) statMatchActivity.findViewById(R.id.statMatchFormeExt2);
    	LinearLayout statMatchFormeLayout = (LinearLayout) statMatchActivity.findViewById(R.id.statMatchFormeLayout);
    	
    	android.view.Display display = ((android.view.WindowManager)statMatchActivity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();    	
    	// Moiti� de la largeur de l'�cran - 2x(largeur min d'une valeur forme) 
    	int intLargeur = (display.getWidth() / 2) - 10;
    	double intMatchJoue;
    	
    	statMatchFormeLayout.setVisibility(View.VISIBLE);
    	statMatchFormeDom1TextView.setBackgroundColor(Color.GREEN);
    	statMatchFormeDom1TextView.setTextColor(Color.BLACK);
    	statMatchFormeDomNTextView.setBackgroundColor(Color.YELLOW);
    	statMatchFormeDomNTextView.setTextColor(Color.BLACK);
    	statMatchFormeDom2TextView.setBackgroundColor(Color.RED);
    	statMatchFormeDom2TextView.setTextColor(Color.BLACK);
    	statMatchFormeExt1TextView.setBackgroundColor(Color.GREEN);
    	statMatchFormeExt1TextView.setTextColor(Color.BLACK);
    	statMatchFormeExtNTextView.setBackgroundColor(Color.YELLOW);
    	statMatchFormeExtNTextView.setTextColor(Color.BLACK);
    	statMatchFormeExt2TextView.setBackgroundColor(Color.RED);
    	statMatchFormeExt2TextView.setTextColor(Color.BLACK);
    	
    	intMatchJoue = infoClubEntryDom.getMatchGagneDom() + infoClubEntryDom.getMatchNulDom() + infoClubEntryDom.getMatchPerduDom();
    	statMatchFormeDom1TextView.setWidth((int) (((double) infoClubEntryDom.getMatchGagneDom() / intMatchJoue) * intLargeur));
    	statMatchFormeDomNTextView.setWidth((int) (((double) infoClubEntryDom.getMatchNulDom() / intMatchJoue) * intLargeur));
    	statMatchFormeDom2TextView.setWidth((int) (((double) infoClubEntryDom.getMatchPerduDom() / intMatchJoue) * intLargeur));

    	intMatchJoue = infoClubEntryExt.getMatchGagneExt() + infoClubEntryExt.getMatchNulExt() + infoClubEntryExt.getMatchPerduExt();
    	statMatchFormeExt1TextView.setWidth((int) (((double)infoClubEntryExt.getMatchGagneExt() / intMatchJoue) * intLargeur));
    	statMatchFormeExtNTextView.setWidth((int) (((double) infoClubEntryExt.getMatchNulExt() / intMatchJoue) * intLargeur));
    	statMatchFormeExt2TextView.setWidth((int) (((double) infoClubEntryExt.getMatchPerduExt() / intMatchJoue) * intLargeur));
    }	
	
	
	// affiche le classement d�taill� des 2 clubs
    private void afficherClassementDetail(ClassementClubEntry infoClubEntryDom, ClassementClubEntry infoClubEntryExt) {
        List<StatMatchClassementEntry> StatMatchClassementEntries = new ArrayList<StatMatchClassementEntry>();
        StatMatchClassementEntry statMatchClassementEntry;

        LinearLayout statMatchClassementLayout = (LinearLayout) statMatchActivity.findViewById(R.id.statMatchClassementLayout);
        ListView statMatchClassementListView = (ListView) statMatchActivity.findViewById(R.id.statMatchClassementList);
        
    	statMatchClassementLayout.setVisibility(View.VISIBLE);
    	
    	statMatchClassementEntry = new StatMatchClassementEntry();
    	statMatchClassementEntry.setTypeInfo("Pts");
    	statMatchClassementEntry.setInfoDom(String.valueOf(infoClubEntryDom.getPoints()));
    	statMatchClassementEntry.setInfoExt(String.valueOf(infoClubEntryExt.getPoints()));
    	StatMatchClassementEntries.add(statMatchClassementEntry);

    	statMatchClassementEntry = new StatMatchClassementEntry();
    	statMatchClassementEntry.setTypeInfo("J");
    	statMatchClassementEntry.setInfoDom(String.valueOf(infoClubEntryDom.getMatchJoue()));
    	statMatchClassementEntry.setInfoExt(String.valueOf(infoClubEntryExt.getMatchJoue()));
    	StatMatchClassementEntries.add(statMatchClassementEntry);

    	statMatchClassementEntry = new StatMatchClassementEntry();
    	statMatchClassementEntry.setTypeInfo("G");
    	statMatchClassementEntry.setInfoDom(String.valueOf(infoClubEntryDom.getMatchGagne()));
    	statMatchClassementEntry.setInfoExt(String.valueOf(infoClubEntryExt.getMatchGagne()));
    	StatMatchClassementEntries.add(statMatchClassementEntry);

    	statMatchClassementEntry = new StatMatchClassementEntry();
    	statMatchClassementEntry.setTypeInfo("N");
    	statMatchClassementEntry.setInfoDom(String.valueOf(infoClubEntryDom.getMatchNul()));
    	statMatchClassementEntry.setInfoExt(String.valueOf(infoClubEntryExt.getMatchNul()));
    	StatMatchClassementEntries.add(statMatchClassementEntry);

    	statMatchClassementEntry = new StatMatchClassementEntry();
    	statMatchClassementEntry.setTypeInfo("P");
    	statMatchClassementEntry.setInfoDom(String.valueOf(infoClubEntryDom.getMatchPerdu()));
    	statMatchClassementEntry.setInfoExt(String.valueOf(infoClubEntryExt.getMatchPerdu()));
    	StatMatchClassementEntries.add(statMatchClassementEntry);

    	statMatchClassementEntry = new StatMatchClassementEntry();
    	statMatchClassementEntry.setTypeInfo("BP");
    	statMatchClassementEntry.setInfoDom(String.valueOf(infoClubEntryDom.getButsPour()));
    	statMatchClassementEntry.setInfoExt(String.valueOf(infoClubEntryExt.getButsPour()));
    	StatMatchClassementEntries.add(statMatchClassementEntry);

    	statMatchClassementEntry = new StatMatchClassementEntry();
    	statMatchClassementEntry.setTypeInfo("BC");
    	statMatchClassementEntry.setInfoDom(String.valueOf(infoClubEntryDom.getButsContre()));
    	statMatchClassementEntry.setInfoExt(String.valueOf(infoClubEntryExt.getButsContre()));
    	StatMatchClassementEntries.add(statMatchClassementEntry);
    	
		StatMatchClassementAdapter adapter = new StatMatchClassementAdapter(statMatchActivity, R.layout.stat_match_classement_item, StatMatchClassementEntries);
	    statMatchClassementListView.setAdapter(adapter);
	    adapter.notifyDataSetChanged();

    }	
    
    
	// Cotes du match
    private class CoteMatchTask extends AsyncTask<String, Void, Boolean> {

        private List<CoteMatchEntry> coteMatchEntries;
        private ProgressDialog dialog;

        private CoteMatchTask() {
            dialog = new ProgressDialog(statMatchActivity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {

            coteMatchEntries = getCoteMatch(Integer.parseInt(statMatchActivity.getIdMatch()));

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        	
        	TextView statMatchCoteDomTextView = (TextView) statMatchActivity.findViewById(R.id.statMatchCoteDom);
        	TextView statMatchCoteNulTextView = (TextView) statMatchActivity.findViewById(R.id.statMatchCoteNul);
        	TextView statMatchCoteExtTextView = (TextView) statMatchActivity.findViewById(R.id.statMatchCoteExt);
        	LinearLayout statMatchCoteLayout = (LinearLayout) statMatchActivity.findViewById(R.id.statMatchCoteLayout);
        	
        	int tabcote[] = new int [3];
        	
	        for(CoteMatchEntry coteMatchEntry : coteMatchEntries) {
	        	if (coteMatchEntry.getTypeMatch().compareTo("1") == 0) {
                	statMatchCoteDomTextView.setText(String.valueOf(coteMatchEntry.getCote()));
                	tabcote[0] = coteMatchEntry.getCote();
	        	} else if (coteMatchEntry.getTypeMatch().compareTo("N") == 0) {
                	statMatchCoteNulTextView.setText(String.valueOf(coteMatchEntry.getCote()));
                	tabcote[1] = coteMatchEntry.getCote();
	        	} else if (coteMatchEntry.getTypeMatch().compareTo("2") == 0) {
                	statMatchCoteExtTextView.setText(String.valueOf(coteMatchEntry.getCote()));
                	tabcote[2] = coteMatchEntry.getCote();
	        	}
	        }
	        
	        // Affectations des couleurs de fond pour les cotes
	        if (tabcote[0] >= tabcote[1] && tabcote[0] >= tabcote[2]) {
	        	statMatchCoteDomTextView.setBackgroundColor(Color.RED);
	        }
	        if (tabcote[1] >= tabcote[0] && tabcote[1] >= tabcote[2]) {
	        	statMatchCoteNulTextView.setBackgroundColor(Color.RED);
	        }
	        if (tabcote[2] >= tabcote[0] && tabcote[2] >= tabcote[1]) {
	        	statMatchCoteExtTextView.setBackgroundColor(Color.RED);
	        }

	        if (tabcote[0] <= tabcote[1] && tabcote[0] <= tabcote[2]) {
	        	statMatchCoteDomTextView.setBackgroundColor(Color.GREEN);
	        	statMatchCoteDomTextView.setTextColor(Color.BLACK);
	        } 
	        if (tabcote[1] <= tabcote[0] && tabcote[1] <= tabcote[2]) {
	        	statMatchCoteNulTextView.setBackgroundColor(Color.GREEN);
	        	statMatchCoteNulTextView.setTextColor(Color.BLACK);
	        } 
	        if (tabcote[2] <= tabcote[0] && tabcote[2] <= tabcote[1]) {
	        	statMatchCoteExtTextView.setBackgroundColor(Color.GREEN);
	        	statMatchCoteExtTextView.setTextColor(Color.BLACK);
	        }
	        
	        statMatchCoteLayout.setVisibility(View.VISIBLE);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }	
    
    

    
    // Informations des 2 clubs
    private class InfoClubTask extends AsyncTask<String, Void, Boolean> {

        private ClassementClubEntry infoClubEntryDom;
        private ClassementClubEntry infoClubEntryExt;
        private ProgressDialog dialog;

        private InfoClubTask() {
            dialog = new ProgressDialog(statMatchActivity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Chargement");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {

            infoClubEntryDom = getInfoClub(statMatchActivity.getNomClubDomicile());
            infoClubEntryExt = getInfoClub(statMatchActivity.getNomClubExterieur());

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        	
        	AsyncImageView statMatchLogoDomAsyncImageView = (AsyncImageView) statMatchActivity.findViewById(R.id.statMatchLogoDom);
        	TextView statMatchEquipeDomTextView = (TextView) statMatchActivity.findViewById(R.id.statMatchEquipeDom);
        	TextView statMatchPlaceDomTextView = (TextView) statMatchActivity.findViewById(R.id.statMatchPlaceDom);
            AsyncImageView statMatchLogoExtAsyncImageView = (AsyncImageView) statMatchActivity.findViewById(R.id.statMatchLogoExt);
            TextView statMatchEquipeExtTextView = (TextView) statMatchActivity.findViewById(R.id.statMatchEquipeExt);
            TextView statMatchPlaceExtTextView = (TextView) statMatchActivity.findViewById(R.id.statMatchPlaceExt);
        	
        	statMatchLogoDomAsyncImageView.setUrl(infoClubEntryDom.getUrlLogo());
        	statMatchEquipeDomTextView.setText(infoClubEntryDom.getClub());
        	statMatchPlaceDomTextView.setText("(" + String.valueOf(infoClubEntryDom.getPlace()) + ")");
            
        	statMatchLogoExtAsyncImageView.setUrl(infoClubEntryExt.getUrlLogo());
        	statMatchEquipeExtTextView.setText(infoClubEntryExt.getClub());
        	statMatchPlaceExtTextView.setText("(" + String.valueOf(infoClubEntryExt.getPlace()) + ")");       	

        	if (currentPage == NUM_PAGE_COTE) {
        		afficherForme(infoClubEntryDom, infoClubEntryExt);
        	} else if (currentPage == NUM_PAGE_CLASSEMENT) {
        		afficherClassementDetail(infoClubEntryDom, infoClubEntryExt);
        	}
        	
        	if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(success);
        }
    }
}
