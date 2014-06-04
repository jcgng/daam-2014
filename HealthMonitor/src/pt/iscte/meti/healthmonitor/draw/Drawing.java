package pt.iscte.meti.healthmonitor.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;

public class Drawing {
	private ImageView imageView;
	public Drawing(ImageView imageView) {
		this.imageView= imageView; 
	}
	
	public void drawThermometer(float temp) {
		Bitmap bg = Bitmap.createBitmap(150,350, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bg);
		Paint paint = new Paint();
		
		/**
		 * How to draw a thermometer on your Android phone?
		 * http://www.phonesdevelopers.com/1750577/
		 *
		 * @author yuanbieli 2011-08-05 
		 */
        int y = 260 - (int) ((temp - 35) * 20);  
        paint.setColor(Color.WHITE);  
        canvas.drawRect(40, 50, 60, 280, paint);  
        Paint paintCircle = new Paint();
        paintCircle.setColor(Color.RED);  
        Paint paintLine = new Paint();  
        paintLine.setColor(Color.BLUE); 
        canvas.drawRect(40, y, 60, 280, paintCircle);  
        canvas.drawCircle(50, 300, 25, paintCircle);  
        int ydegree = 260;  
        int tempScale = 35;  
        while(ydegree > 55) {  
            canvas.drawLine(60, ydegree, 67, ydegree, paint);  
            if (ydegree % 20 == 0) {  
                canvas.drawLine(60, ydegree, 72, ydegree, paintLine);  
                canvas.drawText(tempScale + "", 70, ydegree + 4, paint);  
                tempScale++;  
            }  
            ydegree = ydegree - 2;  
        }
        /*** ***/
        
        this.imageView.setImageBitmap(bg);
	}
}
