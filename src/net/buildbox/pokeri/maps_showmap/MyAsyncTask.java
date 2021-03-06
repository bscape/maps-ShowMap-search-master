package net.buildbox.pokeri.maps_showmap;

import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.buildbox.pokeri.maps_showmap.MainActivity.ListAdapter;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

public class MyAsyncTask extends AsyncTask<String, Integer, String> {

	// 検索結果用のマーカー
	static Marker resultMarker[] = new Marker[1];

	Context context = null;
	GoogleMap map = null;
	String genre = null;
	Api_Gnavi Api_Gnavi = new Api_Gnavi();
	double latitude = 0;
	double longitude = 0;
	double distance = 0; // 外接円の半径
    public  ProgressDialog m_ProgressDialog = null;
	
	//private ArrayAdapter<ItemBean> arrayAdapter = null; //　sideviewのlist追加用アダプタ
    private ListAdapter arrayAdapter = null;
    
	// コンストラクタでメインスレッド（MainActivity.java）のmapや外心座標等を受け取る

	public MyAsyncTask(GoogleMap tmp_map, double tmp_latitude, double tmp_longitude,
			int tmp_distance, String tmp_genre, Context tmp_con, ListAdapter tmp_arrayAdapter) {

		map = tmp_map;
		latitude = tmp_latitude;
		longitude = tmp_longitude;
		distance = tmp_distance;
		genre = tmp_genre;
		context = tmp_con;
		arrayAdapter = tmp_arrayAdapter;
		
	}

	ProgressDialog dlg = null;
	
	// 検索中のダイアログを表示
	@Override
	public void onPreExecute() {
		dlg = new ProgressDialog(context);
		dlg.setIndeterminate(true);
		dlg.setMessage("検索中");
		dlg.show();
	}
	
	// ダイアログのキャンセル処理
//	@Override
//	public void onCancelled(){
//		dlg.dismiss();
//	}
		 
	@Override
	public String doInBackground(String... params) {

		// ---------------------------------------------------------------------------------------------

		// 検索クエリとなるURLを作成
		String queryUrl = Api_Gnavi.createUrl(params[0], String.valueOf(latitude),String.valueOf(longitude),genre);

		// ---------------------------------------------------------------------------------------------
				
		HttpURLConnection http = null;
		InputStream in = null;
		URL url = null;
		String result = "";

		try {
			// URLにHTTP接続
			url = new URL(queryUrl);
			http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("GET");
			http.connect();
			// InputStream型変数inにデータをダウンロード
			in = http.getInputStream();
			// 検索結果のxmlから必要なパラメータを切り出す
			result = Api_Gnavi.getResult(in);
			// 検索終了と共に進捗ダイアログを画面から削除
			dlg.dismiss();
			// 取得したxmlテキストをonPostExcecuteに引き渡す
			if (result.equals("")) return "no result";
			return result;
		} catch (Exception e) {
			Log.d("通信エラー", ""+e.toString());
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
			 "通信エラーです。",
			 Toast.LENGTH_SHORT).show();
			return;
		}
		if (src.equals("no result")){
			 Toast.makeText(context.getApplicationContext(),
			 "検索結果は0件でした。",
			 Toast.LENGTH_SHORT).show();
			return;
		}
		
		double lat = 0;
		double lng = 0;

		// 直前の検索結果を破棄
		for (int i = 0; i < resultMarker.length; i++){
			if (resultMarker[i] != null){
				resultMarker[i].remove();
			}
		}
		// マーカーのオプション用インスタンス
		MarkerOptions options = new MarkerOptions();
		// 1店舗1行の形で切り出して配列に格納
		String[] strAry = src.split("\n");
		// マーカー格納用の変数（のインスタンス）を新規作成
		resultMarker = new  Marker[strAry.length];
		//　sideviewの一覧クリア
		arrayAdapter.clear();
		// 表示できる検索結果があったかどうかのフラグ
		boolean markerFlg = false;
		
		
		ArrayList<ItemBean> list = new ArrayList<ItemBean>();

		for (int i = 0; i < strAry.length; i++) {

			// 1店舗の各パラメータを切り出して配列に格納
			String[] strAry2 = strAry[i].split(",,");
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

			//座標が外接円の範囲内かどうか判定
	        //外心と店の距離
			double r = 6378.137; // 襍､驕灘濠蠕Ъkm]
			// 蠎励?ｮ蠎ｧ讓?
			double dy1 = lat * PI / 180;
			double dx1 = lng * PI / 180;
			// 螟門ｿ?縺ｮ蠎ｧ讓?
			double my1 = latitude * PI / 180;
			double mx1 = longitude * PI / 180;
			// 螟門ｿ?縺ｨ蠎励?ｮ霍晞屬[m]
			double distance2 = r
					* acos(sin(dy1) * sin(my1) + cos(dy1) * cos(my1)
							* cos(mx1 - dx1)) * 1000;

			// 外心と店の距離が半径以内であれば外接円の内部。半径以上なら外部。
			if (distance2 <= distance) {
				// 表示位置を生成
				// 極端に座標が近い場合は後から生成されたピンが既存のピンを上書きするらしい。
				LatLng posMapPoint = new LatLng(lat, lng);
				// ピンとタイトル（店名）の設定
				options.position(posMapPoint);
				options.title(strAry2[1]);
				// ピンを地図上に追加
				resultMarker[i] = map.addMarker(options);
				//　お店の名前、URL、電話番号をsideviewに追加。
				//arrayAdapter.add(strAry2[1]+"\n"+strAry2[4]+"\n"+strAry2[5]+"\n");
				//ItemBean item = new ItemBean();
				ItemBean item = new ItemBean();
				item.setName(strAry2[1]);
				item.setUrl(strAry2[4]);
				item.setRange(distance2);
				//ArrayList<ItemBean> list = new ArrayList<ItemBean>();
				list.add(item);
				//ListAdapter adapter = new ListAdapter(getApplicationContext(),list);
				//listview.setadapter(adapter);
				// 「表示できる検索結果があった」ことのフラグを立てる
				//arrayAdapter.add(item);
				markerFlg = true;
			}
		}
		Collections.sort(list, new Rangesort());
		Iterator<ItemBean> it = list.iterator();
        while (it.hasNext()) {
            ItemBean ib = it.next();
            arrayAdapter.add(ib);
        }
		
		if (! markerFlg){
			 Toast.makeText(context.getApplicationContext(),
			 "検索結果は0件でした。",
			 Toast.LENGTH_SHORT).show();
		}
		
	}
}

class Rangesort implements Comparator<ItemBean>{
    //　比較メソッド（データクラスを比較して-1, 0, 1を返すように記述する）
    public int compare(ItemBean a, ItemBean b) {
        double no1 = a.getRange();
        double no2 = b.getRange();

        //　距離順でソート
        if (no1 > no2) {
            return 1;

        } else if (no1 == no2) {
            return 0;

        } else {
            return -1;

        }
    }
}

