package com.runningracehisotry.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.facebook.widget.FacebookDialog;
import com.google.gson.Gson;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.runningracehisotry.R;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.History;
import com.runningracehisotry.models.Shoe;
import com.runningracehisotry.models.ShoeObject;
import com.runningracehisotry.views.CustomAlertDialog;
import com.runningracehisotry.views.CustomAlertDialog.OnNegativeButtonClick;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class Utilities {

    /**
     * show alert message
     *
     * @param message Alert message
     * @param title   alert title
     */
    public static void showAlertMessage(Context context, String message,
                                        String title) {

        final CustomAlertDialog dialog = new CustomAlertDialog(context);
        dialog.setCancelableFlag(false);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setNegativeButton(context.getString(R.string.ok),
                new OnNegativeButtonClick() {

                    @Override
                    public void onButtonClick(final View view) {
                        // TODO Auto-generated method stub

                        dialog.dismiss();
                    }
                });

        dialog.show();
    }

    /**
     * Display shoe image
     *
     * @param imageView  Image view to display
     * @param roundImage
     */
    public static void displayParseImage(ParseObject parseObject,
                                         final ImageView imageView, final int roundImage) {

        parseObject.fetchIfNeededInBackground(new GetCallback<ParseObject>() {

            @Override
            public void done(ParseObject arg0, ParseException arg1) {
                // TODO Auto-generated method stub
                if (arg1 == null) {

                    ParseFile image = arg0.getParseFile(Constants.DATA
                            .toUpperCase());
                    image.getDataInBackground(new GetDataCallback() {

                        @Override
                        public void done(byte[] data, ParseException arg1) {
                            // TODO Auto-generated method stub
                            if (arg1 == null) {

                                Bitmap bmp = BitmapFactory.decodeByteArray(
                                        data, 0, data.length);
                                bmp = getRoundedCornerBitmap(bmp, roundImage);
                                imageView.setImageBitmap(bmp);
                                imageView.setTag(bmp);
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Round bitmap
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static void showPickerImageDialog(final Activity act,
                                             final int requestCodeCamera, final int requestCodeGallery) {

        final Dialog dialog = new Dialog(act);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choose_images);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        TextView takePhotoBtn = (TextView) dialog
                .findViewById(R.id.dialog_take_picture);
        TextView selectGalleryBtn = (TextView) dialog
                .findViewById(R.id.dialog_choose_from_gallery);

        takePhotoBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                Intent captureIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, createImage());
                act.startActivityForResult(captureIntent, requestCodeCamera);
                dialog.dismiss();
            }
        });

        selectGalleryBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                act.startActivityForResult(photoPickerIntent,
                        requestCodeGallery);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static Uri createImage() {

        File appDir = new File(Environment.getExternalStorageDirectory()
                + Constants.APP_FOLDER);
        if (!appDir.exists()) {

            appDir.mkdir();
        }

        File file = new File(Environment.getExternalStorageDirectory()
                + Constants.APP_FOLDER, "image.jpg");
        return Uri.fromFile(file);
    }

    /**
     * Start crop image activity
     */
    public static void startCropImage(Activity acti, int requestCode,
                                      Uri imageUri) {

        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(imageUri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 1000);
        cropIntent.putExtra("outputY", 1000);
        cropIntent.putExtra("return-data", true);
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, createImage());
        acti.startActivityForResult(cropIntent, requestCode);
    }

    /**
     * Open browser with link
     *
     * @param url Link
     */
    public static void openBrower(Context context, String url) {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }

    /**
     * Contact us function
     */
    public static void contactUs(Context context) {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL,
                new String[]{context.getString(R.string.contact_email)});
        i.putExtra(Intent.EXTRA_SUBJECT,
                context.getString(R.string.contact_subject));
        i.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.contact_body));
        try {

            context.startActivity(Intent.createChooser(i, "Contact us..."));
        } catch (ActivityNotFoundException ex) {

            Utilities
                    .showAlertMessage(
                            context,
                            context.getString(R.string.dialog_contact_us_fails_message),
                            context.getString(R.string.dialog_contact_us_fails_title));
        }
    }

    /**
     * Get user image
     */
    public static String getUserProfileImage(ParseUser user) {

        String imgUrl = "";
        if (user != null) {

            if (user.containsKey(Constants.KIND)) {

                String kindStr = user.getString(Constants.KIND);
                if (kindStr.startsWith("fb:")) {

                    String fbId = kindStr.replace("fb:", "");
                    imgUrl = String.format(
                            "http://graph.facebook.com/%s/picture?width=640",
                            fbId);
                }
            }
        }

        return imgUrl;
    }

    public static Uri saveBitmap(Bitmap bitmap, String fileName) {

        OutputStream fOut = null;

        File appDir = new File(Environment.getExternalStorageDirectory()
                + Constants.APP_FOLDER);
        if (!appDir.exists()) {

            appDir.mkdir();
        }

        File file = new File(Environment.getExternalStorageDirectory()
                + Constants.APP_FOLDER, fileName);
        try {

            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            return Uri.fromFile(file);
        } catch (Exception e) {

            return null;
        }
    }

    /**
     * Share information to social network
     */
    public static void doShare(Context context, String nameApp,
                               String shareText, ArrayList<Uri> uris) {

        try {

            List<Intent> targetedShareIntents = new ArrayList<Intent>();
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("image/*");
            List<ResolveInfo> resInfo = context.getPackageManager()
                    .queryIntentActivities(share, 0);
            if (!resInfo.isEmpty()) {
                boolean hasResolveHandler = false;

                for (ResolveInfo info : resInfo) {

                    Intent targetedShare = new Intent();
                    targetedShare.setType("image/*");
                    if (info.activityInfo.packageName.toLowerCase().contains(
                            nameApp)
                            || info.activityInfo.name.toLowerCase().contains(
                            nameApp)) {

                        hasResolveHandler = true;

                        targetedShare.putExtra(Intent.EXTRA_TEXT, shareText);
                        if (uris != null && uris.size() > 0) {

                            if (nameApp.equals("com.facebook.katana")) {

                                targetedShare
                                        .setAction(android.content.Intent.ACTION_SEND_MULTIPLE);
                                targetedShare.putParcelableArrayListExtra(
                                        Intent.EXTRA_STREAM, uris);
                            } else {

                                targetedShare
                                        .setAction(android.content.Intent.ACTION_SEND);
                                targetedShare.putExtra(Intent.EXTRA_STREAM,
                                        uris.get(uris.size() - 1));
                            }
                        } else {

                            targetedShare
                                    .setAction(android.content.Intent.ACTION_SEND);
                        }

                        targetedShare.setPackage(info.activityInfo.packageName);
                        targetedShareIntents.add(targetedShare);
                    }
                }

                if (hasResolveHandler) {

                    Intent chooserIntent = Intent.createChooser(
                            targetedShareIntents.remove(0), "Select app to share");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                            targetedShareIntents.toArray(new Parcelable[]{}));
                    context.startActivity(chooserIntent);
                } else {
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + nameApp)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + nameApp)));
                    }
                }
            } else {
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + nameApp)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + nameApp)));
                }
            }
        } catch (Exception e) {
        }
    }

    public static void shareTwitter(Activity context, String shareText) {

    }

    // Gson util
    public static String toJson(Object obj) {
        Gson gson = new Gson();
        // convert java object to JSON format,
        // and returned as JSON formatted string
        String json = null;

        try {
            json = gson.toJson(obj);

        } catch (Exception e) {
            LogUtil.d("UTILITY", "error toJSon");
            e.printStackTrace();
        }
        return json;
    }

    public static Shoe fromJson(String json) {
        Gson gson = new Gson();
        // convert java object to JSON format,
        // and returned as JSON formatted string
        Shoe shoe = null;

        try {
            shoe = gson.fromJson(json, Shoe.class);

        } catch (Exception e) {
            LogUtil.d("UTILITY", "error fromJSon");
            e.printStackTrace();
        }
        return shoe;
    }

    public static String getDisplayedHistoryOfShoe(History history) {
        String temp = history.getCreatedAt().substring(0, 10);
        LogUtil.d("UTILITY", "temp date: " + temp);
        String[] split = temp.split("-");
        StringBuilder str = new StringBuilder(split[2]);
        str.append("/");
        str.append(split[1]);
        str.append("/");
        str.append(split[0]);
        str.append(": you added ");
        str.append(history.getMiles());
        str.append(" miles");
        return str.toString();
    }

    public static boolean isValidEmail(String email) {
        //boolean result = false;
        if (email == null || email.isEmpty()) {
            return false;
        } else {
            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            CharSequence inputStr = email;

            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputStr);
            if (matcher.matches()) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Share information to social network custom
     */
    public static void doShareSocial(Context context, String nameApp,
                                     String shareText, ArrayList<Uri> uris) {

        try {

            List<Intent> targetedShareIntents = new ArrayList<Intent>();
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("image/*");
            List<ResolveInfo> resInfo = context.getPackageManager()
                    .queryIntentActivities(share, 0);
            if (!resInfo.isEmpty()) {
                boolean hasResolveHandler = false;

                for (ResolveInfo info : resInfo) {

                    Intent targetedShare = new Intent();
                    targetedShare.setType("image/*");
                    if (info.activityInfo.packageName.toLowerCase().contains(
                            nameApp)
                            || info.activityInfo.name.toLowerCase().contains(
                            nameApp)) {

                        hasResolveHandler = true;

                        targetedShare.putExtra(Intent.EXTRA_TEXT, shareText);
                        if (uris != null && uris.size() > 0) {
                            LogUtil.e(Constants.LOG_TAG, "URI not null");
                            if (nameApp.equals("com.facebook.katana")) {
                                LogUtil.e(Constants.LOG_TAG, "URI not null share fb");
                                targetedShare
                                        .setAction(android.content.Intent.ACTION_SEND_MULTIPLE);
                                targetedShare.putParcelableArrayListExtra(
                                        Intent.EXTRA_STREAM, uris);
                            } else {
                                LogUtil.e(Constants.LOG_TAG, "URI not null share no fb");
                                targetedShare
                                        .setAction(android.content.Intent.ACTION_SEND);
                                targetedShare.putExtra(Intent.EXTRA_STREAM,
                                        uris.get(uris.size() - 1));
                            }
                        } else {
                            LogUtil.e(Constants.LOG_TAG, "URI null");
                            targetedShare
                                    .setAction(android.content.Intent.ACTION_SEND);
                        }

                        targetedShare.setPackage(info.activityInfo.packageName);
                        targetedShareIntents.add(targetedShare);
                    }
                }

                if(hasResolveHandler) {

                    Intent chooserIntent = Intent.createChooser(
                            targetedShareIntents.remove(0), "Select app to share");
                    try {
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                                targetedShareIntents.toArray(new Parcelable[]{}));
                    } catch (Exception ex) {
                        LogUtil.e(Constants.LOG_TAG, "error when share race image");
                    }

                    context.startActivity(chooserIntent);
                } else {
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + nameApp)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + nameApp)));
                    }
                }
            } else {
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + nameApp)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + nameApp)));
                }

            }
        } catch (Exception e) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + nameApp)));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + nameApp)));
            }
        }
    }

    public List<Shoe> convertListShoe(List<ShoeObject> list){
        return null;
    }
}
