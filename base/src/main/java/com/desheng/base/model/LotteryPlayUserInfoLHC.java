package com.desheng.base.model;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lee on 2018/3/15.
 */

public class LotteryPlayUserInfoLHC {
    
    private String Success;
    private Object Msg;
    private ExtendObjBean ExtendObj;
    private Object OK;
    private String PageCount;
    private ObjBean Obj;
    
    public String getSuccess() {
        return Success;
    }
    
    public void setSuccess(String Success) {
        this.Success = Success;
    }
    
    public Object getMsg() {
        return Msg;
    }
    
    public void setMsg(Object Msg) {
        this.Msg = Msg;
    }
    
    public ExtendObjBean getExtendObj() {
        return ExtendObj;
    }
    
    public void setExtendObj(ExtendObjBean ExtendObj) {
        this.ExtendObj = ExtendObj;
    }
    
    public Object getOK() {
        return OK;
    }
    
    public void setOK(Object OK) {
        this.OK = OK;
    }
    
    public String getPageCount() {
        return PageCount;
    }
    
    public void setPageCount(String PageCount) {
        this.PageCount = PageCount;
    }
    
    public ObjBean getObj() {
        return Obj;
    }
    
    public void setObj(ObjBean Obj) {
        this.Obj = Obj;
    }
    
    public static class ExtendObjBean {
        /**
         * IsLogin : 1
         */
        
        private String IsLogin;
        
        public String getIsLogin() {
            return IsLogin;
        }
        
        public void setIsLogin(String IsLogin) {
            this.IsLogin = IsLogin;
        }
    }
    
    public static class ObjBean {
       
        private String IsLogin;
        private String CurrentPeriod;
        private String CloseCount;
        private String OpenCount;
        private String PrePeriodNumber;
        private String PreResult;
        private String NotCountSum;
        private String Balance;
        private HashMap<String, String> Lines;
        private ZongchuYilouBean ZongchuYilou;
        private List<List<String>> ChangLong;
        private List<LuZhuBean> LuZhu;
        
        public String getIsLogin() {
            return IsLogin;
        }
        
        public void setIsLogin(String IsLogin) {
            this.IsLogin = IsLogin;
        }
        
        public String getCurrentPeriod() {
            return CurrentPeriod;
        }
        
        public void setCurrentPeriod(String CurrentPeriod) {
            this.CurrentPeriod = CurrentPeriod;
        }
        
        public String getCloseCount() {
            return CloseCount;
        }
        
        public void setCloseCount(String CloseCount) {
            this.CloseCount = CloseCount;
        }
        
        public String getOpenCount() {
            return OpenCount;
        }
        
        public void setOpenCount(String OpenCount) {
            this.OpenCount = OpenCount;
        }
        
        public String getPrePeriodNumber() {
            return PrePeriodNumber;
        }
        
        public void setPrePeriodNumber(String PrePeriodNumber) {
            this.PrePeriodNumber = PrePeriodNumber;
        }
        
        public String getPreResult() {
            return PreResult;
        }
        
        public void setPreResult(String PreResult) {
            this.PreResult = PreResult;
        }
        
        public String getNotCountSum() {
            return NotCountSum;
        }
        
        public void setNotCountSum(String NotCountSum) {
            this.NotCountSum = NotCountSum;
        }
        
        public String getBalance() {
            return Balance;
        }
        
        public void setBalance(String Balance) {
            this.Balance = Balance;
        }
    
        public HashMap<String, String> getLines() {
            return Lines;
        }
    
        public void setLines(HashMap<String, String> lines) {
            Lines = lines;
        }
    
        public ZongchuYilouBean getZongchuYilou() {
            return ZongchuYilou;
        }
        
        public void setZongchuYilou(ZongchuYilouBean ZongchuYilou) {
            this.ZongchuYilou = ZongchuYilou;
        }
        
        public List<List<String>> getChangLong() {
            return ChangLong;
        }
        
        public void setChangLong(List<List<String>> ChangLong) {
            this.ChangLong = ChangLong;
        }
        
        public List<LuZhuBean> getLuZhu() {
            return LuZhu;
        }
        
        public void setLuZhu(List<LuZhuBean> LuZhu) {
            this.LuZhu = LuZhu;
        }
        
        public static class ZongchuYilouBean {
            
            private Object miss;
            private HitBean hit;
            
            public Object getMiss() {
                return miss;
            }
            
            public void setMiss(Object miss) {
                this.miss = miss;
            }
            
            public HitBean getHit() {
                return hit;
            }
            
            public void setHit(HitBean hit) {
                this.hit = hit;
            }
            
            public static class HitBean {
                private List<Integer> n1;
                private List<Integer> n2;
                private List<Integer> n3;
                private List<Integer> n4;
                private List<Integer> n5;
                
                public List<Integer> getN1() {
                    return n1;
                }
                
                public void setN1(List<Integer> n1) {
                    this.n1 = n1;
                }
                
                public List<Integer> getN2() {
                    return n2;
                }
                
                public void setN2(List<Integer> n2) {
                    this.n2 = n2;
                }
                
                public List<Integer> getN3() {
                    return n3;
                }
                
                public void setN3(List<Integer> n3) {
                    this.n3 = n3;
                }
                
                public List<Integer> getN4() {
                    return n4;
                }
                
                public void setN4(List<Integer> n4) {
                    this.n4 = n4;
                }
                
                public List<Integer> getN5() {
                    return n5;
                }
                
                public void setN5(List<Integer> n5) {
                    this.n5 = n5;
                }
            }
        }
        
        public static class LuZhuBean {
            
            private String c;
            private String n;
            private int p;
            
            public String getC() {
                return c;
            }
            
            public void setC(String c) {
                this.c = c;
            }
            
            public String getN() {
                return n;
            }
            
            public void setN(String n) {
                this.n = n;
            }
            
            public int getP() {
                return p;
            }
            
            public void setP(int p) {
                this.p = p;
            }
        }
    }
}
