package com.emms.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
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
	public  void upZipFile(File zipFile, String folderPath, final Context context,DownloadCallback downloadCallback) throws ZipException, IOException {
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
				str = new String(str.getBytes("8859_1"), HTTP.UTF_8);
				File desFile = new File(str);
				if (!desFile.exists()) {
					File fileParentDir = desFile.getParentFile();
					if (!fileParentDir.exists()) {
						fileParentDir.mkdirs();
					}
					desFile.createNewFile();
				}
				out = new FileOutputStream(desFile);
				byte buffer[] = new byte[1024];
				int realLength;
				while ((realLength = in.read(buffer)) > 0) {
					out.write(buffer, 0, realLength);
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

}
