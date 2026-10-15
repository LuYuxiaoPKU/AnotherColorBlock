import io, glob, json, os, sys
from collections import defaultdict

"""扫 Claude Code 会话 transcript 汇总真实 token usage（总量口径）
用法: py usage-report.py [项目目录...]  缺省扫 AnotherColorBlock 两目录
说明: API 经多层转发，transcript 中模型名不可信，不做按模型拆分"""

DIRS = sys.argv[1:] or [
    r'C:/Users/Yuxiao Lu/.claude/projects/C--00-Data-RGM-AnotherColorBlock/',
    r'C:/Users/Yuxiao Lu/.claude/projects/C--00-Data-RGM-AnotherColorBlock-CC/',
]

tot = {'in':0,'out':0,'cache_r':0,'calls':0}
monthly = defaultdict(lambda: [0,0,0])
first=last=''

for d in DIRS:
    for jf in glob.glob(d+'*.jsonl'):
        with io.open(jf,'r',encoding='utf-8',errors='ignore') as fh:
            for line in fh:
                try: rec = json.loads(line)
                except: continue
                if rec.get('type') != 'assistant': continue
                u = (rec.get('message') or {}).get('usage') or {}
                i = u.get('input_tokens',0) or 0
                o = u.get('output_tokens',0) or 0
                cr = u.get('cache_read_input_tokens',0) or 0
                if i==0 and o==0 and cr==0: continue
                tot['in']+=i; tot['out']+=o; tot['cache_r']+=cr; tot['calls']+=1
                ts = rec.get('timestamp') or ''
                if ts:
                    if not first or ts<first: first=ts
                    if ts>last: last=ts
                    k=ts[:7]
                    monthly[k][0]+=i; monthly[k][1]+=cr; monthly[k][2]+=o

def p(*a): print(*a)
p(f'API calls: {tot["calls"]:,}')
p(f'input (uncached): {tot["in"]/1e6:.1f}M')
p(f'input (cache hit): {tot["cache_r"]/1e6:.1f}M')
p(f'output: {tot["out"]/1e6:.2f}M')
p(f'span: {first[:10]} ~ {last[:10]}')
p()
p('month   in(M)  cacheR(M)  out(M)')
for k in sorted(monthly):
    i,c,o=monthly[k]
    p(f'{k}  {i/1e6:>7.1f}  {c/1e6:>9.1f}  {o/1e6:>6.2f}')
