var DataFormat = function() {

	var formatAmount = function(amount) {
		if (amount < 1) {
			return amount.toFixed(5);
		}
		if (amount < 100) {
			return amount.toFixed(3);
		}
		return amount.toFixed(3);
	}

	var formatAccountType = function(code) {
		if (code == 1) {
			return '彩票账户';
		}
		if (code == 2) {
			return '百家乐账户';
		}
	}

	var greeting = function() {
		var hour = moment().hour();
		if(hour >= 6 && hour < 11) {
			return '早上好';
		}
		if(hour >= 11 && hour < 13) {
			return '中午好';
		}
		if(hour >= 13 && hour < 19) {
			return '下午好';
		}
		if(hour >= 19 || hour < 6) {
			return '晚上好';
		}
	}

	var formatUserType = function(type) {
		if(type == 0) {
			return '玩家';
		}
		if(type == 1) {
			return '代理';
		}
	}

	var formatUserVipLevel = function(level) {
		if(level == 0) {
			return '普通会员';
		}
		if(level == 1) {
			return '青铜 VIP';
		}
		if(level == 2) {
			return '紫晶 VIP';
		}
		if(level == 3) {
			return '白金 VIP';
		}
		if(level == 4) {
			return '黄金 VIP';
		}
		if(level == 5) {
			return '钻石 VIP';
		}
		if(level == 6) {
			return '至尊 VIP';
		}
	}

	var formatLevelUsers = function(thisUser, list) {
		$.each(list, function(i, val) {
			thisUser += ' &gt; ' + val;
		});
		return thisUser;
	}

	var formatUserOnlineStatus = function(status) {
		if(status == 0) {
			return '离线';
		}
		if(status == 1) {
			return '在线';
		}
	}

	var formatUserPlanLevel = function(level) {
		if(level == 0) {
			return '菜鸟';
		}
		if(level == 1) {
			return '学徒';
		}
		if(level == 2) {
			return '出师';
		}
		if(level == 3) {
			return '操盘';
		}
		if(level == 4) {
			return '大师';
		}
		if(level == 5) {
			return '宗师';
		}
		if(level == 6) {
			return '大神';
		}
	}

	var formatUserCardStatus = function(status) {
		if(status == 0) {
			return '正常';
		}
		if(status == -1) {
			return '资料无效';
		}
		if(status == -2) {
			return '已锁定';
		}
	}

	var formatUserRechargeType = function(type) {
		if(type == 0) {
			return '网银充值';
		}
		if(type == 1) {
			return '转账汇款';
		}
		if(type == 3) {
			return '系统充值';
		}
		if(type == 4) {
			return '微信充值';
		}
	}

	var formatUserRechargeStatus = function(type) {
		if(type == 0) {
			return '成功';
		}
		if(type == 6 || type == 7) {
			return '失败';
		}
		if(type != 0 && type != 6 && type != 7) {
			return '待处理';
		}
	}

	var formatUserStatus = function(type) {
		if(!type) {
			return '会员';
		}
		if(type == 0) {
			return '会员';
		}
		return '代理';
	}

	var formatUserWithdrawalsStatus = function(status) {
		/*if(status == 0) {
			return '待处理';
		}
		if(status == 1) {
			return '已完成';
		}
		if(status == -1) {
			return '已拒绝';
		}*/
		if(status == 0){
			return '成功';
		}
		if(status == 6 ||status == 7 ||status == 2){
			return '失败';
		}
		if(status != 0 && status != 6 && status != 7){
			return '待处理';
		}
	}

	var formatUserTransferStatus = function(type) {
		if(type == 0) {
			return '处理中';
		}
		if(type == 1) {
			return '成功';
		}
		if(type == 2) {
			return '失败';
		}
	}

	var formatUserBetsModel = function(model) {
		if(model == 'yuan') {
			return '元';
		}
		if(model == 'jiao') {
			return '角';
		}
		if(model == 'fen') {
			return '分';
		}
		if(model == 'li') {
			return '厘';
		}
	}
  //NORMAL(0, "未开奖"), NON_WIN(1, "未中奖"), AWARDED(2, "已派奖"), WAIT_AWARD(3, "等待派奖"), CANCELED(4, "个人撤单"), CANCELD_SYSTEM(
  //5, "系统撤单"), ORDER_REFUND(6, "已退款"), WIN(7, "已中奖"), UNKNOWN(8, "异常状态");
	var formatUserBetsStatus = function(status) {
		if(status == 0) {
			return '未开奖';
		}
		if(status == 1) {
			return '未中奖';
		}
		if(status == 2) {
			return '已派奖';
		}
		if(status == 3) {
			return '等待派奖';
		}
		if(status == 4) {
			return '个人撤单';
		}
		if(status == 5) {
			return '系统撤单';
		}
		if(status == 6) {
			return '已退款';
		}
		if(status == 7) {
			return '已中奖';
		}
		if(status == 8) {
			return '异常状态';
		}
    return '异常状态';
	}

	var formatUserChaseStatus = function(status) {
		if (status == 0) {
			return '未开始';
		}
		if (status == 1) {
			return '进行中';
		}
		if (status == 2) {
			return '已完成';
		}
		if (status == -1) {
			return '已撤单';
		}
	}

	var formatUserBillType = function(code) {
		if (code == 1000) {
            return '存款';
        }
        if (code == 1001) {
            return '取款';
        }
        if (code == 1002) {
            return '取款退回';
        }
        if (code == 1100) {
            return '转入';
        }
        if (code == 1101) {
            return '转出'
        }
        if (code == 1102) {
            return '上下级转账';
        }
        if (code == 1200) {
            return '优惠活动';
        }
        if (code == 1300) {
            return '消费';
        }
        if (code == 1301) {
            return '派奖';
        }
        if (code == 1302) {
            return '消费返点';
        }
        if (code == 1303) {
            return '取消订单';
        }
        if (code == 1400) {
            return '代理返点';
        }
        if (code == 1500) {
            return '分红';
        }
        if (code == 1600) {
            return '管理员增';
        }
        if (code == 1601) {
            return '管理员减';
        }
        if (code == 1700) {
            return '积分兑换';
        }
        if (code == 1800) {
            return '支付佣金';
        }
        if (code == 1801) {
            return '获得佣金';
        }
        if (code == 1900) {
            return '会员返水';
        }
	}

	var formatUserMessageType = function(type) {
		if(type == 0) {
			return '建议反馈';
		}
		if(type == 1) {
			return '已收消息';
		}
		if(type == 2) {
			return '已发消息';
		}
	}

	var formatUserMessageStatus = function(status, type) {
		if(status == 0) {
			return '正常';
		}
		if(status == 1) {
			return '已读';
		}
		if(status == -1) {
			return '已删除';
		}
	}

	var formatUserSysMessageType = function(type) {
		if(type == 0) {
			return '系统通知';
		}
		if(type == 1) {
			return '站内消息';
		}
		if(type == 2) {
			return '提现通知';
		}
		if(type == 3) {
			return '到账通知';
		}
	}

	var formatLotteryPaymentThridType = function(type) {
		if(type == 'ips') {
			return '环讯支付';
		}
		if(type == 'baofoo') {
			return '宝付支付';
		}
		if(type == 'newpay') {
			return '新生支付';
		}
		if(type == 'ecpss') {
			return '汇潮支付';
		}
		if(type == 'yeepay') {
			return '易宝支付';
		}
		if(type == 'mobao') {
			return '摩宝支付';
		}
		if(type == 'gopay') {
			return '国付宝支付';
		}
		if(type == 'pay41') {
			return '通汇支付';
		}
	}

	return {
		formatAmount: formatAmount,
		formatAccountType: formatAccountType,
		greeting: greeting,
		// 用户类型
		formatUserType: formatUserType,
		// 用户 VIP等级
		formatUserVipLevel: formatUserVipLevel,
		// 用户层级关系
		formatLevelUsers: formatLevelUsers,
		// 用户在线状态
		formatUserOnlineStatus: formatUserOnlineStatus,
		// 计划等级
		formatUserPlanLevel: formatUserPlanLevel,
		// 用户银行卡状态
		formatUserCardStatus: formatUserCardStatus,
		// 充值类型
		formatUserRechargeType: formatUserRechargeType,
		// 取款状态
		formatUserWithdrawalsStatus: formatUserWithdrawalsStatus,
		// 账户转账状态
		formatUserTransferStatus: formatUserTransferStatus,
		// 投注模式
		formatUserBetsModel: formatUserBetsModel,
		// 订单状态
		formatUserBetsStatus: formatUserBetsStatus,
		// 账单类别
		formatUserBillType: formatUserBillType,
		// 消息类型
		formatUserMessageType: formatUserMessageType,
		// 消息状态
		formatUserMessageStatus: formatUserMessageStatus,
		// 系统消息类型
		formatUserSysMessageType: formatUserSysMessageType,
		// 第三方支付类别
		formatLotteryPaymentThridType: formatLotteryPaymentThridType,
		formatUserChaseStatus: formatUserChaseStatus,
		//充值状态
		formatUserRechargeStatus:formatUserRechargeStatus,
		formatUserStatus:formatUserStatus
	}
}();

