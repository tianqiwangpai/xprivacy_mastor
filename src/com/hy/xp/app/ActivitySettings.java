package com.hy.xp.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hy.xp.app.task.PhoneDataBean;

public class ActivitySettings extends ActivityBase implements OnCheckedChangeListener, OnClickListener
{
	private int userId;
	private int uid;
	private boolean expert;

	private CheckBox cbNotify;
	private CheckBox cbBlacklist;
	private CheckBox cbUsage;
	private CheckBox cbParameters;
	private CheckBox cbValues;
	private CheckBox cbLog;
	private CheckBox cbSystem;
	private CheckBox cbExperimental;
	private CheckBox cbHttps;
	private CheckBox cbAOSP;
	private EditText etConfidence;
	private EditText etQuirks;
	private Button btnFlush;
	private Button btnClearDb;
	private EditText etSerial;
	private EditText etLat;
	private EditText etLon;
	private EditText etAlt;
	private EditText etMac;
	private EditText etIP;
	private EditText etImei;
	private EditText etPhone;
	private EditText etId;
	private EditText etGsfId;
	private EditText etAdId;
	private EditText etMcc;
	private EditText etMnc;
	private EditText etCountry;
	private EditText etOperator;
	private EditText etIccId;
	private EditText etCid;
	private EditText etLac;
	private EditText etSubscriber;
	private EditText etSSID;
	private EditText etUa;

