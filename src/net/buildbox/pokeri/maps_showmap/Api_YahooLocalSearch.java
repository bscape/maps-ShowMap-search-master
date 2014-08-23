package net.buildbox.pokeri.maps_showmap;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.util.Log;

public class Api_YahooLocalSearch extends WebApi {

	@Override
	String createUrl(String query, String lat, String lng, String opt) {

		String queryUrl = "http://search.olp.yahooapis.jp/OpenLocalPlatform/V1/localSearch";
        String apiKey = "dj0zaiZpPTF4b21pU1duRVpIUyZzPWNvbnN1bWVyc2VjcmV0Jng9Yjc-";
        String qPage = "50"; //一度のリクエストで得るレスポンス件数の最大値
        String keyword = null;;
        String qLat = lat;
        String qLng = lng;
        String range = "50"; //半径50kmの結果を取得
        
        //検索キーワードをUTF-8でURLエンコーディングする（API側の要請に基づく）
        try {
        	keyword = URLEncoder.encode(query, "utf-8");
        }catch (UnsupportedEncodingException e){
        	//do nothing
        }
        // 検索クエリとなるURLを作成
    	queryUrl += "?appid="+apiKey+"&results="+qPage+"&query=" + keyword + "&dist=" + range + "&lat=" + qLat + "&lon=" + qLng;
    	Log.d("YahooApi", "url="+queryUrl);
    	
    	return queryUrl;

	}

	@Override
	String getResult(InputStream xml) {
		String result;
		
		//検索結果のxmlテキストを、１店舗１行のカンマ区切りテキストに整形する
		//引数１：xmlテキスト　引数２以降：抽出したいタグ名（ただし記載する順番はxmlテキスト中に記述される順番と一致させる必要がある）
		//一度に多量のタグを抽出しようとすると、取得結果が途中で途切れる（メモリ不足のため？）
		//DLしたデータは一旦ファイルに書き出したほうが良い？
		result = new XmlParse().execute(xml,"Gid","Name","Coordinates"); 
		Log.d("YahooApi", "result="+result);
		
		return result;
	}

}
