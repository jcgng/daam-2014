package pt.iscte.meti.healthmonitor.listeners;

import pt.iscte.meti.healthmonitor.MainActivity;
import pt.iscte.meti.healthmonitor.MonitorActivity;
import pt.iscte.meti.healthmonitor.R;
import pt.iscte.meti.healthmonitor.models.PatientData;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class SwipeListListener implements View.OnTouchListener {
    private static final int MIN_DISTANCE = 100;
    private float downX, downY, upX, upY;

    private MainActivity mainActivity;
    private View selected = null;
    private int position;
    
    public SwipeListListener(Activity mainActivity) {
    	this.mainActivity = (MainActivity) mainActivity; 
    }
    
    /**
     * Based on SwipeListView 
     * @author Jake Wharton
     * 
     * @param downX
     * @param downY
     * @return
     */
    private void findDownChild(float downX, float downY) {
        Rect rect = new Rect();
        int childCount = mainActivity.getPatientListView().getChildCount();
        int[] listViewCoords = new int[2];
        mainActivity.getPatientListView().getLocationOnScreen(listViewCoords);
        int x = Math.round(downX) - listViewCoords[0];
        int y = Math.round(downY) - listViewCoords[1];
        View child;
        for (int i = 0; i < childCount; i++) {
            child = mainActivity.getPatientListView().getChildAt(i);
            child.getHitRect(rect);
            if(rect.contains(x, y)) {
            	position = i + mainActivity.getPatientListView().getFirstVisiblePosition();
            	selected = child; // This is your down view
            }
        }
    }
    /******/
    
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
	        case MotionEvent.ACTION_DOWN:
	            downX = event.getRawX();
	            downY = event.getRawY();
	            // get selected child
	            findDownChild(downX,downY);
	            return false; // allow other events like Click to be processed
	        case MotionEvent.ACTION_MOVE:
	            upX = event.getRawX();
	            upY = event.getRawY();
	
	            float deltaX = downX - upX;
	            float deltaY = downY - upY;
	
	            // horizontal swipe detection
	            if (Math.abs(deltaX) > MIN_DISTANCE) {
	                // left or right
	                if (deltaX < 0) {
	                	if(selected!=null) {
	                		selected.setPadding(-1*Math.round(deltaX),0,0,0);
	                		// change text
	                		TextView textView = (TextView)selected.findViewById(R.id.patientName);
	                		textView.setText("Monitor");
	                		// get patient
	                		PatientData patient = mainActivity.getPatientListData().get(position);
	                		if((-1*deltaX) >= (0.8*selected.getWidth())) {
	            		    	Intent intent = new Intent(selected.getContext(), MonitorActivity.class);
	            		    	intent.putExtra("idPatients", patient.getId());
	            		    	intent.putExtra("name", patient.getName());
	            		    	intent.putExtra("bedNumber", patient.getBed());
	            		    	downX = upX;
	            		    	downY = upY;
	                          	mainActivity.startActivity(intent);
	                			return true;
	                		}
	                	}
	                    return true;
	                }
	                // right to left
	                if (deltaX > 0) {
	                    return true;
	                }
	            } else { 
	                // vertical swipe detection
	                if (Math.abs(deltaY) > MIN_DISTANCE) {
	                    // top or down
	                    if (deltaY < 0) {
	                        return false;
	                    }
	                    if (deltaY > 0) {
	                        return false;
	                    }
	                } 
	            }
	            return true;
	        case MotionEvent.ACTION_CANCEL:
	        case MotionEvent.ACTION_UP:
	        	if(selected!=null) {
	        		// reset position
	        		selected.setPadding(0,0,0,0);
		        	// reset text
	        		TextView textView = (TextView)selected.findViewById(R.id.patientName);
	        		PatientData patient = mainActivity.getPatientListData().get(position);
	        		textView.setText(patient.getName() + "\nBed: " + patient.getBed());
	        	}
	        	// reset child
	        	selected = null;
	        	return false;
        }
        return false;
    }
}



