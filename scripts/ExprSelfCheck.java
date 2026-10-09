/**
 * 表达式引擎本机自检（不依赖 Gradle/Minecraft，javac 直接编译运行）。
 * 用法： javac -cp <asm.jar> -d <out> common/src/main/java/com/noone/particleex/util/*.java <此文件> & java -cp <out>;<asm.jar> ExprSelfCheck
 */
import com.noone.particleex.util.*;

public class ExprSelfCheck {
    static int passed = 0, failed = 0;

    public static void main(String[] args) {
        // 算术与优先级
        eval("1+2", 3);
        eval("1+2*3", 7);
        eval("(1+2)*3", 9);
        eval("10/3", 3);
        eval("10%3", 1);
        eval("2^10", 1024);
        eval("-5+3", -2);

        // 变量赋值
        ParticleStruct s = evalStruct("x,y=2,3");
        check(s.x == 2.0, "x=2");
        check(s.y == 3.0, "y=3");
        evalWith(s, "x*y", 6);

        // 多语句
        eval("x=1;x=2", 2);
        evalWith(evalStruct("x=1;x=2"), "x*10", 20);

        // 函数
        eval("sin(0)", 0);
        eval("cos(0)", 1);
        eval("pow(2,10)", 1024);
        eval("sqrt(9)", 3);
        eval("abs(-7)", 7);
        eval("max(3,7)", 7);

        // 常量
        eval("PI>3.14&PI<3.15", 1);
        eval("E>2.71&E<2.72", 1);

        // 比较与逻辑
        eval("3>2", 1);
        eval("2>3", 0);
        eval("1<2&2<3", 1);
        eval("1>2|2<3", 1);
        eval("!0", 1);
        eval("1==1", 1);

        // 条件 / destroy 写法
        eval("age>100", 0);
        ParticleStruct s2 = evalStruct("1");
        s2.age = 150;
        checkEval(s2, "age>100", 1, "age 条件");
        eval("destroy=age>100", 0);

        // 错误输入
        expectError("1+");
        expectError("(");
        expectError("x=,");
        expectError("sin");
        expectError("");

        // null 语义
        check(ExpressionUtil.parse(null) == null, "parse(null)=null");
        check(ExpressionUtil.parse("null") == null, "parse('null')=null");

        System.out.println("PASSED=" + passed + " FAILED=" + failed);
        if (failed > 0) System.exit(1);
    }

    static void eval(String expr, int expected) {
        try {
            IExecutable exe = ExpressionUtil.parse(expr);
            int got = exe.invoke();
            check(got == expected, "'" + expr + "' = " + got + " (expect " + expected + ")");
        } catch (Throwable t) {
            check(false, "'" + expr + "' threw: " + t);
        }
    }

    static ParticleStruct evalStruct(String expr) {
        IExecutable exe = ExpressionUtil.parse(expr);
        exe.invoke(); // 执行赋值语句，struct 才有值
        return exe.getData();
    }

    static void evalWith(ParticleStruct s, String expr, int expected) {
        IExecutable exe = ExpressionUtil.parse(expr);
        ParticleStruct d = exe.getData();
        d.x = s.x; d.y = s.y; d.z = s.z; d.age = s.age;
        for (java.lang.reflect.Field f : ParticleStruct.class.getFields()) {
            try { f.set(d, f.get(s)); } catch (Exception ignored) {}
        }
        int got = exe.invoke();
        check(got == expected, "'" + expr + "' = " + got + " (expect " + expected + ")");
    }

    static void checkEval(ParticleStruct s, String expr, int expected, String label) {
        evalWith(s, expr, expected);
    }

    static void expectError(String expr) {
        try {
            IExecutable exe = ExpressionUtil.parse(expr);
            exe.invoke();
            check(false, "'" + expr + "' 应报错但通过了");
        } catch (RuntimeException e) {
            check(true, "'" + expr + "' 正确报错: " + e.getMessage().split("\n")[0]);
        } catch (Throwable t) {
            check(true, "'" + expr + "' 正确报错(非Runtime): " + t);
        }
    }

    static void check(boolean ok, String label) {
        if (ok) { passed++; System.out.println("  OK: " + label); }
        else { failed++; System.err.println("FAIL: " + label); }
    }
}