/**
 * 路由器设置
 */
var Route = {
	ROOTPATH: "/yx",
	PATH: "/yx/u/api",
	// 用户登录
	LOGIN: "/login",
	// 用户退出
	LOGOUT: "/logout",
	Account: {
		PATH: "/account",
		// 列出完整的用户信息
		LIST_FULL_INFO: "/list-full-info",
		// 检查用户名是否存在
		CHECK_USERNAME_EXIST: "/check-username-exist",
		// 修改用户昵称
		MODIFY_NICKNAME: "/modify-nickname",
		// 修改用户密码
		MODIFY_PASSWORD: "/modify-password",
		// 修改头像
		MODIFY_AVATAR: "/modify-avatar",
		// 修改用户资金密码
		MODIFY_WITHDRAW_PASSWORD: "/modify-withdraw-password",
		// 准备绑定
		PREPARE_BIND: "/prepare-bind",
		// 请求绑定
		APPLY_BIND: "/apply-bind",
		// 列出卡片
		LIST_CARD: "/list-card",
		// 准备绑定卡片
		PREPARE_BIND_CARD: "/prepare-bind-card",
		// 绑定卡片
		BIND_CARD: "/bind-card",
		// 设置默认卡片
		SET_DEFAULT_CARD: "/set-default-card",
		// 获取随机密保问题
		GET_RANDOM_SECURITY: "/get-random-security",
		// 绑定密保问题
		BIND_SECURITY: "/bind-security",
		// 搜索账单
		SEARCH_BILL: "/search-bill",
		// 获取账单详情
		GET_BILL_DETAILS: "/get-bill-details",
		// 搜索充值
		SEARCH_RECHARGE: "/search-recharge",
		// 准备提现
		PREPARE_WITHDRAW: "/prepare-withdraw",
		// 提现申请
		APPLY_WITHDRAW: "/apply-withdraw",
		// 搜索提现
		SEARCH_WITHDRAW: "/search-withdraw",
		// 同账户转账
		APPLY_SELF_TRANSFER: "/apply-self-transfer",
		// 上下级转账
		APPLY_ACCOUNT_TRANSFER: "/apply-account-transfer",
		// 彩票账户报表
		REPORT_GAME_LOTTERY: "/report-game-lottery",
		// 百家乐账户报表
		REPORT_GAME_BACCARAT: "/report-game-baccarat",
		// 获取消息列表
		LIST_MESSAGE: "/list-message",
		// 获取消息详情
		GET_MESSAGE_DETAILS: "/get-message-details",
		// 发送消息
		SEND_MESSAGE: "/send-message",
		// 读取消息
		READ_MESSAGE: "/read-message",
		// 删除消息
		DELETE_MESSAGE: "/delete-message",
		// 列出系统消息
		LIST_SYSTEM_MESSAGE: "/list-system-message",
		// 清空系统消息
		CLEAR_SYSTEM_MESSAGE: "/clear-system-message",
	},
	Agent: {
		PATH: "/agent",
		// 添加新的用户
		ADD_NEW_ACCOUNT: "/add-new-account",
		// 列出来账号配额
		LIST_CODE_QUOTA: "/list-code-quota",
		// 列出来团队账号
		LIST_TEAM_ACCOUNT: "/list-team-account",
		// 列出在线用户
		LIST_ONLINE_ACCOUNT: "/list-online-account",
		// 搜索彩票游戏订单
		SEARCH_GAME_LOTTERY_ORDER: "/search-game-lottery-order",
		// 搜索账户账单
		SEARCH_ACCOUNT_BILL: "/search-account-bill",
		// 彩票账户报表
		REPORT_GAME_LOTTERY: "/report-game-lottery",
		// 百家乐账户报表
		REPORT_GAME_BACCARAT: "/report-game-baccarat"
	},
	GameLottery: {
		PATH: "/game-lottery",
		// 彩票游戏信息
		STATIC_INFO: "/static-info",
		// 彩票游戏追号时间
		STATIC_CHASE_TIME: "/static-chase-time",
		// 彩票游戏开奖号码
		STATIC_OPEN_CODE: "/static-open-code",
		// 彩票游戏开奖时间
		STATIC_OPEN_TIME: "/static-open-time",
		// 添加订单
		ADD_ORDER: "/add-order",
		// 撤销订单
		CANCEL_ORDER: "/cancel-order",
		// 获取订单
		GET_ORDER: "/get-order",
		// 搜索订单
		SEARCH_ORDER: "/search-order",
		// 拉取开奖通知
		PULL_OPEN_NOTICE: "/pull-open-notice"
	},
	GameBaccarat: {
		PATH: "/game-baccarat",
	},
	Payment: {
		PATH: "/payment",
		// 列出银行
		STATIC_LIST_BANK: "/static-list-bank",
		// 列出所有可用支付方式
		REQUEST_ALL_METHOD: "/request-all-method",
		// 请求第三方支付
		REQUEST_THRID_PAY: "/request-thrid-pay",
	},
	System: {
		PATH: "/system",
		// 列出系统公告
		LIST_NOTICE: "/list-notice",
		// 获取公告详情
		GET_NOTICE_DETAILS: "/get-notice-details",
	},
	WebAjax: {
		PATH: "/game-lottery",
		// 初始化页面
		INIT_PAGE: "/init-page",
		// 循环
		LOOP: "/loop",
		// 初始化彩票页面
		INIT_GAME_LOTTERY: "/init-game-lottery",
	},
  LotteryGroups:{
    'ssc':[11,18,15,16,19,911],
    'pk10':[43,204,47],
    '11y':[24,21,23,22,25,26,28],
    '3d':[41,42],
    'k3':[33,35,36,31,32],
    'kl8':[7,8],
    'limit':[911]
  },
  LotteryBonusGroups:[100000,200000,400000],
  LotteryBonusTip:function(id) {
    var ltgroups = Route.LotteryGroups;
    var limitnum = Route.LotteryBonusGroups[2];
    $.inArray(id,Route.LotteryGroups['pk10'])>-1 ? limitnum= Route.LotteryBonusGroups[1] : true;
    $.inArray(id,Route.LotteryGroups['11y'])>-1 ? limitnum= Route.LotteryBonusGroups[1] : true;
    $.inArray(id,Route.LotteryGroups['3d'])>-1 ? limitnum= Route.LotteryBonusGroups[0] : true;
    $.inArray(id,Route.LotteryGroups['k3'])>-1 ? limitnum= Route.LotteryBonusGroups[1] : true;
    $.inArray(id,Route.LotteryGroups['kl8'])>-1 ? limitnum= Route.LotteryBonusGroups[1] : true;
    $.inArray(id,Route.LotteryGroups['limit'])>-1 ? limitnum= Route.LotteryBonusGroups[0] : true;
    if (id=='911') {
      limitnum= Route.LotteryBonusGroups[0]  
    }
    return limitnum;
  }
};

