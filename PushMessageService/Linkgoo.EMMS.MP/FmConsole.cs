using Com.Linkgoo.MessagePush.Model;
using Linkgoo.EMMS.MP.Core;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Linkgoo.EMMS.MP
{
    delegate void RefreshUICallback(object args);
    public partial class FmConsole : Form
    {
        CoreService PushService;
        List<TransferInfo> TransferInfoList = new List<TransferInfo>();
        List<PushMsgTrace> PushMsgTraceList = new List<PushMsgTrace>();
        [DllImport("user32.dll", SetLastError = true)]
        static extern void SwitchToThisWindow(IntPtr hWnd, bool fAltTab);
        public FmConsole()
        {
            InitializeComponent();
            this.dgvRequestInfo.AutoGenerateColumns = false;
            this.dgvPushMsgInfo.AutoGenerateColumns = false;
        }

        private void btStart_Click(object sender, EventArgs e)
        {
            btStart.Enabled = false;
            this.AppendToTraceLog("正在启动服务……");
            if (PushService == null)
            {
                PushService = new CoreService();
                PushService.AfterFetchDataHandler += PushService_AfterFetchDataHandler;
                PushService.AfterPushMsgEventHandler += PushService_AfterPushMsgEventHandler;
                PushService.OnPushMsgErrorEventHandler += PushService_OnPushMsgErrorEventHandler;
                PushService.FinishStopServiceEventHandler += PushService_FinishStopServiceEventHandler;
            }

            PushService.StartService();
            this.AppendToTraceLog("服务已启动……");
            btnStop.Enabled = true;
        }

        private void btnStop_Click(object sender, EventArgs e)
        {
            btnStop.Enabled = false;
            this.AppendToTraceLog("正在停止服务……");
            PushService.StopService();
        }

        private void PushService_OnPushMsgErrorEventHandler(PushMsg sender, string errorInfo)
        {
            this.RefreshRequestInfo(errorInfo);
        }
        private void PushService_AfterPushMsgEventHandler(PushMsg sender, PushMsgEventArgs args)
        {
            this.RefreshRequestInfo(args);
        }

        private void PushService_AfterFetchDataHandler(object sender, FetchDataEventArgs args)
        {
            this.RefreshRequestInfo(args);
        }
        private void PushService_FinishStopServiceEventHandler(object sender)
        {
            this.RefreshRequestInfo(null);
        }
        void RefreshRequestInfo(object args)
        {
            if (this.dgvRequestInfo.InvokeRequired||this.dgvPushMsgInfo.InvokeRequired||tbTraceLog.InvokeRequired)
            {
                this.Invoke(new RefreshUICallback(RefreshRequestInfo), args);
            }
            else
            {
                if(args is FetchDataEventArgs)
                {
                    FetchDataEventArgs fetchDataEventArgs = args as FetchDataEventArgs;
                    if (fetchDataEventArgs.TransferInfoTrace.PushMessageList!=null&&
                        fetchDataEventArgs.TransferInfoTrace.PushMessageList.Count > 0)
                    {
                        TransferInfoList.Add((args as FetchDataEventArgs).TransferInfoTrace);
                        this.dgvRequestInfo.DataSource = new BindingList<TransferInfo>(TransferInfoList);
                        this.dgvRequestInfo.Refresh();
                        this.AppendToTraceLog((args as FetchDataEventArgs).TransferInfoTrace.ResponseInfo);
                    }
                    else
                    {
                        this.AppendToTraceLog("无最新数据……");
                    }
                }
                else if(args is PushMsgEventArgs)
                {
                    PushMsgTraceList.Add((args as PushMsgEventArgs).PushMsgTraceInfo);
                    this.dgvPushMsgInfo.DataSource = new BindingList<PushMsgTrace>(PushMsgTraceList);
                    this.dgvPushMsgInfo.Refresh();
                    this.AppendToTraceLog((args as PushMsgEventArgs).PushMsgTraceInfo.Description);
                }
                else if(args is String)
                {
                    this.AppendToTraceLog(args as String);
                }
                else
                {
                    this.AppendToTraceLog("服务已停止……");
                    btStart.Enabled = true;
                }
            }
        }
        void AppendToTraceLog(String log)
        {
            if(tbTraceLog.Lines.Count()>10000)
            {
                tbTraceLog.Clear();
            }
            tbTraceLog.AppendText(String.Format("({0}):{1}\n",DateTime.Now, log));
            tbTraceLog.ScrollToCaret();
        }
        void RefreshData(BackgroundWorker worker, DoWorkEventArgs e)
        {
            
        }

        private void bgwConsole_DoWork(object sender, DoWorkEventArgs e)
        {
            TransferInfoList.Add(e.Argument as TransferInfo);
            this.dgvRequestInfo.DataSource = new BindingList<TransferInfo>(TransferInfoList);
            this.dgvRequestInfo.Refresh();
        }

        private void FmConsole_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (PushService!=null&&PushService.IsRunning)
            {
                e.Cancel = true;
                MessageBox.Show("服务正在运行，请先停止服务！");
            }
            else
            {
                FmCheckPassWord fmCheckPassWord = new FmCheckPassWord();
                fmCheckPassWord.ShowDialog();
                e.Cancel = true;
            }
        }

        private void miShow_Click(object sender, EventArgs e)
        {
            this.nfIPrinter.Visible = false;
            this.Visible = true;
            this.WindowState = FormWindowState.Normal;
            this.Activate();
        }

        private void miExit_Click(object sender, EventArgs e)
        {
            this.nfIPrinter.Visible = false;
            this.Close();
        }

        private void nfIPrinter_MouseDoubleClick(object sender, MouseEventArgs e)
        {
            if (this.WindowState == System.Windows.Forms.FormWindowState.Minimized)
            {
                this.Show();
                this.nfIPrinter.Visible = false;
                Process procCurrent = Process.GetCurrentProcess();
                SwitchToThisWindow(procCurrent.MainWindowHandle, true);
            }
        }

        private void FmConsole_SizeChanged(object sender, EventArgs e)
        {
            if (this.WindowState == System.Windows.Forms.FormWindowState.Minimized)
            {
                this.nfIPrinter.Visible = true;
                this.Hide();
            }
            if (this.WindowState == FormWindowState.Normal)
            {
                this.nfIPrinter.Visible = false;
            }
        }

        private void btnSetAutoRun_Click(object sender, EventArgs e)
        {
            string path = Application.StartupPath;
            if (Untity.SetAutoRun(path + @"\PrintService.exe", true) == "1")
            {
                MessageBox.Show("设置成功！");
            }
        }
    }
}
