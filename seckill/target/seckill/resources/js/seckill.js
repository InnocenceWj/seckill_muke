//存放主要交互逻辑js代码
//javascript模块化,以分包的方式规范编码 seckill.detail.init()
var seckill = {
    //封装秒杀相关ajax的url
    URL: {
        now: '/seckill/time/now',
        exposer: function (seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        excution: function (seckillId, md5) {
            return '/seckill/' + seckillId + '/' + md5 + '/excution';
        }
    },
    handleSeckill: function (seckillId, node) {
        //获取秒杀逻辑，控制显示逻辑，执行秒杀
        //生成一个开始秒杀的按钮(在操作节点之前先隐藏节点)
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            if (result&&result.success) {
                var exposer = result.data;
                if (exposer.exposed) {
                    //开启秒杀
                    var md5 = exposer.md5;
                    var killUrl = seckill.URL.excution(seckillId, md5);
                    $('#killBtn').one('click', function () {
                        //    执行秒杀请求
                        //    1.先禁用按钮
                        $(this).addClass('disabled');
                        //    2.发送秒杀请求
                        $.post(killUrl, {}, function (result) {
                            if (result&&result.success) {
                                var killResult = result.data;
                                var state = killResult.state;
                                var stateInfo = killResult.stateInfo;
                                node.html('<span class="label label-success">' + stateInfo + '</span>')
                            }
                        })
                    });
                    node.show();
                } else {
                    //    未开启秒杀
                    var now = exposer.now;
                    var start = exposer.start;
                    var end = exposer.end;
                    //重新计算计时逻辑
                    seckill.countDown(seckillId, now, start, end);
                }
            } else {
                console.log("result:" + result);
            }

        });
    },
    validatePhone: function (phone) {
        // isNaN() 函数用于检查其参数是否是非数字值。
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        } else {
            return false;
        }
    },
    countDown: function (seckillId, nowTime, startTime, endTime) {
        var seckillBox = $('#seckill-box');
        if (nowTime > endTime) {
            seckillBox.html('秒杀结束');
        } else if (nowTime < startTime) {
            //    秒杀未开始
            //    开始时间+1秒，防止用户端的计时时间偏移
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime, function (event) {
                var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
                seckillBox.html(format)
            }).on('finish.countdown', function () {
                //获取秒杀地址 控制显示逻辑 执行秒杀
                seckill.handleSeckill(seckillId, seckillBox);
            });

        } else {
            seckill.handleSeckill(seckillId, seckillBox);
        }
    },
    //详情页秒杀逻辑
    detail: {
        //详情页初始化。
        //这里注意jsp如何给js传递参数
        init: function (params) {
            //手机验证和登录，计时交互
            //    1.在cookie中查找手机号
            var killPhone = $.cookie("killPhone");
            if (!seckill.validatePhone(killPhone)) {
                var killModal = $("#killPhoneModal");
                killModal.modal({
                    show: true,//显示弹出层
                    backdrop: "static",//禁止位置关闭
                    keyboard: false//关闭键盘事件
                });
                $("#killPhoneBtn").click(function () {
                    var inputPhone = $("#killPhoneKey").val();
                    $.cookie("killPhone", inputPhone, {expires: 7, path: '/seckill'});
                    if (seckill.validatePhone(inputPhone)) {
                        window.location.reload();
                    } else {
                        //遇到提示信息：手机号出错！ ，一般做数据字典
                        $("#killPhoneMessage").hide().html("<label class='label label-danger'>手机号出错！</label>").show(200)
                    }
                });
            }
            //2.已经登陆
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            //计时交互
            //不应该把请求地址放在这里，这不是一个好习惯。
            //获取服务器时间，因为不同浏览器不同pc时间存在偏差
            $.get(seckill.URL.now, function (result) {
                if (result) {
                    var nowTime = result.data;
                    //计时判断
                    seckill.countDown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log('result:' + result);
                }
            });
        },
    }
}