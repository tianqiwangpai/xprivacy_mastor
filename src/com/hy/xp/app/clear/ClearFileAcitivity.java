package com.hy.xp.app.clear;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.hy.xp.app.ActivityBase;
import com.hy.xp.app.ActivityMain;
import com.hy.xp.app.Common;
import com.hy.xp.app.PrivacyService;
import com.hy.xp.app.R;
import com.hy.xp.app.file.FileInfo;
import com.hy.xp.app.task.PreferenceUtils;

public class ClearFileAcitivity extends ActivityBase
{

	List<FileInfo> fileInfos = new ArrayList<FileInfo>();
	private static ExecutorService mExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new PriorityThreadFactory());
	DirAdapter mAdapter;

	private static class PriorityThreadFactory implements ThreadFactory
	{
		@Override
		public Thread newThread(Runnable r)
		{
			Thread t = new Thread(r);
			t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_clearfile);
		// Start task to get app list
		FileListTask mFileListTask = new FileListTask();
		mFileListTask.executeOnExecutor(mExecutor, (Object) null);

		TextView mTextView = (TextView) findViewById(R.id.tvPath);
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
		mTextView.setText("当前目录：" + path);
	}

	private List<FileInfo> getFileInfos()
	{
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;// 可读可写
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;// 可读
			mExternalStorageWriteable = false;// 不可写
		} else {
			mExternalStorageAvailable = mExternalStorageWriteable = false;// 不可读不可写
		}

		if (mExternalStorageWriteable && mExternalStorageAvailable) {
			File sdcard = Environment.getExternalStorageDirectory();
			// 对文件夹进行过滤筛选
			String[] filenames = sdcard.list(new FilenameFilter()
			{
				public boolean accept(File dir, String filename)
				{
					File file = new File(dir, filename);
					if (file.exists() && file.isDirectory()) {
						return true;
					}
					return false;
				}
			});

			Arrays.sort(filenames);
			fileInfos.clear();
			if (null != filenames) {
				String sdcardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
				for (String filename : filenames) {
					FileInfo mFileInfo = new FileInfo();
					mFileInfo.setFileName(filename);
					mFileInfo.setFilePath(sdcardRoot);
					fileInfos.add(mFileInfo);
				}
			}
		}
		return fileInfos;

	}

	private class FileListTask extends AsyncTask<Object, Integer, List<FileInfo>>
	{
		private ProgressDialog mProgressDialog;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			// Show progress dialog
			mProgressDialog = new ProgressDialog(ClearFileAcitivity.this);
			mProgressDialog.setMessage(getString(R.string.msg_loading));
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setProgressNumberFormat(null);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();
		}

		@Override
		protected List<FileInfo> doInBackground(Object... arg0)
		{
			// TODO Auto-generated method stub
			return getFileInfos();
		}

		@Override
		protected void onPostExecute(List<FileInfo> result)
		{
			// TODO Auto-generated method stub
			if (!ClearFileAcitivity.this.isFinishing()) {
				// Display app list
				mAdapter = new DirAdapter(ClearFileAcitivity.this, result);
				ListView lvApp = (ListView) findViewById(R.id.lv_dirs);
				lvApp.setAdapter(mAdapter);

				// Dismiss progress dialog
				if (mProgressDialog.isShowing())
					try {
						mProgressDialog.dismiss();
					} catch (IllegalArgumentException ignored) {
					}

			}
			super.onPostExecute(result);
		}

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

	private void optionSave()
	{
		// TODO Auto-generated method stub

		List<FileInfo> m = mAdapter.getList();
		for (int i = 0; i < m.size(); i++) {
			FileInfo mFileInfo = m.get(i);
			PreferenceUtils.setParam(ClearFileAcitivity.this, Common.CLEAN_FILE, mFileInfo.getFilePath() + mFileInfo.getFileName(), mFileInfo.getFilePath() + mFileInfo.getFileName());
		}
		finish();

	}

	public class DirAdapter extends BaseAdapter
	{
		private Context mContext;
		private List<FileInfo> mFileInfos;
		private List<FileInfo> mFileInfoTmps;
		List<Boolean> mChecked;
		HashMap<Integer, View> map = new HashMap<Integer, View>();
		List<Integer> listItemID = new ArrayList<Integer>();

		public DirAdapter(Context mContext, List<FileInfo> mFileInfos)
		{
			this.mContext = mContext;
			this.mFileInfos = mFileInfos;
			mChecked = new ArrayList<Boolean>();
			for (int i = 0; i < mFileInfos.size(); i++) {
				mChecked.add(false);
			}
		}

		public List<FileInfo> getList()
		{
			mFileInfoTmps = new ArrayList<FileInfo>();
			listItemID.clear();
			for (int i = 0; i < mAdapter.mChecked.size(); i++) {
				if (mAdapter.mChecked.get(i)) {
					listItemID.add(i);
				}
			}
			for (int i = 0; i < listItemID.size(); i++) {
				mFileInfoTmps.add(mFileInfos.get(listItemID.get(i)));
			}
			return mFileInfoTmps;
		}

		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return mFileInfos.size();
		}

		@Override
		public Object getItem(int arg0)
		{
			// TODO Auto-generated method stub
			return mFileInfos.get(arg0);
		}

		@Override
		public long getItemId(int arg0)
		{
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{

			View view;
			ViewHolder holder = null;

			if (map.get(position) == null) {
				LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = mInflater.inflate(R.layout.dir_item, null);
				holder = new ViewHolder();

				holder.tv_name = (TextView) view.findViewById(R.id.tv_dirName);
				holder.mCheckBox = (CheckBox) view.findViewById(R.id.cb_dir_select);
				final int p = position;
				map.put(position, view);
				holder.mCheckBox.setOnClickListener(new View.OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						CheckBox cb = (CheckBox) v;
						mChecked.set(p, cb.isChecked());
					}
				});
				view.setTag(holder);
			} else {
				// Log.e("MainActivity", "position2 = " + position);
				view = map.get(position);
				holder = (ViewHolder) view.getTag();
			}

			FileInfo mFileInfo = mFileInfos.get(position);
			holder.tv_name.setText(mFileInfo.getFileName());
			return view;
		}

		class ViewHolder
		{
			TextView tv_name;
			CheckBox mCheckBox;

			// public ViewHolder(View view)
			// {
			// tv_name = (TextView) view.findViewById(R.id.tv_dirName);
			// mCheckBox = (CheckBox) view.findViewById(R.id.cb_dir_select);
			// view.setTag(this);
			// }
		}
	}
}
