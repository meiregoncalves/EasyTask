package com.example.meire.agendatarefas.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.content.ContentValues.TAG;

public class MeuFirebaseInstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh(){
        String refreshdToken = FirebaseInstanceId.getInstance().getToken();

        sendRegistrationToServer(refreshdToken);
    }

    private void sendRegistrationToServer(String refreshdToken) {
        Log.d(TAG, "Refreshed token: " + refreshdToken);
    }
}
