package com.emms.util;

import java.io.Serializable;

/**
 * 一个图片对象
 *
 * 
 */
public class ImageItem implements Serializable {
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
	public boolean isSelected = false;
}
