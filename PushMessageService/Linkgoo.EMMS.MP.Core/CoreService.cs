using cn.jpush.api;
using Com.Linkgoo.MessagePush.BLL;
using Com.Linkgoo.MessagePush.Model;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Linkgoo.EMMS.MP.Core
{
    public delegate void FinishStopServiceEvent(object sender);
    public class CoreService
    {
        private FetchData MFetchData;
        private PushMsg PushMsgService;
        public bool IsRunning = false;

        private PushMessageBLL pushMessageBLL;

        #region 事件
        public event AfterFetchData AfterFetchDataHandler;
        public event AfterPushMsgEvent AfterPushMsgEventHandler;
        public event OnPushMsgErrorEvent OnPushMsgErrorEventHandler;
        public event FinishStopServiceEvent FinishStopServiceEventHandler;
        #endregion

        public CoreService()
        {
        }
        public void StartService()
        {
            if (this.pushMessageBLL == null)
            {
                this.pushMessageBLL = new PushMessageBLL();
            }
            if (this.MFetchData == null)
            {
                this.MFetchData = new FetchData();
                this.MFetchData.AfterFetchDataHandler += this.AfterFetchDataHandler;
            }
            if (this.PushMsgService == null)
            {
                PushMsgService = new PushMsg();
                PushMsgService.AfterPushMsgEventHandler += AfterPushMsgEventHandler;
                PushMsgService.OnPushMsgErrorEventHandler += OnPushMsgErrorEventHandler;
            }
            IsRunning = true;
            ThreadPool.QueueUserWorkItem(new WaitCallback(this.runService));
        }
        private void runService(object data)
        {
            while (IsRunning)
            {
                this.MFetchData.DoFetch();
                this.PushMsgService.DoPush();
                Thread.Sleep(SysContext.Instance.PushTimeSpan);
            }
            if (FinishStopServiceEventHandler != null)
            {
                FinishStopServiceEventHandler(this);
            }
        }
        public void StopService()
        {
            this.IsRunning = false;
        }
    }
}
