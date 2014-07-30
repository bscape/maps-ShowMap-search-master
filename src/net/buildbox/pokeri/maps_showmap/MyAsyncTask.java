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
	// double oY = 0; //�ｿｽO�ｿｽS�ｿｽ�ｿｽlat
	// double oX = 0; //�ｿｽO�ｿｽS�ｿｽ�ｿｽlng
	double latitude = 0;
	double longitude = 0;
	double distance = 0; // �ｿｽO�ｿｽﾚ円�ｿｽﾌ費ｿｽ�ｿｽa

	// �ｿｽR�ｿｽ�ｿｽ�ｿｽX�ｿｽg�ｿｽ�ｿｽ�ｿｽN�ｿｽ^�ｿｽﾅ�ｿｽ�ｿｽC�ｿｽ�ｿｽ�ｿｽX�ｿｽ�ｿｽ�ｿｽb�ｿｽh�ｿｽ�ｿｽmap�ｿｽ�ｿｽ�ｿｽｯ趣ｿｽ�ｿｽ

	public MyAsyncTask(GoogleMap map2, double latitude2, double longitude2,
			int distance2, String item2) {

		map = map2;
		latitude = latitude2;
		longitude = longitude2;
		distance = distance2;
		genre = item2;
		// TODO 閾ｪ蜍慕函謌舌＆繧後◆繧ｳ繝ｳ繧ｹ繝医Λ繧ｯ繧ｿ繝ｼ繝ｻ繧ｹ繧ｿ繝�
	}

	@Override
	public String doInBackground(String... params) {

		// ---------------------------------------------------------------------------------------------

		// ---------------------------------------------------------------------------------------------

		// �ｿｽg�ｿｽp�ｿｽ�ｿｽ�ｿｽ�ｿｽWebAPI�ｿｽﾌ費ｿｽ�ｿｽ�ｿｽ
		if (genre.equals("居酒屋")) {
			webApi = new Api_Gnavi();
		} else if (genre.equals("観光")) {
			// �ｿｽ�ｿｽ�ｿｽ�ｿｽAPI�ｿｽﾌイ�ｿｽ�ｿｽ�ｿｽX�ｿｽ^�ｿｽ�ｿｽ�ｿｽX�ｿｽ�ｿｽﾏ撰ｿｽ�ｿｽ�ｿｽ�ｿｽuwebApi�ｿｽv�ｿｽﾅ作成�ｿｽ�ｿｽ�ｿｽ�ｿｽ
			webApi = new Api_YahooLocalSearch();
		}

		// �ｿｽe�ｿｽ�ｿｽAPI�ｿｽﾖの鯉ｿｽ�ｿｽ�ｿｽ�ｿｽN�ｿｽG�ｿｽ�ｿｽ�ｿｽﾆなゑｿｽURL�ｿｽ�ｿｽ�ｿｽ�成
		Log.d("createUrl","params[0]="+params[0]+"lat="+String.valueOf(latitude)+"lng="+String.valueOf(longitude));
		String queryUrl = webApi.createUrl(params[0], String.valueOf(latitude),String.valueOf(longitude));

		// ---------------------------------------------------------------------------------------------
		HttpURLConnection http = null;
		InputStream in = null;
		URL url = null;
		String result = "";

		try {
			// URL�ｿｽ�ｿｽHTTP�ｿｽﾚ托ｿｽ
			url = new URL(queryUrl);
			http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("GET");
			http.connect();
			// InputStream�ｿｽ^�ｿｽﾏ撰ｿｽin�ｿｽﾉデ�ｿｽ[�ｿｽ^�ｿｽ�ｿｽ�ｿｽ_�ｿｽE�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ[�ｿｽh
			in = http.getInputStream();

			// �ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽﾊゑｿｽxml�ｿｽ�ｿｽ�ｿｽ�ｿｽK�ｿｽv�ｿｽﾈパ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ[�ｿｽ^�ｿｽ�ｿｽﾘゑｿｽo�ｿｽ�ｿｽ
			result = webApi.getResult(in);

			// �ｿｽ謫ｾ�ｿｽ�ｿｽ�ｿｽ�ｿｽxml�ｿｽe�ｿｽL�ｿｽX�ｿｽg�ｿｽ�ｿｽonPostExcecute�ｿｽﾉ茨ｿｽ�ｿｽ�ｿｽ�ｿｽn�ｿｽ�ｿｽ
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
		// �ｿｽ}�ｿｽ[�ｿｽJ�ｿｽ[�ｿｽﾌオ�ｿｽv�ｿｽV�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽp�ｿｽC�ｿｽ�ｿｽ�ｿｽX�ｿｽ^�ｿｽ�ｿｽ�ｿｽX
		MarkerOptions options = new MarkerOptions();

		// 1�ｿｽX�ｿｽ�ｿｽ1�ｿｽs�ｿｽﾌ形�ｿｽﾅ切ゑｿｽo�ｿｽ�ｿｽ�ｿｽﾄ配�ｿｽ�ｿｽﾉ格�ｿｽ[
		String[] strAry = src.split("\n");

		for (int i = 0; i < strAry.length; i++) {

			// 1�ｿｽX�ｿｽﾜの各�ｿｽp�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ[�ｿｽ^�ｿｽ�ｿｽﾘゑｿｽo�ｿｽ�ｿｽ�ｿｽﾄ配�ｿｽ�ｿｽﾉ格�ｿｽ[
			String[] strAry2 = strAry[i].split(",");
			for (int j = 0; j < strAry2.length; j++) {
			}

			// API�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽlat,lng�ｿｽﾌ擾ｿｽ�ｿｽﾔゑｿｽ�ｿｽ痰､�ｿｽﾌで、�ｿｽ�ｿｽ�ｿｽﾌ対会ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ
			// API�ｿｽ�ｿｽ�ｿｽﾌ切ゑｿｽo�ｿｽ�ｿｽ�ｿｽ^�ｿｽ�ｿｽAPI�ｿｽN�ｿｽ�ｿｽ�ｿｽX�ｿｽﾌ抵ｿｽ�ｿｽﾉ難ｿｽ�ｿｽ�ｿｽ�ｿｽﾗゑｿｽ�ｿｽ�ｿｽ�ｿｽB
			// �ｿｽ�ｿｽ�ｿｽW�ｿｽﾌ値�ｿｽ�ｿｽString�ｿｽ�ｿｽ�ｿｽ�ｿｽDouble�ｿｽﾉ型�ｿｽﾏ奇ｿｽ

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
				// TODO �ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ黷ｽ catch �ｿｽu�ｿｽ�ｿｽ�ｿｽb�ｿｽN
				e.printStackTrace();
			}

			// �ｿｽ�ｿｽ�ｿｽW�ｿｽ�ｿｽ�ｿｽO�ｿｽﾚ円�ｿｽﾌ範囲難ｿｽ�ｿｽ�ｿｽ�ｿｽﾇゑｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ
			// �ｿｽO�ｿｽS�ｿｽﾆ店�ｿｽﾌ具ｿｽ�ｿｽ�ｿｽ
			double r = 6378.137; // 襍､驕灘濠蠕Ъkm]
			// 蠎励�ｮ蠎ｧ讓�
			double dy1 = lat * PI / 180;
			double dx1 = lng * PI / 180;
			// 螟門ｿ�縺ｮ蠎ｧ讓�
			double my1 = latitude * PI / 180;
			double mx1 = longitude * PI / 180;
			// 螟門ｿ�縺ｨ蠎励�ｮ霍晞屬[m]
			double distance2 = r
					* acos(sin(dy1) * sin(my1) + cos(dy1) * cos(my1)
							* cos(mx1 - dx1)) * 1000;

			// �ｿｽO�ｿｽS�ｿｽﾆ店�ｿｽﾌ具ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽa�ｿｽﾈ難ｿｽ�ｿｽﾅゑｿｽ�ｿｽ�ｿｽﾎ外�ｿｽﾚ円�ｿｽﾌ難ｿｽ�ｿｽ�ｿｽ�ｿｽB�ｿｽ�ｿｽ�ｿｽa�ｿｽﾈ擾ｿｽﾈゑｿｽO�ｿｽ�ｿｽ�ｿｽB
			if (distance2 <= distance) {

				// �ｿｽ\�ｿｽ�ｿｽ�ｿｽﾊ置�ｿｽｶ撰ｿｽ
				// �ｿｽﾉ端�ｿｽﾉ搾ｿｽ�ｿｽW�ｿｽ�ｿｽ�ｿｽﾟゑｿｽ�ｿｽ鼾�ｿｽﾍ後か�ｿｽ逅ｶ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ黷ｽ�ｿｽs�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽﾌピ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ繽托ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ轤ｵ�ｿｽ�ｿｽ�ｿｽB�ｿｽu�ｿｽ謔ｩ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽv�ｿｽﾅ鯉ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽ�ｿｽﾆわか�ｿｽ�ｿｽB
				LatLng posMapPoint = new LatLng(lat, lng);

				// �ｿｽs�ｿｽ�ｿｽ�ｿｽﾆタ�ｿｽC�ｿｽg�ｿｽ�ｿｽ�ｿｽi�ｿｽX�ｿｽ�ｿｽ�ｿｽj�ｿｽﾌ設抵ｿｽ
				options.position(posMapPoint);
				options.title(strAry2[1]);
				// �ｿｽs�ｿｽ�ｿｽ�ｿｽﾌ�

				// marker[m] = map.addMarker(options);
				marker = map.addMarker(options);
				// m++;
			}
		}
	}
}