package net.buildbox.pokeri.maps_showmap;

import java.io.InputStream;

public abstract class WebApi {
	abstract String createUrl(String query, String lat, String lng); //�����L�[���[�h�ƑI��͈͂̊O�S���W��n��
	abstract String getResult(InputStream xml); 
}