/**
 * HTTP请求
 */
var HttpRequest = function(options) {
	var defaults = {
		type: 'post',
		data: {},
		dataType: 'json',
		async: true,
		cache: false,
		beforeSend: null,
		success: null,
		complete: null
	};
	var o = $.extend({}, defaults, options);
	$.ajax({
        type: 'post',
        url: o.url,
        data: o.data,
        dataType: 'json',
        async: o.async,
        beforeSend: function() {
        	o.beforeSend && o.beforeSend();
        },
        success: function(response) {
        	o.success && o.success(response);
        },
        complete: function() {
        	o.complete && o.complete();
        }
    });
};

var MainCtrl = function() {

	/**
	 * 登录方法
	 */
	var login = function(options) {
		//options.url = Route.PATH + Route.LOGIN;
		options.url = "/sso/login";
		HttpRequest(options);
	};

	/**
	 * 退出方法
	 */
	var logout = function(options) {
		options.url = "/sso/logout";
		HttpRequest(options);
	};

	return {
		login: login,
		logout: logout
	}

}();

var AccountCtrl = function() {

	var thisScope = 'Account';

	var getScopeUrl = function(key) {
		return Route.PATH + Route[thisScope].PATH + Route[thisScope][key];
	}

	/**
	 * 修改密码方法
	 */
	var modifyPassword = function(options) {
		options.url = getScopeUrl('MODIFY_PASSWORD');
		HttpRequest(options);
	}

	return {
		modifyPassword: modifyPassword
	}

}();

