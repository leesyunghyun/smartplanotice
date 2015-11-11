package smart.planotice.smartplanotice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import smart.planotice.smartplanotice.R;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapLocationManager.OnLocationChangeListener;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.NMapView.OnMapStateChangeListener;
import com.nhn.android.maps.NMapView.OnMapViewTouchEventListener;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.maps.overlay.NMapPathData;
import com.nhn.android.maps.overlay.NMapPathLineStyle;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager.OnCalloutOverlayListener;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay.OnStateChangeListener;
import com.nhn.android.mapviewer.overlay.NMapPathDataOverlay;

public class SPN_ParentMain extends NMapActivity implements
		OnMapStateChangeListener, OnMapViewTouchEventListener {

	// 플래그
	public static final int CancelFlag = 1;
	public static final int SavePathFlag = 2;
	public static final int SelectChildFlag = 3;
	public static final int TouchUpFlag = 4;

	// API-KEY
	public static final String API_KEY = "436b70ce78f67eec4b0dafbf157fc1cd";

	// 네이버 맵 객체
	NMapView mMapView = null;

	// 맵 컨트롤러
	NMapController mMapController = null;

	// 내 위치
	NMapLocationManager mMapLocationMy = null;
	LocationManager LocationM = null;
	Context context;

	// 마커

	NotificationManager NotiManager;
	Vibrator Vib;
	
	SPN_MapViewerProvider ViewerProvider = null;
	NMapOverlayManager OverlayManager;
	NMapPOIdata[] poiData;
	Drawable drawable, StartDraw, FinishDraw, ChildDraw;
	NMapPOIdataOverlay[] poIdataOverlay;
	SPN_MapOverlay Overlay;
	SPN_MapOverlay[] DataOverlay;
	NMapMyLocationOverlay MyLocation;
	NMapPathDataOverlay[] pathDataOverlay;
	NMapPathData[] mPathData = null;

	NMapPOIdataOverlay ChildPoiDataOverlay = null;
	NMapPOIdata ChildPoiData;

	// 지도레이아웃
	LinearLayout liMap;
	LinearLayout litop;

	// 자녀 버튼
	Button BtnChild[];
	int ChildCount = 0;
	int SelectedChildIndex = 0;

	// 하단 버튼
	Button btndown1;
	Button btnmain1, btnmain2, btnmain3, btnmain4;
	Button btncolor1, btncolor2, btncolor3, btncolor4;
	Button btnleft1, btnleft2;
	boolean isPanEnable = true;
	boolean isbtnMainShow = false;
	boolean isbtnColorShow = false;

	int Colorindex = 0;

	// 출발지
	boolean[] isFirst;
	NGeoPoint[] FirstGP;
	NGeoPoint[] LastGP;

	// 중심좌표
	double CenterLongtitude;
	double CenterLatitude;

	Canvas canvas;

	private Thread SPNthread;
	ByteArrayOutputStream baos;
	ObjectOutputStream out;
	ByteArrayInputStream bais;
	ObjectInputStream ois;
	Toast mtoast;
	String InMessage;

	private Intent ChildInfoIntent;
	private String[] ChildPhone;
	private ArrayList<String> arraylist;
	private ArrayAdapter<String> arrayadapter;

	private ListView lvNavList = null;
	private DrawerLayout dlDrawer = null;

	private String[] PaneStr = { R.drawable.btnfragment_view + "/현재 보기 모드입니다.",
			R.drawable.btncanvas + "/경로지정",
			R.drawable.btnfragment_allview + "/전체보기",
			R.drawable.btnfragment_childrotate + "/자녀위치추적",
			R.drawable.btnfragment_childcourse + "/자녀경로추적",
			R.drawable.btnfragment_tracecancel + "/자녀위치추적취소",
			R.drawable.btnroadtracecancel + "/자녀경로추적취소" };

	private String[] NoPaneStr = { R.drawable.viewicon + "/현재 지정 모드입니다.",
			R.drawable.roadsave + "/경로저장", R.drawable.roadcancel + "/경로취소",
			R.drawable.colorchange + "/색변경" };

	// 자녀추적
	boolean isReqChildLocation = false;
	boolean isReqChildLoadLocation = false;
	// 자녀지도에서 쓸 변수들

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 자동 생성된 메소드 스텁
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parentmain);
		ChildInfoIntent = getIntent();
		context = SPN_ParentMain.this;
		liMap = (LinearLayout) findViewById(R.id.lispnMap);
		litop = (LinearLayout) findViewById(R.id.lisvparentmain01);

		SetChild();

		NotiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		drawable = getResources().getDrawable(R.drawable.ic_launcher);
		StartDraw = getResources().getDrawable(R.drawable.start);
		FinishDraw = getResources().getDrawable(R.drawable.finish);
		ChildDraw = getResources().getDrawable(R.drawable.map_child);

		btndown1 = (Button) findViewById(R.id.btnparentmaindown01);
		btnmain1 = (Button) findViewById(R.id.btnparentmain01);
		btnmain2 = (Button) findViewById(R.id.btnparentmain02);
		btnmain3 = (Button) findViewById(R.id.btnparentmain03);
		btnmain4 = (Button) findViewById(R.id.btnparentmain04);
		btncolor1 = (Button) findViewById(R.id.btnparentmain05);
		btncolor2 = (Button) findViewById(R.id.btnparentmain06);
		btncolor3 = (Button) findViewById(R.id.btnparentmain07);
		btncolor4 = (Button) findViewById(R.id.btnparentmain08);
		btnleft1 = (Button) findViewById(R.id.btnparentmain10);
		btnleft2 = (Button) findViewById(R.id.btnparentmain11);

		SPN_MainActivity.ProgressSet(SPN_ParentMain.this,
				"서버에 정보를 전송중입니다. 잠시만 기다려 주세요...");
		arraylist = new ArrayList<String>();

		for (int i = 0; i < PaneStr.length; i++) {
			arraylist.add(PaneStr[i]);
		}
		arrayadapter = new MyAdapter(SPN_ParentMain.this,
				android.R.layout.simple_list_item_1, arraylist);
		lvNavList = (ListView) findViewById(R.id.lv_activity_main_nav_list);

		lvNavList.setAdapter(arrayadapter);

		dlDrawer = (DrawerLayout) findViewById(R.id.dl_activity_main_drawer);

		lvNavList.setOnItemClickListener(new DrawerItemClickListener());

		LocationM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (!LocationM.isProviderEnabled(LocationManager.GPS_PROVIDER)
				&& isPanEnable) {
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

		CenterLatitude = mMapController.getMapCenter().latitude;
		CenterLongtitude = mMapController.getMapCenter().longitude;

		ViewerProvider = new SPN_MapViewerProvider(SPN_ParentMain.this);
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

							// mMapController.setMapCenter(new NGeoPoint(
							// mMapLocationMy.getMyLocation().longitude,
							// mMapLocationMy.getMyLocation().latitude),
							// 14);

							mMapLocationMy.disableMyLocation();

						}
						return true;
					}
				});

		btndown1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (LocationM.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					mMapLocationMy.enableMyLocation(true);
				} else {
					AlertDialog.Builder dlg = new AlertDialog.Builder(context);
					dlg.setMessage("GPS기능이 꺼져있습니다. GPS설정화면으로 이동하시겠습니까?");
					dlg.setTitle("알림");
					dlg.setPositiveButton("이동", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Intent gps = new Intent(
									android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivity(gps);
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
			}
		});

		btnmain1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 자동 생성된 메소드 스텁
				// 오른쪽
				switch (mMapController.getZoomLevel()) {
				case 1:
				case 2:
					mMapController.animateTo(new NGeoPoint(
							CenterLongtitude + 0.8, CenterLatitude));
					break;
				case 3:
				case 4:
					mMapController.animateTo(new NGeoPoint(
							CenterLongtitude + 0.4, CenterLatitude));
					break;
				case 5:
				case 6:
					mMapController.animateTo(new NGeoPoint(
							CenterLongtitude + 0.1, CenterLatitude));
					break;
				case 7:
				case 8:
					mMapController.animateTo(new NGeoPoint(
							CenterLongtitude + 0.035, CenterLatitude));
					break;
				case 9:
				case 10:
				case 11:
					mMapController.animateTo(new NGeoPoint(
							CenterLongtitude + 0.01, CenterLatitude));
					break;
				case 12:
				case 13:
				case 14:
					mMapController.animateTo(new NGeoPoint(
							CenterLongtitude + 0.0015, CenterLatitude));
					break;
				}
			}
		});

		btnmain2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 자동 생성된 메소드 스텁
				// 왼쪽
				switch (mMapController.getZoomLevel()) {
				case 1:
				case 2:
					mMapController.animateTo(new NGeoPoint(
							CenterLongtitude - 0.8, CenterLatitude));
					break;
				case 3:
				case 4:
					mMapController.animateTo(new NGeoPoint(
							CenterLongtitude - 0.4, CenterLatitude));
					break;
				case 5:
				case 6:
					mMapController.animateTo(new NGeoPoint(
							CenterLongtitude - 0.1, CenterLatitude));
					break;

				case 7:
				case 8:
					mMapController.animateTo(new NGeoPoint(
							CenterLongtitude - 0.035, CenterLatitude));
					break;
				case 9:
				case 10:
				case 11:
					mMapController.animateTo(new NGeoPoint(
							CenterLongtitude - 0.01, CenterLatitude));
					break;
				case 12:
				case 13:
				case 14:
					mMapController.animateTo(new NGeoPoint(
							CenterLongtitude - 0.0015, CenterLatitude));
					break;
				}
			}
		});

		btnmain3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 자동 생성된 메소드 스텁
				// 위족
				switch (mMapController.getZoomLevel()) {
				case 1:
				case 2:
					mMapController.animateTo(new NGeoPoint(CenterLongtitude,
							CenterLatitude + 0.8));
					break;
				case 3:
				case 4:
					mMapController.animateTo(new NGeoPoint(CenterLongtitude,
							CenterLatitude + 0.4));
					break;
				case 5:
				case 6:
					mMapController.animateTo(new NGeoPoint(CenterLongtitude,
							CenterLatitude + 0.1));
					break;
				case 7:
				case 8:
					mMapController.animateTo(new NGeoPoint(CenterLongtitude,
							CenterLatitude + 0.035));
					break;
				case 9:
				case 10:
				case 11:
					mMapController.animateTo(new NGeoPoint(CenterLongtitude,
							CenterLatitude + 0.01));
					break;
				case 12:
				case 13:
				case 14:
					mMapController.animateTo(new NGeoPoint(CenterLongtitude,
							CenterLatitude + 0.0015));
					break;
				}
			}
		});

		btnmain4.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 자동 생성된 메소드 스텁
				// 아래쪽
				switch (mMapController.getZoomLevel()) {
				case 1:
				case 2:
					mMapController.animateTo(new NGeoPoint(CenterLongtitude,
							CenterLatitude - 0.8));
					break;
				case 3:
				case 4:
					mMapController.animateTo(new NGeoPoint(CenterLongtitude,
							CenterLatitude - 0.4));
					break;
				case 5:
				case 6:
					mMapController.animateTo(new NGeoPoint(CenterLongtitude,
							CenterLatitude - 0.1));
					break;
				case 7:
				case 8:
					mMapController.animateTo(new NGeoPoint(CenterLongtitude,
							CenterLatitude - 0.035));
					break;
				case 9:
				case 10:
				case 11:
					mMapController.animateTo(new NGeoPoint(CenterLongtitude,
							CenterLatitude - 0.01));
					break;
				case 12:
				case 13:
				case 14:
					mMapController.animateTo(new NGeoPoint(CenterLongtitude,
							CenterLatitude - 0.0015));
					break;
				}
			}
		});

		btncolor1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Colorindex = 0;
				ChkColorBtn(Colorindex);
			}
		});

		btncolor2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Colorindex = 1;
				ChkColorBtn(Colorindex);
			}
		});

		btncolor3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Colorindex = 2;
				ChkColorBtn(Colorindex);
			}
		});

		btncolor4.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Colorindex = 3;
				ChkColorBtn(Colorindex);
			}
		});

		btnleft1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isPanEnable) {
					return;
				}

				if (SelectedChildIndex == -1) {
					handler.sendEmptyMessage(1);
					return;
				}

				OverlayManager.clearOverlays();

				if (DataOverlay[SelectedChildIndex].GetPathDataSize() != 0) {

					DataOverlay[SelectedChildIndex]
							.CancelDraw(DataOverlay[SelectedChildIndex]
									.GetPathDataSize() - 1);

					DrawMap_PathPoI(SelectedChildIndex, CancelFlag);

					if (DataOverlay[SelectedChildIndex].GetPathDataSize() != 0) {
						LastGP[SelectedChildIndex] = new NGeoPoint(
								DataOverlay[SelectedChildIndex]
										.GetPathDataArray().get(
												DataOverlay[SelectedChildIndex]
														.GetPathDataSize() - 1).x,
								DataOverlay[SelectedChildIndex]
										.GetPathDataArray().get(
												DataOverlay[SelectedChildIndex]
														.GetPathDataSize() - 1).y);
					} else {
						isFirst[SelectedChildIndex] = true;
						poIdataOverlay[SelectedChildIndex].removeAllPOIdata();
					}
				}
			}
		});

		btnleft2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 자동 생성된 메소드 스텁
				if (dlDrawer.isDrawerOpen(lvNavList))
					dlDrawer.closeDrawer(lvNavList);
				else
					dlDrawer.openDrawer(lvNavList);
			}
		});
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> adapter, View view,
				int position, long id) {
			if (arraylist.get(0).equals(PaneStr[0])) {
				switch (position) {
				case 0:
					break;
				case 1:
					if (isPanEnable) {
						AlertDialog.Builder dlg = new AlertDialog.Builder(
								SPN_ParentMain.this);
						dlg.setCancelable(false);
						dlg.setMessage("경로를 설정하기 위해 지도 위치를 임의적으로 고정하였습니다. 상,하,좌,우에 위치한 화살표버튼으로 지도의 위치를 변경하시기 바랍니다.");
						dlg.setTitle("알림");
						dlg.setPositiveButton("확인", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Overlay = new SPN_MapOverlay();
								isPanEnable = false;
								mMapController.setPanEnabled(false);
								mMapController.setZoomEnabled(false);

								SetBtnShown(true, 0);
								btncolor1.performClick();
								mMapView.setBuiltInZoomControls(false, null);

								arraylist.clear();
								for (int i = 0; i < NoPaneStr.length; i++) {
									arraylist.add(NoPaneStr[i]);
								}
								arrayadapter = new MyAdapter(
										SPN_ParentMain.this,
										android.R.layout.simple_list_item_1,
										arraylist);

								lvNavList.setAdapter(arrayadapter);

								dialog.dismiss();
							}
						});
						dlg.show();
					}
					break;
				case 2:
					if (SelectedChildIndex == -1) {
						handler.sendEmptyMessage(1);
						return;
					}

					OverlayManager.clearOverlays();

					for (int i = 0; i < ChildCount; i++) {
						if (DataOverlay[i].GetPathDataSize() == 0) {
							continue;
						}
						mPathData[i] = new NMapPathData(
								DataOverlay[i].GetPathDataSize());
						mPathData[i].initPathData();

						for (int j = 0; j < DataOverlay[i].GetPathDataSize(); j++) {
							mPathData[i].addPathPoint(DataOverlay[i]
									.GetPathDataArray().get(j).x,
									DataOverlay[i].GetPathDataArray().get(j).y,
									NMapPathLineStyle.TYPE_SOLID);
						}

						mPathData[i].endPathData();

						if (DataOverlay[i].GetPathDataSize() != 0) {
							poiData[i].beginPOIdata(2);
							poiData[i].addPOIitem(FirstGP[i].longitude,
									FirstGP[i].latitude, ChildPhone[i]
											+ "의 출발지", StartDraw, 0);
							poiData[i].addPOIitem(LastGP[i].longitude,
									LastGP[i].latitude,
									ChildPhone[i] + "의 도착지", FinishDraw, 0);
							poiData[i].endPOIdata();

							pathDataOverlay[i] = OverlayManager
									.createPathDataOverlay(mPathData[i]);
							poIdataOverlay[i] = OverlayManager
									.createPOIdataOverlay(poiData[i], null);
							poIdataOverlay[i]
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
							pathDataOverlay[i]
									.setLineColor(
											DataOverlay[i]
													.GetPathDataArray()
													.get(DataOverlay[i]
															.GetPathDataSize() - 1).mpaint
													.getColor(), 255);
						}
					}
					mMapController.setMapCenter(FirstGP[0], 3);
					break;
				case 3:
					// 자녀위치추적
					if (ChildCount != 0) {
						if (!isReqChildLocation) {
							handler.sendEmptyMessage(5);
							isReqChildLocation = true;
							Notification childmad = new Notification(R.drawable.main,
									"현재 자녀위치추적중입니다.", System.currentTimeMillis());
							childmad.defaults = Notification.DEFAULT_SOUND;
							childmad.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_SHOW_LIGHTS;
							Intent putAccredit = new Intent();
							putAccredit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							PendingIntent pendAccredit = PendingIntent.getActivity(
									SPN_ParentMain.this, 0, putAccredit, 0);
							childmad.setLatestEventInfo(SPN_ParentMain.this,
									"SmartPlaNotice", "현재 자녀위치추적중입니다. . .", pendAccredit);
							NotiManager.notify(0, childmad);

							Vib.vibrate(1000);
						} else {
							handler.sendEmptyMessage(6);
						}
					}
					break;
				case 4:
					// 자녀경로추적
					if (isReqChildLoadLocation) {
						handler.sendEmptyMessage(13);
					} else {
						handler.sendEmptyMessage(10);
						isReqChildLoadLocation = true;
						Notification childmad = new Notification(R.drawable.main,
								"현재 자녀경로추적중입니다.", System.currentTimeMillis());
						childmad.defaults = Notification.DEFAULT_SOUND;
						childmad.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_SHOW_LIGHTS;
						Intent putAccredit = new Intent();
						putAccredit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						PendingIntent pendAccredit = PendingIntent.getActivity(
								SPN_ParentMain.this, 0, putAccredit, 0);
						childmad.setLatestEventInfo(SPN_ParentMain.this,
								"SmartPlaNotice", "현재 자녀경로추적중입니다. . .", pendAccredit);
						NotiManager.notify(1, childmad);

						Vib.vibrate(1000);
						
					}
					break;
				case 5:
					if (isReqChildLocation) {
						isReqChildLocation = false;
						handler.removeMessages(5);
						handler.sendEmptyMessage(8);
						NotiManager.cancel(0);
					} else {
						handler.sendEmptyMessage(7);
					}
					break;
				case 6:
					if (isReqChildLoadLocation) {
						isReqChildLoadLocation = false;
						handler.sendEmptyMessage(11);
						NotiManager.cancel(1);
					} else {
						handler.sendEmptyMessage(12);
					}
					break;
				}
			} else {
				switch (position) {
				case 0:
					Toast.makeText(SPN_ParentMain.this, "테스트",
							Toast.LENGTH_SHORT).show();
					break;
				case 1:
					if (isPanEnable) {
						return;
					}

					if (SelectedChildIndex == -1) {
						handler.sendEmptyMessage(1);
						return;
					}

					if (isFirst[SelectedChildIndex]) {
						return;
					}
					OverlayManager.clearOverlays();

					for (int i = 0; i < ChildCount; i++) {
						if (DataOverlay[i].GetPathDataSize() == 0) {
							continue;
						}

						DrawMap_PathPoI(i, SavePathFlag);
					}
					isPanEnable = true;
					mMapController.setPanEnabled(true);
					mMapController.setZoomEnabled(true);
					SetBtnShown(false, 0);
					mMapView.setBuiltInZoomControls(true, null);

					Overlay.setHidden(true);
					Overlay = null;
					OverlayManager.removeOverlay(Overlay);

					arraylist.clear();
					for (int i = 0; i < PaneStr.length; i++) {
						arraylist.add(PaneStr[i]);
					}

					arrayadapter = new MyAdapter(SPN_ParentMain.this,
							android.R.layout.simple_list_item_1, arraylist);

					lvNavList.setAdapter(arrayadapter);

					handler.sendEmptyMessage(2);
					SPN_MainActivity.progressDialog.show();
					break;
				case 2:
					AlertDialog.Builder dlg = new AlertDialog.Builder(
							SPN_ParentMain.this);
					dlg.setMessage("경로설정을 취소하시겠습니까?");
					dlg.setTitle("안내");
					dlg.setPositiveButton("예", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							isPanEnable = true;
							mMapController.setPanEnabled(true);
							mMapController.setZoomEnabled(true);
							SetBtnShown(false, 0);
							mMapView.setBuiltInZoomControls(true, null);

							/*
							 * 전에 경로저장을 했던 경로를 불러와야됨 즉 데이터베이스 이용해서 불러와야됨.
							 * for(int i=0;i<ChildCount;i++) { DataOverlay[i] =
							 * new SPN_MapOverlay(); }
							 */

							Overlay.setHidden(true);
							Overlay = null;
							OverlayManager.removeOverlay(Overlay);
							OverlayManager.clearOverlays();

							arraylist.clear();
							for (int i = 0; i < PaneStr.length; i++) {
								arraylist.add(PaneStr[i]);
							}

							arrayadapter = new MyAdapter(SPN_ParentMain.this,
									android.R.layout.simple_list_item_1,
									arraylist);
							lvNavList.setAdapter(arrayadapter);

						}
					});

					dlg.setNegativeButton("아니요", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO 자동 생성된 메소드 스텁
							dialog.dismiss();
						}
					});
					dlg.show();
					break;
				case 3:
					if (!isPanEnable) {
						SetBtnShown(true, 1);
					}
					break;
				case 4:
					Toast.makeText(SPN_ParentMain.this, "아이템4",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}

			dlDrawer.closeDrawer(lvNavList);
		}
	};

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (dlDrawer.isDrawerOpen(lvNavList))
				dlDrawer.closeDrawer(lvNavList);
			else
				dlDrawer.openDrawer(lvNavList);
			return true;
		} else {
			return super.onKeyUp(keyCode, event);
		}
	}

	public void SetChild() {
		// 인텐트로 아이 카운트를 받아온 다음 포문으로 버튼 객체를 삽입
		ChildPhone = ChildInfoIntent.getStringArrayExtra("ChildInfo");
		if (ChildPhone[0].equals(SPN_Preferense.EmptyState)) {
			ChildCount = 0;
		} else {
			ChildCount = ChildPhone.length;
		}
		SelectedChildIndex = -1;
		BtnChild = new Button[ChildCount];
		DataOverlay = new SPN_MapOverlay[ChildCount];
		poiData = new NMapPOIdata[ChildCount];
		isFirst = new boolean[ChildCount];
		FirstGP = new NGeoPoint[ChildCount];
		LastGP = new NGeoPoint[ChildCount];

		pathDataOverlay = new NMapPathDataOverlay[ChildCount];
		poIdataOverlay = new NMapPOIdataOverlay[ChildCount];
		mPathData = new NMapPathData[ChildCount];

		if (ChildCount == 0) {
			TextView tv = new TextView(SPN_ParentMain.this);
			tv.setText("등록된 자녀가 없습니다. 자녀를 등록해주세요");
			tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			tv.setClickable(false);
			tv.setFocusable(false);
			litop.addView(tv);
			return;
		}
		for (int i = 0; i < ChildCount; i++) {
			final int index = i;
			BtnChild[i] = new Button(SPN_ParentMain.this);
			DataOverlay[i] = new SPN_MapOverlay();
			poiData[i] = new NMapPOIdata(1, ViewerProvider);
			ChildPoiData = new NMapPOIdata(ChildCount, ViewerProvider);
			FirstGP[i] = new NGeoPoint();
			LastGP[i] = new NGeoPoint();
			isFirst[i] = true;
			BtnChild[i].setFocusable(false);
			BtnChild[i].setBackgroundResource(R.drawable.btnchild);
			BtnChild[i].setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (isPanEnable && SelectedChildIndex == -1) {
						return;
					}
					SelectedChildIndex = index;

					OverlayManager.clearOverlays();

					if (DataOverlay[SelectedChildIndex].GetPathDataSize() != 0) {

						DrawMap_PathPoI(SelectedChildIndex, SelectChildFlag);

						if (DataOverlay[SelectedChildIndex].GetPathDataSize() != 0) {

							poIdataOverlay[SelectedChildIndex]
									.showAllPOIdata(0);
						} else {
							isFirst[SelectedChildIndex] = true;
						}
					}
				}
			});
			litop.addView(BtnChild[i]);
		}
	}

	@Override
	public void onAnimationStateChange(NMapView arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapCenterChange(NMapView arg0, NGeoPoint arg1) {
		// TODO Auto-generated method stub
		CenterLatitude = arg1.latitude;
		CenterLongtitude = arg1.longitude;
	}

	@Override
	public void onMapCenterChangeFine(NMapView arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapInitHandler(NMapView arg0, NMapError arg1) {
		if (arg1 == null) {
			if (mMapLocationMy.isMyLocationFixed()) {
				mMapController.setMapCenter(
						new NGeoPoint(mMapLocationMy.getMyLocation().longitude,
								mMapLocationMy.getMyLocation().latitude), 14);

			} else {
				mMapController.setMapCenter(new NGeoPoint(126.978371,
						37.5666091), 14);
			}
		} else {
			android.util.Log.e("NMAP",
					"onMapInitHandler : error = " + arg1.toString());
		}
	}

	@Override
	public void onZoomLevelChange(NMapView arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (dlDrawer.isDrawerOpen(lvNavList)) {
			dlDrawer.closeDrawer(lvNavList);
			return;
		}

		if (isPanEnable) {
			mMapLocationMy.disableMyLocation();
			finish();
			SPN_ParentMain.this.onDestroy();
		} else {
			AlertDialog.Builder dlg = new AlertDialog.Builder(
					SPN_ParentMain.this);
			dlg.setMessage("경로설정을 취소하시겠습니까?");
			dlg.setTitle("안내");
			dlg.setPositiveButton("예", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					isPanEnable = true;
					mMapController.setPanEnabled(true);
					mMapController.setZoomEnabled(true);
					SetBtnShown(false, 0);
					mMapView.setBuiltInZoomControls(true, null);

					Overlay.setHidden(true);
					Overlay = null;
					OverlayManager.removeOverlay(Overlay);

					arraylist.clear();
					for (int i = 0; i < PaneStr.length; i++) {
						arraylist.add(PaneStr[i]);
					}

					arrayadapter = new MyAdapter(SPN_ParentMain.this,
							android.R.layout.simple_list_item_1, arraylist);
					lvNavList.setAdapter(arrayadapter);
				}
			});

			dlg.setNegativeButton("아니요", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO 자동 생성된 메소드 스텁
					dialog.dismiss();
				}
			});
			dlg.show();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (LocationM.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			mMapLocationMy.enableMyLocation(true);
		}

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

		NGeoPoint gp = arg0.getMapProjection().fromPixels((int) arg1.getX(),
				(int) arg1.getY());
		Log.e("경도 : " + gp.getLongitudeE6(), "위도 : " + gp.getLatitudeE6());

		if (isbtnMainShow) {
			SetBtnShown(false, 1);
			if (SelectedChildIndex == -1) {
				handler.sendEmptyMessage(1);
				return;
			}

			if (isFirst[SelectedChildIndex]) {
				isFirst[SelectedChildIndex] = false;
				FirstGP[SelectedChildIndex] = gp;
				poiData[SelectedChildIndex].beginPOIdata(1);
				poiData[SelectedChildIndex].addPOIitem(
						FirstGP[SelectedChildIndex].longitude,
						FirstGP[SelectedChildIndex].latitude,
						ChildPhone[SelectedChildIndex] + "의 출발지", StartDraw, 0);
				poiData[SelectedChildIndex].endPOIdata();
			}

			switch (Colorindex) {
			case 0:
				Overlay.ChageColor(Color.RED);
				break;
			case 1:
				Overlay.ChageColor(Color.BLUE);
				break;
			case 2:
				Overlay.ChageColor(Color.YELLOW);
				break;
			case 3:
				Overlay.ChageColor(Color.GREEN);
				break;
			case 4:
				Overlay.ChageColor(Color.WHITE);
				break;
			}

			Overlay.onTouchEvent(arg1, arg0);
			OverlayManager.addOverlay(Overlay);
		}
	}

	@Override
	public void onTouchUp(NMapView arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		NGeoPoint gp = arg0.getMapProjection().fromPixels((int) arg1.getX(),
				(int) arg1.getY());
		Log.e("경도 : " + gp.longitude, "위도 : " + gp.latitude);
		if (isbtnMainShow) {
			if (SelectedChildIndex == -1) {
				handler.sendEmptyMessage(1);
				return;
			}

			LastGP[SelectedChildIndex] = gp;
			OverlayManager.clearOverlays();

			for (int i = 0; i < Overlay.GetPathDataSize(); i++) {
				DataOverlay[SelectedChildIndex].SetData(
						Overlay.arVertex.get(i).x, Overlay.arVertex.get(i).y,
						Overlay.arVertex.get(i).bDraw, Overlay
								.GetPathDataArray().get(i).x, Overlay
								.GetPathDataArray().get(i).y, Overlay
								.GetPathDataArray().get(i).mpaint);
			}

			DrawMap_PathPoI(SelectedChildIndex, TouchUpFlag);

			Overlay = new SPN_MapOverlay();
		}
	}

	public void SetBtnShown(boolean shown, int flag) {
		switch (flag) {
		case 0:
			if (shown) {
				if (isbtnMainShow) {
					return;
				}
				btnmain1.setVisibility(View.VISIBLE);
				btnmain2.setVisibility(View.VISIBLE);
				btnmain3.setVisibility(View.VISIBLE);
				btnmain4.setVisibility(View.VISIBLE);
				btnleft1.setVisibility(View.VISIBLE);

				btncolor1.setVisibility(View.VISIBLE);
				btncolor2.setVisibility(View.VISIBLE);
				btncolor3.setVisibility(View.VISIBLE);
				btncolor4.setVisibility(View.VISIBLE);

				btnmain1.startAnimation(AnimationUtils.loadAnimation(
						SPN_ParentMain.this, R.anim.right_in));
				btnmain2.startAnimation(AnimationUtils.loadAnimation(
						SPN_ParentMain.this, R.anim.left_in));
				btnleft1.startAnimation(AnimationUtils.loadAnimation(
						SPN_ParentMain.this, R.anim.right_in));
				btnmain3.startAnimation(AnimationUtils.loadAnimation(
						SPN_ParentMain.this, R.anim.top_in));
				btnmain4.startAnimation(AnimationUtils.loadAnimation(
						SPN_ParentMain.this, R.anim.bottom_in));

				if (!isbtnColorShow) {
					btncolor1.startAnimation(AnimationUtils.loadAnimation(
							SPN_ParentMain.this, R.anim.right_in));
					btncolor2.startAnimation(AnimationUtils.loadAnimation(
							SPN_ParentMain.this, R.anim.right_in));
					btncolor3.startAnimation(AnimationUtils.loadAnimation(
							SPN_ParentMain.this, R.anim.right_in));
					btncolor4.startAnimation(AnimationUtils.loadAnimation(
							SPN_ParentMain.this, R.anim.right_in));
				}
				isbtnColorShow = true;
				isbtnMainShow = true;
			} else {
				if (!isbtnMainShow) {
					return;
				}
				btnmain1.setVisibility(View.GONE);
				btnmain2.setVisibility(View.GONE);
				btnmain3.setVisibility(View.GONE);
				btnmain4.setVisibility(View.GONE);
				btnleft1.setVisibility(View.GONE);

				btnmain1.startAnimation(AnimationUtils.loadAnimation(
						SPN_ParentMain.this, R.anim.right_out));
				btnmain2.startAnimation(AnimationUtils.loadAnimation(
						SPN_ParentMain.this, R.anim.left_out));
				btnleft1.startAnimation(AnimationUtils.loadAnimation(
						SPN_ParentMain.this, R.anim.right_out));
				btnmain3.startAnimation(AnimationUtils.loadAnimation(
						SPN_ParentMain.this, R.anim.top_out));
				btnmain4.startAnimation(AnimationUtils.loadAnimation(
						SPN_ParentMain.this, R.anim.bottom_out));

				if (isbtnColorShow) {
					btncolor1.startAnimation(AnimationUtils.loadAnimation(
							SPN_ParentMain.this, R.anim.right_out));
					btncolor2.startAnimation(AnimationUtils.loadAnimation(
							SPN_ParentMain.this, R.anim.right_out));
					btncolor3.startAnimation(AnimationUtils.loadAnimation(
							SPN_ParentMain.this, R.anim.right_out));
					btncolor4.startAnimation(AnimationUtils.loadAnimation(
							SPN_ParentMain.this, R.anim.right_out));
				}

				btncolor1.setVisibility(View.GONE);
				btncolor2.setVisibility(View.GONE);
				btncolor3.setVisibility(View.GONE);
				btncolor4.setVisibility(View.GONE);

				isbtnColorShow = false;
				isbtnMainShow = false;
			}
			break;
		case 1:
			if (shown) {
				if (isbtnColorShow) {
					return;
				}
				btncolor1.setVisibility(View.VISIBLE);
				btncolor2.setVisibility(View.VISIBLE);
				btncolor3.setVisibility(View.VISIBLE);
				btncolor4.setVisibility(View.VISIBLE);

				btncolor1.startAnimation(AnimationUtils.loadAnimation(
						SPN_ParentMain.this, R.anim.right_in));
				btncolor2.startAnimation(AnimationUtils.loadAnimation(
						SPN_ParentMain.this, R.anim.right_in));
				btncolor3.startAnimation(AnimationUtils.loadAnimation(
						SPN_ParentMain.this, R.anim.right_in));
				btncolor4.startAnimation(AnimationUtils.loadAnimation(
						SPN_ParentMain.this, R.anim.right_in));
				isbtnColorShow = true;
			} else {
				if (!isbtnColorShow) {
					return;
				}
				btncolor1.startAnimation(AnimationUtils.loadAnimation(
						SPN_ParentMain.this, R.anim.right_out));
				btncolor2.startAnimation(AnimationUtils.loadAnimation(
						SPN_ParentMain.this, R.anim.right_out));
				btncolor3.startAnimation(AnimationUtils.loadAnimation(
						SPN_ParentMain.this, R.anim.right_out));
				btncolor4.startAnimation(AnimationUtils.loadAnimation(
						SPN_ParentMain.this, R.anim.right_out));

				btncolor1.setVisibility(View.GONE);
				btncolor2.setVisibility(View.GONE);
				btncolor3.setVisibility(View.GONE);
				btncolor4.setVisibility(View.GONE);
				isbtnColorShow = false;
			}
			break;
		}
	}

	public void DrawMap_PathPoI(int index, int flag) {
		if (DataOverlay[index].GetPathDataSize() != 0) {
			mPathData[index] = new NMapPathData(
					DataOverlay[index].GetPathDataSize());
			mPathData[index].initPathData();

			for (int i = 0; i < DataOverlay[index].GetPathDataSize(); i++) {
				mPathData[index].addPathPoint(DataOverlay[index]
						.GetPathDataArray().get(i).x, DataOverlay[index]
						.GetPathDataArray().get(i).y,
						NMapPathLineStyle.TYPE_SOLID);
			}

			mPathData[index].endPathData();

			if (DataOverlay[index].GetPathDataSize() != 0) {

				if (flag == CancelFlag) {
					poIdataOverlay[index].deselectFocusedPOIitem();
					poIdataOverlay[index].removeAllPOIdata();
					poiData[index].beginPOIdata(1);
					poiData[index].addPOIitem(FirstGP[index].longitude,
							FirstGP[index].latitude,
							ChildPhone[index] + "의 출발지", StartDraw, 0)
							.setMarkerId(0);

					poiData[index].endPOIdata();
				} else if (flag == SavePathFlag) {
					poIdataOverlay[index].deselectFocusedPOIitem();
					poIdataOverlay[index].removeAllPOIdata();
					poiData[index].beginPOIdata(2);
					poiData[index].addPOIitem(FirstGP[index].longitude,
							FirstGP[index].latitude,
							ChildPhone[index] + "의 출발지", StartDraw, 0)
							.setMarkerId(0);
					poiData[index].addPOIitem(LastGP[index].longitude,
							LastGP[index].latitude,
							ChildPhone[index] + "의 도착지", FinishDraw, 0)
							.setMarkerId(1);
					poiData[index].endPOIdata();
				} else if (flag == SelectChildFlag) {
					poIdataOverlay[index].deselectFocusedPOIitem();
					poIdataOverlay[index].removeAllPOIdata();
					if (isPanEnable) {
						poiData[index].beginPOIdata(2);
						poiData[index].addPOIitem(FirstGP[index].longitude,
								FirstGP[index].latitude,
								ChildPhone[index] + "의 출발지", StartDraw, 0)
								.setMarkerId(0);
						poiData[index].addPOIitem(LastGP[index].longitude,
								LastGP[index].latitude,
								ChildPhone[index] + "의 도착지", FinishDraw, 0)
								.setMarkerId(1);
						poiData[index].endPOIdata();
					} else {
						poiData[index].beginPOIdata(1);
						poiData[index].addPOIitem(FirstGP[index].longitude,
								FirstGP[index].latitude,
								ChildPhone[index] + "의 출발지", StartDraw, 0)
								.setMarkerId(0);
						poiData[index].endPOIdata();
					}
					poIdataOverlay[index].selectPOIitem(0, true);
				} else if (flag == TouchUpFlag) {

				}
				pathDataOverlay[index] = OverlayManager
						.createPathDataOverlay(mPathData[index]);
				poIdataOverlay[index] = OverlayManager.createPOIdataOverlay(
						poiData[index], null);
				poIdataOverlay[index]
						.setOnStateChangeListener(new OnStateChangeListener() {

							@Override
							public void onFocusChanged(NMapPOIdataOverlay arg0,
									NMapPOIitem arg1) {

							}

							@Override
							public void onCalloutClick(NMapPOIdataOverlay arg0,
									NMapPOIitem arg1) {

							}
						});
				mMapController.setMapCenter(
						DataOverlay[index].GetPathDataArray().get(
								DataOverlay[index].GetPathDataSize() - 1).x,
						DataOverlay[index].GetPathDataArray().get(
								DataOverlay[index].GetPathDataSize() - 1).y);
				pathDataOverlay[index]
						.setLineColor(
								DataOverlay[index].GetPathDataArray()
										.get(DataOverlay[index]
												.GetPathDataSize() - 1).mpaint
										.getColor(), 255);
			}
		}
	}

	public void ChkColorBtn(int index) {
		btncolor1.setBackgroundResource(R.drawable.palletred);
		btncolor2.setBackgroundResource(R.drawable.palletblue);
		btncolor3.setBackgroundResource(R.drawable.palletyello);
		btncolor4.setBackgroundResource(R.drawable.palletgreen);

		switch (index) {
		case 0:
			btncolor1.setBackgroundResource(R.drawable.clickpalletred);
			break;
		case 1:
			btncolor2.setBackgroundResource(R.drawable.clickpalletblue);
			break;
		case 2:
			btncolor3.setBackgroundResource(R.drawable.clickpalletyello);
			break;
		case 3:
			btncolor4.setBackgroundResource(R.drawable.clickpalletgreen);
			break;
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO 자동 생성된 메소드 스텁
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mtoast = Toast.makeText(SPN_ParentMain.this,
						"네트워크 상태가 원활하지 않습니다.", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				break;
			case 1:
				mtoast = Toast.makeText(SPN_ParentMain.this,
						"상단에 위치한 자녀를 먼저 선택해주세요.", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				break;
			case 2:
				if (SPN_MainActivity.progressDialog.isShowing()) {
					if (!SPN_MainActivity.MainThread.channel.isConnected()) {
						handler.sendEmptyMessage(0);
						SPN_MainActivity.MainServConnect(SPN_ParentMain.this);
					}

					if (SPNthread != null && SPNthread.isAlive()) {
						SPNthread.interrupt();
					}

					SPNthread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO 자동 생성된 메소드 스텁
							SendChildGpsSave();
							SPNthread.interrupt();
						}
					});
					SPNthread.start();
				}
				break;
			case 3:
				mtoast = Toast.makeText(SPN_ParentMain.this, "성공", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				break;
			case 4:
				mtoast = Toast.makeText(SPN_ParentMain.this, "실패", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				break;
			case 5:
				SPN_MainActivity.progressDialog.show();
				handler.sendEmptyMessage(9);
				handler.sendEmptyMessageDelayed(5, 20000);
				if (SPN_MainActivity.progressDialog.isShowing()) {
					if (!SPN_MainActivity.MainThread.channel.isConnected()) {
						handler.sendEmptyMessage(0);
						SPN_MainActivity.MainServConnect(SPN_ParentMain.this);
					}

					if (SPNthread != null && SPNthread.isAlive()) {
						SPNthread.interrupt();
					}

					SPNthread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO 자동 생성된 메소드 스텁
							ReqGpsState01();
							SPNthread.interrupt();
						}
					});
					SPNthread.start();
				}
				break;
			case 6:
				mtoast = Toast.makeText(SPN_ParentMain.this,
						"이미 자녀위치를 추적중입니다.", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				break;
			case 7:
				mtoast = Toast.makeText(SPN_ParentMain.this,
						"현재 자녀위치를 추적중이지 않습니다.", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				break;
			case 8:
				SPN_MainActivity.progressDialog.show();
				if (SPN_MainActivity.progressDialog.isShowing()) {
					if (!SPN_MainActivity.MainThread.channel.isConnected()) {
						handler.sendEmptyMessage(0);
						SPN_MainActivity.MainServConnect(SPN_ParentMain.this);
					}

					if (SPNthread != null && SPNthread.isAlive()) {
						SPNthread.interrupt();
					}

					SPNthread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO 자동 생성된 메소드 스텁
							NoReqGpsState01();
							SPNthread.interrupt();
						}
					});
					SPNthread.start();
				}
				break;
			case 9:
				
				if (!SPN_Connect_BackServ.ChildLocation.isEmpty()) {
					OverlayManager.clearOverlays();
					if (ChildPoiDataOverlay != null) {
						ChildPoiDataOverlay.deselectFocusedPOIitem();
						ChildPoiDataOverlay.removeAllPOIdata();
					}
					ChildPoiData
							.beginPOIdata(SPN_Connect_BackServ.ChildLocation
									.size());
					for (int i = 0; i < ChildCount; i++) {
						if (SPN_Connect_BackServ.ChildLocation
								.containsKey(ChildPhone[i].replace("+82", "0"))) {
							if (!SPN_Connect_BackServ.ChildLocation.get(
									ChildPhone[i].replace("+82", "0")).equals(
									"GpsOff")) {
								String[] str;
								str = SPN_Connect_BackServ.ChildLocation.get(
										ChildPhone[i].replace("+82", "0"))
										.split("/");
								Log.e(str[0], str[1]);
								ChildPoiData.addPOIitem(
										Double.parseDouble(str[0]),
										Double.parseDouble(str[1]),
										ChildPhone[i], ChildDraw, 0);
							}
						}

					}

					ChildPoiData.endPOIdata();
					ChildPoiDataOverlay = OverlayManager.createPOIdataOverlay(
							ChildPoiData, null);

					ChildPoiDataOverlay
							.setOnStateChangeListener(new OnStateChangeListener() {

								@Override
								public void onFocusChanged(
										NMapPOIdataOverlay arg0,
										NMapPOIitem arg1) {
									// TODO 자동 생성된 메소드 스텁

								}

								@Override
								public void onCalloutClick(
										NMapPOIdataOverlay arg0,
										NMapPOIitem arg1) {
									// TODO 자동 생성된 메소드 스텁

								}
							});
					for (int i = 0; i < ChildCount; i++) {
						if (DataOverlay[i].GetPathDataSize() == 0) {
							continue;
						}

						DrawMap_PathPoI(i, SavePathFlag);
					}
				}
				break;
			case 10:
				SPN_MainActivity.progressDialog.show();
				if (SPN_MainActivity.progressDialog.isShowing()) {
					if (!SPN_MainActivity.MainThread.channel.isConnected()) {
						handler.sendEmptyMessage(0);
						SPN_MainActivity.MainServConnect(SPN_ParentMain.this);
					}

					if (SPNthread != null && SPNthread.isAlive()) {
						SPNthread.interrupt();
					}

					SPNthread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO 자동 생성된 메소드 스텁
							TransChildGpsState();
							SPNthread.interrupt();
						}
					});
					SPNthread.start();
				}
				break;
			case 11:
				SPN_MainActivity.progressDialog.show();
				if (SPN_MainActivity.progressDialog.isShowing()) {
					if (!SPN_MainActivity.MainThread.channel.isConnected()) {
						handler.sendEmptyMessage(0);
						SPN_MainActivity.MainServConnect(SPN_ParentMain.this);
					}

					if (SPNthread != null && SPNthread.isAlive()) {
						SPNthread.interrupt();
					}

					SPNthread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO 자동 생성된 메소드 스텁
							NoReqGpsState02();
							SPNthread.interrupt();
						}
					});
					SPNthread.start();
				}
				break;
			case 12:
				mtoast = Toast.makeText(SPN_ParentMain.this,
						"현재 자녀경로를 추적중이지 않습니다.", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				break;
			case 13:
				mtoast = Toast.makeText(SPN_ParentMain.this,
						"이미 자녀경로를 추적중입니다.", 0);
				mtoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mtoast.show();
				break;
			}
		}
	};

	public void ReqGpsState01() {
		try {
			baos = new ByteArrayOutputStream();
			out = new ObjectOutputStream(baos);

			SPN_Object spn = new SPN_Object(SPN_MainActivity.MainPhoneInfoMy,
					null, null, SPN_Preferense.ReqGpsState01);

			Object obc = spn;

			out.writeObject(obc);

			SPN_MainActivity.MainThread.channel.write(ByteBuffer.wrap(baos
					.toByteArray()));
			ByteBuffer data = ByteBuffer.allocate(4096);
			SPN_MainActivity.MainThread.selector.select();

			SPN_MainActivity.MainThread.channel.read(data);

			bais = new ByteArrayInputStream(data.array());

			ois = new ObjectInputStream(bais);

			SPN_Object inspn = (SPN_Object) ois.readObject();

			InMessage = inspn.Message;
			if (InMessage.equals(SPN_Preferense.SuccessState)) {
				// handler.sendEmptyMessage(3);
			} else {
				handler.sendEmptyMessage(4);
			}
			SPN_MainActivity.progressDialog.dismiss();

		} catch (Exception e) {
			e.printStackTrace();
			handler.sendEmptyMessageDelayed(5, 3000);
		}
	}

	public void SendChildGpsSave() {
		try {
			ArrayList<String> Spn_Vertex = new ArrayList<String>();

			for (int j = 0; j < ChildCount; j++) {
				for (int i = 0; i < DataOverlay[j].GetPathDataSize(); i++) {
					Spn_Vertex.add(Double.parseDouble(String.format("%.7f",
							DataOverlay[j].arVertex2.get(i).x))
							+ "/"
							+ Double.parseDouble(String.format("%.7f",
									DataOverlay[j].arVertex2.get(i).y)));
				}

				baos = new ByteArrayOutputStream();
				out = new ObjectOutputStream(baos);

				SPN_GPSInfoObject PhoneGpsInfoYou = new SPN_GPSInfoObject(
						Spn_Vertex, j);

				SPN_PhoneInfoObject PhoneInfoYou = new SPN_PhoneInfoObject(
						null, null, ChildPhone[j], null, null, PhoneGpsInfoYou);

				SPN_Object spn = new SPN_Object(
						SPN_MainActivity.MainPhoneInfoMy, PhoneInfoYou, null,
						SPN_Preferense.AddChildGpsState);

				Object obc = spn;

				out.writeObject(obc);

				SPN_MainActivity.MainThread.channel.write(ByteBuffer.wrap(baos
						.toByteArray()));

				ByteBuffer data = ByteBuffer.allocate(4096);
				SPN_MainActivity.MainThread.selector.select();

				SPN_MainActivity.MainThread.channel.read(data);

				bais = new ByteArrayInputStream(data.array());

				ois = new ObjectInputStream(bais);

				SPN_Object inspn = (SPN_Object) ois.readObject();

				InMessage = inspn.Message;
				if (InMessage.equals(SPN_Preferense.SuccessState)) {
					handler.sendEmptyMessage(3);
				} else {
					handler.sendEmptyMessage(4);
				}
			}

			SPN_MainActivity.progressDialog.dismiss();
		} catch (Exception e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
			handler.sendEmptyMessageDelayed(2, 3000);
		}
	}

	public void TransChildGpsState() {
		try {
			baos = new ByteArrayOutputStream();
			out = new ObjectOutputStream(baos);

			SPN_Object spn = new SPN_Object(SPN_MainActivity.MainPhoneInfoMy,
					null, null, SPN_Preferense.TransChildGpsState);

			Object obc = spn;

			out.writeObject(obc);

			SPN_MainActivity.MainThread.channel.write(ByteBuffer.wrap(baos
					.toByteArray()));

			ByteBuffer data = ByteBuffer.allocate(1024);
			SPN_MainActivity.MainThread.selector.select();

			SPN_MainActivity.MainThread.channel.read(data);

			bais = new ByteArrayInputStream(data.array());

			ois = new ObjectInputStream(bais);

			SPN_Object inspn = (SPN_Object) ois.readObject();

			InMessage = inspn.Message;
			if (InMessage.equals(SPN_Preferense.SuccessState)) {
				handler.sendEmptyMessage(3);
			} else {
				handler.sendEmptyMessage(4);
			}
			SPN_MainActivity.progressDialog.dismiss();
		} catch (Exception e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
			handler.sendEmptyMessageDelayed(10, 3000);
		}
	}

	public void NoReqGpsState01() {
		try {
			baos = new ByteArrayOutputStream();
			out = new ObjectOutputStream(baos);

			SPN_Object spn = new SPN_Object(null, null, null,
					SPN_Preferense.NoReqGpsState01);

			Object obc = spn;

			out.writeObject(obc);

			SPN_MainActivity.MainThread.channel.write(ByteBuffer.wrap(baos
					.toByteArray()));

			ByteBuffer data = ByteBuffer.allocate(1024);
			SPN_MainActivity.MainThread.selector.select();

			SPN_MainActivity.MainThread.channel.read(data);

			bais = new ByteArrayInputStream(data.array());

			ois = new ObjectInputStream(bais);

			SPN_Object inspn = (SPN_Object) ois.readObject();

			InMessage = inspn.Message;
			if (InMessage.equals(SPN_Preferense.SuccessState)) {
				handler.sendEmptyMessage(3);
			} else {
				handler.sendEmptyMessage(4);
			}

			SPN_MainActivity.progressDialog.dismiss();
		} catch (Exception e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
			handler.sendEmptyMessageDelayed(8, 3000);
		}
	}

	public void NoReqGpsState02() {
		try {
			baos = new ByteArrayOutputStream();
			out = new ObjectOutputStream(baos);

			SPN_Object spn = new SPN_Object(SPN_MainActivity.MainPhoneInfoMy,
					null, null, SPN_Preferense.NoReqGpsState02);

			Object obc = spn;

			out.writeObject(obc);

			SPN_MainActivity.MainThread.channel.write(ByteBuffer.wrap(baos
					.toByteArray()));

			ByteBuffer data = ByteBuffer.allocate(1024);
			SPN_MainActivity.MainThread.selector.select();

			SPN_MainActivity.MainThread.channel.read(data);

			bais = new ByteArrayInputStream(data.array());

			ois = new ObjectInputStream(bais);

			SPN_Object inspn = (SPN_Object) ois.readObject();

			InMessage = inspn.Message;
			if (InMessage.equals(SPN_Preferense.SuccessState)) {
				handler.sendEmptyMessage(3);
			} else {
				handler.sendEmptyMessage(4);
			}

			SPN_MainActivity.progressDialog.dismiss();
		} catch (Exception e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
			handler.sendEmptyMessageDelayed(11, 3000);
		}
	}

	public class MyAdapter extends ArrayAdapter {

		private Activity m_context;
		private ArrayList<String> m_arrayList;
		private ArrayList<LinearLayout> abc;

		public MyAdapter(Activity context, int textViewResourceId,
				ArrayList<String> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			m_context = context;
			m_arrayList = objects;
			//
			setCheckBoxList();
		}

		private void setCheckBoxList() {
			abc = new ArrayList<LinearLayout>();

			for (int i = 0; i < m_arrayList.size(); i++) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				LinearLayout a1 = (LinearLayout) vi.inflate(
						R.layout.listview_example, null);

				a1.setGravity(Gravity.CENTER);
				ImageView imv = new ImageView(m_context);
				TextView tv = new TextView(m_context);

				tv.setFocusable(false);
				tv.setClickable(false);
				DisplayMetrics outMetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
				int pixel = (int) (1.9 * outMetrics.densityDpi);
				tv.setWidth(pixel);
				imv.setFocusable(false);
				imv.setClickable(false);

				String str[] = m_arrayList.get(i).split("/");

				imv.setImageResource(Integer.parseInt(str[0]));

				tv.setText(str[1]);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
				a1.addView(imv);
				a1.addView(tv);
				abc.add(a1);
			}

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			// return m_imagelist.get(position);
			return abc.get(position);
		}

	}

}
