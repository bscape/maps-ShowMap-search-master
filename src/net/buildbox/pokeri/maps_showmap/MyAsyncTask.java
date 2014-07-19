package net.buildbox.pokeri.maps_showmap;

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
	LatLng[] selectedArea = new LatLng[3];
	String genre;
	Marker marker = null;
    WebApi webApi = null;
    double oY = 0; //外心のlat
    double oX = 0; //外心のlng
    double distance = 0; //外接円の半径
	
	//コンストラクタでメインスレッドのmapを受け取る
		public MyAsyncTask(GoogleMap tmp, LatLng[] tmp2, String tmp3){
			map = tmp;
			selectedArea = tmp2;
			genre = tmp3;
		}
	
	@Override
	public String doInBackground(String... params) {
		
//---------------------------------------------------------------------------------------------
        //クエリに送る位置座標を決めるため、四角形の最小包含円の中心座標を求める（選択エリアの中心を元に、半径xxキロ内の店を検索、という形で結果を絞る）
        //任意の3頂点の外接円を求め、残り1点が内部にあればOK。なければ別の3点の組み合わせで試す
        //２次元平面の想定で計算しているため、四角形が日付変更線をまたいでいる場合は正確な結果を出せない
        
        double tmp[] = new double[6];

        
	    //外心を計算。理屈は未検証・・・
        tmp[0] = 2 * (selectedArea[1].longitude - selectedArea[0].longitude);
        tmp[1] = 2 * (selectedArea[1].latitude - selectedArea[0].latitude);
        tmp[2] = Math.pow(selectedArea[0].longitude,2) - Math.pow(selectedArea[1].longitude,2) + Math.pow(selectedArea[0].latitude,2) - Math.pow(selectedArea[1].latitude,2);
        tmp[3] = 2 * (selectedArea[2].longitude - selectedArea[0].longitude);
        tmp[4] = 2 * (selectedArea[2].latitude - selectedArea[0].latitude);
        tmp[5] = Math.pow(selectedArea[0].longitude,2) - Math.pow(selectedArea[2].longitude,2) + Math.pow(selectedArea[0].latitude,2) - Math.pow(selectedArea[2].latitude,2);
        //外心のx座標＝longitude
        oX = ((tmp[1] * tmp[5]) - (tmp[4] * tmp[2])) / ((tmp[0] * tmp[4]) - (tmp[3] * tmp[1]));
        //外心のy座標＝latitude
        oY = ((tmp[2] * tmp[3]) - (tmp[5] * tmp[0])) / ((tmp[0] * tmp[4]) - (tmp[3] * tmp[1]));
        
        //外心の半径
        double dx = Math.pow(oX - selectedArea[0].longitude,2);
        double dy = Math.pow(oY - selectedArea[0].latitude,2);
        distance = Math.sqrt(dx + dy);

        Log.d("外心のx座標",""+oX);
        Log.d("外心のy座標",""+oY);

//---------------------------------------------------------------------------------------------

        // 使用するWebAPIの判定
        if(genre.equals("居酒屋")){
        	webApi = new Api_Gnavi();
        }else if(genre.equals("観光")){
        	//他のAPIのインスタンスを変数名「webApi」で作成する
        	webApi = new Api_YahooLocalSearch();
        }
        
        // 各種APIへの検索クエリとなるURLを作成
        String queryUrl = webApi.createUrl(params[0],String.valueOf(oY),String.valueOf(oX));
    	
//---------------------------------------------------------------------------------------------
		HttpURLConnection http = null;
        InputStream in = null;
		URL url = null;
		String result = "";
        
        try {
			//URLにHTTP接続
        	url = new URL(queryUrl);
			http = (HttpURLConnection)url.openConnection();
			http.setRequestMethod("GET");
			http.connect();
			// InputStream型変数inにデータをダウンロード
			in = http.getInputStream();
			
			// 検索結果のxmlから必要なパラメータを切り出す
			result = webApi.getResult(in);

			//取得したxmlテキストをonPostExcecuteに引き渡す
			return result;

		} catch(Exception e) {
			return e.toString();
	    } finally {
	    	try {
	    		if(http != null)
	    			http.disconnect();
	    		if(in != null) 
	    			in.close();
	    	}catch (Exception e) {}
	    }
	}
	
//	@Override
	protected void onPostExecute(String src){

		//マーカーのオプション用インスタンス
		MarkerOptions options = new MarkerOptions();	
		Marker marker;
		
		//1店舗1行の形で切り出して配列に格納
		String[] strAry = src.split("\n");
		
		for (int i = 0 ; i < strAry.length ; i++) {

			//1店舗の各パラメータを切り出して配列に格納
			String[] strAry2 = strAry[i].split(",");
			for (int j = 0 ; j < strAry2.length ; j++) {
			}

			//API次第でlat,lngの順番が違うので、その対応も入れる
			//API毎の切り出し型もAPIクラスの中に入れるべきか。
			//座標の値をStringからDoubleに型変換

			double lat = 0;
			double lng = 0;
			
			try {
				if (webApi.getClass() == this.getClass().getClassLoader().loadClass("net.buildbox.pokeri.maps_showmap.Api_Gnavi")){
					lat = Double.parseDouble(strAry2[2]);//lat
					lng = Double.parseDouble(strAry2[3]);//lng
				}else {
					lat = Double.parseDouble(strAry2[3]);//lng
					lng = Double.parseDouble(strAry2[2]);//lat
				}
			} catch (ClassNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			
			//座標が外接円の範囲内かどうか判定
	        //外心と店の距離
	        double dx2 = Math.pow(oX - lng,2);
	        double dy2 = Math.pow(oY - lat,2);
	        double distance2 = Math.sqrt(dx2 + dy2);
	        
	        //外心と店の距離が半径以内であれば外接円の内部。半径以上なら外部。
	        if (distance2 <= distance) {

		    	// 表示位置を生成
				// 極端に座標が近い場合は後から生成されたピンが既存のピンを上書きするらしい。「よかたい」で検索するとわかる。
		    	LatLng posMapPoint = new LatLng(lat,lng);
		    	
				// ピンとタイトル（店名）の設定
				options.position(posMapPoint);
				options.title(strAry2[1]);
				// ピンの追加
				marker = map.addMarker(options);
	        }
		}
	}
}