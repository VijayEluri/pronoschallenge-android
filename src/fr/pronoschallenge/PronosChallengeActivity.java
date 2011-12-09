package fr.pronoschallenge;

import fr.pronoschallenge.classement.ClassementActivity;
import fr.pronoschallenge.classement.ClassementQuickAction;
import fr.pronoschallenge.util.AppUtil;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBar.Type;
import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class PronosChallengeActivity extends GDActivity {
	
	private QuickActionWidget classementQuickActionGrid;

    private List icons = new ArrayList();
	
	public PronosChallengeActivity() {
		super(Type.Normal);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setActionBarContentView(R.layout.main);

        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new HomeMenuItemsAdapter());

        icons.add(new HomeMenuItem(getString(R.string.button_pronos), BitmapFactory.decodeResource(getResources(), R.drawable.pronos)));
        icons.add(new HomeMenuItem(getString(R.string.button_classements), BitmapFactory.decodeResource(getResources(), R.drawable.classements)));
        icons.add(new HomeMenuItem(getString(R.string.button_gazouillis), BitmapFactory.decodeResource(getResources(), R.drawable.gazouillis)));
        icons.add(new HomeMenuItem(getString(R.string.button_options), BitmapFactory.decodeResource(getResources(), R.drawable.options)));
        icons.add(new HomeMenuItem(getString(R.string.button_mes_amis), BitmapFactory.decodeResource(getResources(), R.drawable.mes_amis)));
        icons.add(new HomeMenuItem(getString(R.string.button_classement_club), BitmapFactory.decodeResource(getResources(), R.drawable.ligue_1)));
        //icons.add(new HomeMenuItem(getString(R.string.button_stat), BitmapFactory.decodeResource(getResources(), R.drawable.ligue_1)));
		
        classementQuickActionGrid = new QuickActionGrid(this);
        classementQuickActionGrid.addQuickAction(new ClassementQuickAction(this, null, R.string.type_classement_general));
        classementQuickActionGrid.addQuickAction(new ClassementQuickAction(this, null, R.string.type_classement_hourra));
        classementQuickActionGrid.addQuickAction(new ClassementQuickAction(this, null, R.string.type_classement_mixte));

        classementQuickActionGrid.setOnQuickActionClickListener(mActionListener);		
	}

    public class HomeMenuItemsAdapter extends BaseAdapter {

        public int getCount() {
            return icons.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View view, ViewGroup viewGroup) {
            View menuItemView;

            HomeMenuItem homeMenuItem = (HomeMenuItem) icons.get(position);

            if(view == null) {
                LayoutInflater li = getLayoutInflater();
                menuItemView = li.inflate(R.layout.home_menu_item, null);
            } else {
                menuItemView = view;
            }

            ImageView imageView = (ImageView) menuItemView.findViewById(R.id.icon_image);
            imageView.setImageBitmap(homeMenuItem.getImage());
            TextView textView = (TextView) menuItemView.findViewById(R.id.icon_text);
            textView.setText(homeMenuItem.getName());

            if(homeMenuItem.getName().equals(getString(R.string.button_classements))) {
                menuItemView.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            classementQuickActionGrid.show(v);
                        }
                    });
            } else if(homeMenuItem.getName().equals(getString(R.string.button_pronos))) {
                menuItemView.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent pronosIntent = new Intent();
                            pronosIntent.setClassName("fr.pronoschallenge", "fr.pronoschallenge.PronosActivity");
                            startActivity(pronosIntent);
                        }
                    });
            } else if(homeMenuItem.getName().equals(getString(R.string.button_gazouillis))) {
                menuItemView.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent gazouillisIntent = new Intent();
                            gazouillisIntent.setClassName("fr.pronoschallenge", "fr.pronoschallenge.gazouillis.GazouillisActivity");
                            startActivity(gazouillisIntent);
                        }
                    });
            } else if(homeMenuItem.getName().equals(getString(R.string.button_options))) {
                menuItemView.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent optionsIntent = new Intent();
                            optionsIntent.setClassName("fr.pronoschallenge", "fr.pronoschallenge.options.OptionsActivity");
                            startActivity(optionsIntent);
                        }
                    });
                
            } else if(homeMenuItem.getName().equals(getString(R.string.button_mes_amis))) {
                menuItemView.setOnClickListener(
                        new View.OnClickListener() {
                            public void onClick(View v) {
                                Intent amisIntent = new Intent();
                                amisIntent.setClassName("fr.pronoschallenge", "fr.pronoschallenge.amis.AmisActivity");
                                startActivity(amisIntent);
                            }
                        }); 
            } else if(homeMenuItem.getName().equals(getString(R.string.button_classement_club))) {
                menuItemView.setOnClickListener(
                        new View.OnClickListener() {
                            public void onClick(View v) {
                                Intent amisIntent = new Intent();
                                amisIntent.setClassName("fr.pronoschallenge", "fr.pronoschallenge.classement.club.ClassementClubActivity");
                                startActivity(amisIntent);
                            }
                        }); 
            } else if(homeMenuItem.getName().equals(getString(R.string.button_stat))) {
                menuItemView.setOnClickListener(
                        new View.OnClickListener() {
                            public void onClick(View v) {
            					Bundle objetbunble = new Bundle();
            					objetbunble.putString("clubDomicile", "Marseille");
            					objetbunble.putString("clubExterieur", "Caen");
                                Intent statIntent = new Intent();
                                statIntent.putExtras(objetbunble);
                                statIntent.setClassName("fr.pronoschallenge", "fr.pronoschallenge.stat.match.StatMatchActivity");
                                startActivity(statIntent);
                            }
                        }); 
            }
            return menuItemView;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem accountMenu = menu.add(Menu.NONE, 1, Menu.NONE, "Compte");
        accountMenu.setIcon(AppUtil.resizeImage(this, R.drawable.account_menu_icon, 32, 32));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case 1 :
                Intent loginIntent = new Intent();
                loginIntent.setClassName("fr.pronoschallenge", "fr.pronoschallenge.auth.LoginActivity");
                startActivity(loginIntent);

            default :
        }


        return super.onOptionsItemSelected(item);
    }

    private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
        public void onQuickActionClicked(QuickActionWidget widget, int position) {
    		final CharSequence[] classementItems = {getString(R.string.type_classement_general), getString(R.string.type_classement_hourra), getString(R.string.type_classement_mixte)};
    		final String[] classementTypes = {ClassementActivity.CLASSEMENT_TYPE_GENERAL, ClassementActivity.CLASSEMENT_TYPE_HOURRA, ClassementActivity.CLASSEMENT_TYPE_MIXTE};
            Intent classementIntent = new Intent();
	    	classementIntent.setClassName("fr.pronoschallenge", "fr.pronoschallenge.ClassementActivity");
	    	classementIntent.putExtra("fr.pronoschallenge.ClassementType", classementTypes[position]);
	    	classementIntent.putExtra("fr.pronoschallenge.ClassementTitle", classementItems[position]);
	        startActivity(classementIntent);
        }
    };
}