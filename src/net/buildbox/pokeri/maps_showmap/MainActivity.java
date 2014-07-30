package net.buildbox.pokeri.maps_showmap;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;
import android.graphics.Color;

//--------------------------------------------------------------------------------------------	
//現在地表示関連
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.navdrawer.SimpleSideDrawer;

import android.app.ActionBar;
import android.content.Context;
import android.location.Location;
import static java.lang.Math.*;

//--------------------------------------------------------------------------------------------	

public class MainActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
		OnMyLocationButtonClickListener {

	//--------------------------------------------------------------------------------------------	
	//	クラス内共用変数の作成

	private FragmentManager fragmentManager;
	private SupportMapFragment fragment;
	private GoogleMap map;
	private CameraPosition.Builder builder;
	private MarkerOptions options;
	private Marker marker[] = new Marker[3];
	private LatLng pointarray[] = new LatLng[3];
	private int mflg = 0;
	private int cflg = 0;
	private String item;
	private Circle circle;
	
	private SimpleSideDrawer mNav;

	double oY = 0; // �ｿｽO�ｿｽS�ｿｽ�ｿｽlat
	double oX = 0; // �ｿｽO�ｿｽS�ｿｽ�ｿｽlng
	int distance = 0; // �ｿｽO�ｿｽﾚ円�ｿｽﾌ費ｿｽ�ｿｽa

	private LocationClient mLocationClient;

	//--------------------------------------------------------------------------------------------
	//	マップ関連の処理
	/**
	 * 4つのピンを立てる
	 * ピンを立てて囲った範囲の色を変える
	 * ぐるなび等のAPIと連携して検索する
	 * 検索した場所にピンを立てる
	 */
    
    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000) // 5 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mNav = new SimpleSideDrawer(this);
        mNav.setLeftBehindContentView(R.layout.activity_behind_left_simple);
        findViewById(R.id.leftBtn).setOnClickListener(new OnClickListener() {
            @Override 
            public void onClick(View v) {
                mNav.toggleLeftDrawer();
            }
        });

		fragmentManager = getSupportFragmentManager();
		fragment = (SupportMapFragment) fragmentManager
				.findFragmentById(R.id.fragmentMap);

		// GoogleMapのインスタンス取得
		map = fragment.getMap();
		// 表示位置（東京駅）の生成
		LatLng posMapPoint = new LatLng(35.681382, 139.766084);
		// 東京駅を表示
		builder = new CameraPosition.Builder();
		// ピンの設定
		options = new MarkerOptions();

		// 現在地取得を許可
	    map.setMyLocationEnabled(true);
	    // 現在地ボタンタッチイベントを取得する
	    map.setOnMyLocationButtonClickListener(this);
	    // Location Serviceを使用するため、LocationClientクラスのインスタンスを生成する
        mLocationClient = new LocationClient(
                getApplicationContext(),
                this,  // ConnectionCallbacks
                this); // OnConnectionFailedListener

		MapsInitializer.initialize(this);

		// 東京駅を表示
		builder.target(posMapPoint);	// カメラの表示位置の指定
		builder.zoom(13.0f);	// カメラのズームレベルの指定
		builder.bearing(0);		// カメラの向きの指定
		builder.tilt(25.0f);	// カメラの傾きの指定
		map.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));

		// ピン上の情報がクリックされた時のイベント処理
		//		map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
		//			@Override
		//			public void onInfoWindowClick(Marker marker) {
		//				Toast.makeText(getApplicationContext(), "東京駅がクリックされました。", Toast.LENGTH_SHORT).show();
		//			}
		//		});

		// マップ上のクリックイベント処理
		map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			@Override
		    public void onMapClick(LatLng point) {
		        Toast.makeText(getApplicationContext(),
		        		"クリックされた座標は " + point.latitude + ", " + point.longitude, Toast.LENGTH_SHORT).show();
				// 3�ｿｽﾂのマ�ｿｽ[�ｿｽJ�ｿｽ[�ｿｽ�ｿｽ�ｿｽ\�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽﾄゑｿｽ�ｿｽ�ｿｽﾎマ�ｿｽ[�ｿｽJ�ｿｽ[�ｿｽｶ撰ｿｽ�ｿｽ�ｿｽ�ｿｽﾈゑｿｽ

				if (mflg < 3) {
					pointarray[mflg] = point;
					// �ｿｽ\�ｿｽ�ｿｽ�ｿｽﾊ置�ｿｽi�ｿｽ^�ｿｽb�ｿｽv�ｿｽ�ｿｽ�ｿｽ黷ｽ�ｿｽ�ｿｽ�ｿｽW�ｿｽj�ｿｽﾌ撰ｿｽ�ｿｽ�ｿｽ
					options.position(new LatLng(pointarray[mflg].latitude,
							pointarray[mflg].longitude));
					// �ｿｽs�ｿｽ�ｿｽ�ｿｽﾌタ�ｿｽC�ｿｽg�ｿｽ�ｿｽ�ｿｽﾝ抵ｿｽ
					options.title("マーカー"+mflg+"座標は " + point.latitude + ", " + point.longitude);
					// �ｿｽs�ｿｽ�ｿｽ�ｿｽﾌ色�ｿｽﾌ設抵ｿｽ
					BitmapDescriptor icon = BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
					options.icon(icon);
					// �ｿｽs�ｿｽ�ｿｽ�ｿｽﾌ追会ｿｽ
					marker[mflg] = map.addMarker(options);
					// �ｿｽs�ｿｽ�ｿｽ�ｿｽｷ会ｿｽ�ｿｽ�ｿｽ�ｿｽﾅド�ｿｽ�ｿｽ�ｿｽb�ｿｽO�ｿｽﾂ能�ｿｽ�ｿｽ
					marker[mflg].setDraggable(true);
					// �ｿｽs�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽﾌカ�ｿｽE�ｿｽ�ｿｽ�ｿｽg�ｿｽ�ｿｽﾇ会ｿｽ
					mflg++;
				}
				if (mflg >= 3) {

					if (cflg == 0) {
						makeCircle();
						cflg = cflg + 1;
					}

				}
			}
		});

		map.setOnMarkerDragListener(new OnMarkerDragListener() {
			@Override
			public void onMarkerDrag(Marker marker2) {
				// TODO Auto-generated method stub
				// Toast.makeText(getApplicationContext(), "繝槭�ｼ繧ｫ繝ｼ繝峨Λ繝�繧ｰ荳ｭ",
				// Toast.LENGTH_SHORT).show();
				if (mflg >= 3) {
					// 繝峨Λ繝�繧ｰ蠕後�槭�ｼ繧ｫ繝ｼ蠎ｧ讓吝叙蠕�
					pointarray[0] = marker[0].getPosition();
					pointarray[1] = marker[1].getPosition();
					pointarray[2] = marker[2].getPosition();
					// 蜑阪�ｮ蜀�繧貞炎髯､縺怜�肴緒逕ｻ
					circle.remove();
					makeCircle();
				}

			}

			@Override
			public void onMarkerDragEnd(Marker marker2) {

				// TODO Auto-generated method stub
				// Toast.makeText(getApplicationContext(), "繝槭�ｼ繧ｫ繝ｼ繝峨Λ繝�繧ｰ邨ゆｺ�",
				// Toast.LENGTH_LONG).show();
			}

			@Override
			public void onMarkerDragStart(Marker marker2) {

				// TODO Auto-generated method stub
				// Toast.makeText(getApplicationContext(), "繝槭�ｼ繧ｫ繝ｼ繝峨Λ繝�繧ｰ髢句ｧ�",
				// Toast.LENGTH_LONG).show();
			}
		});

		// マップ上の長押しイベント処理
				map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
				    @Override
				    public void onMapLongClick(LatLng point) {
				    	
				    	CircleOptions circleOptions = new CircleOptions()
				        .center(new LatLng(point.latitude,point.longitude))
				        .radius(1000); 
				    	map.addCircle(circleOptions);
				    	
				    	Toast.makeText(getApplicationContext(),
				        		"長押しされた座標は " + point.latitude + ", " + point.longitude, Toast.LENGTH_SHORT).show();
				    }
				});

				//--------------------------------------------------------------------------------------------
