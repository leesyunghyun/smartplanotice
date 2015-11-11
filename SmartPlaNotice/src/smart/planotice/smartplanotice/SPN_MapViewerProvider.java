package smart.planotice.smartplanotice;

import smart.planotice.smartplanotice.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.ListView;

import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;

public class SPN_MapViewerProvider extends NMapResourceProvider {

	Drawable drawable;
	public SPN_MapViewerProvider(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Drawable getCalloutBackground(NMapOverlayItem arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Drawable[] getCalloutRightAccessory(NMapOverlayItem arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Drawable[] getCalloutRightButton(NMapOverlayItem arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCalloutRightButtonText(NMapOverlayItem arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getCalloutTextColors(NMapOverlayItem arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Drawable getDirectionArrow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Drawable[] getLocationDot() {
		// TODO Auto-generated method stub
		Drawable[] drawable = new Drawable[2];
	
		drawable[0] = mContext.getResources().getDrawable(R.drawable.mywhich);
		drawable[1] = mContext.getResources().getDrawable(R.drawable.mywhichclick);

		for (int i = 0; i < drawable.length; i++) {
			int w = drawable[i].getIntrinsicWidth() / 2;
			int h = drawable[i].getIntrinsicHeight() / 2;

			drawable[i].setBounds(-w, -h, w, h);
		}
		return drawable;
	}

	@Override
	public int getLayoutIdForOverlappedListView() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getListItemDividerId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getListItemImageViewId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getListItemLayoutIdForOverlappedListView() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getListItemTailTextViewId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getListItemTextViewId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOverlappedListViewId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getParentLayoutIdForOverlappedListView() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setOverlappedItemResource(NMapPOIitem arg0, ImageView arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOverlappedListViewLayout(ListView arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int findResourceIdForMarker(int arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected Drawable getDrawableForMarker(int arg0, boolean arg1,
			NMapOverlayItem arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setBounds(Drawable arg0, int arg1, NMapOverlayItem arg2) {
		// TODO 자동 생성된 메소드 스텁
		super.setBounds(arg0, arg1, arg2);
	}

}
