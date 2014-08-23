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
		
		//tag[0]�`[x]�Ŏ󂯎�����^�O��xml�̒�����T���A�P�X�܂P�s�̃J���}��؂�e�L�X�g�ɐ��`����
		
		//XmlPullParser�̃C���X�^���X�𐶐�
		final XmlPullParser xmlPullParser = Xml.newPullParser();
		
	    	//xml�f�[�^��xmlPullParser�Ɉ����n��
	    	try {
				xmlPullParser.setInput(xml,null);
			} catch (XmlPullParserException e1) {
				Log.d("XmlPullParser", "setinput error");
			}

	    	//xml�^�O�̃X�e�[�^�X�iEND_DOCUMENT���j��eventType�Ɋi�[����
			int eventType = 0;
			try {
				eventType = xmlPullParser.getEventType();
			} catch (XmlPullParserException e1) {
				e1.printStackTrace();
			}
			
        while (eventType != XmlPullParser.END_DOCUMENT) {

        	try {
        		//�J�n�^�O�������ꍇ�A���o�������s��
	        	if(eventType == XmlPullParser.START_TAG) {
	        		
	        		tagName = xmlPullParser.getName();
	        		
	        		if (tagName.equals(idTag)){
	        			result += xmlPullParser.nextText() + ",,";
	        			idFlg = true; //���������O�̃^�O�͍ŏ��ɏo�Ă������̂����E��Ȃ��悤�ɂ��邽�߂̎d�|��
	        		}
	        		
	        		//���o�Ώۃ^�O�̒��g��result�ɃJ���}��؂�ŒǋL����
	        		if (idFlg){
		        		for (int i = 0 ; i < tag.length ; i++){
		        			if (tagName.equals(tag[i])){
			        			result += xmlPullParser.nextText() + ",,";
			        		}
		        		}
		        		//���o�Ώۂ̍Ō���^�O�����������ꍇ�A�Ō�ɉ��s��ǋL����
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
        		//���̃^�O��ǂݍ���
        		eventType = xmlPullParser.next();
        	}catch (Exception e) {
        		Log.d("xmlPullParser", "ParseError2");
        	}
        }
		return result;
	}
}
