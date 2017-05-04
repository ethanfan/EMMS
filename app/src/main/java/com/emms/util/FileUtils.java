package com.emms.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;


import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils
{

	public static void saveBitmap(Context context , Bitmap bm, String picName)
	{
		String SDPATH = context.getExternalFilesDir(null)
				+ "/btp/formats/";
		Log.e("", "保存图片");
		try
		{
			if (!isFileExist(context,""))
			{
				File tempf = createSDDir(context,"");
			}
			File f = new File(SDPATH, picName + ".JPEG");
			if (f.exists())
			{
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			Log.e("", "已经保存");
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static File createSDDir(Context context ,String dirName) throws IOException
	{
		String SDPATH = context.getExternalFilesDir(null)
				+ "/btp/formats/";
		File dir = new File(SDPATH + dirName);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
		{

			System.out.println("createSDDir:" + dir.getAbsolutePath());
			System.out.println("createSDDir:" + dir.mkdir());
		}
		return dir;
	}

	public static boolean isFileExist(Context context ,String fileName)
	{
		String SDPATH = context.getExternalFilesDir(null)
				+ "/btp/formats/";
		File file = new File(SDPATH + fileName);
		file.isFile();
		return file.exists();
	}

	public static void delFile(Context context ,String fileName)
	{
		String SDPATH = context.getExternalFilesDir(null)
				+ "/btp/formats/";
		File file = new File(SDPATH + fileName);
		if (file.isFile())
		{
			file.delete();
		}
		file.exists();
	}

	public static void deleteDir(Context context)
	{
		String SDPATH = context.getExternalFilesDir(null)
				+ "/btp/formats/";
		File dir = new File(SDPATH);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;

		for (File file : dir.listFiles())
		{
			if (file.isFile())
				file.delete(); // 删除所有文件
			else if (file.isDirectory())
				deleteDir(context); // 递规的方式删除文件夹
		}
		dir.delete();// 删除目录本身
	}

	public static boolean fileIsExists(String path)
	{
		try
		{
			File f = new File(path);
			if (!f.exists())
			{
				return false;
			}
		} catch (Exception e)
		{

			return false;
		}
		return true;
	}
	public  void upZipFile(File zipFile, String folderPath, final Context context,DownloadCallback downloadCallback) throws IOException {
		File desDir = new File(folderPath);
		if (!desDir.exists()) {
			desDir.mkdirs();
		}
		ZipFile zf = new ZipFile(zipFile);
		InputStream in = null;
		OutputStream out = null;
		try {
			for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
				ZipEntry entry = ((ZipEntry) entries.nextElement());
				in = zf.getInputStream(entry);
				String str = folderPath + File.separator + entry.getName();
				str = new String(str.getBytes("8859_1"), "UTF-8");
				File desFile = new File(str);
				if (!desFile.exists()) {
					File fileParentDir = desFile.getParentFile();
					if (!fileParentDir.exists()) {
						fileParentDir.mkdirs();
					}
					desFile.createNewFile();
				}
				out = new FileOutputStream(desFile);
				byte buffer[] = new byte[1024*1024];
				int realLength;
				try {
					while ((realLength = in.read(buffer)) > 0) {
						out.write(buffer, 0, realLength);
						out.flush();
					}
				}catch (IOException e){
					//DO nothing
					//Log.e("","");
				}
			}
			if(downloadCallback!=null) {
				downloadCallback.success(true);
			}
		} finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}
	}
	public static boolean unzip(
			File zipFile,
			String folderpath,
			DownloadCallback downloadCallback)throws IOException {
		File desDir = new File(folderpath);
		if (!desDir.exists()) {
			desDir.mkdirs();
		}
		FileInputStream fin = null;
		GZIPInputStream zin = null;
		BufferedOutputStream bufout = null;
		File file = null;
		try {
			fin = new FileInputStream(zipFile);
			zin = new GZIPInputStream(fin);
				file = new File(zipFile.getParentFile().getAbsolutePath()+"/EMMS.db");
				if (!file.exists()) {
					file.getParentFile().mkdirs();
					file.createNewFile();
				}
				FileOutputStream fout = new FileOutputStream(file);
				bufout = new BufferedOutputStream(fout);
				byte[] buffer = new byte[8096];
				int length = -1;
				while((length = zin.read(buffer)) != -1) {
					bufout.write(buffer, 0, length);//涓�娆℃�у皢缂撳啿鍖虹殑鎵�鏈夋暟鎹兘鍐欏嚭鍘�
					bufout.flush();
				}
				if(downloadCallback!=null) {
					downloadCallback.success(true);
				}
		} catch (Exception e) {
			if (file != null && file.exists()) {
				file.delete();
			}
			return false;
		} finally {
			close(fin);
			close(zin);
			close(bufout);
		}
		return true;
	}
	private static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// Ignored
			}
		}
	}
}
