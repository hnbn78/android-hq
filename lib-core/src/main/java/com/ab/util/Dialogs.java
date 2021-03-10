package com.ab.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ab.core.R;
import com.ab.dialog.AbDialogOnClickListener;
import com.ab.dialog.AbDialogOnItemClickListener;
import com.ab.dialog.AbDialogTextOnClickListener;
import com.ab.dialog.AbSampleDialogFragment;
import com.ab.thread.ThreadCollector;
import com.ab.view.MaterialDialog;

/**
 * Created by lee on 2017/9/27.
 */

public class Dialogs {
    private static boolean isFullScreen;

    public static String OPTION_LIST_DIALOG = "OPTION_LIST_DIALOG";
    public static String EDIT_DIALOG = "EDIT_DIALOG";
    public static String TEXT_DIALOG = "TEXT_DIALOG";
    public static String CUSTOM_DIALOG = "CUSTOM_DIALOG";
    private static String FULLSCREEN_DIALOG = "FULLSCREEN_DIALOG";

    public static AlertDialog alertDialog = null;
    public static Dialog progressFragment = null;
    public static AbSampleDialogFragment editFragment = null;
    public static AbSampleDialogFragment textFragment = null;
    private static AbSampleDialogFragment fullScreenFragment;
    private static Drawable progressDrawable;
    private static MaterialDialog materialDialog;
    private static MaterialDialog materialDialogAnother;

    public static void setIsFullScreen(boolean isFullScreen) {
        Dialogs.isFullScreen = isFullScreen;
    }

    public static boolean hasAlertDailog() {
        return alertDialog != null;
    }

    public static AlertDialog genAlertDialog(final Activity act, String title, String leftBtnTxt, String rightBtnTxt, int resId, final AbDialogOnClickListener listener) {
        View view = View.inflate(act, resId, null);
        return genAlertDialog(act, title, leftBtnTxt, rightBtnTxt, resId, listener);
    }

