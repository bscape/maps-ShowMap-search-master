package net.buildbox.pokeri.maps_showmap;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.util.Log;

public class Api_Gnavi extends WebApi {
		
	//検索URLを作成する
	@Override
	protected String createUrl(String query, String lat, String lng, String opt){
			
	    String queryUrl = "http://api.gnavi.co.jp/ver1/RestSearchAPI/";
        String apiKey = "2ef4333acaf2e5ea9388911e3c6acdb2";
        String qPage = "50"; //一度のリクエストで得るレスポンス件数の最大値
        String keyword = query;;
        String qLat = lat;
        String qLng = lng;
        String range = "5"; //半径3km以内の結果を取得
        String genre = opt; //大業態を指定
        
        //検索キーワードをUTF-8でURLエンコーディングする（API側の要請に基づく）
        try {
        	keyword = URLEncoder.encode(keyword, "utf-8");
        }catch (UnsupportedEncodingException e){
        	//do nothing
        }
        
        //大業態のカテゴリコードを取得する
        if (opt.equals("全て")){
        	genre = "";
        } else {
    		HttpURLConnection http = null;
    		InputStream in = null;
    		URL url = null;
    		String result = "";
    		try {
    			// URLにHTTP接続
    			url = new URL("http://api.gnavi.co.jp/ver1/CategoryLargeSearchAPI/?keyid=" + apiKey);
    			http = (HttpURLConnection) url.openConnection();
    			http.setRequestMethod("GET");
    			http.connect();
    			// InputStream型変数inにデータをダウンロード
    			in = http.getInputStream();
    			// 取得したxmlテキストから業態コードを取得する
    			result = new XmlParse().execute(in,"category_l_code","category_l_name");
    			// 1行ずつ切り出して配列に格納
    			String[] strAry = result.split("\n");
				// 指定した大業態の業態コードを取得
    			for (int i = 0; i < strAry.length; i++) {
    				String[] strAry2 = strAry[i].split(",,");
    				if(strAry2[1].equals(genre)){
    					genre = "&category_l=" + strAry2[0];
    					break;
    				}
    			}    			
    		} catch (Exception e) {
    			Log.d("通信エラー", ""+e.toString());
    			return "communication error";
    		} finally {
    			try {
    				if (http != null) http.disconnect();
    				if (in != null)	in.close();
    			} catch (Exception e) {
    			}
    		}
        }
        
        // 検索クエリとなるURLを作成
    	queryUrl += "?keyid="+apiKey+"&hit_per_page="+qPage+"&coordinates_mode=2"+"&freeword=" + keyword + "&range=" + range + "&latitude=" + qLat + "&longitude=" + qLng + genre;
	
    	Log.d("GnaviApi", "url="+queryUrl);
    	
    	return queryUrl;
	}
	
	//切り出すパラメータを指定して検索結果のxml解析を行う
	@Override
	protected String getResult(InputStream xml){
		String result;
		
		//検索結果のxmlテキストを、１店舗１行のカンマ区切りテキストに整形する
		//引数１：xmlテキスト　引数２以降：抽出したいタグ名（ただし記載する順番はxmlテキスト中に記述される順番と一致させる必要がある）
		//一度に多量のタグを抽出しようとすると、取得結果が途中で途切れる（メモリ不足のため？）
		//DLしたデータは一旦ファイルに書き出したほうが良い？
		result = new XmlParse().execute(xml,"id","name","latitude","longitude","url","tel");
		Log.d("GnaviApi", "result="+result);
		
		return result;
	}
}
