package net.buildbox.pokeri.maps_showmap;

import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.AsyncTask;
import android.util.Log;

public class MyAsyncTask extends AsyncTask<String, Integer, String> {

	GoogleMap map = null;
	String genre;
	Marker marker = null;
	WebApi webApi = null;
	// double oY = 0; //�O�S��lat
	// double oX = 0; //�O�S��lng
	double latitude = 0;
	double longitude = 0;
	double distance = 0; // �O�ډ~�̔��a

	// �R���X�g���N�^�Ń��C���X���b�h��map���󂯎��

	public MyAsyncTask(GoogleMap map2, double latitude2, double longitude2,
			int distance2, String item2) {

		map = map2;
		latitude = latitude2;
		longitude = longitude2;
		distance = distance2;
		genre = item2;
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public String doInBackground(String... params) {

		// ---------------------------------------------------------------------------------------------

		// ---------------------------------------------------------------------------------------------

		// �g�p����WebAPI�̔���
		if (genre.equals("������")) {
			webApi = new Api_Gnavi();
		} else if (genre.equals("�ό�")) {
			// ����API�̃C���X�^���X��ϐ����uwebApi�v�ō쐬����
			webApi = new Api_YahooLocalSearch();
		}

		// �e��API�ւ̌����N�G���ƂȂ�URL���쐬
		String queryUrl = webApi.createUrl(params[0], String.valueOf(latitude),
				String.valueOf(longitude));

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
			result = webApi.getResult(in);

			// �擾����xml�e�L�X�g��onPostExcecute�Ɉ����n��
			return result;

		} catch (Exception e) {
			return e.toString();
		} finally {
			try {
				if (http != null)
					http.disconnect();
				if (in != null)
					in.close();
			} catch (Exception e) {
			}
		}
	}

	// @Override
	protected void onPostExecute(String src) {

		int m = 0;
		// if(marker[m] != null){
		// marker[m].remove();
		// m++;
		// }
		// �}�[�J�[�̃I�v�V�����p�C���X�^���X
		MarkerOptions options = new MarkerOptions();

		// 1�X��1�s�̌`�Ő؂�o���Ĕz��Ɋi�[
		String[] strAry = src.split("\n");

		for (int i = 0; i < strAry.length; i++) {

			// 1�X�܂̊e�p�����[�^��؂�o���Ĕz��Ɋi�[
			String[] strAry2 = strAry[i].split(",");
			for (int j = 0; j < strAry2.length; j++) {
			}

			// API�����lat,lng�̏��Ԃ��Ⴄ�̂ŁA���̑Ή��������
			// API���̐؂�o���^��API�N���X�̒��ɓ����ׂ����B
			// ���W�̒l��String����Double�Ɍ^�ϊ�

			double lat = 0;
			double lng = 0;

			try {
				if (webApi.getClass() == this
						.getClass()
						.getClassLoader()
						.loadClass("net.buildbox.pokeri.maps_showmap.Api_Gnavi")) {
					lat = Double.parseDouble(strAry2[2]);// lat
					lng = Double.parseDouble(strAry2[3]);// lng
				} else {
					lat = Double.parseDouble(strAry2[3]);// lng
					lng = Double.parseDouble(strAry2[2]);// lat
				}
			} catch (ClassNotFoundException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}

			// ���W���O�ډ~�͈͓̔����ǂ�������
			// �O�S�ƓX�̋���
			double r = 6378.137; // 赤道半径[km]
			// 店の座標
			double dy1 = lat * PI / 180;
			double dx1 = lng * PI / 180;
			// 外心の座標
			double my1 = latitude * PI / 180;
			double mx1 = longitude * PI / 180;
			// 外心と店の距離[m]
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
				// �s���̒

				// marker[m] = map.addMarker(options);
				marker = map.addMarker(options);
				// m++;
			}
		}
	}
}