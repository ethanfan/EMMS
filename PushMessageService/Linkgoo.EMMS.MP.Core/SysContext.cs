namespace Linkgoo.EMMS.MP.Core
{
    using Com.Linkgoo.MessagePush.BLL;
    using Com.Linkgoo.MessagePush.VO;
    using System;
    using System.Collections.Generic;

    public class SysContext
    {

        private static SysContext Singleton = null;

        private SysContext()
        {
        }

        private void LoadQuicklyValue()
        {
            if (this.SysConstants.ContainsKey("AppKey"))
            {
                this.AppKey = this.SysConstants["AppKey"].Value;
            }
            if (this.SysConstants.ContainsKey("MasterSecret"))
            {
                this.MasterSecret = this.SysConstants["MasterSecret"].Value;
            }
            if (this.SysConstants.ContainsKey("Audience_Tag"))
            {
                this.Audience_Tag = this.SysConstants["Audience_Tag"].Value;
            }
            if (this.SysConstants.ContainsKey("PushTimeSpan"))
            {
                this.PushTimeSpan = int.Parse(this.SysConstants["PushTimeSpan"].Value);
            }
            if (this.SysConstants.ContainsKey("CheckPushStateTimeSpan"))
            {
                this.CheckPushStateTimeSpan = int.Parse(this.SysConstants["CheckPushStateTimeSpan"].Value);
            }
            if (this.SysConstants.ContainsKey("TimeToLive"))
            {
                try
                {
                    this.TimeToLive = int.Parse(this.SysConstants["TimeToLive"].Value);
                }
                catch (Exception)
                {
                    this.TimeToLive = 0x4650L;
                }
            }
            else
            {
                this.TimeToLive = 0x4650L;
            }
        }

        public string AppKey { get; set; }

        public string Audience_Tag { get; set; }

        public int CheckPushStateTimeSpan { get; set; }

        public static SysContext Instance
        {
            get
            {
                if (Singleton == null)
                {
                    Singleton = new SysContext();
                    Singleton.SysConstants = new SysConstantsBLL().getAllDicSysConstantsVO();
                    Singleton.LoadQuicklyValue();
                }
                return Singleton;
            }
        }

        public string MasterSecret { get; set; }

        public int PushSpan { get; set; }

        public int PushTimeSpan { get; set; }

        public Dictionary<string, SysConstantsVO> SysConstants { get; set; }

        public long TimeToLive { get; set; }
    }
}

