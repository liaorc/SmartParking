package cn.edu.sjtu.icat.smartparking;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by Ruochen on 2015/10/12.
 */
public class CurrentUserInfoSaver {
    private Context mContext;
    private String mFilename;

    public CurrentUserInfoSaver(Context c, String filename) {
        mContext = c;
        mFilename = filename;
    }

    public void saveUserInfo(CurrentUser user) throws IOException, JSONException {

        JSONObject obj = user.toJSON();
        Writer writer = null;
        try {
            OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(obj.toString());
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    public JSONObject loadUserInfo() throws JSONException, IOException {
        BufferedReader reader = null;
        try {
            InputStream in = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ( (line = reader.readLine()) != null ) {
                jsonString.append(line);
            }
            JSONObject json = new JSONObject(jsonString.toString());
            Log.d("info_saver", jsonString.toString());
            return json;
            // CurrentUser.get(mContext).restoreUser(json);
        } catch (FileNotFoundException e) {
            //
        } finally {
            if (reader != null)
                reader.close();
        }
        return null;
    }
}
