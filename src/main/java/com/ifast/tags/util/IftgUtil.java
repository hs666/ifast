package com.ifast.tags.util;

import com.ifast.common.domain.DictDO;
import com.ifast.common.service.DictService;
import com.ifast.tags.vo.ValueVo;
import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

import java.util.*;

/**
 * IftgUtil 辅助工具类
 *
 * @author: zet
 * @date: 2018/8/23 22:46
 */
public class IftgUtil {
    private static final String KEY_ALL = "all";

    /**
     * 获取数据
     *
     * @param dictService 字典service对象
     * @param dicType     字典类型编码
     * @return
     */
    public static List<ValueVo> getValues(DictService dictService, String dicType) {
        return getValues(dictService, dicType, null);
    }

    /**
     * 获取数据
     *
     * @param dictService   字典service对象
     * @param dicType       字典类型编码
     * @param selectedValue 默认选中的值
     * @return
     */
    public static List<ValueVo> getValues(DictService dictService, String dicType, String[] selectedValue) {
        if (KEY_ALL.equals(dicType)) {
            return getListType(dictService, dicType, selectedValue);
        }

        Map<String, Object> map = new HashMap<>(16);
        map.put("type", dicType);
        List<DictDO> dictDOS = dictService.selectByMap(map);
        List<ValueVo> options = parseToValues(dictDOS, selectedValue);
        return options;
    }

    public static List<ValueVo> getListType(DictService dictService, String dicType, String[] selectedValue) {
        List<DictDO> dictDOS = dictService.listType();
        return parseListTypeToValues(dictDOS, selectedValue);
    }

    /**
     * 获取标签对应值
     *
     * @param arguments     thymeleaf 上下文对象
     * @param element       当前节点对象
     * @param attributeName 属性名
     * @return 标签对象值
     */
    public static String getTargetAttributeValue(Arguments arguments, Element element, String attributeName) {
        String attributeValue = element.getAttributeValue(attributeName);
        Configuration configuration = arguments.getConfiguration();
        IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
        IStandardExpression expression = null;
        try {
            expression = expressionParser.parseExpression(configuration, arguments, attributeValue);
        } catch (Exception e) {
            return null;
        }
        Object result = expression.execute(configuration, arguments);
        return result == null ? "" : result.toString();
    }

    /**
     * 转换格式
     *
     * @param dictDOS 待转换的字典数据
     * @return 转换后的格式
     */
    private static List<ValueVo> parseToValues(List<DictDO> dictDOS, String[] selectedValue) {
        List<ValueVo> valueVos = new ArrayList<>();
        ValueVo valueVo = null;
        List<String> selecteds = Objects.nonNull(selectedValue) ? Arrays.asList(selectedValue) : null;

        for (DictDO dictDO : dictDOS) {
            valueVo = new ValueVo();
            valueVo.setName(dictDO.getName());
            valueVo.setVlaue(dictDO.getValue());
            if (Objects.nonNull(selecteds) &&
                    selecteds.contains(dictDO.getValue())) {
                valueVo.setSelected(true);
            }
            valueVos.add(valueVo);
        }
        return valueVos;
    }

    /**
     * 转换格式(字典大类字典)
     *
     * @param dictDOS 待转换的字典数据
     * @return 转换后的格式
     */
    private static List<ValueVo> parseListTypeToValues(List<DictDO> dictDOS, String[] selectedValue) {
        List<ValueVo> valueVos = new ArrayList<>();
        ValueVo valueVo = null;
        List<String> selecteds = Objects.nonNull(selectedValue) ? Arrays.asList(selectedValue) : null;

        for (DictDO dictDO : dictDOS) {
            valueVo = new ValueVo();
            valueVo.setName(dictDO.getDescription());
            valueVo.setVlaue(dictDO.getType());
            if (Objects.nonNull(selecteds) &&
                    selecteds.contains(dictDO.getType())) {
                valueVo.setSelected(true);
            }
            valueVos.add(valueVo);
        }
        return valueVos;
    }
}
