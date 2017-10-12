namespace Linkgoo.EMMS.MP
{
    partial class FmConsole
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmConsole));
            this.toolStripContainer1 = new System.Windows.Forms.ToolStripContainer();
            this.tabControl1 = new System.Windows.Forms.TabControl();
            this.tabPage3 = new System.Windows.Forms.TabPage();
            this.tbTraceLog = new System.Windows.Forms.TextBox();
            this.tabPage2 = new System.Windows.Forms.TabPage();
            this.dgvPushMsgInfo = new System.Windows.Forms.DataGridView();
            this.AppKey = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.BeginDateTime = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.EndPushTime = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Description = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.tabPage1 = new System.Windows.Forms.TabPage();
            this.dgvRequestInfo = new System.Windows.Forms.DataGridView();
            this.RequestURL = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.LastUpdateTime = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.RequestTime = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.ResponseTime = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.ResponseInfo = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.toolStrip1 = new System.Windows.Forms.ToolStrip();
            this.btStart = new System.Windows.Forms.ToolStripButton();
            this.btnStop = new System.Windows.Forms.ToolStripButton();
            this.bgwConsole = new System.ComponentModel.BackgroundWorker();
            this.nflMenu = new System.Windows.Forms.ContextMenuStrip(this.components);
            this.miShow = new System.Windows.Forms.ToolStripMenuItem();
            this.miExit = new System.Windows.Forms.ToolStripMenuItem();
            this.nfIPrinter = new System.Windows.Forms.NotifyIcon(this.components);
            this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
            this.btnSetAutoRun = new System.Windows.Forms.ToolStripButton();
            this.toolStripContainer1.ContentPanel.SuspendLayout();
            this.toolStripContainer1.TopToolStripPanel.SuspendLayout();
            this.toolStripContainer1.SuspendLayout();
            this.tabControl1.SuspendLayout();
            this.tabPage3.SuspendLayout();
            this.tabPage2.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgvPushMsgInfo)).BeginInit();
            this.tabPage1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgvRequestInfo)).BeginInit();
            this.toolStrip1.SuspendLayout();
            this.nflMenu.SuspendLayout();
            this.SuspendLayout();
            // 
            // toolStripContainer1
            // 
            // 
            // toolStripContainer1.ContentPanel
            // 
            this.toolStripContainer1.ContentPanel.Controls.Add(this.tabControl1);
            this.toolStripContainer1.ContentPanel.Size = new System.Drawing.Size(1441, 769);
            this.toolStripContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.toolStripContainer1.Location = new System.Drawing.Point(0, 0);
            this.toolStripContainer1.Name = "toolStripContainer1";
            this.toolStripContainer1.Size = new System.Drawing.Size(1441, 807);
            this.toolStripContainer1.TabIndex = 0;
            this.toolStripContainer1.Text = "toolStripContainer1";
            // 
            // toolStripContainer1.TopToolStripPanel
            // 
            this.toolStripContainer1.TopToolStripPanel.Controls.Add(this.toolStrip1);
            // 
            // tabControl1
            // 
            this.tabControl1.Alignment = System.Windows.Forms.TabAlignment.Bottom;
            this.tabControl1.Controls.Add(this.tabPage3);
            this.tabControl1.Controls.Add(this.tabPage2);
            this.tabControl1.Controls.Add(this.tabPage1);
            this.tabControl1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tabControl1.Location = new System.Drawing.Point(0, 0);
            this.tabControl1.Name = "tabControl1";
            this.tabControl1.SelectedIndex = 0;
            this.tabControl1.Size = new System.Drawing.Size(1441, 769);
            this.tabControl1.TabIndex = 1;
            // 
            // tabPage3
            // 
            this.tabPage3.Controls.Add(this.tbTraceLog);
            this.tabPage3.Location = new System.Drawing.Point(8, 8);
            this.tabPage3.Name = "tabPage3";
            this.tabPage3.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage3.Size = new System.Drawing.Size(1425, 722);
            this.tabPage3.TabIndex = 2;
            this.tabPage3.Text = "跟踪日志";
            this.tabPage3.UseVisualStyleBackColor = true;
            // 
            // tbTraceLog
            // 
            this.tbTraceLog.BackColor = System.Drawing.SystemColors.WindowText;
            this.tbTraceLog.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tbTraceLog.Font = new System.Drawing.Font("黑体", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.tbTraceLog.ForeColor = System.Drawing.SystemColors.Info;
            this.tbTraceLog.Location = new System.Drawing.Point(3, 3);
            this.tbTraceLog.Multiline = true;
            this.tbTraceLog.Name = "tbTraceLog";
            this.tbTraceLog.ReadOnly = true;
            this.tbTraceLog.ScrollBars = System.Windows.Forms.ScrollBars.Vertical;
            this.tbTraceLog.Size = new System.Drawing.Size(1419, 716);
            this.tbTraceLog.TabIndex = 0;
            // 
            // tabPage2
            // 
            this.tabPage2.Controls.Add(this.dgvPushMsgInfo);
            this.tabPage2.Location = new System.Drawing.Point(8, 8);
            this.tabPage2.Name = "tabPage2";
            this.tabPage2.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage2.Size = new System.Drawing.Size(1425, 722);
            this.tabPage2.TabIndex = 1;
            this.tabPage2.Text = "推送消息";
            this.tabPage2.UseVisualStyleBackColor = true;
            // 
            // dgvPushMsgInfo
            // 
            this.dgvPushMsgInfo.AllowUserToAddRows = false;
            this.dgvPushMsgInfo.AllowUserToDeleteRows = false;
            this.dgvPushMsgInfo.AutoSizeColumnsMode = System.Windows.Forms.DataGridViewAutoSizeColumnsMode.DisplayedCells;
            this.dgvPushMsgInfo.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgvPushMsgInfo.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.AppKey,
            this.BeginDateTime,
            this.EndPushTime,
            this.Description});
            this.dgvPushMsgInfo.Dock = System.Windows.Forms.DockStyle.Fill;
            this.dgvPushMsgInfo.Location = new System.Drawing.Point(3, 3);
            this.dgvPushMsgInfo.Name = "dgvPushMsgInfo";
            this.dgvPushMsgInfo.ReadOnly = true;
            this.dgvPushMsgInfo.RowTemplate.Height = 37;
            this.dgvPushMsgInfo.Size = new System.Drawing.Size(1419, 716);
            this.dgvPushMsgInfo.TabIndex = 0;
            // 
            // AppKey
            // 
            this.AppKey.DataPropertyName = "AppKey";
            this.AppKey.HeaderText = "AppKey";
            this.AppKey.Name = "AppKey";
            this.AppKey.ReadOnly = true;
            this.AppKey.Width = 127;
            // 
            // BeginDateTime
            // 
            this.BeginDateTime.DataPropertyName = "BeginPushTime";
            this.BeginDateTime.HeaderText = "开始时间";
            this.BeginDateTime.Name = "BeginDateTime";
            this.BeginDateTime.ReadOnly = true;
            this.BeginDateTime.Width = 151;
            // 
            // EndPushTime
            // 
            this.EndPushTime.DataPropertyName = "EndPushTime";
            this.EndPushTime.HeaderText = "结束时间";
            this.EndPushTime.Name = "EndPushTime";
            this.EndPushTime.ReadOnly = true;
            this.EndPushTime.Width = 151;
            // 
            // Description
            // 
            this.Description.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.DisplayedCells;
            this.Description.DataPropertyName = "Description";
            this.Description.HeaderText = "说明";
            this.Description.Name = "Description";
            this.Description.ReadOnly = true;
            this.Description.Width = 103;
            // 
            // tabPage1
            // 
            this.tabPage1.Controls.Add(this.dgvRequestInfo);
            this.tabPage1.Location = new System.Drawing.Point(8, 8);
            this.tabPage1.Name = "tabPage1";
            this.tabPage1.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage1.Size = new System.Drawing.Size(1425, 722);
            this.tabPage1.TabIndex = 0;
            this.tabPage1.Text = "获取数据";
            this.tabPage1.UseVisualStyleBackColor = true;
            // 
            // dgvRequestInfo
            // 
            this.dgvRequestInfo.AllowUserToAddRows = false;
            this.dgvRequestInfo.AllowUserToDeleteRows = false;
            this.dgvRequestInfo.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgvRequestInfo.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.RequestURL,
            this.LastUpdateTime,
            this.RequestTime,
            this.ResponseTime,
            this.ResponseInfo});
            this.dgvRequestInfo.Dock = System.Windows.Forms.DockStyle.Fill;
            this.dgvRequestInfo.Location = new System.Drawing.Point(3, 3);
            this.dgvRequestInfo.Name = "dgvRequestInfo";
            this.dgvRequestInfo.ReadOnly = true;
            this.dgvRequestInfo.RowTemplate.Height = 37;
            this.dgvRequestInfo.Size = new System.Drawing.Size(1419, 716);
            this.dgvRequestInfo.TabIndex = 1;
            // 
            // RequestURL
            // 
            this.RequestURL.DataPropertyName = "RequestURL";
            this.RequestURL.FillWeight = 449.2385F;
            this.RequestURL.HeaderText = "请求地址";
            this.RequestURL.Name = "RequestURL";
            this.RequestURL.ReadOnly = true;
            this.RequestURL.Width = 200;
            // 
            // LastUpdateTime
            // 
            this.LastUpdateTime.DataPropertyName = "LastUpdateTime";
            this.LastUpdateTime.FillWeight = 12.69037F;
            this.LastUpdateTime.HeaderText = "时间戳";
            this.LastUpdateTime.Name = "LastUpdateTime";
            this.LastUpdateTime.ReadOnly = true;
            // 
            // RequestTime
            // 
            this.RequestTime.DataPropertyName = "RequestTime";
            this.RequestTime.FillWeight = 12.69037F;
            this.RequestTime.HeaderText = "请求时间";
            this.RequestTime.Name = "RequestTime";
            this.RequestTime.ReadOnly = true;
            // 
            // ResponseTime
            // 
            this.ResponseTime.DataPropertyName = "ResponseTime";
            this.ResponseTime.FillWeight = 12.69037F;
            this.ResponseTime.HeaderText = "响应时间";
            this.ResponseTime.Name = "ResponseTime";
            this.ResponseTime.ReadOnly = true;
            // 
            // ResponseInfo
            // 
            this.ResponseInfo.DataPropertyName = "ResponseInfo";
            this.ResponseInfo.FillWeight = 12.69037F;
            this.ResponseInfo.HeaderText = "结果";
            this.ResponseInfo.Name = "ResponseInfo";
            this.ResponseInfo.ReadOnly = true;
            this.ResponseInfo.Width = 180;
            // 
            // toolStrip1
            // 
            this.toolStrip1.Dock = System.Windows.Forms.DockStyle.None;
            this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
            this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btStart,
            this.btnStop,
            this.toolStripSeparator1,
            this.btnSetAutoRun});
            this.toolStrip1.Location = new System.Drawing.Point(3, 0);
            this.toolStrip1.Name = "toolStrip1";
            this.toolStrip1.Size = new System.Drawing.Size(350, 38);
            this.toolStrip1.TabIndex = 0;
            // 
            // btStart
            // 
            this.btStart.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.btStart.Image = ((System.Drawing.Image)(resources.GetObject("btStart.Image")));
            this.btStart.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.btStart.Name = "btStart";
            this.btStart.Size = new System.Drawing.Size(66, 35);
            this.btStart.Text = "启动";
            this.btStart.Click += new System.EventHandler(this.btStart_Click);
            // 
            // btnStop
            // 
            this.btnStop.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.btnStop.Enabled = false;
            this.btnStop.Image = ((System.Drawing.Image)(resources.GetObject("btnStop.Image")));
            this.btnStop.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.btnStop.Name = "btnStop";
            this.btnStop.Size = new System.Drawing.Size(66, 35);
            this.btnStop.Text = "停止";
            this.btnStop.Click += new System.EventHandler(this.btnStop_Click);
            // 
            // bgwConsole
            // 
            this.bgwConsole.WorkerReportsProgress = true;
            this.bgwConsole.DoWork += new System.ComponentModel.DoWorkEventHandler(this.bgwConsole_DoWork);
            // 
            // nflMenu
            // 
            this.nflMenu.ImageScalingSize = new System.Drawing.Size(32, 32);
            this.nflMenu.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miShow,
            this.miExit});
            this.nflMenu.Name = "nflMenu";
            this.nflMenu.Size = new System.Drawing.Size(137, 76);
            // 
            // miShow
            // 
            this.miShow.Name = "miShow";
            this.miShow.Size = new System.Drawing.Size(136, 36);
            this.miShow.Text = "显示";
            this.miShow.Click += new System.EventHandler(this.miShow_Click);
            // 
            // miExit
            // 
            this.miExit.Name = "miExit";
            this.miExit.Size = new System.Drawing.Size(136, 36);
            this.miExit.Text = "退出";
            this.miExit.Click += new System.EventHandler(this.miExit_Click);
            // 
            // nfIPrinter
            // 
            this.nfIPrinter.ContextMenuStrip = this.nflMenu;
            this.nfIPrinter.Icon = ((System.Drawing.Icon)(resources.GetObject("nfIPrinter.Icon")));
            this.nfIPrinter.Text = "打印服务";
            this.nfIPrinter.MouseDoubleClick += new System.Windows.Forms.MouseEventHandler(this.nfIPrinter_MouseDoubleClick);
            // 
            // toolStripSeparator1
            // 
            this.toolStripSeparator1.Name = "toolStripSeparator1";
            this.toolStripSeparator1.Size = new System.Drawing.Size(6, 38);
            // 
            // btnSetAutoRun
            // 
            this.btnSetAutoRun.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.btnSetAutoRun.Image = ((System.Drawing.Image)(resources.GetObject("btnSetAutoRun.Image")));
            this.btnSetAutoRun.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.btnSetAutoRun.Name = "btnSetAutoRun";
            this.btnSetAutoRun.Size = new System.Drawing.Size(138, 35);
            this.btnSetAutoRun.Text = "开机自启动";
            this.btnSetAutoRun.Click += new System.EventHandler(this.btnSetAutoRun_Click);
            // 
            // FmConsole
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(12F, 24F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1441, 807);
            this.Controls.Add(this.toolStripContainer1);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "FmConsole";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "EMMS消息推送";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmConsole_FormClosing);
            this.SizeChanged += new System.EventHandler(this.FmConsole_SizeChanged);
            this.toolStripContainer1.ContentPanel.ResumeLayout(false);
            this.toolStripContainer1.TopToolStripPanel.ResumeLayout(false);
            this.toolStripContainer1.TopToolStripPanel.PerformLayout();
            this.toolStripContainer1.ResumeLayout(false);
            this.toolStripContainer1.PerformLayout();
            this.tabControl1.ResumeLayout(false);
            this.tabPage3.ResumeLayout(false);
            this.tabPage3.PerformLayout();
            this.tabPage2.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.dgvPushMsgInfo)).EndInit();
            this.tabPage1.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.dgvRequestInfo)).EndInit();
            this.toolStrip1.ResumeLayout(false);
            this.toolStrip1.PerformLayout();
            this.nflMenu.ResumeLayout(false);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.ToolStripContainer toolStripContainer1;
        private System.Windows.Forms.ToolStrip toolStrip1;
        private System.Windows.Forms.ToolStripButton btStart;
        private System.ComponentModel.BackgroundWorker bgwConsole;
        private System.Windows.Forms.TabControl tabControl1;
        private System.Windows.Forms.TabPage tabPage1;
        private System.Windows.Forms.DataGridView dgvRequestInfo;
        private System.Windows.Forms.DataGridViewTextBoxColumn RequestURL;
        private System.Windows.Forms.DataGridViewTextBoxColumn LastUpdateTime;
        private System.Windows.Forms.DataGridViewTextBoxColumn RequestTime;
        private System.Windows.Forms.DataGridViewTextBoxColumn ResponseTime;
        private System.Windows.Forms.DataGridViewTextBoxColumn ResponseInfo;
        private System.Windows.Forms.TabPage tabPage2;
        private System.Windows.Forms.DataGridView dgvPushMsgInfo;
        private System.Windows.Forms.TabPage tabPage3;
        private System.Windows.Forms.TextBox tbTraceLog;
        private System.Windows.Forms.ToolStripButton btnStop;
        private System.Windows.Forms.DataGridViewTextBoxColumn AppKey;
        private System.Windows.Forms.DataGridViewTextBoxColumn BeginDateTime;
        private System.Windows.Forms.DataGridViewTextBoxColumn EndPushTime;
        private System.Windows.Forms.DataGridViewTextBoxColumn Description;
        private System.Windows.Forms.ContextMenuStrip nflMenu;
        private System.Windows.Forms.ToolStripMenuItem miShow;
        private System.Windows.Forms.ToolStripMenuItem miExit;
        private System.Windows.Forms.NotifyIcon nfIPrinter;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
        private System.Windows.Forms.ToolStripButton btnSetAutoRun;
    }
}