package com.desheng.app.toucai.panel;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.ab.global.Config;
import com.ab.global.ENV;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.ClipboardUtils;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.fragment.BasePageFragment;
import com.desheng.base.action.HttpAction;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.OpenAccount;
import com.noober.background.view.BLRadioButton;
import com.noober.background.view.BLRadioGroup;
import com.noober.background.view.BLTextView;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragLinkOpenAccount extends BasePageFragment implements View.OnClickListener {
    private EditText etReturn;
    private BLTextView createAccount;
    private BLRadioGroup accountType;
    private OpenAccount openAccount;
    private int type = 0;
    private int day = 1;
    private BLRadioButton typePlayer;
    private String lotteryPoint;
    private Spinner spinner;
    private List<String> spinnerDataList;
    private ArrayAdapter<String> adapter;
    private int[] selectDays = {1, 7, 30, -1};
    private BLRadioButton typeAgent;

    public static FragLinkOpenAccount newInstance(OpenAccount openAccountInfo) {
        FragLinkOpenAccount fragment = new FragLinkOpenAccount();
        Bundle bundle = new Bundle();
        bundle.putSerializable("addAccount", openAccountInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int setContentView() {
        return R.layout.layout_frag_link_openaccount;
    }

    @Override
    public void fetchData() {
    }

    @Override
    protected void initView(View rootview) {
        etReturn = ((EditText) rootview.findViewById(R.id.fandian));
        createAccount = ((BLTextView) rootview.findViewById(R.id.createAccount));
        accountType = ((BLRadioGroup) rootview.findViewById(R.id.accountType));
        typePlayer = ((BLRadioButton) rootview.findViewById(R.id.typePlayer));
        typeAgent = ((BLRadioButton) rootview.findViewById(R.id.typeAgent));
        spinner = ((Spinner) rootview.findViewById(R.id.spinner));
        openAccount = (OpenAccount) getArguments().getSerializable("addAccount");
        createAccount.setOnClickListener(this);

        spinnerDataList = new ArrayList<String>();
        spinnerDataList.add("一天");
        spinnerDataList.add("七天");
        spinnerDataList.add("三十天");
        spinnerDataList.add("永久有效");

        /*为spinner定义适配器，也就是将数据源存入adapter，这里需要三个参数
        1. 第一个是Context（当前上下文），这里就是this
        2. 第二个是spinner的布局样式，这里用android系统提供的一个样式
        3. 第三个就是spinner的数据源，这里就是dataList*/
        adapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, spinnerDataList);

        //为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //为spinner绑定我们定义好的数据适配器
        spinner.setAdapter(adapter);

        //为spinner绑定监听器，这里我们使用匿名内部类的方式实现监听器
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                day = selectDays[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner.setSelection(0);

        accountType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.typeAgent:
                        String minPointAgentStr = Nums.formatDecimal(openAccount.lotteryAgentRange.minPoint, 1);
                        String maxPointAgentStr = Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint, 1);
                        if (Strs.isEqual(minPointAgentStr, maxPointAgentStr)) {
                            etReturn.setText(minPointAgentStr);
                            etReturn.setEnabled(false);
                        } else {
                            etReturn.setHint("可分配范围 " + minPointAgentStr + " ~ " + maxPointAgentStr);
                        }
                        type = 1;
                        break;
                    case R.id.typePlayer://默认为玩家
                        String minPointPlayerStr = Nums.formatDecimal(openAccount.lotteryPlayerRange.minPoint, 1);
                        String maxPointPlayerStr = Nums.formatDecimal(openAccount.lotteryPlayerRange.maxPoint, 1);
                        if (Strs.isEqual(minPointPlayerStr, maxPointPlayerStr)) {
                            etReturn.setText(minPointPlayerStr);
                            etReturn.setEnabled(false);
                        } else {
                            etReturn.setHint("可分配范围 " + minPointPlayerStr + " ~ " + maxPointPlayerStr);
                        }
                        type = 0;
                        break;
                    default:
                }
            }
        });
        if (openAccount != null) {
            typePlayer.setVisibility(openAccount.allowAddUser ? View.VISIBLE : View.GONE);
            type = openAccount.allowAddUser ? 0 : 1;
            typeAgent.setVisibility(openAccount.allowAddAgent ? View.VISIBLE : View.GONE);
            typePlayer.setChecked(openAccount.allowAddUser ? true : false);
            typeAgent.setChecked((!openAccount.allowAddUser && openAccount.allowAddAgent) ? true : false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createAccount:
                lotteryPoint = etReturn.getText().toString();

                String[] numParts = lotteryPoint.split("\\.");
                if (numParts.length == 2 && numParts[1].length() > 1) {
                    Toasts.show(getActivity(), "返点只能输入一位小数", false);
                    return;
                }

                double point = Nums.parse(lotteryPoint, 0.0);

                double min = 0.0;
                double max = 0.0;
                if (type == 1) {
                    min = openAccount.lotteryAgentRange.minPoint;
                    max = openAccount.lotteryAgentRange.maxPoint;
                } else {
                    min = openAccount.lotteryPlayerRange.minPoint;
                    max = openAccount.lotteryPlayerRange.maxPoint;
                }
                if (point < 0 || point - min < -0.00001 || point - max > 0.00001) {
                    Toast.makeText(mActivity, "请输入正确返点", Toast.LENGTH_LONG).show();
                    return;
                }

                if (Config.custom_flag.equals(BaseConfig.FLAG_JINDU) && UserManager.getIns().getMainUserLevel() == 5) {
                    if (point < 0 || point < min || point > max - 0.1) {
                        Toast.makeText(mActivity, "请输入正确返点", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                getLink();
                break;
        }
    }

    /**
     * 链接开户
     */
    public void getLink() {
        HttpAction.addRegistLink(type, day, lotteryPoint, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", String.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    // 将文本内容放到系统剪贴板里。
                    String data = getFieldObject(extra, "data", String.class);
                    if (data.endsWith(".html")) {
                        data = data.replace(".html", "");
                    }
                    List<String> urls = UserManager.getIns().getUrls();
                    if (urls != null && urls.size() > 0) {
                        ClipboardUtils.copyText(mActivity, urls.get(0) + data);
                    } else {
                        ClipboardUtils.copyText(mActivity, ENV.curr.host + data);
                    }
                    Toasts.show(getContext(), "开户成功链接生成成功，并已复制链接!", true);
                } else {
                    Toasts.show(getContext(), msg);
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                return true;
            }
        });
    }
}
