package com.tencent.bk.devops.atom.task;

import com.tencent.bk.devops.atom.AtomContext;
import com.tencent.bk.devops.atom.common.Status;
import com.tencent.bk.devops.atom.pojo.AtomResult;
import com.tencent.bk.devops.atom.pojo.DataField;
import com.tencent.bk.devops.atom.pojo.StringData;
import com.tencent.bk.devops.atom.spi.AtomService;
import com.tencent.bk.devops.atom.spi.TaskAtom;
import com.tencent.bk.devops.atom.task.pojo.AtomParam;
import com.tencent.bk.devops.atom.task.pojo.constant.ErrorCodeConstants;
import com.tencent.bk.devops.atom.utils.I18nUtil;
import com.tencent.bk.devops.atom.utils.MessageUtil;
import com.tencent.bk.devops.atom.utils.json.JsonUtil;
import com.tencent.bk.devops.plugin.pojo.ErrorType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @version 1.0
 */
@AtomService(paramClass = AtomParam.class)
public class DemoAtom implements TaskAtom<AtomParam> {

    private final static Logger logger = LoggerFactory.getLogger(DemoAtom.class);

    /**
     * 执行主入口
     *
     * @param atomContext 插件上下文
     */
    @Override
    public void execute(AtomContext<AtomParam> atomContext) {
        // 1.1 拿到请求参数
        AtomParam param = atomContext.getParam();
        logger.info("the param is :{}", JsonUtil.toJson(param));
        // 1.2 拿到初始化好的返回结果对象
        AtomResult result = atomContext.getResult();
        // 2. 校验参数失败直接返回
        checkParam(param, result);
        if (result.getStatus() != Status.success) {
            return;
        }
        // 3. 模拟处理插件业务逻辑
        logger.groupStart("Task log"); // 控制前端日志折叠的首尾行
        logger.info("the desc is :{}", param.getDesc()); // 打印描述信息
        logger.groupEnd("Task log");

        try {
            Thread.sleep(1000); // 模拟某种可能出错的业务场景
        } catch (Exception e) {
            // 设置错误信息
            result.setErrorInfo(
                Status.error,
                ErrorCodeConstants.UNKNOWN_ERROR,
                ErrorType.PLUGIN,
                null
            );
        }
        // 4. 输出参数，如果有的话
        // 输出参数是一个Map,Key是参数名， value是值对象
        Map<String, DataField> data = result.getData();
        // 假设这个是输出参数的内容
        StringData testResult = new StringData("hello");
        // 设置一个名称为testResult的出参
        data.put("testResult", testResult);
        logger.info("the testResult is :{}", JsonUtil.toJson(testResult));
        // 结束。
    }

    /**
     * 检查参数
     *
     * @param param  请求参数
     * @param result 结果
     */
    private void checkParam(AtomParam param, AtomResult result) {
        // 参数检查
        if (StringUtils.isBlank(param.getDesc())) {
            // 根据语言信息获取字段名称
            String descName = MessageUtil.getMessageByLocale("input.desc.label", I18nUtil.getLanguage());
            result.setErrorInfo(
                Status.failure,
                ErrorCodeConstants.PARAMETER_IS_NULL,
                ErrorType.USER,
                new String[]{descName}
            );
        }

        /*
         其他比如判空等要自己业务检测处理，否则后面执行可能会抛出异常，状态将会是 Status.error
         这种属于插件处理不到位，算是bug行为，需要插件的开发去定位
          */
    }

}
