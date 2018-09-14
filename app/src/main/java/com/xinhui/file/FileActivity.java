package com.xinhui.file;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

import com.artifex.mupdf.MuPDFActivity;
import com.xinhui.electronicsignature.R;



import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/***
 * 项目：ElectronicSignature
 * 类名：FileActivity
 * 功能：实现对手机文件目录的显示及操作
 * 创建时间：2013-12-10
 * 创建人：LXH
 */
public class FileActivity extends ListActivity{
	/**
	 * 要显示的文件名
	 */
	private ArrayList<String> items;
	/**
	 * 显示文件路径
	 */
	private ArrayList<String> paths;
	/**
	 * 根目录
	 */
	private String rootPath = "/";
	private TextView ram_total_tv,sd_total_tv,ram_available_tv,sd_available_tv;
	private MemoryInfo memoryInfo;
	private Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//隐去标题栏（应用程序的名字）
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//设置一个Activity的显示界面
		setContentView(R.layout.file_layout);
		ram_total_tv = (TextView) findViewById(R.id.ram_total_tv);
		sd_total_tv = (TextView) findViewById(R.id.sd_total_tv);
		ram_available_tv = (TextView) findViewById(R.id.ram_available_tv);
		sd_available_tv = (TextView) findViewById(R.id.sd_available_tv);
		getFileDir(rootPath);// 获取文件列表
		/*动态更新内存信息*/
		handler = new Handler();
		handler.post(thread);

	}

	Runnable thread = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.postDelayed(thread, 1000*3);
			getMemoryInfo();//获取存储信息
		}
	};
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(thread);
	};
	// List中item的点击事件
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		File f = new File(paths.get(position));

		if (f.isDirectory()) {
			//判断文件或文件夹是否可读，不可读提示，可读进入下一目录
			if(f.canRead()){
				getFileDir(paths.get(position));
			}else{
				Toast.makeText(this, "权限不足！", Toast.LENGTH_SHORT).show();
			}
		}else{
			if(f.canRead()){
				if((f.getName()).contains(".pdf")){
					Toast.makeText(this, "正在解析", Toast.LENGTH_SHORT).show();
					String openFile = paths.get(position);
					Uri uri = Uri.parse(openFile);
					Intent intent = new Intent(getApplicationContext(), MuPDFActivity.class);
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(uri);
					startActivity(intent);
				}else{
					Toast.makeText(this, "请选择PDF文件", Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(this, "不可读文件！", Toast.LENGTH_SHORT).show();
			}
		}
	}
	private void getFileDir(String path) {
		// TODO Auto-generated method stub
		//showXPath.setText(path);//显示当前路径
		items = new ArrayList<String>();
		paths = new ArrayList<String>();

		File presentFile = new  File(path);
		File[] files = presentFile.listFiles();
		// 显示当前路径，并返回根目录
		items.add("当前路径："+path+" 文件个数："+files.length);
		paths.add(rootPath);
		if (! path.equals(rootPath)) {
			// 返回上一级目录
			items.add("Back");
			paths.add(presentFile.getParent());
		}
		// 添加当前路径下的所有的文件名和路径
		for (File f : files) {
			items.add(f.getName());
			paths.add(f.getPath());
		}
		// 设置列表适配器
		setListAdapter(new FileListAdapter(FileActivity.this, items, paths));
	}
	private void getMemoryInfo(){
		memoryInfo = new MemoryInfo();

		float long1 = memoryInfo.getAvailableInternalMemorySize(); //内部可用存储空间
		float long2 = memoryInfo.getTotalInternalMemorySize();  //内部总共存储空间
		float long3 = memoryInfo.getAvailableExternalMemorySize();  //外部可用存储空间
		float long4 = memoryInfo.getTotalExternalMemorySize(); //外部总共存储空间
		if(long2/(1024*1024) >= 1024){
			BigDecimal b2 = new BigDecimal(long2/(1024*1024*1024));
			long2 = b2.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();//表明四舍五入，保留两位小数
			ram_total_tv.setText(long2+"G");
		}else{
			BigDecimal b2 = new BigDecimal(long2/(1024*1024));
			long2 = b2.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();//表明四舍五入，保留两位小数
			ram_total_tv.setText(long2+"M");
		}
		if (long4/(1024*1024) >= 1024) {
			BigDecimal b4 = new BigDecimal(long4/(1024*1024*1024));
			long4 = b4.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();//表明四舍五入，保留两位小数
			sd_total_tv.setText(long4+"G") ;
		}else{
			BigDecimal b4 = new BigDecimal(long2/(1024*1024));
			long4 = b4.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();//表明四舍五入，保留两位小数
			sd_total_tv.setText(long4+"M") ;
		}
		if(long1/(1024*1024) >= 1024){
			BigDecimal b1 = new BigDecimal(long1/(1024*1024*1024));
			long1 = b1.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();//表明四舍五入，保留两位小数
			ram_available_tv.setText(long1+"G") ;
		}else{
			BigDecimal b1 = new BigDecimal(long2/(1024*1024));
			long1 = b1.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();//表明四舍五入，保留两位小数
			ram_available_tv.setText(long1+"M") ;
		}
		if(long3/(1024*1024) >= 0){
			BigDecimal b3 = new BigDecimal(long3/(1024*1024*1024));
			long3 = b3.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();//表明四舍五入，保留两位小数
			sd_available_tv.setText(long3+"G");
		}else{
			BigDecimal b3 = new BigDecimal(long2/(1024*1024));
			long3 = b3.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();//表明四舍五入，保留两位小数
			sd_available_tv.setText(long3+"M") ;
		}
	}

	@Override
	public void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
	}
}
