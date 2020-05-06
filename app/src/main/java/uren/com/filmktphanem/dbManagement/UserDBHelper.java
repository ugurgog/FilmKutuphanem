package uren.com.filmktphanem.dbManagement;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import uren.com.filmktphanem.Interfaces.CompleteCallback;
import uren.com.filmktphanem.Interfaces.OnCompleteCallback;
import uren.com.filmktphanem.models.User;

import static uren.com.filmktphanem.Constants.StringConstants.fb_child_admin;
import static uren.com.filmktphanem.Constants.StringConstants.fb_child_email;
import static uren.com.filmktphanem.Constants.StringConstants.fb_child_login_method;
import static uren.com.filmktphanem.Constants.StringConstants.fb_child_name;
import static uren.com.filmktphanem.Constants.StringConstants.fb_child_profilePhotoUrl;
import static uren.com.filmktphanem.Constants.StringConstants.fb_child_users;


public class UserDBHelper {

    public static void addUser(final User user, final OnCompleteCallback onCompleteCallback) {

        if (user == null)
            return;
        if (user.getUserid() == null || user.getUserid().isEmpty())
            return;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(fb_child_users).child(user.getUserid());

        final Map<String, Object> values = new HashMap<>();

        if (user.getEmail() != null)
            values.put(fb_child_email, user.getEmail());
        if (user.getLoginMethod() != null)
            values.put(fb_child_login_method, user.getLoginMethod());
        if (user.getName() != null)
            values.put(fb_child_name, user.getName());
        if (user.getProfilePhotoUrl() != null)
            values.put(fb_child_profilePhotoUrl, user.getProfilePhotoUrl());

        values.put(fb_child_admin, user.isAdmin());

        databaseReference.updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public static void updateUser(User user, boolean updateAlgolia, final OnCompleteCallback onCompleteCallback) {

        if (user == null)
            return;
        if (user.getUserid() == null || user.getUserid().isEmpty())
            return;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(fb_child_users).child(user.getUserid());

        final Map<String, Object> values = new HashMap<>();

        if (user.getEmail() != null)
            values.put(fb_child_email, user.getEmail());
        if (user.getName() != null)
            values.put(fb_child_name, user.getName());
        if (user.getProfilePhotoUrl() != null)
            values.put(fb_child_profilePhotoUrl, user.getProfilePhotoUrl());
        if (user.getLoginMethod() != null)
            values.put(fb_child_login_method, user.getLoginMethod());


        databaseReference.updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public static void getUser(final String userid, final CompleteCallback completeCallback) {

        if (userid == null) return;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(fb_child_users).child(userid);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Map<String, Object> userMap = (Map) dataSnapshot.getValue();

                if (userMap != null) {
                    String email = (String) userMap.get(fb_child_email);
                    String name = (String) userMap.get(fb_child_name);
                    String photoUrl = (String) userMap.get(fb_child_profilePhotoUrl);

                    boolean admin = false;
                    try {
                        admin = (boolean) userMap.get(fb_child_admin);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String loginMethod = null;
                    try {
                        loginMethod = (String) userMap.get(fb_child_login_method);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    User user = new User(userid, name, email, photoUrl, admin, loginMethod);
                    user.setAdmin(admin);
                    completeCallback.onComplete(user);
                } else
                    completeCallback.onComplete(new User());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                completeCallback.onFailed(databaseError.toString());
            }
        });
    }

    public static void deleteLoginMethod(final String userid, final OnCompleteCallback onCompleteCallback){
        FirebaseDatabase.getInstance().getReference(fb_child_users).child(userid).child(fb_child_login_method)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onCompleteCallback.OnCompleted();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onCompleteCallback.OnFailed(e.getMessage());
            }
        });
    }
}
