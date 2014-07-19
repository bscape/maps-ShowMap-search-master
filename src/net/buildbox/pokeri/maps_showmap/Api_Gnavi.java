package net.buildbox.pokeri.maps_showmap;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.util.Log;

public class Api_Gnavi extends WebApi {

	//変数を作成する
	String query;
	String result;
		
	//検索URLを作成する
	@Override
	protected String createUrl(String query, String lat, String lng){
			
	    String queryUrl = "http://api.gnavi.co.jp/ver1/RestSearchAPI/";
        String apiKey = "2ef4333acaf2e5ea9388911e3c6acdb2";
        String qPage = "50"; //一度のリクエストで得るレスポンスデータの最大件数
        String keyword = null;;
        String qLat = lat;
        String qLng = lng;
        String range = "5"; //半径3km
        
        //検索キーワードをUTF-8でURLエンコーディングする
        try {
        	keyword = URLEncoder.encode(query, "utf-8");
        }catch (UnsupportedEncodingException e){
        	//do nothing
        }
        // ぐるなびAPIへの検索クエリとなるURLを作成
    	queryUrl += "?keyid="+apiKey+"&hit_per_page="+qPage+"&coordinates_mode=2"+"&freeword=" + keyword + "&range=" + range + "&latitude=" + qLat + "&longitude=" + qLng;
	
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
