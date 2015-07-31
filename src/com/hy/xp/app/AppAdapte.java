package com.hy.xp.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.hy.xp.app.clear.AppCleanManager;
import com.hy.xp.app.clear.ClearFileAcitivity;
import com.hy.xp.app.task.PreferenceUtils;

public class AppAdapte extends SimpleAdapter {
	private Context mcontext;
	public static List<String> dataselected = new ArrayList();
	public static List<String> cleanselected = new ArrayList();
	private String[] mfrom = null;
	private int[] mto = null;
	private List<Map<String, ?>> mdata = null;

	public AppAdapte(Context context, List<Map<String, ?>> data, int resource,
			String[] from, int[] to) {
		super(context, data, resource, from, to);
		mcontext = context;
		mfrom = from;
		mto = to;
		mdata = data;
	}

	class ListItemView {
		TextView appname;
		ImageView icon;
		CheckBox wzchecked;
		CheckBox clcheckbox;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemView listItemView = null;
		if (convertView == null) {
			convertView = LinearLayout.inflate(mcontext,R.layout.listviewcontent, null);
			listItemView = new ListItemView();
			listItemView.appname = (TextView) convertView.findViewById(R.id.appname);
			listItemView.icon = (ImageView) convertView.findViewById(R.id.appicon);
			listItemView.wzchecked = (CheckBox) convertView.findViewById(R.id.wzcheckbox);
			listItemView.clcheckbox = (CheckBox) convertView.findViewById(R.id.clcheckbox);
			convertView.setTag(listItemView);
		} else {
			listItemView = (AppAdapte.ListItemView) convertView.getTag();
		}
		
		
		listItemView.wzchecked.setOnCheckedChangeListener(null);
		listItemView.wzchecked.setChecked(false);
		if (Util.itemexists(dataselected.toArray(),
				mdata.get(position).get("packagename").toString())) {
			listItemView.wzchecked.setChecked(true);
		} else {
			listItemView.wzchecked.setChecked(false);
		}
		
		MyCheckedChangeListener onCheckedChangeListener = new MyCheckedChangeListener();
		onCheckedChangeListener.setposition(position);
		listItemView.wzchecked
				.setOnCheckedChangeListener(onCheckedChangeListener);
		
		
		listItemView.clcheckbox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Intent it = new Intent(mcontext,
								ClearFileAcitivity.class);
						mcontext.startActivity(it);
					}
				});
		
		listItemView.appname.setText(mdata.get(position).get(mfrom[0]).toString());
		listItemView.icon.setImageDrawable((Drawable) mdata.get(position).get(mfrom[1]));
		return convertView;
	}

	class MyCheckedChangeListener implements OnCheckedChangeListener {
		private int position;

		public void setposition(int p) {
			position = p;
		}

		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			HashMap<String, Object> data = (HashMap) mdata.get(position);
			String appname = data.get("name").toString();
			int uid = (Integer) data.get("uuid");
			String packagename = data.get("packagename").toString();
			if (isChecked) {
				if (!Util.itemexists(AppAdapte.dataselected.toArray(), appname)) {
					setuidper(uid, packagename);
					dataselected.add(packagename);
					cleanselected.add(appname);
				}
			} else {
				cleanuidper(uid, packagename);
				dataselected.remove(packagename);
				cleanselected.remove(appname);
			}

		}
	}

	private void setuidper(int uid, String packagename) {
		PrivacyManager.deleteRestrictions(uid, null, true);
		PrivacyManager.deleteSettings(uid);
		PrivacyManager.deleteUsage(uid);
		PrivacyManager.clearPermissionCache(uid);
		PrivacyManager.applyTemplate(uid, Meta.cTypeTemplate, null, true, true, false);
		PrivacyManager.setSetting(uid, PrivacyManager.cSettingState, Integer.toString(ApplicationInfoEx.STATE_ATTENTION));
		PreferenceUtils
				.setParam(mcontext, "xp_clear", packagename, packagename);
		SharedPreferences prefs = mcontext.getSharedPreferences("ModSettings",
				Context.MODE_WORLD_READABLE);
		SharedPreferences.Editor e = prefs.edit();
		e.putBoolean(packagename + "/" + "mdatatype", true).commit();
		e.putBoolean(packagename + "/" + "updateDisplayInfoLocked", true).commit();
		e.putBoolean(packagename + "/" + "timeMachine", true).commit();
		e.putInt(packagename + "/recents-mode", 0x2).commit();
	}

	private void cleanuidper(int uid, String packagename) {
		PrivacyManager.deleteRestrictions(uid, null, true);
		PrivacyManager.deleteSettings(uid);
		PrivacyManager.deleteUsage(uid);
		PrivacyManager.clearPermissionCache(uid);
		PreferenceUtils.clearKey(mcontext, "xp_clear", packagename);
		SharedPreferences prefs = mcontext.getSharedPreferences("ModSettings",
				Context.MODE_WORLD_READABLE);
		SharedPreferences.Editor e = prefs.edit();
		e.remove(packagename + "/" + "mdatatype");
		e.remove(packagename + "/" + "updateDisplayInfoLocked");
		e.remove(packagename + "/" + "timeMachine");
		e.remove(packagename + "/recents-mode");
		e.commit();
	}

	private void cleanData(String packagenaem) {
		AppCleanManager.cleanAppDataByPackageName(mcontext,
				mcontext.getPackageManager(), packagenaem);
	}
}
