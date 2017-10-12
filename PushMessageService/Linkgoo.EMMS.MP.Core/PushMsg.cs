using cn.jpush.api;
using cn.jpush.api.common;
using cn.jpush.api.common.resp;
using cn.jpush.api.push;
using cn.jpush.api.push.mode;
using Com.Linkgoo.MessagePush.BLL;
using Com.Linkgoo.MessagePush.Model;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Linkgoo.EMMS.MP.Core
{
    public delegate void AfterPushMsgEvent(PushMsg sender, PushMsgEventArgs args);
    public delegate void OnPushMsgErrorEvent(PushMsg sender, String errorInfo);
    public class PushMsg
    {
        private PushMessageBLL pushMessageBLL = new PushMessageBLL();
        private JPushClient PushClient;
        public AfterPushMsgEvent AfterPushMsgEventHandler;
        public OnPushMsgErrorEvent OnPushMsgErrorEventHandler;
        public void DoPush()
        {
            if (this.PushClient == null)
            {
                this.PushClient = new JPushClient(SysContext.Instance.AppKey, SysContext.Instance.MasterSecret);
            }
            this.singlePush(this.pushMessageBLL.getActiveMsgs());
        }

        void singlePush(List<PushMessage> msgs)
        {
            
            PushMsgTrace pushMsgTraceInfo = new PushMsgTrace();
            pushMsgTraceInfo.AppKey = SysContext.Instance.AppKey;
            pushMsgTraceInfo.TreatCount = msgs.Count;
            if (msgs.Count != 0)
            {
                int num = 0;
                pushMsgTraceInfo.BeginPushTime = DateTime.Now;
                foreach (PushMessage message in msgs)
                {
                    switch (message.TaskAssignType)
                    {
                        case 0:
                            message.AcceptIds = message.Operator_ID;
                            break;

                        case 1:
                            message.AcceptIds = message.Organise_ID;
                            break;
                    }
                    this.pushMessage(message);
                    if (message.IsActive == 0)
                    {
                        num++;
                    }
                }
                pushMsgTraceInfo.ActualCount = num;
                pushMsgTraceInfo.EndPushTime = DateTime.Now;
                pushMsgTraceInfo.Description = String.Format("预计推送{0}条消息，成功推送{1}条消息！",
                    pushMsgTraceInfo.TreatCount, pushMsgTraceInfo.ActualCount);
                if (AfterPushMsgEventHandler != null)
                {
                    AfterPushMsgEventHandler(this, new PushMsgEventArgs(pushMsgTraceInfo));
                }
            }
        }
        private void pushMessage(PushMessage pushMessage)
        {
            string messageContent = pushMessage.MessageContent;
            PushPayload payload = new PushPayload
            {
                platform = Platform.android()
            };
            switch (pushMessage.TaskAssignType)
            {
                case 0:
                    {
                        char[] separator = new char[] { ',' };
                        payload.audience = Audience.s_alias(pushMessage.AcceptIds.Split(separator));
                        break;
                    }
                case 1:
                    {
                        char[] chArray2 = new char[] { ',' };
                        payload.audience = Audience.s_tag(pushMessage.AcceptIds.Split(chArray2));
                        break;
                    }
            }
            try
            {
                payload.message = Message.content(messageContent);
                payload.options.time_to_live = SysContext.Instance.TimeToLive;
                pushMessage.LastPushDateTime = DateTime.Now;
                MessageResult result = this.PushClient.SendPush(payload);
                pushMessage.msg_id = result.msg_id;
                pushMessage.IsActive = 0;
                
            }
            catch (APIRequestException requestException)
            {
                pushMessage.RePushCount--;
                StringBuilder errorInfo = new StringBuilder("Jpush服务器返回错误，详细信息如下：\n");
                errorInfo.Append("HTTP Status:").Append(requestException.Status).Append("\n");
                errorInfo.Append("Error Code:").Append(requestException.ErrorCode).Append("\n");
                errorInfo.Append("Error Message: ").Append(requestException.ErrorMessage).Append("\n");
                errorInfo.Append("Msg ID: ").Append(requestException.MsgId);
                if (OnPushMsgErrorEventHandler != null)
                {
                    OnPushMsgErrorEventHandler(this, errorInfo.ToString());
                }
                
                //PushMsgLog.Warn("请求推送服务发生异常", exception);
            }
            catch (APIConnectionException connectException)
            {
                pushMessage.RePushCount--;
                StringBuilder errorInfo = new StringBuilder("Jpush服务器正忙，请稍后再试！\n");
                errorInfo.Append("Error Message:").Append(connectException.Message);
                if (OnPushMsgErrorEventHandler != null)
                {
                    OnPushMsgErrorEventHandler(this, errorInfo.ToString());
                }
                //PushMsgLog.Warn("连接推送服务发生异常", exception2);
            }
            finally
            {
                this.pushMessageBLL.UpdateMessage(pushMessage);
            }
        }
    }

    public class PushMsgEventArgs : EventArgs
    {
        public PushMsgTrace PushMsgTraceInfo{ get; set; }
        public PushMsgEventArgs(PushMsgTrace PushMsgTraceInfo)
        {
            this.PushMsgTraceInfo = PushMsgTraceInfo;
        }
    }
    public class PushMsgTrace
    {
        public DateTime BeginPushTime { get; set; }
        public DateTime EndPushTime { get; set; }
        public int TreatCount { get; set; }//待推送
        public int ActualCount { get; set; }//实际推送
        public String Description { get; set; }
        public String AppKey { get; set; }
    }
}
