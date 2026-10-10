// 1.20.2/1.20.4 payload 转换：StreamCodec.of((buf, value)->{...}, buf->{...}) → write(FriendlyByteBuf)/read(FriendlyByteBuf)
const fs = require('fs');
const path = process.argv[2];
let s = fs.readFileSync(path, 'utf8');
const cls = s.match(/public record (\w+)\(/)[1];

// 1. RegistryFriendlyByteBuf → FriendlyByteBuf
s = s.replace(/RegistryFriendlyByteBuf/g, 'FriendlyByteBuf');
// 2. 删 StreamCodec import 与 CODEC 声明头
s = s.replace(/import net\.minecraft\.network\.codec\.StreamCodec;\n/, '');
s = s.replace(/  public static final StreamCodec<[^>]*> CODEC = StreamCodec\.of\(/, '  @Override\n  public void write(FriendlyByteBuf buf) {');
// 3. 第一 lambda 结束 + 第二 lambda 开始 → write 方法收尾 + read 方法头
s = s.replace(/        \},\n          buf -> \{/s, '  }\n\n  public static ' + cls + ' read(FriendlyByteBuf buf) {');
// 4. 第二 lambda 结尾 `});` → read 收尾 + type()
s = s.replace(/        \}\);\n/s, '  }\n\n  @Override\n  public Type<\\? extends CustomPacketPayload> type\\(\\) \{\n    return TYPE;\n  }\n');
// 5. 写侧 value. → this.（读侧第二 lambda 不含 value）
// 先按方法体分段：写侧在 write 方法内（从 write 头到第一个 '  }\n\n  public static' 之间）
const m = s.match(/public void write\(FriendlyByteBuf buf\) \{\n([\s\S]*?)\n  \}\n\n  public static/);
if (m) {
  const body = m[1].replace(/\bvalue\./g, 'this.');
  s = s.replace(m[1], body);
}
// 6. 粒子序列化 1.20.4 API
s = s.replace(/ParticleTypes\.STREAM_CODEC\.encode\(buf, ([^)]+)\)/g, 'ParticleTypes.writeParticle(buf, $1)');
s = s.replace(/ParticleTypes\.STREAM_CODEC\.decode\(buf\)/g, 'buf.readParticle(null)');
// 7. import 清理（ParticleTypes 仍需要）
fs.writeFileSync(path, s);
console.log('converted', path, '->', cls);
