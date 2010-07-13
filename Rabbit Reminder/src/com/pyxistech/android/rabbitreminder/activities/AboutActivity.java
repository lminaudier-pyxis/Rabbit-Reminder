/**
	RABBIT REMINDER
	Copyright (C) 2010  Pyxis Technologies
	
	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.
	
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License along
	with this program; if not, write to the Free Software Foundation, Inc.,
	51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

package com.pyxistech.android.rabbitreminder.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.pyxistech.android.rabbitreminder.R;

public class AboutActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.about);
		
		ImageView grownByPyxisLogo = (ImageView) findViewById(R.id.about_grown_by_pyxis_logo);
		grownByPyxisLogo.setOnClickListener(grownByPyxisLogoClicked);
	}
	
	private OnClickListener grownByPyxisLogoClicked = new OnClickListener() {
		public void onClick(View v) {
			String url = getString(R.string.pyxis_website_uri);
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
		}
	};
}
