package com.noone.particleex.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** 表达式引擎回归测试（纯 Java，无 MC 依赖，CI 自动运行）。 */
class ExpressionTest {

    /** 算术与优先级 */
    @Test
    void basicArithmetic() {
        assertEval("1+2", 3);
        assertEval("1+2*3", 7);
        assertEval("(1+2)*3", 9);
        assertEval("10/3", 3);          // 整数除法
        assertEval("10%3", 1);
        assertEval("2^10", 1024);
        assertEval("-5+3", -2);
    }

    /** 变量赋值与读取 */
    @Test
    void variableAssign() {
        ParticleStruct s = eval("x,y=2,3");
        assertEquals(2.0, s.x);
        assertEquals(3.0, s.y);
        assertEvalWith(s, "x*y", 6);
    }

    /** 多语句 */
    @Test
    void multiStatement() {
        assertEvalWith(eval("x=1;x=2"), "x*10", 20);
    }

    /** 函数 */
    @Test
    void functions() {
        assertEval("sin(0)", 0);
        assertEval("cos(0)", 1);
        assertEval("pow(2,10)", 1024);
        assertEval("sqrt(9)", 3);
        assertEval("abs(-7)", 7);
        assertEval("max(3,7)", 7);
    }

    /** 常量 */
    @Test
    void constants() {
        assertEval("PI>3.14&PI<3.15", 1);
        assertEval("E>2.71&E<2.72", 1);
    }

    /** 比较与逻辑 */
    @Test
    void comparisonAndLogic() {
        assertEval("3>2", 1);
        assertEval("2>3", 0);
        assertEval("1<2&2<3", 1);
        assertEval("1>2|2<3", 1);
        assertEval("!0", 1);
        assertEval("1==1", 1);
    }

    /** 条件粒子常用写法：destroy / 表达式真假 */
    @Test
    void conditionUsage() {
        assertEval("age>100", 0);           // struct.age 默认 0
        ParticleStruct s = eval("1");
        s.age = 150;
        assertEquals(1, invoke(ExpressionUtil.parse("1"), s));
        assertEval("destroy=age>100", 0);       // 赋值表达式返回被赋值值
    }

    /** 错误输入必须抛异常（不静默吞掉） */
    @Test
    void syntaxErrorsThrow() {
        assertThrows(RuntimeException.class, () -> ExpressionUtil.parse("1+"));
        assertThrows(RuntimeException.class, () -> ExpressionUtil.parse("("));
        assertThrows(RuntimeException.class, () -> ExpressionUtil.parse("x=,"));
        assertThrows(RuntimeException.class, () -> ExpressionUtil.parse("sin"));
        assertNull(ExpressionUtil.parse(""));
    }

    /** 表达式为 null / "null" 字符串返回空执行器（命令层语义） */
    @Test
    void nullExpression() {
        assertNull(ExpressionUtil.parse(null));
        assertNull(ExpressionUtil.parse("null"));
    }

    private static void assertEval(String expr, int expected) {
        assertEquals(expected, invoke(ExpressionUtil.parse(expr), new ParticleStruct()));
    }

    private static void assertEvalWith(ParticleStruct s, String expr, int expected) {
        assertEquals(expected, invoke(ExpressionUtil.parse(expr), s));
    }

    private static ParticleStruct eval(String expr) {
        ParticleStruct s = new ParticleStruct();
        IExecutable exe = ExpressionUtil.parse(expr);
        exe.getData().x = s.x; exe.getData().y = s.y; exe.getData().z = s.z;
        exe.getData().age = s.age;
        return exe.getData();
    }

    private static int invoke(IExecutable exe) {
        return invoke(exe, new ParticleStruct());
    }

    private static int invoke(IExecutable exe, ParticleStruct s) {
        ParticleStruct d = exe.getData();
        d.x = s.x; d.y = s.y; d.z = s.z; d.age = s.age;
        return exe.invoke();
    }
}
