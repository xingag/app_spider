package com.xingag.crack_wx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.xingag.bean.Contact;
import com.xingag.util.FileUtils;
import com.xingag.util.MD5Utils;
import com.xingag.util.NormUtils;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/****
 * 破解微信通信录
 *
 *
 * 数据库：sqlcipher
 */

public class MainActivity extends AppCompatActivity
{
    public static final String WX_ROOT_PATH = "/data/data/com.tencent.mm/";
    private static final String WX_DB_DIR_PATH = WX_ROOT_PATH + "MicroMsg";
    //    private List<File> mWxDbPathList = new ArrayList<>();
    private static final String WX_DB_FILE_NAME = "EnMicroMsg.db";

    //目标路径
    private String mCurrApkPath = "/data/data/com.xingag.crack_wx/";
    private static final String COPY_WX_DATA_DB = "wx_data.db";

    private static final String DATA_CSV = "wx_data.csv";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String imei = NormUtils.getPhoneIMEI();
        String uid = NormUtils.getUid();

        Log.d("xag", "imei:" + imei);
        Log.d("xag", "uid:" + uid);


        //1、数据库密码
        String db_pwd = NormUtils.getDbPassword(imei, uid);

        Log.d("xag", "数据库密码是:" + db_pwd);

        String dbParentPath = MD5Utils.md5("mm" + uid);
        String db_path = WX_DB_DIR_PATH + "/" + dbParentPath + "/" + WX_DB_FILE_NAME;

        Log.d("xag", "dbPath:" + db_path);


        //3、拷贝数据库
        String copyFilePath = mCurrApkPath + COPY_WX_DATA_DB;
        //将微信数据库拷贝出来，因为直接连接微信的db，会导致微信崩溃
        FileUtils.copyFile(db_path, copyFilePath);


        //4.打开数据库
        //过SQLCipher这个库来连接加密的数据库
        File copyWxDataDb = new File(copyFilePath);
        openWxDb(copyWxDataDb, db_pwd);
    }

    /**
     * 连接数据库
     * <p>
     * 常用库介绍：【rcontact】联系人表，【message】聊天消息表
     *
     * @param dbFile
     */
    private void openWxDb(File dbFile, String db_pwd)
    {
        //所有联系人
        List<Contact> contacts = new ArrayList<>();
        SQLiteDatabase.loadLibs(this);
        SQLiteDatabaseHook hook = new SQLiteDatabaseHook()
        {
            public void preKey(SQLiteDatabase database)
            {
            }

            public void postKey(SQLiteDatabase database)
            {
                database.rawExecSQL("PRAGMA cipher_migrate;"); //兼容2.0的数据库
            }
        };

        try
        {
            //打开数据库连接
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, db_pwd, null, hook);
            //查询所有联系人
            //过滤掉本人、群聊、公众号、服务号等一些联系人
            //verifyFlag != 0:公众号、服务号
            //注意黑名单用户，我-设置-隐私-通讯录黑名单

            Cursor c1 = db.rawQuery(
                    "select * from rcontact where verifyFlag =0 and type not in (2,4,8,9,33,35,256,258,512,2051,32768,32770,32776,33024,65536,65792,98304) and username not like \"%@app\" and username not like \"%@qqim\" and username not like \"%@chatroom\" and encryptUsername!=\"\"",
                    null);

            while (c1.moveToNext())
            {
                String userName = c1.getString(c1.getColumnIndex("username"));
                String alias = c1.getString(c1.getColumnIndex("alias"));
                String nickName = c1.getString(c1.getColumnIndex("nickname"));
                int type = c1.getInt(c1.getColumnIndex("type"));

                //过滤群聊
                if (userName.contains("chatroom"))
                {
                    continue;
                }

                //过滤特殊的人群
//                if (types_beside.contains(type))
//                {
//                    Log.d("xxx", "特殊，昵称:" + nickName + ",type:" + type);
//                    continue;
//                }

                Log.d("xag", "****************************");
                Log.d("xag", "userName:" + userName);
                Log.d("xag", "alias:" + alias);
                Log.d("xag", "nickName:" + nickName);
                Log.d("xag", "****************************");
                contacts.add(new Contact(userName, alias, nickName));
            }
            Log.d("xag", "微信通讯录中，联系人数目:" + contacts.size() + "个");
            for (int i = 0; i < contacts.size(); i++)
            {
                Log.d("xag", contacts.get(i).getNickName());
            }
            c1.close();
            db.close();
        } catch (Exception e)
        {
            Log.e("xag", "读取数据库信息失败" + e.toString());
            Toast.makeText(this, "读取微信通信录失败！", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
        }
        Log.d("xag", "查询结束");
        FileUtils.writeCsvFile(mCurrApkPath + DATA_CSV, contacts);

        Log.d("xag", "写入到csv文件成功");

        Toast.makeText(this, "读取微信通信录成功！", Toast.LENGTH_SHORT).show();
    }
}
