package com.del.app.manager.task;

import android.os.AsyncTask;
import java.util.List;
import com.del.app.manager.model.ModelApk;
import android.app.ProgressDialog;
import android.content.Context;
import java.util.ArrayList;
import com.del.app.manager.util.ApkUtils;
import com.del.app.manager.Interface.ResultTask;
import com.kaopiz.kprogresshud.KProgressHUD;

public class SystemAppTask extends AsyncTask <Void, Integer, List<ModelApk>> {
	
	
    private Context ctx;
    private List<ModelApk> app;
	private KProgressHUD hud;
	private ResultTask resultTask = null;
	
	public SystemAppTask(Context ctx, ResultTask resultTask ){
		this.ctx = ctx;
		this.resultTask =resultTask;
	}
	
	
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
		hud = KProgressHUD.create(ctx);
		hud.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
		hud.show();
    }
    
    @Override
    protected List<ModelApk> doInBackground(Void... params) {
        app = new ArrayList<>();
        
        app = ApkUtils.getSystemApk(ctx, true);
        
        return app;
    }
    
    @Override
    protected void onPostExecute(List<ModelApk> result) {
        super.onPostExecute(result);
		if(hud != null && hud.isShowing()){
			hud.dismiss();
            resultTask.sysAppFinised(result);
        }
    }
    
}
