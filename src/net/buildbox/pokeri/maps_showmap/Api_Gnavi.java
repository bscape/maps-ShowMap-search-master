package net.buildbox.pokeri.maps_showmap;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.util.Log;

public class Api_Gnavi extends WebApi {

	//�ϐ����쐬����
	String query;
	String result;
		
	//����URL���쐬����
	@Override
	protected String createUrl(String query, String lat, String lng){
			
	    String queryUrl = "http://api.gnavi.co.jp/ver1/RestSearchAPI/";
        String apiKey = "2ef4333acaf2e5ea9388911e3c6acdb2";
        String qPage = "50"; //��x�̃��N�G�X�g�œ��郌�X�|���X�f�[�^�̍ő匏��
        String keyword = null;;
        String qLat = lat;
        String qLng = lng;
        String range = "5"; //���a3km
        
        //�����L�[���[�h��UTF-8��URL�G���R�[�f�B���O����
        try {
        	keyword = URLEncoder.encode(query, "utf-8");
        }catch (UnsupportedEncodingException e){
        	//do nothing
        }
        // ����Ȃ�API�ւ̌����N�G���ƂȂ�URL���쐬
    	queryUrl += "?keyid="+apiKey+"&hit_per_page="+qPage+"&coordinates_mode=2"+"&freeword=" + keyword + "&range=" + range + "&latitude=" + qLat + "&longitude=" + qLng;
	
    	Log.d("GnaviApi", "url="+queryUrl);
    	
    	return queryUrl;
	}
	
	//�؂�o���p�����[�^���w�肵�Č������ʂ�xml��͂��s��
	@Override
	protected String getResult(InputStream xml){
		String result;
		
		//�������ʂ�xml�e�L�X�g���A�P�X�܂P�s�̃J���}��؂�e�L�X�g�ɐ��`����
		//�����P�Fxml�e�L�X�g�@�����Q�ȍ~�F���o�������^�O���i�������L�ڂ��鏇�Ԃ�xml�e�L�X�g���ɋL�q����鏇�Ԃƈ�v������K�v������j
		//��x�ɑ��ʂ̃^�O�𒊏o���悤�Ƃ���ƁA�擾���ʂ��r���œr�؂��i�������s���̂��߁H�j
		//DL�����f�[�^�͈�U�t�@�C���ɏ����o�����ق����ǂ��H
		result = new XmlParse().execute(xml,"id","name","latitude","longitude","url","tel");
		Log.d("GnaviApi", "result="+result);
		
		return result;
	}
}
