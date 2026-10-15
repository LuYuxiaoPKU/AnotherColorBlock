import io, glob, json, os, sys
from collections import defaultdict

"""扫 Claude Code 会话 transcript 汇总真实 token usage
用法: py usage-report.py [项目目录...]  缺省扫 AnotherColorBlock 两目录"""

DIRS = sys.argv[1:] or [
    r'C:/Users/Yuxiao Lu/.claude/projects/C--00-Data-RGM-AnotherColorBlock/',
    r'C:/Users/Yuxiao Lu/.claude/projects/C--00-Data-RGM-AnotherColorBlock-CC/',
]

model_stat = defaultdict(lambda: {'in':0,'out':0,'cache_r':0,'calls':0,'first':'','last':''})
sess_stat  = defaultdict(lambda: {'in':0,'out':0,'cache_r':0,'calls':0,'models':set(),'first':'','last':''})
monthly    = defaultdict(lambda: [0,0,0])

for d in DIRS:
    for jf in glob.glob(d+'*.jsonl'):
        sid = os.path.basename(jf).replace('.jsonl','')[:8]
        s = sess_stat[sid]
        with io.open(jf,'r',encoding='utf-8',errors='ignore') as fh:
            for line in fh:
                try: rec = json.loads(line)
                except: continue
                if rec.get('type') != 'assistant': continue
                msg = rec.get('message') or {}
                u = msg.get('usage') or {}
                i = u.get('input_tokens',0) or 0
                o = u.get('output_tokens',0) or 0
                cr = u.get('cache_read_input_tokens',0) or 0
                if i==0 and o==0 and cr==0: continue
                m = (msg.get('model') or '?').split('/')[-1]
                ts = rec.get('timestamp') or ''
                mm = model_stat[m]
                mm['in']+=i; mm['out']+=o; mm['cache_r']+=cr; mm['calls']+=1
                if ts and (not mm['first'] or ts<mm['first']): mm['first']=ts
                if ts and ts>mm['last']: mm['last']=ts
                s['in']+=i; s['out']+=o; s['cache_r']+=cr; s['calls']+=1
                s['models'].add(m)
                if ts and (not s['first'] or ts<s['first']): s['first']=ts
                if ts and ts>s['last']: s['last']=ts
                if ts:
                    k=ts[:7]
                    monthly[k][0]+=i; monthly[k][1]+=cr; monthly[k][2]+=o

def p(*a): print(*a)

p('='*88)
p('按模型')
p(f"{'model':<32}{'calls':>7}{'in(M)':>9}{'cacheR(M)':>11}{'out(M)':>9}  {'span':<21}")
tin=tcr=tout=tc=0
for m,s in sorted(model_stat.items(), key=lambda kv:-kv[1]['in']):
    tin+=s['in']; tcr+=s['cache_r']; tout+=s['out']; tc+=s['calls']
    p(f"{m:<32}{s['calls']:>7}{s['in']/1e6:>9.1f}{s['cache_r']/1e6:>11.1f}{s['out']/1e6:>9.2f}  {s['first'][:10]}~{s['last'][:10]}")
p(f"{'TOTAL':<32}{tc:>7}{tin/1e6:>9.1f}{tcr/1e6:>11.1f}{tout/1e6:>9.2f}")

p()
p('按会话（top 15 by input）')
p(f"{'session':<10}{'calls':>7}{'in(M)':>9}{'cacheR(M)':>11}{'out(M)':>9}  {'models':<30}{'span':<21}")
for sid,s in sorted(sess_stat.items(), key=lambda kv:-kv[1]['in'])[:15]:
    ms=','.join(sorted(x.split('-')[0][:12] for x in s['models']))
    p(f"{sid:<10}{s['calls']:>7}{s['in']/1e6:>9.1f}{s['cache_r']/1e6:>11.1f}{s['out']/1e6:>9.2f}  {ms:<30}{s['first'][:10]}~{s['last'][:10]}")

p()
p('按月')
p(f"{'month':<9}{'in(M)':>9}{'cacheR(M)':>11}{'out(M)':>9}")
for k in sorted(monthly):
    i,c,o=monthly[k]
    p(f"{k:<9}{i/1e6:>9.1f}{c/1e6:>11.1f}{o/1e6:>9.2f}")

p()
p('说明: cacheR=缓存命中输入（多数供应商按约 1/10 价计）; in=未命中输入（全价）; out=输出（含思考）')
p('      model=auto 为模型选择器别名，实际路由记录在平台侧，量级已并入上述 input 统计')