    public static AlertDialog genAlertDialog(final Activity act, String title, String leftBtnTxt, String rightBtnTxt, View customContentView, final AbDialogOnClickListener listener) {
        if (alertDialog != null) {
            return alertDialog;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle(title);
        builder.setView(customContentView);
        builder.setCancelable(false);

        if (Strs.isNotEmpty(rightBtnTxt)) {
            builder.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //点击确定按钮处理
                            if (listener != null) {
                                listener.onPositiveClick();
                            } else {
                                hideAlertDialog();
                            }
                        }

                    });
        }
        if (Strs.isNotEmpty(leftBtnTxt)) {
            builder.setNegativeButton(android.R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //点击取消按钮处理
                            if (listener != null) {
                                listener.onNegativeClick();
                            } else {
                                hideAlertDialog();
                            }
                        }
                    });
        }
        alertDialog = builder.create();
        return alertDialog;
    }

    public static void setStandardDialogStyle() {
        if (alertDialog == null) {
            return;
        }

        Button button1 = (Button) alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);//设置底部Button
        button1.setTextColor(Color.BLACK);//文字颜色
        button1.setTextSize(23);//文字大小

        Button button2 = (Button) alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button2.setTextColor(Color.RED);
        button2.setTextSize(23);
    }


    public static void showAlertDialog(DialogInterface.OnShowListener showListener, DialogInterface.OnDismissListener dismissListener) {
        if (alertDialog != null && !alertDialog.isShowing()) {
            alertDialog.setOnShowListener(showListener);
            alertDialog.setOnDismissListener(dismissListener);
            try {
                alertDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void hideAlertDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            alertDialog = null;
        }
    }

    /**
     * 当确定按钮点击时调用callback.stringCallback;
     *
     * @param act
     * @param listener
     */
    public static AbSampleDialogFragment showOptionListDialog(final Activity act, String title, final ListAdapter adapter, boolean isFullHeight, final AbDialogOnItemClickListener listener) {
        View view = LayoutInflater.from(act).inflate(R.layout.ab_dialog_option_list, null);
        final TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onNegativeClick();
                }
                removeDialog(act, OPTION_LIST_DIALOG);
            }
        });
        ListView listView = (ListView) view.findViewById(R.id.lvOptions);
        listView.setAdapter(adapter);
        if (isFullHeight) {
            Views.setListViewFullHeight(listView);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onItemClick(view, position, id);
                removeDialog(act, OPTION_LIST_DIALOG);
            }
        });
        AbSampleDialogFragment fragment = null;
        if (isFullScreen) {
            fragment = AbDialogUtil.showFullScreenDialog(OPTION_LIST_DIALOG, view);
        } else {
            fragment = AbDialogUtil.showDialog(OPTION_LIST_DIALOG, view);
        }
        return fragment;
    }

    /**
     * @param act
     */
    public static MaterialDialog showCustomDialog(final Activity act, View viewGroup, boolean cancellTouchOutside) {
        if (materialDialog != null) {
            try { // 如果上一个dialog在另一个activity中被系统dismiss，并且没有调用Dialogs.dissmisCustomDialog()，在某些平台上会抛异常
                materialDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            materialDialog = null;
        }
        materialDialog = new MaterialDialog(act);
        materialDialog.setCanceledOnTouchOutside(cancellTouchOutside);
        materialDialog.setContentView(viewGroup);
        materialDialog.setBackgroundResource(R.color.transparent);
        try {
            materialDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return materialDialog;
    }

    public static void dissmisCustomDialog() {
        if (materialDialog != null) {
            try {
                materialDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            materialDialog = null;
        }
    }

    /**
     * @param act
     */
    public static MaterialDialog showCustomDialogAnother(final Activity act, View viewGroup, boolean cancellTouchOutside) {
        if (materialDialogAnother != null) {
            try { // 如果上一个dialog在另一个activity中被系统dismiss，并且没有调用Dialogs.dissmisCustomDialog()，在某些平台上会抛异常
                materialDialogAnother.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            materialDialogAnother = null;
        }
        materialDialogAnother = new MaterialDialog(act);
        materialDialogAnother.setCanceledOnTouchOutside(cancellTouchOutside);
        materialDialogAnother.setContentView(viewGroup);
        materialDialogAnother.setBackgroundResource(R.color.transparent);
        try {
            materialDialogAnother.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return materialDialogAnother;
    }


    public static void dissmisCustomDialogAnother() {
        if (materialDialogAnother != null) {
            try {
                materialDialogAnother.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            materialDialogAnother = null;
        }
    }

    public static MaterialDialog showTipDialog(Context context, String title, String msg, String left, String right,
                                               View.OnClickListener nagetiveClickListener, View.OnClickListener positiveClickListener) {
        MaterialDialog dialog = new MaterialDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setNegativeButton(left, nagetiveClickListener);

        dialog.setPositiveButton(right, positiveClickListener);
        dialog.show();

        return dialog;

    }

    /**
     * 当确定按钮点击时调用callback.stringCallback;
     *
     * @param act
     * @return
     */
    public static AbSampleDialogFragment showFullScreenDialog(final Activity act, ViewGroup contentView) {
        fullScreenFragment = AbDialogUtil.showFullScreenDialog(FULLSCREEN_DIALOG, contentView);
        return fullScreenFragment;
    }

    public static void hideFullScreenDialog(final Activity act) {
        if (fullScreenFragment != null) {
            removeDialog(act, FULLSCREEN_DIALOG);
            fullScreenFragment = null;
        }
    }

    /**
     * 当确定按钮点击时调用callback.stringCallback;
     *
     * @param act
     * @param listener
     * @param hint
     * @return
     */
    public static AbSampleDialogFragment showEditDialog(final Activity act, String hint, final AbDialogTextOnClickListener listener) {
        View view = LayoutInflater.from(act).inflate(R.layout.ab_dialog_edit, null);
        final EditText etEdit = (EditText) view.findViewById(R.id.etEdit);
        etEdit.setHint(hint);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        Button btnConfirm = (Button) view.findViewById(R.id.btnConfirm);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onNegativeClick();
                }
                removeDialog(act, EDIT_DIALOG);
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPositiveClick(Views.getText(etEdit));
                }
            }
        });
        if (isFullScreen) {
            editFragment = AbDialogUtil.showFullScreenDialog(EDIT_DIALOG, view);
        } else {
            editFragment = AbDialogUtil.showDialog(EDIT_DIALOG, view);
        }
        return editFragment;
    }

    public static void hideEditDialog(final Activity act) {
        if (editFragment != null) {
            removeDialog(act, EDIT_DIALOG);
            editFragment = null;
        }
    }


    /**
     * 当确定按钮点击时调用callback.stringCallback;
     *
     * @param act
     * @param listener
     * @return
     */
    public static AbSampleDialogFragment showTextDialog(final Activity act, String msg, String leftBtnTxt, String rightBtnTxt, final AbDialogOnClickListener listener) {
        View view = LayoutInflater.from(act).inflate(R.layout.ab_dialog_edit, null);
        final EditText etEdit = (EditText) view.findViewById(R.id.etEdit);
        Views.setEditorEnable(etEdit, false);
        etEdit.setText(msg);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        Button btnConfirm = (Button) view.findViewById(R.id.btnConfirm);

        if (Strs.isNotEmpty(leftBtnTxt)) {
            btnCancel.setText(leftBtnTxt);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onNegativeClick();
                    }
                    removeDialog(act, TEXT_DIALOG);
                }
            });
        } else {
            btnConfirm.setVisibility(View.GONE);
        }

        if (Strs.isNotEmpty(rightBtnTxt)) {
            btnConfirm.setText(rightBtnTxt);
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onPositiveClick();
                    }
                    removeDialog(act, TEXT_DIALOG);
                }
            });
        } else {
            btnConfirm.setVisibility(View.GONE);
        }

        if (isFullScreen) {
            textFragment = AbDialogUtil.showFullScreenDialog(TEXT_DIALOG, view);
        } else {
            textFragment = AbDialogUtil.showDialog(TEXT_DIALOG, view);
        }
        return textFragment;
    }

    public static void hideTextDialog(final Activity act) {
        if (textFragment != null) {
            removeDialog(act, TEXT_DIALOG);
            textFragment = null;
        }
    }


    /**
     * @param act
     */
    public static Dialog showProgressDialog(final Activity act, String msg) {
        if (progressFragment == null) {
            if (progressDrawable == null) {
                progressDrawable = Views.fromDrawables(R.drawable.progress_drawable_white);
            }
            progressFragment = new Dialog(act, R.style.progress_dialog);
            progressFragment.setContentView(R.layout.ab_dialog_progress);
            progressFragment.setCancelable(true);
            progressFragment.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            ViewGroup content = (ViewGroup) progressFragment.findViewById(R.id.vgContent);
            ProgressBar progressBar = (ProgressBar) progressFragment.findViewById(R.id.pbProgress);
            if (progressBar != null) {
                progressBar.setIndeterminateDrawable(progressDrawable);
            }
            TextView tvMsg = (TextView) progressFragment.findViewById(R.id.id_tv_loadingmsg);
            if (tvMsg != null) {
                if (Strs.isNotEmpty(msg)) {
                    tvMsg.setText(msg);
                } else {
                    tvMsg.setVisibility(View.GONE);
                }
            }
            progressFragment.setCanceledOnTouchOutside(false);
            try {
                progressFragment.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return progressFragment;
    }

    /**
     * @param act
     */
    public static Dialog showLoadingDialog(final Activity act, String msg) {
        if (progressFragment == null) {
            if (progressDrawable == null) {
                progressDrawable = Views.fromDrawables(R.drawable.progress_drawable_white);
            }
            progressFragment = new Dialog(act, R.style.progress_dialog);
            progressFragment.setContentView(R.layout.ab_dialog_loading);
            progressFragment.setCancelable(true);
            progressFragment.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            ViewGroup content = (ViewGroup) progressFragment.findViewById(R.id.vgContent);
            ProgressBar progressBar = (ProgressBar) progressFragment.findViewById(R.id.pbProgress);
            if (progressBar != null) {
                progressBar.setIndeterminateDrawable(progressDrawable);
            }
            TextView tvMsg = (TextView) progressFragment.findViewById(R.id.id_tv_loadingmsg);
            if (tvMsg != null) {
                if (Strs.isNotEmpty(msg)) {
                    tvMsg.setText(msg);
                } else {
                    tvMsg.setVisibility(View.GONE);
                }
            }
            progressFragment.setCanceledOnTouchOutside(false);
            try {
                progressFragment.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return progressFragment;
    }

    /**
     * 当确定按钮点击时调用callback.stringCallback;
     */
    public static void updateProgressDialog(String msg) {
        if (progressFragment != null) {
            progressFragment.setTitle(msg);
        }
    }

    public static void hideProgressDialog(final Activity act) {
        if (progressFragment != null) {
            try {
                progressFragment.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            progressFragment = null;
        }
    }

    /**
     * 描述：移除Fragment.
     *
     * @param context the context
     */
    public static void removeDialog(final Context context, final String mDialogTag) {
        ThreadCollector.getIns().runOnUIThread(new Runnable() {

            @Override
            public void run() {
                try {
                    FragmentActivity activity = (FragmentActivity) context;
                    android.support.v4.app.FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    // 指定一个系统转场动画
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    android.support.v4.app.Fragment prev = activity.getSupportFragmentManager().findFragmentByTag(mDialogTag);
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    //ft.addToBackStack(null);
                    ft.commit();
                } catch (Exception e) {
                    //可能有Activity已经被销毁的异常
                    e.printStackTrace();
                }
            }
        });

    }


}
