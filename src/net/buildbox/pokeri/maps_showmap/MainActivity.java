package net.buildbox.pokeri.maps_showmap;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
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

import android.R.menu;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.SearchView.OnQueryTextListener;

//--------------------------------------------------------------------------------------------	
//現在地表示関連
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
//--------------------------------------------------------------------------------------------	

public class MainActivity extends FragmentActivity implements 
	ConnectionCallbacks,
	OnConnectionFailedListener,
	LocationListener,
	OnMyLocationButtonClickListener {

//--------------------------------------------------------------------------------------------	
//	クラス内共用変数の作成

	private FragmentManager fragmentManager;
	private SupportMapFragment fragment;
	private GoogleMap map;
	private CameraPosition.Builder builder;
	private MarkerOptions options;
	private Marker marker[] = new Marker[3];
	private int mflg = 0;
	private String item;

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
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		fragmentManager = getSupportFragmentManager();
		fragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.fragmentMap);

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
		    	// 3つのマーカーが表示されていればマーカーを生成しない
		    	if (mflg < 3) {
			    	// 表示位置（タップされた座標）の生成
					options.position(new LatLng(point.latitude, point.longitude));
					// ピンのタイトル設定
					options.title("マーカー"+mflg+"座標は " + point.latitude + ", " + point.longitude);
					// ピンの色の設定
					BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
					options.icon(icon);
					// ピンの追加
					marker[mflg] = map.addMarker(options);
					// ピンを長押しでドラッグ可能に
					marker[mflg].setDraggable(true);
					// ピン数のカウントを追加
					mflg++;
		    	}
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
//		スピナーの設定
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

//		    	Toast.makeText(getApplicationContext(),"選択されたアイテムは " + item, Toast.LENGTH_SHORT).show();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		//--------------------------------------------------------------------------------------------
//		その他
		/**
		 * エリア設定のリセット
		 * 
		 */
		
		
	}
	
	 // Implementation of {@link LocationListener}.
    @Override
    public void onLocationChanged(Location location) {
     // Do nothing
    }
    // Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
    @Override
    public void onConnected(Bundle connectionHint) {
        mLocationClient.requestLocationUpdates(REQUEST,this);  // this = LocationListener
    }
    // Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
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
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

//--------------------------------------------------------------------------------------------
//	アクションバーの検索ボックス
    
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

        // 検索文字列を入力した際や検索実行時に呼ばれるリスナーを設定
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // アクションバーを取得
                ActionBar actionBar = getActionBar();
                // マーカーの座標格納用変数
                LatLng currentPoint[] = new LatLng[3];

                // 検索キーワードをタイトルに設定
                actionBar.setTitle(query);
                
                // 検索キーワードを座標と共にWebApiへ引き渡す（posMapPointは配列型。4点まとめて渡している）
                // 3点が設定されていない場合はメッセージを表示する
                if (mflg == 3) {
                //3点の現在座標を格納する
	                for (int i = 0 ; i < 3 ; i++){
	                	currentPoint[i] = marker[i].getPosition();
	                }
	                new MyAsyncTask(map,currentPoint,item).execute(query);
                }else{
    		        Toast.makeText(getApplicationContext(),
    		        		"検索範囲の指定が不足しています。3点で指定してください。", Toast.LENGTH_SHORT).show();
                }
                
                // デフォルトで表示されるソフトウェアキーボードを非表示
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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