var GameLotteryCtrl = function() {

	var thisScope = 'GameLottery';

	var getScopeUrl = function(key) {
		return Route.PATH + Route[thisScope].PATH + Route[thisScope][key];
	}

	/**
	 * 获取彩票游戏开奖号码
	 */
	var staticOpenCode = function(options) {
		//console.log('staticOpenCodestaticOpenCodestaticOpenCode');
    options.url = getScopeUrl('STATIC_OPEN_CODE');
		HttpRequest(options);
	}

	/**
	 * 获取彩票游戏开奖时间
	 */
	var staticOpenTime = function(options) {
		options.url = getScopeUrl('STATIC_OPEN_TIME');
		HttpRequest(options);
	}

	/**
	 * 投注方法
	 */
	var addOrder = function(options) {
		options.url = getScopeUrl('ADD_ORDER');
		HttpRequest(options);
	}

	var pullOpenNotice = function(options) {
		options.url = getScopeUrl('PULL_OPEN_NOTICE');
		HttpRequest(options);
	}

	return {
		staticOpenCode: staticOpenCode,
		staticOpenTime: staticOpenTime,
		addOrder: addOrder,
		pullOpenNotice: pullOpenNotice
	}

}();

var PaymentCtrl = function() {

	var thisScope = 'Payment';

	var getScopeUrl = function(key) {
		return Route.PATH + Route[thisScope].PATH + Route[thisScope][key];
	}

	var requestAllMethod = function(options) {
		options.url = getScopeUrl('REQUEST_ALL_METHOD');
    //console.log(options);
		HttpRequest(options);
	}

	var requestThridPay = function(options) {
		options.url = getScopeUrl('REQUEST_THRID_PAY');
		HttpRequest(options);
	}

	return {
		requestAllMethod: requestAllMethod,
		requestThridPay: requestThridPay
	}

}();

