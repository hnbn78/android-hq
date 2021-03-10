package com.desheng.base.model;

import java.util.List;

public class LotteryPlayLHCCategory {

    /**
     * titleName : 特码
     * data : [{"showName":"特码","name":"特码","lotteryCode":"lhc_tema"}]
     */

    private String titleName;
    private List<DataBean> data;
    private boolean isChecked;

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public static class DataBean {
        /**
         * showName : 特码
         * name : 特码
         * lotteryCode : lhc_tema
         */

        private String showName;
        private String name;
        private String lotteryCode;
        private boolean isChecked;

        public String getShowName() {
            return showName;
        }

        public void setShowName(String showName) {
            this.showName = showName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLotteryCode() {
            return lotteryCode;
        }

        public void setLotteryCode(String lotteryCode) {
            this.lotteryCode = lotteryCode;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "showName='" + showName + '\'' +
                    ", name='" + name + '\'' +
                    ", lotteryCode='" + lotteryCode + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LotteryPlayLHCCategory{" +
                "titleName='" + titleName + '\'' +
                ", data=" + data +
                '}';
    }
}
