package com.del.app.manager.task;

import android.os.AsyncTask;
import android.content.Context;
import java.util.List;
import com.del.app.manager.model.ModelApk;
import android.app.ProgressDialog;
import java.util.ArrayList;
import com.del.app.manager.util.ApkUtils;
import com.del.app.manager.Interface.Resul2Task;
import com.kaopiz.kprogresshud.KProgressHUD;

public class UserAppTask extends AsyncTask<String, Integer, List<ModelApk>> {
    
    private Context mContext;
    private List<ModelApk> app;
    private Resul2Task resultTask = null;
	private KProgressHUD hud;
	
    public UserAppTask(Context mContext, Resul2Task resultTask){
        this.mContext = mContext;
        this.resultTask = resultTask;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
		hud = KProgressHUD.create(mContext);
		hud.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
		hud.show();
    }
    
    @Override
    protected List<ModelApk> doInBackground(String... con) {
        app = new ArrayList<ModelApk>();
        app = ApkUtils.getAllApk(mContext, true);
        return app;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List<ModelApk> result) {
        super.onPostExecute(result);
        
        if(hud != null && hud.isShowing()){
           hud.dismiss();
            resultTask.userFinised(result);
        }
    }
}
