package org.seckill.controller;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatSecKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @创建人 wj
 * @创建时间 2018/10/25
 * @描述
 */
@Controller
@RequestMapping("/seckill")
//url:/模块/资源/{id}/细分
public class SeckillController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private SeckillService seckillService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String List(Model model) {
        List<Seckill> list = seckillService.getseckillList();
        model.addAttribute("list", list);
        return "list";
    }

    //    value = "/${seckillId}/detail" 不能加$符号，否则找不到路径！！！
    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable(value = "seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if (seckill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }


    @RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable(value = "seckillId") Long seckillId) {
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/{seckillId}/{md5}/excution", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    //required = false :不传值不会报错
    public SeckillResult<SeckillExecution> excution(@PathVariable("seckillId") Long seckillId, @CookieValue(value = "killPhone", required = false) Long userPhone, @PathVariable("md5") String md5) {
        //验证复杂时，采用 springMVC valid 验证
        if (userPhone == null) {
            return new SeckillResult<SeckillExecution>(true, "用户未注册");
        }
        try {
            // 一个方法不处理这个异常，而是调用层次向上传递，谁调用这个方法，这个异常就由谁来处理。所以要处理这个方法的异常
             SeckillExecution seckillExecution = seckillService.excuteSeckill(seckillId, userPhone, md5);
            //使用存储过程
//            SeckillExecution seckillExecution = seckillService.excuteSeckillProcedure(seckillId, userPhone, md5);
            return new SeckillResult<SeckillExecution>(true, seckillExecution);
        } catch (SeckillCloseException se) {
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.END);
            return new SeckillResult<SeckillExecution>(false, seckillExecution);
        } catch (RepeatSecKillException re) {
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KIL);
            return new SeckillResult<SeckillExecution>(false, seckillExecution);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.SYSTEM_ERROR);
            return new SeckillResult<SeckillExecution>(false, seckillExecution);
        }
    }

    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time() {
        Date date = new Date();
        return new SeckillResult<Long>(false, date.getTime());
    }


}
