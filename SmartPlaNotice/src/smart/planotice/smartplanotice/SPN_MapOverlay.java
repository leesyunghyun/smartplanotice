package smart.planotice.smartplanotice;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;

import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.overlay.NMapPathData;

public class SPN_MapOverlay extends NMapOverlay{

	ArrayList<Vertex> arVertex;
	ArrayList<Vertex2> arVertex2;
	
	Paint mPaintRed;
	Paint mPaintYellow;
	Paint mPaintBlue;
	Paint mPaintGreen;
	Paint mPaintWhite;

	NMapPathData pathData;
	
	float x;
	float y;
	boolean bDraw;

	int flag = 0;
	int[] pathDataArray;
	
	public SPN_MapOverlay() {
		arVertex = new ArrayList<SPN_MapOverlay.Vertex>();
		arVertex2 = new ArrayList<SPN_MapOverlay.Vertex2>();
		
		mPaintRed = new Paint();
		mPaintRed.setColor(Color.RED);
		mPaintRed.setStrokeWidth(5);// 두께
		mPaintRed.setAntiAlias(true);

		mPaintBlue = new Paint();
		mPaintBlue.setColor(Color.BLUE);
		mPaintBlue.setStrokeWidth(5);// 두께
		mPaintBlue.setAntiAlias(true);

		mPaintGreen = new Paint();
		mPaintGreen.setColor(Color.GREEN);
		mPaintGreen.setStrokeWidth(5);// 두께
		mPaintGreen.setAntiAlias(true);

		mPaintYellow = new Paint();
		mPaintYellow.setColor(Color.YELLOW);
		mPaintYellow.setStrokeWidth(5);// 두께
		mPaintYellow.setAntiAlias(true);

		mPaintWhite = new Paint();
		mPaintWhite.setAlpha(0);
		mPaintWhite.setStrokeWidth(30);// 두께
		mPaintWhite.setAntiAlias(true);

		mPaintWhite.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
	}
	
	@Override
	public boolean draw(Canvas arg0, NMapView arg1, boolean arg2, long arg3) {
		// TODO 자동 생성된 메소드 스텁
		for (int i = 0; i < arVertex.size(); i++) {
			if (arVertex.get(i).bDraw) {
				arg0.drawLine(arVertex.get(i - 1).x, arVertex.get(i - 1).y,
						arVertex.get(i).x, arVertex.get(i).y,
						arVertex.get(i).mpaint);
			} else {
				arg0.drawPoint(arVertex.get(i).x, arVertex.get(i).y,
						arVertex.get(i).mpaint);
			}
		}
		
		return true;
	}

	public int GetPathDataSize()
	{
		return arVertex.size();
	}
	
	public void CancelDraw(int index)
	{
		arVertex.remove(index);
		arVertex2.remove(index);
	}
	
	public ArrayList<Vertex2> GetPathDataArray()
	{
		return arVertex2;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0, NMapView arg1) {
		
		NGeoPoint gp = arg1.getMapProjection().fromPixels((int) arg0.getX(),
				(int) arg0.getY());
		
		if (arg0.getAction() == MotionEvent.ACTION_DOWN) {
			switch (flag) {
			case 0:
				arVertex.add(new Vertex(arg0.getX(), arg0.getY(), false,
						mPaintRed));
				arVertex2.add(new Vertex2(gp.longitude,gp.latitude,mPaintRed));
				break;
			case 1:
				arVertex.add(new Vertex(arg0.getX(), arg0.getY(), false,
						mPaintBlue));
				arVertex2.add(new Vertex2(gp.longitude,gp.latitude,mPaintBlue));
				break;
			case 2:
				arVertex.add(new Vertex(arg0.getX(), arg0.getY(), false,
						mPaintYellow));
				arVertex2.add(new Vertex2(gp.longitude,gp.latitude,mPaintYellow));
				break;
			case 3:
				arVertex.add(new Vertex(arg0.getX(), arg0.getY(), false,
						mPaintGreen));
				arVertex2.add(new Vertex2(gp.longitude,gp.latitude,mPaintGreen));
				break;
			case 4:
				arVertex.add(new Vertex(arg0.getX(), arg0.getY(), false,
						mPaintWhite));
				arVertex2.add(new Vertex2(gp.longitude,gp.latitude,mPaintWhite));
				break;
			}
			
			return true;
		}

		if (arg0.getAction() == MotionEvent.ACTION_MOVE) {
			switch (flag) {
			case 0:
				arVertex.add(new Vertex(arg0.getX(), arg0.getY(), true,
						mPaintRed));
				arVertex2.add(new Vertex2(gp.longitude,gp.latitude,mPaintRed));
				break;
			case 1:
				arVertex.add(new Vertex(arg0.getX(), arg0.getY(), true,
						mPaintBlue));
				arVertex2.add(new Vertex2(gp.longitude,gp.latitude,mPaintBlue));
				break;
			case 2:
				arVertex.add(new Vertex(arg0.getX(), arg0.getY(), true,
						mPaintYellow));
				arVertex2.add(new Vertex2(gp.longitude,gp.latitude,mPaintYellow));
				break;
			case 3:
				arVertex.add(new Vertex(arg0.getX(), arg0.getY(), true,
						mPaintGreen));
				arVertex2.add(new Vertex2(gp.longitude,gp.latitude,mPaintGreen));
				break;
			case 4:
				arVertex.add(new Vertex(arg0.getX(), arg0.getY(), true,
						mPaintWhite));
				arVertex2.add(new Vertex2(gp.longitude,gp.latitude,mPaintWhite));
				break;
			}
			return true;
		}
		return false;
	}

	public void ChageColor(int color) {
		if (color == Color.RED) {
			flag = 0;
		} else if (color == Color.BLUE) {
			flag = 1;
		} else if (color == Color.YELLOW) {
			flag = 2;
		} else if (color == Color.GREEN) {
			flag = 3;
		} else if (color == Color.WHITE) {
			flag = 4;
		}
	}

	public void SetData(float fx, float fy, boolean fb, double ax, double ay, Paint adPaint)
	{
		arVertex.add(new Vertex(fx, fy, fb, adPaint));
		arVertex2.add(new Vertex2(ax, ay, adPaint));
	}
	
	class Vertex {
		float x;
		float y;
		boolean bDraw;
		Paint mpaint;

		public Vertex(float ax, float ay, boolean ad, Paint adPaint) {
			x = ax;
			y = ay;
			bDraw = ad;
			mpaint = adPaint;
		}
	}

	class Vertex2 {
		double x;
		double y;
		Paint mpaint;

		public Vertex2(double ax, double ay,Paint adPaint) {
			x = ax;
			y = ay;
			mpaint = adPaint;
		}
	}
}
