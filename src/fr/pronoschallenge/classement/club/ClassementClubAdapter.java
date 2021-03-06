package fr.pronoschallenge.classement.club;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.pronoschallenge.R;
import greendroid.widget.AsyncImageView;

public class ClassementClubAdapter extends ArrayAdapter<ClassementClubEntry> {

	public int intPlacePrecedent = 0;

	public ClassementClubAdapter(Context context, int textViewResourceId,
			List<ClassementClubEntry> objects) {
		super(context, textViewResourceId, objects);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        
        if (view == null)
        {
            LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.classement_club_item, null); 
        }
        ClassementClubEntry classementClubEntry = getItem(position);
        if (classementClubEntry != null)
        {
            
            TextView classementClubEntryClub = (TextView)view.findViewById(R.id.classementClubEntryClub);  
            classementClubEntryClub.setText(classementClubEntry.getClub());

        	if (classementClubEntry.getClub().compareTo("...") != 0) {
        	
        		if (position == 0) {
        			intPlacePrecedent = 0;
        		} else {
        			ClassementClubEntry classementClubEntryPrec = getItem(position - 1);
        			intPlacePrecedent = classementClubEntryPrec.getPlace();
        		}        		
        		
	            TextView classementClubEntryPlace = (TextView)view.findViewById(R.id.classementClubEntryPlace);
	            if (classementClubEntry.getPlace() == intPlacePrecedent) {
	            	classementClubEntryPlace.setText("");
	            } else {
	            	classementClubEntryPlace.setText(String.valueOf(classementClubEntry.getPlace()));
	            }
	
	            AsyncImageView classementClubEntryLogo = (AsyncImageView)view.findViewById(R.id.classementClubEntryLogo);
	            classementClubEntryLogo.setUrl(classementClubEntry.getUrlLogo());
	
	            TextView classementClubEntryPoints = (TextView)view.findViewById(R.id.classementClubEntryPoints);  
	            classementClubEntryPoints.setText(String.valueOf(classementClubEntry.getPoints()));
	
	            TextView classementClubEntryJoue = (TextView)view.findViewById(R.id.classementClubEntryJoue);  
	            classementClubEntryJoue.setText(String.valueOf(classementClubEntry.getMatchJoue()));
	
	            TextView classementClubEntryDiff = (TextView)view.findViewById(R.id.classementClubEntryDiff);  
	            classementClubEntryDiff.setText(String.valueOf(classementClubEntry.getDiff()));
        		
        	}

        }
        
     return view;

	}

}
