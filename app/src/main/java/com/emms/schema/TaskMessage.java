package com.emms.schema;

import com.datastore_android_sdk.schema.Identity;
import com.datastore_android_sdk.schema.Model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by jaffer.deng on 2016/7/10.
 *
 */
@DatabaseTable(tableName = "TaskMessage")
public class TaskMessage extends Model<Operator, Long> implements Identity<Long> {

    public static final String MESSAGE_ID = "Message_ID";
    public static final String MESSAGE_CONTENT = "MessageContent";
    public static final String LASTUPDATETIME = "LastUpdateTime";

    public Long getMessage_ID() {
        return Message_ID;
    }

    public void setMessage_ID(Long message_ID) {
        Message_ID = message_ID;
    }

    public String getMessageContent() {
        return MessageContent;
    }

    public void setMessageContent(String messageContent) {
        MessageContent = messageContent;
    }

    @DatabaseField(id = true,
            columnName = MESSAGE_ID, canBeNull = false)
    @SerializedName(MESSAGE_ID)
    @Expose
    private Long Message_ID;

    @DatabaseField(columnName = MESSAGE_CONTENT, canBeNull = true)
    @SerializedName(MESSAGE_CONTENT)
    @Expose
    private String MessageContent;

    public String getLastupdatetime() {
        return lastupdatetime;
    }

    public void setLastupdatetime(String lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }

    @DatabaseField(columnName = LASTUPDATETIME, canBeNull = false,defaultValue = "")
    @SerializedName(LASTUPDATETIME)
    @Expose
    private String lastupdatetime;

    @Override
    public int hashCode() {
        return Message_ID.hashCode();
    }

    @Override
    public Long getIdentity() {
        return Message_ID;
    }

    @Override
    public String getIdentityAttribute() {
        return MESSAGE_ID;
    }
}
