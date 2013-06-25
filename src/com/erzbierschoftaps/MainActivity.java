package com.erzbierschoftaps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	public final static String BAR_URL = "com.erzbierschoftaps.BAR_URL";
	public final static String BAR_NAME = "com.erzbierschoftaps.BAR_NAME";
	private String winterthurUrl = "http://punkt.erzbierschof.ch/on-tap";
	private String winterthurName = "Winterthur";
	private String liebefeldUrl = "http://bar.erzbierschof.ch/on-tap";
	private String liebefeldName = "Liebefeld";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
	
	public void checkWinterthurTaps(View v){
		Intent intent = new Intent(this, TapsActivity.class);
		intent.putExtra(BAR_URL, this.winterthurUrl);
		intent.putExtra(BAR_NAME, this.winterthurName);
		startActivity(intent);
	}
	
    public void checkLiebefeldTaps(View v){
		Intent intent = new Intent(this, TapsActivity.class);
		intent.putExtra(BAR_URL, this.liebefeldUrl);
		intent.putExtra(BAR_NAME, this.liebefeldName);
		startActivity(intent);
	}
	
}
