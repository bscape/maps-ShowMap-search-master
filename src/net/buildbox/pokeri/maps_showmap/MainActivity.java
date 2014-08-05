package net.buildbox.pokeri.maps_showmap;

import java.util.ArrayList;

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
//���ݒn�\���֘A
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
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import static java.lang.Math.*;

//--------------------------------------------------------------------------------------------	

public class MainActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
		OnMyLocationButtonClickListener {

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

	double oY = 0; // ?��O?��S?��?��lat
	double oX = 0; // ?��O?��S?��?��lng
	int distance = 0; // ?��O?��ډ~?��̔�?��a

	private LocationClient mLocationClient;
    
    // �ʒu���̍X�V�p�x�E���x��ݒ肷��B
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000) // 5 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	//--------------------------------------------------------------------------------------------

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

		// GoogleMap�̃C���X�^���X�擾
		map = fragment.getMap();
		// �����\���ʒu�i�����w�j�̐���
		LatLng posMapPoint = new LatLng(35.681382, 139.766084);
		// �����w��\��
		builder = new CameraPosition.Builder();
		// �s���̐ݒ�
		options = new MarkerOptions();

		// ���ݒn�擾������
	    map.setMyLocationEnabled(true);
	    // ���ݒn�{�^���^�b�`�C�x���g���擾����
//	    map.setOnMyLocationButtonClickListener(this);
	    // Location Service���g�p���邽�߁ALocationClient�N���X�̃C���X�^���X�𐶐�����
        mLocationClient = new LocationClient(
                getApplicationContext(),
                this,  // ConnectionCallbacks
                this); // OnConnectionFailedListener
        mLocationClient.connect();

		MapsInitializer.initialize(this);

		// �ŐV�̌��ݒn���擾�o���Ȃ������ꍇ�A�����w�t�߂�\��������
//		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//		double lat = location.getLatitude();
//		double lng = location.getLongitude();
//		posMapPoint = new LatLng(lat, lng);
		builder.target(posMapPoint);	// �J�����̕\���ʒu�̎w��
		builder.zoom(13.0f);	// �J�����̃Y�[�����x���̎w��
		builder.bearing(0);		// �J�����̌����̎w��
		builder.tilt(25.0f);	// �J�����̌X���̎w��
		map.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));

		
		// �}�b�v��̃N���b�N�C�x���g����
		map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			@Override
		    public void onMapClick(LatLng point) {
		        Toast.makeText(getApplicationContext(),
		        		"�N���b�N���ꂽ���W�� " + point.latitude + ", " + point.longitude, Toast.LENGTH_SHORT).show();
		    	// 3�̃}�[�J�[���\������Ă���΃}�[�J�[�𐶐����Ȃ�

				if (mflg < 3) {
					pointarray[mflg] = point;
					// �\���ʒu�i�^�b�v���ꂽ���W�j�̎擾
					options.position(new LatLng(pointarray[mflg].latitude,
							pointarray[mflg].longitude));
					// �s���̃^�C�g����ݒ�
					options.title("�}�[�J�["+mflg+"���W�� " + point.latitude + ", " + point.longitude);
					// �s���̐F��ݒ�
					BitmapDescriptor icon = BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
					options.icon(icon);
					// �s����n�}��ɒǉ�
					marker[mflg] = map.addMarker(options);
					// �s�����h���b�O�\�ɂ���
					marker[mflg].setDraggable(true);
					// �s�����̃J�E���g��ǉ�
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
					// ドラ�?グ後�?��?�カー座標取�?
					pointarray[0] = marker[0].getPosition();
					pointarray[1] = marker[1].getPosition();
					pointarray[2] = marker[2].getPosition();
					// 前�?��?を削除し�?�描画
					circle.remove();
					makeCircle();
				}
			}

			@Override
			public void onMarkerDragEnd(Marker marker2) {
				// �h���b�O�I���̃��X�i�[�B����͉������Ȃ��B
			}

			@Override
			public void onMarkerDragStart(Marker marker2) {
				// �h���b�O�J�n�̃��X�i�[�B����͉������Ȃ��B
			}
		});

		// �}�b�v��̒������C�x���g����
				map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
				    @Override
				    public void onMapLongClick(LatLng point) {
				    	// Do Nothing
				    }
				});

				//--------------------------------------------------------------------------------------------
				/**
				 * �����{�b�N�X��p�ӂ���
				 * ����Ȃѓ���API�ɃL�[���[�h�ƃs���̍��W�������n��
				 */

				// �X�s�i�[�̐ݒ�
				String[] items = {"������","�ό�"};
				Spinner spinnerGenre = (Spinner) findViewById(R.id.spinnerGenre);
				// �A�_�v�^�ɃA�C�e����ǉ�
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						this,
						android.R.layout.simple_spinner_item,
						items);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// �A�_�v�^�̐ݒ�
				spinnerGenre.setAdapter(adapter);
				// �X�s�i�[�̃^�C�g���ݒ�
				spinnerGenre.setPrompt("�W�������̑I��");
				// �|�W�V�����̎w��
				spinnerGenre.setSelection(0);
				
				spinnerGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view,
							int position, long id) {
						Spinner spnGenre = (Spinner) parent;
						item = (String) spnGenre.getItemAtPosition(position);

//				    	Toast.makeText(getApplicationContext(),"�I�����ꂽ�A�C�e���� " + item, Toast.LENGTH_SHORT).show();
					}
					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

				//--------------------------------------------------------------------------------------------
