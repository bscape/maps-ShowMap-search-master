package net.buildbox.pokeri.maps_showmap;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.util.Log;

public class Api_Gnavi extends WebApi {
		
	//����URL���쐬����
	@Override
	protected String createUrl(String query, String lat, String lng, String opt){
			
	    String queryUrl = "http://api.gnavi.co.jp/ver1/RestSearchAPI/";
        String apiKey = "2ef4333acaf2e5ea9388911e3c6acdb2";
        String qPage = "50"; //��x�̃��N�G�X�g�œ��郌�X�|���X�����̍ő�l
        String keyword = query;;
        String qLat = lat;
        String qLng = lng;
        String range = "5"; //���a3km�ȓ��̌��ʂ��擾
        String genre = opt; //��ƑԂ��w��
        
        //�����L�[���[�h��UTF-8��URL�G���R�[�f�B���O����iAPI���̗v���Ɋ�Â��j
        try {
        	keyword = URLEncoder.encode(keyword, "utf-8");
        }catch (UnsupportedEncodingException e){
        	//do nothing
        }
        
        //��ƑԂ̃J�e�S���R�[�h���擾����
        if (opt.equals("�S��")){
        	genre = "";
        } else {
    		HttpURLConnection http = null;
    		InputStream in = null;
    		URL url = null;
    		String result = "";
    		try {
    			// URL��HTTP�ڑ�
    			url = new URL("http://api.gnavi.co.jp/ver1/CategoryLargeSearchAPI/?keyid=" + apiKey);
    			http = (HttpURLConnection) url.openConnection();
    			http.setRequestMethod("GET");
    			http.connect();
    			// InputStream�^�ϐ�in�Ƀf�[�^���_�E�����[�h
    			in = http.getInputStream();
    			// �擾����xml�e�L�X�g����ƑԃR�[�h���擾����
    			result = new XmlParse().execute(in,"category_l_code","category_l_name");
    			// 1�s���؂�o���Ĕz��Ɋi�[
    			String[] strAry = result.split("\n");
				// �w�肵����ƑԂ̋ƑԃR�[�h���擾
    			for (int i = 0; i < strAry.length; i++) {
    				String[] strAry2 = strAry[i].split(",");
    				if(strAry2[1].equals(genre)){
    					genre = "&category_l=" + strAry2[0];
    					break;
    				}
    			}    			
    		} catch (Exception e) {
    			Log.d("�ʐM�G���[", ""+e.toString());
    			return "communication error";
    		} finally {
    			try {
    				if (http != null) http.disconnect();
    				if (in != null)	in.close();
    			} catch (Exception e) {
    			}
    		}
        }
        
        // �����N�G���ƂȂ�URL���쐬
    	queryUrl += "?keyid="+apiKey+"&hit_per_page="+qPage+"&coordinates_mode=2"+"&freeword=" + keyword + "&range=" + range + "&latitude=" + qLat + "&longitude=" + qLng + genre;
	
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
