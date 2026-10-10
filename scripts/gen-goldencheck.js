const fs = require('fs');
const cases = JSON.parse(fs.readFileSync(process.env.TEMP + '/golden.json', 'utf8'));
function jesc(s) {
  return s.replace(/\\/g, '\\\\').replace(/"/g, '\\"').replace(/\n/g, '\\n');
}
let out = 'import com.noone.particleex.util.ExpressionUtil;\n'
  + 'import com.noone.particleex.util.IExecutable;\n'
  + 'import com.noone.particleex.util.ParticleStruct;\n\n'
  + 'public class GoldenCheck {\n'
  + '    static int passed = 0, failed = 0;\n'
  + '    public static void main(String[] args) {\n'
  + '        String[][] cases = {\n';
for (const [expr, expect] of cases) {
  // 剥掉包裹单/双引号（如 "'java.lang.X: msg'"、'"java.lang.X: msg"'），否则 startsWith("java.lang") 判定失效
  let e = expect;
  if (e.length >= 2 && ((e[0] === "'" && e[e.length - 1] === "'") || (e[0] === '"' && e[e.length - 1] === '"'))) e = e.slice(1, -1);
  out += '            {"' + jesc(expr) + '", "' + jesc(e) + '"},\n';
}
out += '        };\n'
  + '        for (String[] c : cases) {\n'
  + '            String expr = c[0], expect = c[1];\n'
  + '            try {\n'
  + '                IExecutable exe = ExpressionUtil.parse(expr);\n'
  + '                if (exe == null) {\n'
  + '                    // parse 返回 null 与上游语义一致（空串/\"null\"）；上游 NPE 发生在调用方未判空处\n'
  + '                    if (expect.startsWith("java.lang.NullPointerException")) { passed++; }\n'
  + '                    else { fail(expr, expect, "null-exe"); }\n'
  + '                    continue;\n'
  + '                }\n'
  + '                ParticleStruct s = new ParticleStruct();\n'
  + '                exe.getData().x = s.x; exe.getData().y = s.y; exe.getData().z = s.z; exe.getData().age = s.age;\n'
  + '                long r = exe.invoke();\n'
  + '                if (expect.startsWith("java.lang")) { fail(expr, expect, "NO-EXC:" + r); }\n'
  + '                else if (String.valueOf(r).equals(expect)) { passed++; }\n'
  + '                else { fail(expr, expect, String.valueOf(r)); }\n'
  + '            } catch (Throwable t) {\n'
  + '                String cls = t.getClass().getName();\n'
  + '                if (expect.startsWith("java.lang")) {\n'
  + '                    String expPrefix = expect.split(":")[0];\n'
  + '                    if (cls.equals(expPrefix)) { passed++; }\n'
  + '                    else { fail(expr, expect, "EXC:" + cls); }\n'
  + '                } else { fail(expr, expect, "EXC:" + cls + ":" + t.getMessage()); }\n'
  + '            }\n'
  + '        }\n'
  + '        System.out.println("PASSED=" + passed + " FAILED=" + failed);\n'
  + '    }\n'
  + '    static void fail(String expr, String expect, String got) {\n'
  + '        failed++;\n'
  + '        System.out.println("FAIL [" + expr + "] expect " + expect + " got " + got);\n'
  + '    }\n'
  + '}\n';
fs.writeFileSync(process.env.TEMP + '/GoldenCheck.java', out);
console.log('generated ok');