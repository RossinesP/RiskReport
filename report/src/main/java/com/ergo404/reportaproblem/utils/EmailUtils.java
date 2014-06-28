package com.ergo404.reportaproblem.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.ergo404.reportaproblem.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pierrerossines on 28/06/2014.
 */
public class EmailUtils {
    public static void email(Context context, String emailTo, String emailCC,
                             String subject, String emailText, String filePath) {
        ArrayList<String> files = new ArrayList<String>();
        files.add(filePath);
        EmailUtils.email(context, emailTo, emailCC, subject, emailText, files);
    }

    public static void email(Context context, String emailTo, String emailCC,
                             String subject, String emailText, List<String> filePaths) {
        //need to "send multiple" to get more than one attachment
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[]{emailTo});
        emailIntent.putExtra(android.content.Intent.EXTRA_CC,
                new String[]{emailCC});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailText);
        //has to be an ArrayList
        ArrayList<Uri> uris = new ArrayList<Uri>();
        //convert from paths to Android friendly Parcelable Uri's
        for (String file : filePaths) {
            File fileIn = new File(file);
            Uri u = Uri.fromFile(fileIn);
            uris.add(u);
        }
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.report_sending_chooser)));
    }
}
