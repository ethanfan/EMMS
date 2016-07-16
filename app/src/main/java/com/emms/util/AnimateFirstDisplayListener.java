package com.emms.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
	
	static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		if (loadedImage != null) {
			ImageView imageView = (ImageView) view;
			// �Ƿ��һ����ʾ
			boolean firstDisplay = !displayedImages.contains(imageUri);
			if (firstDisplay) {
				// ͼƬ����Ч��
				FadeInBitmapDisplayer.animate(imageView, 500);
				displayedImages.add(imageUri);
			}
		}
	}
}