	public static final String ACTION_SETTINGS = "com.hy.xp.app.action.SETTINGS";
	public static final String cUid = "Uid";
	private PhoneDataBean mBases = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings);
		setTitle(R.string.menu_settings);

		userId = Util.getUserId(Process.myUid());

		final Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey(cUid))
			uid = extras.getInt(cUid);
		else
			uid = userId;

		File sdcardDir = Environment.getExternalStorageDirectory();
		String path = sdcardDir.getPath() + "/xp_datafile/setting";
		File mFile = new File(path);
		if (mFile.exists()) {
			try {
				String result = BufferedReaderJSON(path);
				if (result != null) {
					Gson mGson = new Gson();
					mBases = mGson.fromJson(result, PhoneDataBean.class);
					// System.out.println(mBases.toString());
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Reference controls
		TextView tvInfo = (TextView) findViewById(R.id.tvInfo);

		cbNotify = (CheckBox) findViewById(R.id.cbNotify);
		cbBlacklist = (CheckBox) findViewById(R.id.cbBlacklist);
		cbUsage = (CheckBox) findViewById(R.id.cbUsage);
		cbParameters = (CheckBox) findViewById(R.id.cbParameters);
		cbValues = (CheckBox) findViewById(R.id.cbValues);
		cbLog = (CheckBox) findViewById(R.id.cbLog);

		CheckBox cbExpert = (CheckBox) findViewById(R.id.cbExpert);
		cbSystem = (CheckBox) findViewById(R.id.cbSystem);
		cbExperimental = (CheckBox) findViewById(R.id.cbExperimental);
		cbHttps = (CheckBox) findViewById(R.id.cbHttps);
		cbAOSP = (CheckBox) findViewById(R.id.cbAOSP);
		LinearLayout llConfidence = (LinearLayout) findViewById(R.id.llConfidence);
		etConfidence = (EditText) findViewById(R.id.etConfidence);
		etQuirks = (EditText) findViewById(R.id.etQuirks);
		btnFlush = (Button) findViewById(R.id.btnFlush);
		btnClearDb = (Button) findViewById(R.id.btnClearDb);

		etSerial = (EditText) findViewById(R.id.etSerial);
		etLat = (EditText) findViewById(R.id.etLat);
		etLon = (EditText) findViewById(R.id.etLon);
		etAlt = (EditText) findViewById(R.id.etAlt);

		etMac = (EditText) findViewById(R.id.etMac);
		etIP = (EditText) findViewById(R.id.etIP);
		etImei = (EditText) findViewById(R.id.etImei);
		etPhone = (EditText) findViewById(R.id.etPhone);
		etId = (EditText) findViewById(R.id.etId);
		etGsfId = (EditText) findViewById(R.id.etGsfId);
		etAdId = (EditText) findViewById(R.id.etAdId);
		etMcc = (EditText) findViewById(R.id.etMcc);
		etMnc = (EditText) findViewById(R.id.etMnc);
		etCountry = (EditText) findViewById(R.id.etCountry);
		etOperator = (EditText) findViewById(R.id.etOperator);
		etIccId = (EditText) findViewById(R.id.etIccId);
		etCid = (EditText) findViewById(R.id.etCid);
		etLac = (EditText) findViewById(R.id.etLac);
		etSubscriber = (EditText) findViewById(R.id.etSubscriber);
		etSSID = (EditText) findViewById(R.id.etSSID);
		etUa = (EditText) findViewById(R.id.etUa);

		// Listen for changes
		cbParameters.setOnCheckedChangeListener(this);
		cbValues.setOnCheckedChangeListener(this);
		cbExpert.setOnCheckedChangeListener(this);

		// Get current values
		boolean usage = PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingUsage, true);
		boolean parameters = PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingParameters, false);
		boolean values = PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingValues, false);
		boolean log = PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingLog, false);

		boolean components = PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingSystem, false);
		boolean experimental = PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingExperimental, false);
		boolean https = PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingHttps, true);
		boolean aosp = PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingAOSPMode, false);
		String confidence = PrivacyManager.getSetting(-uid, PrivacyManager.cSettingConfidence, "");

		// Get quirks
		boolean freeze = PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingFreeze, false);
		boolean resolve = PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingResolve, false);
		boolean noresolve = PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingNoResolve, false);
		boolean permman = PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingPermMan, false);
		boolean iwall = PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingIntentWall, false);
		boolean safemode = PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingSafeMode, false);
		boolean test = PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingTestVersions, false);
		// boolean updates = PrivacyManager.getSettingBool(-uid,
		// PrivacyManager.cSettingUpdates, false);
		boolean odsystem = PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingOnDemandSystem, false);
		boolean wnomod = PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingWhitelistNoModify, false);
		List<String> listQuirks = new ArrayList<String>();
		if (freeze)
			listQuirks.add("freeze");
		if (resolve)
			listQuirks.add("resolve");
		if (noresolve)
			listQuirks.add("noresolve");
		if (permman)
			listQuirks.add("permman");
		if (iwall)
			listQuirks.add("iwall");
		if (safemode)
			listQuirks.add("safemode");
		if (test)
			listQuirks.add("test");
		// if (updates)
		// listQuirks.add("updates");
		if (odsystem)
			listQuirks.add("odsystem");
		if (wnomod)
			listQuirks.add("wnomod");
		Collections.sort(listQuirks);
		String quirks = TextUtils.join(",", listQuirks.toArray());

		expert = (components || experimental || !https || aosp || !"".equals(confidence) || listQuirks.size() > 0);

		// Application specific
		boolean notify = PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingNotify, true);
		boolean blacklist = PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingBlacklist, false);
		etSerial = (EditText) findViewById(R.id.etSerial);
		etSerial.setText(mBases != null ? mBases.getSerial() : "  ");

		etLat = (EditText) findViewById(R.id.etLat);
		etLat.setText(mBases != null ? mBases.getLatitude() : "  ");

		etLon = (EditText) findViewById(R.id.etLon);
		etLon.setText(mBases != null ? mBases.getLongitude() : "  ");

		etAlt = (EditText) findViewById(R.id.etAlt);
		etAlt.setText(mBases != null ? mBases.getAltitude() : "  ");

		etMac = (EditText) findViewById(R.id.etMac);
		etMac.setText(mBases != null ? mBases.getMacAddress() : "  ");

		etIP = (EditText) findViewById(R.id.etIP);
		etIP.setText(mBases != null ? mBases.getIpAddress() : "  ");

		etImei = (EditText) findViewById(R.id.etImei);
		etImei.setText(mBases != null ? mBases.getImei() : "  ");

		etPhone = (EditText) findViewById(R.id.etPhone);
		etPhone.setText(mBases != null ? mBases.getPhoneNumber() : "  ");

		etId = (EditText) findViewById(R.id.etId);
		etId.setText(mBases != null ? mBases.getAndroidID() : "  ");

		etGsfId = (EditText) findViewById(R.id.etGsfId);
		etGsfId.setText(mBases != null ? mBases.getGsfId() : "  ");

		etAdId = (EditText) findViewById(R.id.etAdId);
		etAdId.setText(mBases != null ? mBases.getAdvertisementID() : "  ");

		etMcc = (EditText) findViewById(R.id.etMcc);
		etMcc.setText(mBases != null ? mBases.getMcc() : "  ");

		etMnc = (EditText) findViewById(R.id.etMnc);
		etMnc.setText(mBases != null ? mBases.getMnc() : "  ");

		etCountry = (EditText) findViewById(R.id.etCountry);
		etCountry.setText(mBases != null ? mBases.getCountry() : "  ");

		etOperator = (EditText) findViewById(R.id.etOperator);
		etOperator.setText(mBases != null ? mBases.getOperator() : "  ");

		etIccId = (EditText) findViewById(R.id.etIccId);
		etIccId.setText(mBases != null ? mBases.getIccId() : "  ");

		// GSM Cell ID
		etCid = (EditText) findViewById(R.id.etCid);
		etCid.setText(mBases != null ? mBases.getGsmCallID() : "  ");

		// GSM LAC
		etLac = (EditText) findViewById(R.id.etLac);
		etLac.setText(mBases != null ? mBases.getGsmLac() : "  ");

		etSubscriber = (EditText) findViewById(R.id.etSubscriber);
		etSubscriber.setText(mBases != null ? mBases.getSerial() : "  ");

		etSSID = (EditText) findViewById(R.id.etSSID);
		etSSID.setText(mBases != null ? mBases.getSsid() : "  ");

		etUa = (EditText) findViewById(R.id.etUa);
		etUa.setText(mBases != null ? mBases.getUa() : "  ");

		EditText etModel = (EditText) findViewById(R.id.etmodel);
		etModel.setText(mBases != null ? mBases.getModel() : "  ");

		EditText etmanufacturer = (EditText) findViewById(R.id.etmanufacturer);
		etmanufacturer.setText(mBases != null ? mBases.getManufacturer() : "  ");

		EditText etproduct = (EditText) findViewById(R.id.etproduct);
		etproduct.setText(mBases != null ? mBases.getProduct() : "  ");

		EditText etdensity = (EditText) findViewById(R.id.etdensity);
		etdensity.setText(mBases != null ? mBases.getDensity() : "  ");

		// cpu
		EditText etcpu = (EditText) findViewById(R.id.etcpu);
		etcpu.setText(mBases != null ? mBases.getDensity() : "");

		// android sdk
		EditText etandroidsdk = (EditText) findViewById(R.id.etandroidsdk);
		etandroidsdk.setText(mBases != null ? mBases.getAndroidCode() : "  ");

		// Set current values
		if (uid == userId) {
			// Global settings
			tvInfo.setVisibility(View.GONE);
			cbUsage.setChecked(usage);
			cbParameters.setChecked(parameters);
			cbValues.setChecked(values);
			if (userId == 0)
				cbLog.setChecked(log);
			else {
				cbLog.setVisibility(View.GONE);
				btnFlush.setVisibility(View.GONE);
				btnClearDb.setVisibility(View.GONE);
			}
			cbExpert.setChecked(expert);

			if (PrivacyManager.cVersion3 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
				cbAOSP.setVisibility(View.VISIBLE);

			if (expert) {
				cbSystem.setChecked(components);
				cbExperimental.setChecked(experimental);
				cbHttps.setChecked(https);
				cbAOSP.setChecked(aosp);
				etConfidence.setText(confidence);
				etQuirks.setText(quirks);
			} else {
				cbSystem.setEnabled(false);
				cbExperimental.setEnabled(false);
				cbHttps.setEnabled(false);
				cbHttps.setChecked(true);
				cbAOSP.setEnabled(false);
				cbAOSP.setChecked(false);
				etConfidence.setEnabled(false);
				etQuirks.setEnabled(false);
				btnFlush.setEnabled(false);
				btnClearDb.setEnabled(false);
			}
		} else {
			// Display application names
			ApplicationInfoEx appInfo = new ApplicationInfoEx(this, uid);
			getActionBar().setSubtitle(TextUtils.join(",  ", appInfo.getApplicationName()));

			// Disable global settings
			cbUsage.setVisibility(View.GONE);
			cbParameters.setVisibility(View.GONE);
			cbValues.setVisibility(View.GONE);
			cbLog.setVisibility(View.GONE);
			cbSystem.setVisibility(View.GONE);
			cbExperimental.setVisibility(View.GONE);
			cbHttps.setVisibility(View.GONE);
			cbAOSP.setVisibility(View.GONE);
			llConfidence.setVisibility(View.GONE);
			btnFlush.setVisibility(View.GONE);
			btnClearDb.setVisibility(View.GONE);

			cbExpert.setChecked(expert);
			if (expert)
				etQuirks.setText(quirks);
			else
				etQuirks.setEnabled(false);
		}

		boolean gnotify = PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingNotify, true);
		if (uid == userId || gnotify)
			cbNotify.setChecked(notify);
		else
			cbNotify.setVisibility(View.GONE);

		PrivacyManager.isApplication(uid);
		PrivacyManager.getSettingBool(userId, PrivacyManager.cSettingOnDemandSystem, false);
		String blFileName = Environment.getExternalStorageDirectory().getPath() + "/.xprivacy/blacklist";
		if (uid == userId || !new File(blFileName).exists())
			cbBlacklist.setVisibility(View.GONE);
		else
			cbBlacklist.setChecked(blacklist);

		// Set randomize on access check boxes

		btnFlush.setOnClickListener(this);
		btnClearDb.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		if (inflater != null && PrivacyService.checkClient()) {
			inflater.inflate(R.menu.settings, menu);
			return true;
		} else
			return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
		case R.id.menu_cancel:
			finish();
			return true;
		case R.id.menu_save:
			optionSave();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		switch (buttonView.getId()) {
		case R.id.cbParameters:
		case R.id.cbValues:
			if (isChecked) {
				buttonView.setChecked(false);
			}
			break;
		case R.id.cbExpert:
			cbSystem.setEnabled(isChecked);
			cbExperimental.setEnabled(isChecked);
			cbHttps.setEnabled(isChecked);
			cbAOSP.setEnabled(isChecked);
			etConfidence.setEnabled(isChecked);
			etQuirks.setEnabled(isChecked);
			btnFlush.setEnabled(isChecked);
			btnClearDb.setEnabled(isChecked);
			if (isChecked) {
				// if (!expert)
				// Toast.makeText(this, getString(R.string.msg_expert),
				// Toast.LENGTH_LONG).show();
			} else {
				cbSystem.setChecked(false);
				cbExperimental.setChecked(false);
				cbHttps.setChecked(true);
				cbAOSP.setChecked(false);
				etConfidence.setText("");
				etQuirks.setText("");
			}
			break;

		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId()) {
		case R.id.btnFlush:
			flush();
			break;
		case R.id.btnClearDb:
			clearDB();
			break;
		}
	}

	private void clearDB()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivitySettings.this);
		alertDialogBuilder.setTitle(R.string.menu_clear_db);
		alertDialogBuilder.setMessage(R.string.msg_sure);
		alertDialogBuilder.setIcon(getThemed(R.attr.icon_launcher));
		alertDialogBuilder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				PrivacyManager.clear();
				Toast.makeText(ActivitySettings.this, getString(R.string.msg_reboot), Toast.LENGTH_LONG).show();
				finish();

				// Refresh main UI
				Intent intent = new Intent(ActivitySettings.this, ActivityMain.class);
				intent.putExtra(ActivityMain.cAction, ActivityMain.cActionRefresh);
				startActivity(intent);
			}
		});
		alertDialogBuilder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	private void flush()
	{
		Intent flushIntent = new Intent(UpdateService.cFlush);
		startService(flushIntent);
		Toast.makeText(ActivitySettings.this, getString(R.string.msg_done), Toast.LENGTH_LONG).show();
	}

	@SuppressLint("DefaultLocale")
	private void optionSave()
	{
		if (uid == userId) {
			// Global settings
			PrivacyManager.setSetting(uid, PrivacyManager.cSettingUsage, Boolean.toString(cbUsage.isChecked()));
			PrivacyManager.setSetting(uid, PrivacyManager.cSettingParameters, Boolean.toString(cbParameters.isChecked()));
			PrivacyManager.setSetting(uid, PrivacyManager.cSettingValues, Boolean.toString(cbValues.isChecked()));
			if (userId == 0)
				PrivacyManager.setSetting(uid, PrivacyManager.cSettingLog, Boolean.toString(cbLog.isChecked()));
			PrivacyManager.setSetting(uid, PrivacyManager.cSettingSystem, Boolean.toString(cbSystem.isChecked()));
			PrivacyManager.setSetting(uid, PrivacyManager.cSettingExperimental, Boolean.toString(cbExperimental.isChecked()));
			PrivacyManager.setSetting(uid, PrivacyManager.cSettingHttps, Boolean.toString(cbHttps.isChecked()));
			PrivacyManager.setSetting(uid, PrivacyManager.cSettingAOSPMode, Boolean.toString(cbAOSP.isChecked()));
			PrivacyManager.setSetting(uid, PrivacyManager.cSettingConfidence, etConfidence.getText().toString());
		}

		// Quirks
		List<String> listQuirks = Arrays.asList(etQuirks.getText().toString().toLowerCase().replace(" ", "").split(","));
		PrivacyManager.setSetting(uid, PrivacyManager.cSettingFreeze, Boolean.toString(listQuirks.contains("freeze")));
		PrivacyManager.setSetting(uid, PrivacyManager.cSettingResolve, Boolean.toString(listQuirks.contains("resolve")));
		PrivacyManager.setSetting(uid, PrivacyManager.cSettingNoResolve, Boolean.toString(listQuirks.contains("noresolve")));
		PrivacyManager.setSetting(uid, PrivacyManager.cSettingPermMan, Boolean.toString(listQuirks.contains("permman")));
		PrivacyManager.setSetting(uid, PrivacyManager.cSettingIntentWall, Boolean.toString(listQuirks.contains("iwall")));
		PrivacyManager.setSetting(uid, PrivacyManager.cSettingSafeMode, Boolean.toString(listQuirks.contains("safemode")));
		PrivacyManager.setSetting(uid, PrivacyManager.cSettingTestVersions, Boolean.toString(listQuirks.contains("test")));
		PrivacyManager.setSetting(uid, PrivacyManager.cSettingOnDemandSystem, Boolean.toString(listQuirks.contains("odsystem")));
		PrivacyManager.setSetting(uid, PrivacyManager.cSettingWhitelistNoModify, Boolean.toString(listQuirks.contains("wnomod")));

		// Notifications
		PrivacyManager.setSetting(uid, PrivacyManager.cSettingNotify, Boolean.toString(cbNotify.isChecked()));

		if (uid != userId)
			PrivacyManager.setSetting(uid, PrivacyManager.cSettingBlacklist, Boolean.toString(cbBlacklist.isChecked()));

		// set
		finish();

		// Refresh view
		if (uid == userId) {
			Intent intent = new Intent(ActivitySettings.this, ActivityMain.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(ActivitySettings.this, ActivityApp.class);
			intent.putExtra(ActivityApp.cUid, uid);
			intent.putExtra(ActivityApp.cAction, ActivityApp.cActionRefresh);
			startActivity(intent);
		}
	}

	// Defacing
	private static String BufferedReaderJSON(String str) throws Exception
	{
		File file = new File(str);
		if (!file.exists() || file.isDirectory()) {
			// throw new FileNotFoundException();
			return null;
		} else {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String temp = null;
			StringBuffer sb = new StringBuffer();

			while ((temp = br.readLine()) != null) {
				sb.append(temp + " ");
				temp = br.readLine();
			}
			br.close();
			return sb.toString();
		}

	}

}
