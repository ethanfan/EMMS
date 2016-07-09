/** 
 * Article.java
 *
 * Copyright (c) 2008-2014 Joy Aether Limited. All rights reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * 
 * This unpublished material is proprietary to Joy Aether Limited.
 * All rights reserved. The methods and
 * techniques described herein are considered trade secrets
 * and/or confidential. Reproduction or distribution, in whole
 * or in part, is forbidden except by express written permission
 * of Joy Aether Limited.
 */
package com.emms.schema;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.jaffer_datastore_android_sdk.schema.BlobDatabaseField;
import com.jaffer_datastore_android_sdk.schema.Identity;
import com.jaffer_datastore_android_sdk.schema.Model;


/**
 * 
 * @author joyaether
 * 
 */
@DatabaseTable(tableName = "articles")
public class Article extends Model<Article, String> implements Identity<String> {

	// For QueryBuilder to be able to find the fields
	public static final String ID_FIELD_NAME = "id";
	public static final String CHANNEL_FIELD_NAME = "channel";
	public static final String ARTICLE_DATE_FIELD_NAME = "article_date";
	public static final String SEQUENCE_FIELD_NAME = "sequence";
	public static final String TITLE_FIELD_NAME = "title";
	public static final String AUTHOR_FIELD_NAME = "author";
	public static final String THUMBNAIL_FIELD_NAME = "thumbnail";
	public static final String IMAGE_FIELD_NAME = "image";
	public static final String STATUS_FIELD_NAME = "status";
	public static final String CHANNEL_ARTICLE_FIELD_NAME = "channel_articles";
	public static final String ARTICLE_CONTENT_FIELD_NAME = "artcle_contents";
	public static final String ARTICLE_REGION_FIELD_NAME = "article_regions";

	public static final int STATUS_TO_BE_APPROVED = 0;
	public static final int STATUS_APPROVED = 1;
	public static final int STATUS_REJECTED = 2;

	@DatabaseField(id = true, 
			columnName = ID_FIELD_NAME, canBeNull = false)
	@SerializedName(ID_FIELD_NAME)
	@Expose
	private String id;

	@DatabaseField(columnName = ARTICLE_DATE_FIELD_NAME, canBeNull = false, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
	@SerializedName(ARTICLE_DATE_FIELD_NAME)
	@Expose
	private Date articleDate;

	@DatabaseField(columnName = SEQUENCE_FIELD_NAME)
	@SerializedName(SEQUENCE_FIELD_NAME)
	@Expose
	private Long sequence;

	@DatabaseField(columnName = TITLE_FIELD_NAME, canBeNull = false)
	@SerializedName(TITLE_FIELD_NAME)
	@Expose
	private String title;

	@DatabaseField(columnName = AUTHOR_FIELD_NAME, canBeNull = false)
	@SerializedName(AUTHOR_FIELD_NAME)
	@Expose
	private String author;

	@DatabaseField(columnName = THUMBNAIL_FIELD_NAME)
	@BlobDatabaseField(baseURI = "/resources/" + THUMBNAIL_FIELD_NAME)
	@SerializedName(THUMBNAIL_FIELD_NAME)
	@Expose
	private String thumbnail;

	@DatabaseField(columnName = IMAGE_FIELD_NAME)
	@BlobDatabaseField(baseURI = "/resources/" + IMAGE_FIELD_NAME)
	@SerializedName(IMAGE_FIELD_NAME)
	@Expose
	private String image;

	// 3 status: 0 - waiting-to-be-approved, 1 - approved, 2 - rejected
	@DatabaseField(columnName = STATUS_FIELD_NAME, canBeNull = false, defaultValue = "0")
	@SerializedName(STATUS_FIELD_NAME)
	@Expose
	private Long status;

	@DatabaseField(columnName = CREATED_DATE_FIELD_NAME, canBeNull = false, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
	@SerializedName(CREATED_DATE_FIELD_NAME)
	@Expose
	private Date createdDate;

	@DatabaseField(columnName = LAST_MODIFIED_DATE_FIELD_NAME, canBeNull = false, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
	@SerializedName(LAST_MODIFIED_DATE_FIELD_NAME)
	@Expose
	private Date lastModifiedDate;

//	@ForeignCollectionField(eager = false)
//	@SerializedName(CHANNEL_ARTICLE_FIELD_NAME)
//	@Expose
//	private ForeignCollection<ChannelArticle> channelArticles;
//
//	@ForeignCollectionField(eager = false)
//	@SerializedName(ARTICLE_CONTENT_FIELD_NAME)
//	@Expose
//	private ForeignCollection<ArticleContent> articleContents;
//
//	@ForeignCollectionField(eager = false)
//	@SerializedName(ARTICLE_REGION_FIELD_NAME)
//	@Expose
//	private ForeignCollection<ArticleRegion> articleRegions;


	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String getIdentity() {
		return id;
	}

	@Override
	public String getIdentityAttribute() {
		return ID_FIELD_NAME;
	}

	public Article() {
		// all persisted classes must define a no-arg constructor with at least
		// package visibility
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getArticleDate() {
		return articleDate;
	}

	public void setArticleDate(Date articleDate) {
		this.articleDate = articleDate;
	}

	public Long getSequence() {
		return sequence;
	}

	public void setSequence(Long sequence) {
		this.sequence = sequence;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	/**
	 * @return the lastModifiedDate
	 */
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * @param lastModifiedDate
	 *            the lastModifiedDate to set
	 */
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the articleContents
	 */
//	public ForeignCollection<ArticleContent> getArticleContents() {
//		return articleContents;
//	}

	/**
	 * @param articleContents
	 *            the articleContents to set
//	 */
//	public void setArticleContents(
//			ForeignCollection<ArticleContent> articleContents) {
//		this.articleContents = articleContents;
//	}
//
//	/**
//	 * @return the channelArticles
//	 */
//	public ForeignCollection<ChannelArticle> getChannelArticles() {
//		return channelArticles;
//	}
//
//	/**
//	 * @param channelArticles
//	 *            the channelArticles to set
//	 */
//	public void setChannelArticles(
//			ForeignCollection<ChannelArticle> channelArticles) {
//		this.channelArticles = channelArticles;
//	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author
	 *            the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

}
