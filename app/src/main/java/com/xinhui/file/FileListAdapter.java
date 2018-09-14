package com.xinhui.file;

import java.io.File;
import java.util.List;

import com.xinhui.electronicsignature.R;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/***
 * 项目：ElectronicSignature
 * 类名：FileListAdapter
 * 功能：文件目录适配器
 * 创建时间：2013-12-10
 * 创建人：LXH
 */
public class FileListAdapter extends BaseAdapter{

	private List<String> items;
	private List<String> paths;
	private LayoutInflater inflater;//声明解析XML文件对象
	private Bitmap rootIcon;
	private Bitmap prevIcon;
	private Bitmap docIcon;
	private Bitmap NotReaddocIcon;
	private Bitmap folderIcon;
	private Bitmap NotReadfolderIcon;
	private Bitmap PdfFileIcon;
	private Bitmap APKFileIcon;
	private Bitmap MP3FileIcon;
	private Bitmap PicFileIcon;
	public FileListAdapter(Context context, List<String> items,
						   List<String> paths) {
		// 从context获取一个布局加载器
		inflater = LayoutInflater.from(context);
		this.items = items;
		this.paths = paths;
		// 初始化关联图标
		rootIcon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.sym_keyboard_shift);
		prevIcon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.sym_keyboard_return);//返回上一级菜单
		docIcon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_gesturebuilder);//显示文本文件
		NotReaddocIcon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_gesturebuilder_notread);//显示文本文件
		folderIcon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.live_folder_notes);//显示文件夹
		NotReadfolderIcon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.live_folder_cnntread);//显示不可读文件夹
		PdfFileIcon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ico_pdf);
		APKFileIcon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.apk_icon);
		MP3FileIcon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.mp3);
		PicFileIcon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.picture);
	}
	@Override
	public int getCount() {
		// 返回数据的记录条数
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		// 返回列表中的数据
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		// 返回item的id值
		return position;
	}
	/* 获取ListView中的item组件容器*/
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null ;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.file_list_item_layout, null);
			// 创建子组件容器
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.fileNameText);
			holder.imageIcon = (ImageView) convertView.findViewById(R.id.imageIcon);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String fileName = items.get(position);
		getFileIco(holder,fileName,position);
		holder.text.setText(items.get(position));
		return convertView;
	}

	/**
	 * 功能：判断文件类型，并设置相应图片
	 */
	private void getFileIco(ViewHolder holder,String fileName,int position) {
		//String end = fileName.substring(fileName.indexOf(".") + 1).toLowerCase();
		File f = new File(paths.get(position));
		//如果f表示的是一个目录则返回true
		if(f.isDirectory()){
			if(fileName.contains("当前路径")){
				holder.imageIcon.setImageBitmap(rootIcon);
			}else if(fileName.equals("Back")){
				holder.imageIcon.setImageBitmap(prevIcon);
			}else if(f.canRead()){
				holder.imageIcon.setImageBitmap(folderIcon);
			}else{
				holder.imageIcon.setImageBitmap(NotReadfolderIcon);
			}
		}else{
			if(f.canRead()){
				//判断文件类型
				if(fileName.contains(".pdf")){
					holder.imageIcon.setImageBitmap(PdfFileIcon);
				}else if(fileName.contains(".apk")){
					holder.imageIcon.setImageBitmap(APKFileIcon);
				}else if(fileName.contains(".mp3") || fileName.contains(".ape") || fileName.contains(".flac")){
					holder.imageIcon.setImageBitmap(MP3FileIcon);
				}else if(fileName.contains(".png") || fileName.contains(".jpg") || fileName.contains(".bmp")){
					holder.imageIcon.setImageBitmap(PicFileIcon);
				}else{
					holder.imageIcon.setImageBitmap(docIcon);
				}
			}else{
				holder.imageIcon.setImageBitmap(NotReaddocIcon);
			}
		}

	}
	/***
	 * 项目：ElectronicSignature
	 * 类名：ViewHolder
	 * 功能：用于存放控件的内部容器类
	 * 创建时间：2013-12-10
	 * 创建人：LXH
	 */
	private class ViewHolder {
		TextView text;
		ImageView imageIcon;
	}
}
