package com.desheng.base.view.trendchart;

import com.desheng.base.util.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyTestClass {

    private List<CartWqBean> mySingleData(List<CartWqBean> cartList, List<int[]> posList, TrendTypeBean trendTypeBean) {
        ArrayList<Integer[]> tempPosList = new ArrayList();
        String[] arrayCode;

        for (int i = 0; i < cartList.size(); i++) {
            ArrayList<Integer> integerList = new ArrayList();
            arrayCode = (cartList.get(i)).getCode().split(",");
            try {

                if (posList.size() <= 1) {
                    break;
                }

                for (String str : arrayCode) {
                    integerList.add(Integer.valueOf(Integer.parseInt(str.trim())));
                }

                Collections.sort(integerList);
                tempPosList.add(new Integer[]{integerList.get((posList.get(1))[0])});
            } catch (Exception e) {
                e.printStackTrace();
            }

            tempPosList.add(new Integer[]{integerList.get((posList.get(0))[0])});
            int[] posArray = backNum(trendTypeBean);

            for (int l = 0; l < cartList.size(); l++) {
                StringBuilder sbName = new StringBuilder();
                List<Integer> nativeList = new ArrayList();

                for (int k = 0; k < posArray.length; k++) {
                    if ((tempPosList.get(0))[0].intValue() == Integer.parseInt(trendTypeBean.getHeadNum()[k].trim())) {
                        posArray[k] = 0;
                    } else {
                        posArray[k] += 1;
                    }
                    if (k == posArray.length - 1) {
                        sbName.append(posArray[k]);
                    } else {
                        sbName.append(posArray[k]);
                        sbName.append(",");
                    }
                    nativeList.add(Integer.valueOf(posArray[k]));
                }
                LogUtil.logE("singleData", "nativeCode:" + sbName.toString());
                cartList.get(l).setNative_code(sbName.toString());
                cartList.get(l).setNativeCodeInt(nativeList);
            }

            return cartList;
        }

        return cartList;

    }

    private int[] backNum(TrendTypeBean trendTypeBean) {
        int[] missBallNum = new int[trendTypeBean.getBlueMissBallNum().length];

        for (int i = 0; i < missBallNum.length; i++) {
            missBallNum[i] = 0;
        }

        return missBallNum;
    }
}
