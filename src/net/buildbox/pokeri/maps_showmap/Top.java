package net.buildbox.pokeri.maps_showmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class Top extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_top);
		
		Button btn = (Button)findViewById(R.id.beer1);
		Animation feedin_btn = AnimationUtils.loadAnimation( this, R.anim.animation );
		feedin_btn.setDuration( 10000 );
		btn.startAnimation( feedin_btn );
		
//		View beer = findViewById(R.id.beer1);
		btn.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.top, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
        intent.setClassName("net.buildbox.pokeri.maps_showmap", "net.buildbox.pokeri.maps_showmap.MainActivity");
        startActivity(intent);
		// TODO 自動生成されたメソッド・スタブ
		
	}
}
