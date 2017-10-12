using log4net;
using log4net.Config;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Linkgoo.EMMS.MP
{
    static class Program
    {
        private static System.Threading.Mutex mutex;
        /// <summary>
        /// 应用程序的主入口点。
        /// </summary>
        [STAThread]
        static void Main()
        {
            log4net.Config.XmlConfigurator.Configure();
            GlobalContext.Properties["sVersion"] = ConfigurationManager.AppSettings["sVersion"];

            mutex = new System.Threading.Mutex(true, "OnlyRun");

            if (mutex.WaitOne(0, false))

            {
                Application.EnableVisualStyles();
                Application.SetCompatibleTextRenderingDefault(false);
                Application.Run(new FmConsole());
                Application.EnableVisualStyles();

                Application.SetCompatibleTextRenderingDefault(false);
            }
            else
            {

                MessageBox.Show("程序已有一个实例在运行，不允许允许多个实例。", "提示", MessageBoxButtons.OK, MessageBoxIcon.Information);

                Application.Exit();

            }
        }
    }
}
