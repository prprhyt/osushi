package jagsc.dlfa.osushi;

import android.os.AsyncTask;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpResponsAsync extends AsyncTask<Void, Void, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // doInBackground前処理
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = null;
        HttpURLConnection con = null;
        DataOutputStream os = null;
        InputStream in = null;

        try {
            URL url = new URL("https://script.google.com/macros/s/AKfycbz4QMY5OiOvtoQl-LPmoqFlGRUOEEAfN-UfrF6RSWAS9B2CkIY/exec");
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");

            // リダイレクトオン
            con.setInstanceFollowRedirects(true);

            con.setDoInput(true);
            con.setDoOutput(true);

            //con.connect(); // ①

            os = new DataOutputStream(con.getOutputStream());
            os.writeBytes("data=" + "Android Test Data");

            int code = con.getResponseCode();

            // 接続が確立したとき
            if (code == HttpURLConnection.HTTP_OK) {
                in = con.getInputStream();
                byte bodyByte[] = new byte[1024];
                int read = in.read(bodyByte);
                result = new String(bodyByte, 0, read);
            }
            // 接続が確立できなかったとき
            else {
                result = String.valueOf(code);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 開いたら閉じる
            try {
                if (in != null) {
                    in.close();
                }
                if (os != null) {
                    os.flush();
                    os.close();
                }
                if (con != null) {con.disconnect();}
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // doInBackground後処理
    }

}
