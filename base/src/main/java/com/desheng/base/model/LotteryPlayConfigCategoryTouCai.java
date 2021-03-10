package com.desheng.base.model;

import java.util.List;

public class LotteryPlayConfigCategoryTouCai {

    /**
     * titleName : 五星
     * cat : [{"titleName":"直选","data":[{"showName":"五星直选复式","name":"直选复式","lotteryCode":"wxzhixfs"},{"showName":"五星直选单式","name":"直选单式","lotteryCode":"wxzhixds"},{"showName":"五星直选组合","name":"直选组合","lotteryCode":"wxzhixzh"}]},{"titleName":"组选","cat":[{"showName":"五星组选120","name":"组选120","lotteryCode":"wxzux120"},{"showName":"五星组选60","name":"组选60","lotteryCode":"wxzux60"},{"showName":"五星组选30","name":"组选30","lotteryCode":"wxzux30"},{"showName":"五星组选20","lotteryCode":"wxzux20","name":"组选20"},{"showName":"五星组选10","lotteryCode":"wxzux10","name":"组选10"},{"showName":"五星组选5","lotteryCode":"wxzux5","name":"组选5"}]},{"titleName":"其他","data":[{"showName":"五星大小单双","name":"总和大小单双","lotteryCode":"ssc5x_sumdxds"}]}]
     */

    private String titleName;
    private boolean catGroup = false;
    private List<CatBean> cat;
    private boolean allInOne;

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public List<CatBean> getCat() {
        return cat;
    }

    public void setCat(List<CatBean> cat) {
        this.cat = cat;
    }

    public boolean isAllInOne() {
        return allInOne;
    }

    public void setAllInOne(boolean allInOne) {
        this.allInOne = allInOne;
    }

    public boolean isCatGroup() {
        return catGroup;
    }

    public void setCatGroup(boolean catGroup) {
        this.catGroup = catGroup;
    }

    public static class CatBean {
        /**
         * titleName : 直选
         * data : [{"showName":"五星直选复式","name":"直选复式","lotteryCode":"wxzhixfs"},{"showName":"五星直选单式","name":"直选单式","lotteryCode":"wxzhixds"},{"showName":"五星直选组合","name":"直选组合","lotteryCode":"wxzhixzh"}]
         */

        private String titleName;
        private List<DataBean> data;

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

        public static class DataBean {
            /**
             * showName : 五星直选复式
             * name : 直选复式
             * lotteryCode : wxzhixfs
             */

            private String showName;
            private String name;
            private String lotteryCode;

            @Override
            public String toString() {
                return "DataBean{" +
                        "showName='" + showName + '\'' +
                        ", name='" + name + '\'' +
                        ", lotteryCode='" + lotteryCode + '\'' +
                        '}';
            }

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
        }
    }
}
