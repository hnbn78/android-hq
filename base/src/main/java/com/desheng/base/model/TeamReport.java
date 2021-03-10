package com.desheng.base.model;

/**
 * Created by lee on 2018/5/1.
 */

public class TeamReport {


    /**'
     * {
     "userName":"cslee01",
     "userType":1,
     "confirmAmount":2,    投注额
     "txsscConfirmAmount":0,  腾讯投注额
     "awardAmount":1.956,   奖金
     "pointAmount":0,       返点
     "profitAmount":0.044,  总盈亏
     "activityAmount":0,    活动
     "depositAmount":0,     充值
     "withdrawAmount":0,    取值、提现
     "feeAmount":0,
     "orderBonusAmount":0,  单单分红总额
     "date":null,
     "teamBalance":null,    团队余额
     "balance":"44.5260"    余额
     }
     */
    public String userName;
    public int userType;
    public double confirmAmount;
    public double txsscConfirmAmount;
    public double awardAmount;
    public double pointAmount;
    public double profitAmount;
    public double activityAmount;
    public double depositAmount;
    public double withdrawAmount;
    public double feeAmount;
    public double orderBonusAmount;
    public String date;
    public double teamBalance;
    public String balance;
    public double bonusAmount;
    public double orderzjjjAmount;
    public double orderDdfhAmount;

}
