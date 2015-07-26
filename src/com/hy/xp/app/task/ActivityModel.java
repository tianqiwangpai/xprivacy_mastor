package com.hy.xp.app.task;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.hy.xp.app.ActivityBase;
import com.hy.xp.app.PrivacyService;
import com.hy.xp.app.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityModel extends ActivityBase
{
	private static final int ACTIVITY_IMPORT_SELECT = 0;
	private String mFileName = null;
	private static XpmodeParser parser;
	private ListView mListView;
	List<xpmodel> xpmodelsList;
	private ModelAdapter modelAdapter;
	private static ExecutorService mExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new PriorityThreadFactory());

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
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (!PrivacyService.checkClient())
			return;

		setContentView(R.layout.model);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mListView = (ListView) findViewById(R.id.task_model_list);
		xpmodelsList = DBMgr.getInstance(ActivityModel.this).getXpmpdellist();

		modelAdapter = new ModelAdapter(ActivityModel.this, xpmodelsList);
		mListView.setAdapter(modelAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		if (inflater != null && PrivacyService.checkClient())
		{
			inflater.inflate(R.menu.model, menu);
			return true;
		} else
			return false;
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		xpmodelsList = DBMgr.getInstance(ActivityModel.this).getXpmpdellist();
		modelAdapter = new ModelAdapter(ActivityModel.this, xpmodelsList);
		mListView.setAdapter(modelAdapter);

		if (modelAdapter != null)
		{
			modelAdapter.notifyDataSetChanged();
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.menu_model_import:
			optionImport();
			return true;
		case R.id.menu_model_ref:
			optionRefresh();
			return true;
		case R.id.menu_model_delete:
			optionDelete();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void optionDelete()
	{
		// TODO Auto-generated method stub
		// Check for availability of sharing intent
		DBMgr.getInstance(ActivityModel.this).del_xp_model();
		optionRefresh();
	}

	private void optionRefresh()
	{
		// TODO Auto-generated method stub
		xpmodelsList = DBMgr.getInstance(ActivityModel.this).getXpmpdellist();
		modelAdapter = new ModelAdapter(ActivityModel.this, xpmodelsList);
		mListView.setAdapter(modelAdapter);
		if (modelAdapter != null)
			modelAdapter.notifyDataSetChanged();
	}

	private void optionImport()
	{
		// TODO Auto-generated method stub
		fileChooser();
	}

	public void fileChooser()
	{
		Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
		Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
		chooseFile.setDataAndType(uri, "text/xml");
		Intent intent = Intent.createChooser(chooseFile, getString(R.string.app_name));
		startActivityForResult(intent, ACTIVITY_IMPORT_SELECT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// Import select
		if (requestCode == ACTIVITY_IMPORT_SELECT)
			if (resultCode == RESULT_CANCELED || data == null)
				finish();
			else
			{
				String fileName = data.getData().getPath();
				mFileName = fileName.replace("/document/primary:", Environment.getExternalStorageDirectory().getAbsolutePath() + File.separatorChar);
				showFileName();
			}
	}

	private void showFileName()
	{
		// TODO Auto-generated method stub
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(ActivityModel.this);
		mBuilder.setTitle("导入提醒");
		mBuilder.setIcon(R.drawable.ic_launcher);
		mBuilder.setMessage(mFileName);
		mBuilder.setNegativeButton(ActivityModel.this.getString(android.R.string.ok), new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface arg0, int arg1)
			{
				// TODO Auto-generated method stub
				Add_TaskAsyncTask mAsyncTask = new Add_TaskAsyncTask();
				mAsyncTask.executeOnExecutor(mExecutor, null);

			}
		}).setNeutralButton(ActivityModel.this.getString(android.R.string.cancel), new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface arg0, int arg1)
			{
				// TODO Auto-generated method stub
				arg0.cancel();

			}
		}).create().show();

	}

	public static List<xpmodel> readcurray_DataBase(String path) throws Exception
	{
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			File file = new File(path);
			parser = new XpmodelByXml();
			if (parser != null)
			{
				return parser.parse(new FileInputStream(file));
			}
		}
		return null;
	}

	class ModelAdapter extends BaseAdapter
	{

		private Context mContext;
		private List<xpmodel> mXpmodels;

		public ModelAdapter(Context mContext, List<xpmodel> mXpmodels)
		{
			this.mContext = mContext;
			this.mXpmodels = mXpmodels;
		}

		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return mXpmodels.size();
		}

		@Override
		public Object getItem(int arg0)
		{
			// TODO Auto-generated method stub
			return mXpmodels.get(arg0);
		}

		@Override
		public long getItemId(int arg0)
		{
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2)
		{
			// TODO Auto-generated method stub
			ViewHolder mHolder;
			if (arg1 == null)
			{
				mHolder = new ViewHolder();
				arg1 = LayoutInflater.from(mContext).inflate(R.layout.model_item, null);
				mHolder.mtvmodel = (TextView) arg1.findViewById(R.id.tvmodel);
				mHolder.mtvproduct = (TextView) arg1.findViewById(R.id.tvproduct);
				mHolder.mtvmanufacturer = (TextView) arg1.findViewById(R.id.tvmanufacturer);
				mHolder.mtvdensity = (TextView) arg1.findViewById(R.id.tvdensity);
				arg1.setTag(mHolder);
			} else
			{
				mHolder = (ViewHolder) arg1.getTag();
			}
			// set data
			xpmodel mXpmodel = mXpmodels.get(arg0);
			mHolder.mtvmodel.setText(mXpmodel.getModel());
			mHolder.mtvproduct.setText(mXpmodel.getProduct());
			mHolder.mtvmanufacturer.setText(mXpmodel.getManufacturer());
			mHolder.mtvdensity.setText(mXpmodel.getDensity());
			return arg1;
		}

		class ViewHolder
		{
			TextView mtvmodel;
			TextView mtvproduct;
			TextView mtvmanufacturer;
			TextView mtvdensity;
		}
	}

	public class Add_TaskAsyncTask extends AsyncTask<Object, Integer, List<xpmodel>>
	{
		ProgressDialog mProgressDialog;

		@Override
		protected void onPreExecute()
		{
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(ActivityModel.this);
			mProgressDialog.setMessage("正在执行操作...");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			// mProgressDialog.setProgressNumberFormat(null);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();
		}

		@Override
		protected List<xpmodel> doInBackground(Object... arg0)
		{
			// TODO Auto-generated method stub
			try
			{
				return readcurray_DataBase(mFileName);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(List<xpmodel> result)
		{
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (!ActivityModel.this.isFinishing())
			{
				mProgressDialog.dismiss();
				if (result != null)
				{
					for (int i = 0; i < result.size(); i++)
					{
						ContentValues values = new ContentValues();
						values.put(xpmodel.MODEL, result.get(i).getModel());
						values.put(xpmodel.MANUFACTURER, result.get(i).getManufacturer());
						values.put(xpmodel.PRODUCT, result.get(i).getProduct());
						values.put(xpmodel.DENSITY, result.get(i).getDensity());
						values.put(xpmodel.FALG, result.get(i).getFlag());
						long s = DBMgr.getInstance(ActivityModel.this).add_xp_model_improt(ActivityModel.this, values);

					}
				} else
				{
					Toast.makeText(ActivityModel.this, "无效的文件！", 1).show();
				}
				optionRefresh();
			}

		}

	}

}
