package uren.com.filmktphanem.Utils.dataModelUtil;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import uren.com.filmktphanem.R;
import uren.com.filmktphanem.Utils.CommonUtils;
import uren.com.filmktphanem.Utils.ShapeUtil;
import uren.com.filmktphanem.models.User;

import static uren.com.filmktphanem.Constants.StringConstants.CHAR_AMPERSAND;

public class UserDataUtil {

    public static String getName(String name) {
        int nameMaxLen = 25;

        if (name != null && !name.isEmpty()) {
            if (name.length() > nameMaxLen)
                return name.trim().substring(0, nameMaxLen) + "...";
            else
                return name;
        } else
            return "unknown";
    }

    public static String getNameFromUser(User user) {
        int nameMaxLen = 25;

        if (user == null) return "unknown";

        if (user.getName() != null && !user.getName().isEmpty()) {
            if (user.getName().length() > nameMaxLen)
                return user.getName().trim().substring(0, nameMaxLen) + "...";
            else
                return user.getName();
        } else
            return "unknown";
    }

    public static void setName(String name, TextView nameTextView) {
        int nameMaxLen = 25;
        if (name != null && nameTextView != null && !name.isEmpty()) {
            nameTextView.setVisibility(View.VISIBLE);
            if (name.length() > nameMaxLen)
                nameTextView.setText(name.trim().substring(0, nameMaxLen) + "...");
            else
                nameTextView.setText(name);
        } else if (nameTextView != null)
            nameTextView.setVisibility(View.GONE);
    }

    public static String getShortenName(String name) {
        StringBuilder returnValue = new StringBuilder();
        if (name != null && !name.trim().isEmpty()) {
            String[] seperatedName = name.trim().split(" ");
            for (String word : seperatedName) {
                if (returnValue.length() < 3)
                    returnValue.append(word.substring(0, 1).toUpperCase());
            }
        }

        return returnValue.toString();
    }

    public static String getNameFromNameWhenLoginWithGoogle(String name) {
        StringBuilder returnValue = new StringBuilder();
        if (name != null && !name.trim().isEmpty()) {
            String[] seperatedName = name.trim().split(" ");
            for (String word : seperatedName) {
                returnValue.append(word);
            }
        }

        return returnValue.toString();
    }

    public static void setProfilePicture(Context context, String url, String name, TextView shortNameTv,
                                        ImageView profilePicImgView) {
        if (context == null) return;

        boolean picExist = false;

        if (url != null && !url.trim().isEmpty()) {
            shortNameTv.setVisibility(View.GONE);
            Glide.with(context)
                    .load(url)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePicImgView);
            picExist = true;
            //profilePicImgView.setPadding(1, 1, 1, 1); // degerler asagidaki imageShape strokeWidth ile aynı tutulmalı
        } else {
            if (name != null && !name.trim().isEmpty()) {
                shortNameTv.setVisibility(View.VISIBLE);
                shortNameTv.setText(UserDataUtil.getShortenName(name));
                profilePicImgView.setImageDrawable(null);
            } else {
                shortNameTv.setVisibility(View.GONE);
                Glide.with(context)
                        .load(R.drawable.ic_person_white_24dp)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profilePicImgView);
            }
        }

        GradientDrawable imageShape;

            if (picExist) {
                imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.White),
                        context.getResources().getColor(R.color.DodgerBlue),
                        GradientDrawable.OVAL, 50, 3);
            } else {
                imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue),
                        context.getResources().getColor(R.color.DodgerBlue),
                        GradientDrawable.OVAL, 50, 3);
            }


        profilePicImgView.setBackground(imageShape);
    }

}
