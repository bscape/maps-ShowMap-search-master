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
	 * ３つのピンを立てる
	 * ピンを立てて囲った範囲の色を変える
	 * ぐるなび等のAPIと連携して検索する
	 * 検索した場所にピンを立てる
	 */
    
    // 位置情報の更新頻度・精度を設定する。
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
		// 初期表示位置（東京駅）の生成
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

		// マップ上のクリックイベント処理
		map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			@Override
		    public void onMapClick(LatLng point) {
		        Toast.makeText(getApplicationContext(),
		        		"クリックされた座標は " + point.latitude + ", " + point.longitude, Toast.LENGTH_SHORT).show();
		    	// 3つのマーカーが表示されていればマーカーを生成しない

				if (mflg < 3) {
					pointarray[mflg] = point;
					// 表示位置（タップされた座標）の取得
					options.position(new LatLng(pointarray[mflg].latitude,
							pointarray[mflg].longitude));
					// ピンのタイトルを設定
					options.title("マーカー"+mflg+"座標は " + point.latitude + ", " + point.longitude);
					// ピンの色を設定
					BitmapDescriptor icon = BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
					options.icon(icon);
					// ピンを地図上に追加
					marker[mflg] = map.addMarker(options);
					// ピンをドラッグ可能にする
					marker[mflg].setDraggable(true);
					// ピン数のカウントを追加
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
				// ドラッグ終了のリスナー。今回は何もしない。
			}

			@Override
			public void onMarkerDragStart(Marker marker2) {
				// ドラッグ開始のリスナー。今回は何もしない。
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
				/**
				 * 検索ボックスを用意する
				 * ぐるなび等のAPIにキーワードとピンの座標を引き渡す
				 */

				// スピナーの設定
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
				 */
	}

	// 地図上の選択範囲に円を重ねる
	public void makeCircle() {

		double tmp[] = new double[6];
		// 選択した３点を頂点とする三角形の外心を求める
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
		// 外心のX座標＝longitude＝経度
		oX = ((tmp[1] * tmp[5]) - (tmp[4] * tmp[2]))
				/ ((tmp[0] * tmp[4]) - (tmp[3] * tmp[1]));
		// 外心のY座標＝latitude＝緯度
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

	@Override
	public void onLocationChanged(Location location) {
		// Do nothing
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(REQUEST, this); // this =
																// LocationListener
	}

	@Override
	public void onDisconnected() {
		// Do nothing
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Do nothing
	}

	@Override
	public boolean onMyLocationButtonClick() {
		Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
				.show();
		return false;
	}

	// --------------------------------------------------------------------------------------------
	// アクションバーの検索ボックス

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		// SearchViewを呼び出す
		final MenuItem searchMenu = menu.findItem(R.id.menu_search);
		final SearchView searchView = (SearchView) searchMenu.getActionView();

		// 検索アイコンを検索ボックスの内側に配置
		searchView.setIconifiedByDefault(false);

		// 検索開始ボタンを表示
		searchView.setSubmitButtonEnabled(true);

		// 検索文字列を入力した際や、検索実行時に呼ばれる各種リスナーを設定
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				// アクションバーを取得
				ActionBar actionBar = getActionBar();
				// 外心の緯度・経度
				double latitude = oY;
				double longitude = oX;

				// 検索キーワードをタイトルに設定
				actionBar.setTitle(query);

				// 検索キーワードを座標と共にWebApiへ引き渡す
				// ３点が設定されていない場合はメッセージを表示する
				// if (mflg == 3) {
				// ３点の現在座標を格納する
				// for (int i = 0 ; i < 3 ; i++){
				// currentPoint[i] = marker[i].getPosition();
				// }
				
				// MyAsyncTaskクラスに座標・キーワードを引き渡し、検索を実行する
				new MyAsyncTask(map, latitude, longitude, distance, item).execute(query);
				// }else{
				// Toast.makeText(getApplicationContext(),
				// "検索範囲の指定が不足しています。3点で指定してください。",
				// Toast.LENGTH_SHORT).show();
				// }

				// デフォルトで表示されるソフトウェアキーボードを非表示にする
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(
						searchView.getWindowToken(), 0);

				// 検索ボックスを閉じる
				searchMenu.collapseActionView();

				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// 検索ボックスの内容が変更された際に実行
				return false;
			}
		});
		return true;
	}
}