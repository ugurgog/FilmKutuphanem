package uren.com.filmktphanem.dbManagement;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import uren.com.filmktphanem.Interfaces.CompleteCallback;
import uren.com.filmktphanem.Interfaces.OnCompleteCallback;

import static uren.com.filmktphanem.Constants.StringConstants.CHAR_E;
import static uren.com.filmktphanem.Constants.StringConstants.FB_CHILD_DEVICE_TOKEN;
import static uren.com.filmktphanem.Constants.StringConstants.FB_CHILD_SIGNIN;
import static uren.com.filmktphanem.Constants.StringConstants.FB_CHILD_TOKEN;


public class TokenDBHelper {

    public static void getUserTokenByuserId(String userid, final CompleteCallback completeCallback){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference(FB_CHILD_DEVICE_TOKEN).child(userid);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Map<String, Object> map = (Map) dataSnapshot.getValue();

                String signIn = null;
                try {
                    signIn = (String) map.get(FB_CHILD_SIGNIN);

                    if(signIn.equals(CHAR_E)){
                        String token = (String) map.get(FB_CHILD_TOKEN);
                        completeCallback.onComplete(token);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    completeCallback.onFailed(e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                completeCallback.onFailed(databaseError.toString());
            }
        });
    }

    public static void updateTokenSigninValue(String userid, String value, final OnCompleteCallback onCompleteCallback) {
        FirebaseDatabase.getInstance().getReference(FB_CHILD_DEVICE_TOKEN)
                .child(userid).child(FB_CHILD_SIGNIN).setValue(value)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        onCompleteCallback.OnCompleted();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onCompleteCallback.OnFailed(e.getMessage());
            }
        });
    }

    public static void sendTokenToServer(String token, String userid) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(FB_CHILD_DEVICE_TOKEN)
                .child(userid);

        final Map<String, Object> values = new HashMap<>();
        values.put(FB_CHILD_TOKEN, token);

        database.updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}