//				スピナーの設定
				/**
				 * 検索ボックスを用意する（アクションバー？）
				 * ぐるなび等のAPIにキーワードとピンの座標を引き渡す
				 */
				
				String[] items = {"居酒屋", "カフェ", "観光"};
				
				Spinner spinnerGenre = (Spinner) findViewById(R.id.spinnerGenre);
				// アダプタにアイテムを追加
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						this,
						android.R.layout.simple_spinner_item,
						items);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// アダプタの設定
				spinnerGenre.setAdapter(adapter);
				// スピナーのタイトル設定
				spinnerGenre.setPrompt("ジャンルの選択");
				// ポジションの指定
				spinnerGenre.setSelection(0);
				
				spinnerGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view,
							int position, long id) {
						Spinner spnGenre = (Spinner) parent;
						item = (String) spnGenre.getItemAtPosition(position);

//				    	Toast.makeText(getApplicationContext(),"選択されたアイテムは " + item, Toast.LENGTH_SHORT).show();
					}
					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

				//--------------------------------------------------------------------------------------------
//				その他
				/**
				 * エリア設定のリセット
				 * 
				 */
				
				

	}

	// 荳臥せ縺ｮ螟匁磁蜀�謠冗判
	public void makeCircle() {

		double tmp[] = new double[6];
		// 螟門ｿ�繧定ｨ育ｮ励�ら炊螻医�ｯ譛ｪ讀懆ｨｼ繝ｻ繝ｻ繝ｻ
		tmp[0] = 2 * (pointarray[1].longitude - pointarray[0].longitude);
		tmp[1] = 2 * (pointarray[1].latitude - pointarray[0].latitude);
		tmp[2] = Math.pow(pointarray[0].longitude, 2)
				- Math.pow(pointarray[1].longitude, 2)
				+ Math.pow(pointarray[0].latitude, 2)
				- Math.pow(pointarray[1].latitude, 2);
		tmp[3] = 2 * (pointarray[2].longitude - pointarray[0].longitude);
		tmp[4] = 2 * (pointarray[2].latitude - pointarray[0].latitude);
		tmp[5] = Math.pow(pointarray[0].longitude, 2)
				- Math.pow(pointarray[2].longitude, 2)
				+ Math.pow(pointarray[0].latitude, 2)
				- Math.pow(pointarray[2].latitude, 2);
		// 螟門ｿ�縺ｮx蠎ｧ讓呻ｼ挈ongitude
		oX = ((tmp[1] * tmp[5]) - (tmp[4] * tmp[2]))
				/ ((tmp[0] * tmp[4]) - (tmp[3] * tmp[1]));
		// 螟門ｿ�縺ｮy蠎ｧ讓呻ｼ挈atitude
		oY = ((tmp[2] * tmp[3]) - (tmp[5] * tmp[0]))
				/ ((tmp[0] * tmp[4]) - (tmp[3] * tmp[1]));

		// double dx = Math.pow(oX - pointarray[0].longitude / 0.0091, 2);
		// double dy = Math.pow(oY - pointarray[0].latitude /0.0111, 2);

		// 螟門ｿ�縺ｮ蜊雁ｾ�繧定ｨ育ｮ励��
		double r = 6378.137; // 襍､驕灘濠蠕Ъkm]
		// 螟門ｿ�
		double dy1 = oY * PI / 180;
		double dx1 = oX * PI / 180;
		// 1蛟狗岼縺ｮ繝槭�ｼ繧ｫ繝ｼ
		double my1 = pointarray[0].latitude * PI / 180;
		double mx1 = pointarray[0].longitude * PI / 180;
		// 螟門ｿ�縺ｨ繝槭�ｼ繧ｫ繝ｼ縺ｮ霍晞屬[m]
		double dist = r
				* acos(sin(dy1) * sin(my1) + cos(dy1) * cos(my1)
						* cos(mx1 - dx1)) * 1000;

		distance = (int) dist;

		Log.d("螟門ｿ�縺ｮx蠎ｧ讓�", "" + oX);
		Log.d("螟門ｿ�縺ｮy蠎ｧ讓�", "" + oY);
		Log.d("蜊雁ｾ�", "" + distance);

		CircleOptions circleOptions = new CircleOptions()
				.center(new LatLng(oY, oX)).radius(distance)
				.strokeColor(Color.rgb(200, 0, 255))
				.fillColor(Color.argb(80, 200, 0, 255));
		circle = map.addCircle(circleOptions);
	};

	// Implementation of {@link LocationListener}.

	@Override
	public void onLocationChanged(Location location) {
		// Do nothing
	}

	// Callback called when connected to GCore. Implementation of {@link
	// ConnectionCallbacks}.
	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(REQUEST, this); // this =
																// LocationListener
	}

	// Callback called when disconnected from GCore. Implementation of {@link
	// ConnectionCallbacks}.
	@Override
	public void onDisconnected() {
		// Do nothing
	}

	// Implementation of {@link OnConnectionFailedListener}.
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Do nothing
	}

	@Override
	public boolean onMyLocationButtonClick() {
		Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
				.show();
		// Return false so that we don't consume the event and the default
		// behavior still occurs
		// (the camera animates to the user's current position).
		return false;
	}

	// --------------------------------------------------------------------------------------------
	// �A�N�V�����o�[�̌����{�b�N�X

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		// SearchView���Ăяo��
		final MenuItem searchMenu = menu.findItem(R.id.menu_search);
		final SearchView searchView = (SearchView) searchMenu.getActionView();

		// �����A�C�R���������{�b�N�X�̓����ɔz�u
		searchView.setIconifiedByDefault(false);

		// �����J�n�{�^����\��
		searchView.setSubmitButtonEnabled(true);

		// �������������͂����ۂ⌟�����s���ɌĂ΂�郊�X�i�[��ݒ�
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				// �A�N�V�����o�[���擾
				ActionBar actionBar = getActionBar();
				// �}�[�J�[�̍��W�i�[�p�ϐ�
				double latitude = oY;
				double longitude = oX;

				// �����L�[���[�h���^�C�g���ɐݒ�
				actionBar.setTitle(query);

				// �����L�[���[�h�����W�Ƌ���WebApi�ֈ����n���iposMapPoint�͔z��^�B4�_�܂Ƃ߂ēn���Ă���j
				// 3�_���ݒ肳��Ă��Ȃ��ꍇ�̓��b�Z�[�W��\������
				// if (mflg == 3) {
				// 3�_�̌��ݍ��W���i�[����
				// for (int i = 0 ; i < 3 ; i++){
				// currentPoint[i] = marker[i].getPosition();
				// }
				new MyAsyncTask(map, latitude, longitude, distance, item).execute(query);
				// }else{
				// Toast.makeText(getApplicationContext(),
				// "�����͈͂̎w�肪�s�����Ă��܂��B3�_�Ŏw�肵�Ă��������B",
				// Toast.LENGTH_SHORT).show();
				// }

				// �f�t�H���g�ŕ\�������\�t�g�E�F�A�L�[�{�[�h���\��
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(
						searchView.getWindowToken(), 0);

				// �����{�b�N�X�����
				searchMenu.collapseActionView();

				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// �����{�b�N�X�̓��e���ύX���ꂽ�ۂɎ��s
				return false;
			}
		});
		return true;
	}
}