//				���̑�
				/**
				 * �G���A�ݒ�̃��Z�b�g
				 */
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	// �n�}��̑I��͈͂ɉ~���d�˂�
	public void makeCircle() {

		double tmp[] = new double[6];
		// �I�������R�_�𒸓_�Ƃ���O�p�`�̊O�S�����߂�
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
		// �O�S��X���W��longitude���o�x
		oX = ((tmp[1] * tmp[5]) - (tmp[4] * tmp[2]))
				/ ((tmp[0] * tmp[4]) - (tmp[3] * tmp[1]));
		// �O�S��Y���W��latitude���ܓx
		oY = ((tmp[2] * tmp[3]) - (tmp[5] * tmp[0]))
				/ ((tmp[0] * tmp[4]) - (tmp[3] * tmp[1]));

		// double dx = Math.pow(oX - pointarray[0].longitude / 0.0091, 2);
		// double dy = Math.pow(oY - pointarray[0].latitude /0.0111, 2);

		// 外�?の半�?を計算�??
		double r = 6378.137; // 赤道半径[km]
		// 外�?
		double dy1 = oY * PI / 180;
		double dx1 = oX * PI / 180;
		// 1個目のマ�?�カー
		double my1 = pointarray[0].latitude * PI / 180;
		double mx1 = pointarray[0].longitude * PI / 180;
		// 外�?とマ�?�カーの距離[m]
		double dist = r
				* acos(sin(dy1) * sin(my1) + cos(dy1) * cos(my1)
						* cos(mx1 - dx1)) * 1000;

		distance = (int) dist;

		Log.d("外�?のx座�?", "" + oX);
		Log.d("外�?のy座�?", "" + oY);
		Log.d("半�?", "" + distance);

		CircleOptions circleOptions = new CircleOptions()
				.center(new LatLng(oY, oX)).radius(distance)
				.strokeColor(Color.rgb(200, 0, 255))
				.fillColor(Color.argb(80, 200, 0, 255));
		circle = map.addCircle(circleOptions);
	};

	@Override
	public void onLocationChanged(Location location) {
		// ���ݒn�Ɉړ�
		CameraPosition cameraPos = new CameraPosition.Builder()
		.target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(13.0f)
		.bearing(0).build();
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
        mLocationClient.removeLocationUpdates(this);
		
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

		// �������������͂����ۂ�A�������s���ɌĂ΂��e�탊�X�i�[��ݒ�
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				// �A�N�V�����o�[���擾
				ActionBar actionBar = getActionBar();
				// �O�S�̈ܓx�E�o�x
				double latitude = oY;
				double longitude = oX;

				// �����L�[���[�h���^�C�g���ɐݒ�
				actionBar.setTitle(query);

				// MyAsyncTask�N���X�ɍ��W�E�L�[���[�h�������n���A���������s����
				if (mflg == 3){
				new MyAsyncTask(map, latitude, longitude, distance, item, getApplicationContext()).execute(query);
				 }else{
				 Toast.makeText(getApplicationContext(),
				 "�����͈͂̎w�肪�s�����Ă��܂��B3�_�Ŏw�肵�Ă��������B",
				 Toast.LENGTH_SHORT).show();
				 }

				// �f�t�H���g�ŕ\�������\�t�g�E�F�A�L�[�{�[�h���\���ɂ���
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