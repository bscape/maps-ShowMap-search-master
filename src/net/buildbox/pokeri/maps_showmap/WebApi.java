package net.buildbox.pokeri.maps_showmap;

import java.io.InputStream;

public abstract class WebApi {
	abstract String createUrl(String query, String lat, String lng, String opt); //検索キーワードと選択範囲の外心座標とオプションのパラメータを渡す
	abstract String getResult(InputStream xml); 
}
