package net.buildbox.pokeri.maps_showmap;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.util.Log;

public class Api_YahooLocalSearch extends WebApi {

	@Override
	String createUrl(String query, String lat, String lng) {

		String queryUrl = "http://search.olp.yahooapis.jp/OpenLocalPlatform/V1/localSearch";
        String apiKey = "dj0zaiZpPTF4b21pU1duRVpIUyZzPWNvbnN1bWVyc2VjcmV0Jng9Yjc-";
        String qPage = "50"; //��x�̃��N�G�X�g�œ��郌�X�|���X�����̍ő�l
        String keyword = null;;
        String qLat = lat;
        String qLng = lng;
        String range = "50"; //���a50km�̌��ʂ��擾
        
        //�����L�[���[�h��UTF-8��URL�G���R�[�f�B���O����iAPI���̗v���Ɋ�Â��j
        try {
        	keyword = URLEncoder.encode(query, "utf-8");
        }catch (UnsupportedEncodingException e){
        	//do nothing
        }
        // �����N�G���ƂȂ�URL���쐬
    	queryUrl += "?appid="+apiKey+"&results="+qPage+"&query=" + keyword + "&dist=" + range + "&lat=" + qLat + "&lon=" + qLng;
    	Log.d("YahooApi", "url="+queryUrl);
    	
    	return queryUrl;

	}

	@Override
	String getResult(InputStream xml) {
		String result;
		
		//�������ʂ�xml�e�L�X�g���A�P�X�܂P�s�̃J���}��؂�e�L�X�g�ɐ��`����
		//�����P�Fxml�e�L�X�g�@�����Q�ȍ~�F���o�������^�O���i�������L�ڂ��鏇�Ԃ�xml�e�L�X�g���ɋL�q����鏇�Ԃƈ�v������K�v������j
		//��x�ɑ��ʂ̃^�O�𒊏o���悤�Ƃ���ƁA�擾���ʂ��r���œr�؂��i�������s���̂��߁H�j
		//DL�����f�[�^�͈�U�t�@�C���ɏ����o�����ق����ǂ��H
		result = new XmlParse().execute(xml,"Gid","Name","Coordinates"); 
		Log.d("YahooApi", "result="+result);
		
		return result;
	}

}
