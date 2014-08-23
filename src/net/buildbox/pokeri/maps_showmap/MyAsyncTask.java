package net.buildbox.pokeri.maps_showmap;

import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.content.Context;

public class MyAsyncTask extends AsyncTask<String, Integer, String> {

	// �������ʗp�̃}�[�J�[
	static Marker resultMarker[] = new Marker[1];

	Context context = null;
	GoogleMap map = null;
	String genre = null;
	Api_Gnavi Api_Gnavi = new Api_Gnavi();
	double latitude = 0;
	double longitude = 0;
	double distance = 0; // �O�ډ~�̔��a
	
	private ArrayAdapter<String> arrayAdapter = null; //�@sideview��list�ǉ��p�A�_�v�^

	// �R���X�g���N�^�Ń��C���X���b�h�iMainActivity.java�j��map��O�S���W�����󂯎��

	public MyAsyncTask(GoogleMap tmp_map, double tmp_latitude, double tmp_longitude,
			int tmp_distance, String tmp_genre, Context tmp_con, ArrayAdapter<String> tmp_arrayAdapter) {

		map = tmp_map;
		latitude = tmp_latitude;
		longitude = tmp_longitude;
		distance = tmp_distance;
		genre = tmp_genre;
		context = tmp_con;
		arrayAdapter = tmp_arrayAdapter;
	}

	@Override
	public String doInBackground(String... params) {

		// ---------------------------------------------------------------------------------------------

		// �����N�G���ƂȂ�URL���쐬
		String queryUrl = Api_Gnavi.createUrl(params[0], String.valueOf(latitude),String.valueOf(longitude),genre);

		// ---------------------------------------------------------------------------------------------
		HttpURLConnection http = null;
		InputStream in = null;
		URL url = null;
		String result = "";

		try {
			// URL��HTTP�ڑ�
			url = new URL(queryUrl);
			http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("GET");
			http.connect();
			// InputStream�^�ϐ�in�Ƀf�[�^���_�E�����[�h
			in = http.getInputStream();
			// �������ʂ�xml����K�v�ȃp�����[�^��؂�o��
			result = Api_Gnavi.getResult(in);
			// �擾����xml�e�L�X�g��onPostExcecute�Ɉ����n��
			if (result.equals("")) return "no result";
			return result;
		} catch (Exception e) {
			Log.d("�ʐM�G���[", ""+e.toString());
			return "communication error";
		} finally {
			try {
				if (http != null) http.disconnect();
				if (in != null) in.close();
			} catch (Exception e) {
			}
		}
	}

	// @Override
	protected void onPostExecute(String src) {

		if (src.equals("communication error")){
			 Toast.makeText(context.getApplicationContext(),
			 "�ʐM�G���[�ł��B",
			 Toast.LENGTH_SHORT).show();
			return;
		}
		if (src.equals("no result")){
			 Toast.makeText(context.getApplicationContext(),
			 "�������ʂ�0���ł����B",
			 Toast.LENGTH_SHORT).show();
			return;
		}
		
		double lat = 0;
		double lng = 0;

		// ���O�̌������ʂ�j��
		for (int i = 0; i < resultMarker.length; i++){
			if (resultMarker[i] != null){
				resultMarker[i].remove();
			}
		}
		// �}�[�J�[�̃I�v�V�����p�C���X�^���X
		MarkerOptions options = new MarkerOptions();
		// 1�X��1�s�̌`�Ő؂�o���Ĕz��Ɋi�[
		String[] strAry = src.split("\n");
		// �}�[�J�[�i�[�p�̕ϐ��i�̃C���X�^���X�j��V�K�쐬
		resultMarker = new  Marker[strAry.length];
		//�@sideview�̈ꗗ�N���A
		arrayAdapter.clear();

		for (int i = 0; i < strAry.length; i++) {

			// 1�X�܂̊e�p�����[�^��؂�o���Ĕz��Ɋi�[
			String[] strAry2 = strAry[i].split(",");
			for (int j = 0; j < strAry2.length; j++) {
			}

			lat = 0;
			lng = 0;

			try {
				if (Api_Gnavi.getClass() == this
						.getClass()
						.getClassLoader()
						.loadClass("net.buildbox.pokeri.maps_showmap.Api_Gnavi")) {
					lat = Double.parseDouble(strAry2[2]);
					lng = Double.parseDouble(strAry2[3]);
				} else {
					lat = Double.parseDouble(strAry2[3]);
					lng = Double.parseDouble(strAry2[2]);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			//���W���O�ډ~�͈͓̔����ǂ�������
	        //�O�S�ƓX�̋���
			double r = 6378.137; // 赤道半径[km]
			// 店�?�座�?
			double dy1 = lat * PI / 180;
			double dx1 = lng * PI / 180;
			// 外�?の座�?
			double my1 = latitude * PI / 180;
			double mx1 = longitude * PI / 180;
			// 外�?と店�?�距離[m]
			double distance2 = r
					* acos(sin(dy1) * sin(my1) + cos(dy1) * cos(my1)
							* cos(mx1 - dx1)) * 1000;

			// �O�S�ƓX�̋��������a�ȓ��ł���ΊO�ډ~�̓����B���a�ȏ�Ȃ�O���B
			if (distance2 <= distance) {
				// �\���ʒu�𐶐�
				// �ɒ[�ɍ��W���߂��ꍇ�͌ォ�琶�����ꂽ�s���������̃s�����㏑������炵���B�u�悩�����v�Ō�������Ƃ킩��B
				LatLng posMapPoint = new LatLng(lat, lng);
				// �s���ƃ^�C�g���i�X���j�̐ݒ�
				options.position(posMapPoint);
				options.title(strAry2[1]);
				// �s����n�}��ɒǉ�
				resultMarker[i] = map.addMarker(options);
				
				//�@���X�̖��O�AURL�A�d�b�ԍ���sideview�ɒǉ��B
				arrayAdapter.add(strAry2[1]+"\n"+strAry2[4]+"\n"+strAry2[5]+"\n");
			}
		}
	}
}