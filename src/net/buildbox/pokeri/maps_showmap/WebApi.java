package net.buildbox.pokeri.maps_showmap;

import java.io.InputStream;

public abstract class WebApi {
	abstract String createUrl(String query, String lat, String lng, String opt); //�����L�[���[�h�ƑI��͈͂̊O�S���W�ƃI�v�V�����̃p�����[�^��n��
	abstract String getResult(InputStream xml); 
}
