/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hisureba.japanesedictionary;


import android.app.Activity;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

	

public class WordActivity extends Activity implements OnClickListener {
	
	private TextView btnJpVn, btnJpEn, btnKanji;
    public DictionaryProvider dp;
    
    private String searchTable = "jp_vn_table";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word);
        
        dp = new DictionaryProvider();
    	dp.onCreate();

        btnJpVn = (TextView) findViewById(R.id.btnJpVn);
        btnJpEn = (TextView) findViewById(R.id.btnJpEn);
        btnKanji = (TextView) findViewById(R.id.btnKanji);

        btnJpVn.setOnClickListener((OnClickListener) this);
        btnJpEn.setOnClickListener(this);
        btnKanji.setOnClickListener(this);  

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Uri uri = getIntent().getData();
        Cursor cursor = managedQuery(uri, null, null, null, null);

        if (cursor == null) {
            finish();
        } else {
            cursor.moveToFirst();

            TextView word = (TextView) findViewById(R.id.word);
            TextView definition = (TextView) findViewById(R.id.definition);

            int wIndex = cursor.getColumnIndexOrThrow(DictionaryDatabase.KEY_WORD);
            int dIndex = cursor.getColumnIndexOrThrow(DictionaryDatabase.KEY_DEFINITION);

            word.setText(cursor.getString(wIndex));
            definition.setText(formatDefination(cursor.getString(dIndex)));
        }
    }
    
    public void onClick(View view) {
    	clearSelectedButton();
    	btnJpVn.setBackgroundColor(getResources().getColor(R.color.selectedButton));
        if(view == btnJpVn){
        	if(searchTable == "vn_jp_table" || searchTable == "jp_vn_table") {
        		if(btnJpVn.getText().equals("JP-VN")){
            		btnJpVn.setText("VN-JP");
            		searchTable = "vn_jp_table";
            	} else {
            		btnJpVn.setText("JP-VN");
            		searchTable = "jp_vn_table";
            	}
        	} else {
        		if(btnJpVn.getText().equals("JP-VN")){
            		searchTable = "jp_vn_table";
            	} else {
            		searchTable = "vn_jp_table";
            	}
        	}        	
        }
        
        if(view == btnJpEn){
        	clearSelectedButton();
        	btnJpEn.setBackgroundColor(getResources().getColor(R.color.selectedButton));
        	if (searchTable == "en_jp_table" || searchTable == "jp_en_table") {
        		if(btnJpEn.getText().equals("JP-EN")){
            		btnJpEn.setText("EN-JP");
            		searchTable = "en_jp_table";
            	} else {
            		btnJpEn.setText("JP-EN");
            		searchTable = "jp_en_table";
            	}
        	} else {
        		if(btnJpEn.getText().equals("JP-EN")){
            		searchTable = "jp_en_table";
            	} else {
            		searchTable = "en_jp_table";
            	}
        	}        	
        }
        
        if(view == btnKanji) {
        	clearSelectedButton();
        	btnKanji.setBackgroundColor(getResources().getColor(R.color.selectedButton));
        }
        
    	dp.setSearchTable(searchTable);
  } 

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                onSearchRequested();
                return true;
            case android.R.id.home:
                Intent intent = new Intent(this, SearchableDictionary.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }
    
    private String formatDefination(String raw) {
    	String output = "";
		String[] words = raw.split("∴");
		for(int i=1; i<words.length; i++) {
			String word = words[i];
			
			String[] types  = word.split("☆");
			String kanji = types[0];
			output += kanji + "\n";
			if(types.length > 1) {
				for(int j=1; j<types.length; j++) {
					String type = types[j];
					
					String[] means = type.split("◆");
					output += "\t◆" + means[0] + "\n";
					if(means.length > 1) {
						for(int k=1; k<means.length; k++) {
							String mean = means[k];
							
							String[] examples = mean.split("※");
							output += "\t※" + examples[0] + "\n";
							if(examples.length > 1) {
								for(int t=1; t<examples.length; t++) {
									output += "\t\t" + examples[t] + "\n";
								}
							}
						}
					}
				}
			}
			output += "\n";
		}
		return output;
    }
    
    private void clearSelectedButton(){
    	btnJpVn.setBackgroundColor(getResources().getColor(R.color.defaultButton));
    	btnJpEn.setBackgroundColor(getResources().getColor(R.color.defaultButton));
    	btnKanji.setBackgroundColor(getResources().getColor(R.color.defaultButton));
    }
}
