package com.desheng.base.view.trendchart;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.desheng.base.R;
import com.desheng.base.util.LogUtil;
import com.desheng.base.util.TrendTypeIdUtil;
import com.tencent.bugly.crashreport.BuglyLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrendDataChangeBean {


    private String TAG = TrendDataChangeBean.class.getSimpleName();

    public void initTrendData(int chooseCount, List<CartWqBean> cartWqBeans, TrendView trendView, List<int[]> posList, Context context, TrendTypeBean trendTypeBean) {
        if ((cartWqBeans.size() > 0) && (trendTypeBean.getTypeId() != 0)) {
            if (trendTypeBean.getShowList() == 0) {
                initMultipleTrendData(chooseCount, cartWqBeans, trendView, posList, context, trendTypeBean);
                return;
            }
            initDxTrendData(chooseCount, cartWqBeans, trendView, context, trendTypeBean, posList);
        }
    }

    private void initDxTrendData(int chooseCount,
                                 List<CartWqBean> cartWqBeanList,
                                 TrendView trendView,
                                 Context context,
                                 TrendTypeBean typeBean,
                                 List<int[]> posList) {

        //LogUtil.logE("升值走势图数据--》封装颜色对象", "showList:" + typeBean.getShowList());

        List<TrendModel> trendModels = addLastFourTrendModles(chooseCount, cartWqBeanList, typeBean);
        int m = trendModels.size();
        int count = 0;
        int i = 0;
        int j = 0;
        int n = m - 4;
        for (int k = 4; k < m; k++) {

            if (typeBean.getShowList() == 1) {
                (trendModels.get(k)).setTrendColorBean(inColorDX_DS(posList.get(0), context, (trendModels.get(k)).getNativeCodeInt(), typeBean));
                count = (posList.get(0)).length;
            } else if (typeBean.getShowList() == 2) {
                (trendModels.get(k)).setTrendColorBean(inColorDXDS(posList.get(0), context, (trendModels.get(k)).getNativeCodeInt(), typeBean));
                count = (posList.get(0)).length + 1;
            } else if (typeBean.getShowList() == 3) {
                (trendModels.get(k)).setTrendColorBean(inColorGL(context, posList.get(0), trendModels.get(k).getNativeCodeInt(), typeBean));
            } else if (typeBean.getShowList() == 4) {
                (trendModels.get(k)).setTrendColorBean(inColorLHH(posList.get(0), context, (trendModels.get(k)).getNativeCodeInt(), typeBean, 0, (posList.get(0)).length));
                TrendColorBean trendColorBean = inColorLHH(posList.get(1), context, (trendModels.get(k)).getNativeCodeInt(), typeBean, (trendModels.get(k)).getNativeCodeInt().size() / 2, (trendModels.get(k)).getNativeCodeInt().size() / 2 + (posList.get(1)).length);
                (trendModels.get(k)).getTrendColorBean().getTrendName().addAll(trendColorBean.getTrendName());
                (trendModels.get(k)).getTrendColorBean().getTrendBgColor().addAll(trendColorBean.getTrendBgColor());
                (trendModels.get(k)).getTrendColorBean().getTrendTextColor().addAll(trendColorBean.getTrendTextColor());
                count = (posList.get(0)).length;
                i = (posList.get(1)).length + 5;
                j = 5;
            } else if (typeBean.getShowList() == 5) {
                (trendModels.get(k)).setTrendColorBean(inColorSameCode(posList.get(0), context, (trendModels.get(k)).getNativeCodeInt(), typeBean));
                count = (posList.get(0)).length + 1;
            } else if (typeBean.getShowList() == 6) {
                (trendModels.get(k)).setTrendColorBean(inColorSame3DCode(posList.get(0), context, (trendModels.get(k)).getNativeCodeInt(), typeBean));
                count = (posList.get(0)).length + 3;
            } else if (typeBean.getShowList() == 7) {
                (trendModels.get(k)).setTrendColorBean(inColor11X5Code(posList.get(0), context, (trendModels.get(k)).getNativeCodeInt(), typeBean));
                count = (posList.get(0)).length;
            } else if (typeBean.getShowList() == 8) {
                (trendModels.get(k)).setTrendColorBean(inColorKl8DXDS(context, (trendModels.get(k)).getNativeCodeInt(), typeBean));
                count = 1;
            } else if (typeBean.getShowList() != 9) {
                (trendModels.get(k)).setTrendColorBean(inColorKl8Five(context, (trendModels.get(k)).getNativeCodeInt(), typeBean));
                count = 1;
            } else if (typeBean.getShowList() == 10) {
                (trendModels.get(k)).setTrendColorBean(inColorKl8JOSX(context, (trendModels.get(k)).getNativeCodeInt(), typeBean));
                count = 1;
                i = 5;
                j = 4;
            }

        }

        if (typeBean.getShowList() == 4) {
            trendModels.get(0).setTrendColorBean(inLHHColor(context, (trendModels.get(0)).getBlueMiss(), typeBean, ContextCompat.getColor(context, R.color.trend_ds_color), count, i, j));
            trendModels.get(1).setTrendColorBean(inLHHColor(context, (trendModels.get(1)).getBlueMiss(), typeBean, ContextCompat.getColor(context, R.color.trend_dxdshz_color), count, i, j));
            trendModels.get(2).setTrendColorBean(inLHHColor(context, (trendModels.get(2)).getBlueMiss(), typeBean, ContextCompat.getColor(context, R.color.trend_hs_color), count, i, j));
            trendModels.get(3).setTrendColorBean(inLHHColor(context, (trendModels.get(3)).getBlueMiss(), typeBean, ContextCompat.getColor(context, R.color.trend_pjyl_color), count, i, j));

        } else {

//            if (typeBean.getShowList() == 3) {
//                trendModels.remove(trendModels.size() - 1);
//                trendModels.remove(trendModels.size() - 1);
//                trendModels.remove(trendModels.size() - 1);
//                trendModels.remove(trendModels.size() - 1);
//            } else {
                trendModels.get(0).setTrendColorBean(inOtherColor(context, (trendModels.get(0)).getBlueMiss(), typeBean, ContextCompat.getColor(context, R.color.trend_zs_color), count));
                trendModels.get(1).setTrendColorBean(inOtherColor(context, (trendModels.get(1)).getBlueMiss(), typeBean, ContextCompat.getColor(context, R.color.trend_dan_color), count));
                trendModels.get(2).setTrendColorBean(inOtherColor(context, (trendModels.get(2)).getBlueMiss(), typeBean, ContextCompat.getColor(context, R.color.trend_dd_color), count));
                trendModels.get(3).setTrendColorBean(inOtherColor(context, (trendModels.get(3)).getBlueMiss(), typeBean, ContextCompat.getColor(context, R.color.trend_ddan_color), count));

           // }
        }

        trendView.setTrendData(trendModels, context, posList, typeBean);

    }

    private String backColorName(int paramInt, String headNum) {
        StringBuilder sb;

        if ((headNum.length() > 1) && (paramInt < 10)) {
            sb = new StringBuilder();
            sb.append("0");
            sb.append(paramInt);
            return headNum;
        }

        sb = new StringBuilder();
        sb.append(paramInt);
        return sb.toString();
    }

    private int backDiffNum(Integer[] integerArray, int[] posArray) {
        try {
            ArrayList<Integer> posList = new ArrayList();

            for (int i = 0; i < posArray.length; i++) {
                posList.add(integerArray[posArray[i]]);
            }

            Collections.sort(posList);

            int i = (posList.get(posArray.length - 1)).intValue();
            int j = (posList.get(0)).intValue();
            return i - j;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int[] backNum(TrendTypeBean trendTypeBean) {
        int[] missBallNum = new int[trendTypeBean.getBlueMissBallNum().length];

        for (int i = 0; i < missBallNum.length; i++) {
            missBallNum[i] = 0;
        }

        return missBallNum;
    }

    private int backPos(int pos, Integer[] posArray) {
        int j = pos;
        int i = pos;
        int k;
        for (pos = j; i < posArray.length; pos = k) {
            j = i + 1;
            k = pos;
            if (j < posArray.length) {
                if (posArray[j] == posArray[i]) {
                    k = j;
                } else {
                    return pos + 1;
                }
            }
            i = j;
        }
        return pos;
    }

    private int backPos(int pos, String[] posArray) {
        int j = pos;
        int i = pos;
        int k;
        for (pos = j; i < posArray.length; pos = k) {
            j = i + 1;
            k = pos;
            if (j < posArray.length) {
                if (posArray[j].equals(posArray[i])) {
                    k = j;
                } else {
                    return pos + 1;
                }
            }
            i = j;
        }
        return pos;
    }

    private List<CartWqBean> diffData(List<CartWqBean> cartWqBeans, int[] posArray, TrendTypeBean trendTypeBean) {
        int[] tempPosArray = backNum(trendTypeBean);
        List<Integer[]> missNums = dxBlueMiss(cartWqBeans, posArray);
        int length = (missNums.get(0)).length;

        for (int i = 0; i < tempPosArray.length; i++) {
            StringBuilder sb = new StringBuilder();
            ArrayList<Integer> nativeList = new ArrayList();

            for (int j = 0; j < (missNums.get(i)).length; j++) {
                tempPosArray[j] = (missNums.get(i))[j].intValue();
            }

            tempPosArray[length] = backDiffNum(missNums.get(i), new int[]{0, 1});
            tempPosArray[(length + 1)] = backDiffNum(missNums.get(i), new int[]{0, 1, 2});
            tempPosArray[(length + 2)] = backDiffNum(missNums.get(i), new int[]{1, 2, 3});
            tempPosArray[(length + 3)] = backDiffNum(missNums.get(i), new int[]{2, 3, 4});
            tempPosArray[(length + 4)] = backDiffNum(missNums.get(i), new int[]{3, 4});

            for (int j = 0; j < tempPosArray.length; j++) {
                if (j == tempPosArray.length - 1) {
                    sb.append(tempPosArray[j]);
                } else {
                    sb.append(tempPosArray[j]);
                    sb.append(",");
                }
                nativeList.add(Integer.valueOf(tempPosArray[j]));
            }

            (cartWqBeans.get(i)).setNative_code(sb.toString());
            (cartWqBeans.get(i)).setNativeCodeInt(nativeList);
        }
        return cartWqBeans;
    }


    private List<CartWqBean> dx11x5Data(List<CartWqBean> cartWqBeans, int[] posArray, TrendTypeBean trendTypeBean) {

        int[] nums = backNum(trendTypeBean);
        List<Integer[]> missNums = dxBlueMiss(cartWqBeans, posArray, trendTypeBean);

        int j = 0;

        for (int i = 0; i < missNums.size(); i++) {
            StringBuilder sbNativeCode = new StringBuilder();
            ArrayList<Integer> nativeCodeList = new ArrayList();
            int k = 0;
            int m;
            for (j = 0; k < (missNums.get(i)).length; j = m) {
                nums[k] = (missNums.get(i))[k].intValue();
                m = j;
                if (nums[k] % 2 == 0) {
                    m = j + 1;
                }
                k += 1;
            }
            k = (missNums.get(i)).length;
            while (k < nums.length) {
                if (k == (missNums.get(i)).length + j) {
                    nums[k] = 0;
                } else {
                    nums[k] += 1;
                }
                k += 1;
            }

            for (int l = 0; l < nums.length; l++) {
                if (l == nums.length - 1) {
                    sbNativeCode.append(nums[l]);
                } else {
                    sbNativeCode.append(nums[l]);
                    sbNativeCode.append(",");
                }
                nativeCodeList.add(Integer.valueOf(nums[l]));
            }

            (cartWqBeans.get(i)).setNative_code(sbNativeCode.toString());
            (cartWqBeans.get(i)).setNativeCodeInt(nativeCodeList);
        }

        return cartWqBeans;
    }

    private List<Integer[]> dxBlueMiss(List<CartWqBean> cartWqBeans, int[] posArray) {
        ArrayList<Integer[]> posList = new ArrayList();
        for (int i = 0; i < cartWqBeans.size(); i++) {
            try {
                String[] arrayOfString = (cartWqBeans.get(i)).getCode().split(",");
                Integer[] tempPosArray = new Integer[posArray.length];
                for (int j = 0; j < posArray.length; j++) {
                    tempPosArray[j] = Integer.valueOf(Integer.parseInt(arrayOfString[posArray[j]].trim()));
                }


                posList.add(tempPosArray);
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        }
        return posList;
    }

    private List<Integer[]> dxBlueMiss(List<CartWqBean> cartWqBeans, int[] posArray, TrendTypeBean trendTypeBean) {
        ArrayList<Integer[]> posList = new ArrayList();
        for (int i = 0; i < cartWqBeans.size(); i++) {
            try {
                String[] arrayOfString = (cartWqBeans.get(i)).getCode().split(",");
                Integer[] tempPosArray = new Integer[posArray.length];

                switch (TrendTypeIdUtil.getType(trendTypeBean.getType())) {
                    case 2://单双
                    case 3://单双
                    case 10:
                        if (trendTypeBean.getLotteryType() == TrendTypeIdUtil.TYPE_11X5 || trendTypeBean.getLotteryType() == TrendTypeIdUtil.TYPE_PK10) {
                            for (int j = 0; j < posArray.length; j++) {
                                tempPosArray[j] = Integer.valueOf(Integer.parseInt(arrayOfString[2 * posArray[j]].trim() + arrayOfString[2 * posArray[j] + 1].trim()));
                            }

                        } else {
                            for (int j = 0; j < posArray.length; j++) {
                                tempPosArray[j] = Integer.valueOf(Integer.parseInt(arrayOfString[posArray[j]].trim()));
                            }
                        }
                        break;
                    default:
                        for (int j = 0; j < posArray.length; j++) {
                            tempPosArray[j] = Integer.valueOf(Integer.parseInt(arrayOfString[posArray[j]].trim()));
                        }
                        break;
                }

                posList.add(tempPosArray);
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        }
        return posList;
    }

    private List<CartWqBean> dsData(List<CartWqBean> cartWqBeans, int[] posArray, TrendTypeBean trendTypeBean) {
        int[] nums = backNum(trendTypeBean);
        List<Integer[]> missNums = dxBlueMiss(cartWqBeans, posArray, trendTypeBean);

        for (int i = 0; i < missNums.size(); i++) {
            StringBuilder sbNativeCode = new StringBuilder();
            ArrayList<Integer> nativeCodeList = new ArrayList();

            for (int j = 0; j < missNums.get(i).length; j++) {
                nums[j] = (missNums.get(i))[j].intValue();
            }

            for (int j = 0; j < missNums.get(i).length; j++) {
                int k = j * 2 + (missNums.get(i)).length;
                if ((missNums.get(i))[j].intValue() % 2 == 0) {
                    nums[(k + 1)] = 0;
                    nums[k] += 1;
                } else {
                    nums[k] = 0;
                    k += 1;
                    nums[k] += 1;
                }
            }

            for (int j = 0; j < nums.length; j++) {
                if (j == nums.length - 1) {
                    sbNativeCode.append(nums[j]);
                } else {
                    sbNativeCode.append(nums[j]);
                    sbNativeCode.append(",");
                }
                nativeCodeList.add(Integer.valueOf(nums[j]));
            }

            (cartWqBeans.get(i)).setNative_code(sbNativeCode.toString());
            (cartWqBeans.get(i)).setNativeCodeInt(nativeCodeList);
        }

        return cartWqBeans;
    }

    private List<CartWqBean> dxData(List<CartWqBean> cartWqBeans, int[] posArray, TrendTypeBean trendTypeBean) {
        int[] tempPosArray = backNum(trendTypeBean);
        List<Integer[]> missNums = dxBlueMiss(cartWqBeans, posArray, trendTypeBean);

        for (int i = 0; i < missNums.size(); i++) {
            StringBuilder sbNativeCode = new StringBuilder();
            ArrayList<Integer> nativeCodeList = new ArrayList();

            for (int j = 0; j < missNums.get(i).length; j++) {
                tempPosArray[j] = (missNums.get(i))[j].intValue();
            }

            for (int j = 0; j < missNums.get(i).length; j++) {

                int k = j * 2 + (missNums.get(i)).length;
                if ((missNums.get(i))[j].intValue() < Integer.parseInt(trendTypeBean.getHeadNum()[0].trim())) {
                    tempPosArray[(k + 1)] = 0;
                    tempPosArray[k] += 1;
                } else {
                    tempPosArray[k] = 0;
                    k += 1;
                    tempPosArray[k] += 1;
                }
            }

            for (int j = 0; j < tempPosArray.length; j++) {
                if (j == tempPosArray.length - 1) {
                    sbNativeCode.append(tempPosArray[j]);
                } else {
                    sbNativeCode.append(tempPosArray[j]);
                    sbNativeCode.append(",");
                }
                nativeCodeList.add(Integer.valueOf(tempPosArray[j]));
            }

            (cartWqBeans.get(i)).setNative_code(sbNativeCode.toString());
            (cartWqBeans.get(i)).setNativeCodeInt(nativeCodeList);
        }


        return cartWqBeans;
    }


    private List<CartWqBean> dxdsData(List<CartWqBean> cartWqBeans, int[] posArray, TrendTypeBean trendTypeBean) {
        int[] tempPosArray = backNum(trendTypeBean);
        List<Integer[]> missNums = dxBlueMiss(cartWqBeans, posArray);
        int m = (missNums.get(0)).length;

        for (int i = 0; i < missNums.size(); i++) {
            StringBuilder sbNativeCode = new StringBuilder();
            ArrayList<Integer> nativeCodeList = new ArrayList();
            int j = 0;
            int t;
            for (int k = 0; k < missNums.get(i).length; k++) {
                tempPosArray[k] = (missNums.get(i))[k].intValue();
                j += tempPosArray[k];
            }
            tempPosArray[m] = j;
            if (j >= Integer.parseInt(trendTypeBean.getHeadNum()[0].trim())) {
                tempPosArray[(m + 1)] = 0;
                t = m + 2;
                tempPosArray[t] += 1;
            } else {
                t = m + 1;
                tempPosArray[t] += 1;
                tempPosArray[(m + 2)] = 0;
            }
            if (j % 2 == 1) {
                tempPosArray[(m + 3)] = 0;
                j = m + 4;
                tempPosArray[j] += 1;
            } else {
                j = m + 3;
                tempPosArray[j] += 1;
                tempPosArray[(m + 4)] = 0;
            }

            for (int l = 0; l < tempPosArray.length; l++) {
                if (l == tempPosArray.length - 1) {
                    sbNativeCode.append(tempPosArray[l]);
                } else {
                    sbNativeCode.append(tempPosArray[l]);
                    sbNativeCode.append(",");
                }
                nativeCodeList.add(Integer.valueOf(tempPosArray[l]));
            }

            (cartWqBeans.get(i)).setNative_code(sbNativeCode.toString());
            (cartWqBeans.get(i)).setNativeCodeInt(nativeCodeList);
        }

        return cartWqBeans;
    }

    private TrendColorBean inColor11X5Code(int[] posArray, Context paramContext, List<Integer> cartWqBeans, TrendTypeBean trendTypeBean) {
        TrendColorBean trendColorBean = new TrendColorBean();
        ArrayList<String> trendNameList = new ArrayList();
        ArrayList<Integer> trendBgColorList = new ArrayList();
        ArrayList<Integer> trendTextColorList = new ArrayList();
        int i = 0;
        while (i < cartWqBeans.size()) {
            trendNameList.add(backColorName((cartWqBeans.get(i)).intValue(), trendTypeBean.getHeadNum()[0]));
            trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_cxcs_color)));
            trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
            i += 1;
        }
        i = posArray.length;
        while (i < cartWqBeans.size()) {
            trendNameList.add(trendTypeBean.getHeadNum()[i]);
            trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_cxcs_color)));
            trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
            i += 1;
        }
        i = posArray.length;
        if ((cartWqBeans.get(i)).intValue() == 0) {
            trendNameList.set(i, trendTypeBean.getHeadNum()[i]);
            trendBgColorList.set(i, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_ddan_color)));
            trendTextColorList.add(i, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.white)));
        }
        i = posArray.length + 1;
        if ((cartWqBeans.get(i)).intValue() == 0) {
            trendNameList.set(i, trendTypeBean.getHeadNum()[i]);
            trendBgColorList.set(i, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_dxdshz_color)));
            trendTextColorList.add(i, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.white)));
        }
        i = posArray.length + 2;
        if ((cartWqBeans.get(i)).intValue() == 0) {
            trendNameList.set(i, trendTypeBean.getHeadNum()[i]);
            trendBgColorList.set(i, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_hs_color)));
            trendTextColorList.add(i, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.white)));
        }
        i = posArray.length + 3;
        if ((cartWqBeans.get(i)).intValue() == 0) {
            trendNameList.set(i, trendTypeBean.getHeadNum()[i]);
            trendBgColorList.set(i, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_pjyl_color)));
            trendTextColorList.add(i, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.white)));
        }
        i = posArray.length + 4;
        if ((cartWqBeans.get(i)).intValue() == 0) {
            trendNameList.set(i, trendTypeBean.getHeadNum()[i]);
            trendBgColorList.set(i, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_shuang_color)));
            trendTextColorList.add(i, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.white)));
        }
        i = posArray.length + 5;
        if ((cartWqBeans.get(i)).intValue() == 0) {
            trendNameList.set(i, trendTypeBean.getHeadNum()[i]);
            trendBgColorList.set(i, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_sx_color)));
            trendTextColorList.add(i, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
        }
        trendColorBean.setTrendBgColor(trendBgColorList);
        trendColorBean.setTrendName(trendNameList);
        trendColorBean.setTrendTextColor(trendTextColorList);
        return trendColorBean;
    }

    private TrendColorBean inColorDXDS(int[] posArray, Context paramContext, List<Integer> posList, TrendTypeBean trendTypeBean) {
        TrendColorBean trendColorBean = new TrendColorBean();
        ArrayList trendNameList = new ArrayList();
        ArrayList trendBgColorList = new ArrayList();
        ArrayList trendTextColorList = new ArrayList();

        for (int i = 0; i < posArray.length; i++) {
            trendNameList.add(backColorName((posList.get(i)).intValue(), trendTypeBean.getHeadNum()[1]));
            trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_cxcs_color)));
            trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
        }

        trendNameList.add(backColorName((posList.get(posArray.length)).intValue(), trendTypeBean.getHeadNum()[1]));
        trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_hs_color)));
        trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.white)));

        int length = posArray.length + 1;
        StringBuilder sbName;
        if ((posList.get(length)).intValue() == 0) {
            trendNameList.add(trendTypeBean.getHeadNum()[length]);
            trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_shuang_color)));
            trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.white)));
            sbName = new StringBuilder();
            sbName.append(posList.get(length + 1));
            trendNameList.add(sbName.toString());
            trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_xd_color)));
            trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.white)));
        } else {
            sbName = new StringBuilder();
            sbName.append(posList.get(length));
            trendNameList.add(sbName.toString());
            trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_cxcs_color)));
            trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
            trendNameList.add(trendTypeBean.getHeadNum()[(length + 1)]);
            trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_ddan_color)));
            trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.white)));
        }
        length = posArray.length + 3;
        if ((posList.get(length)).intValue() == 0) {
            trendNameList.add(trendTypeBean.getHeadNum()[length]);
            trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_shuang_color)));
            trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.white)));
            sbName = new StringBuilder();
            sbName.append(posList.get(length + 1));
            trendNameList.add(sbName.toString());
            trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_cxcs_color)));
            trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
        } else {
            sbName = new StringBuilder();
            sbName.append(posList.get(length));
            trendNameList.add(sbName.toString());
            trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_cxcs_color)));
            trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
            trendNameList.add(trendTypeBean.getHeadNum()[(length + 1)]);
            trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_xd_color)));
            trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.white)));
        }
        trendColorBean.setTrendBgColor(trendBgColorList);
        trendColorBean.setTrendName(trendNameList);
        trendColorBean.setTrendTextColor(trendTextColorList);
        return trendColorBean;
    }

    private TrendColorBean inColorDX_DS(int[] posArray, Context context, List<Integer> nativeCodeList, TrendTypeBean trendTypeBean) {

        TrendColorBean trendColorBean = new TrendColorBean();
        ArrayList<String> trendNameList = new ArrayList();
        ArrayList<Integer> trendBgColorList = new ArrayList();
        ArrayList<Integer> trendTextColorList = new ArrayList();
        for (int j = 0; j < posArray.length; j++) {

            trendNameList.add(backColorName((nativeCodeList.get(j)).intValue(), trendTypeBean.getHeadNum()[0]));
            trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.trend_cxcs_color)));
            trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.black)));
        }

        for (int i = 0; i < posArray.length; i++) {
            int j = i * 2 + 3;
            StringBuilder sbName;
            if ((nativeCodeList.get(j)).intValue() == 0) {
                trendNameList.add(trendTypeBean.getHeadNum()[j]);
                trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.trend_dxdshz_color)));
                trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.white)));
                sbName = new StringBuilder();
                sbName.append(nativeCodeList.get(j + 1));
                trendNameList.add(sbName.toString());
                trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.trend_cxcs_color)));
                trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.black)));
            } else {
                sbName = new StringBuilder();
                sbName.append(nativeCodeList.get(j));
                trendNameList.add(sbName.toString());
                trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.trend_cxcs_color)));
                trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.black)));
                trendNameList.add(trendTypeBean.getHeadNum()[(j + 1)]);
                trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.trend_ddan_color)));
                trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.white)));
            }
        }
        trendColorBean.setTrendBgColor(trendBgColorList);
        trendColorBean.setTrendName(trendNameList);
        trendColorBean.setTrendTextColor(trendTextColorList);
        return trendColorBean;
    }

    private TrendColorBean inColorGL(Context context, int[] posArray, List<Integer> nativeList, TrendTypeBean trendTypeBean) {
        TrendColorBean trendColorBean = new TrendColorBean();
        ArrayList<String> trendNameList = new ArrayList();
        ArrayList<Integer> trendBgColorList = new ArrayList();
        ArrayList<Integer> trendTextColorList = new ArrayList();

        for (int i = 0; i < posArray.length; i++) {
            trendNameList.add(backColorName((nativeList.get(i)).intValue(), trendTypeBean.getHeadNum()[1]));
            trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.trend_cxcs_color)));
            trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.black)));
        }

        int length = 0;
        StringBuilder sb1 = new StringBuilder();
        sb1.append(nativeList.get(length));
        trendNameList.add(sb1.toString());
        trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.trend_dxdshz_color)));
        trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.white)));

        StringBuilder sb2 = new StringBuilder();
        sb2.append(nativeList.get(length + 1));
        trendNameList.add(sb2.toString());
        trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.trend_shuang_color)));
        trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.black)));

        StringBuilder sb3 = new StringBuilder();
        sb3.append(nativeList.get(length + 2));
        trendNameList.add(sb3.toString());
        trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.trend_pjyl_color)));
        trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.white)));

        StringBuilder sb4 = new StringBuilder();
        sb4.append(nativeList.get(length + 3));
        trendNameList.add(sb4.toString());
        trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.trend_zs_color)));
        trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.black)));

        StringBuilder sb5 = new StringBuilder();
        sb5.append(nativeList.get(length + 4));
        trendNameList.add(sb5.toString());
        trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.trend_ddan_color)));
        trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.white)));

        trendColorBean.setTrendBgColor(trendBgColorList);
        trendColorBean.setTrendName(trendNameList);
        trendColorBean.setTrendTextColor(trendTextColorList);

        return trendColorBean;
    }

    private TrendColorBean inColorKl8DXDS(Context paramContext, List<Integer> cartWqBeans, TrendTypeBean trendTypeBean) {
        TrendColorBean trendColorBean = new TrendColorBean();
        ArrayList<String> trendNameList = new ArrayList();
        ArrayList<Integer> trendBgColorList = new ArrayList();
        ArrayList<Integer> trendTextColorList = new ArrayList();

        for (int i = 0; i < trendTypeBean.getHeadNum().length; i++) {
            trendNameList.add(backColorName((cartWqBeans.get(i)).intValue(), "0"));
            trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_cxcs_color)));
            trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
        }

        int j = ContextCompat.getColor(paramContext, R.color.trend_ds_color);
        int[] tempPosArray = new int[10];
        tempPosArray[0] = ContextCompat.getColor(paramContext, R.color.trend_hs_color);
        int k = ContextCompat.getColor(paramContext, R.color.trend_dxdshz_color);
        tempPosArray[1] = k;
        tempPosArray[2] = ContextCompat.getColor(paramContext, R.color.trend_shuang_color);
        tempPosArray[3] = ContextCompat.getColor(paramContext, R.color.trend_pjyl_color);
        tempPosArray[4] = ContextCompat.getColor(paramContext, R.color.trend_xd_color);
        tempPosArray[5] = ContextCompat.getColor(paramContext, R.color.trend_sx_color);
        tempPosArray[6] = ContextCompat.getColor(paramContext, R.color.trend_xd_color);
        tempPosArray[7] = ContextCompat.getColor(paramContext, R.color.trend_xs_color);
        tempPosArray[8] = ContextCompat.getColor(paramContext, R.color.trend_zdlc_color);
        tempPosArray[9] = ContextCompat.getColor(paramContext, R.color.trend_zdyl_color);
        trendBgColorList.set(0, Integer.valueOf(tempPosArray[0]));
        trendTextColorList.set(0, Integer.valueOf(j));

        for (int i = 1; i < trendTypeBean.getHeadNum().length; i++) {
            if ((cartWqBeans.get(i)).intValue() == 0) {
                trendNameList.set(i, trendTypeBean.getHeadNum()[i]);
                trendBgColorList.set(i, Integer.valueOf(tempPosArray[i]));
                trendTextColorList.set(i, Integer.valueOf(j));
            }
        }

        trendColorBean.setTrendBgColor(trendBgColorList);
        trendColorBean.setTrendName(trendNameList);
        trendColorBean.setTrendTextColor(trendTextColorList);
        return trendColorBean;
    }

    private TrendColorBean inColorKl8Five(Context paramContext, List<Integer> cartWqBeans, TrendTypeBean trendTypeBean) {
        TrendColorBean trendColorBean = new TrendColorBean();
        ArrayList trendNameList = new ArrayList();
        ArrayList trendBgColorList = new ArrayList();
        ArrayList trendTextColorList = new ArrayList();

        for (int i = 0; i < trendTypeBean.getHeadNum().length; i++) {
            trendNameList.add(backColorName((cartWqBeans.get(i)).intValue(), "0"));
            trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_cxcs_color)));
            trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_dan_color)));
        }
        int colorID = ContextCompat.getColor(paramContext, R.color.trend_ds_color);
        int[] tempPosArray = new int[6];
        tempPosArray[0] = ContextCompat.getColor(paramContext, R.color.trend_hs_color);
        int k = ContextCompat.getColor(paramContext, R.color.trend_dxdshz_color);
        tempPosArray[1] = k;
        tempPosArray[2] = ContextCompat.getColor(paramContext, R.color.trend_shuang_color);
        tempPosArray[3] = ContextCompat.getColor(paramContext, R.color.trend_zdlc_color);
        tempPosArray[4] = ContextCompat.getColor(paramContext, R.color.trend_pjyl_color);
        tempPosArray[5] = ContextCompat.getColor(paramContext, R.color.trend_xd_color);
        trendBgColorList.set(0, Integer.valueOf(tempPosArray[0]));
        trendTextColorList.set(0, Integer.valueOf(colorID));

        for (int i = 1; i < trendTypeBean.getHeadNum().length; i++) {
            if ((cartWqBeans.get(i)).intValue() == 0) {
                trendNameList.set(i, trendTypeBean.getHeadNum()[i]);
                trendBgColorList.set(i, Integer.valueOf(tempPosArray[i]));
                trendTextColorList.set(i, Integer.valueOf(colorID));
            }
        }
        trendColorBean.setTrendBgColor(trendBgColorList);
        trendColorBean.setTrendName(trendNameList);
        trendColorBean.setTrendTextColor(trendTextColorList);
        return trendColorBean;
    }

    private TrendColorBean inColorKl8JOSX(Context paramContext, List<Integer> cartWqBeans, TrendTypeBean trendTypeBean) {
        TrendColorBean trendColorBean = new TrendColorBean();
        ArrayList<String> trendNameList = new ArrayList();
        ArrayList<Integer> trendBgColorList = new ArrayList();
        ArrayList<Integer> trendTextColorList = new ArrayList();

        for (int i = 0; i < trendTypeBean.getHeadNum().length; i++) {
            trendNameList.add(backColorName((cartWqBeans.get(i)).intValue(), "0"));
            trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_cxcs_color)));
            trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
        }

        StringBuilder sbColorName = new StringBuilder();
        sbColorName.append(backColorName((cartWqBeans.get(0)).intValue(), "00"));
        sbColorName.append(":");
        sbColorName.append(backColorName(20 - (cartWqBeans.get(0)).intValue(), "00"));
        trendNameList.set(0, sbColorName.toString());
        trendBgColorList.set(0, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_cxcs_color)));
        trendTextColorList.set(0, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
        sbColorName = new StringBuilder();
        sbColorName.append(backColorName((cartWqBeans.get(4)).intValue(), "00"));
        sbColorName.append(":");
        sbColorName.append(backColorName(20 - (cartWqBeans.get(4)).intValue(), "00"));
        trendNameList.set(4, sbColorName.toString());
        trendBgColorList.set(4, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_cxcs_color)));
        trendTextColorList.set(4, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
        int k = ContextCompat.getColor(paramContext, R.color.trend_ds_color);
        int m = ContextCompat.getColor(paramContext, R.color.trend_cxcs_color);
        int n = ContextCompat.getColor(paramContext, R.color.trend_dxdshz_color);
        int i1 = ContextCompat.getColor(paramContext, R.color.trend_pjyl_color);
        int i2 = ContextCompat.getColor(paramContext, R.color.trend_shuang_color);
        int i3 = ContextCompat.getColor(paramContext, R.color.trend_cxcs_color);
        int i4 = ContextCompat.getColor(paramContext, R.color.trend_dxdshz_color);
        int i5 = ContextCompat.getColor(paramContext, R.color.trend_pjyl_color);
        int i6 = ContextCompat.getColor(paramContext, R.color.trend_shuang_color);

        for (int i = 0; i < trendTypeBean.getHeadNum().length; i++) {
            if ((trendNameList.get(i)).trim().equals("0")) {
                trendNameList.set(i, trendTypeBean.getHeadNum()[i]);
                trendBgColorList.set(i, Integer.valueOf(new int[]{m, n, i1, i2, i3, i4, i5, i6}[i]));
                trendTextColorList.set(i, Integer.valueOf(k));
            }
        }

        trendColorBean.setTrendBgColor(trendBgColorList);
        trendColorBean.setTrendName(trendNameList);
        trendColorBean.setTrendTextColor(trendTextColorList);
        return trendColorBean;
    }

    private TrendColorBean inColorLHH(int[] posArray, Context paramContext, List<Integer> cartWqBeans, TrendTypeBean trendTypeBean, int nativeCodeInt, int length) {
        TrendColorBean trendColorBean = new TrendColorBean();
        ArrayList<String> trendNameList = new ArrayList();
        ArrayList<Integer> trendBgColorList = new ArrayList();
        ArrayList<Integer> trendTextColorList = new ArrayList();
        StringBuilder sbName;

        for (int i = nativeCodeInt; i < length; i++) {
            trendNameList.add(backColorName((cartWqBeans.get(i)).intValue(), trendTypeBean.getHeadNum()[1]));
            trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_cxcs_color)));
            trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
        }

        if (nativeCodeInt < length) {
            nativeCodeInt += posArray.length;
            if ((cartWqBeans.get(nativeCodeInt)).intValue() == 0) {
                sbName = new StringBuilder();
                sbName.append(trendTypeBean.getHeadNum()[nativeCodeInt]);
                trendNameList.add(sbName.toString());
                trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_dxdshz_color)));
                trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.white)));
            } else {
                sbName = new StringBuilder();
                sbName.append(cartWqBeans.get(nativeCodeInt));
                trendNameList.add(sbName.toString());
                trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_cxcs_color)));
                trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
            }
            length = nativeCodeInt + 1;
            if ((cartWqBeans.get(length)).intValue() == 0) {
                sbName = new StringBuilder();
                sbName.append(trendTypeBean.getHeadNum()[length]);
                trendNameList.add(sbName.toString());
                trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_pjyl_color)));
                trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.white)));
            } else {
                sbName = new StringBuilder();
                sbName.append(cartWqBeans.get(length));
                trendNameList.add(sbName.toString());
                trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_cxcs_color)));
                trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
            }
            nativeCodeInt += 2;
            if ((cartWqBeans.get(nativeCodeInt)).intValue() == 0) {
                sbName = new StringBuilder();
                sbName.append(trendTypeBean.getHeadNum()[nativeCodeInt]);
                trendNameList.add(sbName.toString());
                trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_shuang_color)));
                trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
            } else {
                sbName = new StringBuilder();
                sbName.append(cartWqBeans.get(nativeCodeInt));
                trendNameList.add(sbName.toString());
                trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_cxcs_color)));
                trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
            }
            trendColorBean.setTrendBgColor(trendBgColorList);
            trendColorBean.setTrendName(trendNameList);
            trendColorBean.setTrendTextColor(trendTextColorList);

        }
        return trendColorBean;
    }

    private TrendColorBean inColorSame3DCode(int[] posArray, Context paramContext, List<Integer> cartWqBeans, TrendTypeBean trendTypeBean) {
        TrendColorBean trendColorBean = new TrendColorBean();
        ArrayList<String> trendNameList = new ArrayList();
        ArrayList<Integer> trendBgColorList = new ArrayList();
        ArrayList<Integer> trendTextColorList = new ArrayList();
        StringBuilder sb;

        for (int i = 0; i < posArray.length + 3; i++) {
            trendNameList.add(backColorName((cartWqBeans.get(i)).intValue(), trendTypeBean.getHeadNum()[1]));
            trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_cxcs_color)));
            trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
        }

        int length = posArray.length;
        trendBgColorList.set(length, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_hs_color)));
        trendTextColorList.add(length, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.white)));
        length = posArray.length + 1;
        trendBgColorList.add(length, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_dxdshz_color)));
        trendTextColorList.add(length, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.white)));
        length = posArray.length + 2;
        trendBgColorList.add(length, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_pjyl_color)));
        trendTextColorList.add(length, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
        int j = posArray.length + 3;
        length = j;
        while (length < cartWqBeans.size()) {
            sb = new StringBuilder();
            sb.append(cartWqBeans.get(j));
            trendNameList.add(sb.toString());
            trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_cxcs_color)));
            trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
            length += 1;
        }
        if ((cartWqBeans.get(j)).intValue() == 0) {
            trendNameList.set(j, trendTypeBean.getHeadNum()[j]);
            trendBgColorList.set(j, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_ddan_color)));
            trendTextColorList.set(j, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
        } else {
            length = j + 1;
            if ((cartWqBeans.get(length)).intValue() == 0) {
                trendNameList.set(length, trendTypeBean.getHeadNum()[length]);
                trendBgColorList.set(length, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_sx_color)));
                trendTextColorList.set(length, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
            } else {
                length = j + 2;
                if ((cartWqBeans.get(length)).intValue() == 0) {
                    trendNameList.set(length, trendTypeBean.getHeadNum()[length]);
                    trendBgColorList.set(length, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_shuang_color)));
                    trendTextColorList.set(length, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
                }
            }
        }
        trendColorBean.setTrendBgColor(trendBgColorList);
        trendColorBean.setTrendName(trendNameList);
        trendColorBean.setTrendTextColor(trendTextColorList);
        return trendColorBean;
    }

    private TrendColorBean inColorSameCode(int[] posArray, Context paramContext, List<Integer> cartWqBeans, TrendTypeBean trendTypeBean) {
        TrendColorBean trendColorBean = new TrendColorBean();
        ArrayList<String> trendNameList = new ArrayList();
        ArrayList<Integer> trendBgColorList = new ArrayList();
        ArrayList<Integer> trendTextColorList = new ArrayList();
        StringBuilder sbName;

        for (int i = 0; i < posArray.length; i++) {
            trendNameList.add(backColorName((cartWqBeans.get(i)).intValue(), trendTypeBean.getHeadNum()[1]));
            trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_cxcs_color)));
            trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
        }

        trendNameList.add(backColorName((cartWqBeans.get(posArray.length)).intValue(), trendTypeBean.getHeadNum()[1]));
        trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_hs_color)));
        trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.white)));

        int position = posArray.length + 1;
        int j = position;
        while (j < cartWqBeans.size()) {
            sbName = new StringBuilder();
            sbName.append(cartWqBeans.get(position));
            trendNameList.add(sbName.toString());
            trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_cxcs_color)));
            trendTextColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
            j += 1;
        }

        if ((cartWqBeans.get(position)).intValue() == 0) {
            trendNameList.set(position, trendTypeBean.getHeadNum()[position]);
            trendBgColorList.set(position, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_ddan_color)));
            trendTextColorList.set(position, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.white)));
        } else {
            int pos2 = position + 1;
            if ((cartWqBeans.get(pos2)).intValue() == 0) {
                trendNameList.set(pos2, trendTypeBean.getHeadNum()[pos2]);
                trendBgColorList.set(pos2, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_sx_color)));
                trendTextColorList.set(pos2, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
            } else {
                pos2 = position + 2;
                if ((cartWqBeans.get(pos2)).intValue() == 0) {
                    trendNameList.set(pos2, trendTypeBean.getHeadNum()[pos2]);
                    trendBgColorList.set(pos2, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_shuang_color)));
                    trendTextColorList.set(pos2, Integer.valueOf(ContextCompat.getColor(paramContext, R.color.black)));
                }
            }
        }
        trendColorBean.setTrendBgColor(trendBgColorList);
        trendColorBean.setTrendName(trendNameList);
        trendColorBean.setTrendTextColor(trendTextColorList);
        return trendColorBean;
    }

    private TrendColorBean inLHHColor(Context context, String blueMiss, TrendTypeBean trendTypeBean, int colorId, int count, int x, int y) {
        TrendColorBean trendColorBean = new TrendColorBean();
        ArrayList<String> trendNameList = new ArrayList();
        ArrayList<Integer> trendBgColorList = new ArrayList();
        ArrayList<Integer> trendTextColorList = new ArrayList();
        if (blueMiss.contains(",")) {
            String[] params = blueMiss.split(",");
            for (int j = 0; j < trendTypeBean.getHeadNum().length; j++) {

                trendNameList.add(params[j]);
                trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(context, R.color.trend_cxcs_color)));
                trendTextColorList.add(Integer.valueOf(colorId));
            }

            for (int i = 0; i < count; i++) {
                colorId = y;

                trendNameList.set(i, "");
            }
            while (colorId < x) {
                trendNameList.set(colorId, "");
                colorId += 1;
            }
            trendColorBean.setTrendBgColor(trendBgColorList);
            trendColorBean.setTrendName(trendNameList);
            trendColorBean.setTrendTextColor(trendTextColorList);
            return trendColorBean;
        }
        return trendColorBean;
    }

    private TrendColorBean inOtherColor(Context paramContext, String blueMiss, TrendTypeBean trendTypeBean, int colorId, int count) {
        TrendColorBean trendColorBean = new TrendColorBean();
        ArrayList<String> trendNameList = new ArrayList();
        ArrayList<Integer> trendBgColorList = new ArrayList();
        ArrayList<Integer> trendTextColorList = new ArrayList();
        if (blueMiss.contains(",")) {
            String[] params = blueMiss.split(",");
            int j = 0;
            for (int i = 0; i < count; i++) {
                j = count;

                trendNameList.add("");
                trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_cxcs_color)));
                trendTextColorList.add(Integer.valueOf(colorId));
            }
            while (j < trendTypeBean.getHeadNum().length) {
                if (j < params.length) {
                    trendNameList.add(params[j]);
                    trendBgColorList.add(Integer.valueOf(ContextCompat.getColor(paramContext, R.color.trend_cxcs_color)));
                    trendTextColorList.add(Integer.valueOf(colorId));
                }
                j += 1;
            }
            trendColorBean.setTrendBgColor(trendBgColorList);
            trendColorBean.setTrendName(trendNameList);
            trendColorBean.setTrendTextColor(trendTextColorList);
            return trendColorBean;
        }
        return trendColorBean;
    }

    private List<TrendModel> addLastFourTrendModles(int chooseCount, List<CartWqBean> cartWqBeanList, TrendTypeBean trendTypeBean) {
        LogUtil.logE("5.封装走势图数据 增加底部四个汇总数据", trendTypeBean.getName());
        ArrayList<TrendModel> trendModels = new ArrayList();
        ArrayList<CartWqBean> cartWqBeans = new ArrayList();
        if (cartWqBeanList.size() == 0) {
            return trendModels;
        }
        if (cartWqBeanList.size() < chooseCount) {
            chooseCount = cartWqBeanList.size();
        }

        for (int i = 0; i < chooseCount; i++) {
            cartWqBeans.add(cartWqBeanList.get(i));
        }

        TrendModel showTimesBean = new TrendModel();
        showTimesBean.setIssueNo("出现次数");
        TrendModel maxMissBean = new TrendModel();
        maxMissBean.setIssueNo("最大遗漏");
        TrendModel averageMissBean = new TrendModel();
        averageMissBean.setIssueNo("平均遗漏");
        TrendModel maxCountBean = new TrendModel();
        maxCountBean.setIssueNo("最大连出");

        int[] nums = backNum(trendTypeBean);
        int[] maxMisses = backNum(trendTypeBean);
        int[] showTimes = backNum(trendTypeBean);
        int[] maxCounts = backNum(trendTypeBean);

        for (int j = 0; j < cartWqBeans.size(); j++) {
            List<Integer> nativeCodeInt = (cartWqBeans.get(j)).getNativeCodeInt();
            for (int i = 0; i < nativeCodeInt.size(); i++) {
                if (((nativeCodeInt).get(i)).intValue() == 0) {
                    showTimes[i] += 1;
                    nums[i] += 1;
                } else {
                    nums[i] = 0;
                }
                if (((nativeCodeInt).get(i)).intValue() > maxMisses[i]) {
                    maxMisses[i] = ((nativeCodeInt).get(i)).intValue();
                }
                if (nums[i] > maxCounts[i]) {
                    maxCounts[i] = nums[i];
                }
            }
        }

        StringBuilder sbShowTimes = new StringBuilder();
        StringBuilder sbMaxCount = new StringBuilder();
        StringBuilder sbMaxMiss = new StringBuilder();
        StringBuilder sbAverageMiss = new StringBuilder();


        for (int k = 0; k < showTimes.length; k++) {
            int averageMissNum = cartWqBeans.size() / (showTimes[k] + 1);
            if (k == showTimes.length - 1) {
                sbShowTimes.append(showTimes[k]);
                sbMaxCount.append(maxCounts[k]);
                sbMaxMiss.append(maxMisses[k]);
                sbAverageMiss.append(averageMissNum);
            } else {
                sbShowTimes.append(showTimes[k]);
                sbShowTimes.append(",");
                sbMaxCount.append(maxCounts[k]);
                sbMaxCount.append(",");
                sbMaxMiss.append(maxMisses[k]);
                sbMaxMiss.append(",");
                sbAverageMiss.append(averageMissNum);
                sbAverageMiss.append(",");
            }
        }

        showTimesBean.setBlueMiss(sbShowTimes.toString());
        maxCountBean.setBlueMiss(sbMaxCount.toString());
        maxMissBean.setBlueMiss(sbMaxMiss.toString());
        averageMissBean.setBlueMiss(sbAverageMiss.toString());

//        LogUtil.logE("出现次数",sbShowTimes.toString());
//        LogUtil.logE("最大遗漏",sbMaxMiss.toString());
//        LogUtil.logE("平均遗漏",sbAverageMiss.toString());
//        LogUtil.logE("最大连出",sbMaxCount.toString());

        for (int i = 0; i < cartWqBeans.size(); i++) {
            TrendModel trendModel = new TrendModel();
            String strIssue = (cartWqBeans.get(i)).getIssue();
            String str = strIssue;
            if (strIssue.contains("-")) {
                str = strIssue.replace("-", "");
            }
            if (str.length() > 8) {
                StringBuilder sbIssue = new StringBuilder();
                sbIssue.append(str, 4, 8).append("-").append(str.substring(8));
                sbIssue.append("期");
                (trendModel).setIssueNo(sbIssue.toString());
            } else {
                StringBuilder sbIssue = new StringBuilder();
                sbIssue.append(str);
                sbIssue.append("期");
                (trendModel).setIssueNo(sbIssue.toString());
            }
            (trendModel).setWinNumber((cartWqBeans.get(i)).getCode());
            (trendModel).setBlueMiss((cartWqBeans.get(i)).getNative_code());
            (trendModel).setNativeCodeInt((cartWqBeans.get(i)).getNativeCodeInt());
            trendModels.add(trendModel);
        }

        trendModels.add(0, showTimesBean);
        trendModels.add(1, maxCountBean);
        trendModels.add(2, maxMissBean);
        trendModels.add(3, averageMissBean);

        return trendModels;
    }

    private void initMultipleTrendData(int chooseCount, List<CartWqBean> cartWqBeans, TrendView trendView, List<int[]> posList, Context context, TrendTypeBean trendTypeBean) {
        LogUtil.logE("设置走势图数据--showList==0 》没有封装颜色，直接上数据", trendTypeBean.getName());
        if (trendView == null) {
            return;
        }
        trendView.setTrendData(addLastFourTrendModles(chooseCount, cartWqBeans, trendTypeBean), context, posList, trendTypeBean);
    }

    public List<CartWqBean> changeData(List<CartWqBean> cartWqBeans, List<int[]> posList, TrendTypeBean trendTypeBean) {
        LogUtil.logE("4.二次封装数据  设置nativeCode:" + trendTypeBean.getType(), trendTypeBean.getName() + TrendTypeIdUtil.getType(trendTypeBean.getType()));
        //无数据
        switch (TrendTypeIdUtil.getType(trendTypeBean.getType())) {
            case 13:

                switch (trendTypeBean.getLotteryType()) {
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        return k18JOSXData(cartWqBeans, trendTypeBean);
                }

                return k18JOSXData(cartWqBeans, trendTypeBean);
            case 12:

                switch (trendTypeBean.getLotteryType()) {
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        return k18FiveData(cartWqBeans, trendTypeBean);
                }
                return k18FiveData(cartWqBeans, trendTypeBean);

            case 11:
                switch (trendTypeBean.getLotteryType()) {
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        return k18DXDSData(cartWqBeans, trendTypeBean);
                }
                return k18DXDSData(cartWqBeans, trendTypeBean);
            case 10:
                switch (trendTypeBean.getLotteryType()) {
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        return dx11x5Data(cartWqBeans, posList.get(0), trendTypeBean);
                }

                return dx11x5Data(cartWqBeans, posList.get(0), trendTypeBean);
            case 9:

                switch (trendTypeBean.getLotteryType()) {
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        return same3DData(cartWqBeans, posList.get(0), trendTypeBean);
                }

                return same3DData(cartWqBeans, posList.get(0), trendTypeBean);
            case 8:
                switch (trendTypeBean.getLotteryType()) {
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        return sameData(cartWqBeans, posList.get(0), trendTypeBean);
                }
                return sameData(cartWqBeans, posList.get(0), trendTypeBean);
            case 7:
                switch (trendTypeBean.getLotteryType()) {
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        return lhhData(cartWqBeans, posList, trendTypeBean);
                }

                return lhhData(cartWqBeans, posList, trendTypeBean);
            case 6:
//                return diffData(cartWqBeans, posList.get(0), trendTypeBean);

                switch (trendTypeBean.getLotteryType()) {
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        return KuaduData(cartWqBeans, posList.get(0), trendTypeBean);
                }

                return KuaduData(cartWqBeans, posList.get(0), trendTypeBean);
            case 5:
                switch (trendTypeBean.getLotteryType()) {
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        return sumData(cartWqBeans, posList.get(0), trendTypeBean);
                }

                return sumData(cartWqBeans, posList.get(0), trendTypeBean);
            case 4:

                switch (trendTypeBean.getLotteryType()) {
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        return dxdsData(cartWqBeans, posList.get(0), trendTypeBean);
                }

                return dxdsData(cartWqBeans, posList.get(0), trendTypeBean);
            case 3:

                switch (trendTypeBean.getLotteryType()) {
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        return dsData(cartWqBeans, posList.get(0), trendTypeBean);
                }

//                return dsData(cartWqBeans, posList.get(0), trendTypeBean);
                return dsData(cartWqBeans, posList.get(0), trendTypeBean);
            case 2:
                switch (trendTypeBean.getLotteryType()) {
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        return dxData(cartWqBeans, posList.get(0), trendTypeBean);
                }
                return dxData(cartWqBeans, posList.get(0), trendTypeBean);
            case 1:
                switch (trendTypeBean.getLotteryType()) {
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                    case TrendTypeIdUtil.TYPE_PK10:
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                        return multipleData(cartWqBeans, posList.get(0), trendTypeBean);
                }
                return multipleData(cartWqBeans, posList.get(0), trendTypeBean);
            case 0:
                switch (trendTypeBean.getLotteryType()) {

//                    case TrendTypeIdUtil.TYPE_KUAISAN:
//                        return singleData(cartWqBeans, posList, trendTypeBean);
                    case TrendTypeIdUtil.TYPE_11X5:
                    case TrendTypeIdUtil.TYPE_PK10:
                        return getNativeCodeFor11X5(cartWqBeans, posList, trendTypeBean);
                    case TrendTypeIdUtil.TYPE_SSC:
                    case TrendTypeIdUtil.TYPE_3D:
                    case TrendTypeIdUtil.TYPE_KUAISAN:
                        return multipleData(cartWqBeans, posList.get(0), trendTypeBean);
                }
                return multipleData(cartWqBeans, posList.get(0), trendTypeBean);
            default:
                return singleData(cartWqBeans, posList, trendTypeBean);
        }
    }

    private List<CartWqBean> k18DXDSData(List<CartWqBean> cartWqBeans, TrendTypeBean trendTypeBean) {
        int[] posArray = backNum(trendTypeBean);
        List<Integer[]> posList = kl8BlueMiss(cartWqBeans);
        int length = posArray.length;

        for (int i = 0; i < posList.size(); i++) {
            StringBuilder sb = new StringBuilder();
            ArrayList<Integer> tempPosList = new ArrayList();
            int j = 0;

            for (int k = 0; k < (posList.get(i)).length; k++) {
                j += (posList.get(i))[k].intValue();
            }

            for (int k = 0; k < posArray.length; k++) {
                posArray[k] += 1;
            }
            posArray[0] = j;
            String str1;
            String str2;
            if (posArray[0] % 2 == 0) {
                str2 = "双";
                posArray[2] = 0;
            } else {
                str2 = "单";
                posArray[1] = 0;
            }
            if (posArray[0] == 810) {
                str1 = "和";
                posArray[5] = 0;
            } else if (posArray[0] > 810) {
                posArray[3] = 0;
                str1 = "大";
            } else {
                str1 = "小";
                posArray[4] = 0;
            }
            StringBuilder sbName = new StringBuilder();
            sbName.append(str1);
            sbName.append(str2);
            str1 = sbName.toString();

            for (int n = length - 4; n < length; n++) {
                if (str1.equals(trendTypeBean.getHeadNum()[n])) {
                    posArray[n] = 0;
                }
            }

            for (int l = 0; l < posArray.length; l++) {
                if (l == posArray.length - 1) {
                    sb.append(posArray[l]);
                } else {
                    sb.append(posArray[l]);
                    sb.append(",");
                }
                tempPosList.add(Integer.valueOf(posArray[l]));
            }
//            LogUtil.logE("k18DXDSData", "nativeCode:" + sb.toString());
            (cartWqBeans.get(i)).setNative_code(sb.toString());
            (cartWqBeans.get(i)).setNativeCodeInt(tempPosList);
        }
        return cartWqBeans;
    }

    private List<CartWqBean> k18FiveData(List<CartWqBean> cartWqBeans, TrendTypeBean trendTypeBean) {
        int[] posArray = backNum(trendTypeBean);
        List<Integer[]> posList = kl8BlueMiss(cartWqBeans);
        int m = posArray.length;

        for (int i = 0; i < posList.size(); i++) {
            StringBuilder sbNum = new StringBuilder();
            ArrayList<Integer> tempPosList = new ArrayList();
            int t = 0;

            for (int k = 0; k < posList.get(i).length; k++) {
                t += posList.get(i)[k].intValue();
            }

            for (int k = 0; k < posArray.length; k++) {
                posArray[k] += 1;
            }

            posArray[0] = t;

            for (int j = 1; j < m; j++) {
                if (posArray[0] > new int[]{0, 210, 696, 764, 856, 924}[j]) {
                    if (posArray[0] < new int[]{0, 695, 763, 855, 923, 1410}[j]) {
                        posArray[j] = 0;
                    }
                }
            }

            for (int j = 0; j < posArray.length; j++) {
                if (j == posArray.length - 1) {
                    sbNum.append(posArray[j]);
                } else {
                    sbNum.append(posArray[j]);
                    sbNum.append(",");
                }
                tempPosList.add(Integer.valueOf(posArray[j]));
            }
//            LogUtil.logE("k18FiveData", "nativeCode:" + sbNum.toString());
            (cartWqBeans.get(i)).setNative_code(sbNum.toString());
            (cartWqBeans.get(i)).setNativeCodeInt(tempPosList);
        }
        return cartWqBeans;
    }

    private List<CartWqBean> k18JOSXData(List<CartWqBean> cartWqBeans, TrendTypeBean trendTypeBean) {
        int[] posArray = backNum(trendTypeBean);
        List<Integer[]> posList = kl8BlueMiss(cartWqBeans);

        for (int i = 0; i < posList.size(); i++) {
            StringBuilder sbNun = new StringBuilder();
            ArrayList<Integer> tempPosList = new ArrayList();
            int m = 0;
            int k = 0;
            int i1;
            for (int j = 0; m < (posList.get(i)).length; j = i1) {
                int n = k;
                if ((posList.get(i))[m].intValue() % 2 == 1) {
                    n = k + 1;
                }
                i1 = j;
                if ((posList.get(i))[m].intValue() < 41) {
                    i1 = j + 1;
                }
                m += 1;
                k = n;
            }
            m = 0;
            while (m < posArray.length) {
                posArray[m] += 1;
                m += 1;
            }
            posArray[0] = k;
            posArray[4] = i;
            if (k > 10) {
                posArray[1] = 0;
            } else if (k == 10) {
                posArray[2] = 0;
            } else {
                posArray[3] = 0;
            }
            if (i > 10) {
                posArray[5] = 0;
            } else if (i == 10) {
                posArray[6] = 0;
            } else {
                posArray[7] = 0;
            }

            for (int l = 0; l < posArray.length; l++) {
                if (l == posArray.length - 1) {
                    sbNun.append(posArray[l]);
                } else {
                    sbNun.append(posArray[l]);
                    sbNun.append(",");
                }
                tempPosList.add(Integer.valueOf(posArray[l]));
            }
//            LogUtil.logE("k18JOSXData", "nativeCode:" + sbNun.toString());
            cartWqBeans.get(i).setNative_code(sbNun.toString());
            cartWqBeans.get(i).setNativeCodeInt(tempPosList);
        }
        return cartWqBeans;
    }

    private List<Integer[]> kl8BlueMiss(List<CartWqBean> cartWqBeans) {
        ArrayList<Integer[]> posList = new ArrayList();

        for (int i = 0; i < cartWqBeans.size(); i++) {
            try {
                String[] codeArray = (cartWqBeans.get(i)).getCode().split(",");
                Integer[] posArray = new Integer[codeArray.length];

                for (int j = 0; j < codeArray.length; j++) {
                    posArray[j] = Integer.valueOf(Integer.parseInt(codeArray[j].trim()));
                }

                posList.add(posArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return posList;
    }

    private List<CartWqBean> lhhData(List<CartWqBean> cartWqBeans, List<int[]> posList, TrendTypeBean trendTypeBean) {
        int[] posArray = backNum(trendTypeBean);
        List<Integer[]> missList1 = dxBlueMiss(cartWqBeans, posList.get(0));
        List<Integer[]> numMiss2 = dxBlueMiss(cartWqBeans, posList.get(1));

        if (missList1.size() <= 0 || numMiss2.size() <= 0) {
            return null;
        }

        int length = (missList1.get(0)).length;

        for (int i = 0; i < missList1.size(); i++) {
            StringBuilder sb = new StringBuilder();
            ArrayList<Integer> tempIntList = new ArrayList();
            int j = (missList1.get(i))[0].intValue();
            int m = (missList1.get(i))[1].intValue();
            posArray[(length - 2)] = j;
            posArray[(length - 1)] = m;
            if (j > m) {
                posArray[length] = 0;
                j = length + 1;
                posArray[j] += 1;
                j = length + 2;
                posArray[j] += 1;
            } else if (j < m) {
                posArray[length] += 1;
                j = length + 1;
                posArray[j] += 1;
                posArray[(length + 2)] = 0;
            } else {
                posArray[length] += 1;
                posArray[(length + 1)] = 0;
                j = length + 2;
                posArray[j] += 1;
            }
            j = length + 5;
            m = (numMiss2.get(i))[0].intValue();
            int n = (numMiss2.get(i))[1].intValue();
            posArray[(j - 2)] = m;
            posArray[(j - 1)] = n;
            if (m > n) {
                posArray[j] = 0;
                m = j + 1;
                posArray[m] += 1;
                j += 2;
                posArray[j] += 1;
            } else if (m < n) {
                posArray[j] += 1;
                m = j + 1;
                posArray[m] += 1;
                posArray[(j + 2)] = 0;
            } else {
                posArray[j] += 1;
                posArray[(j + 1)] = 0;
                j += 2;
                posArray[j] += 1;
            }

            for (int l = 0; l < posArray.length; l++) {
                if (l == posArray.length - 1) {
                    sb.append(posArray[l]);
                } else {
                    sb.append(posArray[l]);
                    sb.append(",");
                }
                tempIntList.add(Integer.valueOf(posArray[l]));
            }
//            LogUtil.logE("lhhData", "nativeCode:" + sb.toString());
            cartWqBeans.get(i).setNative_code(sb.toString());
            cartWqBeans.get(i).setNativeCodeInt(tempIntList);
        }
        return cartWqBeans;
    }

    private List<CartWqBean> multipleData(List<CartWqBean> cartWqBeans, int[] posArray, TrendTypeBean trendTypeBean) {
        ArrayList<Integer[]> posList = new ArrayList();
        Integer[] intArray;
        Log.e(TAG, "multipleData: " + cartWqBeans.toString());
        for (int i = 0; i < cartWqBeans.size(); i++) {
            try {
                String[] strs = (cartWqBeans.get(i)).getCode().split(",");
                ArrayList<Integer> temPosList = new ArrayList();

                for (int j = 0; j < posArray.length; j++) {
                    //temPosList.add(Integer.valueOf(Integer.parseInt(strs[2 * j].trim() + strs[2 * j + 1].trim())));

                    if (strs.length == 10 || strs.length == 20) {

                        switch (TrendTypeIdUtil.getType(trendTypeBean.getType())) {
                            case 1:
                                if (trendTypeBean.getLotteryType() == TrendTypeIdUtil.TYPE_PK10) {
                                    try {
                                        temPosList.add(Integer.parseInt(strs[2 * posArray[j]].trim() + strs[2 * posArray[j] + 1].trim()));
                                    } catch (NumberFormatException e) {
                                        BuglyLog.e(TAG, "走势图code字符串转数字错误: " + e.getMessage());
                                    }

                                } else {
                                    try {
                                        temPosList.add(Integer.parseInt(strs[2 * j].trim() + strs[2 * j + 1].trim()));
                                    } catch (NumberFormatException e) {
                                        BuglyLog.e(TAG, "走势图code字符串转数字错误: " + e.getMessage());
                                    }

                                }
                                break;
                            default:
                                try {
                                    temPosList.add(Integer.parseInt(strs[2 * j].trim() + strs[2 * j + 1].trim()));
                                } catch (NumberFormatException e) {
                                    BuglyLog.e(TAG, "走势图code字符串转数字错误: " + e.getMessage());
                                }

                                break;
                        }

                    } else {

                        try {
                            temPosList.add(Integer.parseInt(strs[posArray[j]].trim()));
                        } catch (NumberFormatException e) {
                            BuglyLog.e(TAG, "走势图code字符串转数字错误: " + e.getMessage());
                        }
                    }

                }

                Collections.sort(temPosList);
                intArray = new Integer[temPosList.size()];

                for (int j = 0; j < temPosList.size(); j++) {
                    intArray[j] = (temPosList.get(j));
                }

                posList.add(intArray);
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        }

        int[] numArray = backNum(trendTypeBean);

        for (int i = 0; i < posList.size(); i++) {
            StringBuilder sb = new StringBuilder();
            List<Integer> tempPosList = new ArrayList();
            int k = 0;

            for (int j = 0; j < numArray.length; j++) {
                if ((posList.get(i))[k].intValue() == Integer.parseInt(trendTypeBean.getHeadNum()[j].trim())) {
                    numArray[j] = 0;
                    k = backPos(k, posList.get(i));
                } else {
                    numArray[j] += 1;
                }
                if (j == numArray.length - 1) {
                    sb.append(numArray[j]);
                } else {
                    sb.append(numArray[j]);
                    sb.append(",");
                }
                (tempPosList).add(Integer.valueOf(numArray[j]));
            }

//            LogUtil.logE("multipleData", "nativeCode:" + sb.toString());
            cartWqBeans.get(i).setNative_code(sb.toString());
            cartWqBeans.get(i).setNativeCodeInt(tempPosList);
        }

        return cartWqBeans;
    }


    private List<CartWqBean> getNativeCodeFor11X5(List<CartWqBean> cartWqBeans, List<int[]> posArrayList, TrendTypeBean trendTypeBean) {
        ArrayList<String[]> posList = new ArrayList();
        String[] tempArray;

        for (int i = 0; i < cartWqBeans.size(); i++) {
            try {
                String[] strs = (cartWqBeans.get(i)).getCode().split(",");
                ArrayList<String> temPosList = new ArrayList();

                if (posArrayList.size() <= 1) {
                    int k = 0;
                    while (k < posArrayList.get(0).length) {
                        temPosList.add(strs[posArrayList.get(0)[k]].trim() + strs[posArrayList.get(0)[k] + 1].trim());
                        LogUtil.logE("", "关心数据:" + strs[posArrayList.get(0)[k]].trim() + strs[posArrayList.get(0)[k] + 1].trim());
                        k = k + 2;
                    }

                    Collections.sort(temPosList);
                    tempArray = new String[temPosList.size()];


                    for (int j = 0; j < temPosList.size(); j++) {
                        tempArray[j] = (temPosList.get(j));
                    }

                    posList.add(tempArray);

                } else {
                    int k = 0;
                    if (posArrayList.get(1)[0] == 2) {//单号——中位 情况
                        while (k < 10) {
                            temPosList.add(strs[k].trim() + strs[k + 1].trim());
                            LogUtil.logE("", "关心数据:" + strs[k].trim() + strs[k + 1].trim());
                            k = k + 2;
                        }

                        Collections.sort(temPosList);
                        tempArray = new String[1];

                        tempArray[0] = (temPosList.get(2));
                        posList.add(tempArray);
                    }
                }

            } catch (Exception localException) {
                localException.printStackTrace();
            }
        }

        int[] numArray = backNum(trendTypeBean);

        for (int i = 0; i < posList.size(); i++) {
            StringBuilder sb = new StringBuilder();
            List<Integer> tempPosList = new ArrayList();
            int k = 0;

            for (int j = 0; j < numArray.length; j++) {
                if (posList.get(i)[k].equals(trendTypeBean.getHeadNum()[j].trim())) {
                    numArray[j] = 0;
                    k = backPos(k, posList.get(i));
                } else {
                    numArray[j] += 1;
                }
                if (j == numArray.length - 1) {
                    sb.append(numArray[j]);
                } else {
                    sb.append(numArray[j]);
                    sb.append(",");
                }
                tempPosList.add(Integer.valueOf(numArray[j]));
            }

//            LogUtil.logE("", "关心数据:" + sb.toString());
            cartWqBeans.get(i).setNative_code(sb.toString());
            cartWqBeans.get(i).setNativeCodeInt(tempPosList);
        }

        return cartWqBeans;
    }

    private List<CartWqBean> same3DData(List<CartWqBean> cartWqBeans, int[] posArray, TrendTypeBean trendTypeBean) {
        int[] posList = backNum(trendTypeBean);
        List<Integer[]> numMiss = dxBlueMiss(cartWqBeans, posArray);

        if (numMiss.size() <= 0) {
            return null;
        }

        int length = (numMiss.get(0)).length;
        int i = 0;

        for (int k = 0; k < numMiss.size(); k++) {
            StringBuilder sbNums = new StringBuilder();
            ArrayList<Integer> tempPosList = new ArrayList();
            int j = 0;
            int i2 = 0;
            int m = 0;
            int i1;
            for (i = 0; j < (numMiss.get(k)).length; i = i1) {
                posList[j] = (numMiss.get(k))[j].intValue();
                i2 += posList[j];
                int n = m;
                if (j < 2) {
                    n = m + posList[j];
                }
                i1 = i;
                if (j >= (numMiss.get(k)).length - 2) {
                    i1 = i + posList[j];
                }
                j += 1;
                m = n;
            }
            posList[length] = i2;
            posList[(length + 1)] = m;
            posList[(length + 2)] = i;
            if (posList[0] == posList[1]) {
                j = 1;
            } else {
                j = 0;
            }
            i = j;
            if (posList[1] == posList[2]) {
                i = j + 1;
            }
            j = i;
            if (posList[0] == posList[2]) {
                j = i + 1;
            }
            i = posList.length - 3;
            if (j == 3) {
                posList[i] = 0;
                j = i + 1;
                posList[j] += 1;
                i += 2;
                posList[i] += 1;
            } else if (j == 0) {
                posList[i] += 1;
                posList[(i + 1)] = 0;
                i += 2;
                posList[i] += 1;
            } else {
                posList[i] += 1;
                j = i + 1;
                posList[j] += 1;
                posList[(i + 2)] = 0;
            }

            for (int l = 0; l < posList.length; l++) {
                if (l == posList.length - 1) {
                    sbNums.append(posList[l]);
                } else {
                    sbNums.append(posList[l]);
                    sbNums.append(",");
                }
                tempPosList.add(Integer.valueOf(posList[l]));
            }
//            LogUtil.logE("same3DData", "nativeCode:" + sbNums.toString());
            (cartWqBeans.get(k)).setNative_code(sbNums.toString());
            (cartWqBeans.get(k)).setNativeCodeInt(tempPosList);
        }


        return cartWqBeans;
    }

    private List<CartWqBean> sameData(List<CartWqBean> cartWqBeans, int[] posArray, TrendTypeBean trendTypeBean) {
        int[] posList = backNum(trendTypeBean);


        List<Integer[]> numMiss = dxBlueMiss(cartWqBeans, posArray);

        if (numMiss.size() <= 0) {
            return null;
        }

        int length = (numMiss.get(0)).length;

        for (int k = 0; k < numMiss.size(); k++) {
            StringBuilder sb = new StringBuilder();
            ArrayList<Integer> tempPosList = new ArrayList();
            int i = 0;
            int j = 0;
            while (i < (numMiss.get(k)).length) {
                posList[i] = (numMiss.get(k))[i].intValue();
                j += posList[i];
                i += 1;
            }
            posList[length] = j;
            if (posList[0] == posList[1]) {
                j = 1;
            } else {
                j = 0;
            }
            i = j;
            if (posList[1] == posList[2]) {
                i = j + 1;
            }
            j = i;
            if (posList[0] == posList[2]) {
                j = i + 1;
            }
            if (j == 3) {
                posList[4] = 0;
                posList[5] += 1;
                posList[6] += 1;
            } else if (j == 0) {
                posList[4] += 1;
                posList[5] = 0;
                posList[6] += 1;
            } else {
                posList[4] += 1;
                posList[5] += 1;
                posList[6] = 0;
            }

            for (int l = 0; l < posList.length; l++) {
                if (l == posList.length - 1) {
                    sb.append(posList[l]);
                } else {
                    sb.append(posList[l]);
                    sb.append(",");
                }

                tempPosList.add(Integer.valueOf(posList[l]));
            }
//            LogUtil.logE("sameData", "nativeCode:" + sb.toString());
            (cartWqBeans.get(k)).setNative_code(sb.toString());
            (cartWqBeans.get(k)).setNativeCodeInt(tempPosList);
        }
        return cartWqBeans;
    }

    private List<CartWqBean> singleData(List<CartWqBean> cartList, List<int[]> posList, TrendTypeBean trendTypeBean) {
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
//                LogUtil.logE("singleData", "nativeCode:" + sbName.toString());
                cartList.get(l).setNative_code(sbName.toString());
                cartList.get(l).setNativeCodeInt(nativeList);
            }

            return cartList;
        }

        return cartList;

    }

    private List<CartWqBean> sumData(List<CartWqBean> cartWqBeans, int[] posArray, TrendTypeBean trendTypeBean) {
        int[] tempPosArray = backNum(trendTypeBean);
        List<Integer[]> posList = dxBlueMiss(cartWqBeans, posArray);

        if (posList.size() <= 0) {
            return null;
        }

        int length = (posList.get(0)).length;

        for (int i = 0; i < posList.size(); i++) {
            StringBuilder sb = new StringBuilder();
            ArrayList<Integer> tempPosList = new ArrayList();

            for (int j = 0; j < posList.get(i).length; j++) {
                tempPosArray[j] = (posList.get(i))[j].intValue();
            }

            tempPosArray[length] = (tempPosArray[0] + tempPosArray[1]);
            tempPosArray[(length + 1)] = (tempPosArray[0] + tempPosArray[1] + tempPosArray[2]);
            tempPosArray[(length + 2)] = (tempPosArray[1] + tempPosArray[2] + tempPosArray[3]);
            tempPosArray[(length + 3)] = (tempPosArray[2] + tempPosArray[3] + tempPosArray[4]);
            tempPosArray[(length + 4)] = (tempPosArray[3] + tempPosArray[4]);

            for (int j = 0; j < tempPosArray.length; j++) {
                if (j == tempPosArray.length - 1) {
                    sb.append(tempPosArray[j]);
                } else {
                    sb.append(tempPosArray[j]);
                    sb.append(",");
                }
                tempPosList.add(Integer.valueOf(tempPosArray[j]));
            }
//            LogUtil.logE("sumData", "nativeCode:" + sb.toString());
            cartWqBeans.get(i).setNative_code(sb.toString());
            cartWqBeans.get(i).setNativeCodeInt(tempPosList);
        }
        return cartWqBeans;
    }

    //跨度
    private List<CartWqBean> KuaduData(List<CartWqBean> cartWqBeans, int[] posArray, TrendTypeBean trendTypeBean) {
        int[] tempPosArray = backNum(trendTypeBean);
        List<Integer[]> posList = dxBlueMiss(cartWqBeans, posArray);

        if (posList.size() <= 0) {
            return null;
        }

        int length = (posList.get(0)).length;

        for (int i = 0; i < posList.size(); i++) {
            StringBuilder sb = new StringBuilder();
            ArrayList<Integer> tempPosList = new ArrayList();

            for (int j = 0; j < posList.get(i).length; j++) {
                tempPosArray[j] = (posList.get(i))[j].intValue();
            }

            tempPosArray[length] = Math.abs(tempPosArray[0] - tempPosArray[1]);

            int temp1 = Math.abs(tempPosArray[0] - tempPosArray[1]);
            int temp2 = Math.abs(tempPosArray[1] - tempPosArray[2]);
            int temp3 = Math.abs(tempPosArray[2] - tempPosArray[0]);
            tempPosArray[(length + 1)] = getmax(temp1, temp2, temp3);

            int temp4 = Math.abs(tempPosArray[2] - tempPosArray[3]);
            int temp5 = Math.abs(tempPosArray[3] - tempPosArray[1]);
            tempPosArray[(length + 2)] = getmax(temp2, temp4, temp5);

            int temp6 = Math.abs(tempPosArray[3] - tempPosArray[4]);
            int temp7 = Math.abs(tempPosArray[4] - tempPosArray[2]);
            tempPosArray[(length + 3)] = getmax(temp4, temp6, temp7);

            tempPosArray[(length + 4)] = Math.abs(tempPosArray[4] - tempPosArray[3]);

            for (int j = 0; j < tempPosArray.length; j++) {
                if (j == tempPosArray.length - 1) {
                    sb.append(tempPosArray[j]);
                } else {
                    sb.append(tempPosArray[j]);
                    sb.append(",");
                }
                tempPosList.add(Integer.valueOf(tempPosArray[j]));
            }
//            LogUtil.logE("sumData", "nativeCode:" + sb.toString());
            cartWqBeans.get(i).setNative_code(sb.toString());
            cartWqBeans.get(i).setNativeCodeInt(tempPosList);
        }
        return cartWqBeans;
    }

    public static int getmax(int a, int b, int c) {
        int max;
        max = a;
        max = (b > max) ? b : max;
        max = (c > max) ? c : max;
        return max;
    }

    public List<CartWqBean> reverseDataList(List<CartWqBean> cartWqBeans) {
        ArrayList<CartWqBean> cartWqBeanList = new ArrayList();
        for (int i = 0; i < cartWqBeans.size(); i++) {
            cartWqBeanList.add(cartWqBeans.get(i));
        }
        return cartWqBeanList;
    }

}

