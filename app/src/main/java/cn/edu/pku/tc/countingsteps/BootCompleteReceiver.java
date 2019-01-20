package cn.edu.pku.tc.countingsteps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by 18012 on 2019/1/13.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        Intent i=new Intent(context,StepService.class);
        context.startService(i);
    }
}
