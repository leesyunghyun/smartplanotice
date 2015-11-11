package smart.planotice.smartplanotice;


import smart.planotice.smartplanotice.R;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.NMapLocationManager.OnLocationChangeListener;
import com.nhn.android.maps.NMapView.OnMapStateChangeListener;
import com.nhn.android.maps.NMapView.OnMapViewTouchEventListener;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.maps.overlay.NMapPathData;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapPathDataOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager.OnCalloutOverlayListener;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay.OnStateChangeListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class SPN_ChildMain extends NMapActivity implements
		OnMapViewTouchEventListener, OnMapStateChangeListener {

	public static NMapLocationManager mMapLocationMy = null;
	LocationManager LocationM = null;
	NMapOverlayManager OverlayManager;
	Context context;
	NMapMyLocationOverlay MyLocation;
	SPN_MapViewerProvider ViewerProvider = null;
	NMapView mMapView = null;
	public static final String API_KEY = "436b70ce78f67eec4b0dafbf157fc1cd";

	// 맵 컨트롤러
	NMapController mMapController = null;

	LinearLayout liMap;
	Intent result;
	
	NMapPathDataOverlay ExamChildPathDataOverlay = null;
	NMapPathData ExamPathData = null;

	NMapPOIdataOverlay ExamChildPoiDataOverlay = null;
	NMapPOIdata ExamChildPoiData = null;
	
	Drawable StartDraw, FinishDraw;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 자동 생성된 메소드 스텁
		super.onCreate(savedInstanceState);
		setContentView(R.layout.childmain);
		liMap = (LinearLayout)findViewById(R.id.lichildmap);
		result = getIntent();
		LocationM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		StartDraw = getResources().getDrawable(R.drawable.start);
		FinishDraw = getResources().getDrawable(R.drawable.finish);
		
		if (!LocationM.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			AlertDialog.Builder dlg = new AlertDialog.Builder(context);
			dlg.setMessage("GPS기능이 꺼져있습니다. GPS설정화면으로 이동하시겠습니까?");
			dlg.setTitle("알림");
			dlg.setPositiveButton("이동", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Intent gps = new Intent(
							android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivityForResult(gps, 0);
					dialog.dismiss();
				}
			});
			dlg.setNegativeButton("거부", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			dlg.setCancelable(false);
			dlg.show();
		}

		mMapView = new NMapView(this);

		mMapController = mMapView.getMapController();

		mMapView.setApiKey(API_KEY);

		liMap.addView(mMapView);

		mMapView.setClickable(true);

		mMapView.setBuiltInZoomControls(true, null);

		mMapView.setOnMapStateChangeListener(this);
		mMapView.setOnMapViewTouchEventListener(this);

		ViewerProvider = new SPN_MapViewerProvider(SPN_ChildMain.this);
		OverlayManager = new NMapOverlayManager(this, mMapView, ViewerProvider);
		OverlayManager
				.setOnCalloutOverlayListener(new OnCalloutOverlayListener() {

					@Override
					public NMapCalloutOverlay onCreateCalloutOverlay(
							NMapOverlay arg0, NMapOverlayItem arg1, Rect arg2) {
						// TODO Auto-generated method stub
						return new SPN_CallOutOverlay(arg0, arg1, arg2);
					}
				});

		mMapLocationMy = new NMapLocationManager(this);
		mMapLocationMy.enableMyLocation(true);
		MyLocation = OverlayManager.createMyLocationOverlay(mMapLocationMy,
				null);

		mMapLocationMy
				.setOnLocationChangeListener(new OnLocationChangeListener() {

					@Override
					public void onLocationUpdateTimeout(NMapLocationManager arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLocationUnavailableArea(
							NMapLocationManager arg0, NGeoPoint arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public boolean onLocationChanged(NMapLocationManager arg0,
							NGeoPoint arg1) {
						// TODO Auto-generated method stub
						if (mMapLocationMy.isMyLocationFixed()) {

							mMapController.animateTo(new NGeoPoint(
									mMapLocationMy.getMyLocation().longitude,
									mMapLocationMy.getMyLocation().latitude));
							return true;
						}
						return true;
					}
				});
		
		int flag = result.getIntExtra("ParrentORChildFlag", -1);
		
		switch(flag)
		{
		case 1:
			if (SPN_Connect_BackServ.ParrentSetMyLocation == null) {
				return;
			}
			int size = SPN_Connect_BackServ.ParrentSetMyLocation.size();

			if (size == 0) {
				return;
			}
			OverlayManager.clearOverlays();
			if (ExamChildPoiDataOverlay != null) {
				ExamChildPoiDataOverlay.deselectFocusedPOIitem();
				ExamChildPoiDataOverlay.removeAllPOIdata();
			}

			double longtitude[] = new double[size];
			double latitude[] = new double[size];
			for (int i = 0; i < size; i++) {
				String str[] = SPN_Connect_BackServ.ParrentSetMyLocation
						.get(i).split("/");
				longtitude[i] = Double.parseDouble(str[0]);
				latitude[i] = Double.parseDouble(str[1]);

			}

			ExamPathData = new NMapPathData(size);
			ExamChildPoiData = new NMapPOIdata(size, ViewerProvider);

			ExamPathData.initPathData();

			for (int j = 0; j < size; j++) {
				ExamPathData
						.addPathPoint(longtitude[j], latitude[j], 0);

			}

			ExamPathData.endPathData();

			ExamChildPoiData.beginPOIdata(2);
			ExamChildPoiData.addPOIitem(longtitude[0], latitude[0],
					SPN_MainActivity.MainPhoneInfoMy.PhoneNumber
							+ "의 출발지", StartDraw, 0);
			ExamChildPoiData.addPOIitem(longtitude[size - 1],
					latitude[size - 1],
					SPN_MainActivity.MainPhoneInfoMy.PhoneNumber
							+ "의 도착지", FinishDraw, 0);
			ExamChildPoiData.endPOIdata();

			ExamChildPathDataOverlay = OverlayManager
					.createPathDataOverlay(ExamPathData);
			ExamChildPoiDataOverlay = OverlayManager
					.createPOIdataOverlay(ExamChildPoiData, null);
			ExamChildPoiDataOverlay
					.setOnStateChangeListener(new OnStateChangeListener() {

						@Override
						public void onFocusChanged(
								NMapPOIdataOverlay arg0,
								NMapPOIitem arg1) {

						}

						@Override
						public void onCalloutClick(
								NMapPOIdataOverlay arg0,
								NMapPOIitem arg1) {

						}
					});
			ExamChildPathDataOverlay.setLineColor(Color.RED, 255);

			mMapController.setMapCenter(new NGeoPoint(longtitude[0],
					latitude[0]), 14);
			break;
		}
	}

	@Override
	public void onAnimationStateChange(NMapView arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapCenterChange(NMapView arg0, NGeoPoint arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapCenterChangeFine(NMapView arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapInitHandler(NMapView arg0, NMapError arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onZoomLevelChange(NMapView arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLongPress(NMapView arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLongPressCanceled(NMapView arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(NMapView arg0, MotionEvent arg1, MotionEvent arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSingleTapUp(NMapView arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTouchDown(NMapView arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTouchUp(NMapView arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		mMapLocationMy.disableMyLocation();
	}
}
