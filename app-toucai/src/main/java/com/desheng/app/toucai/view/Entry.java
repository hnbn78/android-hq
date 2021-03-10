package com.desheng.app.toucai.view;

/**
 * Created by o on 2018/4/7.
 */

public class Entry {
        private float mVal;
        private int mXIndex;
        private Object mData;

        public Entry(float val, int xIndex) {
            this.mVal = 0.0F;
            this.mXIndex = 0;
            this.mData = null;
            this.mVal = val;
            this.mXIndex = xIndex;
        }

        public Entry(float val, int xIndex, Object data) {
            this(val, xIndex);
            this.mData = data;
        }

        public int getXIndex() {
            return this.mXIndex;
        }

        public void setXIndex(int x) {
            this.mXIndex = x;
        }

        public float getVal() {
            return this.mVal;
        }

        public void setVal(float val) {
            this.mVal = val;
        }

        public Object getData() {
            return this.mData;
        }

        public void setData(Object data) {
            this.mData = data;
        }

        public Entry copy() {
            Entry e = new Entry(this.mVal, this.mXIndex, this.mData);
            return e;
        }

        public boolean equalTo(Entry e) {
            return e == null?false:(e.mData != this.mData?false:(e.mXIndex != this.mXIndex?false:Math.abs(e.mVal - this.mVal) <= 1.0E-5F));
        }

        public String toString() {
            return "Entry, xIndex: " + this.mXIndex + " val (sum): " + this.getVal();
        }
}

