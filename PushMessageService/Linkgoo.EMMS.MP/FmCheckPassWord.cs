using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Linkgoo.EMMS.MP
{
    public partial class FmCheckPassWord : Form
    {
        public FmCheckPassWord()
        {
            InitializeComponent();
        }

        private void btnOK_Click(object sender, EventArgs e)
        {
            if (tbPassword.Text == "linkgoo")
            {
                System.Environment.Exit(0);
            }
            else
            {
                MessageBox.Show("密码错误！");
            }
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void tbPassword_KeyUp(object sender, KeyEventArgs e)
        {
            if(e.KeyCode==Keys.Enter)
            {
                btnOK_Click(btnOK, null);
            }
        }
    }
}
