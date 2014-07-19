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
    double oY = 0; //�O�S��lat
    double oX = 0; //�O�S��lng
    double distance = 0; //�O�ډ~�̔��a
	
	//�R���X�g���N�^�Ń��C���X���b�h��map���󂯎��
		public MyAsyncTask(GoogleMap tmp, LatLng[] tmp2, String tmp3){
			map = tmp;
			selectedArea = tmp2;
			genre = tmp3;
		}
	
	@Override
	public String doInBackground(String... params) {
		
//---------------------------------------------------------------------------------------------
        //�N�G���ɑ���ʒu���W�����߂邽�߁A�l�p�`�̍ŏ���܉~�̒��S���W�����߂�i�I���G���A�̒��S�����ɁA���axx�L�����̓X�������A�Ƃ����`�Ō��ʂ��i��j
        //�C�ӂ�3���_�̊O�ډ~�����߁A�c��1�_�������ɂ����OK�B�Ȃ���Εʂ�3�_�̑g�ݍ��킹�Ŏ���
        //�Q�������ʂ̑z��Ōv�Z���Ă��邽�߁A�l�p�`�����t�ύX�����܂����ł���ꍇ�͐��m�Ȍ��ʂ��o���Ȃ�
        
        double tmp[] = new double[6];

        
	    //�O�S���v�Z�B�����͖����؁E�E�E
        tmp[0] = 2 * (selectedArea[1].longitude - selectedArea[0].longitude);
        tmp[1] = 2 * (selectedArea[1].latitude - selectedArea[0].latitude);
        tmp[2] = Math.pow(selectedArea[0].longitude,2) - Math.pow(selectedArea[1].longitude,2) + Math.pow(selectedArea[0].latitude,2) - Math.pow(selectedArea[1].latitude,2);
        tmp[3] = 2 * (selectedArea[2].longitude - selectedArea[0].longitude);
        tmp[4] = 2 * (selectedArea[2].latitude - selectedArea[0].latitude);
        tmp[5] = Math.pow(selectedArea[0].longitude,2) - Math.pow(selectedArea[2].longitude,2) + Math.pow(selectedArea[0].latitude,2) - Math.pow(selectedArea[2].latitude,2);
        //�O�S��x���W��longitude
        oX = ((tmp[1] * tmp[5]) - (tmp[4] * tmp[2])) / ((tmp[0] * tmp[4]) - (tmp[3] * tmp[1]));
        //�O�S��y���W��latitude
        oY = ((tmp[2] * tmp[3]) - (tmp[5] * tmp[0])) / ((tmp[0] * tmp[4]) - (tmp[3] * tmp[1]));
        
        //�O�S�̔��a
        double dx = Math.pow(oX - selectedArea[0].longitude,2);
        double dy = Math.pow(oY - selectedArea[0].latitude,2);
        distance = Math.sqrt(dx + dy);

        Log.d("�O�S��x���W",""+oX);
        Log.d("�O�S��y���W",""+oY);

//---------------------------------------------------------------------------------------------

        // �g�p����WebAPI�̔���
        if(genre.equals("������")){
        	webApi = new Api_Gnavi();
        }else if(genre.equals("�ό�")){
        	//����API�̃C���X�^���X��ϐ����uwebApi�v�ō쐬����
        	webApi = new Api_YahooLocalSearch();
        }
        
        // �e��API�ւ̌����N�G���ƂȂ�URL���쐬
        String queryUrl = webApi.createUrl(params[0],String.valueOf(oY),String.valueOf(oX));
    	
//---------------------------------------------------------------------------------------------
		HttpURLConnection http = null;
        InputStream in = null;
		URL url = null;
		String result = "";
        
        try {
			//URL��HTTP�ڑ�
        	url = new URL(queryUrl);
			http = (HttpURLConnection)url.openConnection();
			http.setRequestMethod("GET");
			http.connect();
			// InputStream�^�ϐ�in�Ƀf�[�^���_�E�����[�h
			in = http.getInputStream();
			
			// �������ʂ�xml����K�v�ȃp�����[�^��؂�o��
			result = webApi.getResult(in);

			//�擾����xml�e�L�X�g��onPostExcecute�Ɉ����n��
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

		//�}�[�J�[�̃I�v�V�����p�C���X�^���X
		MarkerOptions options = new MarkerOptions();	
		Marker marker;
		
		//1�X��1�s�̌`�Ő؂�o���Ĕz��Ɋi�[
		String[] strAry = src.split("\n");
		
		for (int i = 0 ; i < strAry.length ; i++) {

			//1�X�܂̊e�p�����[�^��؂�o���Ĕz��Ɋi�[
			String[] strAry2 = strAry[i].split(",");
			for (int j = 0 ; j < strAry2.length ; j++) {
			}

			//API�����lat,lng�̏��Ԃ��Ⴄ�̂ŁA���̑Ή��������
			//API���̐؂�o���^��API�N���X�̒��ɓ����ׂ����B
			//���W�̒l��String����Double�Ɍ^�ϊ�

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
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}
			
			//���W���O�ډ~�͈͓̔����ǂ�������
	        //�O�S�ƓX�̋���
	        double dx2 = Math.pow(oX - lng,2);
	        double dy2 = Math.pow(oY - lat,2);
	        double distance2 = Math.sqrt(dx2 + dy2);
	        
	        //�O�S�ƓX�̋��������a�ȓ��ł���ΊO�ډ~�̓����B���a�ȏ�Ȃ�O���B
	        if (distance2 <= distance) {

		    	// �\���ʒu�𐶐�
				// �ɒ[�ɍ��W���߂��ꍇ�͌ォ�琶�����ꂽ�s���������̃s�����㏑������炵���B�u�悩�����v�Ō�������Ƃ킩��B
		    	LatLng posMapPoint = new LatLng(lat,lng);
		    	
				// �s���ƃ^�C�g���i�X���j�̐ݒ�
				options.position(posMapPoint);
				options.title(strAry2[1]);
				// �s���̒ǉ�
				marker = map.addMarker(options);
	        }
		}
	}
}