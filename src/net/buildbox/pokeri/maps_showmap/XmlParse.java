package net.buildbox.pokeri.maps_showmap;

import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class XmlParse {

	public String execute(InputStream xml, String idTag, String... tag){
		
		String tagName = "";
		String result = "";
		boolean idFlg = false;
		
		//tag[0]～[x]で受け取ったタグをxmlの中から探し、１店舗１行のカンマ区切りテキストに整形する
		
		//XmlPullParserのインスタンスを生成
		final XmlPullParser xmlPullParser = Xml.newPullParser();
		
	    	//xmlデータをxmlPullParserに引き渡す
	    	try {
				xmlPullParser.setInput(xml,null);
			} catch (XmlPullParserException e1) {
				Log.d("XmlPullParser", "setinput error");
			}

	    	//xmlタグのステータス（END_DOCUMENT等）をeventTypeに格納する
			int eventType = 0;
			try {
				eventType = xmlPullParser.getEventType();
			} catch (XmlPullParserException e1) {
				e1.printStackTrace();
			}
			
        while (eventType != XmlPullParser.END_DOCUMENT) {

        	try {
        		//開始タグだった場合、抽出処理を行う
	        	if(eventType == XmlPullParser.START_TAG) {
	        		
	        		tagName = xmlPullParser.getName();
	        		
	        		if (tagName.equals(idTag)){
	        			result += xmlPullParser.nextText() + ",";
	        			idFlg = true; //←同じ名前のタグは最初に出てきたものしか拾わないようにするための仕掛け
	        		}
	        		
	        		//抽出対象タグの中身をresultにカンマ区切りで追記する
	        		if (idFlg){
		        		for (int i = 0 ; i < tag.length ; i++){
		        			if (tagName.equals(tag[i])){
			        			result += xmlPullParser.nextText() + ",";
			        		}
		        		}
		        		//抽出対象の最後尾タグを処理した場合、最後に改行を追記する
		        		if (tagName.equals(tag[tag.length -1])){ 
		        			result +="\n";
		        			idFlg = false;
		        		}
	        		}
	        	}
        	}catch (Exception e) {
            	Log.d("xmlPullParser", "ParseError1");
            }
        	
        	try {
        		//次のタグを読み込む
        		eventType = xmlPullParser.next();
        	}catch (Exception e) {
        		Log.d("xmlPullParser", "ParseError2");
        	}
        }
		return result;
	}
}
