using Com.Linkgoo.MessagePush.BLL;
using Com.Linkgoo.MessagePush.Model;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Linkgoo.EMMS.MP.Core
{
    public delegate void AfterFetchData(object sender, FetchDataEventArgs e);
    public class FetchData
    {
        public event AfterFetchData AfterFetchDataHandler;
        PushMessageBLL pushMessageBLL;
        public FetchData()
        {

        }
        public async void DoFetch()
        {
            if (pushMessageBLL == null)
            {
                pushMessageBLL = new PushMessageBLL();
            }
            HttpClient client = new HttpClient();
            TransferInfo transferInfo = new TransferInfo();
            string url = SysContext.Instance.SysConstants["TaskMessagePushURL"].Value + "?factory=GEW";
            //创建HttpClient（注意传入HttpClientHandler）
            var handler = new HttpClientHandler() { AutomaticDecompression = DecompressionMethods.GZip };

            string lastUpdateTime = pushMessageBLL.GetLastTimeStamp();
            if (!string.IsNullOrEmpty(lastUpdateTime))
            {
                url = url + "&lastUpdateTime=" + lastUpdateTime;
            }
            else
            {
                url = url + "&lastUpdateTime=0";
            }
            transferInfo.LastUpdateTime = lastUpdateTime;
            using (var http = new HttpClient(handler))
            {
                http.DefaultRequestHeaders.Add("Origin", "http://EMMSAPP");
                http.DefaultRequestHeaders.Add("Referer", "http://EMMSAPP");
                transferInfo.RequestTime = DateTime.Now;//请求时间
                //await异步等待回应
                var response = await http.GetAsync(url);
                //确保HTTP成功状态值
                response.EnsureSuccessStatusCode();

                //await异步读取最后的JSON（注意此时gzip已经被自动解压缩了，因为上面的AutomaticDecompression = DecompressionMethods.GZip）
                List<PushMessage> msgs = JsonConvert.DeserializeObject<List<PushMessage>>(await response.Content.ReadAsStringAsync());
                transferInfo.ResponseTime = DateTime.Now;
                //PushMsgLog.InfoFormat("总过获取{0}条记录.", msgs.Count);
                pushMessageBLL.SaveMessages(msgs);
                if (this.AfterFetchDataHandler != null)
                {
                    transferInfo.RequestURL = url;
                    transferInfo.ResponseInfo = String.Format("总过获取{0}条记录.",msgs.Count);
                    this.AfterFetchDataHandler(this,new FetchDataEventArgs(transferInfo));
                }
            }
        }
    }
    public class FetchDataEventArgs:EventArgs
    {
        public List<PushMessage> PushMessageList { get; set; }
        public FetchDataEventArgs(List<PushMessage> pushMessageList)
        {
            this.PushMessageList = pushMessageList;
        }

        public TransferInfo TransferInfoTrace { get; set; }
        public FetchDataEventArgs(TransferInfo TransferInfoTrace)
        {
            this.TransferInfoTrace = TransferInfoTrace;
        }
    }
    public class TransferInfo
    {
        public String RequestURL { get; set; }
        public DateTime RequestTime { get; set; }
        public String LastUpdateTime { get; set; }
        public DateTime ResponseTime { get; set; }
        public String ResponseInfo { get; set; }
        public List<PushMessage> PushMessageList { get; set; }
    }
}