/**
 * 数据工厂
 */
var AppData = function() {

	var isLogin = false; // 是否登录
	var mainAccount; // 主账户
	var lotteryAccount; // 彩票账户
	var baccaratAccount; // 百家乐账户
	var info; // 信息
	var msgCount;

	var init = function(options) {
		if (options === undefined) {
			options = {};
		}
		options.url = Route.ROOTPATH + Route.WebAjax.PATH + Route.WebAjax.INIT_PAGE;
		options.async = false;
		options.success = function(response) {
			if (response.error == 0) {
				var data = response.data;
				isLogin = data.isLogin;
				if (isLogin) {
					mainAccount = data.main;
					lotteryAccount = data.lottery;
					info = data.info;
					msgCount = data.msgCount;
				}
        //console.log(mainAccount.type=='0');
        if (mainAccount.type=='0') {
          $('#agent-top-nav,.topagtbtn').hide()
        }else {
          $('#agent-top-nav,.topagtbtn').show()
        }

        setTimeout(function() {
          //console.log(parseInt(lotteryAccount.code,10)<1701,$('#bonuspage').size(),$('.nav').size());
          if (parseInt(lotteryAccount.code,10)<1701) {
            //$('a[data-dk-dropdown-value="91"]').parent().remove();
            //$('#bonuspage').remove();
          }else {
            //$('#bonuspage').show();
          }
        },1000);
			}
		};
		HttpRequest(options);
	};

	return {
		init: init,
    jbox: null,
		isLogin: function() {
			return isLogin;
		},
		getMainAccount: function() {
			return mainAccount;
		},
		getLotteryAccount: function() {
			return lotteryAccount;
		},
		getBaccaratAccount: function() {
			return baccaratAccount;
		},
		getInfo: function() {
			return info;
		},
		getMsgCount: function() {
			return msgCount;
		}
	}

}();
if (location.pathname!='/static/help.html') {
  AppData.init();
}
/**
 * 循环请求
 */
var AppLoop = function() {

	var loop = [];
	var callback = [];

	var init = function() {
		function timeout(){
			var options = {

			}
			options.data = {
				//loop: $.toJSON(loop)
        loop: JSON.stringify(loop)
			}
			options.url = Route.PATH +  Route.WebAjax.LOOP;
			options.success = function(response) {
				if (response.error == 0) {
					$('[data-field="lotteryBalance"]').html(response.data.lotteryBalance);
					$('[data-field="baccaratBalance"]').html(response.data.baccaratBalance);
          $('#will-sum01').html(response.data.lotteryBalance);
					$('[data-field="msgCount"]').html(response.data.msgCount);
					for (var i = 0; i < callback.length; i++) {
						if ($.isFunction(callback[i])) {
							callback[i](response.data);
						}
					}
				}
			};
			HttpRequest(options);
			setTimeout(timeout,30000)
		}
		timeout();
	}

	var push = function(data, cb) {
		loop.push(data);
		callback.push(cb);
	}

	return {
		init: init,
		push: push
	}

}();
