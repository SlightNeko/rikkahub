import json, re, urllib.request

def get_all(names, workspace_id):
    req = urllib.request.Request(
        "http://127.0.0.1:8787/mcp",
        data=json.dumps({
            "jsonrpc":"2.0","id":1,
            "method":"tools/call",
            "params":{"name":"mt_apk_read_text","arguments":{
                "workspaceId":workspace_id,"editSessionId":"",
                "locator":"axml:AndroidManifest.xml",
                "limit":800,"maxChars":65536,"startLine":0,"startColumn":0
            }}
        }).encode(),
        headers={"Content-Type":"application/json"}
    )
    resp = urllib.request.urlopen(req, timeout=30)
    r = json.loads(resp.read())
    for c in r['result']['content']:
        d = json.loads(c['text'])
        text = d['data']['textWindow']['text']
        for m in re.finditer(r'android:name="([^"]+)"', text):
            name = m.group(1)
            if name.startswith(('android.','com.google.','androidx.','com.android.','okhttp3')):
                continue
            n = name.rsplit('.',1)[-1] if '.' in name else name
            names.add((n, name))

nekohub_all = set()
juban_all = set()
get_all(nekohub_all, "ozip8r85")
get_all(juban_all, "1rob8i23")

nekohub_names = {n[0] for n in nekohub_all}
juban_names = {n[0] for n in juban_all}

# Key classes that differ
interesting = ["SafeMode","Shizuku","Pomodoro","Daily","MusicPlayer","DeviceEvent","Ucrop","QRScanner","WebServer"]
print("=== 关键类对比 ===")
for kw in interesting:
    n_has = [v for v in nekohub_all if kw.lower() in v[0].lower()]
    j_has = [v for v in juban_all if kw.lower() in v[0].lower()]
    print(f"\n{kw}:")
    print(f"  NekoHub: {[v[1] for v in n_has] if n_has else '(无)'}")
    print(f"  橘瓣: {[v[1] for v in j_has] if j_has else '(无)'}")
